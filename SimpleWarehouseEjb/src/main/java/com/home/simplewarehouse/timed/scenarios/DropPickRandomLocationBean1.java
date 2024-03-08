package com.home.simplewarehouse.timed.scenarios;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.location.DimensionException;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.RandomLocation;
import com.home.simplewarehouse.topology.SampleWarehouseService;
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
	private SampleWarehouseService sampleWarehouseService;
	
	@EJB
	private LocationService locationService;

	@EJB
	private HandlingUnitService handlingUnitService;

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
		
		for (Location loc : locationService.getAll()) {
			locationService.delete(loc);
		}

		for (HandlingUnit hu : handlingUnitService.getAll()) {
			handlingUnitService.delete(hu);
		}

		LOG.trace("<-- cleanup()");		
	}

	@Override
	public void processScenario() {
		LOG.trace("--> processScenario()");
		
		cleanup();

		HandlingUnit h1 = handlingUnitService.createOrUpdate(new HandlingUnit("1"));
		HandlingUnit h2 = handlingUnitService.createOrUpdate(new HandlingUnit("2"));
		HandlingUnit h3 = handlingUnitService.createOrUpdate(new HandlingUnit("3"));
		HandlingUnit h4 = handlingUnitService.createOrUpdate(new HandlingUnit("4"));

		Location lA = locationService.createOrUpdate(new RandomLocation("A"));
		LOG.debug(lA);
		
		Location lB = locationService.createOrUpdate(new RandomLocation("B"));
		LOG.debug(lB);
		
		Location lC = locationService.createOrUpdate(new RandomLocation("C"));
		LOG.debug(lC);
		
		try {
			// Drop to location in random order
			handlingUnitService.dropTo(lA, h1);
			lA = locationService.getById("A");
			handlingUnitService.dropTo(lA, h4);
			lA = locationService.getById("A");
			handlingUnitService.dropTo(lA, h2);
			lA = locationService.getById("A");
			handlingUnitService.dropTo(lA, h3);
		
			LOG.debug(locationService.getById("A"));
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
