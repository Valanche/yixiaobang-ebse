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
public enum HelpFinishStatus implements ValueEnum {

	/**
	 * 帮助信息仍然进行中
	 */
	IN_PROGRESS("进行中"),
	/**
	 * 帮助信息已经截止
	 */
	CLOSED("已截止"),
	/**
	 *
	 */
	DELETED("已删除");

	private static final Map<String, HelpFinishStatus> CODE_MAP = new HashMap<>();

	static {
		for (HelpFinishStatus status : HelpFinishStatus.values()) {
			CODE_MAP.put(status.getValue(), status);
		}
	}

	@Setter
	private String value;

	public static HelpFinishStatus getStatusByValue(String value) {
		return CODE_MAP.get(value);
	}

	public static HelpFinishStatus get(int v) {
		String str = String.valueOf(v);
		return get(str);
	}

	public static HelpFinishStatus get(String str) {
		for (HelpFinishStatus s : values()) {
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
