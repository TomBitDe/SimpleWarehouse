package com.home.simplewarehouse.rest.applconfigservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.home.simplewarehouse.patterns.singleton.simplecache.ValueSourceEntry;
import com.home.simplewarehouse.patterns.singleton.simplecache.model.ApplConfig;
import com.home.simplewarehouse.rest.standardservices.StandardRestServices;
import com.home.simplewarehouse.utils.configurator.base.Configurator;
import com.home.simplewarehouse.views.KeyValueSourceEntry;

/**
 * RESTful Application configuration data service.
 */
@Path("/ApplConfigRestService")
@Stateless
public class ApplConfigRestService extends StandardRestServices {
	@EJB
	ApplConfigService applConfigService;
	
	@EJB
	Configurator configurator;

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
    	try {
            List<ApplConfig> applConfigList = applConfigService.getContent();

            GenericEntity<List<ApplConfig>> content
                    = new GenericEntity<List<ApplConfig>>(new ArrayList<>(applConfigList)) {
            };

            return Response.ok(content).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }

	/**
     * Get a string of all application configuration entries.
     *
     * @return the configuration list
     */
    @GET
    @Path("/TextContent")
    @Produces({MediaType.TEXT_PLAIN})
    public Response getTextContent() {
    	try {
            String textContent = configurator.getConfiguration();

            return Response.ok(textContent).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
    }

	/**
     * Get a list of all application configuration entries.
     *
     * @return the configuration list
     */
    @GET
    @Path("/Configuration")
    @Produces({MediaType.APPLICATION_XML})
    public Response getConfiguration() {
    	try {
    		Map<String, ValueSourceEntry> origin = configurator.getConfigurationMap();

    		List<KeyValueSourceEntry> target = new ArrayList<>();
    		
    		for (Map.Entry<String, ValueSourceEntry> entry : origin.entrySet()) {
                String key = entry.getKey();
                ValueSourceEntry valueSourceEntry = entry.getValue();

                // Access the elements
                String value = valueSourceEntry.getValue();
                String source = valueSourceEntry.getSource();

                // Process all now
                target.add(new KeyValueSourceEntry(key, value, source));
            }
    			
            GenericEntity<List<KeyValueSourceEntry>> configuration
                    = new GenericEntity<List<KeyValueSourceEntry>>(new ArrayList<>(target)) {
            };

            return Response.ok(configuration).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
        int intOffset = Integer.parseInt(offset);
        int intCount = Integer.parseInt(count);

    	try {
            // Get the configuration entries from the remote call as a List
            List<ApplConfig> applConfigList = applConfigService.getContent(intOffset, intCount);

            GenericEntity<List<ApplConfig>> content
                    = new GenericEntity<List<ApplConfig>>(new ArrayList<>(applConfigList)) {
            };

            return Response.ok(content).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
    	try {
            ApplConfig entry = applConfigService.getById(key);

            return Response.ok().entity(entry).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
    	try {
    		ApplConfig entry = applConfigService.getById(key);

    		if (entry != null) {
    			return Response.ok().entity("true").build();
    		}
    		return Response.ok().entity("false").build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
    	try {
            int val = applConfigService.count();

            return Response.ok().entity(String.valueOf(val)).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
    	try {
            ApplConfig entry = applConfigService.create(new ApplConfig(key, value));

            return Response.ok().entity(entry).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
    	try {
            ApplConfig entry = applConfigService.update(new ApplConfig(key, value));

            return Response.ok().entity(entry).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
    	try {
            ApplConfig entry = applConfigService.delete(key);

            return Response.ok().entity(entry).build();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}
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
    	try {
            applConfigService.refresh();
    	}
    	catch (Exception ex) {
    		return Response.ok().entity(ex.getMessage()).build();
    	}

        return Response.ok("Refreshed").build();
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
