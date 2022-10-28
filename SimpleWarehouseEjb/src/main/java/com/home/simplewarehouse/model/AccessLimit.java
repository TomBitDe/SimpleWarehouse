package com.home.simplewarehouse.model;

/**
 * How a handling unit placed on a location can be accessed.
 */
public enum AccessLimit {
	/**
	 * Location allows random access. A handling unit can be accessed at any time.
	 * No sequence has to be considered
	 */
	RANDOM,
	/**
	 * Location allows only first in first out access. Only this sequence is valid
	 */
	FIFO,
	/**
	 * Location allows only last in first out access. Only this sequence is valid
	 */
	LIFO
}
