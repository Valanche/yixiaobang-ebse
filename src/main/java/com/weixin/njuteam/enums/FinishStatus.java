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
public enum FinishStatus implements ValueEnum {

	/**
	 * 代表通知进行中
	 */
	IN_PROGRESS("进行中"),
	/**
	 * 代表通知未完成
	 */
	TO_BE_CONTINUE("未完成"),
	/**
	 * 代表通知已完成
	 */
	DONE("已完成");

	private static final Map<String, FinishStatus> CODE_MAP = new HashMap<>();

	static {
		for (FinishStatus status : FinishStatus.values()) {
			CODE_MAP.put(status.getValue(), status);
		}
	}

	@Setter
	private String value;

	public static FinishStatus getStatusByValue(String value) {
		return CODE_MAP.get(value);
	}

	public static FinishStatus get(String value) {
		for (FinishStatus s : values()) {
			if (s.getValue().equals(value)) {
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
