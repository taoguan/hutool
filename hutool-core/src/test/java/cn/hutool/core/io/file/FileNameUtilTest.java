package cn.hutool.core.io.file;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class FileNameUtilTest {

	@Test
	public void cleanInvalidTest(){
		String name = FileNameUtil.cleanInvalid("1\n2\n");
		assertEquals("12", name);

		name = FileNameUtil.cleanInvalid("\r1\r\n2\n");
		assertEquals("12", name);
	}

	@Test
	public void mainNameTest() {
		final String s = FileNameUtil.mainName("abc.tar.gz");
		assertEquals("abc", s);
	}

	@Test
	public void extNameAndMainNameBugTest() {
		// 正确，输出前缀为 "app-v2.3.1-star"
		assertEquals("app-v2.3.1-star",FileNameUtil.mainName("app-v2.3.1-star.gz"));
		// 当前代码会失败，预期后缀结果 "gz"，但是输出 "star.gz"
		assertEquals("gz", FileNameUtil.extName("app-v2.3.1-star.gz"));
	}
}
