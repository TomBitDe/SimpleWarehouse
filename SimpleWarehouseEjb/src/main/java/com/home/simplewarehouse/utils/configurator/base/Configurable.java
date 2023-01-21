package com.home.simplewarehouse.utils.configurator.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines what is configurable.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configurable {
	/**
	 * Configurable String value
	 * 
	 * @return the value
	 */
	public String value();
}
