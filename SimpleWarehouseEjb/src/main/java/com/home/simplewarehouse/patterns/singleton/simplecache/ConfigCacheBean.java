package com.home.simplewarehouse.patterns.singleton.simplecache;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Config Cash access providing class.
 */
@Startup
@Local(ConfigCache.class)
@Singleton
public class ConfigCacheBean implements ConfigCache {
	private static final Logger LOG = LogManager.getLogger(ConfigCacheBean.class);
	
	private static final String KEY_DEFAULT_VALUE_FORMATTER = "Key=[{}] DefaultValue=[{}]";
	private static final String VALUE_FORMATTER = "Value=[{}]";

	private Map<String, ValueSourceEntry> cache;

	@EJB
	private CacheDataProvider cacheDataProvider;

	/**
	 * Create the Configuration Cache Bean
	 */
	public ConfigCacheBean() {
		super();
		LOG.trace("--> ConfigCacheBean");
		LOG.trace("<-- ConfigCacheBean");
	}

	/**
	 * Refresh the cache data REST controlled
	 */
	@Override
	public void refresh() {
		LOG.trace("--> refresh");

		cache = createFreshCache();

		LOG.trace("<-- refresh");
	}

	/**
	 * Populate the cache data for the application timer controlled
	 */
	@Schedule(hour="*", minute="*", second="*/3", persistent = false)
	@PostConstruct
	private void populateCache() {
		LOG.trace("--> populateCache");

		cache = createFreshCache();

		LOG.trace("<-- populateCache");
	}

	/**
	 * Load the data into the cache
	 *
	 * @return the data map for the cache
	 */
	private Map<String, ValueSourceEntry> createFreshCache() {
		LOG.trace("--> createFreshCache");

		Map<String, ValueSourceEntry> map = cacheDataProvider.loadCacheData();

		LOG.debug("-------- ConfigCache data --------");
		if (map.isEmpty()) {
			LOG.debug("EMPTY!");
		}
		else {
			map.forEach((k,v) -> LOG.debug("Key=[{}] Value=[{}]", k, v));
		}

		LOG.trace("<-- createFreshCache");

		return map;
	}

	@Override
	@Lock(LockType.READ)
	public String getData(String key) {
		LOG.trace("Key=[{}]", key);
		
		String val = null;
		
		if (cache.get(key) != null) {
			val = cache.get(key).getValue();
		}
		
		LOG.trace(VALUE_FORMATTER, val);

	    return val;
	}

	@Override
	@Lock(LockType.READ)
	public String getData(String key, String defaultValue) {
		LOG.trace(KEY_DEFAULT_VALUE_FORMATTER, key, defaultValue);

		String val = null;
		
		if (cache.get(key) != null) {
			val = cache.get(key).getValue();
		}
		
		if (val == null) {
			val = defaultValue;
		}

		LOG.trace(VALUE_FORMATTER, val);

	    return val;
	}

	@Override
	@Lock(LockType.READ)
	public int getData(String key, int defaultValue) {
		LOG.trace(KEY_DEFAULT_VALUE_FORMATTER, key, defaultValue);

		int val;

		try {
			if (cache.get(key) != null) {
				val = Integer.valueOf(cache.get(key).getValue());
			}
			else {
				val = defaultValue;
			}
		}
		catch(NumberFormatException nex) {
			val = defaultValue;
		}

		LOG.trace(VALUE_FORMATTER, val);

	    return val;
	}

	@Override
	@Lock(LockType.READ)
	public long getData(String key, long defaultValue) {
		LOG.trace(KEY_DEFAULT_VALUE_FORMATTER, key, defaultValue);

		long val;

		try {
			if (cache.get(key) != null) {
				val = Integer.valueOf(cache.get(key).getValue());
			}
			else {
				val = defaultValue;
			}
		}
		catch(NumberFormatException nex) {
			val = defaultValue;
		}

		LOG.trace(VALUE_FORMATTER, val);

	    return val;
	}
}
