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

import com.home.simplewarehouse.patterns.singleton.simplecache.model.ApplConfig;

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
    private static final String ENTRY_TIMER_1 = "/Entry/Timer1";
    private static final String ENTRY_TIMER_2 = "/Entry/Timer2";
    private static final String RESULT_FORMAT = "Result is [{}]";
    
    private transient Form formData = new Form();
    
    /**
     * The ping action result value
     */
    private String pingResult;
    /**
     * The refresh action result value
     */
    private String refreshResult;
    /**
     * The timer call result value
     */
    private transient Response timerCallResult;

    /**
     * The access key content
     */
    private String key;
    /**
     * The access keys value content
     */
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
    	startTimer(EXISTS_TIMER_1, "Timer1");
    }
    
    /**
     * Stops timer 1
     */
    public void stopTimer1() {
    	stopTimer("Timer1");
    }
    
    /**
     * Starts timer 2
     */
    public void startTimer2() {
    	startTimer(EXISTS_TIMER_2, "Timer2");
    }
    
    /**
     * Stops timer 2
     */
    public void stopTimer2() {
    	stopTimer("Timer2");
    }

    private void startTimer(final String existsTimer, final String timerName) {
        Client client = ClientBuilder.newClient();
        
        String exists = client.target(APPL_CONFIG_REST_SERVICE_URL + existsTimer)
                .request(MediaType.APPLICATION_XML)
                .get(String.class);

        setFormData(timerName, "UP");
        
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
    
    private void stopTimer(final String timeName) {
        Client client = ClientBuilder.newClient();
        
        setFormData(timeName, "DOWN");
        
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
    
    /**
     * Gets the status of Timer1
     * 
     * @return the status
     */
    public String getTimer1Status() {
    	String val;
    	
    	val = getTimerStatus(EXISTS_TIMER_1, ENTRY_TIMER_1, "Timer1");
    	
    	return val;
    }
    
    /**
     * Gets the status of Timer2
     * 
     * @return the status
     */
    public String getTimer2Status() {
    	String val;
    	
    	val = getTimerStatus(EXISTS_TIMER_2, ENTRY_TIMER_2, "Timer2");
    	
    	return val;
    }
    
    private void setFormData(String key, String value) {
    	formData.param(key, value);
    	this.key = key;
    	this.value = value;
    }
    
    private String getFormData() {
    	return "/Entry/" + key + "/" + value;
    }
    
    private String getTimerStatus(final String existsTimer, final String entryTimer, final String timerName) {
    	String status;
    	
        Client client = ClientBuilder.newClient();
        
        String exists = client.target(APPL_CONFIG_REST_SERVICE_URL + existsTimer)
                .request(MediaType.APPLICATION_XML)
                .get(String.class);

        if (exists.equals("false")) {
        	status = "DOWN";
        }
        else {
            ApplConfig ret = client.target(APPL_CONFIG_REST_SERVICE_URL + entryTimer)
                    .request(MediaType.APPLICATION_XML)
                    .get(ApplConfig.class);
            
            status = ret.getParamVal();
        }
        
        client.close();
        
        LOG.debug(RESULT_FORMAT, status);
        
        return status;
    }
}
