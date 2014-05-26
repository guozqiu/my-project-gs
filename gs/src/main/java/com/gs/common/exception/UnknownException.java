package com.gs.common.exception;

public class UnknownException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnknownException() {
	}

	public UnknownException(String message) {
		super(message);
	}

	public UnknownException(Throwable cause) {
		super(cause);
	}

	public UnknownException(String message, Throwable cause) {
		super(message, cause);
	}

}
