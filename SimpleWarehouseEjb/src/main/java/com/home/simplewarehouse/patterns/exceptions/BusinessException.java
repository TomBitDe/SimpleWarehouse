package com.home.simplewarehouse.patterns.exceptions;

public class BusinessException extends Exception {
    
	private static final long serialVersionUID = -6441190786811364924L;

	public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
