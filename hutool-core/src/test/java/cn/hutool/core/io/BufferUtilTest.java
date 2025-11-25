package cn.hutool.core.io;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;

/**
 * BufferUtil单元测试
 *
 * @author looly
 *
 */
public class BufferUtilTest {

	@Test
	public void copyTest() {
		byte[] bytes = "AAABBB".getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		ByteBuffer buffer2 = BufferUtil.copy(buffer, ByteBuffer.allocate(5));
		assertEquals("AAABB", StrUtil.utf8Str(buffer2));
	}

	@Test
	public void readBytesTest() {
		byte[] bytes = "AAABBB".getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		byte[] bs = BufferUtil.readBytes(buffer, 5);
		assertEquals("AAABB", StrUtil.utf8Str(bs));
	}

	@Test
	public void readBytes2Test() {
		byte[] bytes = "AAABBB".getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		byte[] bs = BufferUtil.readBytes(buffer, 5);
		assertEquals("AAABB", StrUtil.utf8Str(bs));
	}

	@Test
	public void readLineTest() {
		String text = "aa\r\nbbb\ncc";
		ByteBuffer buffer = ByteBuffer.wrap(text.getBytes());

		// 第一行
		String line = BufferUtil.readLine(buffer, CharsetUtil.CHARSET_UTF_8);
		assertEquals("aa", line);

		// 第二行
		line = BufferUtil.readLine(buffer, CharsetUtil.CHARSET_UTF_8);
		assertEquals("bbb", line);

		// 第三行因为没有行结束标志，因此返回null
		line = BufferUtil.readLine(buffer, CharsetUtil.CHARSET_UTF_8);
		assertNull(line);

		// 读取剩余部分
		assertEquals("cc", StrUtil.utf8Str(BufferUtil.readBytes(buffer)));
	}

	@Test
	public void testByteBufferSideEffect() {
		String originalText = "Hello";
		ByteBuffer buffer = ByteBuffer.wrap(originalText.getBytes(StandardCharsets.UTF_8));
		// 此时 buffer.remaining() == 5
		assertEquals(5, buffer.remaining());

		// 调用工具类转换，打印buffer内容
		String result = StrUtil.str(buffer, StandardCharsets.UTF_8);
		assertEquals(originalText, result);

		// 预期：
		// 工具类不应该修改原 buffer 的指针，remaining 应该依然为 5
		// 再次调用工具类转换，输出结果应该不变
		assertEquals(originalText, StrUtil.str(buffer, StandardCharsets.UTF_8));
	}
}
