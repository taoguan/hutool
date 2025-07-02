package cn.hutool.captcha;

import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.core.math.Calculator;
import org.junit.jupiter.api.Test;

public class GeneratorTest {

	@Test
	public void mathGeneratorTest() {
		final MathGenerator mathGenerator = new MathGenerator();
		for (int i = 0; i < 1000; i++) {
			mathGenerator.verify(mathGenerator.generate(), "0");
		}

		final MathGenerator mathGenerator1 = new MathGenerator(false);
		for (int i = 0; i < 1000; i++) {
			String generate = mathGenerator1.generate();
			if( Calculator.conversion(generate) < 0){
				throw new RuntimeException("No Pass");
			}
		}
	}
}
