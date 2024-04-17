package com.weixin.njuteam.exception;

/**
 * @author Zyi
 */
public class DuplicateTitleException extends RuntimeException {

	public DuplicateTitleException() {
		super();
	}

	public DuplicateTitleException(Throwable cause) {
		super(cause);
	}

	public DuplicateTitleException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateTitleException(String message) {
		super(message);
	}
}
