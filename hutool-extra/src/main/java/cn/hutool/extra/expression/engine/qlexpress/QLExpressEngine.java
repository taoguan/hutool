package cn.hutool.extra.expression.engine.qlexpress;

import cn.hutool.extra.expression.ExpressionEngine;
import cn.hutool.extra.expression.ExpressionException;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.config.QLExpressRunStrategy;

import javax.naming.InitialContext;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * QLExpress引擎封装<br>
 * 见：https://github.com/alibaba/QLExpress
 *
 * @author looly
 * @since 5.8.9
 */
public class QLExpressEngine implements ExpressionEngine {

	private final ExpressRunner engine;

	/**
	 * 构造
	 */
	public QLExpressEngine() {
		engine = new ExpressRunner();

		// issue#3994@Github
		// Enforce blacklisting of high-risk method invocations
		QLExpressRunStrategy.setForbidInvokeSecurityRiskMethods(true);
		// Explicitly forbid JNDI lookup calls through InitialContext
		QLExpressRunStrategy.addSecurityRiskMethod(InitialContext.class, "doLookup");
	}

	@Override
	public Object eval(final String expression, final Map<String, Object> context, Collection<Class<?>> allowClassSet) {
		// issue#3994@Github
		if (null != allowClassSet) {
			for (Class<?> clazz : allowClassSet) {
				for (Method method : clazz.getDeclaredMethods()) {
					QLExpressRunStrategy.addSecureMethod(clazz, method.getName());
				}
			}
		}
		final DefaultContext<String, Object> defaultContext = new DefaultContext<>();
		defaultContext.putAll(context);
		try {
			return engine.execute(expression, defaultContext, null, true, false);
		} catch (final Exception e) {
			throw new ExpressionException(e);
		}
	}
}
