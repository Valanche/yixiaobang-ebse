package com.weixin.njuteam.util;

import cn.hutool.core.util.IdUtil;

import java.util.Random;

/**
 * 用来生成随机的文件名
 *
 * @author Zyi
 */
public class RandomUtil {

	private static final Random RANDOM = new Random();

	private RandomUtil() {

	}

	public static String getRandomString(int length) {
		String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		// stringBuilder相对于stringBuffer效率比较高
		// 但是线程不安全 这里由于不是成员变量 所以不需要考虑线程问题
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < length; i++) {
			stringBuilder.append(str.charAt(RANDOM.nextInt(str.length())));
		}

		return stringBuilder.toString();
	}

	public static String getRandomUUID() {
		return IdUtil.fastSimpleUUID();
	}
}
