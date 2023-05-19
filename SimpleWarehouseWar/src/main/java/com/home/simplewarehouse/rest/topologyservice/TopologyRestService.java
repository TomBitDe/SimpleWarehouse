package com.home.simplewarehouse.rest.topologyservice;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.home.simplewarehouse.rest.standardservices.StandardServices;
import com.home.simplewarehouse.topology.SampleWarehouseService;

/**
 * RESTful Topology data service.
 */
@Path("/TopologyRestService")
@Stateless
public class TopologyRestService extends StandardServices {
	@EJB
	SampleWarehouseService sampleWarehouseService;
	
	/**
	 * Mandatory default constructor
	 */
	public TopologyRestService() {
		super();
	}

    /**
     * Create Simple Warehouse sample data.
     */
    @PUT
    @Path("/SampleData")
    @Produces({MediaType.APPLICATION_XML})
    public Response initialize() {
    	sampleWarehouseService.initialize();
    	
    	return Response.ok().build();
    }
    
    /**
     * Delete the Simple Warehouse sample data.
     */
    @DELETE
    @Path("/SampleData")
    @Produces({MediaType.APPLICATION_XML})
    public Response cleanup() {
    	sampleWarehouseService.cleanup();
    	
    	return Response.ok().build();
    }

    /**
     * Give a list of all supported service operations.
     *
     * @return a list of service operations
     */
    @OPTIONS
    @Path("/Options")
    @Produces({MediaType.TEXT_PLAIN})
    @Override
    public String getSupportedOperations() {
        return "OPTIONS, GET, PUT, DELETE";
    }
}
