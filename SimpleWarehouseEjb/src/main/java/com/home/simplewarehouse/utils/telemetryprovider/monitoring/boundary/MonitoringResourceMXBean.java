package com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary;

import java.util.List;
import java.util.Map;

import com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity.Invocation;

/**
 * Definitions for the Monitoring Resource MXBean
 */
public interface MonitoringResourceMXBean {
	/**
	 * Gets a list of the slowest methods
	 * @return the list
	 */
	List<Invocation> getSlowestMethods();
	/**
	 * Provide diagnostics data
	 * @return the data
	 */
	Map<String, String> getDiagnostics();
	/**
	 * Provide an exception statistics
	 * @return the data
	 */
	Map<String, Integer> getExceptionStatistics();
	/**
	 * Gets the number of exceptions
	 * @return the count
	 */
	String getNumberOfExceptions();
	/**
	 * Gets the exceptions
	 * @return the exceptions
	 */
	String getExceptions();
	/**
	 * Clear the content
	 */
	void clear();
}
