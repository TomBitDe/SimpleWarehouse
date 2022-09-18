package com.home.simplewarehouse.telemetryprovider.monitoring.entity;

import java.util.List;
import java.util.Map;

public interface MonitoringRessourceMXBean {
	List<Invocation> getSlowestMethods();
	Map<String, String> getDiagnostics();
	Map<String, Integer> getExceptionStatistics();
	String getNumberOfExceptions();
	String getExceptions();
	void clear();
}
