package com.home.simplewarehouse.handlingunit;

/**
 * Exception thrown when trying to drop a HandlingUnit to a Location that has no more capacity.  
 */
public class LocationCapacityExceededException extends Exception {
	private static final long serialVersionUID = -8265509324969758651L;

	/**
	 * Constructor with the error message to use
	 * 
	 * @param errorMessage the error message content
	 */
	public LocationCapacityExceededException(String errorMessage) {
		super(errorMessage);
	}
	
	/**
	 * Constructor with error message and error to use
	 * 
	 * @param errorMessage the error message content
	 * @param err the error
	 */
	public LocationCapacityExceededException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}
