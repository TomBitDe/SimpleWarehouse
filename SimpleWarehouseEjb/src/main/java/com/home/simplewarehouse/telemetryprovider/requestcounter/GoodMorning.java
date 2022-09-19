package com.home.simplewarehouse.telemetryprovider.requestcounter;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * This is only a sample how to integrate the PerformanceAuditor.
 */
@Stateless
@Interceptors(PerformanceAuditor.class)
public class GoodMorning {
	private static final Logger LOG = LogManager.getLogger(GoodMorning.class);
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
