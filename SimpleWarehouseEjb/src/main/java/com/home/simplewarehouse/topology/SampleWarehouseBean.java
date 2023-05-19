package com.home.simplewarehouse.topology;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.model.FifoLocation;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.LifoLocation;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Bean class for Sample Warehouse usage. 
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class SampleWarehouseBean implements SampleWarehouseService {
	private static final Logger LOG = LogManager.getLogger(SampleWarehouseBean.class);
	
	@EJB
	LocationService locationService;
	
	@EJB
	HandlingUnitService handlingUnitService;
	
	/**
	 * Create the Sample Warehouse Bean
	 */
	public SampleWarehouseBean() {
		super();
		LOG.trace("--> SampleWarehouseBean()");
		LOG.trace("<-- SampleWarehouseBean()");
	}
	
	/**
	 * Create sample Locations (Random, FiFo, LiFo) and HandlingUnits 
	 */
	public void initialize() {
		LOG.trace("--> initialize()");
		
		// Do a cleanup before
		cleanup();
		
		// Locations
		List<Location> locationList = new ArrayList<>();
		locationList.clear();
		
		// Random
		for (char c = 'A', num = 1; num <= LOCATION_NUM; ++c, ++num) {
			locationList.add(new Location("" + c, SampleWarehouseBean.class.getSimpleName()));
		}
		
		// FiFo
		for (char c = 'A', num = 1; num <= LOCATION_NUM; ++c, ++num) {
			locationList.add(new FifoLocation("FIFO_" + c, SampleWarehouseBean.class.getSimpleName()));
		}
		
		// LiFo
		for (char c = 'A', num = 1; num <= LOCATION_NUM; ++c, ++num) {
			locationList.add(new LifoLocation("LIFO_" + c, SampleWarehouseBean.class.getSimpleName()));
		}
		
		locationList.forEach(l -> locationService.createOrUpdate(l));
		
		// HandlingUnits
		List<HandlingUnit> handlingUnitList = new ArrayList<>();
		handlingUnitList.clear();

		for (int val = 1; val <= HANDLING_UNIT_NUM; ++val) {
			handlingUnitList.add(new HandlingUnit(String.valueOf(val), SampleWarehouseBean.class.getSimpleName()));
		}
		handlingUnitList.forEach(h -> handlingUnitService.createOrUpdate(h));
		
		LOG.trace("<-- initialize()");
	}
	
	/**
	 * Delete all Locations and HandlingUnits
	 */
	public void cleanup() {
		LOG.trace("--> cleanup()");
		
		locationService.getAll().forEach(l -> locationService.delete(l));
		handlingUnitService.getAll().forEach(h -> handlingUnitService.delete(h));
		
		LOG.trace("<-- cleanup()");
	}
}
