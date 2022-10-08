package com.home.simplewarehouse.topology;

import java.util.ArrayList;
import java.util.List;

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
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Bean class for Sample Warehouse usage. 
 */
@Stateless
@Local(SampleWarehouseLocal.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class SampleWarehouseBean implements SampleWarehouseLocal {
	private static final Logger LOG = LogManager.getLogger(SampleWarehouseBean.class);
	
	@EJB
	LocationLocal locationLocal;
	
	@EJB
	HandlingUnitLocal handlingUnitLocal;
	
	public SampleWarehouseBean() {
		super();
		LOG.trace("--> SampleWarehouseBean()");
		LOG.trace("<-- SampleWarehouseBean()");
	}
	
	public void initialize() {
		LOG.trace("--> initialize()");
		
		List<Location> locationList = new ArrayList<>();
		locationList.clear();
		
		for (char c = 'A', num = 1; num <= LOCATION_NUM; ++c, ++num) {
			locationList.add(new Location(String.valueOf(c), SampleWarehouseBean.class.getSimpleName()));
		}
		locationList.forEach(l -> locationLocal.create(l));
		
		List<HandlingUnit> handlingUnitList = new ArrayList<>();
		handlingUnitList.clear();

		for (int val = 1; val <= HANDLING_UNIT_NUM; ++val) {
			handlingUnitList.add(new HandlingUnit(String.valueOf(val), SampleWarehouseBean.class.getSimpleName()));
		}
		handlingUnitList.forEach(h -> handlingUnitLocal.create(h));
		
		LOG.trace("<-- initialize()");
	}
	
	public void cleanup() {
		LOG.trace("--> cleanup()");
		
		locationLocal.getAll().forEach(l -> locationLocal.delete(l));
		handlingUnitLocal.getAll().forEach(h -> handlingUnitLocal.delete(h));
		
		LOG.trace("<-- cleanup()");
	}
}
