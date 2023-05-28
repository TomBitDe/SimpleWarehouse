package com.home.simplewarehouse.patterns.singleton.simplecache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.patterns.singleton.simplecache.model.ApplConfig;

/**
 * Load data for a cache from a DB table.<br>
 * Since this are application global data the DB table is 'ApplConfig (APPL_CONFIG)'.<br>
 */
@Stateless(name = "CacheDataFromDbTable")
@Local(com.home.simplewarehouse.patterns.singleton.simplecache.CacheDataProvider.class)
public class CacheDataFromDbTable implements CacheDataProvider {
	private static final Logger LOG = LogManager.getLogger(CacheDataFromDbTable.class);

	@EJB
	ApplConfigManager applConfigManager;

	/**
	 * Default constructor.
	 */
	public CacheDataFromDbTable() {
		super();
		LOG.trace("--> CacheDataFromDbTable");
		LOG.trace("<-- CacheDataFromDbTable");
	}

	@Override
	public Map<String, String> loadCacheData() {
		LOG.trace("--> loadCacheData");

		Map<String, String> map = new HashMap<>();

		List<ApplConfig> configList = applConfigManager.getAll();

		for (ApplConfig entry : configList) {
			map.put(entry.getKeyVal(), entry.getParamVal());
		}
		LOG.info("Global configurations = {}", map);

		LOG.trace("<-- loadCacheData");

		return map;
	}
}
