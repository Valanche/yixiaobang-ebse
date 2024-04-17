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
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JSONType(serializeEnumAsJavaBean = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SeekHelpType implements ValueEnum {

	/**
	 * 跑腿类型
	 */
	SEEK_HELP("求帮跑腿"),
	/**
	 * 求租类型
	 */
	SEEK_RENT("求租"),
	/**
	 * 外借
	 */
	SEEK_LEND("求借"),
	/**
	 * 求购
	 */
	SEEK_BUY("求购");

	private static final Map<String, SeekHelpType> CODE_MAP = new HashMap<>();

	static {
		for (SeekHelpType status : SeekHelpType.values()) {
			CODE_MAP.put(status.getValue(), status);
		}
	}

	@Setter
	private String value;

	public static SeekHelpType getTypeByValue(String value) {
		return CODE_MAP.get(value);
	}

	public static SeekHelpType get(int v) {
		String str = String.valueOf(v);
		return get(str);
	}

	public static SeekHelpType get(String str) {
		for (SeekHelpType s : values()) {
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
