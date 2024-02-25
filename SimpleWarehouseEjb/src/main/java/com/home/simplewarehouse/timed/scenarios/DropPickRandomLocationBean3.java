package com.home.simplewarehouse.timed.scenarios;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.location.CapacityExceededException;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.location.OverheightException;
import com.home.simplewarehouse.location.OverlengthException;
import com.home.simplewarehouse.location.OverwidthException;
import com.home.simplewarehouse.location.WeightExceededException;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.topology.SampleWarehouseService;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Drop many HandlingUnit on a single Location and then do many picks.
 */
@Stateless
@Local(DropPickRandomLocationLocal3.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class DropPickRandomLocationBean3 implements DropPickRandomLocationLocal3 {
	private static final Logger LOG = LogManager.getLogger(DropPickRandomLocationBean3.class);
	
	@EJB
	private SampleWarehouseService sampleWarehouseService;
	
	@EJB
	private LocationService locationService;

	@EJB
	private HandlingUnitService handlingUnitService;

	/**
	 * Default constructor
	 */
	public DropPickRandomLocationBean3() {
		super();
		LOG.trace("--> DropPickRandomLocationBean3()");
		LOG.trace("<-- DropPickRandomLocationBean3()");
	}

	@Override
	public void processScenario() {
		LOG.trace("--> processScenario()");

		Location lB = locationService.getById("B");
		LOG.info("Location lB={}", lB);
		
		if (lB != null) {
			List<String> huIds = Arrays.asList("5", "6", "7", "8", "9", "10", "11", "12");
			
			for (String huId : huIds) {
				if (handlingUnitService.getById(huId) == null) {
			        handlingUnitService.createOrUpdate(new HandlingUnit(huId));
				}
			}
			
			handlingUnitService.assign("6", "5");
			handlingUnitService.assign("7", "5");
			handlingUnitService.assign("8", "5");
			handlingUnitService.assign("9", "7");
			handlingUnitService.assign("10", "7");
			handlingUnitService.assign("11", "8");
			handlingUnitService.assign("12", "9");
			
			try {
				handlingUnitService.dropTo(locationService.getById("B"), handlingUnitService.getById("5"));
			}
			catch (CapacityExceededException | WeightExceededException | OverheightException | OverlengthException
					| OverwidthException e) {
				LOG.error(e.getMessage());
			}
			
			// Now pick from location
			for (String huId : huIds) {
			    try {
				    handlingUnitService.pickFrom(locationService.getById("B"), handlingUnitService.getById(huId));

				    lB = locationService.getById("B");
				    LOG.info(lB);
			    }
			    catch (Exception e) {
				    LOG.error(e.getMessage());
			    }
			}
		}

		LOG.trace("<-- processScenario()");
	}
}
