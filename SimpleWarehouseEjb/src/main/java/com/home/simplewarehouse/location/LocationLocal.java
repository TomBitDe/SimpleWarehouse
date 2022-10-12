package com.home.simplewarehouse.location;

import java.util.List;

import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;

/**
 * Local interface for Location usage.
 */
public interface LocationLocal {
	/**
	 * Create a Location and persist
	 * 
	 * @param location the given Location
	 */
	public void create(final Location location);
	/**
	 * Delete a Location and remove
	 * 
	 * @param location the given Location
	 */
	public void delete(Location location);
	/**
	 * Get a Location by its id
	 * 
	 * @param id the id
	 * 
	 * @return the corresponding Location
	 */
	public Location getById(final String id);
	/**
	 * Get a list of all Location items
	 * 
	 * @return the Location list
	 */
	public List<Location> getAll();
	/**
	 * Get a list of all Location items containing the given HandlingUnit
	 * 
	 * @param handlingUnit the handlingUnit to search for
	 * 
	 * @return the Location list
	 */
	public List<Location> getAllContaining(final HandlingUnit handlingUnit);
	/**
	 * Get a list of all Location items containing the given HandlingUnit but not on exceptLocation
	 * 
	 * @param handlingUnit the handlingUnit to search for
	 * @param exceptLocation the location not to consider
	 * 
	 * @return the Location list
	 */
	public List<Location> getAllContainingExceptLocation(final HandlingUnit handlingUnit, final Location exceptLocation);
	/**
	 * Get a list of all Location items with the given ErrorStatus
	 * 
	 * @param errorStatus the errorStatus to search for
	 * 
	 * @return the Location list
	 */
	public List<Location> getAllInErrorStatus(final ErrorStatus errorStatus);
}
