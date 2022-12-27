package com.home.simplewarehouse.location;

/**
 * Exception thrown when trying to drop a HandlingUnit to a Location when width limit is reached.  
 */
public class WidthExceededException extends DimensionException {
	private static final long serialVersionUID = -3685484191564064330L;

	/**
	 * Constructor with the error message to use
	 * 
	 * @param errorMessage the error message content
	 */
	public WidthExceededException(String errorMessage) {
		super(errorMessage);
	}
	
	/**
	 * Constructor with error message and error to use
	 * 
	 * @param errorMessage the error message content
	 * @param err the error
	 */
	public WidthExceededException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}
