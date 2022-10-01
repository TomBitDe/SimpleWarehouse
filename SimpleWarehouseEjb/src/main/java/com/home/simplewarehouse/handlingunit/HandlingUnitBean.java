package com.home.simplewarehouse.handlingunit;

import java.util.List;

import javax.ejb.EJB;
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

import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.telemetryprovider.monitoring.PerformanceAuditor;

@Stateless
@Local(HandlingUnitLocal.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class HandlingUnitBean implements HandlingUnitLocal {
	private static final Logger LOG = LogManager.getLogger(HandlingUnitBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private LocationLocal locationLocal;

	public HandlingUnitBean() {
		super();
		LOG.trace("--> HandlingUnitBean()");
		LOG.trace("<-- HandlingUnitBean()");
	}

	@Override
	public void create(HandlingUnit handlingUnit) {
		LOG.trace("--> create");

		em.persist(handlingUnit);
		em.flush();

		LOG.trace("<-- create");
	}

	@Override
	public void delete(HandlingUnit handlingUnit) {
		LOG.trace("--> delete(" + handlingUnit + ')');

		if (handlingUnit != null && handlingUnit.getId() != null) {
			if (!em.contains(handlingUnit)) {
				handlingUnit = em.merge(handlingUnit);
			}
			
			if (handlingUnit.getLocation() != null) {
			    handlingUnit.getLocation().removeHandlingUnit(handlingUnit);
			}
			
			em.remove(handlingUnit);
			em.flush();

		}
		LOG.trace("<-- delete()");
	}

	@Override
	public HandlingUnit getById(String id) {
		LOG.trace("--> getById(" + id + ')');

		HandlingUnit handlingUnit = em.find(HandlingUnit.class, id);

		LOG.trace("<-- getById");

		return handlingUnit;
	}

	@Override
	public List<HandlingUnit> getAll() {
		LOG.trace("--> getAll()");

		TypedQuery<HandlingUnit> query = em.createQuery("SELECT hu FROM HandlingUnit hu", HandlingUnit.class);
		List<HandlingUnit> handlingUnit = query.getResultList();

		LOG.trace("<-- getAll()");

		return handlingUnit;
	}
}
