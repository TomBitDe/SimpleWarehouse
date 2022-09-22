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

import com.home.simplewarehouse.location.model.Location;
import com.home.simplewarehouse.telemetryprovider.monitoring.PerformanceAuditor;

@Stateless
@Local(LocationLocal.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class LocationBean implements LocationLocal {
	private static final Logger LOG = LogManager.getLogger(LocationBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	public LocationBean() {
		super();
		LOG.trace("--> LocationBean()");
		LOG.trace("<-- LocationBean()");
	}
	
	@Override
	public void create(Location location) {
		LOG.trace("--> create");

		em.persist(location);
		em.flush();

		LOG.trace("<-- create");
	}

	@Override
	public Location delete(String id) {
		LOG.trace("--> delete(" + id + ')');

		Location location = getById(id);

		if (location != null) {
			em.remove(location);
			em.flush();

			LOG.debug("deleted: " + location);
		}
		else {
			LOG.debug("Id <" + id + "> not found");
		}
		LOG.trace("<-- delete(" + id + ')');

		return location;
	}

	@Override
	public Location delete(Location location) {
		LOG.trace("--> delete(" + location + ')');

		if (location != null && location.getId() != null) {

			Location oldLocation = delete(location.getId());

			LOG.trace("<-- delete()");

			return oldLocation;
		}
		LOG.trace("<-- delete()");

		return null;
	}

	@Override
	public Location getById(String id) {
		LOG.trace("--> getById(" + id + ')');

		Location location = em.find(Location.class, id);

		LOG.trace("<-- getById");

		return location;
	}

	@Override
	public List<Location> getAll() {
		LOG.trace("--> getAll()");

		TypedQuery<Location> query = em.createQuery("SELECT l FROM Location l", Location.class);
		List<Location> location = query.getResultList();

		LOG.trace("<-- getAll()");

		return location;
	}
}
