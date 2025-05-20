package cn.hutool.core.thread;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * {@link RecyclableBatchThreadPoolExecutor} 测试类
 */
public class RecyclableBatchThreadPoolExecutorTest {

	@Test
	public void test() throws InterruptedException {
		int corePoolSize = 10;// 线程池大小
		int batchSize = 100;// 每批次数据量
		int clientCount = 30;// 调用者数量
		test(corePoolSize,batchSize,clientCount);
	}

	public void test(int corePoolSize,int batchSize,int clientCount ) throws InterruptedException{
		RecyclableBatchThreadPoolExecutor processor = new RecyclableBatchThreadPoolExecutor(corePoolSize);
		// 模拟多个调用者线程提交任务
		ExecutorService testExecutor = Executors.newFixedThreadPool(clientCount);
		Map<Integer, List<Integer>> map = new HashMap<>();
		for(int i = 0; i < clientCount; i++){
			map.put(i,testDate(1000));
		}
		long s = System.nanoTime();
		List<Future<?>> futures = new ArrayList<>();
		for (int j = 0; j < clientCount; j++) {
			final int clientId = j;
			Future<?> submit = testExecutor.submit(() -> {
				Function<Integer, String> function = p -> {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					return Thread.currentThread().getName() + "#" + p;
				};
				long start = System.nanoTime();
				List<String> process = processor.process(map.get(clientId), batchSize, function);
				long duration = System.nanoTime() - start;
				System.out.printf("【clientId：%s】处理结果：%s\n处理耗时：%.2f秒%n", clientId, process, duration / 1e9);
			});
			futures.add(submit);
		}
		futures.forEach(p-> {
			try {
				p.get();
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		});
		long d = System.nanoTime() - s;
		System.out.printf("总耗时：%.2f秒%n",d/1e9);
		testExecutor.shutdown();
		processor.shutdown();
	}
	public static List<Integer> testDate(int count){
		List<Integer> list = new ArrayList<>();
		for(int i = 1;i<=count;i++){
			list.add(i);
		}
		return list;
	}

}
