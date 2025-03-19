package com.home.simplewarehouse.rest.zoneservice;

import java.util.HashSet;
import java.util.Set;

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

import com.home.simplewarehouse.model.Zone;
import com.home.simplewarehouse.rest.standardservices.StandardRestServices;
import com.home.simplewarehouse.zone.ZoneService;

/**
 * RESTful Zone handling service.
 */
@Path("/ZoneRestService")
@Stateless
public class ZoneRestService extends StandardRestServices {
	@EJB
	ZoneService zoneService;

	/**
	 * Mandatory default constructor
	 */
	public ZoneRestService() {
		super();
	}

	/**
     * Get a list of all Zones.
     *
     * @return the Zone list
     */
    @GET
    @Path("/Content")
    @Produces({MediaType.APPLICATION_XML})
    public Response getContent() {
    	try {
            Set<Zone> zoneSet = zoneService.getAll();

            GenericEntity<Set<Zone>> content
                    = new GenericEntity<Set<Zone>>(new HashSet<>(zoneSet)) {
            };
            
            return Response.ok(content).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }
    
    /**
     * Get a list of Zones.
     *
     * @param offset the position to start fetching
     * @param count  the number of fetches to do
     *
     * @return the Zone list based on offset and count
     */
    @GET
    @Path("/Content/{offset}/{count}")
    @Produces({MediaType.APPLICATION_XML})
    public Response getContent(@PathParam("offset") String offset, @PathParam("count") String count) {
        int intOffset = Integer.parseInt(offset);
        int intCount = Integer.parseInt(count);

    	try {
            // Get the configuration entries from the remote call as a Set
            Set<Zone> zoneSet = zoneService.getAll(intOffset, intCount);

            GenericEntity<Set<Zone>> content
                    = new GenericEntity<Set<Zone>>(new HashSet<>(zoneSet)) {
            };

            return Response.ok(content).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }

	/**
     * Get a Zone by its key value.
     *
     * @param key the key value
     *
     * @return the matching Zone
     */
    @GET
    @Path("/Entry/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response getById(@PathParam("key") String key) {
    	try {
            Zone zone = zoneService.getById(key);

            return Response.ok().entity(zone).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }

    /**
     * Check if a Zone exists by its key.
     *
     * @param key the key of the Zone
     *
     * @return true if the Zone exists otherwise false
     */
    @GET
    @Path("/Exists/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response exists(@PathParam("key") String key) {
    	try {
        	Zone zone = zoneService.getById(key);

            if (zone != null) {
                return Response.ok().entity("true").build();
            }
            return Response.ok().entity("false").build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }

    /**
     * Create a Zone with default values.
     *
     * @param key the access key of the Zone
     *
     * @return the data of the created Zone
     */
    @PUT
    @Path("/Entry/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response create(@PathParam("key") String key) {
    	try {
    		Zone zone = zoneService.createOrUpdate(new Zone(key));
        	
        	return Response.ok().entity(zone).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }
    
    /**
     * Delete a Zone by its key.
     *
     * @param key the key of the Zone to delete
     *
     * @return the data of the removed Zone
     */
    @DELETE
    @Path("/Entry/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response delete(@PathParam("key") String key) {
    	try {
            zoneService.delete(key);
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}

        return Response.ok().build();
    }

	/**
     * Count the number of Zones.
     *
     * @return the number of Zones
     */
    @GET
    @Path("/Count")
    @Produces({MediaType.APPLICATION_XML})
    public Response count() {
    	try {
            int val = zoneService.count();

            return Response.ok().entity(String.valueOf(val)).build();
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
        return "GET, DELETE, PUT";
    }
}
