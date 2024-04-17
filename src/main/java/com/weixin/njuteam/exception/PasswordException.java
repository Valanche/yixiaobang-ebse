package com.weixin.njuteam.exception;

/**
 * @author Zyi
 */
public class PasswordException extends RuntimeException {

	public PasswordException() {
		super();
	}

	public PasswordException(Throwable cause) {
		super(cause);
	}

	public PasswordException(String message, Throwable cause) {
		super(message, cause);
	}

	public PasswordException(String message) {
		super(message);
	}
}
