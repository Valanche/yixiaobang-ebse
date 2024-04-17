package com.weixin.njuteam.exception;

/**
 * @author Zyi
 */
public class UpdateException extends RuntimeException {

	public UpdateException() {
		super();
	}

	public UpdateException(Throwable cause) {
		super(cause);
	}

	public UpdateException(String message, Throwable cause) {
		super(message, cause);
	}

	public UpdateException(String message) {
		super(message);
	}
}
