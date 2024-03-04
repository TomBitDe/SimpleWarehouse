package com.home.simplewarehouse.beans;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.patterns.singleton.simplecache.model.ApplConfig;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity.ExceptionStatistics;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity.Invocation;
import com.home.simplewarehouse.views.KeyValueSourceEntry;

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
    private static final String EXISTS_TIMER_3 = "/Exists/Timer3";
    private static final String ENTRY_TIMER_1 = "/Entry/Timer1";
    private static final String ENTRY_TIMER_2 = "/Entry/Timer2";
    private static final String ENTRY_TIMER_3 = "/Entry/Timer3";
    private static final String RESULT_FORMAT = "Result is [{}]";
    
    private static final String MONITOR_REST_SERVICE_URL = "http://localhost:8080/war/resources/monitoring";
    
    private static final int DEFAULT_MAX_ROWS = 50;
    
    /**
     * Maximum rows for getting SlowestMethods
     */
    private String maxResults = String.valueOf(DEFAULT_MAX_ROWS);

    private transient Form formData = new Form();
    
    /**
     * Gets the maximum results value
     * 
     * @return the maximum results
     */
    public String getMaxResults() {
		return maxResults;
	}

    /**
     * Sets the maximum results value
     * 
     * @param maxResults the maximum value
     */
	public void setMaxResults(String maxResults) {
		this.maxResults = maxResults;
	}

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
     * Gets the current configured ApplConfig items
     * 
     * @return the items
     */
    public List<ApplConfig> getApplConfigItems() {
        Client client = ClientBuilder.newClient();
        
        List<ApplConfig> applConfigItems = client.target(APPL_CONFIG_REST_SERVICE_URL + "/Content")
				.request(MediaType.APPLICATION_XML)
				.get(new GenericType<List<ApplConfig>>() {});            
        
        client.close();

        LOG.debug(RESULT_FORMAT, applConfigItems);
        
        return applConfigItems;
    }
    
    /**
     * Gets the current configurations
     * 
     * @return the items
     */
    public List<KeyValueSourceEntry> getConfigurations() {
        Client client = ClientBuilder.newClient();
        
        List<KeyValueSourceEntry> configuratorItems = client.target(APPL_CONFIG_REST_SERVICE_URL + "/Configuration")
				.request(MediaType.APPLICATION_XML)
				.get(new GenericType<List<KeyValueSourceEntry>>() {});            
        
        client.close();

        LOG.debug(RESULT_FORMAT, configuratorItems);
        
        return configuratorItems;
    }
    
    /**
     * Gets the current configured ApplConfig items as text
     * 
     * @return the items
     */
    public String getApplConfigItemsAsText() {
        Client client = ClientBuilder.newClient();
        
        String applConfigItems = client.target(APPL_CONFIG_REST_SERVICE_URL + "/TextContent")
				.request(MediaType.TEXT_PLAIN)
				.get(String.class);            
        
        client.close();

        LOG.debug(RESULT_FORMAT, applConfigItems);
        
        return applConfigItems;
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

    /**
     * Starts timer 3
     */
    public void startTimer3() {
    	startTimer(EXISTS_TIMER_3, "Timer3");
    }
    
    /**
     * Stops timer 3
     */
    public void stopTimer3() {
    	stopTimer("Timer3");
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
    	
    	val = getTimerStatus(EXISTS_TIMER_1, ENTRY_TIMER_1);
    	
    	return val;
    }
    
    /**
     * Gets the status of Timer2
     * 
     * @return the status
     */
    public String getTimer2Status() {
    	String val;
    	
    	val = getTimerStatus(EXISTS_TIMER_2, ENTRY_TIMER_2);
    	
    	return val;
    }
    
    /**
     * Gets the status of Timer3
     * 
     * @return the status
     */
    public String getTimer3Status() {
    	String val;
    	
    	val = getTimerStatus(EXISTS_TIMER_3, ENTRY_TIMER_3);
    	
    	return val;
    }
    
    /**
     * Clear all monitor entries
     */
    public void clearMonitor() {
        Client client = ClientBuilder.newClient();
        
        client.target(MONITOR_REST_SERVICE_URL + "/clear")
				.request(MediaType.TEXT_PLAIN)
				.delete();            
        
        client.close();
    }
    
    /**
     * Gets the number of exceptions
     * 
     * @return the number of exceptions
     */
    public String getNumberOfExceptions() {
        Client client = ClientBuilder.newClient();
        
        String count = client.target(MONITOR_REST_SERVICE_URL + "/exceptionCount")
                .request(MediaType.TEXT_PLAIN)
                .get(String.class);

        client.close();
        
        LOG.debug(RESULT_FORMAT, count);
        
        return count;
    }
    
    /**
     * Gets the exception statistics list
     * 
     * @return the exception statistics
     */
    public List<ExceptionStatistics> getExceptionStatistics() {
        Client client = ClientBuilder.newClient();
        
        List<ExceptionStatistics> exceptionItems = client.target(MONITOR_REST_SERVICE_URL + "/exceptionList")
				.request(MediaType.APPLICATION_XML)
				.get(new GenericType<List<ExceptionStatistics>>() {});            
        
        client.close();

        LOG.debug(RESULT_FORMAT, exceptionItems);
    	
    	return exceptionItems;
    }
    
    /**
     * Gets the 50 slowest methods
     * 
     * @return the items
     */
    public List<Invocation> getSlowestMethods() {
        Client client = ClientBuilder.newClient();
        
        int maxRows;
        
        try {
        	maxRows = Integer.parseInt(getMaxResults());
			if (maxRows <= 0) {
				maxRows = DEFAULT_MAX_ROWS;
			}
        }
        catch (NumberFormatException nfex) {
        	maxRows = DEFAULT_MAX_ROWS;
        }
		setMaxResults(String.valueOf(maxRows));

        String endpoint = MONITOR_REST_SERVICE_URL + "/slowestMethods";
        endpoint += "/" + maxRows;
        
        List<Invocation> items = client.target(endpoint)
				.request(MediaType.APPLICATION_XML)
				.get(new GenericType<List<Invocation>>() {});            
        
        client.close();

        LOG.debug(RESULT_FORMAT, items);
        
        return items;
    }
    
    private void setFormData(String key, String value) {
    	formData.param(key, value);
    	this.key = key;
    	this.value = value;
    }
    
    private String getFormData() {
    	return "/Entry/" + key + "/" + value;
    }
    
    private String getTimerStatus(final String existsTimer, final String entryTimer) {
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
}
