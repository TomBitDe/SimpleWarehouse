package com.home.simplewarehouse.model;

import static org.junit.Assert.assertEquals;

import java.util.EnumMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test the Access Limit Enum.
 */
@RunWith(JUnit4.class)
public class AccessLimitTest {
	private static final Logger LOG = LogManager.getLogger(AccessLimitTest.class);
	
	/**
	 * Mandatory default constructor
	 */
	public AccessLimitTest() {
		super();
		// DO NOTHING HERE!
	}
	
	/**
	 * Test sorting
	 */
	@Test
	public void sortFromTest() {
		EnumMap<AccessLimit, Integer> accessLimitForSort = new EnumMap<AccessLimit, Integer>(AccessLimit.class);

		accessLimitForSort.put(AccessLimit.RANDOM, 3);
		accessLimitForSort.put(AccessLimit.FIFO, 1);
		accessLimitForSort.put(AccessLimit.LIFO, 2);

		LOG.info("Keys={} Values={}", accessLimitForSort.keySet(), accessLimitForSort.values());

		TreeMap<Integer, AccessLimit> sorted = AccessLimit.sortFrom(accessLimitForSort);
		
		assertEquals(3, sorted.values().size());
		assertEquals(AccessLimit.FIFO, sorted.get(1));
		assertEquals(AccessLimit.LIFO, sorted.get(2));
		assertEquals(AccessLimit.RANDOM, sorted.get(3));
		
		accessLimitForSort.put(AccessLimit.RANDOM, 2);
		accessLimitForSort.put(AccessLimit.FIFO, 2);
		accessLimitForSort.put(AccessLimit.LIFO, 1);

		sorted = AccessLimit.sortFrom(accessLimitForSort);
		
		assertEquals(2, sorted.values().size());
		assertEquals(AccessLimit.LIFO, sorted.get(1));
		assertEquals(AccessLimit.FIFO, sorted.get(2));
	}
}
