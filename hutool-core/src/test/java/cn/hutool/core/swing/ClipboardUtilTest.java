package cn.hutool.core.swing;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 剪贴板工具类单元测试
 *
 * @author looly
 *
 */
public class ClipboardUtilTest {

	@Test
	@Disabled
	public void setAndGetStrTest() {
		try {
			ClipboardUtil.setStr("test");

			String test = ClipboardUtil.getStr();
			assertEquals("test", test);
		} catch (java.awt.HeadlessException e) {
			// 忽略 No X11 DISPLAY variable was set, but this program performed an operation which requires it.
			// ignore
		}
	}
}
