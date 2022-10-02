package com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary;

import java.util.List;
import java.util.Map;

import com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity.Invocation;

public interface MonitoringResourceMXBean {
	List<Invocation> getSlowestMethods();
	Map<String, String> getDiagnostics();
	Map<String, Integer> getExceptionStatistics();
	String getNumberOfExceptions();
	String getExceptions();
	void clear();
}
