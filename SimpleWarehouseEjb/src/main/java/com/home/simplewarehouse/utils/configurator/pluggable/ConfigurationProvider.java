package com.home.simplewarehouse.utils.configurator.pluggable;

import java.util.Map;

/**
 * Defines the configuration provider.
 */
public interface ConfigurationProvider {
	/**
	 * Gets the configuration
	 * 
	 * @return the configuration
	 */
	public Map<String, String> getConfiguration();
}
