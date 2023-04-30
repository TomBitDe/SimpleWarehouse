package com.home.simplewarehouse.location;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.model.LocationStatus;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Bean class for LocationStatus usage. 
 */
@Stateless
@Local(LocationStatusLocal.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class LocationStatusBean implements LocationStatusLocal {
	private static final Logger LOG = LogManager.getLogger(LocationStatusBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Default constructor is mandatory
	 */
	public LocationStatusBean() {
		super();
		LOG.trace("--> LocationStatusBean()");
		LOG.trace("<-- LocationStatusBean()");
	}

	@Override
	public LocationStatus getById(final String id) {
		LOG.trace("--> getById({})", id);

		LocationStatus locationStatus = em.find(LocationStatus.class, id);

		LOG.trace("<-- getById");

		return locationStatus;
	}

	@Override
	public List<LocationStatus> getAll() {
		LOG.trace("--> getAll()");

		TypedQuery<LocationStatus> query = em.createQuery("SELECT ls FROM LocationStatus ls", LocationStatus.class);
		List<LocationStatus> locationStati = query.getResultList();

		LOG.trace("<-- getAll()");

		return locationStati;
	}
}
