package com.home.simplewarehouse.utils.rest;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;

/**
 * Test the RESTConfig class.
 */
public class RESTConfigTest {
	/**
	 * Test creation of config
	 */
	@Test
	@InSequence(0)
	public void testConfig() {
		RESTConfig config = new RESTConfig();
		
		assertNotNull(config);
		
		Map<String, Object> props = config.getProperties();
		
		assertNotNull(props);
		
		assertTrue(props.isEmpty());
		
		Set<Object> singletons = config.getSingletons();
		
		assertTrue(singletons.isEmpty());
	}
}
