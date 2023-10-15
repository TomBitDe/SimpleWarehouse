package com.home.simplewarehouse.location;

/**
 * Exception thrown when trying to drop a HandlingUnit to a Location that has no more capacity.  
 */
public class CapacityExceededException extends DimensionException {
	private static final long serialVersionUID = -8265509324969758651L;

	/**
	 * Constructor with the error message to use
	 * 
	 * @param errorMessage the error message content
	 */
	public CapacityExceededException(String errorMessage) {
		super(errorMessage);
	}
}
