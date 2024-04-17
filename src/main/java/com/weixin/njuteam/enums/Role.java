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
public enum Role implements ValueEnum {

	/**
	 * 普通管理员
	 */
	COMMON_MANAGER("普通管理员"),
	/**
	 * 超级管理员
	 */
	SUPER_MANAGER("超级管理员");

	private static final Map<String, Role> ROLE_MAP = new HashMap<>();

	static {
		for (Role role : Role.values()) {
			ROLE_MAP.put(role.getValue(), role);
		}
	}

	@Setter
	private String value;

	public static Role getRoleByValue(String value) {
		return ROLE_MAP.get(value);
	}

	public static Role get(String value) {
		for (Role s : values()) {
			if (s.getValue().equals(value)) {
				return s;
			}
		}
		return null;
	}

	@Override
	public String getValue() {
		return value;
	}
}
