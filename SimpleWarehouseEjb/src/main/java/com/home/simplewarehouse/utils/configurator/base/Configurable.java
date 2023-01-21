package com.home.simplewarehouse.utils.configurator.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Defines what is configurable.
 */
public @interface Configurable {
	/**
	 * Configurable String value
	 * 
	 * @return the value
	 */
	public String value();
}
