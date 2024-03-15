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
import com.home.simplewarehouse.handlingunit.HandlingUnitNotOnLocationException;
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.handlingunit.LocationIsEmptyException;
import com.home.simplewarehouse.model.EntityBase;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.LifoLocation;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the LIFO Location.
 */
@RunWith(Arquillian.class)
public class LifoLocationTest {
	private static final Logger LOG = LogManager.getLogger(LifoLocationTest.class);

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
	public LifoLocationTest() {
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
		
		Location expLocation = locationService.createOrUpdate(new LifoLocation("A"));
		LOG.info("Lifo Location created: " + expLocation);
		
		assertFalse(expLocation.equals(null));

		assertEquals(expLocation, locationService.getById("A"));
		assertEquals(EntityBase.USER_DEFAULT, expLocation.getUpdateUserId());
		assertNotNull(expLocation.getUpdateTimestamp());
		
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
		
		Location location = locationService.createOrUpdate(new LifoLocation("A"));

		assertEquals("A", location.getLocationId());
		
		// Delete the location
		locationService.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		LOG.info("Lifo Location deleted: " + location.getLocationId());
		
		location = locationService.createOrUpdate(new LifoLocation("A", "Test"));

		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertNotNull(location.getUpdateTimestamp());
		LOG.info("Lifo Location created: " + location);

		// Delete the location
		locationService.delete(location);
		LOG.info("Lifo Location deleted: " + location.getLocationId());
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		location = locationService.createOrUpdate(new LifoLocation("A", "Test", ts));

		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertEquals(ts, location.getUpdateTimestamp());
		LOG.info("Lifo Location created: " + location);
	}

	/**
	 * Test the delete by location
	 */
	@Test
	@InSequence(2)
	public void deleteByLocation() {
		LOG.info("--- Test deleteByLocation");

		assertTrue(locationService.getAll().isEmpty());
		
	    Location location = locationService.createOrUpdate(new LifoLocation("A"));

		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		
		// Delete the locationService
		locationService.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());

		// MANDATORY reread
		assertNull(locationService.getById("A"));
		LOG.info("Lifo Location deleted: " + location.getLocationId());
		
	    // MANDATORY reread
		location = locationService.getById("A");
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
		locationService.createOrUpdate(new LifoLocation("A", "Test"));
		locationService.createOrUpdate(new LifoLocation("B", "Test"));
		locationService.createOrUpdate(new LifoLocation("C", "Test"));
		locationService.createOrUpdate(new LifoLocation("D", "Test"));
		locationService.createOrUpdate(new LifoLocation("E", "Test"));

		// Get them all and check
		List<Location> locations = locationService.getAll();

		assertNotNull(locations);
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
		Location locA = locationService.createOrUpdate(new LifoLocation("A", "Test"));
		LOG.info("Lifo Location prepared: " + locA);
		
