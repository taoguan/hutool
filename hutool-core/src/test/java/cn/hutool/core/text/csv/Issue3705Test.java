package cn.hutool.core.text.csv;

import cn.hutool.core.util.CharUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

public class Issue3705Test {
	@Test
	void writeTest() {
		final StringWriter stringWriter = new StringWriter();

		CsvWriteConfig csvWriteConfig = new CsvWriteConfig();
		try (CsvWriter csvWriter = new CsvWriter(stringWriter, csvWriteConfig)) {
			// 由于一行的第一个字段中有逗号，因此需要使用双引号包围，否则会被当成分隔符
			csvWriter.writeLine("2024-08-20 14:24:35,");
			csvWriter.writeLine("最后一行");
			csvWriter.flush();
		}

		String lineSeparator = new String(new char[]{CharUtil.CR, CharUtil.LF});

		Assertions.assertEquals(
			"\"2024-08-20 14:24:35,\"" + lineSeparator + "最后一行",
			stringWriter.toString());
	}
}
