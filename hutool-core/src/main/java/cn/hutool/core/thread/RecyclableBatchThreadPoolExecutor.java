package cn.hutool.core.thread;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 可召回批处理线程池执行器
 * 1.数据分批并行处理
 * 2.线程安全，可用同时执行多个任务
 * 3.主线程、线程池混合执行，主线程空闲时会尝试召回线程池队列中的任务执行，无需担心任务阻塞
 *
 * @author likuan
 */
public class RecyclableBatchThreadPoolExecutor {
	private final ExecutorService executor;

	public RecyclableBatchThreadPoolExecutor(int poolSize){
		this(poolSize,"recyclable-batch-pool-");
	}

	/**
	 * 建议的构造方法
	 * 使用无界队列，主线程会召回队列中的任务执行，不会有任务堆积，无需考虑拒绝策略
	 * 假如在web场景中请求量过大导致oom，不使用此工具也会有同样的结果，甚至更严重，应该对请求做限制或做其他优化
	 *
	 * @param poolSize 线程池大小
	 * @param threadPoolPrefix 线程名前缀
	 */
	public RecyclableBatchThreadPoolExecutor(int poolSize, String threadPoolPrefix){
		AtomicInteger threadNumber = new AtomicInteger(1);
		ThreadFactory threadFactory = r -> {
			Thread t = new Thread(r, threadPoolPrefix + threadNumber.getAndIncrement());
			if (t.isDaemon()) {
				t.setDaemon(false);
			}
			if (t.getPriority() != Thread.NORM_PRIORITY) {
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		};
		this.executor = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),threadFactory);
	}

	/**
	 * 自定义线程池，一般不需要使用
	 * @param executor 线程池
	 */
	public RecyclableBatchThreadPoolExecutor(ExecutorService executor){
		this.executor = executor;
	}

	/**
	 * 关闭线程池
	 */
	public void shutdown(){
		executor.shutdown();
	}

	/**
	 * 分批次处理数据
	 * 1.所有批次执行完成后会过滤null并返回合并结果，保持输入数据顺序，不需要结果{@link Function}返回null即可
	 * 2.异常在{@link Function}中自行处理
	 * 3.主线程会参与处理批次数据，如果要异步执行任务请使用普通线程池
	 *
	 * @param <T> 输入数据类型
	 * @param <R> 输出数据类型
	 * @param data 待处理数据集合
	 * @param batchSize 每批次数据量
	 * @param processor 单条数据处理函数
	 * @return 处理结果集合
	 */
	@SuppressWarnings("unchecked")
	public <T,R> List<R> process(List<T> data, int batchSize, Function<T,R> processor) {
		if (batchSize < 1) {
			throw new IllegalArgumentException("batchSize >= 1");
		}
		List<List<T>> batches = splitData(data, batchSize);
		int batchCount = batches.size();
		ConcurrentLinkedQueue<IdempotentTask<R>> taskQueue = new ConcurrentLinkedQueue<>();
		Map<Integer,Future<ResultWarp<R>>> futuresMap = new HashMap<>();
		// 提交前 batchCount-1 批任务
		for (int i = 0 ; i < batchCount-1 ; i++) {
			final int index = i;
			IdempotentTask<R> task = new IdempotentTask<>(i,() -> processBatch(batches.get(index), processor));
			taskQueue.add(task);
			futuresMap.put(i,executor.submit(task));
		}
		Object[] arr = new Object[batchCount];
		// 处理最后一批
		arr[batchCount-1] = processBatch(batches.get(batchCount-1), processor);
		// 处理剩余任务
		processRemainingTasks(taskQueue, futuresMap,arr);
		//排序、过滤null
		return Stream.of(arr)
				.filter(Objects::nonNull)
				.map(p -> (List<R>) p)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	/**
	 * 处理剩余任务并收集结果
	 * @param taskQueue 任务队列
	 * @param futuresMap 异步任务映射
	 * @param arr 结果存储数组
	 */
	private <R> void processRemainingTasks(Queue<IdempotentTask<R>> taskQueue, Map<Integer,Future<ResultWarp<R>>> futuresMap,Object[] arr) {
		// 主消费未执行任务
		IdempotentTask<R> task;
		while ((task = taskQueue.poll()) != null) {
			try {
				ResultWarp<R> call = task.call();
				if (call.effective) {
					// 取消被主线程执行任务
					Future<ResultWarp<R>> future = futuresMap.remove(task.index);
					future.cancel(false);
					//加入结果集
					arr[task.index] = call.result;
				}
			} catch (Exception e) {
				// 不处理异常
				throw new RuntimeException(e);
			}
		}
		futuresMap.forEach((index,future)->{
			try {
				ResultWarp<R> resultWarp = future.get();
				if(resultWarp.effective){
					arr[index] = resultWarp.result;
				}
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		});
	}

	/**
	 * 幂等任务包装类，确保任务只执行一次
	 */
	private static class IdempotentTask<R> implements Callable<ResultWarp<R>> {

		private final int index;
		private final Callable<List<R>> delegate;
		private final AtomicBoolean executed = new AtomicBoolean(false);

		IdempotentTask(int index,Callable<List<R>> delegate) {
			this.index = index;
			this.delegate = delegate;
		}

		@Override
		public ResultWarp<R> call() throws Exception {
			if (executed.compareAndSet(false, true)) {
				return new ResultWarp<>(delegate.call(), true);
			}
			return new ResultWarp<>(null, false);
		}
	}

	/**
	 * 结果包装类，标记结果有效性
	 */
	private static class ResultWarp<R>{
		private final List<R> result;
		private final boolean effective;
		ResultWarp(List<R> result, boolean effective){
			this.result = result;
			this.effective = effective;
		}
	}

	/**
	 * 数据分片方法
	 * @param data 原始数据
	 * @param batchSize 每批次数据量
	 * @return 分片后的二维集合
	 */
	public static <T> List<List<T>> splitData(List<T> data, int batchSize) {
		int batchCount = (data.size() + batchSize - 1) / batchSize;
		return new AbstractList<List<T>>() {
			@Override
			public List<T> get(int index) {
				int from = index * batchSize;
				int to = Math.min((index + 1) * batchSize, data.size());
				return data.subList(from, to);
			}

			@Override
			public int size() {
				return batchCount;
			}
		};
	}

	/**
	 * 单批次数据处理
	 * @param batch 单批次数据
	 * @param processor 处理函数
	 * @return 处理结果
	 */
	public static <T,R> List<R> processBatch(List<T> batch, Function<T,R> processor) {
		return batch.stream().map(processor).filter(Objects::nonNull).collect(Collectors.toList());
	}

}
