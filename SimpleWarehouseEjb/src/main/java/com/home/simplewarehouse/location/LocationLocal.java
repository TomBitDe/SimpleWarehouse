package com.home.simplewarehouse.location;

import java.util.List;

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
	public void create(Location location);
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
	public Location getById(String id);
	/**
	 * Get a list of all Location items
	 * 
	 * @return the Location list
	 */
	public List<Location> getAll();
}
