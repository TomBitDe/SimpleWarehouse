package com.home.simplewarehouse.utils.configurator.jmxrestview;

import java.util.Map;

/**
 * Defines the ConfiguratorMXBean.
 */
public interface ConfiguratorMXBean {
	/**
	 * Gets the Configuration Map
	 * 
	 * @return the Map
	 */
	public Map<String, String> getConfigurationMap();
	/**
	 * Gets the Configuration
	 * 
	 * @return the Configuration as String
	 */
	public String getConfiguration();
	/**
	 * Gets the Configuation entry for the given key
	 * 
	 * @param key the key value
	 * 
	 * @return the entry as String
	 */
	public String getEntry(String key);
	/**
	 * Add this entry to the Configuration
	 * 
	 * @param key the key value for this entry
	 * @param value the entries value
	 * @param uriInfo the Uri info
	 */
	public void addEntry(String key, String value);
	/**
	 * Delete the configuration entry for the given key
	 * 
	 * @param key the key value
	 */
	public void deleteEntry(String key);
}