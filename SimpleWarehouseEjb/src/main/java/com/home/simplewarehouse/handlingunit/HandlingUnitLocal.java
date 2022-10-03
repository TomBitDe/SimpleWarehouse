package com.home.simplewarehouse.handlingunit;

import java.util.List;

import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;

/**
 * Local interface for HandlingUnit usage.
 */
public interface HandlingUnitLocal {
	/**
	 * Create a HandlingUnit and persist
	 * 
	 * @param handlingUnit the given HandlingUnit
	 */
	public void create(HandlingUnit handlingUnit);
	/**
	 * Delete a HandlingUnit and remove
	 * 
	 * @param handlingUnit the given HandlingUnit
	 */
	public void delete(HandlingUnit handlingUnit);
	/**
	 * Get a HandlingUnit by its id
	 * 
	 * @param id the id
	 * 
	 * @return the corresponding HandlingUnit
	 */
	public HandlingUnit getById(String id);
	/**
	 * Pick from a Location the given HandlingUnit
	 * 
	 * @param location the Location
	 * @param handlingUnit the HandlingUnit
	 * 
	 * @throws LocationIsEmptyException in case the Location is empty
	 * @throws HandlingUnitNotOnLocationException in case the Location does not contain the HandlingUnit
	 */
	public void pickFrom(Location location, HandlingUnit handlingUnit) throws LocationIsEmptyException, HandlingUnitNotOnLocationException;
	/**
	 * Drop a HandlingUnit on a Location
	 * 
	 * @param location the given Location
	 * @param handlingUnit the HandlingUnit
	 */
	public void dropTo(Location location, HandlingUnit handlingUnit);
	/**
	 * Get a list of all HandlingUnit items
	 * 
	 * @return the HandlingUnit list
	 */
	public List<HandlingUnit> getAll();
}
