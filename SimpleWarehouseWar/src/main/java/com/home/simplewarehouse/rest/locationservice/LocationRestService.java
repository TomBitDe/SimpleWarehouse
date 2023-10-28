package com.home.simplewarehouse.rest.locationservice;

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

import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.rest.standardservices.StandardRestServices;

/**
 * RESTful Location handling service.
 */
@Path("/LocationRestService")
@Stateless
public class LocationRestService extends StandardRestServices {
	@EJB
	LocationService locationService;

	/**
	 * Mandatory default constructor
	 */
	public LocationRestService() {
		super();
	}

	/**
     * Get a list of all Locations.
     *
     * @return the Location list
     */
    @GET
    @Path("/Content")
    @Produces({MediaType.APPLICATION_XML})
    public Response getContent() {
    	try {
            List<Location> locationList = locationService.getAll();

            GenericEntity<List<Location>> content
                    = new GenericEntity<List<Location>>(new ArrayList<>(locationList)) {
            };
            
            return Response.ok(content).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }
    
    /**
     * Get a list of Locations.
     *
     * @param offset the position to start fetching
     * @param count  the number of fetches to do
     *
     * @return the Location list based on offset and count
     */
    @GET
    @Path("/Content/{offset}/{count}")
    @Produces({MediaType.APPLICATION_XML})
    public Response getContent(@PathParam("offset") String offset, @PathParam("count") String count) {
        int intOffset = Integer.parseInt(offset);
        int intCount = Integer.parseInt(count);

    	try {
            // Get the configuration entries from the remote call as a List
            List<Location> locationList = locationService.getAll(intOffset, intCount);

            GenericEntity<List<Location>> content
                    = new GenericEntity<List<Location>>(new ArrayList<>(locationList)) {
            };

            return Response.ok(content).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }

	/**
     * Get a Location by its key value.
     *
     * @param key the key value
     *
     * @return the matching Location
     */
    @GET
    @Path("/Entry/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response getById(@PathParam("key") String key) {
    	try {
            Location location = locationService.getById(key);

            return Response.ok().entity(location).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }

    /**
     * Check if a Location exists by its key.
     *
     * @param key the key of the Location
     *
     * @return true if the Location exists otherwise false
     */
    @GET
    @Path("/Exists/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response exists(@PathParam("key") String key) {
    	try {
        	Location location = locationService.getById(key);

            if (location != null) {
                return Response.ok().entity("true").build();
            }
            return Response.ok().entity("false").build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }

    /**
     * Create a Location with default values.
     *
     * @param key the access key of the Location
     *
     * @return the data of the created Location
     */
    @PUT
    @Path("/Entry/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response create(@PathParam("key") String key) {
    	try {
        	Location location = locationService.createOrUpdate(new Location(key));
        	
        	return Response.ok().entity(location).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }
    
    /**
     * Delete a Location by its key.
     *
     * @param key the key of the location to delete
     *
     * @return the data of the removed Location
     */
    @DELETE
    @Path("/Entry/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response delete(@PathParam("key") String key) {
    	try {
            locationService.delete(key);
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}

        return Response.ok().build();
    }

	/**
     * Count the number of Locations.
     *
     * @return the number of Locations
     */
    @GET
    @Path("/Count")
    @Produces({MediaType.APPLICATION_XML})
    public Response count() {
    	try {
            int val = locationService.count();

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
