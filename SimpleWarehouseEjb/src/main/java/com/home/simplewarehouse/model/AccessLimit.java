package com.home.simplewarehouse.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * How a handling unit placed on a location can be accessed.
 */
public enum AccessLimit {
	/**
	 * Location allows random access. A handling unit can be accessed at any time.
	 * No sequence has to be considered
	 */
	RANDOM,
	/**
	 * Location allows only first in first out access. Only this sequence is valid
	 */
	FIFO,
	/**
	 * Location allows only last in first out access. Only this sequence is valid
	 */
	LIFO;
	
	/**
	 * Sort AccessLimits ascending on value
	 * 
	 * @param map the map of AccessLimit and order number
	 *  
	 * @return a sorted TreeMap
	 */
	public static SortedMap<Integer, AccessLimit> sortFrom(Map<AccessLimit, Integer> map) {
		TreeMap<Integer, AccessLimit> ret = new TreeMap<>();
		
		List<Map.Entry<AccessLimit, Integer>> toSort = new ArrayList<>();
		
		for (Map.Entry<AccessLimit, Integer> entry : map.entrySet()) {
			toSort.add(entry);
		}

		Collections.sort(toSort, Comparator.comparing(Entry::getValue));
		
		for (Map.Entry<AccessLimit, Integer> entry : toSort) {
			ret.put(entry.getValue(), entry.getKey());
		}
		
		return ret;
	}
}
