package com.home.simplewarehouse.zone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

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

import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
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
	
	public static final String ZONE_IS_NULL = "zone is null";
	public static final String ZONE_ID_IS_NULL = "zone id is null";

	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private LocationService locationService;

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
	public void delete(Zone zone) {
		LOG.trace("--> delete({})", zone);

		if (zone != null && zone.getId() != null) {
			Zone zo = getById(zone.getId());
			
			if (zo != null && !zo.getLocations().isEmpty()) {
			    List<Location> locationsCopy = new ArrayList<>(zo.getLocations());
			    locationsCopy.stream()
			        .filter(Objects::nonNull)
			        .forEach(l -> l.setZones(null));
			    em.flush();
			}
			
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
	public Set<Zone> getAll() {
		LOG.trace("--> getAll()");

		Set<Zone> zones = new HashSet<>();
		
		TypedQuery<Zone> query = em.createQuery("SELECT zo FROM Zone zo", Zone.class);
		zones.addAll(query.getResultList());

		LOG.trace("<-- getAll()");

		return zones;
	}

	@Override
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
	public void clear(Zone zone) {
		checkZone(zone);
		
	    List<Location> safeList = new CopyOnWriteArrayList<>(zone.getLocations());
	    
	    zone.setLocations(new HashSet<>());
	    em.merge(zone);
	    
	    safeList.stream().forEach(loc -> {
	    	loc.setZones(null);
	    	em.merge(loc);
	    });
	}

	@Override
	public void clearAll() {
		getAll().stream().forEach(this::clear);
	}

	@Override
	public void deleteAll() {
		getAll().stream().forEach(this::delete);
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
}
