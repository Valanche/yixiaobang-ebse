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
public enum NucleicAcidType implements ValueEnum {

	/**
	 * 核酸预约通知
	 */
	BOOKING("预约"),
	/**
	 * 核酸上报通知
	 */
	REPORTING("上报"),
	/**
	 * 核酸检测通知
	 */
	TESTING("检测");

	private static final Map<String, NucleicAcidType> CODE_MAP = new HashMap<>();

	static {
		for (NucleicAcidType acidType : NucleicAcidType.values()) {
			CODE_MAP.put(acidType.getValue(), acidType);
		}
	}

	@Setter
	private String value;

	public static NucleicAcidType getTypeByValue(String value) {
		return CODE_MAP.get(value);
	}

	public static NucleicAcidType get(int v) {
		String str = String.valueOf(v);
		return get(str);
	}

	public static NucleicAcidType get(String str) {
		for (NucleicAcidType s : values()) {
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
