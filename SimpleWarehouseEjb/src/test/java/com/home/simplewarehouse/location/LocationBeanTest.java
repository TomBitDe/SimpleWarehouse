package com.home.simplewarehouse.location;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
import com.home.simplewarehouse.handlingunit.HandlingUnitLocal;
import com.home.simplewarehouse.model.EntityBase;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the Location bean.
 */
@RunWith(Arquillian.class)
public class LocationBeanTest {
	private static final Logger LOG = LogManager.getLogger(LocationBeanTest.class);

	/**
	 * Configure the deployment.<br>
	 * Add all needed EJB interfaces and beans for the test.
	 * 
	 * @return the archive
	 */
	@Deployment
	public static JavaArchive createTestArchive() {
		LOG.trace("--> createTestArchive()");
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				/* Put the test-*.xml in JARs META-INF folder as *.xml */
				.addAsManifestResource(new File("src/test/resources/META-INF/test-persistence.xml"), "persistence.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-ejb-jar.xml"), "ejb-jar.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-glassfish-ejb-jar.xml"), "glassfish-ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(
						LocationLocal.class, LocationBean.class,
						HandlingUnitLocal.class, HandlingUnitBean.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		LOG.trace("<-- createTestArchive()");
		return archive;
	}

	@EJB
	LocationLocal locationLocal;
	
	@EJB
	HandlingUnitLocal handlingUnitLocal;
	
	/**
	 * Mandatory default constructor
	 */
	public LocationBeanTest() {
		super();
		// DO NOTHING HERE!
	}
	
	/**
	 * What to do before an individual test will be executed (each test)
	 */
	@Before
	public void beforeTest() {
		LOG.trace("--> beforeTest()");
		
		LOG.trace("<-- beforeTest()");		
	}
	
	/**
	 * What to do after an individual test will be executed (each test)
	 */
	@After
	public void afterTest() {
		LOG.trace("--> afterTest()");

		// Cleanup locations
		List<Location> locations = locationLocal.getAll();
		
		locations.stream().forEach(l -> locationLocal.delete(l));
		
		// Cleanup handling units
		List<HandlingUnit> handlingUnits = handlingUnitLocal.getAll();
		
		handlingUnits.stream().forEach(h -> handlingUnitLocal.delete(h));		

		LOG.trace("<-- afterTest()");
	}

	/**
	 * Simple location with no reference to handling units
	 */
	@Test
	@InSequence(0)
	public void create_getById() {
		LOG.info("--- Test create_getById");

		assertTrue(locationLocal.getAll().isEmpty());
		
		Location expLocation = new Location("A");

		locationLocal.create(expLocation);
		LOG.info("Location created: " + expLocation);

		// MANDATORY reread
		Location location = locationLocal.getById(expLocation.getLocationId());		
		LOG.info("Location getById: " + location);
		
		assertEquals(expLocation, location);
		assertEquals(EntityBase.USER_DEFAULT, location.getUpdateUserId());
		assertNotNull(location.getUpdateTimestamp());
		
	    // MANDATORY reread
		assertNull(locationLocal.getById("B"));
	}
	
	/**
	 * Test the delete, getById and create sequence
	 */
	@Test
	@InSequence(1)
	public void delete_getById_create() {
		LOG.info("--- Test delete_getById_create");

		assertTrue(locationLocal.getAll().isEmpty());
		
	    locationLocal.create(new Location("A"));

	    // MANDATORY reread
	    Location location = locationLocal.getById("A");
		LOG.info("Location getById: " + location);
		
		assertEquals("A", location.getLocationId());
		
		// Delete the location
		locationLocal.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		LOG.info("Location deleted: " + location.getLocationId());
		
		locationLocal.create(new Location("A", "Test"));

		// MANDATORY reread
		location = locationLocal.getById("A");				
		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertNotNull(location.getUpdateTimestamp());
		LOG.info("Location created: " + location);

		// Delete the location
		locationLocal.delete(location);
		LOG.info("Location deleted: " + location.getLocationId());
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		locationLocal.create(new Location("A", "Test", ts));

	    // MANDATORY reread
		location = locationLocal.getById("A");
		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertEquals(ts, location.getUpdateTimestamp());
		LOG.info("Location created: " + location);
	}

	/**
	 * Test the delete by location
	 */
	@Test
	@InSequence(2)
	public void deleteByLocation() {
		LOG.info("--- Test deleteByLocation");

		assertTrue(locationLocal.getAll().isEmpty());
		
	    locationLocal.create(new Location("A"));

	    Location location = locationLocal.getById("A");
		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		
		// Delete the location
		locationLocal.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());

		// MANDATORY reread
		assertNull(locationLocal.getById("A"));
		LOG.info("Location deleted: " + location.getLocationId());
		
	    // MANDATORY reread
		location = locationLocal.getById("A");
		assertNull(location);
		
		// Delete null
		location = null;
		locationLocal.delete(location);
		assertTrue(true);

		locationLocal.create(new Location("A"));
		location = locationLocal.getById("A");
		assertNotNull(location);
		location.setLocationId(null);
		locationLocal.delete(location);
		location = locationLocal.getById("A");
		assertNotNull(location);
	}
	
	/**
	 * Test getAll method
	 */
	@Test
	@InSequence(3)
	public void getAll() {
		LOG.info("--- Test getAll");
		
		assertTrue(locationLocal.getAll().isEmpty());
		
		// Prepare some locations; 5 locations
		locationLocal.create(new Location("A", "Test"));
		locationLocal.create(new Location("B", "Test"));
		locationLocal.create(new Location("C", "Test"));
		locationLocal.create(new Location("D", "Test"));
		locationLocal.create(new Location("E", "Test"));

		// Get them all and output
		List<Location> locations = locationLocal.getAll();
		locations.stream().forEach( l -> LOG.info(l.toString()) );

		assertFalse(locations.isEmpty());
		assertEquals(5, locations.size());
	}
	
	/**
	 * Test delete a location with handling units on it
	 */
	@Test
	@InSequence(4)
	public void deleteLocationWithHandlingUnits() {
		LOG.info("--- Test deleteLocationWithHandlingUnits");
		
		assertTrue(locationLocal.getAll().isEmpty());
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		
		// Prepare a location
		locationLocal.create(new Location("A", "Test"));
		Location locA = locationLocal.getById("A");
		LOG.info("Location prepared: " + locA);
		
		// Drop to make a relation
		try {
			handlingUnitLocal.dropTo(locA, new HandlingUnit("1", "Test"));

			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, new HandlingUnit("2", "Test"));

			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, new HandlingUnit("3", "Test"));

			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, new HandlingUnit("4", "Test"));

			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, new HandlingUnit("5", "Test"));

			// MANDATORY reread
			locA = locationLocal.getById("A");
			assertNotNull(locA);
			assertFalse(locA.getHandlingUnits().isEmpty());
			assertEquals(5, locA.getHandlingUnits().size());

			assertFalse(locA.getHandlingUnits().contains(new HandlingUnit("12")));

			LOG.info("5 HandlingUnits dropped to " + locA.getLocationId() + "   " + locA);

			LOG.info("Sample hU2 and hU5");
			// MANDATORY reread
			HandlingUnit hU2 = handlingUnitLocal.getById("2");
			LOG.info(hU2);
			// MANDATORY reread
			HandlingUnit hU5 = handlingUnitLocal.getById("5");
			LOG.info(hU5);

			// Now delete the location
			// MANDATORY reread
			locA = locationLocal.getById("A");
			locationLocal.delete(locA);
			LOG.info("Location deleted: " + locA.getLocationId());

			// MANDATORY reread
			hU2 = handlingUnitLocal.getById("2");
			hU5 = handlingUnitLocal.getById("5");

			assertNotNull(hU2);
			assertNotNull(hU5);

			assertNull(hU2.getLocation());
			assertNull(hU5.getLocation());

			LOG.info("Sample hU2 and hU5 have no longer a location");
			LOG.info(hU2);
			LOG.info(hU5);
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);			
		}
	}

	/**
	 * Test delete a location with one single handling unit on it
	 */
	@Test
	@InSequence(5)
	public void deleteLocationWithOneHandlingUnit() {
		LOG.info("--- Test deleteLocationWithOneHandlingUnit");
		
		assertTrue(locationLocal.getAll().isEmpty());
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		
		// Prepare a location
		locationLocal.create(new Location("A", "Test"));
		Location locA = locationLocal.getById("A");
		
		// Test the special toString also
		assumeTrue(locA.toString().contains("HandlingUnits RANDOM=[]"));
		
		LOG.info("Location prepared: " + locA);
		
		// Drop to make a relation
		try {
			handlingUnitLocal.dropTo(locA, new HandlingUnit("8", "Test"));
		
			// MANDATORY reread
			locA = locationLocal.getById("A");
			assertNotNull(locA);	
			assertFalse(locA.getHandlingUnits().isEmpty());
			assertEquals(1, locA.getHandlingUnits().size());

			// Test the special toString also
			assertTrue(locA.toString().contains("HandlingUnits RANDOM=[\"8\"]"));

			assertFalse(locA.getHandlingUnits().contains(new HandlingUnit("2")));
		
			LOG.info("1 HandlingUnit dropped to " + locA.getLocationId() + "   " + locA);

			LOG.info("Sample hU8");
			// MANDATORY reread
			HandlingUnit hU8 = handlingUnitLocal.getById("8");
			LOG.info(hU8);
		
			// Now delete the location
			// MANDATORY reread
			locA = locationLocal.getById("A");
			locationLocal.delete(locA);
			LOG.info("Location deleted: " + locA.getLocationId());
		
			// MANDATORY reread
			hU8 = handlingUnitLocal.getById("8");
		
			assertNotNull(hU8);
		
			assertNull(hU8.getLocation());
		
			LOG.info("Sample hU1 has no longer a location");
			LOG.info(hU8);
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);			
		}
	}
	
	/**
	 * Test get all locations with free capacity
	 */
	@Test
	@InSequence(8)
	public void getAllWithFreeCapacity() {
		LOG.info("--- Test getAllWithFreeCapacity");
		
		assertTrue(locationLocal.getAll().isEmpty());

		locationLocal.create(new Location("A"));
		locationLocal.create(new Location("B"));
		locationLocal.create(new Location("C"));
		locationLocal.create(new Location("D"));
		

		LOG.info("Locations created: " + locationLocal.getAll().size());
		
		List<Location> freeCapacityLocations = locationLocal.getAllWithFreeCapacity();
		
		assertEquals(4, freeCapacityLocations.size());

		Location expLocation = locationLocal.getById("C");
		assertTrue(freeCapacityLocations.contains(expLocation));
	}

	/**
	 * Test get handling units on a location
	 */
	@Test
	@InSequence(8)
	public void getHandlingUnits() {
		LOG.info("--- Test getHandlingUnits");
		
		assertTrue(locationLocal.getAll().isEmpty());

		locationLocal.create(new Location("A"));

		LOG.info("Locations created: " + locationLocal.getAll().size());
		Location locA = locationLocal.getById("A");
		
		// Drop to make a relation
		try {
			handlingUnitLocal.dropTo(locA, new HandlingUnit("1", "Test"));

			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, new HandlingUnit("2", "Test"));

			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, new HandlingUnit("3", "Test"));

			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, new HandlingUnit("4", "Test"));

			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, new HandlingUnit("5", "Test"));
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);			
		}

		// MANDATORY reread
		locA = locationLocal.getById("A");
		assertNotNull(locA);
		assertFalse(locationLocal.getHandlingUnits(locA).isEmpty());
		assertEquals(5, locationLocal.getHandlingUnits(locA).size());
		
		try {
			locationLocal.getHandlingUnits(null);
			
			Assert.fail("Exception expected");
		}
		catch (EJBException iaex) {
			LOG.info("Expected exception: " + iaex.getMessage());
		}
		catch (Exception ex) {
			Assert.fail("Not expected: " + ex);						
		}
		
		locA.setLocationId(null);
		try {
			locationLocal.getHandlingUnits(locA);
			
			Assert.fail("Exception expected");
		}
		catch (EJBException iaex) {
			LOG.info("Expected exception: " + iaex.getMessage());
		}
		catch (Exception ex) {
			Assert.fail("Not expected: " + ex);						
		}
	}

	/**
	 * Test get available picks on a location
	 */
	@Test
	@InSequence(11)
	public void getAvailablePicks() {
		LOG.info("--- Test getAvailablePicks");
		
		assertTrue(locationLocal.getAll().isEmpty());

		locationLocal.create(new Location("A"));

		LOG.info("Locations created: " + locationLocal.getAll().size());
		Location locA = locationLocal.getById("A");
		
		// Drop to make a relation
		try {
			handlingUnitLocal.dropTo(locA, new HandlingUnit("1", "Test"));

			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, new HandlingUnit("2", "Test"));
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);			
		}
		
		// MANDATORY reread
		locA = locationLocal.getById("A");
		assertNotNull(locA);
		assertFalse(locationLocal.getAvailablePicks(locA).isEmpty());
		assertEquals(2, locationLocal.getAvailablePicks(locA).size());
		
		try {
			locationLocal.getAvailablePicks(null);
			
			Assert.fail("Exception expected");
		}
		catch (EJBException iaex) {
			LOG.info("Expected exception: " + iaex.getMessage());
		}
		catch (Exception ex) {
			Assert.fail("Not expected: " + ex);						
		}
		
		locA.setLocationId(null);
		try {
			locationLocal.getAvailablePicks(locA);
			
			Assert.fail("Exception expected");
		}
		catch (EJBException iaex) {
			LOG.info("Expected exception: " + iaex.getMessage());
		}
		catch (Exception ex) {
			Assert.fail("Not expected: " + ex);						
		}
	}

	/**
	 * Test get all full locations
	 */
	@Test
	@InSequence(14)
	public void getAllFull() {
		LOG.info("--- Test getAllFull");
		
		assertTrue(locationLocal.getAll().isEmpty());

		locationLocal.create(new Location("A"));

		LOG.info("Locations created: " + locationLocal.getAll().size());
		Location locA = locationLocal.getById("A");
		
		assertTrue(locationLocal.getAllFull().isEmpty());
		
		locA.getDimension().setMaxCapacity(2);
		
		// Drop to make a relation
		try {
			handlingUnitLocal.dropTo(locA, new HandlingUnit("1", "Test"));

			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, new HandlingUnit("2", "Test"));
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);			
		}

		// MANDATORY reread
		locA = locationLocal.getById("A");
		assertNotNull(locA);
		assertFalse(locationLocal.getAllFull().isEmpty());
		assertEquals(1, locationLocal.getAllFull().size());
	}

	/**
	 * Test add handling unit only exceptional cases
	 */
	@Test
	@InSequence(17)
	public void addHandlingUnit() {
		LOG.info("--- Test addHandlingUnit");
		
		assertTrue(locationLocal.getAll().isEmpty());

		locationLocal.create(new Location("A"));

		LOG.info("Locations created: " + locationLocal.getAll().size());
		Location locA = locationLocal.getById("A");
		
		assertTrue(locationLocal.getAllFull().isEmpty());
		
		boolean ret = locationLocal.addHandlingUnit(locA, null);
		assertFalse(ret);
		
		// Drop to make a relation
		try {
			locationLocal.addHandlingUnit(null, new HandlingUnit("1", "Test"));
			
			Assert.fail("Exception expected");
		}
		catch (EJBException iaex) {
			LOG.info("Expected exception: " + iaex.getMessage());
		}
		catch (Exception ex) {
			Assert.fail("Not expected: " + ex);						
		}

		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		locA.setLocationId(null);
		try {
			locationLocal.addHandlingUnit(locA, hU1);
			
			Assert.fail("Exception expected");
		}
		catch (EJBException iaex) {
			LOG.info("Expected exception: " + iaex.getMessage());
		}
		catch (Exception ex) {
			Assert.fail("Not expected: " + ex);						
		}
	}
}
