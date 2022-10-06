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

import com.home.simplewarehouse.model.LocationCharacteristics;
import com.home.simplewarehouse.model.LocationStatus;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Bean class for LocationCharacteristics usage. 
 */
@Stateless
@Local(LocationCharacteristicsLocal.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class LocationCharacteristicsBean implements LocationCharacteristicsLocal {
	private static final Logger LOG = LogManager.getLogger(LocationCharacteristicsBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Default constructor is mandatory
	 */
	public LocationCharacteristicsBean() {
		super();
		LOG.trace("--> LocationCharacteristicsBean()");
		LOG.trace("<-- LocationCharacteristicsBean()");
	}

	@Override
	public LocationCharacteristics getById(String id) {
		LOG.trace("--> getById(" + id + ')');

		LocationCharacteristics locationCharacteristics = em.find(LocationCharacteristics.class, id);

		LOG.trace("<-- getById");

		return locationCharacteristics;
	}

	@Override
	public List<LocationCharacteristics> getAll() {
		LOG.trace("--> getAll()");

		TypedQuery<LocationCharacteristics> query = em.createQuery("SELECT lc FROM LocationCharacteristics lc", LocationCharacteristics.class);
		List<LocationCharacteristics> locationCharacteristics = query.getResultList();

		LOG.trace("<-- getAll()");

		return locationCharacteristics;
	}
}
