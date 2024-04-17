package com.weixin.njuteam.config;

import cn.hutool.core.util.ObjectUtil;
import com.weixin.njuteam.enums.ValueEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

/**
 * 前端传值时转换成对应类型的enum
 *
 * @author Zyi
 */
@Slf4j
public class StringTypeConverter<T extends ValueEnum> implements Converter<String, T> {

	Class<T> targetType;

	public StringTypeConverter(Class<T> targetType) {
		this.targetType = targetType;
	}

	public static <T extends ValueEnum> Object getEnum(Class<T> targetType, String source) {
		for (T e : targetType.getEnumConstants()) {
			if (source.equals(e.getValue())) {
				return e;
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T convert(String source) {
		T t = (T) getEnum(this.targetType, source);

		if (ObjectUtil.isNull(t)) {
			throw new IllegalArgumentException("无法匹配对应的枚举类型");
		}

		return t;
	}
}
