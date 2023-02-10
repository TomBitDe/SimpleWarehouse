package com.home.simplewarehouse.location;

import java.util.List;
import java.util.stream.Collectors;

import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.HeightCategory;
import com.home.simplewarehouse.model.LengthCategory;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.WidthCategory;

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
	 * Gets a list of all HandlingUnits on a location
	 * 
	 * @param location the given location
	 * 
	 * @return a list of HandlingUnits
	 */
	public List<HandlingUnit> getHandlingUnits(Location location);
	
	public void setHandlingUnits(Location location, List<HandlingUnit> handlingUnits);
	
	public boolean addHandlingUnit(Location location, HandlingUnit handlingUnit);
	
	public boolean removeHandlingUnit(Location location, HandlingUnit handlingUnit);

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
	/**
	 * Check if a Location is fully occupied related to its capacity
	 * 
	 * @param location the location to check
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
	 * @param location the location to check
	 * @param weight the additional weight
	 * 
	 * @return true if the additional weight will exceed the weight limit else false
	 */
	public boolean overweight(final Location location, final int weight);
	/**
	 * Check if a Location fits related to its maximum height
	 * 
	 * @param location the location to check
	 * @param height the height to drop
	 * 
	 * @return true if the height does not fit else false
	 */
	public boolean overheight(final Location location, final HeightCategory height);
	/**
	 * Check if a Location fits related to its maximum length
	 * 
	 * @param location the location to check
	 * @param length the length to drop
	 * 
	 * @return true if the length does not fit else false
	 */
	public boolean overlength(final Location location, final LengthCategory length);
	/**
	 * Check if a Location fits related to its maximum width
	 * 
	 * @param location the location to check
	 * @param width the width to drop
	 * 
	 * @return true if the width does not fit else false
	 */
	public boolean overwidth(final Location location, final WidthCategory width);
	/**
	 * Check if a dimension limit exceeds when dropping a HandlingUnit on a Location
	 * 
	 * @param location the location to check
	 * @param handlingUnit the handlingUnit to drop
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
	 * @return the List of HandlingUnits
	 */
	public List<HandlingUnit> getAvailablePicks(Location location);

}
