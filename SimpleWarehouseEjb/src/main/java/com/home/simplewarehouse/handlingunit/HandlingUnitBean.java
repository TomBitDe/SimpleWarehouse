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
import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Bean class for HandlingUnit usage. 
 */
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

	/**
	 * Default constructor is mandatory
	 */
	public HandlingUnitBean() {
		super();
		LOG.trace("--> HandlingUnitBean()");
		LOG.trace("<-- HandlingUnitBean()");
	}

	@Override
	public void create(final HandlingUnit handlingUnit) {
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
	public HandlingUnit getById(final String id) {
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
	
	@Override
	public void pickFrom(Location location, HandlingUnit handlingUnit) throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		if (!em.contains(location)) {
			location = em.merge(location);
		}
		
		if (!em.contains(handlingUnit)) {
			handlingUnit = em.merge(handlingUnit);
		}
		
		if (location == null) {
			throw new IllegalArgumentException("Location is null");
		}
		if (location.getHandlingUnits() == null) {
			throw new IllegalStateException("Location has illegal state (HandlingUnits is null)");
		}
		if (location.getHandlingUnits().isEmpty()) {
			// ATTENTION: Location error status does not need to be changed because the location was EMPTY!
			//            NO manual adjustment is needed in this case!
			throw new LocationIsEmptyException("Location [" + location.getLocationId() + "] is EMPTY");
		}
		
		if (! location.removeHandlingUnit(handlingUnit)) {
			// CAUTION: Set the location error status to ERROR
			//          Manual adjustment is needed!
			location.getLocationStatus().setErrorStatus(ErrorStatus.ERROR);
			
			throw new HandlingUnitNotOnLocationException("Handling unit not on Location [" + location.getLocationId() + ']');
		}
		
		handlingUnit.setLocation(null);
		
		em.flush();
	}

	@Override
	public void dropTo(Location location, HandlingUnit handlingUnit) {
		if (!em.contains(location)) {
			location = em.merge(location);
		}
		
		if (!em.contains(handlingUnit)) {
			handlingUnit = em.merge(handlingUnit);
		}
		
		if (location == null) {
			throw new IllegalArgumentException("Location is null");
		}
		else {
			List<Location> locations = locationLocal.getAllContaining(handlingUnit);
			
			for (Location item : locations) {
				// HandlingUnit is already stored elsewhere
				try {
					LOG.warn("Handling unit " + handlingUnit.getId() + " is here: " + item);

					pickFrom(item, handlingUnit);
					
					item.getLocationStatus().setErrorStatus(ErrorStatus.ERROR);
				}
				catch(HandlingUnitNotOnLocationException | LocationIsEmptyException ex) {
					LOG.warn("Correction PICK with error. Check " + item);
				}				
			}

			handlingUnit.setLocation(location);
			location.addHandlingUnit(handlingUnit);
		}
		em.flush();
	}
}
