package com.home.simplewarehouse.handlingunit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	private static final String HU_IS_HERE_FORMATTER = "HandlingUnit {} is here: {}";
	private static final String LOCATION_IS_NULL_MSG = "Location is null";
	private static final String HU_IS_NULL_MSG = "HandlingUnit is null";
	private static final String BASE_IS_NULL_MSG = "Base is null";
	
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
	public void delete(final HandlingUnit handlingUnit) {
		LOG.trace("--> delete({})", handlingUnit);

		if (handlingUnit != null && handlingUnit.getId() != null) {
			HandlingUnit hu;
			
			hu = getById(handlingUnit.getId());
			
			if (hu.getLocation() != null) {
			    hu.getLocation().removeHandlingUnit(hu);
			}
			
			free(hu);
			
			em.remove(hu);
			em.flush();

			LOG.debug("deleted: {}", hu);
		}
		else {
			LOG.debug("HandlingUnit == null or Id == null");
		}

		LOG.trace("<-- delete()");
	}

	@Override
	public HandlingUnit getById(final String id) {
		LOG.trace("--> getById({})", id);
		
		if (id == null) {
			throw new IllegalArgumentException();
		}

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
	public void pickFrom(final Location location, final HandlingUnit handlingUnit) throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.trace("--> pickFrom()");

		if (location == null) {
			throw new IllegalArgumentException(LOCATION_IS_NULL_MSG);
		}
		
		Location lo = locationLocal.getById(location.getLocationId());
		if (lo == null) {
			lo = locationLocal.create(location);
		}
		else {
			lo = em.merge(location);
		}
		
		if (handlingUnit == null) {
			throw new IllegalArgumentException(HU_IS_NULL_MSG);
		}
		
		HandlingUnit hu = getById(handlingUnit.getId());
		if (hu == null) {
			hu = create(handlingUnit);
		}
		else {
			hu = em.merge(handlingUnit);
		}
		
		if ((lo.getHandlingUnits()).isEmpty()) {
			// Check handlingUnit is already stored elsewhere
			List<Location> locations = locationLocal.getAllContainingExceptLocation(hu, lo);
			
			for (Location other : locations) {
				// HandlingUnit is already stored elsewhere
				LOG.warn(HU_IS_HERE_FORMATTER, hu.getId(), other);

				other.getLocationStatus().setErrorStatus(ErrorStatus.ERROR);
			}
			em.flush();
			
			// ATTENTION: Location error status does not need to be changed because the location was EMPTY!
			//            NO manual adjustment is needed in this case!
			throw new LocationIsEmptyException("Location [" + lo.getLocationId() + "] is EMPTY");
		}
		else {
			if (lo.getHandlingUnits().contains(hu)) {
				// Pick it now
				hu.setLocation(null);
				hu.setLocaPos(null);
				
				lo.removeHandlingUnit(hu);
				
				em.flush();
			}
			else {
				// Check handlingUnit is already stored elsewhere
				List<Location> locations = locationLocal.getAllContainingExceptLocation(hu, lo);
				
				for (Location other : locations) {					
					// HandlingUnit is already stored elsewhere
					LOG.warn(HU_IS_HERE_FORMATTER, hu.getId(), other);

					other.getLocationStatus().setErrorStatus(ErrorStatus.ERROR);
				}
				
				lo.getLocationStatus().setErrorStatus(ErrorStatus.ERROR);
				
				em.flush();
								
				throw new HandlingUnitNotOnLocationException("Handling unit not on Location [" + lo.getLocationId() + ']');
			}
		}

		LOG.trace("<-- pickFrom()");
	}

	@Override
	public HandlingUnit pickFrom(final Location location) throws LocationIsEmptyException {
		LOG.trace("--> pickFrom()");

		if (location == null) {
			throw new IllegalArgumentException(LOCATION_IS_NULL_MSG);
		}
		
		Location lo = locationLocal.getById(location.getLocationId());
		if (lo == null) {
			lo = locationLocal.create(location);
		}
		else {
			lo = em.merge(location);
		}
		
		List<HandlingUnit> picksAvailable = lo.getAvailablePicks();
		
		if (picksAvailable.isEmpty()) {
			// ATTENTION: Location error status does not need to be changed because the location was EMPTY!
			//            NO manual adjustment is needed in this case!
			throw new LocationIsEmptyException("Location [" + lo.getLocationId() + "] is EMPTY");
		}
		
		// Pick it now
		lo.removeHandlingUnit(picksAvailable.get(0));
		
		em.flush();
		
		LOG.trace("<-- pickFrom()");

		return getById(picksAvailable.get(0).getId());
	}

	@Override
	public void dropTo(final Location location, final HandlingUnit handlingUnit)
			throws CapacityExceededException, WeightExceededException, OverheightException
			, OverlengthException, OverwidthException
	{
		LOG.trace("--> dropTo()");

		if (handlingUnit == null) {
			LOG.warn("HandlingUnit is null; this is valid but nothing will happen!");
			return;
		}
				
		HandlingUnit hu = getById(handlingUnit.getId());
		if (hu == null) {
			hu = create(handlingUnit);
		}
		else {
			hu = em.merge(handlingUnit);
		}

		if (location == null) {
			throw new IllegalArgumentException(LOCATION_IS_NULL_MSG);
		}
		
		Location lo = locationLocal.getById(location.getLocationId());
		if (lo == null) {
			lo = locationLocal.create(location);
		}
		else {
			lo = em.merge(location);
		}
		
		locationLocal.checkDimensionLimitExceeds(lo, hu);
		
		List<Location> locations = locationLocal.getAllContainingExceptLocation(hu, lo);
		
		for (Location other : locations) {
			// HandlingUnit is already stored elsewhere
			try {
				LOG.warn(HU_IS_HERE_FORMATTER, hu.getId(), other);
				pickFrom(other, hu);
					
				other.getLocationStatus().setErrorStatus(ErrorStatus.ERROR);
			}
			catch(HandlingUnitNotOnLocationException | LocationIsEmptyException ex) {
				LOG.error("Correction PICK with error. Check {}", other);
			}				
		}

		hu.setLocation(lo);
		locationLocal.addHandlingUnit(lo, hu);
		
		LOG.trace("<-- dropTo()");

		em.flush();
	}

	@Override
	public HandlingUnit assign(final HandlingUnit handlingUnit, final HandlingUnit base) {
		LOG.trace("--> assign() hu={} base={}", handlingUnit, base);
		
		if (handlingUnit == null) {
			throw new IllegalArgumentException(HU_IS_NULL_MSG);
		}

		HandlingUnit hu = getById(handlingUnit.getId());
		if (hu == null) {
			hu = create(handlingUnit);
		}
		else {
			hu = em.merge(handlingUnit);
		}
		
		if (base == null) {
			throw new IllegalArgumentException(BASE_IS_NULL_MSG);
		}
		
		HandlingUnit ba = getById(base.getId());
		if (ba == null) {
			ba = create(base);
		}
		else {
			ba = em.merge(base);
		}
				
		hu.setBase(ba);
		boolean ret = ba.getContains().add(hu);
		LOG.info("assign result is: {}", ret);
		
		ba.setContains(ba.getContains());
		
		LOG.trace("<-- assign() {}", ba);
		
		em.flush();
		
		return ba;
	}

	@Override
	public HandlingUnit remove(final HandlingUnit handlingUnit, final HandlingUnit base) {
		LOG.trace("--> remove() hu={} base={}", handlingUnit, base);
		
		if (handlingUnit == null) {
			throw new IllegalArgumentException(HU_IS_NULL_MSG);
		}

		HandlingUnit hu = getById(handlingUnit.getId());
		if (hu == null) {
			hu = create(handlingUnit);
		}
		else {
			hu = em.merge(handlingUnit);
		}
		
		if (base == null) {
			throw new IllegalArgumentException(BASE_IS_NULL_MSG);
		}
		
		HandlingUnit ba = getById(base.getId());
		if (ba == null) {
			ba = create(base);
		}
		else {
			ba = em.merge(base);
		}
		
		hu.setBase(null);
		boolean ret = ba.getContains().remove(hu);
		LOG.info("remove result is: {}", ret);
		
		ba.setContains(ba.getContains());
		
		if (ba.getBase() != null) {
		    ba.getBase().getContains().remove(ba);
		}
		
		LOG.trace("<-- remove() {}", ba);
		
		em.flush();
		
		return ba;
	}
	
	@Override
	public HandlingUnit move(final HandlingUnit handlingUnit, final HandlingUnit destHandlingUnit) {
		LOG.trace("--> move() hu={} destHu={}", handlingUnit, destHandlingUnit);
		
		if (handlingUnit == null) {
			throw new IllegalArgumentException(HU_IS_NULL_MSG);
		}

		HandlingUnit hu = getById(handlingUnit.getId());
		if (hu == null) {
			hu = create(handlingUnit);
		}
		else {
			hu = em.merge(handlingUnit);
		}
		
		if (destHandlingUnit == null) {
			throw new IllegalArgumentException(HU_IS_NULL_MSG);
		}

		HandlingUnit dest = getById(destHandlingUnit.getId());
		if (dest == null) {
			dest = create(destHandlingUnit);
		}
		else {
			dest = em.merge(destHandlingUnit);
		}
		
		if (hu.getBase() != null) {
			LOG.info("Remove {} from base {}", hu.getId(), hu);
			HandlingUnit base = remove(hu, hu.getBase());
			LOG.info("Base now {}", base);
		}
		LOG.info("Assign {} to destination {}", hu.getId(), dest);
		dest = assign(getById(hu.getId()), dest);
		
		LOG.trace("<-- moved() hu={} destHu={}", hu, dest);
		
		em.flush();
		
		return getById(hu.getId());
	}

	@Override
	public Set<HandlingUnit> free(final HandlingUnit base) {
		LOG.trace("--> free() base={}", base);
		
		if (base == null) {
			throw new IllegalArgumentException(BASE_IS_NULL_MSG);
		}
		
		HandlingUnit ba = getById(base.getId());
		if (ba == null) {
			ba = create(base);
		}
		else {
			ba = em.merge(base);
		}
		
		Set<HandlingUnit> ret;			
		Set<HandlingUnit> huList = new HashSet<>(); 
		huList.addAll(ba.getContains());
		
		if (ba.getContains().removeAll(huList)) {
			ret = huList;
			for (HandlingUnit hu : ret) {
				hu.setBase(null);
			}
		}
		else {
			ret = new HashSet<>();			
		}
		
		ba.setContains(ba.getContains());
		
		if (ba.getBase() != null) {
		    ba.getBase().getContains().remove(ba);
		}
		
		LOG.info("free result is: {}", base);
		
		LOG.trace("<-- free() {}", ret);
		
		em.flush();
				
		return ret;
	}
}
