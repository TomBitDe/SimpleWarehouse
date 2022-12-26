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
import com.home.simplewarehouse.handlingunit.HandlingUnitNotOnLocationException;
import com.home.simplewarehouse.handlingunit.LocationIsEmptyException;
import com.home.simplewarehouse.model.EntityBase;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.FifoLocation;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the FIFO Location.
 */
@RunWith(Arquillian.class)
public class FifoLocationTest {
	private static final Logger LOG = LogManager.getLogger(FifoLocationTest.class);

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
	public FifoLocationTest() {
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
		
		FifoLocation expLocation = new FifoLocation("A");

		locationLocal.create(expLocation);
		LOG.info("Fifo Location created: " + expLocation);

		// MANDATORY reread
		Location location = locationLocal.getById(expLocation.getLocationId());		
		LOG.info("FifoLocation getById: " + location);
		
		assertEquals(expLocation, location);
		assertEquals(EntityBase.USER_DEFAULT, location.getUpdateUserId());
		assertNotNull(location.getUpdateTimestamp());
		
	    // Should be null because never created
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
		
	    locationLocal.create(new FifoLocation("A"));

	    // MANDATORY reread
	    Location location = locationLocal.getById("A");
		LOG.info("Fifo Location getById: " + location);
		
		assertEquals("A", location.getLocationId());
		
		// Delete the location
		locationLocal.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		LOG.info("Fifo Location deleted: " + location.getLocationId());
		
		locationLocal.create(new FifoLocation("A", "Test"));

		// MANDATORY reread
		location = locationLocal.getById("A");				
		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertNotNull(location.getUpdateTimestamp());
		LOG.info("Fifo Location created: " + location);

		// Delete the location
		locationLocal.delete(location);
		LOG.info("Fifo Location deleted: " + location.getLocationId());
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		locationLocal.create(new FifoLocation("A", "Test", ts));

	    // MANDATORY reread
		location = locationLocal.getById("A");
		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertEquals(ts, location.getUpdateTimestamp());
		LOG.info("Fifo Location created: " + location);
	}

	/**
	 * Test the delete by location
	 */
	@Test
	@InSequence(2)
	public void deleteByLocation() {
		LOG.info("--- Test deleteByLocation");

		assertTrue(locationLocal.getAll().isEmpty());
		
	    locationLocal.create(new FifoLocation("A"));

	    Location location = locationLocal.getById("A");
		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		
		// Delete the location
		locationLocal.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());

		// MANDATORY reread
		assertNull(locationLocal.getById("A"));
		LOG.info("Fifo Location deleted: " + location.getLocationId());
		
	    // MANDATORY reread
		location = (FifoLocation) locationLocal.getById("A");
		assertNull(location);
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
		locationLocal.create(new FifoLocation("A", "Test"));
		locationLocal.create(new FifoLocation("B", "Test"));
		locationLocal.create(new FifoLocation("C", "Test"));
		locationLocal.create(new FifoLocation("D", "Test"));
		locationLocal.create(new FifoLocation("E", "Test"));

		// Get them all and output
		List<Location> locations = locationLocal.getAll();
		// locations.stream().forEach( l -> LOG.info(l.toString()) );

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
		locationLocal.create(new FifoLocation("A", "Test"));
		Location locA = locationLocal.getById("A");
		LOG.info("Fifo Location prepared: " + locA);
		
		try {
			// Drop to make a relation
			HandlingUnit hU1 = new HandlingUnit("1", "Test");
			handlingUnitLocal.dropTo(locA, hU1);

			HandlingUnit hU2 = new HandlingUnit("2", "Test");
			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, hU2);

