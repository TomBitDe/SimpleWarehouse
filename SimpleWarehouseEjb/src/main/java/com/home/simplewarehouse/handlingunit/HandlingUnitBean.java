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

import com.home.simplewarehouse.location.CapacityExceededException;
import com.home.simplewarehouse.location.OverheightException;
import com.home.simplewarehouse.location.OverlengthException;
import com.home.simplewarehouse.location.OverwidthException;
import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.location.WeightExceededException;
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
	
	private static final String HU_IS_HERE_FORMATTER = "Handling unit {} is here: {}";
	private static final String LOCATION_IS_NULL_MSG = "Location is null";
	
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
	public HandlingUnit create(final HandlingUnit handlingUnit) {
		LOG.trace("--> create");

		em.persist(handlingUnit);
		em.flush();

		LOG.trace("<-- create");
		
		return getById(handlingUnit.getId());
	}

	@Override
	public void delete(HandlingUnit handlingUnit) {
		LOG.trace("--> delete({})", handlingUnit);

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
		LOG.trace("--> getById({})", id);

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
		LOG.trace("--> pickFrom()");

		if (location == null) {
			throw new IllegalArgumentException(LOCATION_IS_NULL_MSG);
		}
		
		if (handlingUnit == null) {
			throw new IllegalArgumentException("HandlingUnit is null");
		}
		
		if (!em.contains(location)) {
			location = em.merge(location);
		}
		
		if (!em.contains(handlingUnit)) {
			handlingUnit = em.merge(handlingUnit);
		}
		
		if ((location.getHandlingUnits()).isEmpty()) {
			// Check handlingUnit is already stored elsewhere
			List<Location> locations = locationLocal.getAllContainingExceptLocation(handlingUnit
					, location);
			
			for (Location other : locations) {
				// HandlingUnit is already stored elsewhere
				LOG.warn(HU_IS_HERE_FORMATTER, handlingUnit.getId(), other);

				other.getLocationStatus().setErrorStatus(ErrorStatus.ERROR);
			}
			em.flush();
			
			// ATTENTION: Location error status does not need to be changed because the location was EMPTY!
			//            NO manual adjustment is needed in this case!
			throw new LocationIsEmptyException("Location [" + location.getLocationId() + "] is EMPTY");
		}
		else {
			if (location.getHandlingUnits().contains(handlingUnit)) {
				// Pick it now
				handlingUnit.setLocation(null);
				handlingUnit.setLocaPos(null);
				
				location.removeHandlingUnit(handlingUnit);
				
				em.flush();
			}
			else {
				// Check handlingUnit is already stored elsewhere
				List<Location> locations = locationLocal.getAllContainingExceptLocation(handlingUnit
						, location);
				
				for (Location other : locations) {					
					// HandlingUnit is already stored elsewhere
					LOG.warn(HU_IS_HERE_FORMATTER, handlingUnit.getId(), other);

					other.getLocationStatus().setErrorStatus(ErrorStatus.ERROR);
				}
				
				location.getLocationStatus().setErrorStatus(ErrorStatus.ERROR);
				
				em.flush();
								
				throw new HandlingUnitNotOnLocationException("Handling unit not on Location [" + location.getLocationId() + ']');
			}
		}

		LOG.trace("<-- pickFrom()");
	}

	@Override
	public HandlingUnit pickFrom(Location location) throws LocationIsEmptyException {
		LOG.trace("--> pickFrom()");

		if (location == null) {
			throw new IllegalArgumentException(LOCATION_IS_NULL_MSG);
		}
		
		if (!em.contains(location)) {
			location = em.merge(location);
		}
		
		List<HandlingUnit> picksAvailable = location.getAvailablePicks();
		
		if (picksAvailable.isEmpty()) {
			// ATTENTION: Location error status does not need to be changed because the location was EMPTY!
			//            NO manual adjustment is needed in this case!
			throw new LocationIsEmptyException("Location [" + location.getLocationId() + "] is EMPTY");
		}
		
		// Pick it now
		location.removeHandlingUnit(picksAvailable.get(0));
		
		em.flush();
		
		LOG.trace("<-- pickFrom()");

		return getById(picksAvailable.get(0).getId());
	}

	@Override
	public void dropTo(Location location, HandlingUnit handlingUnit)
			throws CapacityExceededException, WeightExceededException, OverheightException
			, OverlengthException, OverwidthException
	{
		LOG.trace("--> dropTo()");

		if (handlingUnit == null) {
			LOG.warn("HandlingUnit is null; this is valid but nothing will happen!");
			return;
		}
		
		if (location == null) {
			throw new IllegalArgumentException(LOCATION_IS_NULL_MSG);
		}
		
		if (!em.contains(location)) {
			location = em.merge(location);
		}
		
		if (!em.contains(handlingUnit)) {
			handlingUnit = em.merge(handlingUnit);
		}
		
		locationLocal.checkDimensionLimitExceeds(location, handlingUnit);
		
		List<Location> locations = locationLocal.getAllContainingExceptLocation(handlingUnit, location);
		
		for (Location other : locations) {
			// HandlingUnit is already stored elsewhere
			try {
				LOG.warn(HU_IS_HERE_FORMATTER, handlingUnit.getId(), other);
				pickFrom(other, handlingUnit);
					
				other.getLocationStatus().setErrorStatus(ErrorStatus.ERROR);
			}
			catch(HandlingUnitNotOnLocationException | LocationIsEmptyException ex) {
				LOG.error("Correction PICK with error. Check {}", other);
			}				
		}

		handlingUnit.setLocation(location);
		locationLocal.addHandlingUnit(location, handlingUnit);
		
		LOG.trace("<-- dropTo()");

		em.flush();
	}
}
