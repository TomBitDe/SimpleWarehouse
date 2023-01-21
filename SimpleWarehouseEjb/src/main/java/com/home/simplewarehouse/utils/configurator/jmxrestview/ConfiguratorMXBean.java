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
	Map<String, String> getConfigurationMap();
}
