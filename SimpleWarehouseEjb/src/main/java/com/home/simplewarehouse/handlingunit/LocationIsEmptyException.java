package com.home.simplewarehouse.handlingunit;

/**
 * Exception thrown when trying to pick a HandlingUnit from a Location that is empty.  
 */
public class LocationIsEmptyException extends Exception {
	private static final long serialVersionUID = -8265509324969758651L;

	/**
	 * Constructor with the error message to use
	 * 
	 * @param errorMessage the error message content
	 */
	public LocationIsEmptyException(String errorMessage) {
		super(errorMessage);
	}
	
	/**
	 * Constructor with error message and error to use
	 * 
	 * @param errorMessage the error message content
	 * @param err the error
	 */
	public LocationIsEmptyException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}
