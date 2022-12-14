package com.home.simplewarehouse.handlingunit;

import java.util.List;

import com.home.simplewarehouse.location.CapacityExceededException;
import com.home.simplewarehouse.location.OverheightException;
import com.home.simplewarehouse.location.OverlengthException;
import com.home.simplewarehouse.location.OverwidthException;
import com.home.simplewarehouse.location.WeightExceededException;
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
	public void create(final HandlingUnit handlingUnit);
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
	public HandlingUnit getById(final String id);
	/**
	 * Pick from a Location the given HandlingUnit
	 * 
	 * @param location the Location to pick from
	 * @param handlingUnit the HandlingUnit
	 * 
	 * @throws LocationIsEmptyException in case the Location is empty
	 * @throws HandlingUnitNotOnLocationException in case the Location does not contain the HandlingUnit
	 */
	public void pickFrom(Location location, HandlingUnit handlingUnit) throws LocationIsEmptyException, HandlingUnitNotOnLocationException;
	/**
	 * Pick from a FIFO / LIFO Location<br>
	 * Because of FIFO / LIFO the HandlingUnit is not a parameter.
	 * The HandlingUnit returned depends on the access limitation.
	 * 
	 * @param location the Location to pick from
	 * @return the HandlingUnit after pick 
	 * 
	 * @throws LocationIsEmptyException in case the Location is empty
	 */
	public HandlingUnit pickFrom(Location location) throws LocationIsEmptyException;
	/**
	 * Drop a HandlingUnit on a Location
	 * 
	 * @param location the given Location
	 * @param handlingUnit the HandlingUnit
	 * 
	 * @throws CapacityExceededException in case the Location capacity will exceed by this drop
	 * @throws WeightExceededException in case the Location weight will exceed by this drop
	 * @throws OverheightException in case the HandlingUnit does not fit in the Location by this drop
	 * @throws OverlengthException in case the HandlingUnit does not fit in the Location by this drop
	 * @throws OverwidthException in case the HandlingUnit does not fit in the Location by this drop
	 */
	public void dropTo(Location location, HandlingUnit handlingUnit) 
			throws CapacityExceededException, WeightExceededException, OverheightException
			, OverlengthException, OverwidthException;
	/**
	 * Get a list of all HandlingUnit items
	 * 
	 * @return the HandlingUnit list
	 */
	public List<HandlingUnit> getAll();
}
