package com.weixin.njuteam.exception;

/**
 * @author Zyi
 */
public class AddTaskException extends RuntimeException {

	public AddTaskException() {
		super();
	}

	public AddTaskException(Throwable cause) {
		super(cause);
	}

	public AddTaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public AddTaskException(String message) {
		super(message);
	}
}
