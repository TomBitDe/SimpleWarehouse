package com.home.simplewarehouse.rest.handlingunitservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.rest.standardservices.StandardRestServices;

/**
 * RESTful Handling Unit service.
 */
@Path("/HandlingUnitRestService")
@Stateless
public class HandlingUnitRestService extends StandardRestServices {
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
    	try {
            List<HandlingUnit> handlingUnitList = handlingUnitService.getAll();

            GenericEntity<List<HandlingUnit>> content
                    = new GenericEntity<List<HandlingUnit>>(new ArrayList<>(handlingUnitList)) {
            };
            
            return Response.ok(content).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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

    	try {
            // Get the configuration entries from the remote call as a List
            List<HandlingUnit> handlingUnitList = handlingUnitService.getAll(intOffset, intCount);

            GenericEntity<List<HandlingUnit>> content
                    = new GenericEntity<List<HandlingUnit>>(new ArrayList<>(handlingUnitList)) {
            };

            return Response.ok(content).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
    	try {
            HandlingUnit handlingUnit = handlingUnitService.getById(key);

            return Response.ok().entity(handlingUnit).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
    	try {
        	HandlingUnit handlingUnit = handlingUnitService.getById(key);

            if (handlingUnit != null) {
                return Response.ok().entity("true").build();
            }
            return Response.ok().entity("false").build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
    	try {
        	HandlingUnit handlingUnit = handlingUnitService.createOrUpdate(new HandlingUnit(key));
        	
        	return Response.ok().entity(handlingUnit).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
    	try {
            handlingUnitService.delete(key);
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}

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
    	try {
            int val = handlingUnitService.count();

            return Response.ok().entity(String.valueOf(val)).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }
    
    /**
     * Drop on a Location the given Handling Unit.
     *
     * @param locationId the id of the Location to drop on
     * @param handlingUnitId the id of the Handling Unit to drop
     * 
     * @return in case of an exception the message
     */
    @POST
    @Path("/Drop/{locationId}/{handlingUnitId}")
    @Produces({MediaType.APPLICATION_XML})
    public Response drop(@PathParam("locationId") String locationId, @PathParam("handlingUnitId") String handlingUnitId) {
    	try {
    	    handlingUnitService.dropTo(locationId, handlingUnitId);
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    	
    	return Response.ok().build();
    }
    
    /**
     * Pick from a Location the given Handling Unit.
     * 
     * @param locationId the id of the Location to drop on
     * @param handlingUnitId the id of the Handling Unit to drop
     * 
     * @return in case of an exception the message
     */
    @POST
    @Path("/Pick/{locationId}/{handlingUnitId}")
    @Produces({MediaType.APPLICATION_XML})
    public Response pick(@PathParam("locationId") String locationId, @PathParam("handlingUnitId") String handlingUnitId) {
    	try {
    	    handlingUnitService.pickFrom(locationId, handlingUnitId);
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    	
    	return Response.ok().build();
    }
    
    /**
     * Pick from a FIFO / LIFO Location.
     * 
     * @param locationId the id of the Location to drop on
     * 
     * @return the handlingUnit or in case of an exception the message
     */
    @POST
    @Path("/Pick/{locationId}")
    @Produces({MediaType.APPLICATION_XML})
    public Response pick(@PathParam("locationId") String locationId) {
    	HandlingUnit handlingUnit = null;
    	
    	try {
            handlingUnit = handlingUnitService.pickFrom(locationId);
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}

        return Response.ok().entity(handlingUnit).build();
    }
    
	/**
	 * Assign a HandlingUnit to this base HandlingUnit
	 * 
	 * @param handlingUnitId the HandlingUnit to assign
	 * @param baseId the base HandlingUnit
	 * 
	 * @return the base HandlingUnit
	 */
    @POST
    @Path("/Assign/{handlingUnitId}/{baseId}")
    @Produces({MediaType.APPLICATION_XML})
    public Response assign(@PathParam("handlingUnitId") String handlingUnitId, @PathParam("baseId") String baseId) {
    	try {
        	HandlingUnit base = handlingUnitService.assign(handlingUnitId, baseId);
        	
        	return Response.ok().entity(base).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }

	/**
	 * Remove a HandlingUnit from this base HandlingUnit
	 * 
	 * @param handlingUnitId the HandlingUnit to remove
	 * @param baseId the base HandlingUnit
	 * 
	 * @return the base HandlingUnit
	 */
    @POST
    @Path("/Remove/{handlingUnitId}/{baseId}")
    @Produces({MediaType.APPLICATION_XML})
    public Response remove(@PathParam("handlingUnitId") String handlingUnitId, @PathParam("baseId") String baseId) {
    	try {
        	HandlingUnit base = handlingUnitService.remove(handlingUnitId, baseId);
        	
        	return Response.ok().entity(base).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }

	/**
	 * Move a HandlingUnit to another destination HandlingUnit
	 * 
	 * @param handlingUnitId the HandlingUnit to move
	 * @param destHandlingUnitId the destination HandlingUnit
	 * 
	 * @return the moved HandlingUnit
	 */
    @POST
    @Path("/Move/{handlingUnitId}/{destHandlingUnitId}")
    @Produces({MediaType.APPLICATION_XML})
    public Response move(@PathParam("handlingUnitId") String handlingUnitId, @PathParam("destHandlingUnitId") String destHandlingUnitId) {
    	try {
        	HandlingUnit moved = handlingUnitService.move(handlingUnitId, destHandlingUnitId);
        	
        	return Response.ok().entity(moved).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }

	/**
	 * Remove all HandlingUnits from this base HandlingUnit
	 * 
	 * @param baseId the base HandlingUnit id
	 * 
	 * @return a Set of all removed HandlingUnits (can be an empty Set)
	 */
    @POST
    @Path("/Free/{baseId}")
    @Produces({MediaType.APPLICATION_XML})
    public Response free(@PathParam("baseId") String baseId) {
    	try {
        	Set<HandlingUnit> removed = handlingUnitService.free(baseId);
        	
            GenericEntity<Set<HandlingUnit>> content
                    = new GenericEntity<Set<HandlingUnit>>(new HashSet<>(removed)) {
            };

            return Response.ok(content).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
