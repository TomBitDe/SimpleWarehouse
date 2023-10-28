package com.home.simplewarehouse.location;

/**
 * Exception thrown when trying to drop to a Location a HandlingUnit that exceeds any Dimension.  
 */
public class DimensionException extends Exception {
	private static final long serialVersionUID = 6822904593272010779L;

	/**
	 * Constructor with the error message to use
	 * 
	 * @param errorMessage the error message content
	 */
	public DimensionException(String errorMessage) {
		super(errorMessage);
	}
}
