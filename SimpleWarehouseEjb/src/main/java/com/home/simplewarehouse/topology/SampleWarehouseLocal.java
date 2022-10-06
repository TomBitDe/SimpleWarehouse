package com.home.simplewarehouse.topology;

/**
 * Local interface for Sample Warehouse usage.
 */
public interface SampleWarehouseLocal {
	public static final int HANDLING_UNIT_NUM = 50;
	public static final int LOCATION_NUM = 26;
	
	/**
	 * Create sample warehouse topology data.
	 */
	public void initialize();
	/**
	 * Cleanup the sample warehouse topology data.
	 */
	public void cleanup();
}
