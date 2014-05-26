package com.gs.common.exception;

public class DuplicationException extends RuntimeException  {
	private static final long serialVersionUID = 1L;

	public DuplicationException() {
	}

	public DuplicationException(String message) {
		super(message);
	}

	public DuplicationException(Throwable cause) {
		super(cause);
	}

	public DuplicationException(String message, Throwable cause) {
		super(message, cause);
	}

}
