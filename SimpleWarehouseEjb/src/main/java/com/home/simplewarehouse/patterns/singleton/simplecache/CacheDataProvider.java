package com.home.simplewarehouse.patterns.singleton.simplecache;

import java.util.Map;

/**
 * Cache Data Provider interface definition.
 */
public interface CacheDataProvider {
	/**
	 * Load the cache data from any source
	 *
	 * @return a Map of data for the cache
	 */
	public Map<String, ValueSourceEntry> loadCacheData();
}
