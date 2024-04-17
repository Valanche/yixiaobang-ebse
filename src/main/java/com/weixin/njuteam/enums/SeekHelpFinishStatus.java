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
public enum SeekHelpFinishStatus implements ValueEnum {

	/**
	 * 进行中
	 */
	IN_PROGRESS("进行中"),
	/**
	 * 未解决
	 */
	NOT_FINISHED("未解决"),
	/**
	 * 已解决
	 */
	FINISHED("已解决"),
	/**
	 *
	 */
	DELETED("已删除");

	private static final Map<String, SeekHelpFinishStatus> CODE_MAP = new HashMap<>();

	static {
		for (SeekHelpFinishStatus status : SeekHelpFinishStatus.values()) {
			CODE_MAP.put(status.getValue(), status);
		}
	}

	@Setter
	private String value;

	public static SeekHelpFinishStatus getStatusByValue(String value) {
		return CODE_MAP.get(value);
	}

	public static SeekHelpFinishStatus get(int v) {
		String str = String.valueOf(v);
		return get(str);
	}

	public static SeekHelpFinishStatus get(String str) {
		for (SeekHelpFinishStatus s : values()) {
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
