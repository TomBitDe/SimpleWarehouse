package com.home.simplewarehouse.timed.scenarios;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.handlingunit.HandlingUnitLocal;
import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.topology.SampleWarehouseLocal;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Drop many HandlingUnit on a single Location and then do many picks.
 */
@Stateless
@Local(DropPickRandomLocationLocal.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class DropPickRandomLocationBean implements DropPickRandomLocationLocal {
	private static final Logger LOG = LogManager.getLogger(DropPickRandomLocationBean.class);
	
	@EJB
	private SampleWarehouseLocal sampleWarehouseLocal;
	
	@EJB
	private LocationLocal locationLocal;

	@EJB
	private HandlingUnitLocal handlingUnitLocal;

	/**
	 * Default constructor
	 */
	public DropPickRandomLocationBean() {
		super();
		LOG.trace("--> DropPickRandomLocationBean()");
		LOG.trace("<-- DropPickRandomLocationBean()");
	}

	@Override
	public void processScenario() {
		LOG.trace("--> processScenario()");

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
		
		// Drop to location in random order
		handlingUnitLocal.dropTo(lA, h1);
		handlingUnitLocal.dropTo(lA, h4);
		handlingUnitLocal.dropTo(lA, h2);
		handlingUnitLocal.dropTo(lA, h3);
		
		lA = locationLocal.getById("A");
		LOG.info(lA);
		
		// Now pick from location randomly
		try {
			handlingUnitLocal.pickFrom(lA, h3);
			lA = locationLocal.getById("A");
			LOG.info(lA);

			handlingUnitLocal.pickFrom(lA, h2);
			lA = locationLocal.getById("A");
			LOG.info(lA);

			handlingUnitLocal.pickFrom(lA, h1);
			lA = locationLocal.getById("A");
			LOG.info(lA);

			handlingUnitLocal.pickFrom(lA, h4);
			lA = locationLocal.getById("A");
			LOG.info(lA);
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		}

		LOG.trace("<-- processScenario()");		
	}
}
