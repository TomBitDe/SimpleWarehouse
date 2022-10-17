package com.home.simplewarehouse.timed;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.handlingunit.HandlingUnitLocal;
import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.topology.SampleWarehouseLocal;

/**
 * Implementation of a timer controlled bean
 * <p>
 * Call JPA session beans to test when running in the application server.
 */
@Singleton
public class TimerJpaSessionsBean {
	private static final Logger LOG = LogManager.getLogger(TimerJpaSessionsBean.class);

	private Date lastProgrammaticTimeout;
	private Date lastAutomaticTimeout;

	@Resource
	private TimerService timerService;

	@EJB
	private SampleWarehouseLocal sampleWarehouseLocal;
	
	@EJB
	private LocationLocal locationLocal;

	@EJB
	private HandlingUnitLocal handlingUnitLocal;

	/**
	 * Default constructor
	 */
	public TimerJpaSessionsBean() {
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

		// Initialize only if no sample data exist
		if (handlingUnitLocal.getAll().isEmpty() && locationLocal.getAll().isEmpty()) {
			sampleWarehouseLocal.initialize();			
		}

		HandlingUnit h1 = handlingUnitLocal.getById("1");
		HandlingUnit h2 = handlingUnitLocal.getById("2");
		HandlingUnit h3 = handlingUnitLocal.getById("3");
		HandlingUnit h4 = handlingUnitLocal.getById("4");

		Location lA = locationLocal.getById("A");
		LOG.info(lA);
		
		handlingUnitLocal.dropTo(lA, h1);
		handlingUnitLocal.dropTo(lA, h2);
		handlingUnitLocal.dropTo(lA, h3);
		handlingUnitLocal.dropTo(lA, h4);
		
		lA = locationLocal.getById("A");
		LOG.info(lA);
		
		try {
			handlingUnitLocal.pickFrom(lA, h1);
			lA = locationLocal.getById("A");
			LOG.info(lA);

			handlingUnitLocal.pickFrom(lA, h2);
			lA = locationLocal.getById("A");
			LOG.info(lA);

			handlingUnitLocal.pickFrom(lA, h3);
			lA = locationLocal.getById("A");
			LOG.info(lA);

			handlingUnitLocal.pickFrom(lA, h4);
			lA = locationLocal.getById("A");
			LOG.info(lA);
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		}
		
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
