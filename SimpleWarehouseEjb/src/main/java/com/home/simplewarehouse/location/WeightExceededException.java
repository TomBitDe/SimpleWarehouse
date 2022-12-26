package com.home.simplewarehouse.location;

/**
 * Exception thrown when trying to drop a HandlingUnit to a Location when weight limit is reached.  
 */
public class WeightExceededException extends Exception {
	private static final long serialVersionUID = 8342809185587103859L;

	/**
	 * Constructor with the error message to use
	 * 
	 * @param errorMessage the error message content
	 */
	public WeightExceededException(String errorMessage) {
		super(errorMessage);
	}
	
	/**
	 * Constructor with error message and error to use
	 * 
	 * @param errorMessage the error message content
	 * @param err the error
	 */
	public WeightExceededException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}
