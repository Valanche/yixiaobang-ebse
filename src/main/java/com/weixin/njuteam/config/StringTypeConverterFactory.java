package com.weixin.njuteam.config;

import com.weixin.njuteam.enums.ValueEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

/**
 * @author Zyi
 */
@Component
@Slf4j
public class StringTypeConverterFactory implements ConverterFactory<String, ValueEnum> {

	@Override
	public <T extends ValueEnum> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringTypeConverter<>(targetType);
	}
}
