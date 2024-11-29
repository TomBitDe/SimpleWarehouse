package com.home.simplewarehouse.zone;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.model.Zone;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Bean class for Zone usage. 
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class ZoneBean implements ZoneService {
	private static final Logger LOG = LogManager.getLogger(ZoneBean.class);
	
	@PersistenceContext
	private EntityManager em;

	/**
	 * Default constructor is mandatory
	 */
	public ZoneBean() {
		super();
		LOG.trace("--> ZoneBean()");
		LOG.trace("<-- ZoneBean()");
	}

	@Override
	public Zone createOrUpdate(Zone zone) {
		LOG.trace("--> create");
		
		if (zone == null || zone.getId() == null || zone.getId().trim().isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		if (getById(zone.getId()) == null) {
			em.persist(zone);
		}
		else {
			em.merge(zone);
		}
		em.flush();

		LOG.trace("<-- create");
		
		return getById(zone.getId());
	}

	@Override
	public void delete(Zone zone) {
		LOG.trace("--> delete({})", zone);

		if (zone != null && zone.getId() != null) {
			Zone zo;
			
			zo = getById(zone.getId());
			
			em.remove(zo);
			em.flush();

			LOG.debug("deleted: {}", zo);
		}
		else {
			LOG.debug("Zone == null or id == null");
		}

		LOG.trace("<-- delete()");
	}

	@Override
	public void delete(String id) {
		LOG.trace("--> delete({})", id);

		Zone zo = getById(id);
		
		if (zo != null) {
			delete(zo);
		} 
		else {
			LOG.debug("Zone == null");
		}

		LOG.trace("<-- delete()");
	}

	@Override
	public Zone getById(String id) {
		LOG.trace("--> getById({})", id);
		
		if (id == null) {
			throw new IllegalArgumentException();
		}

		Zone zone = em.find(Zone.class, id);

		LOG.trace("<-- getById");

		return zone;
	}

	@Override
	public List<Zone> getAll() {
		LOG.trace("--> getAll()");

		TypedQuery<Zone> query = em.createQuery("SELECT zo FROM Zone zo", Zone.class);
		List<Zone> zone = query.getResultList();

		LOG.trace("<-- getAll()");

		return zone;
	}

	@Override
	public List<Zone> getAll(int offset, int count) {
		LOG.trace("--> getAll({}, {})", offset, count);

        if (offset < 0) {
            throw new IllegalArgumentException("offset < 0");
        }
        if (count < 1) {
            throw new IllegalArgumentException("count < 1");
        }

        TypedQuery<Zone> query = em.createQuery("SELECT zo FROM Zone zo", Zone.class);
        query.setFirstResult(offset);
        query.setMaxResults(count);

        List<Zone> zones = query.getResultList();

		LOG.trace("<-- getAll({}, {})", offset, count);

		return zones;
	}

	@Override
	public int count() {
		final TypedQuery<Number> query = em.createQuery("SELECT COUNT(zo) FROM Zone zo", Number.class);

		return query.getSingleResult().intValue();
	}
}
