package com.weixin.njuteam.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * @author Zyi
 * @deprecated 该类只用于加密 已经不需要使用了
 */
@Deprecated
public class JasyptUtil {

	private static final PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
	private static final SimpleStringPBEConfig config = new SimpleStringPBEConfig();

	private static String password = "a74e2f91-c034-4f2c-af5a-8f845b54a539";

	public static void setPassword(String password) {
		JasyptUtil.password = password;
	}

	public static void main(String[] args) {
		addConfig();
		// 加密
		String encPwd1 = encryptPwd("root");
		// 加密
		String encPwd2 = encryptPwd("#CITI123456");
		// 解密
		String decPwd1 = decryptPwd(encPwd1);
		// 解密
		String decPwd2 = decryptPwd(encPwd2);
		System.out.println(encPwd1);
		System.out.println(encPwd2);
		System.out.println("username：" + decPwd1);
		System.out.println("password：" + decPwd2);
	}

	private static void addConfig() {
		config.setAlgorithm("PBEWithMD5AndDES");
		config.setPassword(password);
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setStringOutputType("base64");
		encryptor.setConfig(config);
	}

	/**
	 * 解密
	 *
	 * @param value 需要加密的密码
	 */
	public static String decryptPwd(String value) {
		return encryptor.decrypt(value);
	}

	/**
	 * 加密方法
	 *
	 * @param value 需要加密的密码
	 */
	public static String encryptPwd(String value) {
		return encryptor.encrypt(value);
	}
}
