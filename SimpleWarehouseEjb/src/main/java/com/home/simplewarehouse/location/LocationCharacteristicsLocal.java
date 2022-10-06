package com.home.simplewarehouse.location;

import java.util.List;

import com.home.simplewarehouse.model.LocationCharacteristics;

/**
 * Local interface for Location Characteristics usage.
 */
public interface LocationCharacteristicsLocal {
	/**
	 * Get a LocationCharacteristics by its id
	 * 
	 * @param id the id
	 * 
	 * @return the corresponding LocationCharacteristics
	 */
	public LocationCharacteristics getById(final String id);
	/**
	 * Get a list of all LocationCharacteristics items
	 * 
	 * @return the LocationCharacteristics list
	 */
	public List<LocationCharacteristics> getAll();
}
