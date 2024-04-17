package com.weixin.njuteam.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response<T> {

	private Integer code;

	private String msg;

	private T data;

	public Response(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	@Override
	public String toString() {
		return code + msg + "\n data: " + (data == null ? "null" : data.toString());
	}
}
