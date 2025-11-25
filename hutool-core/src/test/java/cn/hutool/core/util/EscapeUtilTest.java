package cn.hutool.core.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EscapeUtilTest {

	@Test
	public void escapeHtml4Test() {
		String escapeHtml4 = EscapeUtil.escapeHtml4("<a>你好</a>");
		assertEquals("&lt;a&gt;你好&lt;/a&gt;", escapeHtml4);

		String result = EscapeUtil.unescapeHtml4("&#25391;&#33633;&#22120;&#31867;&#22411;");
		assertEquals("振荡器类型", result);

		String escape = EscapeUtil.escapeHtml4("*@-_+./(123你好)");
		assertEquals("*@-_+./(123你好)", escape);
	}

	@Test
	public void escapeTest(){
		String str = "*@-_+./(123你好)ABCabc";
		String escape = EscapeUtil.escape(str);
		assertEquals("*@-_+./%28123%u4f60%u597d%29ABCabc", escape);

		String unescape = EscapeUtil.unescape(escape);
		assertEquals(str, unescape);
	}

	@Test
	public void escapeAllTest(){
		String str = "*@-_+./(123你好)ABCabc";

		String escape = EscapeUtil.escapeAll(str);
		assertEquals("%2a%40%2d%5f%2b%2e%2f%28%31%32%33%u4f60%u597d%29%41%42%43%61%62%63", escape);

		String unescape = EscapeUtil.unescape(escape);
		assertEquals(str, unescape);
	}

	/**
	 * https://gitee.com/chinabugotech/hutool/issues/I49JU8
	 */
	@Test
	public void escapeAllTest2(){
		String str = "٩";

		String escape = EscapeUtil.escapeAll(str);
		assertEquals("%u0669", escape);

		String unescape = EscapeUtil.unescape(escape);
		assertEquals(str, unescape);
	}

	@Test
	public void escapeSingleQuotesTest(){
		// 单引号不做转义
		String str = "'some text with single quotes'";
		final String s = EscapeUtil.escapeHtml4(str);
		assertEquals("'some text with single quotes'", s);
	}

	@Test
	public void unescapeSingleQuotesTest(){
		String str = "&apos;some text with single quotes&apos;";
		final String s = EscapeUtil.unescapeHtml4(str);
		assertEquals("'some text with single quotes'", s);
	}

	@Test
	public void escapeXmlTest(){
		final String a = "<>";
		final String escape = EscapeUtil.escapeXml(a);
		assertEquals("&lt;&gt;", escape);
		assertEquals("中文“双引号”", EscapeUtil.escapeXml("中文“双引号”"));
	}

	@Test
	void testUnescapeNull() {
		assertNull(EscapeUtil.unescape(null));
	}

	@Test
	void testUnescapeEmpty() {
		assertEquals("", EscapeUtil.unescape(""));
	}

	@Test
	void testUnescapeBlank() {
		assertEquals("   ", EscapeUtil.unescape("   "));
	}

	@Test
	void testUnescapeAsciiCharacters() {
		// 测试ASCII字符转义
		assertEquals("hello", EscapeUtil.unescape("hello"));
		assertEquals("test space", EscapeUtil.unescape("test%20space"));
		assertEquals("A", EscapeUtil.unescape("%41"));
		assertEquals("a", EscapeUtil.unescape("%61"));
		assertEquals("0", EscapeUtil.unescape("%30"));
		assertEquals("!", EscapeUtil.unescape("%21"));
		assertEquals("@", EscapeUtil.unescape("%40"));
		assertEquals("#", EscapeUtil.unescape("%23"));
	}

	@Test
	void testUnescapeUnicodeCharacters() {
		// 测试Unicode字符转义
		assertEquals("中", EscapeUtil.unescape("%u4E2D"));
		assertEquals("文", EscapeUtil.unescape("%u6587"));
		assertEquals("测", EscapeUtil.unescape("%u6D4B"));
		assertEquals("试", EscapeUtil.unescape("%u8BD5"));
		assertEquals("😊", EscapeUtil.unescape("%uD83D%uDE0A")); // 笑脸表情
	}

	@Test
	void testUnescapeMixedContent() {
		// 测试混合内容
		assertEquals("Hello 世界!", EscapeUtil.unescape("Hello%20%u4E16%u754C%21"));
		assertEquals("测试: 100%", EscapeUtil.unescape("%u6D4B%u8BD5%3A%20100%25"));
		assertEquals("a+b=c", EscapeUtil.unescape("a%2Bb%3Dc"));
	}

	@Test
	void testUnescapeIncompleteEscapeSequences() {
		// 测试不完整的转义序列
		assertEquals("test%", EscapeUtil.unescape("test%"));
		assertEquals("test%u", EscapeUtil.unescape("test%u"));
		assertEquals("test%u1", EscapeUtil.unescape("test%u1"));
		assertEquals("test%u12", EscapeUtil.unescape("test%u12"));
		assertEquals("test%u123", EscapeUtil.unescape("test%u123"));
		assertEquals("test%1", EscapeUtil.unescape("test%1"));
		assertEquals("test%2", EscapeUtil.unescape("test%2"));
	}

	@Test
	void testUnescapeEdgeCases() {
		// 测试边界情况
		assertEquals("%", EscapeUtil.unescape("%"));
		assertEquals("%u", EscapeUtil.unescape("%u"));
		assertEquals("%%", EscapeUtil.unescape("%%"));
		assertEquals("%u%", EscapeUtil.unescape("%u%"));
		assertEquals("100% complete", EscapeUtil.unescape("100%25%20complete"));
	}

	@Test
	void testUnescapeMultipleEscapeSequences() {
		// 测试多个连续的转义序列
		assertEquals("ABC", EscapeUtil.unescape("%41%42%43"));
		assertEquals("中文测试", EscapeUtil.unescape("%u4E2D%u6587%u6D4B%u8BD5"));
		assertEquals("A 中 B", EscapeUtil.unescape("%41%20%u4E2D%20%42"));
	}

	@Test
	void testUnescapeSpecialCharacters() {
		// 测试特殊字符
		assertEquals("\n", EscapeUtil.unescape("%0A"));
		assertEquals("\r", EscapeUtil.unescape("%0D"));
		assertEquals("\t", EscapeUtil.unescape("%09"));
		assertEquals(" ", EscapeUtil.unescape("%20"));
		assertEquals("<", EscapeUtil.unescape("%3C"));
		assertEquals(">", EscapeUtil.unescape("%3E"));
		assertEquals("&", EscapeUtil.unescape("%26"));
	}

	@Test
	void testUnescapeComplexScenario() {
		// 测试复杂场景
		final String original = "Hello 世界! 这是测试。Email: test@example.com";
		final String escaped = "Hello%20%u4E16%u754C%21%20%u8FD9%u662F%u6D4B%u8BD5%u3002Email%3A%20test%40example.com";
		assertEquals(original, EscapeUtil.unescape(escaped));
	}

	@Test
	void testUnescapeWithIncompleteAtEnd() {
		// 测试末尾有不完整转义序列
		assertEquals("normal%", EscapeUtil.unescape("normal%"));
		assertEquals("normal%u", EscapeUtil.unescape("normal%u"));
		assertEquals("normal%u1", EscapeUtil.unescape("normal%u1"));
		assertEquals("normal%1", EscapeUtil.unescape("normal%1"));
	}

	@Test
	void testUnescapeUppercaseHex() {
		// 测试大写十六进制
		assertEquals("A", EscapeUtil.unescape("%41"));
		assertEquals("A", EscapeUtil.unescape("%41"));
		assertEquals("中", EscapeUtil.unescape("%u4E2D"));
		assertEquals("中", EscapeUtil.unescape("%u4E2D"));
	}
}
