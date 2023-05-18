package com.home.simplewarehouse.location;

import java.util.ArrayList;
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

import com.home.simplewarehouse.model.Dimension;
import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.HeightCategory;
import com.home.simplewarehouse.model.LengthCategory;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.LocationStatus;
import com.home.simplewarehouse.model.WidthCategory;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;

/**
 * Bean class for Location usage. 
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors(PerformanceAuditor.class)
public class LocationBean implements LocationService {
	private static final Logger LOG = LogManager.getLogger(LocationBean.class);
	
	private static final String LOCATION_IS_NULL = "locationService is null";
	private static final String LOCATION_ID_IS_NULL = "locationId is null";
	
	private static final String HEIGHT_DOES_NOT_FIT = "Location has maximum height {}, heigth {} does not fit";
	private static final String LENGTH_DOES_NOT_FIT = "Location has maximum length {}, length {} does not fit";
	private static final String WIDTH_DOES_NOT_FIT = "Location has maximum width {}, width {} does not fit";
	
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Default constructor is mandatory
	 */
	public LocationBean() {
		super();
		LOG.trace("--> LocationBean()");
		LOG.trace("<-- LocationBean()");
	}
	
	@Override
	public Location createOrUpdate(final Location location) {
		LOG.trace("--> create");

		if (getById(location.getLocationId()) == null) {
			if (location.getLocationStatus() == null) {
				LocationStatus locationStatus = new LocationStatus(location.getLocationId());
				locationStatus.setLocation(location);
				location.setLocationStatus(locationStatus);
			}

			if (location.getDimension() == null) {
				Dimension dimension = new Dimension(location.getLocationId());
				dimension.setLocation(location);
				location.setDimension(dimension);
			}

			// No need to em.persist(locationStatusService) because it is done by cascade =
			// CascadeType.ALL
			// Same for dimensionService
			em.persist(location);
		}
		else {
			em.merge(location);
		}
		em.flush();

		LOG.trace("<-- create");
		
		return getById(location.getLocationId());
	}

	@Override
	public void delete(final Location location) {
		LOG.trace("--> delete({})", location);

		if (location != null && location.getLocationId() != null) {
			Location lo;
			
			if (!em.contains(location)) {
				lo = em.merge(location);
			}
			else {
				lo = location;
			}
			
			for (HandlingUnit handlingUnit : lo.getHandlingUnits()) {
				handlingUnit.setLocation(null);
				handlingUnit.setLocaPos(null);		
				em.flush();
			}
			
			// No need to    em.remove(locationStatusService)  because it is done by  cascade = CascadeType.ALL
			// Same for      dimensionService
			em.remove(lo);
			em.flush();

			LOG.debug("deleted: {}", lo);
		} 
		else {
			LOG.debug("Location == null or Id == null");
		}

		LOG.trace("<-- delete()");
	}

	@Override
	public void delete(final String id) {
		LOG.trace("--> delete({})", id);

		Location location = getById(id);
		
		if (location != null) {
			delete(location);
		} 
		else {
			LOG.debug("Location == null");
		}

		LOG.trace("<-- delete()");
	}

	@Override
	public Location getById(final String id) {
		LOG.trace("--> getById({})", id);

		if (id == null) {
			throw new IllegalArgumentException();
		}

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
	public List<Location> getAll(int offset, int count) {
		LOG.trace("--> getAll({}, {})", offset, count);

        if (offset < 0) {
            throw new IllegalArgumentException("offset < 0");
        }
        if (count < 1) {
            throw new IllegalArgumentException("count < 1");
        }

        TypedQuery<Location> query = em.createQuery("SELECT l FROM Location l", Location.class);
        query.setFirstResult(offset);
        query.setMaxResults(count);

        List<Location> locations = query.getResultList();

		LOG.trace("<-- getAll({}, {})", offset, count);

		return locations;
	}
	
	@Override
	public List<Location> getAllContaining(final HandlingUnit handlingUnit) {
		LOG.trace("--> getAllContaining({})", handlingUnit);
		
		List<Location> ret = new ArrayList<>();

		List<Location> locations = getAll();
		
		for (Location location : locations) {
			if (location.getHandlingUnits().contains(handlingUnit)) {
				ret.add(location);
			}
		}

		LOG.trace("<-- getAllContaining()");

		return ret;
	}
	
	@Override
	public List<Location> getAllContainingExceptLocation(final HandlingUnit handlingUnit,
			final Location exceptLocation) {
		LOG.trace("--> getAllContaining({})", handlingUnit);
		
		List<Location> ret = new ArrayList<>();

		List<Location> locations = getAll();
		
		for (Location location : locations) {
			if (! location.equals(exceptLocation) && location.getHandlingUnits().contains(handlingUnit)) {
				ret.add(location);
			}
		}

		LOG.trace("<-- getAllContaining()");

		return ret;
	}
	
	@Override
	public List<Location> getAllInErrorStatus(final ErrorStatus errorStatus) {
		LOG.trace("--> getAllInErrorStatus({})", errorStatus);
		
		List<Location> ret = new ArrayList<>();

		List<Location> locations = getAll();
		
		for (Location location : locations) {
			if (location.getLocationStatus().getErrorStatus().equals(errorStatus)) {
				ret.add(location);
			}
		}

		LOG.trace("<-- getAllInErrorStatus()");

		return ret;
	}

	@Override
	public boolean isFull(final Location location) {
		LOG.trace("--> isFull({})", location.getLocationId());

		Location loc = getById(location.getLocationId());
		
		if (loc.getDimension().getMaxCapacity() <= 0) {
			LOG.trace("<-- isFull(NOT_RELEVANT)");
			return false;
		}
		
		boolean full = loc.getHandlingUnits().size() >= loc.getDimension().getMaxCapacity();
		
		if (full) {
			LOG.info("Location already contains {}, maxCapacity is {}", loc.getHandlingUnits().size()
					, loc.getDimension().getMaxCapacity());
		}
		
		LOG.trace("<-- isFull() {}", full);
		
		return full;
	}

	@Override
	public List<Location> getAllFull() {
		LOG.trace("--> getAllFull()");
		
		List<Location> ret = new ArrayList<>();

		List<Location> locations = getAll();
		
		for (Location location : locations) {
			if (isFull(location)) {
				ret.add(location);
			}
		}

		LOG.trace("<-- getAllFull()");

		return ret;
	}

	@Override
	public List<Location> getAllWithFreeCapacity() {
		LOG.trace("--> getAllWithFreeCapacity()");
		
		List<Location> ret = new ArrayList<>();

		List<Location> locations = getAll();
		
		for (Location location : locations) {
			if (!isFull(location)) {
				ret.add(location);
			}
		}

		LOG.trace("<-- getAllWithFreeCapacity()");

		return ret;
	}

	@Override
	public boolean overweight(final Location location, final int weight) {
		LOG.trace("--> overweight({}, {})", location.getLocationId(), weight);

		Location loc = getById(location.getLocationId());
		
		if (loc.getDimension().getMaxWeight() <= 0) {
			LOG.trace("<-- overweight(NOT_RELEVANT)");
			return false;
		}
		
		// Expected total weight
		int expSum = loc.getHandlingUnits().stream().mapToInt(HandlingUnit::getWeight).sum() + weight;
		
		boolean overweight = expSum >= loc.getDimension().getMaxWeight();
		
		if (overweight) {
			LOG.info("Location already has weight {} and to add {}, maxWeight is {}"
					, loc.getHandlingUnits().stream().mapToInt(HandlingUnit::getWeight).sum()
					, weight, loc.getDimension().getMaxWeight());
		}
		
		LOG.trace("<-- overweight() {}", overweight);
		
		return overweight;
	}

	@Override
	public boolean overheight(final Location location, final HeightCategory height) {
		LOG.trace("--> overheight({}, {})", location.getLocationId(), height);

		Location loc = getById(location.getLocationId());
		
		if (loc.getDimension().getMaxHeight().equals(HeightCategory.NOT_RELEVANT)) {
			LOG.trace("<-- overheight(NOT_RELEVANT)");
			return false;
		}
		
		boolean overheight;
		
		if (height.equals(HeightCategory.TOO_HIGH) || height.equals(HeightCategory.UNKNOWN)) {
			overheight = true;
		}
		else if (loc.getDimension().getMaxHeight().equals(HeightCategory.MIDDLE)
				&& (height.equals(HeightCategory.HIGH))) {
			overheight = true;
		}
		else if (loc.getDimension().getMaxHeight().equals(HeightCategory.LOW)
				&& (height.equals(HeightCategory.HIGH) || height.equals(HeightCategory.MIDDLE))) {
			overheight = true;
		}
		else {
			overheight = false;
		}
		
		if (overheight) {
			LOG.info(HEIGHT_DOES_NOT_FIT, loc.getDimension().getMaxHeight()
					, height);
		}
		
		LOG.trace("<-- overheight() {}", overheight);
		
		return overheight;
	}

	@Override
	public boolean overlength(final Location location, final LengthCategory length) {
		LOG.trace("--> overlength({}, {})", location.getLocationId(), length);

		Location loc = getById(location.getLocationId());
		
		if (loc.getDimension().getMaxLength().equals(LengthCategory.NOT_RELEVANT)
				|| loc.getDimension().getMaxCapacity() > 1) {
			LOG.trace("<-- overlength(NOT_RELEVANT)");
			return false;
		}
		
		boolean overlength;
		
		if (length.equals(LengthCategory.TOO_LONG) || length.equals(LengthCategory.UNKNOWN)) {
			overlength = true;
		}
		else if (loc.getDimension().getMaxLength().equals(LengthCategory.MIDDLE)
				&& (length.equals(LengthCategory.LONG))) {
			overlength = true;
		}
		else if (loc.getDimension().getMaxLength().equals(LengthCategory.SHORT)
				&& (length.equals(LengthCategory.LONG) || length.equals(LengthCategory.MIDDLE))) {
			overlength = true;
		}
		else {
			overlength = false;
		}
		
		if (overlength) {
			LOG.info(LENGTH_DOES_NOT_FIT, loc.getDimension().getMaxLength()
					, length);
		}
		
		LOG.trace("<-- overlength() {}", overlength);
		
		return overlength;
	}

	@Override
	public boolean overwidth(final Location location, final WidthCategory width) {
		LOG.trace("--> overwidth({}, {})", location.getLocationId(), width);

		Location loc = getById(location.getLocationId());
		
		if (loc.getDimension().getMaxWidth().equals(WidthCategory.NOT_RELEVANT)) {
			LOG.trace("<-- overwidth(NOT_RELEVANT)");
			return false;
		}
		
		boolean overwidth;
		
		if (width.equals(WidthCategory.TOO_WIDE) || width.equals(WidthCategory.UNKNOWN)) {
			overwidth = true;
		}
		else if (loc.getDimension().getMaxWidth().equals(WidthCategory.MIDDLE)
				&& (width.equals(WidthCategory.WIDE))) {
			overwidth = true;
		}
		else if (loc.getDimension().getMaxWidth().equals(WidthCategory.NARROW)
				&& (width.equals(WidthCategory.WIDE) || width.equals(WidthCategory.MIDDLE))) {
			overwidth = true;
		}
		else {
			overwidth = false;
		}
		
		if (overwidth) {
			LOG.info(WIDTH_DOES_NOT_FIT, loc.getDimension().getMaxWidth()
					, width);
		}
		
		LOG.trace("<-- overwidth() {}", overwidth);
		
		return overwidth;
	}

	@Override
	public void checkDimensionLimitExceeds(final Location location, final HandlingUnit handlingUnit)
			throws CapacityExceededException, WeightExceededException, OverheightException
			, OverlengthException, OverwidthException {
		LOG.trace("--> checkDimensionLimitExceeds({}, {})", location.getLocationId(), handlingUnit.getId());

		if (isFull(location)) {
			throw new CapacityExceededException("Location has no more capacity");
		}
		
		if (overweight(location, handlingUnit.getWeight())) {
			throw new WeightExceededException("Location will become overweighted");
		}
		
		if (overheight(location, handlingUnit.getHeight())) {
			throw new OverheightException("HandlingUnit does not fit in Location (height)");
		}
		
		if (overlength(location, handlingUnit.getLength())) {
			throw new OverlengthException("HandlingUnit does not fit in Location (length)");
		}

		if (overwidth(location, handlingUnit.getWidth())) {
			throw new OverwidthException("HandlingUnit does not fit in Location (width)");
		}

		LOG.trace("<-- checkDimensionLimitExceeds()");
	}

	@Override
	public List<HandlingUnit> getHandlingUnits(final Location location) {
		if (location == null) {
			throw new IllegalArgumentException(LOCATION_IS_NULL);
		}

		if (location.getLocationId() == null) {
			throw new IllegalArgumentException(LOCATION_ID_IS_NULL);
		}
		
		return getById(location.getLocationId()).getHandlingUnits();
	}

	@Override
	public boolean addHandlingUnit(final Location location, final HandlingUnit handlingUnit) {
		if (location == null) {
			throw new IllegalArgumentException(LOCATION_IS_NULL);
		}

		if (location.getLocationId() == null) {
			throw new IllegalArgumentException(LOCATION_ID_IS_NULL);
		}
		
		if (handlingUnit == null || handlingUnit.getId() == null) {
			return false;
		}
		
		return location.addHandlingUnit(handlingUnit);
	}

	@Override
	public List<HandlingUnit> getAvailablePicks(final Location location) {
		LOG.trace("--> getAvailablePicks()");
		
		if (location == null) {
			throw new IllegalArgumentException(LOCATION_IS_NULL);
		}

		if (location.getLocationId() == null) {
			throw new IllegalArgumentException(LOCATION_ID_IS_NULL);
		}
		
		List<HandlingUnit> ret = location.getAvailablePicks();
		
		LOG.trace("<-- getAvailablePicks()");

		return ret;
	}

	@Override
	public int count() {
		final TypedQuery<Number> query = em.createQuery("SELECT COUNT(l) FROM Location l", Number.class);

		return query.getSingleResult().intValue();
	}
}
