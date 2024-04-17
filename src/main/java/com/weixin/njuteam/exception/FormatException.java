package com.weixin.njuteam.exception;

/**
 * @author Zyi
 */
public class FormatException extends RuntimeException {

	public FormatException() {
		super();
	}

	public FormatException(Throwable cause) {
		super(cause);
	}

	public FormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public FormatException(String message) {
		super(message);
	}
}
