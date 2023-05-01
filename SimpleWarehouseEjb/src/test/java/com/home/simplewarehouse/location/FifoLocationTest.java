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
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
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
						LocationService.class, LocationBean.class,
						HandlingUnitService.class, HandlingUnitBean.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		LOG.trace("<-- createTestArchive()");
		return archive;
	}

	@EJB
	LocationService locationService;
	
	@EJB
	HandlingUnitService handlingUnitService;
	
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
		List<Location> locations = locationService.getAll();
		
		locations.stream().forEach(l -> locationService.delete(l));
		
		// Cleanup handling units
		List<HandlingUnit> handlingUnits = handlingUnitService.getAll();
		
		handlingUnits.stream().forEach(h -> handlingUnitService.delete(h));		

		LOG.trace("<-- afterTest()");
	}

	/**
	 * Simple locationService with no reference to handling units
	 */
	@Test
	@InSequence(0)
	public void create_getById() {
		LOG.info("--- Test create_getById");

		assertTrue(locationService.getAll().isEmpty());
		
		FifoLocation expLocation = new FifoLocation("A");

		locationService.create(expLocation);
		LOG.info("Fifo Location created: " + expLocation);

		// MANDATORY reread
		Location location = locationService.getById(expLocation.getLocationId());		
		LOG.info("FifoLocation getById: " + location);
		
		assertEquals(expLocation, location);
		assertEquals(EntityBase.USER_DEFAULT, location.getUpdateUserId());
		assertNotNull(location.getUpdateTimestamp());
		
	    // Should be null because never created
		assertNull(locationService.getById("B"));
	}
	
	/**
	 * Test the delete, getById and create sequence
	 */
	@Test
	@InSequence(1)
	public void delete_getById_create() {
		LOG.info("--- Test delete_getById_create");

		assertTrue(locationService.getAll().isEmpty());
		
	    Location location = locationService.create(new FifoLocation("A"));
		LOG.info("Fifo Location create: " + location);
		
		assertEquals("A", location.getLocationId());
		
		// Delete the locationService
		locationService.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		LOG.info("Fifo Location deleted: " + location.getLocationId());
		
		location = locationService.create(new FifoLocation("A", "Test"));				
		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertNotNull(location.getUpdateTimestamp());
		LOG.info("Fifo Location created: " + location);

		// Delete the locationService
		locationService.delete(location);
		LOG.info("Fifo Location deleted: " + location.getLocationId());
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		location = locationService.create(new FifoLocation("A", "Test", ts));
		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertEquals(ts, location.getUpdateTimestamp());
		LOG.info("Fifo Location created: " + location);
	}

	/**
	 * Test the delete by locationService
	 */
	@Test
	@InSequence(2)
	public void deleteByLocation() {
		LOG.info("--- Test deleteByLocation");

		assertTrue(locationService.getAll().isEmpty());
		
	    Location location = locationService.create(new FifoLocation("A"));
		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		
		// Delete the locationService
		locationService.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());

		// MANDATORY reread
		assertNull(locationService.getById("A"));
		LOG.info("Fifo Location deleted: " + location.getLocationId());
		
	    // MANDATORY reread
		location = (FifoLocation) locationService.getById("A");
		assertNull(location);
	}
	
	/**
	 * Test getAll method
	 */
	@Test
	@InSequence(3)
	public void getAll() {
		LOG.info("--- Test getAll");
		
		assertTrue(locationService.getAll().isEmpty());
		
		// Prepare some locations; 5 locations
		locationService.create(new FifoLocation("A", "Test"));
		locationService.create(new FifoLocation("B", "Test"));
		locationService.create(new FifoLocation("C", "Test"));
		locationService.create(new FifoLocation("D", "Test"));
		locationService.create(new FifoLocation("E", "Test"));

		// Get them all and output
		List<Location> locations = locationService.getAll();
		// locations.stream().forEach( l -> LOG.info(l.toString()) );

		assertFalse(locations.isEmpty());
		assertEquals(5, locations.size());
	}
	
	/**
	 * Test delete a locationService with handling units on it
	 */
	@Test
	@InSequence(4)
	public void deleteLocationWithHandlingUnits() {
		LOG.info("--- Test deleteLocationWithHandlingUnits");
		
		assertTrue(locationService.getAll().isEmpty());
		assertTrue(handlingUnitService.getAll().isEmpty());
		
		// Prepare a locationService
		Location locA = locationService.create(new FifoLocation("A", "Test"));
		LOG.info("Fifo Location prepared: " + locA);
		
		try {
			// Drop to make a relation
			handlingUnitService.dropTo(locA, new HandlingUnit("1", "Test"));

			locA = locationService.getById("A");
			handlingUnitService.dropTo(locA, new HandlingUnit("2", "Test"));

			locA = locationService.getById("A");
			handlingUnitService.dropTo(locA, new HandlingUnit("3", "Test"));

			locA = locationService.getById("A");
			handlingUnitService.dropTo(locA, new HandlingUnit("4", "Test"));

			locA = locationService.getById("A");
			handlingUnitService.dropTo(locA, new HandlingUnit("5", "Test"));

			// MANDATORY reread
			locA = locationService.getById("A");
			assertNotNull(locA);
			assertFalse(locA.getHandlingUnits().isEmpty());
			assertEquals(5, locA.getHandlingUnits().size());

			assertFalse(locA.getHandlingUnits().contains(new HandlingUnit("12")));

			LOG.info("5 HandlingUnits dropped to " + locA.getLocationId() + "   " + locA);

			LOG.info("Sample hU2 and hU5");
			// MANDATORY reread
			HandlingUnit hU2 = handlingUnitService.getById("2");
			LOG.info(hU2);
			// MANDATORY reread
			HandlingUnit hU5 = handlingUnitService.getById("5");
			LOG.info(hU5);

			// Now delete the locationService
			// MANDATORY reread
			locA = locationService.getById("A");
			locationService.delete(locA);
			LOG.info("Fifo Location deleted: " + locA.getLocationId());

			// MANDATORY reread
			hU2 = handlingUnitService.getById("2");
			hU5 = handlingUnitService.getById("5");

			assertNotNull(hU2);
			assertNotNull(hU5);

			LOG.info("Sample hU2 and hU5 have no longer a locationService and locaPos");
			assertNull(hU2.getLocation());
			assertNull(hU2.getLocaPos());
			assertNull(hU5.getLocation());
			assertNull(hU5.getLocaPos());

			LOG.info(hU2);
			LOG.info(hU5);
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
	}

	/**
	 * Test delete a locationService with one single handling unit on it
	 */
	@Test
	@InSequence(5)
	public void deleteLocationWithOneHandlingUnit() {
		LOG.info("--- Test deleteLocationWithOneHandlingUnit");
		
		assertTrue(locationService.getAll().isEmpty());
		assertTrue(handlingUnitService.getAll().isEmpty());
		
		// Prepare a locationService
		Location locA = locationService.create(new FifoLocation("A", "Test"));
		
		// Test the special toString also
		assumeTrue(locA.toString().contains("HandlingUnits FIFO=[]"));
		
		LOG.info("Fifo Location prepared: " + locA);
		
		// Drop to make a relation
		try {
			handlingUnitService.dropTo(locA, new HandlingUnit("8", "Test"));

			// MANDATORY reread
			locA = (FifoLocation) locationService.getById("A");
			assertNotNull(locA);
			assertFalse(locA.getHandlingUnits().isEmpty());
			assertEquals(1, locA.getHandlingUnits().size());

			// Test the special toString also
			assertTrue(locA.toString().contains("HandlingUnits FIFO=[\"8 Pos 1\"]"));

			assertFalse(locA.getHandlingUnits().contains(new HandlingUnit("2")));

			LOG.info("1 HandlingUnit dropped on " + locA.getLocationId() + locA);

			// MANDATORY reread
			HandlingUnit hU8 = handlingUnitService.getById("8");
			assertNotNull(hU8.getLocation());
			assertNotNull(hU8.getLocaPos());
			LOG.info("Sample hU8 {}", hU8);

			// Now delete the locationService
			// MANDATORY reread
			locA = locationService.getById("A");
			locationService.delete(locA);
			LOG.info("Fifo Location deleted: " + locA.getLocationId());

			// MANDATORY reread
			hU8 = handlingUnitService.getById("8");

			assertNotNull(hU8);

			assertNull(hU8.getLocation());
			assertNull(hU8.getLocaPos());

			LOG.info("Sample hU8 has no longer a locationService {}", hU8);
		}
		catch (DimensionException dimex) {
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
		
		assertTrue(locationService.getAll().isEmpty());
		assertTrue(handlingUnitService.getAll().isEmpty());
		
		// Prepare a locationService
		Location locA = locationService.create(new FifoLocation("A", "Test"));
		LOG.info("Fifo Location prepared: " + locA);
		
		// Drop to make a relation
		try {
			handlingUnitService.dropTo(locA,  new HandlingUnit("1", "Test"));

			locA = locationService.getById("A");
			handlingUnitService.dropTo(locA, new HandlingUnit("2", "Test"));

			locA = locationService.getById("A");
			handlingUnitService.dropTo(locA, new HandlingUnit("3", "Test"));

			locA = locationService.getById("A");
			handlingUnitService.dropTo(locA, new HandlingUnit("4", "Test"));

			locA = locationService.getById("A");
			handlingUnitService.dropTo(locA, new HandlingUnit("5", "Test"));

			// MANDATORY reread
			locA = locationService.getById("A");
			assertNotNull(locA);
			assertFalse(locA.getHandlingUnits().isEmpty());
			assertEquals(5, locA.getHandlingUnits().size());

			LOG.info("5 HandlingUnits dropped to " + locA.getLocationId() + "   " + locA);

			// MANDATORY reread
			HandlingUnit hU5 = handlingUnitService.getById("5");
			assertEquals(locA, hU5.getLocation());
			assertEquals(Integer.valueOf(5), hU5.getLocaPos());
			LOG.info("Sample hU5 locaPos check: {}", hU5);

			// MANDATORY reread
			HandlingUnit hU1 = handlingUnitService.getById("1");
			assertEquals(locA, hU1.getLocation());
			assertEquals(Integer.valueOf(1), hU1.getLocaPos());
			LOG.info("Sample hU1 locaPos check: {}", hU1);
		
			handlingUnitService.pickFrom(locA, hU1);
			
			locA = locationService.getById("A");
			assertEquals(4, locA.getHandlingUnits().size());
			LOG.info("After FIRST PICK {}", locA);

			hU1 = handlingUnitService.getById("1");
			assertNull(hU1.getLocation());
			assertNull(hU1.getLocaPos());
						
			HandlingUnit hU2 = handlingUnitService.getById("2");
			assertNotNull(hU2);
			assertEquals(locA, hU2.getLocation());
			assertEquals(Integer.valueOf(1), hU2.getLocaPos());
			
			handlingUnitService.pickFrom(locA, hU2);
			
			locA = locationService.getById("A");
			HandlingUnit hU3 = handlingUnitService.getById("3");
			handlingUnitService.pickFrom(locA, hU3);
			
			locA = locationService.getById("A");
			HandlingUnit hU4 = handlingUnitService.getById("4");
			handlingUnitService.pickFrom(locA, hU4);			

			locA = locationService.getById("A");
			assertEquals(1, locA.getHandlingUnits().size());
			LOG.info("After FOURTH PICK {}", locA);

			hU5 = handlingUnitService.getById("5");
			assertNotNull(hU5);
			assertEquals(locA, hU5.getLocation());
			assertEquals(Integer.valueOf(1), hU5.getLocaPos());
		}
		catch (LocationIsEmptyException | DimensionException | HandlingUnitNotOnLocationException ex) {
			Assert.fail("Not expected: " + ex);
		}
	}
}
