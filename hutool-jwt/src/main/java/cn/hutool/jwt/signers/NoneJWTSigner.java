package cn.hutool.jwt.signers;

import cn.hutool.core.util.StrUtil;

/**
 * 无需签名的JWT签名器
 *
 * @author looly
 * @since 5.7.0
 */
public class NoneJWTSigner implements JWTSigner {

	/**
	 * 定义一个常量ID_NONE，表示没有ID的情况
	 */
	public static final String ID_NONE = "none";

	/**
	 * 创建一个NoneJWTSigner实例，用于处理没有签名的JWT
	 */
	public static NoneJWTSigner NONE = new NoneJWTSigner();

	/**
	 * 判断给定的算法是否为无签名的算法
	 *
	 * @param alg 算法
	 * @return 如果是无签名的算法，则返回true；否则返回false
	 * @since 5.8.42
	 */
	public static boolean isNone(final String alg) {
		return StrUtil.isBlank( alg) || StrUtil.equalsIgnoreCase(alg, ID_NONE);
	}

	@Override
	public String sign(String headerBase64, String payloadBase64) {
		return StrUtil.EMPTY;
	}

	@Override
	public boolean verify(String headerBase64, String payloadBase64, String signBase64) {
		return StrUtil.isEmpty(signBase64);
	}

	@Override
	public String getAlgorithm() {
		return ID_NONE;
	}
}
