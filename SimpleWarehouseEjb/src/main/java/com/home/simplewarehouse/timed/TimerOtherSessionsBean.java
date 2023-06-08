package com.home.simplewarehouse.timed;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.utils.configurator.base.Configurator;

/**
 * Implementation of a timer controlled bean<br>
 * <p>
 * Call OTHER session beans to test when running in the application server.
 */
@Singleton
public class TimerOtherSessionsBean extends AbstractTimerSession {
	private static final Logger LOG = LogManager.getLogger(TimerOtherSessionsBean.class);

	@EJB
	private Configurator configurator;
	
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
	@Override
	@Timeout
	public void programmaticTimeout(Timer timer) {
		this.setLastProgrammaticTimeout(new Date());
		LOG.trace("Programmatic timeout occurred.");
	}

	/**
	 * Call all the session beans to run them in the application server periodically
	 */
	@Override
	@Schedule(minute="*/1", hour="*", persistent = false)
	public void automaticTimeout() {
		LOG.trace("--> automaticTimeout()");

		this.setLastAutomaticTimeout(new Date());

		// Only when configuration for key Timer2 is UP
		if (configurator.getEntry("Other", "DOWN").equals("UP")) {
			// Add the session beans here
			
		}
		
		LOG.trace("<-- automaticTimeout()");
	}
}
