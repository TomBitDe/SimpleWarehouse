package com.home.simplewarehouse.rest.standardservices;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class StandardServices {
	/**
	 * Mandatory default constructor
	 */
	public StandardServices() {
		super();
	}

    /**
     * Just check if the REST service is available.
     *
     * @return the text "Pong"
     */
    @GET
    @Path("/Ping")
    @Produces({MediaType.TEXT_PLAIN})
    public Response ping() {
    	return Response.ok("Pong").build();
    }
    
    /**
     * Give a list of all supported service operations.
     *
     * @return a list of service operations
     */
    @OPTIONS
    @Path("/Options")
    @Produces({MediaType.TEXT_PLAIN})
    public String getSupportedOperations() {
        return "OPTIONS, GET";
    }
}
