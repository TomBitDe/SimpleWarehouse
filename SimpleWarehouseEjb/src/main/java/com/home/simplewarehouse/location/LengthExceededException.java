package com.home.simplewarehouse.location;

/**
 * Exception thrown when trying to drop a HandlingUnit to a Location when length limit is reached.  
 */
public class LengthExceededException extends DimensionException {
	private static final long serialVersionUID = -5701304007906265190L;

	/**
	 * Constructor with the error message to use
	 * 
	 * @param errorMessage the error message content
	 */
	public LengthExceededException(String errorMessage) {
		super(errorMessage);
	}
	
	/**
	 * Constructor with error message and error to use
	 * 
	 * @param errorMessage the error message content
	 * @param err the error
	 */
	public LengthExceededException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}
