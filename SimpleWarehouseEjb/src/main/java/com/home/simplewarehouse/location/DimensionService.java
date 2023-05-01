package com.home.simplewarehouse.location;

import java.rmi.Remote;
import java.util.List;

import com.home.simplewarehouse.model.Dimension;

/**
 * Local interface for Dimension usage.
 */
public interface DimensionService extends Remote {
	/**
	 * Get a Dimension by its id
	 * 
	 * @param id the id
	 * 
	 * @return the corresponding Dimension
	 */
	public Dimension getById(final String id);
	/**
	 * Get a list of all Dimension items
	 * 
	 * @return the Dimension list
	 */
	public List<Dimension> getAll();
}
