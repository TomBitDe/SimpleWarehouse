package com.home.simplewarehouse.model;

/**
 * Logical lock status (manually controlled by the user).
 */
public enum LockStatus {
	/**
	 * Completely unlocked; pick and drop allowed
	 */
	UNLOCKED,
	/**
	 * Pick is NOT allowed
	 */
	PICK_LOCKED,
	/**
	 * Drop is NOT allowed
	 */
	DROP_LOCKED,
	/**
	 * Pick AND drop is NOT allowed
	 */
	LOCKED
}
