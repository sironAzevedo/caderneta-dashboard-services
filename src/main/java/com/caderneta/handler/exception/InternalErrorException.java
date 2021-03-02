package com.caderneta.handler.exception;

public class InternalErrorException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InternalErrorException(String message) {
		super(message);
	}

	public InternalErrorException(Exception e) {
		super(e);
	} 
}
