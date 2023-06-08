package com.home.simplewarehouse.timed;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstraction for timer controlled session beans.
 */
public abstract class AbstractTimerSession {
	private static final Logger LOG = LogManager.getLogger(AbstractTimerSession.class);

	private Date lastProgrammaticTimeout;
	private Date lastAutomaticTimeout;
	
	@Resource
	private TimerService timerService;
	
	/**
	 * Default constructor
	 */
	protected AbstractTimerSession() {
		super();
	}
	
	/**
	 * Do a programmatic timeout
	 * 
	 * @param timer the time to use
	 */
	abstract void programmaticTimeout(Timer timer);
	
	/**
	 * Implement what has do be done in the application server periodically
	 */
	abstract void automaticTimeout();
	
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
		LOG.trace("setLastProgrammaticTimeout {}", lastTimeout);
		
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
		LOG.trace("setLastAutomaticTimeout {}", lastAutomaticTimeout);

		this.lastAutomaticTimeout = lastAutomaticTimeout;
	}
}
