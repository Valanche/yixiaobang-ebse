package com.weixin.njuteam.config;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.weixin.njuteam.enums.ValueEnum;

import java.lang.reflect.Type;

/**
 * @author Zyi
 */
public class FinishStatusDeserializer implements ObjectDeserializer {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser defaultJSONParser, Type type, Object o) {
		final JSONLexer lexer = defaultJSONParser.lexer;
		Class<T> clazz = (Class<T>) type;
		Object[] enumConstants = clazz.getEnumConstants();

		if (ValueEnum.class.isAssignableFrom(clazz)) {
			for (Object enumConstant : enumConstants) {
				ValueEnum valueEnum = (ValueEnum) enumConstant;
				String value = valueEnum.getValue();

				if (lexer.stringVal().equals(value)) {
					return (T) valueEnum;
				}
			}
		}

		return null;
	}

	@Override
	public int getFastMatchToken() {
		return 0;
	}
}
