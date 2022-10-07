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

import com.home.simplewarehouse.model.Dimension;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Bean class for Dimension usage. 
 */
@Stateless
@Local(DimensionLocal.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class DimensionBean implements DimensionLocal {
	private static final Logger LOG = LogManager.getLogger(DimensionBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Default constructor is mandatory
	 */
	public DimensionBean() {
		super();
		LOG.trace("--> DimensionBean()");
		LOG.trace("<-- DimensionBean()");
	}

	@Override
	public Dimension getById(String id) {
		LOG.trace("--> getById({})", id);

		Dimension dimension = em.find(Dimension.class, id);

		LOG.trace("<-- getById");

		return dimension;
	}

	@Override
	public List<Dimension> getAll() {
		LOG.trace("--> getAll()");

		TypedQuery<Dimension> query = em.createQuery("SELECT dim FROM Dimension dim", Dimension.class);
		List<Dimension> dimension = query.getResultList();

		LOG.trace("<-- getAll()");

		return dimension;
	}
}
