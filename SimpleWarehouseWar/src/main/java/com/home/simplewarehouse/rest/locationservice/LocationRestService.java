package com.home.simplewarehouse.rest.locationservice;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.model.Location;

/**
 * RESTful Location handling service.
 */
@Path("/LocationRestService")
@Stateless
public class LocationRestService {
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
        List<Location> locationList = locationService.getAll();

        GenericEntity<List<Location>> content
                = new GenericEntity<List<Location>>(new ArrayList<>(locationList)) {
        };
        
        return Response.ok(content).build();
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

        // Get the configuration entries from the remote call as a List
        List<Location> locationList = locationService.getAll(intOffset, intCount);

        GenericEntity<List<Location>> content
                = new GenericEntity<List<Location>>(new ArrayList<>(locationList)) {
        };

        return Response.ok(content).build();
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
}
