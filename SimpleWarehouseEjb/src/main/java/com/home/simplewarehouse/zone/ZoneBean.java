package com.home.simplewarehouse.zone;

import java.util.HashSet;
import java.util.Iterator;
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

import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
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
	
	/**
	 * Message constant
	 */
	public static final String ZONE_IS_NULL = "zone is null";
	/**
	 * Message constant
	 */
	public static final String ZONE_ID_IS_NULL = "zone id is null";
	/**
	 * Message constant
	 */
	public static final String ZONE_ID_IS_EMPTY = "zone id is empty or blank";

	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private LocationService locationService;
	
	@EJB
	private HandlingUnitService handlingUnitService;

	/**
	 * Default constructor is mandatory
	 */
	public ZoneBean() {
		super();
		LOG.trace("--> ZoneBean()");
		LOG.trace("<-- ZoneBean()");
	}

	@Override
	public Zone createOrUpdate(final Zone zone) {
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
	public void delete(Zone zone) {
	    LOG.trace("--> delete({})", zone);

	    if (zone != null && zone.getId() != null) {
	        Zone zo = getById(zone.getId());

	        if (zo != null && !zo.getLocations().isEmpty()) {
	            Set<Location> safeList = new HashSet<>(zo.getLocations());

	            for (Location item : safeList) {
	                Location locn = locationService.getById(item.getLocationId());
	                if (locn != null && locn.getZones() != null && locn.getZones().remove(zo)) {
	                    em.refresh(locn);
	                    locn.setZones(locn.getZones());
	                    locationService.createOrUpdate(locn);
	                }
	            }
	        }

	        em.remove(em.contains(zo) ? zo : em.merge(zo)); 
	        em.flush();

	        LOG.debug("deleted: {}", zo);
	    }
	    else {
	        LOG.debug("Zone == null or id == null");
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
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Set<Zone> getAll() {
		LOG.trace("--> getAll()");

		Set<Zone> zones = new HashSet<>();
		
		TypedQuery<Zone> query = em.createQuery("SELECT zo FROM Zone zo", Zone.class);
		zones.addAll(query.getResultList());

		LOG.trace("<-- getAll()");

		return zones;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Set<Zone> getAll(int offset, int count) {
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

        Set<Zone> zones = new HashSet<>();
        zones.addAll(query.getResultList());

		LOG.trace("<-- getAll({}, {})", offset, count);

		return zones;
	}

	@Override
	public int count() {
		final TypedQuery<Number> query = em.createQuery("SELECT COUNT(zo) FROM Zone zo", Number.class);

		return query.getSingleResult().intValue();
	}
	
	@Override
	public void moveLocation(Location location, Zone current, Zone destination) {
		checkParam(location, current, destination);
		
		// Remove from current
		if (location.getZones() != null) {
			location.getZones().remove(current);
		    
		    em.merge(location);
		}
		
		current.getLocations().remove(location);
		em.merge(current);
		
		// Add to destination zone
		destination.getLocations().add(location);
		em.merge(destination);
		
		Set<Zone> temp = location.getZones();
		temp.add(destination);
		location.setZones(temp);
		
		em.merge(location);
		
		em.flush();
	}

	@Override
	public void moveLocations(Set<Location> locations, Zone current, Zone destination) {
		locations.stream().forEach(l -> moveLocation(l, current, destination));
	}

	@Override
	public void addLocationTo(String locationId, String zoneId) {
		checkParam(locationId, zoneId);
		
		addLocationTo(locationService.getById(locationId), getById(zoneId));
	}

	@Override
	public void addLocationTo(Location location, Zone zone) {
		checkParam(location, zone);

		zone.getLocations().add(location);
		location.getZones().add(zone);

		em.merge(zone);
		em.merge(location);
		em.flush();
	}

	@Override
	public void initZoneBy(Zone zone, Set<Location> locations) {
		checkParam(locations, zone);
		
		zone.setLocations(locations);
		
		Set<Zone> temp = new HashSet<>();
		temp.add(zone);
		
		locations.stream().forEach(l -> l.setZones(temp));
		
		em.merge(zone);
		
		locations.stream().forEach(l -> em.merge(l));
    }
	
	@Override
	public void clear(String zoneId) {
        checkZone(zoneId);
        
		clear(getById(zoneId));
	}
	

	@Override
	public void clear(Zone zone) {
		checkZone(zone);
		
	    if (zone.getId() != null) {
	        Zone zo = getById(zone.getId());

	        if (zo != null && !zo.getLocations().isEmpty()) {
	            Set<Location> safeList = new HashSet<>(zo.getLocations());

	            for (Location item : safeList) {
	                Location locn = locationService.getById(item.getLocationId());
	                if (locn != null && locn.getZones() != null) {
	                    em.refresh(locn);
	                    locn.getZones().remove(zo);
	                    locn.setZones(locn.getZones());
	                    locationService.createOrUpdate(locn);
	                }
	            }
	        }

	        em.merge(zo); 
	        em.flush();

	        LOG.debug("cleared: {}", zo);
	    }
	    else {
	        LOG.debug("Zone == null or id == null");
	    }

	    LOG.trace("<-- clear()");
	}

	@Override
	public void clearAll() {
		getAll().stream().forEach(this::clear);
	}

	@Override
	public void deleteAll() {
		Iterator<Zone> iterator = getAll().iterator();
		while (iterator.hasNext()) {
		    delete(iterator.next());
		}
	}

	private void checkParam(Location location, Zone zone) {
		if (location == null) {
			throw new IllegalArgumentException(LocationBean.LOCATION_IS_NULL);
		}

		if (location.getLocationId() == null) {
			throw new IllegalArgumentException(LocationBean.LOCATION_ID_IS_NULL);
		}
		
		if (zone == null) {
			throw new IllegalArgumentException(ZONE_IS_NULL);
		}

		if (zone.getId() == null) {
			throw new IllegalArgumentException(ZONE_ID_IS_NULL);
		}
	}
	
	private void checkParam(String locationId, String zoneId) {
		if (locationId == null) {
			throw new IllegalArgumentException(LocationBean.LOCATION_ID_IS_NULL);
		}

		if (locationId.trim().isEmpty()) {
			throw new IllegalArgumentException(LocationBean.LOCATION_ID_IS_EMPTY);
		}

		if (locationService.getById(locationId) == null) {
			throw new IllegalArgumentException(LocationBean.LOCATION_IS_NULL);
		}
		
		if (zoneId == null) {
			throw new IllegalArgumentException(ZONE_ID_IS_NULL);
		}

		if (zoneId.trim().isEmpty()) {
			throw new IllegalArgumentException(ZONE_ID_IS_EMPTY);
		}

		if (getById(zoneId) == null) {
			throw new IllegalArgumentException(ZONE_ID_IS_NULL);
		}
	}
	
	private void checkParam(Location location, Zone current, Zone destination) {
		checkParam(location, current);
		
		checkZone(destination);
	}

	private void checkParam(Set<Location> locations, Zone zone) {
		locations.stream().forEach(l -> checkParam(l, zone));
	}
	
	private void checkZone(Zone zone) {
		if (zone == null) {
			throw new IllegalArgumentException(ZONE_IS_NULL);
		}

		if (zone.getId() == null) {
			throw new IllegalArgumentException(ZONE_ID_IS_NULL);
		}
	}

	private void checkZone(String zoneId) {
		if (zoneId == null) {
			throw new IllegalArgumentException(ZONE_ID_IS_NULL);
		}

		if (zoneId.trim().isEmpty() ) {
			throw new IllegalArgumentException(ZONE_ID_IS_EMPTY);
		}
		
		if (getById(zoneId) == null) {
			throw new IllegalArgumentException(ZONE_IS_NULL);
		}
	}

	@Override
	public Set<Location> getAllLocations(String zoneId) {
		if (zoneId == null) {
			throw new IllegalArgumentException(ZONE_ID_IS_NULL);
		}
		if (getById(zoneId) == null) {
			throw new IllegalArgumentException(ZONE_IS_NULL);
		}
		
		Set<Location> ret;
		
		ret = locationService.getAll().stream()
				.filter(loc -> loc.getZones().stream()
						.anyMatch(zone -> zone.getId().equals(zoneId)))
				.collect(Collectors.toSet());
		
		if (ret == null)
			return new HashSet<>();
		
		return ret;
	}

	@Override
	public Set<HandlingUnit> getAllHandlingUnits(String zoneId) {
		if (zoneId == null) {
			throw new IllegalArgumentException(ZONE_ID_IS_NULL);
		}
		if (getById(zoneId) == null) {
			throw new IllegalArgumentException(ZONE_IS_NULL);
		}
		
		Set<HandlingUnit> ret;
		
		ret = getAllLocations(zoneId).stream()
				 // Stream of HandlingUnits from all Locations create
	             .flatMap(loc -> loc.getHandlingUnits().stream())
	             // Collect the HandlingUnits in a Set	
	             .collect(Collectors.toSet());	
		
		if (ret == null)
			return new HashSet<>();
		
		return ret;
	}

	@Override
	public Set<Location> getAllLocations(Zone zone) {
		checkZone(zone);
		
		Set<Location> ret;
		
		ret = locationService.getAll().stream()
				.filter(loc -> loc.getZones().stream()
						.anyMatch(zo -> zo.equals(zone)))
				.collect(Collectors.toSet());
		
		if (ret == null)
			return new HashSet<>();
		
		return ret;
	}

	@Override
	public Set<HandlingUnit> getAllHandlingUnits(Zone zone) {
		checkZone(zone);
		
		Set<HandlingUnit> ret;
		
		ret = getAllLocations(zone).stream()
				 // Stream of HandlingUnits from all Locations create
	             .flatMap(loc -> loc.getHandlingUnits().stream())
	             // Collect the HandlingUnits in a Set	
	             .collect(Collectors.toSet());	
		
		if (ret == null)
			return new HashSet<>();
		
		return ret;
	}
}
