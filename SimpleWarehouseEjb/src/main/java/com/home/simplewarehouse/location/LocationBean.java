package com.home.simplewarehouse.location;

import java.util.ArrayList;
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

import com.home.simplewarehouse.handlingunit.HandlingUnitLocal;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Bean class for Location usage. 
 */
@Stateless
@Local(LocationLocal.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class LocationBean implements LocationLocal {
	private static final Logger LOG = LogManager.getLogger(LocationBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private HandlingUnitLocal handlingUnitLocal;
	
	/**
	 * Default constructor is mandatory
	 */
	public LocationBean() {
		super();
		LOG.trace("--> LocationBean()");
		LOG.trace("<-- LocationBean()");
	}
	
	@Override
	public void create(final Location location) {
		LOG.trace("--> create");

		em.persist(location);
		em.flush();

		LOG.trace("<-- create");
	}

	@Override
	public void delete(Location location) {
		LOG.trace("--> delete(" + location + ')');

		if (location != null && location.getLocationId() != null) {
			if (!em.contains(location)) {
				location = em.merge(location);
			}
			
			for (HandlingUnit item : location.getHandlingUnits()) {
				item.setLocation(null);
				em.persist(item);
				em.flush();
			}

			em.remove(location);
			em.flush();

			LOG.debug("deleted: " + location);
		} 
		else {
			LOG.debug("Location == null or Id == null");
		}

		LOG.trace("<-- delete()");
	}

	@Override
	public Location getById(final String id) {
		LOG.trace("--> getById(" + id + ')');

		Location location = em.find(Location.class, id);

		LOG.trace("<-- getById");

		return location;
	}

	@Override
	public List<Location> getAll() {
		LOG.trace("--> getAll()");

		TypedQuery<Location> query = em.createQuery("SELECT l FROM Location l", Location.class);
		List<Location> locations = query.getResultList();

		LOG.trace("<-- getAll()");

		return locations;
	}
	
	@Override
	public List<Location> getAllContaining(final HandlingUnit handlingUnit) {
		LOG.trace("--> getAllContaining() " + handlingUnit);
		
		List<Location> ret = new ArrayList<>();

		List<Location> locations = getAll();
		
		for (Location item : locations) {
			if (item.getHandlingUnits().contains(handlingUnit)) {
				ret.add(item);
			}
		}

		LOG.trace("<-- getAllContaining()");

		return ret;
	}
}
