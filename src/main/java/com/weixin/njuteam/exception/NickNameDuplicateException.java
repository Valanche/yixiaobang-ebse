package com.weixin.njuteam.exception;

/**
 * @author Zyi
 */
public class NickNameDuplicateException extends RuntimeException {

	public NickNameDuplicateException() {
		super();
	}

	public NickNameDuplicateException(Throwable cause) {
		super(cause);
	}

	public NickNameDuplicateException(String message, Throwable cause) {
		super(message, cause);
	}

	public NickNameDuplicateException(String message) {
		super(message);
	}
}
