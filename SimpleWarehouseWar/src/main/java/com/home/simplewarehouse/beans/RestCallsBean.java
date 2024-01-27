package com.home.simplewarehouse.beans;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Bean class that provides REST access (example). 
 */
@Named
@RequestScoped
public class RestCallsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(RestCallsBean.class);
	
    private static final String APPL_CONFIG_REST_SERVICE_URL = "http://localhost:8080/war/resources/ApplConfigRestService";
    
    private String pingResult;

	/**
	 * Default constructor is not mandatory
	 */
    public RestCallsBean() {
    	super();
    }
    
	/**
	 * Calls the Ping from ApplConfigRestService
	 */
    public void fetchPingResultFromApplConfigRestService() {
        Client client = ClientBuilder.newClient();
        pingResult = client.target(APPL_CONFIG_REST_SERVICE_URL + "/Ping")
                              .request(MediaType.TEXT_PLAIN)
                              .get(String.class);
        
        LOG.debug("Result is [{}]", pingResult);
    }
    
    /**
     * Gets the ping result
     * 
     * @return the result
     */
    public String getPingResult() {
    	return pingResult;
    }
}
