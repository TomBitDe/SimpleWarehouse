package com.home.simplewarehouse.handlingunit;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.handlingunit.model.HandlingUnit;

@Stateless
@Local(HandlingUnitLocal.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class HandlingUnitBean implements HandlingUnitLocal {
	private static final Logger LOG = LogManager.getLogger(HandlingUnitBean.class);
	
	@PersistenceContext
	private EntityManager em;

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
	public HandlingUnit delete(String id) {
		LOG.trace("--> delete(" + id + ')');

		HandlingUnit handlingUnit = getById(id);

		if (handlingUnit != null) {
			em.remove(handlingUnit);
			em.flush();

			LOG.debug("deleted: " + handlingUnit);
		}
		else {
			LOG.debug("Id <" + id + "> not found");
		}
		LOG.trace("<-- delete(" + id + ')');

		return handlingUnit;
	}

	@Override
	public HandlingUnit delete(HandlingUnit handlingUnit) {
		LOG.trace("--> delete(" + handlingUnit + ')');

		if (handlingUnit != null && handlingUnit.getId() != null) {

			HandlingUnit oldHandlingUnit = delete(handlingUnit.getId());

			LOG.trace("<-- delete()");

			return oldHandlingUnit;
		}
		LOG.trace("<-- delete()");

		return null;
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
