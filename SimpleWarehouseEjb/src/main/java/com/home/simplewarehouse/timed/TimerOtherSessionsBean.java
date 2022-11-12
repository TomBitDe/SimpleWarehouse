package com.home.simplewarehouse.timed;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of a timer controlled bean<br>
 * <p>
 * Call OTHER session beans to test when running in the application server.
 */
@Singleton
public class TimerOtherSessionsBean {
	private static final Logger LOG = LogManager.getLogger(TimerOtherSessionsBean.class);

	private Date lastProgrammaticTimeout;
	private Date lastAutomaticTimeout;

	@Resource
	private TimerService timerService;

	/**
	 * Default constructor
	 */
	public TimerOtherSessionsBean() {
		super();
	}

	/**
	 * Do a programmatic timeout
	 * 
	 * @param timer the time to use
	 */
	@Timeout
	public void programmaticTimeout(Timer timer) {
		this.setLastProgrammaticTimeout(new Date());
		LOG.info("Programmatic timeout occurred.");
	}

	/**
	 * Call all the session beans to run them in the application server periodically
	 */
	@Schedule(minute="*/1", hour="*")
	public void automaticTimeout() {
		LOG.trace("--> automaticTimeout()");

		this.setLastAutomaticTimeout(new Date());

		// Add the session beans here
		
		LOG.trace("<-- automaticTimeout()");
	}

	/**
	 * Gets the last programmatic timeout
	 * 
	 * @return the timeout
	 */
	public String getLastProgrammaticTimeout() {
		if (lastProgrammaticTimeout != null) {
			return lastProgrammaticTimeout.toString();
		}
		else {
			return "never";
		}

	}

	/**
	 * Sets the last programmatic timeout
	 * 
	 * @param lastTimeout the timeout value
	 */
	public void setLastProgrammaticTimeout(Date lastTimeout) {
		this.lastProgrammaticTimeout = lastTimeout;
	}

	/**
	 * Gets the last automatic timeout
	 *  
	 * @return the timeout
	 */
	public String getLastAutomaticTimeout() {
		if (lastAutomaticTimeout != null) {
			return lastAutomaticTimeout.toString();
		}
		else {
			return "never";
		}
	}

	/**
	 * Sets the last automatic timeout
	 * 
	 * @param lastAutomaticTimeout the timeout value
	 */
	public void setLastAutomaticTimeout(Date lastAutomaticTimeout) {
		this.lastAutomaticTimeout = lastAutomaticTimeout;
	}
}
