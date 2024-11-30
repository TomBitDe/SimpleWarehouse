package com.home.simplewarehouse.zone;

import java.util.List;

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
	 * Get a Zone by its id
	 * 
	 * @param id the id
	 * 
	 * @return the corresponding Zone
	 */
	public Zone getById(final String id);
	/**
	 * Get a list of all Zone items
	 * 
	 * @return the Zone list
	 */
	public List<Zone> getAll();
	/**
	 * Get a list of all Zone items
	 * 
     * @param offset the position to start fetching
     * @param count  the number of fetches to do
     *
	 * @return the Zone list based on offset and count
	 */
	public List<Zone> getAll(int offset, int count);
	/**
	 * Count the Zone items
	 *
	 * @return the number of Zone items
	 */
	public int count();
	
	public void moveLocationTo(Location location, Zone zone);

	public void moveLocationsTo(List<Location> locations, Zone zone);
	
	public void addLocationTo(Location location, Zone zone);
	
	public void initZoneTo(Zone zone, List<Location> locations);
	
	public void clear(Zone zone);

	public void clearAllZones();
}
