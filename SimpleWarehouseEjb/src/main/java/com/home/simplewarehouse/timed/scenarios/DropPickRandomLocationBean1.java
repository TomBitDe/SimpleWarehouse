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
		
		handlingUnitService.getAll().stream().forEach(h -> handlingUnitService.delete(h));

		locationService.getAll().stream().forEach(l -> locationService.delete(l));
		
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

		Location lA = locationService.getById("A");
		if (lA == null) {
		    lA = locationService.createOrUpdate(new RandomLocation("A"));
		}
		else {
			locationService.delete(lA);
		    lA = locationService.createOrUpdate(new RandomLocation("A"));
		}
		LOG.debug(lA);
		
        Location lD = locationService.getById("D");
        if (lD == null) {
		    lD = locationService.createOrUpdate(new RandomLocation("D"));
        }
        else {
			locationService.delete(lD);
		    lD = locationService.createOrUpdate(new RandomLocation("D"));
        }
		LOG.debug(lD);
		
        Location lC = locationService.getById("C");
        if (lC == null) {
		    lC = locationService.createOrUpdate(new RandomLocation("C"));
        }
        else {
			locationService.delete(lC);
		    lC = locationService.createOrUpdate(new RandomLocation("C"));
        }
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
