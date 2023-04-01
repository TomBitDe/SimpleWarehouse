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
import com.home.simplewarehouse.location.DimensionException;
import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.topology.SampleWarehouseLocal;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Drop many HandlingUnit on a single Location and then do many picks.
 */
@Stateless
@Local(DropPickRandomLocationLocal1.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class DropPickRandomLocationBean1 implements DropPickRandomLocationLocal1 {
	private static final Logger LOG = LogManager.getLogger(DropPickRandomLocationBean1.class);
	
	@EJB
	private SampleWarehouseLocal sampleWarehouseLocal;
	
	@EJB
	private LocationLocal locationLocal;

	@EJB
	private HandlingUnitLocal handlingUnitLocal;

	/**
	 * Default constructor
	 */
	public DropPickRandomLocationBean1() {
		super();
		LOG.trace("--> DropPickRandomLocationBean1()");
		LOG.trace("<-- DropPickRandomLocationBean1()");
	}

	private void cleanup() {
		LOG.trace("--> cleanup()");
		
		for (Location loc : locationLocal.getAll()) {
			locationLocal.delete(loc);
		}

		for (HandlingUnit hu : handlingUnitLocal.getAll()) {
			handlingUnitLocal.delete(hu);
		}

		LOG.trace("<-- cleanup()");		
	}

	@Override
	public void processScenario() {
		LOG.trace("--> processScenario()");
		
		cleanup();

		HandlingUnit h1 = handlingUnitLocal.create(new HandlingUnit("1"));
		HandlingUnit h2 = handlingUnitLocal.create(new HandlingUnit("2"));
		HandlingUnit h3 = handlingUnitLocal.create(new HandlingUnit("3"));
		HandlingUnit h4 = handlingUnitLocal.create(new HandlingUnit("4"));

		Location lA = locationLocal.create(new Location("A"));
		LOG.debug(lA);
		
		try {
			// Drop to location in random order
			handlingUnitLocal.dropTo(lA, h1);
			lA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(lA, h4);
			lA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(lA, h2);
			lA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(lA, h3);
		
			LOG.debug(locationLocal.getById("A"));
		}
		catch (DimensionException dimex) {
			LOG.error("Unexpected exception : {}", dimex.getMessage());
		}
		catch (Exception ex) {
			LOG.error("Unexpected exception : {}", ex.getMessage());
		}
		
		LOG.trace("<-- processScenario()");
	}
}
