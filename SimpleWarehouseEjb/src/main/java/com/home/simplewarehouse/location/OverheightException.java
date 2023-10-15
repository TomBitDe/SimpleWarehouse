package com.home.simplewarehouse.location;

/**
 * Exception thrown when trying to drop a HandlingUnit to a Location when height limit is reached.  
 */
public class OverheightException extends DimensionException {
	private static final long serialVersionUID = -5654290849844279923L;

	/**
	 * Constructor with the error message to use
	 * 
	 * @param errorMessage the error message content
	 */
	public OverheightException(String errorMessage) {
		super(errorMessage);
	}
}
