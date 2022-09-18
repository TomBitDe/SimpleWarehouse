package com.home.simplewarehouse.singleton.simplecache;

/**
 * Configuration Cache interface definition.
 */
public interface ConfigCache {
	/**
	 * Get the value for a key
	 *
	 * @param key do the lookup for this entry
	 *
	 * @return null if the value for the given key does not exist else the value
	 */
	public String getData(String key);

	/**
	 * Get the value for a key
	 *
	 * @param key         do the lookup for this entry
	 * @param defaultVal  if a value for the given key does not exist return this value
	 *
	 * @return defaultVal if the value for the given key does not exist else the value
	 */
	public String getData(String key, String defaultVal);

	/**
	 * Get the value for a key
	 *
	 * @param key         do the lookup for this entry
	 * @param defaultVal  if a value for the given key does not exist return this value
	 *
	 * @return defaultVal if the value for the given key does not exist else the value
	 */
	public int getData(String key, int defaultVal);

	/**
	 * Get the value for a key
	 *
	 * @param key         do the lookup for this entry
	 * @param defaultVal  if a value for the given key does not exist return this value
	 *
	 * @return defaultVal if the value for the given key does not exist else the value
	 */
	public long getData(String key, long defaultVal);

	/**
	 * Refresh the cache data from source
	 */
	public void refresh();
}
