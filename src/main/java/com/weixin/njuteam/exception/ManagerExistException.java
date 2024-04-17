package com.weixin.njuteam.exception;

/**
 * @author Zyi
 */
public class ManagerExistException extends RuntimeException {

	public ManagerExistException() {
		super();
	}

	public ManagerExistException(Throwable cause) {
		super(cause);
	}

	public ManagerExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public ManagerExistException(String message) {
		super(message);
	}
}
