package cn.hutool.core.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswdStrengthTest {
	@Test
	public void strengthTest(){
		String passwd = "2hAj5#mne-ix.86H";
		assertEquals(13, PasswdStrength.check(passwd));
	}

	@Test
	public void strengthNumberTest(){
		String passwd = "9999999999999";
		assertEquals(0, PasswdStrength.check(passwd));
	}

	@Test
	public void consecutiveLettersTest() {
		// 测试连续小写字母会被降级
		assertEquals(0, PasswdStrength.check("abcdefghijklmn"));
		// 测试连续大写字母会被降级
		assertEquals(0, PasswdStrength.check("ABCDEFGHIJKLMN"));
	}

	@Test
	public void dictionaryWeakPasswordTest() {
		// 测试包含简单密码字典中的弱密码
		assertEquals(0, PasswdStrength.check("password"));
		assertEquals(3, PasswdStrength.check("password2"));
	}

	@Test
	public void numericSequenceTest() {
		assertEquals(0, PasswdStrength.check("01234567890"));
		assertEquals(0, PasswdStrength.check("09876543210"));
	}
}