		// Drop to make a relation
		try {
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

			//LOG.info("5 HandlingUnits dropped to " + locA.getLocationId() + "   " + locA);

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
			LOG.info("Lifo Location deleted: " + locA.getLocationId());

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
		locationService.createOrUpdate(new LifoLocation("A", "Test"));
		Location locA = locationService.getById("A");
		
		// Test the special toString also
		assumeTrue(locA.toString().contains("HandlingUnits LIFO=[]"));
		
		LOG.info("Lifo Location prepared: " + locA);
		
		// Drop to make a relation
		try {
			handlingUnitService.dropTo(locA, new HandlingUnit("8", "Test"));

			// MANDATORY reread
			locA = locationService.getById("A");
			assertNotNull(locA);
			assertFalse(locA.getHandlingUnits().isEmpty());
			assertEquals(1, locA.getHandlingUnits().size());

			// Test the special toString also
			assertTrue(locA.toString().contains("HandlingUnits LIFO=[\"8 Pos 1\"]"));

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
			LOG.info("Lifo Location deleted: " + locA.getLocationId());

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
	 * Test handling units LIFO sequence (locaPos)
	 */
	@Test
	@InSequence(6)
	public void checkLifoSequence() {
		LOG.info("--- Test checkLifoSequence");
		
		assertTrue(locationService.getAll().isEmpty());
		assertTrue(handlingUnitService.getAll().isEmpty());
		
		// Prepare a locationService
		locationService.createOrUpdate(new LifoLocation("A", "Test"));
		Location locA = locationService.getById("A");
		LOG.info("Lifo Location prepared: " + locA);
		
		// Drop to make a relation
		try {
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

			LOG.info("5 HandlingUnits dropped to " + locA.getLocationId() + "   " + locA);

			// MANDATORY reread
			HandlingUnit hU5 = handlingUnitService.getById("5");
			assertEquals(locA, hU5.getLocation());
			assertEquals(Integer.valueOf(1), hU5.getLocaPos());
			LOG.info("Sample hU5 locaPos check: {}", hU5);

			// MANDATORY reread
			HandlingUnit hU2 = handlingUnitService.getById("2");
			assertEquals(locA, hU2.getLocation());
			assertEquals(Integer.valueOf(4), hU2.getLocaPos());
			LOG.info("Sample hU2 locaPos check: {}", hU2);

			handlingUnitService.pickFrom(locA, hU5);

			locA = locationService.getById("A");
			assertEquals(4, locA.getHandlingUnits().size());
			LOG.info("After FIRST PICK {}", locA);

			hU5 = handlingUnitService.getById("5");
			assertNull(hU5.getLocation());
			assertNull(hU5.getLocaPos());

			HandlingUnit hU4 = handlingUnitService.getById("4");
			assertNotNull(hU4);
			assertEquals(locA, hU4.getLocation());
			assertEquals(Integer.valueOf(1), hU4.getLocaPos());

			handlingUnitService.pickFrom(locA, hU4);

			locA = locationService.getById("A");
			HandlingUnit hU3 = handlingUnitService.getById("3");
			handlingUnitService.pickFrom(locA, hU3);

			locA = locationService.getById("A");
			hU2 = handlingUnitService.getById("2");
			handlingUnitService.pickFrom(locA, hU2);

			locA = locationService.getById("A");
			assertEquals(1, locA.getHandlingUnits().size());
			LOG.info("After FOURTH PICK {}", locA);

			HandlingUnit hU1 = handlingUnitService.getById("1");
			assertNotNull(hU1);
			assertEquals(locA, hU1.getLocation());
			assertEquals(Integer.valueOf(1), hU1.getLocaPos());
		}
		catch (LocationIsEmptyException | DimensionException | HandlingUnitNotOnLocationException ex) {
			Assert.fail("Not expected: " + ex);
		}
	}
	
	/**
	 * Test pickFrom
	 */
	@Test
	@InSequence(8)
	public void pickFromLifo() {
		LOG.info("--- Test pickFromLifo");
		
		try {
			// One single drop
			handlingUnitService.dropTo(new LifoLocation("W"), new HandlingUnit("8"));
			
			// Now pick with id
			HandlingUnit hu8 = handlingUnitService.pickFrom("W");
			
			// Check
			assertNotNull(hu8);
			assertEquals("8", hu8.getId());
			
			// Location is empty
			assertTrue(locationService.getById("W").getAvailablePicks().isEmpty());
		}
		catch (LocationIsEmptyException | DimensionException ex) {
			Assert.fail("Not expected: " + ex);
		}
	}
	
	/**
	 * Test exceptional cases
	 */
	@Test
	@InSequence(10)
	public void checkExceptionalCases() {
		LOG.info("--- Test checkExceptionalCases");
		
		try {
			handlingUnitService.pickFrom((Location)null);

			Assert.fail("Exception expected");
		}
		catch (LocationIsEmptyException lieex) {
			Assert.fail("Not expected: " + lieex);
		}
		catch (EJBException ex) {
			assertTrue(true);
		}

		try {
			handlingUnitService.pickFrom((String)null);

			Assert.fail("Exception expected");
		}
		catch (LocationIsEmptyException lieex) {
			Assert.fail("Not expected: " + lieex);
		}
		catch (EJBException ex) {
			assertTrue(true);
		}
		
		try {
			handlingUnitService.pickFrom(new LifoLocation("X"));

			Assert.fail("Exception expected");
		}
		catch (LocationIsEmptyException e) {
			assertTrue(true);
		}
		
		locationService.createOrUpdate(new LifoLocation("Y"));
		try {
			handlingUnitService.pickFrom("Y");

			Assert.fail("Exception expected");
		}
		catch (LocationIsEmptyException e) {
			assertTrue(true);
		}		
	}
}
