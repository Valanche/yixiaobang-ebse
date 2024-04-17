package com.weixin.njuteam.exception;

/**
 * @author zhengyi
 */
public class FileDeleteException extends RuntimeException {

	public FileDeleteException() {
		super();
	}

	public FileDeleteException(Throwable cause) {
		super(cause);
	}

	public FileDeleteException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileDeleteException(String message) {
		super(message);
	}
}
