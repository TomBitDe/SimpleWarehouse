package com.home.simplewarehouse.utils.configurator.jmxrestview;

import java.util.Map;

import com.home.simplewarehouse.patterns.singleton.simplecache.ValueSourceEntry;

/**
 * Defines the Configurator MXBean.
 */
public interface ConfiguratorMXBean {
	/**
	 * Gets all the Configuration entries as Map
	 * 
	 * @return the Map
	 */
	public Map<String, ValueSourceEntry> getConfigurationMap();
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
	 * Put this entry to the Configuration (add or replace value)
	 * 
	 * @param key the key value for this entry
	 * @param value the entries value
	 */
	public void putEntry(String key, String value);
	/**
	 * Delete the Configuration entry for the given key
	 * 
	 * @param key the key value
	 */
	public void deleteEntry(String key);
}