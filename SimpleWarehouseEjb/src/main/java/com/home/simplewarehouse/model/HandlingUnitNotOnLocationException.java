package com.home.simplewarehouse.model;

public class HandlingUnitNotOnLocationException extends Exception {
	private static final long serialVersionUID = -8265509324969758651L;

	public HandlingUnitNotOnLocationException(String errorMessage) {
		super(errorMessage);
	}
	
	public HandlingUnitNotOnLocationException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}
