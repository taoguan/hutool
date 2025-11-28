package cn.hutool.core.math;

import cn.hutool.core.lang.Console;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 排列单元测试
 * @author looly
 */
public class ArrangementTest {

	// ----------------------------------------------------
	// 基础测试
	// ----------------------------------------------------
	@Test
	public void arrangementTest() {
		long result = Arrangement.count(4, 2);
		assertEquals(12, result);

		result = Arrangement.count(4, 1);
		assertEquals(4, result);

		result = Arrangement.count(4, 0);
		assertEquals(1, result);

		long resultAll = Arrangement.countAll(4);
		assertEquals(64, resultAll);
	}

	// ----------------------------------------------------
	// select 基础测试
	// ----------------------------------------------------
	@Test
	public void selectTest() {
		Arrangement arrangement = new Arrangement(new String[]{"1", "2", "3", "4"});
		List<String[]> list = arrangement.select(2);

		// 校验数量一致
		assertEquals(Arrangement.count(4, 2), list.size());

		// 逐项严格校验顺序是否一致（按 DFS 顺序）
		assertArrayEquals(new String[]{"1", "2"}, list.get(0));
		assertArrayEquals(new String[]{"1", "3"}, list.get(1));
		assertArrayEquals(new String[]{"1", "4"}, list.get(2));
		assertArrayEquals(new String[]{"2", "1"}, list.get(3));
		assertArrayEquals(new String[]{"2", "3"}, list.get(4));
		assertArrayEquals(new String[]{"2", "4"}, list.get(5));
		assertArrayEquals(new String[]{"3", "1"}, list.get(6));
		assertArrayEquals(new String[]{"3", "2"}, list.get(7));
		assertArrayEquals(new String[]{"3", "4"}, list.get(8));
		assertArrayEquals(new String[]{"4", "1"}, list.get(9));
		assertArrayEquals(new String[]{"4", "2"}, list.get(10));
		assertArrayEquals(new String[]{"4", "3"}, list.get(11));

		// 测试 selectAll
		List<String[]> selectAll = arrangement.selectAll();
		assertEquals(Arrangement.countAll(4), selectAll.size());

		// m=0，应该返回一个空排列
		List<String[]> list2 = arrangement.select(0);
		assertEquals(1, list2.size());
		assertEquals(0, list2.get(0).length);
	}

	// ----------------------------------------------------
	// 扩展测试：边界、错误处理
	// ----------------------------------------------------
	@Test
	public void boundaryTest() {
		Arrangement arr = new Arrangement(new String[]{"A", "B", "C"});

		// m = n
		List<String[]> full = arr.select(3);
		assertEquals(6, full.size());

		// m = 1
		List<String[]> one = arr.select(1);
		assertEquals(3, one.size());
		assertArrayEquals(new String[]{"A"}, one.get(0));

		// m > n → empty list
		assertTrue(arr.select(10).isEmpty());

		// m < 0 → empty list
		assertTrue(arr.select(-1).isEmpty());
	}

	// ----------------------------------------------------
	// 扩展测试：空数组
	// ----------------------------------------------------
	@Test
	public void emptyTest() {
		Arrangement arrangement = new Arrangement(new String[]{});

		assertEquals(1, arrangement.select(0).size());
		assertTrue(arrangement.select(1).isEmpty());
		assertTrue(arrangement.selectAll().isEmpty()); // A(0,m) = 0 for m>0，A(0,0)=1 → 全排列 = 1 个空排列
	}

	// ----------------------------------------------------
	// 扩展测试：重复元素（用于验证去重算法）
	// 默认 Arrangement 不去重，因此应该包含重复排列
	// ----------------------------------------------------
	@Test
	@Disabled("默认 Arrangement 不支持去重；启用后手动检查")
	public void duplicateElementTest() {
		Arrangement arrangement = new Arrangement(new String[]{"1", "1", "3"});

		List<String[]> list = arrangement.select(2);

		// 应该有 A(3,2) = 6 个
		assertEquals(6, list.size());

		for (String[] s : list) {
			Console.log(s);
		}
	}

	// ----------------------------------------------------
	// 扩展测试：selectAll 覆盖全部不重复排列（A(n,1..n)）
	// ----------------------------------------------------
	@Test
	public void selectAllTest() {
		Arrangement arrangement = new Arrangement(new String[]{"1", "2", "3"});

		List<String[]> all = arrangement.selectAll();

		// 打印用于观测
		for (String[] s : all) {
			Console.log(s);
		}

		// A(3,1) + A(3,2) + A(3,3) = 3 + 6 + 6 = 15
		assertEquals(Arrangement.countAll(3), all.size());
		assertEquals(15, all.size());

		// spot check 不重复排列
		assertArrayEquals(new String[]{"1"}, all.get(0));
		assertArrayEquals(new String[]{"1", "2"}, all.get(3));
		assertArrayEquals(new String[]{"1", "2", "3"}, all.get(9));
	}

	// ----------------------------------------------------
	// 迭代器测试
	// ----------------------------------------------------
	@Test
	public void iteratorTest() {
		Arrangement arrangement = new Arrangement(new String[]{"1", "2", "3"});

		// 测试 m=2 的情况
		List<String[]> iterResult = new ArrayList<>();
		for (String[] perm : arrangement.iterate(2)) {
			iterResult.add(perm);
		}

		assertEquals(6, iterResult.size());
		assertArrayEquals(new String[]{"1", "2"}, iterResult.get(0));
		assertArrayEquals(new String[]{"1", "3"}, iterResult.get(1));
		assertArrayEquals(new String[]{"2", "1"}, iterResult.get(2));
		assertArrayEquals(new String[]{"2", "3"}, iterResult.get(3));
		assertArrayEquals(new String[]{"3", "1"}, iterResult.get(4));
		assertArrayEquals(new String[]{"3", "2"}, iterResult.get(5));
	}

	@Test
	public void iteratorFullTest() {
		Arrangement arrangement = new Arrangement(new String[]{"1", "2", "3"});

		// 测试全排列的情况
		List<String[]> iterResult = new ArrayList<>();
		for (String[] perm : arrangement.iterate(3)) {
			iterResult.add(perm);
		}

		assertEquals(6, iterResult.size());
	}

	@Test
	public void iteratorBoundaryTest() {
		Arrangement arrangement = new Arrangement(new String[]{"1", "2", "3"});

		// 测试 m > n 的情况
		List<String[]> iterResult = new ArrayList<>();
		for (String[] perm : arrangement.iterate(5)) {
			iterResult.add(perm);
		}
		assertTrue(iterResult.isEmpty());
	}

}
