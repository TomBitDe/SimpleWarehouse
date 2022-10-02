package com.home.simplewarehouse.utils.configurator.pluggable;

import java.util.Map;

public interface ConfigurationProvider {
	public Map<String, String> getConfiguration();
}
