package com.home.simplewarehouse.topology;

import com.home.simplewarehouse.patterns.exceptions.BusinessException;

/**
 * Local interface for Sample Warehouse usage.
 */
public interface SampleWarehouseService {
	/**
	 * Number of HandlingUnits to create
	 */
	public static final int HANDLING_UNIT_NUM = 50;
	/**
	 * Number of Locations per type (Random, FiFo, LiFo) to create
	 */
	public static final int LOCATION_NUM = 26;
	
	/**
	 * Create sample warehouse topology data.
	 */
	public void initialize() throws BusinessException;
	/**
	 * Cleanup the sample warehouse topology data.
	 */
	public void cleanup() throws BusinessException;
}
