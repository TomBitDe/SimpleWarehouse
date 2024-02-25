package com.home.simplewarehouse.timed;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.timed.scenarios.DropPickRandomLocationLocal3;
import com.home.simplewarehouse.utils.configurator.base.Configurator;

/**
 * Implementation of a timer controlled bean
 * <p>
 * Call JPA session beans to test when running in the application server.
 */
@Singleton
public class TimerJpaSessionsBean3 extends AbstractTimerSession {
	private static final Logger LOG = LogManager.getLogger(TimerJpaSessionsBean3.class);

	@EJB
	private Configurator configurator;

	@EJB
	private DropPickRandomLocationLocal3 dropPickRandomLocation3;
	
	/**
	 * Default constructor
	 */
	public TimerJpaSessionsBean3() {
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
	@Schedule(hour="*", minute="*", second="*/6", persistent = false)
	public void automaticTimeout() {
		LOG.trace("--> automaticTimeout()");

		this.setLastAutomaticTimeout(new Date());
		
		// Only when configuration for key Timer3 is UP
		if (configurator.getEntry("Timer3", "DOWN").equals("UP")) {
			// Add the session beans here
			dropPickRandomLocation3.processScenario();
		}
		
		LOG.trace("<-- automaticTimeout()");
	}
}
