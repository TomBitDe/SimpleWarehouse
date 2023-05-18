package com.home.simplewarehouse.rest.handlingunitservice;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.rest.standardservices.StandardServices;

/**
 * RESTful Handling Unit service.
 */
@Path("/HandlingUnitRestService")
@Stateless
public class HandlingUnitRestService extends StandardServices {
	@EJB
	HandlingUnitService handlingUnitService;
	
	/**
	 * Mandatory default constructor
	 */
	public HandlingUnitRestService() {
		super();
	}

	/**
     * Get a list of all Handling Units.
     *
     * @return the Handling Unit list
     */
    @GET
    @Path("/Content")
    @Produces({MediaType.APPLICATION_XML})
    public Response getContent() {
        List<HandlingUnit> handlingUnitList = handlingUnitService.getAll();

        GenericEntity<List<HandlingUnit>> content
                = new GenericEntity<List<HandlingUnit>>(new ArrayList<>(handlingUnitList)) {
        };
        
        return Response.ok(content).build();
    }
    
    /**
     * Get a list of Handling Units.
     *
     * @param offset the position to start fetching
     * @param count  the number of fetches to do
     *
     * @return the Handling Unit list based on offset and count
     */
    @GET
    @Path("/Content/{offset}/{count}")
    @Produces({MediaType.APPLICATION_XML})
    public Response getContent(@PathParam("offset") String offset, @PathParam("count") String count) {
        int intOffset = Integer.parseInt(offset);
        int intCount = Integer.parseInt(count);

        // Get the configuration entries from the remote call as a List
        List<HandlingUnit> handlingUnitList = handlingUnitService.getAll(intOffset, intCount);

        GenericEntity<List<HandlingUnit>> content
                = new GenericEntity<List<HandlingUnit>>(new ArrayList<>(handlingUnitList)) {
        };

        return Response.ok(content).build();
    }

	/**
     * Get a Handling Unit by its key value.
     *
     * @param key the key value
     *
     * @return the matching Handling Unit
     */
    @GET
    @Path("/Entry/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response getById(@PathParam("key") String key) {
        HandlingUnit handlingUnit = handlingUnitService.getById(key);

        return Response.ok().entity(handlingUnit).build();
    }

    /**
     * Check if a Handling Unit exists by its key.
     *
     * @param key the key of the Handling Unit
     *
     * @return true if the Handling Unit exists otherwise false
     */
    @GET
    @Path("/Exists/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response exists(@PathParam("key") String key) {
    	HandlingUnit handlingUnit = handlingUnitService.getById(key);

        if (handlingUnit != null) {
            return Response.ok().entity("true").build();
        }
        return Response.ok().entity("false").build();

    }

    /**
     * Create a Handling Unit with default values.
     *
     * @param key the access key of the Handling Unit
     *
     * @return the data of the created Handling Unit
     */
    @PUT
    @Path("/Entry/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response create(@PathParam("key") String key) {
    	HandlingUnit handlingUnit = handlingUnitService.createOrUpdate(new HandlingUnit(key));
    	
    	return Response.ok().entity(handlingUnit).build();
    }
    
    /**
     * Delete a Handling Unit by its key.
     *
     * @param key the key of the Handling Unit to delete
     *
     * @return the data of the removed Handling Unit
     */
    @DELETE
    @Path("/Entry/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response delete(@PathParam("key") String key) {
        handlingUnitService.delete(key);

        return Response.ok().build();
    }

	/**
     * Count the number of Handling Units.
     *
     * @return the number of Handling Units
     */
    @GET
    @Path("/Count")
    @Produces({MediaType.APPLICATION_XML})
    public Response count() {
        int val = handlingUnitService.count();

        return Response.ok().entity(String.valueOf(val)).build();
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
        return "GET, DELETE, PUT, POST";
    }
}
