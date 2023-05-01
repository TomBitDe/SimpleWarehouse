package com.home.simplewarehouse.location;

import java.util.List;

import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.HeightCategory;
import com.home.simplewarehouse.model.LengthCategory;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.WidthCategory;

/**
 * Service interface for Location usage.
 */
public interface LocationService {
	/**
	 * Create a Location and persist
	 * 
	 * @param location the given Location
	 * 
	 * @return the Location created
	 */
	public Location create(final Location location);
	/**
	 * Delete a Location and remove
	 * 
	 * @param location the given Location
	 */
	public void delete(final Location location);
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
	 * Gets a list of all HandlingUnits on a locationService
	 * 
	 * @param location the given locationService
	 * 
	 * @return a list of HandlingUnits
	 */
	public List<HandlingUnit> getHandlingUnits(final Location location);
	/**
	 * Adds a HandlingUnit to the list of HandlingUnits of this Location
	 * 
	 * @param location the locationService having the List 
	 * @param handlingUnit the handlingUnitService to add
	 * 
	 * @return true if adding has been done, else false
	 */
	public boolean addHandlingUnit(final Location location, final HandlingUnit handlingUnit);
	/**
	 * Get a list of all Location items containing the given HandlingUnit
	 * 
	 * @param handlingUnit the handlingUnitService to search for
	 * 
	 * @return the Location list
	 */
	public List<Location> getAllContaining(final HandlingUnit handlingUnit);
	/**
	 * Get a list of all Location items containing the given HandlingUnit but not on exceptLocation
	 * 
	 * @param handlingUnit the handlingUnitService to search for
	 * @param exceptLocation the locationService not to consider
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
	/**
	 * Check if a Location is fully occupied related to its capacity
	 * 
	 * @param location the locationService to check
	 * 
	 * @return true if fully occupied else false
	 */
	public boolean isFull(final Location location);
	/**
	 * Get a list of all full Locations
	 * 
	 * @return the Location list
	 */
	public List<Location> getAllFull();
	/**
	 * Get a list of all Locations with free capacity
	 * 
	 * @return the Location list
	 */
	public List<Location> getAllWithFreeCapacity();
	/**
	 * Check if a Location will be overweighted related to its maximum weight
	 * 
	 * @param location the locationService to check
	 * @param weight the additional weight
	 * 
	 * @return true if the additional weight will exceed the weight limit else false
	 */
	public boolean overweight(final Location location, final int weight);
	/**
	 * Check if a Location fits related to its maximum height
	 * 
	 * @param location the locationService to check
	 * @param height the height to drop
	 * 
	 * @return true if the height does not fit else false
	 */
	public boolean overheight(final Location location, final HeightCategory height);
	/**
	 * Check if a Location fits related to its maximum length
	 * 
	 * @param location the locationService to check
	 * @param length the length to drop
	 * 
	 * @return true if the length does not fit else false
	 */
	public boolean overlength(final Location location, final LengthCategory length);
	/**
	 * Check if a Location fits related to its maximum width
	 * 
	 * @param location the locationService to check
	 * @param width the width to drop
	 * 
	 * @return true if the width does not fit else false
	 */
	public boolean overwidth(final Location location, final WidthCategory width);
	/**
	 * Check if a dimensionService limit exceeds when dropping a HandlingUnit on a Location
	 * 
	 * @param location the locationService to check
	 * @param handlingUnit the handlingUnitService to drop
	 * 
	 * @throws CapacityExceededException in case the Location capacity will exceed by this drop
	 * @throws WeightExceededException in case the Location weight will exceed by this drop
	 * @throws OverheightException in case the HandlingUnit does not fit in the Location by this drop
	 * @throws OverlengthException in case the HandlingUnit does not fit in the Location by this drop
	 * @throws OverwidthException in case the HandlingUnit does not fit in the Location by this drop
	 */
	public void checkDimensionLimitExceeds(final Location location, final HandlingUnit handlingUnit)
			throws CapacityExceededException, WeightExceededException, OverheightException
				, OverlengthException, OverwidthException;
	/**
	 * Gets all the HandlingUnits possible to Pick from the Location
	 * 
	 * @param location the locationService to fetch the Picks for
	 * 
	 * @return the List of HandlingUnits
	 */
	public List<HandlingUnit> getAvailablePicks(final Location location);

}
