package com.home.simplewarehouse.utils.configurator.base;

import javax.enterprise.inject.ResolutionException;

public class NotConfiguredException extends ResolutionException {
	private static final long serialVersionUID = 1L;

	public NotConfiguredException() {
		super();
	}

    public NotConfiguredException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotConfiguredException(String message) {
        super(message);
    }

    public NotConfiguredException(Throwable cause) {
        super(cause);
    }
}
