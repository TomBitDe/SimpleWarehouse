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
	 * Gets a list of the slowest methods
	 * 
	 * @param maxResult maximum number of elements in the list
	 * 
	 * @return the list
	 */
    List<Invocation> getSlowestMethods(int maxResult);
	/**
	 * Provide diagnostics data
	 * @return the data
	 */
	Map<String, String> getDiagnostics();
	/**
	 * Provide diagnostics data for a given key
	 * 
	 * @param key the key value
	 * 
	 * @return the data
	 */
	String getDiagnosticsForKey(String key);
	/**
	 * Provide an exception statistics
	 * @return the data
	 */
	Map<String, Integer> getExceptionStatistics();
	/**
	 * Gets the Exception Statistics as String
	 * 
	 * @return the Exception Statistics
	 */
	String getExceptionStatisticsAsString();
	/**
	 * Provide an exception statistics as a String
	 * @return the data
	 */
	String getDiagnosticsAsString();
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
