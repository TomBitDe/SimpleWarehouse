package com.home.simplewarehouse.location;

import java.rmi.Remote;
import java.util.List;

import com.home.simplewarehouse.model.LocationStatus;

/**
 * Local interface for Location Status usage.
 */
public interface LocationStatusService extends Remote {
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
