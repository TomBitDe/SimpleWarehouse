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
public class DropPickRandomLocationBean2 implements DropPickRandomLocationLocal2 {
	private static final Logger LOG = LogManager.getLogger(DropPickRandomLocationBean2.class);
	
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
		LOG.trace("--> DropPickRandomLocationBean2()");
		LOG.trace("<-- DropPickRandomLocationBean2()");
	}

	@Override
	public void processScenario() {
		LOG.trace("--> processScenario()");

		// Initialize only if no sample data exist
		if (handlingUnitLocal.getAll().isEmpty() && locationLocal.getAll().isEmpty()) {
			sampleWarehouseLocal.initialize();
		}

		Location lA = locationLocal.getById("A");
		LOG.info(lA);

		// Now pick from location randomly
		try {
			handlingUnitLocal.pickFrom(lA, handlingUnitLocal.getById("4"));
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		}
		lA = locationLocal.getById("A");
		LOG.info(locationLocal.getById("A"));

		try {
			handlingUnitLocal.pickFrom(lA, handlingUnitLocal.getById("1"));
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		}
		lA = locationLocal.getById("A");
		LOG.info(lA);

		try {
			handlingUnitLocal.pickFrom(lA, handlingUnitLocal.getById("3"));
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		}
		lA = locationLocal.getById("A");
		LOG.info(lA);

		try {
			handlingUnitLocal.pickFrom(lA, handlingUnitLocal.getById("2"));
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		}
		lA = locationLocal.getById("A");
		LOG.info(lA);

		LOG.trace("<-- processScenario()");
	}
}
