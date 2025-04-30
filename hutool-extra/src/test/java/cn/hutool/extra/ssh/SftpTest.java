package cn.hutool.extra.ssh;

import cn.hutool.core.util.CharsetUtil;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.io.File;
import java.util.List;

/**
 * 基于sshj 框架SFTP 封装测试.
 *
 * @author youyongkun
 * @since 5.7.18
 */
public class SftpTest {

	private SshjSftp sshjSftp;

	@Before
	@Disabled
	public void init() {
		sshjSftp = new SshjSftp("127.0.0.1", 8022, "test", "test", CharsetUtil.CHARSET_UTF_8);
	}

	@Test
	@Disabled
	public void lsTest() {
		List<String> files = sshjSftp.ls("/");
		if (files != null && !files.isEmpty()) {
			files.forEach(System.out::println);
		}
	}

	@Test
	@Disabled
	public void downloadTest() {
		sshjSftp.recursiveDownloadFolder("/temp/20250427/", new File("D:\\temp\\20250430\\20250427\\"));
	}

	@Test
	@Disabled
	public void uploadTest() {
		sshjSftp.upload("/ftp-2/20250430/", new File("D:\\temp\\20250430\\test.txt"));
	}

	@Test
	@Disabled
	public void mkDirTest() {
		boolean flag = sshjSftp.mkdir("/ftp-2/20250430-1");
		System.out.println("是否创建成功: " + flag);
	}

	@Test
	@Disabled
	public void pwdTest() {
		String pwd = sshjSftp.pwd();
		System.out.println("PWD: " + pwd);
	}

	@Test
	@Disabled
	public void mkDirsTest() {
		// 在当前目录下批量创建目录
		sshjSftp.mkDirs("/ftp-2/20250430-2/t1/t2/");
	}

	@Test
	@Disabled
	public void delDirTest() {
		sshjSftp.delDir("/ftp-2/20250430-2/t1/t2");
	}

	@Test
	@Disabled
	public void cdTest() {
		System.out.println(sshjSftp.cd("/ftp-2"));
		System.out.println(sshjSftp.cd("/ftp-4"));
	}
}
