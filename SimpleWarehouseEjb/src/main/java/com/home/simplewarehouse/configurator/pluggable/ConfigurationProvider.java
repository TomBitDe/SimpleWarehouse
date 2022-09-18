package com.home.simplewarehouse.configurator.pluggable;

import java.util.Map;

public interface ConfigurationProvider {
	public Map<String, String> getConfiguration();
}
