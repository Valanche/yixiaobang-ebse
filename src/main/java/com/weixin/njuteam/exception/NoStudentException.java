package com.weixin.njuteam.exception;

/**
 * @author Zyi
 */
public class NoStudentException extends RuntimeException {

	public NoStudentException() {
		super();
	}

	public NoStudentException(Throwable cause) {
		super(cause);
	}

	public NoStudentException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoStudentException(String message) {
		super(message);
	}
}
