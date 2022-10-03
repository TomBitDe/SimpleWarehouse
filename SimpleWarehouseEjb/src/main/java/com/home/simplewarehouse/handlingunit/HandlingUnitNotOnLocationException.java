package com.home.simplewarehouse.handlingunit;

/**
 * Exception thrown when trying to pick a HandlingUnit from a Location where it is not present.  
 */
public class HandlingUnitNotOnLocationException extends Exception {
	private static final long serialVersionUID = -8265509324969758651L;

	/**
	 * Constructor with the error message to use
	 * 
	 * @param errorMessage the error message content
	 */
	public HandlingUnitNotOnLocationException(String errorMessage) {
		super(errorMessage);
	}
	
	/**
	 * Constructor with error message and error to use
	 * 
	 * @param errorMessage the error message content
	 * @param err the error
	 */
	public HandlingUnitNotOnLocationException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}
