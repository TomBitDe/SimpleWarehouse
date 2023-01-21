package com.home.simplewarehouse.utils.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * The general REST configuration application path
 */
@ApplicationPath("resources")
public class RESTConfig extends Application {
	/**
	 * Default constructor just to implement the path
	 */
	public RESTConfig() {
		super();
	}
 }
