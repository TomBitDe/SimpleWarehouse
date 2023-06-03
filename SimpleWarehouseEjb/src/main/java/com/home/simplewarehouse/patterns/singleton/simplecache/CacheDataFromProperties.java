package com.home.simplewarehouse.patterns.singleton.simplecache;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Load data for a cache from a properties file.<br>
 * Since this are application global data the file name is 'globals.properties'.<br>
 */
@Stateless(name = "CacheDataFromProperties")
@Local(com.home.simplewarehouse.patterns.singleton.simplecache.CacheDataProvider.class) 
public class CacheDataFromProperties implements CacheDataProvider {
	private static final Logger LOG = LogManager.getLogger(CacheDataFromProperties.class);
	private static final String GLOBAL_PROPS = "globals.properties";

	/**
	 * Default constructor.
	 */
	public CacheDataFromProperties() {
		super();
		LOG.trace("--> CacheDataFromProperties");
		LOG.trace("<-- CacheDataFromProperties");
	}

	@Override
	public Map<String, ValueSourceEntry> loadCacheData() {
		LOG.trace("--> loadCacheData");

		Map<String, ValueSourceEntry> map = new HashMap<>();

		InputStream inputStream = null;

		try {
			LOG.debug("Load cache from [{}]", GLOBAL_PROPS);

			inputStream = this.getClass().getClassLoader().getResourceAsStream(GLOBAL_PROPS);

	        if (inputStream == null) {
	        	LOG.warn("InputStream is [{}]: check if [{}] is available!", inputStream, GLOBAL_PROPS);
	        }
	        else {
		        LOG.debug("InputStream is available!");

		        Properties properties = new Properties();

				properties.load(inputStream);
				inputStream.close();

				properties.forEach((key, val) -> map.put((String) key, new ValueSourceEntry((String)val, "Properties")));
				LOG.debug("{} = {}", GLOBAL_PROPS, map);
	        }
		}
        catch (IOException e) {
			LOG.error(e.getMessage());
		}

		LOG.trace("<-- loadCacheData");

		return map;
	}
}
