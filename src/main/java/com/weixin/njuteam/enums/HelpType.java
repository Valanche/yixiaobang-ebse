package com.weixin.njuteam.enums;

import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zyi
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JSONType(serializeEnumAsJavaBean = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum HelpType implements ValueEnum {

	/**
	 * 跑腿类型
	 */
	HELP("帮跑腿"),
	/**
	 * 租借类型
	 */
	RENT("出租"),
	/**
	 * 外借
	 */
	LEND("外借"),
	/**
	 * 出售
	 */
	SELL("出售");

	private static final Map<String, HelpType> CODE_MAP = new HashMap<>();

	static {
		for (HelpType status : HelpType.values()) {
			CODE_MAP.put(status.getValue(), status);
		}
	}

	@Setter
	private String value;

	public static HelpType getTypeByValue(String value) {
		return CODE_MAP.get(value);
	}

	public static HelpType get(int v) {
		String str = String.valueOf(v);
		return get(str);
	}

	public static HelpType get(String str) {
		for (HelpType s : values()) {
			if (s.getValue().equals(str)) {
				return s;
			}
		}
		return null;
	}

	@Override
	public String getValue() {
		return this.value;
	}
}
