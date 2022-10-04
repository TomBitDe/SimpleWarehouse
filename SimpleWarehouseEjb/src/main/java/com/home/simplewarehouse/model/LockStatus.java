package com.home.simplewarehouse.model;

public enum LockStatus {
	UNLOCKED,
	PICK_LOCKED,
	DROP_LOCKED,
	LOCKED,        /* Both for PICK and DROP locked */
}
