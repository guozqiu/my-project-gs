package com.gs.common.exception;

public class NoAccessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoAccessException() {
	}

	public NoAccessException(String message) {
		super(message);
	}

	public NoAccessException(Throwable cause) {
		super(cause);
	}

	public NoAccessException(String message, Throwable cause) {
		super(message, cause);
	}

}
