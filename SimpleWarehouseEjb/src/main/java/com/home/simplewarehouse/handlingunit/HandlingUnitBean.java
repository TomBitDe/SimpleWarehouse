package com.home.simplewarehouse.handlingunit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
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
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.location.OverheightException;
import com.home.simplewarehouse.location.OverlengthException;
import com.home.simplewarehouse.location.OverwidthException;
import com.home.simplewarehouse.location.WeightExceededException;
import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Bean class for HandlingUnit usage. 
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class HandlingUnitBean implements HandlingUnitService {
	private static final Logger LOG = LogManager.getLogger(HandlingUnitBean.class);
	
	private static final String HU_IS_HERE_FORMATTER = "HandlingUnit {} is here: {}";
	private static final String LOCATION_IS_NULL_MSG = "Location is null";
	private static final String HU_IS_NULL_MSG = "HandlingUnit is null";
	private static final String BASE_IS_NULL_MSG = "Base is null";
	
	private static final String END_PICK_FROM = "<-- pickFrom()";
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private LocationService locationService;
	
	/**
	 * Default constructor is mandatory
	 */
	public HandlingUnitBean() {
		super();
		LOG.trace("--> HandlingUnitBean()");
		LOG.trace("<-- HandlingUnitBean()");
	}

	@Override
	public HandlingUnit createOrUpdate(final HandlingUnit handlingUnit) {
		LOG.trace("--> create");
		
		if (getById(handlingUnit.getId()) == null) {
			em.persist(handlingUnit);
		}
		else {
			em.merge(handlingUnit);
		}
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
	public void delete(final String id) {
		LOG.trace("--> delete({})", id);

		HandlingUnit handlingUnit = getById(id);
		
		if (handlingUnit != null) {
			delete(handlingUnit);
		} 
		else {
			LOG.debug("HandlingUnit == null");
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
	public List<HandlingUnit> getAll(int offset, int count) {
		LOG.trace("--> getAll({}, {})", offset, count);

        if (offset < 0) {
            throw new IllegalArgumentException("offset < 0");
        }
        if (count < 1) {
            throw new IllegalArgumentException("count < 1");
        }

        TypedQuery<HandlingUnit> query = em.createQuery("SELECT h FROM HandlingUnit h", HandlingUnit.class);
        query.setFirstResult(offset);
        query.setMaxResults(count);

        List<HandlingUnit> locations = query.getResultList();

		LOG.trace("<-- getAll({}, {})", offset, count);

		return locations;
	}
	
	@Override
	public void pickFrom(final Location location, final HandlingUnit handlingUnit) throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.trace("--> pickFrom({}, {})", location, handlingUnit);

		checkIllegalArgument(location, handlingUnit);
		
		Location lo = locationService.getById(location.getLocationId());
		if (lo == null) {
			lo = locationService.createOrUpdate(location);
		}
		else {
			lo = em.merge(location);
		}
		
		HandlingUnit hu = getById(handlingUnit.getId());
		if (hu == null) {
			hu = createOrUpdate(handlingUnit);
		}
		else {
			hu = em.merge(handlingUnit);
		}
		
		if ((lo.getHandlingUnits()).isEmpty()) {
			// Check handlingUnitService is already stored elsewhere
			List<Location> locations = locationService.getAllContainingExceptLocation(hu, lo);
			
			for (Location other : locations) {
				// HandlingUnit is already stored elsewhere
				LOG.warn(HU_IS_HERE_FORMATTER, hu.getId(), other);

				other.getLocationStatus().setErrorStatus(ErrorStatus.ERROR);
			}
			em.flush();
			
			// ATTENTION: Location error status does not need to be changed because the locationService was EMPTY!
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
				// Check handlingUnitService is already stored elsewhere
				List<Location> locations = locationService.getAllContainingExceptLocation(hu, lo);
				
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

		LOG.trace(END_PICK_FROM);
	}

	@Override
	public void pickFrom(final String locationId, final String handlingUnitId) throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.trace("--> pickFrom({}, {})", locationId, handlingUnitId);
		
		Location location;
		if (locationId == null) {
			location = null;
		}
		else {
			location = locationService.getById(locationId);
		}
		
		HandlingUnit handlingUnit;
		if (handlingUnitId == null) {
			handlingUnit = null;
		}
		else {
			handlingUnit = getById(handlingUnitId);
		}
		
		pickFrom(location, handlingUnit);

		LOG.trace(END_PICK_FROM);
	}
	
	@Override
	public HandlingUnit pickFrom(final Location location) throws LocationIsEmptyException {
		LOG.trace("--> pickFrom({})", location);

		checkIllegalArgument(location);
		
		Location lo = locationService.getById(location.getLocationId());
		if (lo == null) {
			lo = locationService.createOrUpdate(location);
		}
		else {
			lo = em.merge(location);
		}
		
		List<HandlingUnit> picksAvailable = lo.getAvailablePicks();
		
		if (picksAvailable.isEmpty()) {
			// ATTENTION: Location error status does not need to be changed because the locationService was EMPTY!
			//            NO manual adjustment is needed in this case!
			throw new LocationIsEmptyException("Location [" + lo.getLocationId() + "] is EMPTY");
		}
		
		// Pick it now
		lo.removeHandlingUnit(picksAvailable.get(0));
		
		em.flush();
		
		LOG.trace(END_PICK_FROM);

		return getById(picksAvailable.get(0).getId());
	}

	@Override
	public HandlingUnit pickFrom(final String locationId) throws LocationIsEmptyException {
		LOG.trace("--> pickFrom({})", locationId);

		Location location;
		if (locationId == null) {
			location = null;
		}
		else {
			location = locationService.getById(locationId);
		}
		
		HandlingUnit hu = pickFrom(location);
		
		LOG.trace(END_PICK_FROM);
		
		return hu;
	}
	
	@Override
	public void dropTo(final Location location, final HandlingUnit handlingUnit)
			throws CapacityExceededException, WeightExceededException, OverheightException
			, OverlengthException, OverwidthException
	{
		LOG.trace("--> dropTo({}, {})", location, handlingUnit);

		checkIllegalArgument(location);
		
		if (handlingUnit == null) {
			LOG.warn("HandlingUnit is null; this is valid but nothing will happen!");
			return;
		}
		
		HandlingUnit hu = getById(handlingUnit.getId());
		if (hu == null) {
			hu = createOrUpdate(handlingUnit);
		}
		else {
			hu = em.merge(handlingUnit);
		}

		Location lo = locationService.getById(location.getLocationId());
		if (lo == null) {
			lo = locationService.createOrUpdate(location);
		}
		else {
			lo = em.merge(location);
		}
		
		locationService.checkDimensionLimitExceeds(lo, hu);
		
		List<Location> locations = locationService.getAllContainingExceptLocation(hu, lo);
		
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

		// HandlingUnit is on top of other; so remove first
		if (hu.getBaseHU() != null) {
			remove(hu, hu.getBaseHU());
			// And reread changed HandlingUnit
			hu = getById(hu.getId());
		}
		
		hu.setLocation(lo);
		lo.addHandlingUnit(hu);
		em.merge(hu);
		em.merge(lo);
		em.flush();
		
		LOG.trace("<-- dropTo()");
	}

	@Override
	public void dropTo(final String locationId, final String handlingUnitId)
			throws CapacityExceededException, WeightExceededException, OverheightException
			, OverlengthException, OverwidthException
	{
		LOG.trace("--> dropTo({}, {})", locationId, handlingUnitId);
		
		Location location;
		if (locationId == null) {
			location = null;
		}
		else {
			location = locationService.getById(locationId);
		}
		
		HandlingUnit handlingUnit;
		if (handlingUnitId == null) {
			handlingUnit = null;
		}
		else {
			handlingUnit = getById(handlingUnitId);
		}
				
		dropTo(location, handlingUnit);
		
		LOG.trace("<-- dropTo()");
	}
	
	@Override
	public HandlingUnit assign(final HandlingUnit handlingUnit, final HandlingUnit base) {
		LOG.trace("--> assign() hu={} base={}", handlingUnit, base);

		checkIllegalArgument(handlingUnit, HU_IS_NULL_MSG);

		checkIllegalArgument(base, BASE_IS_NULL_MSG);
		
		HandlingUnit hu = getById(handlingUnit.getId());
		if (hu == null) {
			hu = createOrUpdate(handlingUnit);
		}
		else {
			hu = em.merge(handlingUnit);
		}
		
		HandlingUnit ba = getById(base.getId());
		if (ba == null) {
			ba = createOrUpdate(base);
		}
		else {
			ba = em.merge(base);
		}
				
		hu.setBaseHU(ba);
		boolean ret = ba.getContains().add(hu);
		LOG.info("assign result is: {}", ret);
		
		ba.setContains(ba.getContains());
		
		LOG.trace("<-- assign() base={}", ba);
		
		em.flush();
		
		return ba;
	}

	@Override
	public HandlingUnit assign(final String handlingUnitId, final String baseId) {
		LOG.trace("--> assign() hu={} base={}", handlingUnitId, baseId);
		
		HandlingUnit handlingUnit;
		if (handlingUnitId == null) {
			handlingUnit = null;
		}
		else {
			handlingUnit = getById(handlingUnitId);
		}
		
		HandlingUnit base;
		if (baseId == null) {
			base = null;
		}
		else {
			base = getById(baseId);
		}
		
		base = assign(handlingUnit, base);
		
		LOG.trace("<-- assign() base={}", base);
		
		return base;
	}
	
	@Override
	public HandlingUnit remove(final HandlingUnit handlingUnit, final HandlingUnit base) {
		LOG.trace("--> remove() hu={} base={}", handlingUnit, base);
		
		checkIllegalArgument(handlingUnit, HU_IS_NULL_MSG);

		checkIllegalArgument(base, BASE_IS_NULL_MSG);
		
		HandlingUnit hu = getById(handlingUnit.getId());
		if (hu == null) {
			hu = createOrUpdate(handlingUnit);
		}
		else {
			hu = em.merge(handlingUnit);
		}
		
		HandlingUnit ba = getById(base.getId());
		if (ba == null) {
			ba = createOrUpdate(base);
		}
		else {
			ba = em.merge(base);
		}
		
		hu.setBaseHU(null);
		boolean ret = ba.getContains().remove(hu);
		if (ret) {
			LOG.info("hu={} removed from base={}", hu.getId(), ba.getId());
		}
		else {
			LOG.warn("base={} does not contain hu={}; NO REMOVE", ba.getId(), hu.getId());
		}
		
		ba.setContains(ba.getContains());
		
		LOG.trace("<-- remove() base={}", ba);
		
		em.flush();
		
		return ba;
	}
	
	@Override
	public HandlingUnit remove(final String handlingUnitId, final String baseId) {
		LOG.trace("--> remove() hu={} base={}", handlingUnitId, baseId);
		
		HandlingUnit handlingUnit;
		if (handlingUnitId == null) {
			handlingUnit = null;
		}
		else {
			handlingUnit = getById(handlingUnitId);
		}
		
		HandlingUnit base;
		if (baseId == null) {
			base = null;
		}
		else {
			base = getById(baseId);
		}
		
		base = remove(handlingUnit, base);
		
		LOG.trace("<-- remove() base={}", base);
		
		return base;
	}
	
	@Override
	public HandlingUnit move(final HandlingUnit handlingUnit, final HandlingUnit destHandlingUnit) {
		LOG.trace("--> move() hu={} destHu={}", handlingUnit, destHandlingUnit);
		
		checkIllegalArgument(handlingUnit, HU_IS_NULL_MSG);

		checkIllegalArgument(destHandlingUnit, HU_IS_NULL_MSG);
		
		HandlingUnit hu = getById(handlingUnit.getId());
		if (hu == null) {
			hu = createOrUpdate(handlingUnit);
		}
		else {
			hu = em.merge(handlingUnit);
		}
		
		HandlingUnit dest = getById(destHandlingUnit.getId());
		if (dest == null) {
			dest = createOrUpdate(destHandlingUnit);
		}
		else {
			dest = em.merge(destHandlingUnit);
		}
		
		if (hu.getBaseHU() != null) {
			LOG.info("Remove {} from base {}", hu.getId(), hu);
			HandlingUnit base = remove(hu, hu.getBaseHU());
			LOG.info("Base now {}", base);
		}
		LOG.info("Assign {} to destination {}", hu.getId(), dest);
		dest = assign(getById(hu.getId()), dest);
		
		LOG.trace("<-- moved() hu={} destHu={}", hu, dest);
		
		em.flush();
		
		return getById(hu.getId());
	}

	@Override
	public HandlingUnit move(final String handlingUnitId, final String baseId) {
		LOG.trace("--> move() hu={} base={}", handlingUnitId, baseId);

		HandlingUnit handlingUnit;
		if (handlingUnitId == null) {
			handlingUnit = null;
		}
		else {
			handlingUnit = getById(handlingUnitId);
		}
		
		HandlingUnit base;
		if (baseId == null) {
			base = null;
		}
		else {
			base = getById(baseId);
		}
		
		HandlingUnit hu = move(handlingUnit, base);
		
		LOG.trace("<-- move() hu={}", hu);
		
		return hu;
	}
	
	@Override
	public Set<HandlingUnit> free(final HandlingUnit base) {
		LOG.trace("--> free() base={}", base);

		checkIllegalArgument(base, BASE_IS_NULL_MSG);

		HandlingUnit ba = getById(base.getId());
		if (ba == null) {
			ba = createOrUpdate(base);
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
				hu.setBaseHU(null);
			}
		}
		else {
			ret = new HashSet<>();			
		}
		
		ba.setContains(ba.getContains());
		
		if (ba.getBaseHU() != null) {
		    ba.getBaseHU().getContains().remove(ba);
		}
		
		LOG.info("free result is: {}", base);
		
		LOG.trace("<-- free() {}", ret);
		
		em.flush();
				
		return ret;
	}

	@Override
	public Set<HandlingUnit> free(final String baseId) {
		LOG.trace("--> free() base={}", baseId);

		HandlingUnit base;
		if (baseId == null) {
			base = null;
		}
		else {
			base = getById(baseId);
		}
		
		Set<HandlingUnit> huSet = free(base);
		
		LOG.trace("<-- free() {}", huSet);
		
		return huSet;
	}
	
	@Override
	public Set<HandlingUnit> flatContains(HandlingUnit base) {
		final Set<HandlingUnit> onBase = new HashSet<>(base.getContains());
		final Set<HandlingUnit> dummy = new HashSet<>();
		
		for (HandlingUnit item : onBase) {
			dummy.addAll(flatContains(item));
		}
		onBase.addAll(dummy);
		
 		return onBase;
	}
	
	@Override
	public int count() {
		final TypedQuery<Number> query = em.createQuery("SELECT COUNT(h) FROM HandlingUnit h", Number.class);

		return query.getSingleResult().intValue();
	}

	@Override
	public void logContains(final HandlingUnit base) {
		LOG.info("BASE {} contains {}", base.getId(),
				base.getContains().stream().map(h -> h.getId()).collect(Collectors.toList())
				);
	}
	
	@Override
	public void logFlatContains(final HandlingUnit base) {
		LOG.info("BASE {} flat contains {}", base.getId(),
				flatContains(base).stream().map(h -> h.getId()).collect(Collectors.toList())
				);
	}
	
	private void checkIllegalArgument(final Location location, final HandlingUnit handlingUnit) {
		if (location == null) {
			throw new IllegalArgumentException(LOCATION_IS_NULL_MSG);
		}
		
		if (handlingUnit == null) {
			throw new IllegalArgumentException(HU_IS_NULL_MSG);
		}
	}

	private void checkIllegalArgument(final Location location) {
		if (location == null) {
			throw new IllegalArgumentException(LOCATION_IS_NULL_MSG);
		}
	}

	private void checkIllegalArgument(final HandlingUnit handlingUnit, final String msg) {
		if (handlingUnit == null) {
			throw new IllegalArgumentException(msg);
		}
	}
}
