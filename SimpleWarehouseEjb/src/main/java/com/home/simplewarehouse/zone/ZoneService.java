package com.home.simplewarehouse.zone;

import java.util.Set;

import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.Zone;

/**
 * Service interface for Zone usage.
 */
public interface ZoneService {
	/**
	 * Create or update a Zone and persist
	 * 
	 * @param zone the given Zone
	 * 
	 * @return the created Zone
	 */
	public Zone createOrUpdate(final Zone zone);
	/**
	 * Delete a Zone and remove
	 * 
	 * @param zone the given Zone
	 */
	public void delete(final Zone zone);
	/**
	 * Delete a Zone by its id and remove
	 * 
	 * @param id of the Zone
	 */
	public void delete(final String id);
	/**
	 * Delete all existing Zones
	 */
	public void deleteAll();
	/**
	 * Get a Zone by its id
	 * 
	 * @param id the id
	 * 
	 * @return the corresponding Zone
	 */
	public Zone getById(final String id);
	/**
	 * Get a set of all Zone items
	 * 
	 * @return the Zone set
	 */
	public Set<Zone> getAll();
	/**
	 * Get a set of all Zone items
	 * 
     * @param offset the position to start fetching
     * @param count  the number of fetches to do
     *
	 * @return the Zone set based on offset and count
	 */
	public Set<Zone> getAll(int offset, int count);
	/**
	 * Get a list of all Locations in a given zoneId
	 * 
     * @param zoneId the Zones id
     *
	 * @return the Location list based on the zoneId
	 */
	public Set<Location> getAllLocations(String zoneId);
	/**
	 * Get a list of all HandlingUnits in a given zoneId
	 * 
     * @param zoneId the Zones id
     *
	 * @return the HandlingUnit list based on the zoneId
	 */
	public Set<HandlingUnit> getAllHandlingUnits(String zoneId);
	/**
	 * Get a list of all Locations in a given Zone
	 * 
     * @param zone the Zone
     *
	 * @return the Location list based on the zone
	 */
	public Set<Location> getAllLocations(Zone zone);
	/**
	 * Get a list of all HandlingUnits in a given zone
	 * 
     * @param zone the Zone
     *
	 * @return the HandlingUnit list based on the zone
	 */
	public Set<HandlingUnit> getAllHandlingUnits(Zone zone);
	/**
	 * Count the Zone items
	 *
	 * @return the number of Zone items
	 */
	public int count();
	/**
	 * Move a Location from its current Zone to the destination Zone
	 * 
	 * @param location the Location to move
	 * @param current the current Zone
	 * @param destination the destination Zone 
	 */
	public void moveLocation(Location location, Zone current, Zone destination);
	/**
	 * Move a List of Locations from their current Zone to the destination Zone
	 * 
	 * @param locations a List of Locations
	 * @param current the current Zone
	 * @param destination the destination Zone 
	 */
	public void moveLocations(Set<Location> locations, Zone current, Zone destination);
	/**
	 * Add a Location to a given Zone
	 * 
	 * @param location the Location to move
	 * @param zone the zone
	 */
	public void addLocationTo(Location location, Zone zone);
	/**
	 * Initialize a Zone with the given List of Locations
	 * 
	 * @param zone the Zone to initialize 
	 * @param locations the List of Locations
	 */
	public void initZoneBy(Zone zone, Set<Location> locations);
	/**
	 * Clear a given Zone from its Locations
	 * 
	 * @param zone the Zone to clear
	 */
	public void clear(Zone zone);
	/**
	 * Clear all existing Zones
	 */
	public void clearAll();
}
