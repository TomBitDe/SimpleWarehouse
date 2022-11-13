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
@Local(DropPickRandomLocationLocal2.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class DropPickRandomLocationBean2 implements DropPickRandomLocationLocal1 {
	private static final Logger LOG = LogManager.getLogger(DropPickRandomLocationBean2.class);
	
	private static final String PICK_FORMATTER = "Pick {}";
	
	@EJB
	private SampleWarehouseLocal sampleWarehouseLocal;
	
	@EJB
	private LocationLocal locationLocal;

	@EJB
	private HandlingUnitLocal handlingUnitLocal;

	/**
	 * Default constructor
	 */
	public DropPickRandomLocationBean2() {
		super();
		LOG.trace("--> DropPickRandomLocationBean1()");
		LOG.trace("<-- DropPickRandomLocationBean1()");
	}

	@Override
	public void processScenario() {
		LOG.trace("--> processScenario()");

		// Initialize only if no sample data exist
		if (handlingUnitLocal.getAll().isEmpty() && locationLocal.getAll().isEmpty()) {
			sampleWarehouseLocal.initialize();			
		}

		HandlingUnit h7 = handlingUnitLocal.getById("7");
		HandlingUnit h8 = handlingUnitLocal.getById("8");
		HandlingUnit h9 = handlingUnitLocal.getById("9");
		HandlingUnit h10 = handlingUnitLocal.getById("10");

		Location lA = locationLocal.getById("A");
		LOG.info(lA);
		
		// Drop to location in random order
		handlingUnitLocal.dropTo(lA, h10);
		handlingUnitLocal.dropTo(lA, h7);
		handlingUnitLocal.dropTo(lA, h8);
		handlingUnitLocal.dropTo(lA, h9);
		
		lA = locationLocal.getById("A");
		LOG.info(lA);
		
		// Now pick from location randomly
		try {
			LOG.info(PICK_FORMATTER, h7.getId());
			handlingUnitLocal.pickFrom(lA, h7);
			lA = locationLocal.getById("A");
			LOG.info(lA);

			LOG.info(PICK_FORMATTER, h10.getId());
			handlingUnitLocal.pickFrom(lA, h10);
			lA = locationLocal.getById("A");
			LOG.info(lA);

			LOG.info(PICK_FORMATTER, h8.getId());
			handlingUnitLocal.pickFrom(lA, h8);
			lA = locationLocal.getById("A");
			LOG.info(lA);

			LOG.info(PICK_FORMATTER, h9.getId());
			handlingUnitLocal.pickFrom(lA, h9);
			lA = locationLocal.getById("A");
			LOG.info(lA);
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		}

		LOG.trace("<-- processScenario()");		
	}
}
