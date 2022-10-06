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

import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

@Stateless
@Local(SampleWarehouseLocal.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class SampleWarehouseBean implements SampleWarehouseLocal {
	private static final Logger LOG = LogManager.getLogger(SampleWarehouseBean.class);
	
	@EJB
	LocationLocal locationLocal;
	
	List<Location> locationList = new ArrayList<>();

	public SampleWarehouseBean() {
		super();
		LOG.trace("--> SampleWarehouseBean()");
		LOG.trace("<-- SampleWarehouseBean()");
	}
	
	public void initialize() {
		LOG.trace("--> initialize()");
		
		for (char c = 'A'; c <= 'Z'; ++c) {
			locationList.add(new Location(String.valueOf(c), SampleWarehouseBean.class.getSimpleName()));
		}
		
		locationList.forEach(l -> locationLocal.create(l));
		
		LOG.trace("<-- initialize()");
	}
	
	public void cleanup() {
		LOG.trace("--> cleanup()");
		
		locationLocal.getAll().forEach(l -> locationLocal.delete(l));
		
		LOG.trace("<-- cleanup()");
	}
}
