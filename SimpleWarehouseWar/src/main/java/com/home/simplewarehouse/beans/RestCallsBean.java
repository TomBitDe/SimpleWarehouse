package com.home.simplewarehouse.beans;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    private static final String EXISTS_TIMER_1 = "/Exists/Timer1";
    private static final String EXISTS_TIMER_2 = "/Exists/Timer2";
    private static final String RESULT_FORMAT = "Result is [{}]";
    
    private Form formData = new Form();
    
    private String pingResult;
    private String refreshResult;
    private Response timerCallResult;
    private String key;
    private String value;

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
        client.close();
        
        LOG.debug(RESULT_FORMAT, pingResult);
    }
    
	/**
	 * Calls the Refresh from ApplConfigRestService
	 */
    public void fetchRefreshResultFromApplConfigRestService() {
        Client client = ClientBuilder.newClient();
        refreshResult = client.target(APPL_CONFIG_REST_SERVICE_URL + "/Refresh")
                              .request(MediaType.TEXT_PLAIN)
                              .get(String.class);
        client.close();
        
        LOG.debug(RESULT_FORMAT, refreshResult);
    }
    
    /**
     * Starts timer 1
     */
    public void startTimer1() {
        Client client = ClientBuilder.newClient();
        
        String exists = client.target(APPL_CONFIG_REST_SERVICE_URL + EXISTS_TIMER_1)
                .request(MediaType.APPLICATION_XML)
                .get(String.class);

        setFormData("Timer1", "UP");
        
        if (exists.equals("false")) {
            timerCallResult = client.target(APPL_CONFIG_REST_SERVICE_URL + getFormData())
                    .request(MediaType.APPLICATION_XML)
                    .put(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED));
        }
        else {
            timerCallResult = client.target(APPL_CONFIG_REST_SERVICE_URL + getFormData())
                    .request(MediaType.APPLICATION_XML)
                    .post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED));
        }
        
        client.close();
        
        LOG.debug(RESULT_FORMAT, timerCallResult);
    }
    
    /**
     * Stops timer 1
     */
    public void stopTimer1() {
        Client client = ClientBuilder.newClient();
        
        setFormData("Timer1", "DOWN");
        
        timerCallResult = client.target(APPL_CONFIG_REST_SERVICE_URL + getFormData())
        		.request(MediaType.APPLICATION_XML)
                .post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED));
        
        client.close();
        
        LOG.debug(RESULT_FORMAT, timerCallResult);
    }
    
    /**
     * Starts timer 2
     */
    public void startTimer2() {
        Client client = ClientBuilder.newClient();

        String exists = client.target(APPL_CONFIG_REST_SERVICE_URL + EXISTS_TIMER_2)
                .request(MediaType.APPLICATION_XML)
                .get(String.class);

        setFormData("Timer2", "UP");
        
        if (exists.equals("false")) {
            timerCallResult = client.target(APPL_CONFIG_REST_SERVICE_URL + getFormData())
            		.request(MediaType.APPLICATION_XML)
                    .put(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED));
        }
        else {
            timerCallResult = client.target(APPL_CONFIG_REST_SERVICE_URL + getFormData())
                    .request(MediaType.APPLICATION_XML)
                    .post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED));
        }
        
        client.close();
        
        LOG.debug(RESULT_FORMAT, timerCallResult);
    }
    
    /**
     * Stops timer 2
     */
    public void stopTimer2() {
        Client client = ClientBuilder.newClient();
        
        setFormData("Timer2", "DOWN");
        
        timerCallResult = client.target(APPL_CONFIG_REST_SERVICE_URL + getFormData())
        		.request(MediaType.APPLICATION_XML)
                .post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED));
        
        client.close();
        
        LOG.debug(RESULT_FORMAT, timerCallResult);
    }

    /**
     * Gets the ping result
     * 
     * @return the result
     */
    public String getPingResult() {
    	return pingResult;
    }
    
    /**
     * Gets the refresh result
     * 
     * @return the result
     */
    public String getRefreshResult() {
    	return refreshResult;
    }

    /**
     * Gets the timer call result
     * 
     * @return the result
     */
    public Response getTimerCallResult() {
    	return timerCallResult;
    }
    
    private void setFormData(String key, String value) {
    	formData.param(key, value);
    	this.key = key;
    	this.value = value;
    }
    
    private String getFormData() {
    	return "/Entry/" + key + "/" + value;
    }
}
