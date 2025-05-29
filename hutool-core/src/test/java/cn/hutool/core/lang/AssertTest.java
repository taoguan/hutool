package cn.hutool.core.lang;

import cn.hutool.core.util.StrUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class AssertTest {

	@Test
	public void isNullTest() {
		String a = null;
		cn.hutool.core.lang.Assert.isNull(a);
	}

	@Test
	public void notNullTest() {
		String a = null;
		cn.hutool.core.lang.Assert.isNull(a);
	}

	@Test
	public void isTrueTest() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			int i = 0;
			//noinspection ConstantConditions
			cn.hutool.core.lang.Assert.isTrue(i > 0, IllegalArgumentException::new);
		});
	}

	@Test
	public void isTrueTest2() {
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
			int i = -1;
			//noinspection ConstantConditions
			cn.hutool.core.lang.Assert.isTrue(i >= 0, IndexOutOfBoundsException::new);
		});
	}

	@Test
	public void isTrueTest3() {
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
			int i = -1;
			//noinspection ConstantConditions
			Assert.isTrue(i > 0, () -> new IndexOutOfBoundsException("relation message to return"));
		});
	}

	@Test
	public void equalsTest() {
		//String a="ab";
		//final String b = new String("abc");
		String a = null;
		final String b = null;
		Assert.equals(a, b);
		Assert.equals(a, b, "{}不等于{}", a, b);
		Assert.equals(a, b, () -> new RuntimeException(StrUtil.format("{}和{}不相等", a, b)));
	}

	@Test
	public void notEqualsTest() {
		//String c="19";
		//final String d = new String("19");
		String c = null;
		final String d = "null";
		//Assert.notEquals(c,d);
		//Assert.notEquals(c,d,"{}等于{}",c,d);
		Assert.notEquals(c, d, () -> new RuntimeException(StrUtil.format("{}和{}相等", c, d)));

	}

	@Test
	public void emptyCollectionTest() {
		List<Object> testList = new ArrayList<>();
		Assertions.assertDoesNotThrow(() -> Assert.empty(null));
		Assertions.assertDoesNotThrow(() -> Assert.empty(testList));
		testList.add(new Object());
		Assertions.assertThrows(IllegalArgumentException.class, () -> Assert.empty(testList));
	}

}
