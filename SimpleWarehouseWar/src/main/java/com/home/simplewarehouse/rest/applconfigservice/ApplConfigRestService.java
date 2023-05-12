package com.home.simplewarehouse.rest.applconfigservice;

import java.util.ArrayList;
import java.util.List;

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

import com.home.simplewarehouse.patterns.singleton.simplecache.ApplConfigService;
import com.home.simplewarehouse.patterns.singleton.simplecache.model.ApplConfig;

/**
 * RESTful Application configuration data service.
 */
@Path("/ApplConfigRestService")
@Stateless
public class ApplConfigRestService {
	@EJB
	ApplConfigService applConfigService;

	/**
	 * Mandatory default constructor
	 */
	public ApplConfigRestService() {
		super();
	}

	/**
     * Get a list of all application configuration entries.
     *
     * @return the configuration list
     */
    @GET
    @Path("/Content")
    @Produces({MediaType.APPLICATION_XML})
    public Response getContent() {
        List<ApplConfig> applConfigList = applConfigService.getContent();

        GenericEntity<List<ApplConfig>> content
                = new GenericEntity<List<ApplConfig>>(new ArrayList<>(applConfigList)) {
        };

        Response response = Response.ok(content).build();

        return response;
    }

    /**
     * Get a list of application configuration entries.
     *
     * @param offset the position to start fetching
     * @param count  the number of fetches to do
     *
     * @return the configuration list based on offset and count
     */
    @GET
    @Path("/Content/{offset}/{count}")
    @Produces({MediaType.APPLICATION_XML})
    public Response getContent(@PathParam("offset") String offset, @PathParam("count") String count) {
        int intOffset = Integer.valueOf(offset);
        int intCount = Integer.valueOf(count);

        // Get the AirportVOs from the remote call as a List
        List<ApplConfig> applConfigList = applConfigService.getContent(intOffset, intCount);

        GenericEntity<List<ApplConfig>> content
                = new GenericEntity<List<ApplConfig>>(new ArrayList<ApplConfig>(applConfigList)) {
        };

        Response response = Response.ok(content).build();

        return response;
    }


	/**
     * Get a configuration entry by its key value.
     *
     * @param key the key value
     *
     * @return the matching configuration entry
     */
    @GET
    @Path("/Entry/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response getById(@PathParam("key") String key) {
        ApplConfig entry = applConfigService.getById(key);

        Response response = Response.ok().entity(entry).build();

        return response;
    }

    /**
     * Check if an configuration entry exists by its key.
     *
     * @param key the key of the configuration entry
     *
     * @return true if the configuration entry exists otherwise false
     */
    @GET
    @Path("/Exists/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response exists(@PathParam("key") String key) {
    	ApplConfig entry = applConfigService.getById(key);

        if (entry != null) {
            return Response.ok().entity("true").build();
        }
        return Response.ok().entity("false").build();

    }

	/**
     * Count the number of configuration entries.
     *
     * @return the number of entries
     */
    @GET
    @Path("/Count")
    @Produces({MediaType.APPLICATION_XML})
    public Response count() {
        int val = applConfigService.count();

        Response response = Response.ok().entity(String.valueOf(val)).build();

        return response;
    }

    /**
     * Create an application configuration entry.
     *
     * @param key the access key of the entry
     * @param value the value of the configuration entry
     *
     * @return the data of the created entry
     */
    @PUT
    @Path("/Entry/{key}/{value}")
    @Produces({MediaType.APPLICATION_XML})
    public Response create(@PathParam("key") String key,
                           @PathParam("value") String value) {

        ApplConfig entry = applConfigService.create(new ApplConfig(key, value));

        Response response;

        response = Response.ok().entity(entry).build();

        return response;
    }

    /**
     * Update an application configuration entry.
     *
     * @param key the access key of the entry
     * @param value the new value of the configuration entry
     *
     * @return the data of the updated entry or an empty response in case it failed
     */
    @POST
    @Path("/Entry/{key}/{value}")
    @Produces({MediaType.APPLICATION_XML})
    public Response update(@PathParam("key") String key,
                           @PathParam("value") String value) {

        ApplConfig entry = applConfigService.update(new ApplConfig(key, value));

        Response response;

        response = Response.ok().entity(entry).build();

        return response;
    }

    /**
     * Delete an application configuration entry.
     *
     * @param key the key of the entry to delete
     *
     * @return the data of the removed entry
     */
    @DELETE
    @Path("/Entry/{key}")
    @Produces({MediaType.APPLICATION_XML})
    public Response delete(@PathParam("key") String key) {
        ApplConfig entry = applConfigService.delete(key);

        Response response;

        response = Response.ok().entity(entry).build();

        return response;
    }

    /**
     * Trigger a refresh of the cache data.
     *
     * @return the text "Refresh"
     */
    @GET
    @Path("/Refresh")
    @Produces({MediaType.TEXT_PLAIN})
    public Response refresh() {
        applConfigService.refresh();

        return Response.ok("Refreshed").build();
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
        return "GET, DELETE, PUT, POST";
    }
}
