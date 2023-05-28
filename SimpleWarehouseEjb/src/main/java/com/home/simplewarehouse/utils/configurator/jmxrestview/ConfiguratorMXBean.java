package com.home.simplewarehouse.utils.configurator.jmxrestview;

import java.util.Map;

/**
 * Defines the Configurator MXBean.
 */
public interface ConfiguratorMXBean {
	/**
	 * Gets all the Configuration entries as Map
	 * 
	 * @return the Map
	 */
	public Map<String, String> getConfigurationMap();
	/**
	 * Gets all the Configuration entries as a single String
	 * 
	 * @return the Configuration as String
	 */
	public String getConfiguration();
	/**
	 * Gets the Configuration entry for the given key
	 * 
	 * @param key the key value
	 * 
	 * @return the entry as String
	 */
	public String getEntry(String key);
	/**
	 * Gets the Configuration entry for the given key
	 * 
	 * @param key the key value
	 * @param defaultValue the value returned if nothing found for the key
	 * 
	 * @return the entry or the defaultValue as String
	 */
	public String getEntry(String key, String defaultValue);
	/**
	 * Add this entry to the Configuration
	 * 
	 * @param key the key value for this entry
	 * @param value the entries value
	 */
	public void addEntry(String key, String value);
	/**
	 * Delete the Configuration entry for the given key
	 * 
	 * @param key the key value
	 */
	public void deleteEntry(String key);
}