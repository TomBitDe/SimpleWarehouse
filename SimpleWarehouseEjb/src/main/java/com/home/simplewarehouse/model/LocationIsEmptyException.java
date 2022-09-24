package com.home.simplewarehouse.model;

public class LocationIsEmptyException extends Exception {
	private static final long serialVersionUID = -8265509324969758651L;

	public LocationIsEmptyException(String errorMessage) {
		super(errorMessage);
	}
	
	public LocationIsEmptyException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}
