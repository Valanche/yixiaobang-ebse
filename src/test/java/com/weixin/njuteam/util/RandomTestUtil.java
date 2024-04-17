package com.weixin.njuteam.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomTestUtil {

	private static final Random RANDOM = new Random();

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

	public static Date getRandomDate(String beginDate, String endDate) {

		// 时间格式化
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// 定义开始时间
		Date start = null;
		Date end = null;
		try {
			start = format.parse(beginDate);
			// 定义结束时间
			end = format.parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (start != null && end != null && start.getTime() >= end.getTime()) {
			return null;
		}

		if (start != null && end != null) {
			long date = random(start.getTime(), end.getTime());
			return new Date(date);
		}

		return null;
	}

	public static long getRandomId() {
		long num = RANDOM.nextLong();

		return Math.abs(num);
	}

	private static long random(long begin, long end) {
		long rtn = begin + (long) (Math.random() * (end - begin));

		// 如果返回的是开始时间和结束时间，通过递归调用本函数查找随机值
		if (rtn == begin || rtn == end) {
			return random(begin, end);
		}
		return rtn;
	}
}
