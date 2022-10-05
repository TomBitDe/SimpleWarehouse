package com.home.simplewarehouse.location;

import java.util.List;

import com.home.simplewarehouse.model.LocationStatus;

/**
 * Local interface for Location Status usage.
 */
public interface LocationStatusLocal {
	/**
	 * Create a LocationStatus and persist
	 * 
	 * @param locationStatus the given LocationStatus
	 */
	public void create(final LocationStatus locationStatus);
	/**
	 * Delete a LocationStatus and remove
	 * 
	 * @param locationStatus the given LocationStatus
	 */
	public void delete(LocationStatus locationStatus);
	/**
	 * Get a LocationStatus by its id
	 * 
	 * @param id the id
	 * 
	 * @return the corresponding LocationStatus
	 */
	public LocationStatus getById(final String id);
	/**
	 * Get a list of all LocationStatus items
	 * 
	 * @return the LocationStatus list
	 */
	public List<LocationStatus> getAll();

}