			HandlingUnit hU3 = new HandlingUnit("3", "Test");
			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, hU3);

			HandlingUnit hU4 = new HandlingUnit("4", "Test");
			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, hU4);

			HandlingUnit hU5 = new HandlingUnit("5", "Test");
			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, hU5);

			// MANDATORY reread
			locA = locationLocal.getById("A");
			assertNotNull(locA);
			assertFalse(locA.getHandlingUnits().isEmpty());
			assertEquals(5, locA.getHandlingUnits().size());

			assertFalse(locA.getHandlingUnits().contains(new HandlingUnit("12")));

			LOG.info("5 HandlingUnits dropped to " + locA.getLocationId() + "   " + locA);

			LOG.info("Sample hU2 and hU5");
			// MANDATORY reread
			hU2 = handlingUnitLocal.getById("2");
			LOG.info(hU2);
			// MANDATORY reread
			hU5 = handlingUnitLocal.getById("5");
			LOG.info(hU5);

			// Now delete the location
			// MANDATORY reread
			locA = locationLocal.getById("A");
			locationLocal.delete(locA);
			LOG.info("Fifo Location deleted: " + locA.getLocationId());

			// MANDATORY reread
			hU2 = handlingUnitLocal.getById("2");
			hU5 = handlingUnitLocal.getById("5");

			assertNotNull(hU2);
			assertNotNull(hU5);

			LOG.info("Sample hU2 and hU5 have no longer a location and locaPos");
			assertNull(hU2.getLocation());
			assertNull(hU2.getLocaPos());
			assertNull(hU5.getLocation());
			assertNull(hU5.getLocaPos());

			LOG.info(hU2);
			LOG.info(hU5);
		}
		catch (CapacityExceededException | WeightExceededException dimex) {
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
		locationLocal.create(new FifoLocation("A", "Test"));
		Location locA = locationLocal.getById("A");
		
		// Test the special toString also
		assumeTrue(locA.toString().contains("HandlingUnits FIFO=[]"));
		
		LOG.info("Fifo Location prepared: " + locA);
		
		// Drop to make a relation
		HandlingUnit hU8 = new HandlingUnit("8", "Test");
		
		try {
			handlingUnitLocal.dropTo(locA, hU8);

			// MANDATORY reread
			locA = (FifoLocation) locationLocal.getById("A");
			assertNotNull(locA);
			assertFalse(locA.getHandlingUnits().isEmpty());
			assertEquals(1, locA.getHandlingUnits().size());

			// Test the special toString also
			assertTrue(locA.toString().contains("HandlingUnits FIFO=[\"8 Pos 1\"]"));

			assertFalse(locA.getHandlingUnits().contains(new HandlingUnit("2")));

			LOG.info("1 HandlingUnit dropped on " + locA.getLocationId() + locA);

			// MANDATORY reread
			hU8 = handlingUnitLocal.getById("8");
			assertNotNull(hU8.getLocation());
			assertNotNull(hU8.getLocaPos());
			LOG.info("Sample hU8 {}", hU8);

			// Now delete the location
			// MANDATORY reread
			locA = locationLocal.getById("A");
			locationLocal.delete(locA);
			LOG.info("Fifo Location deleted: " + locA.getLocationId());

			// MANDATORY reread
			hU8 = handlingUnitLocal.getById("8");

			assertNotNull(hU8);

			assertNull(hU8.getLocation());
			assertNull(hU8.getLocaPos());

			LOG.info("Sample hU8 has no longer a location {}", hU8);
		}
		catch (CapacityExceededException | WeightExceededException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
	}
	
	/**
	 * Test handling units FIFO sequence (locaPos)
	 */
	@Test
	@InSequence(6)
	public void checkFifoSequence() {
		LOG.info("--- Test checkFifoSequence");
		
		assertTrue(locationLocal.getAll().isEmpty());
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		
		// Prepare a location
		locationLocal.create(new FifoLocation("A", "Test"));
		Location locA = locationLocal.getById("A");
		LOG.info("Fifo Location prepared: " + locA);
		
		// Drop to make a relation
		HandlingUnit hU1 = new HandlingUnit("1", "Test");
		
		try {
			handlingUnitLocal.dropTo(locA, hU1);

			HandlingUnit hU2 = new HandlingUnit("2", "Test");
			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, hU2);

			HandlingUnit hU3 = new HandlingUnit("3", "Test");
			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, hU3);

			HandlingUnit hU4 = new HandlingUnit("4", "Test");
			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, hU4);

			HandlingUnit hU5 = new HandlingUnit("5", "Test");
			locA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(locA, hU5);

			// MANDATORY reread
			locA = locationLocal.getById("A");
			assertNotNull(locA);
			assertFalse(locA.getHandlingUnits().isEmpty());
			assertEquals(5, locA.getHandlingUnits().size());

			LOG.info("5 HandlingUnits dropped to " + locA.getLocationId() + "   " + locA);

			// MANDATORY reread
			hU5 = handlingUnitLocal.getById("5");
			assertEquals(locA, hU5.getLocation());
			assertEquals(Integer.valueOf(5), hU5.getLocaPos());
			LOG.info("Sample hU5 locaPos check: {}", hU5);

			// MANDATORY reread
			hU1 = handlingUnitLocal.getById("1");
			assertEquals(locA, hU1.getLocation());
			assertEquals(Integer.valueOf(1), hU1.getLocaPos());
			LOG.info("Sample hU1 locaPos check: {}", hU1);
		
			handlingUnitLocal.pickFrom(locA, hU1);
			
			locA = locationLocal.getById("A");
			assertEquals(4, locA.getHandlingUnits().size());
			LOG.info("After FIRST PICK {}", locA);

			hU1 = handlingUnitLocal.getById("1");
			assertNull(hU1.getLocation());
			assertNull(hU1.getLocaPos());
						
			hU2 = handlingUnitLocal.getById("2");
			assertNotNull(hU2);
			assertEquals(locA, hU2.getLocation());
			assertEquals(Integer.valueOf(1), hU2.getLocaPos());
			
			handlingUnitLocal.pickFrom(locA, hU2);
			
			locA = locationLocal.getById("A");
			hU3 = handlingUnitLocal.getById("3");
			handlingUnitLocal.pickFrom(locA, hU3);
			
			locA = locationLocal.getById("A");
			hU4 = handlingUnitLocal.getById("4");
			handlingUnitLocal.pickFrom(locA, hU4);			

			locA = locationLocal.getById("A");
			assertEquals(1, locA.getHandlingUnits().size());
			LOG.info("After FOURTH PICK {}", locA);

			hU5 = handlingUnitLocal.getById("5");
			assertNotNull(hU5);
			assertEquals(locA, hU5.getLocation());
			assertEquals(Integer.valueOf(1), hU5.getLocaPos());
		}
		catch (LocationIsEmptyException | CapacityExceededException | WeightExceededException| HandlingUnitNotOnLocationException ex) {
			Assert.fail("Not expected: " + ex);
		}
	}
}
