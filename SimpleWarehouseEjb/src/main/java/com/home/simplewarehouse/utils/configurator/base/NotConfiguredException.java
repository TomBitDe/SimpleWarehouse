package com.home.simplewarehouse.utils.configurator.base;

import javax.enterprise.inject.ResolutionException;

/**
 * Exception in case something is not configured. 
 */
public class NotConfiguredException extends ResolutionException {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public NotConfiguredException() {
		super();
	}

	/**
	 * Create this exception
	 * 
	 * @param message the message to show
	 * @param cause the exception cause
	 */
    public NotConfiguredException(String message, Throwable cause) {
        super(message, cause);
    }

	/**
	 * Create this exception
	 * 
	 * @param message the message to show
	 */
    public NotConfiguredException(String message) {
        super(message);
    }

	/**
	 * Create this exception
	 * 
	 * @param cause the exception cause
	 */
    public NotConfiguredException(Throwable cause) {
        super(cause);
    }
}
