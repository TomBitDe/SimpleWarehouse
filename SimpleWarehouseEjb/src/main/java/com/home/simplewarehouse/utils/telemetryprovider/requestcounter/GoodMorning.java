package com.home.simplewarehouse.utils.telemetryprovider.requestcounter;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * This is only a sample how to integrate the PerformanceAuditor.
 */
@Stateless
@Interceptors(PerformanceAuditor.class)
public class GoodMorning {
	private static final Logger LOG = LogManager.getLogger(GoodMorning.class);
	
	/**
	 * Create this GoodMorning
	 */
	public GoodMorning() {
		super();
    	LOG.trace("--> GoodMorning()");
    	LOG.trace("<-- GoodMorning()");
	}
	
	/**
	 * Method say should have a duration of 200
	 */
    public void say() {
    	LOG.info("--> say()");

    	try {
			Thread.sleep(200);
		}
    	catch (Exception ex)
    	{
    		LOG.fatal(ex.getMessage());
    		Thread.currentThread().interrupt();
		}

    	LOG.info("<-- say()");
    }

    /**
     * Method tooEarly throws an IllegalStateException
     */
    public void tooEarly() {
    	LOG.info("--> tooEarly()");

    	throw new IllegalStateException("Too early for good morning!");
    }
}
