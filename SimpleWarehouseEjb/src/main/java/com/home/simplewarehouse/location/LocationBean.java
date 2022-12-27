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
import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.HeightCategory;
import com.home.simplewarehouse.model.LengthCategory;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.Dimension;
import com.home.simplewarehouse.model.LocationStatus;
import com.home.simplewarehouse.model.WidthCategory;
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
	
	private static final String HEIGHT_DOES_NOT_FIT = "Location has maximum height {}, heigth {} does not fit";
	private static final String LENGTH_DOES_NOT_FIT = "Location has maximum length {}, length {} does not fit";
	private static final String WIDTH_DOES_NOT_FIT = "Location has maximum width {}, width {} does not fit";
	
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

		LocationStatus locationStatus = new LocationStatus(location.getLocationId());
		locationStatus.setLocation(location);
				
		location.setLocationStatus(locationStatus);
		
		Dimension dimension = new Dimension(location.getLocationId());
		dimension.setLocation(location);
		
		location.setDimension(dimension);
		
		// No need to    em.persist(locationStatus)  because it is done by  cascade = CascadeType.ALL
		// Same for      dimension
		em.persist(location);
		
		em.flush();

		LOG.trace("<-- create");
	}

	@Override
	public void delete(Location location) {
		LOG.trace("--> delete({})", location);

		if (location != null && location.getLocationId() != null) {
			if (!em.contains(location)) {
				location = em.merge(location);
			}
			
			for (HandlingUnit handlingUnit : location.getHandlingUnits()) {
				handlingUnit.setLocation(null);
				handlingUnit.setLocaPos(null);		
				em.flush();
			}
			
			// No need to    em.remove(locationStatus)  because it is done by  cascade = CascadeType.ALL
			// Same for      dimension
			em.remove(location);
			em.flush();

			LOG.debug("deleted: {}", location);
		} 
		else {
			LOG.debug("Location == null or Id == null");
		}

		LOG.trace("<-- delete()");
	}

	@Override
	public Location getById(final String id) {
		LOG.trace("--> getById({})", id);

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
	public boolean overweight(Location location, int weight) {
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
	public boolean overheight(Location location, HeightCategory height) {
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
	public boolean overlength(Location location, LengthCategory length) {
		LOG.trace("--> overlength({}, {})", location.getLocationId(), length);

		Location loc = getById(location.getLocationId());
		
		if (loc.getDimension().getMaxLength().equals(LengthCategory.NOT_RELEVANT)
				|| loc.getDimension().getMaxCapacity() > 1
				|| loc.getDimension().getMaxCapacity() == 0) {
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
	public boolean overwidth(Location location, WidthCategory width) {
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
}
