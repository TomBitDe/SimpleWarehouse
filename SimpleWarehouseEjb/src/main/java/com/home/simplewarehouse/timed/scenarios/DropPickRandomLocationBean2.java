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
import com.home.simplewarehouse.model.Location;
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
	private LocationService locationService;

	@EJB
	private HandlingUnitService handlingUnitService;

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

		Location lA = locationService.getById("A");
		LOG.info("Location lA={}", lA);
		
		if (lA != null) {
			List<String> huIds = Arrays.asList("4", "1", "3", "2");
			
			try {
				handlingUnitService.dropTo(locationService.getById("A"), handlingUnitService.getById("1"));
			}
			catch (CapacityExceededException | WeightExceededException | OverheightException | OverlengthException
					| OverwidthException e) {
				LOG.error(e.getMessage());
			}
			
			// Now pick from location
			for (String huId : huIds) {
			    try {
				    handlingUnitService.pickFrom(locationService.getById("A"), handlingUnitService.getById(huId));

				    lA = locationService.getById("A");
				    LOG.info(lA);
			    }
			    catch (Exception e) {
				    LOG.error(e.getMessage());
			    }
			}
		}

		LOG.trace("<-- processScenario()");
	}
}
