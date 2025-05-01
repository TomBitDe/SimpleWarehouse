package com.home.simplewarehouse.location;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
import com.home.simplewarehouse.model.AbsolutPosition;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.LogicalPosition;
import com.home.simplewarehouse.model.RandomLocation;
import com.home.simplewarehouse.model.RelativPosition;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;
import com.home.simplewarehouse.zone.ZoneBean;
import com.home.simplewarehouse.zone.ZoneService;

/**
 * Test the Location bean.
 */
@RunWith(Arquillian.class)
public class LocationPositionTest {
	private static final Logger LOG = LogManager.getLogger(LocationPositionTest.class);

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
						ZoneService.class, ZoneBean.class,
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
	public LocationPositionTest() {
		super();
		// DO NOTHING HERE!
	}
	
	/**
	 * What to do before an individual test will be executed (each test)
	 */
	@Before
	public void beforeTest() {
		LOG.trace("--> beforeTest()");
		
		// Cleanup locations
		locationService.getAll().stream().forEach(l -> locationService.delete(l));
		
		// Cleanup handling units
		handlingUnitService.getAll().stream().forEach(h -> handlingUnitService.delete(h));		
		
		LOG.trace("<-- beforeTest()");		
	}
	
	/**
	 * What to do after an individual test will be executed (each test)
	 */
	@After
	public void afterTest() {
		LOG.trace("--> afterTest()");

		// Cleanup locations
		locationService.getAll().stream().forEach(l -> locationService.delete(l));
		
		// Cleanup handling units
		handlingUnitService.getAll().stream().forEach(h -> handlingUnitService.delete(h));		

		LOG.trace("<-- afterTest()");
	}

	/**
	 * Test the delete, getById and create sequence
	 */
	@Test
	@InSequence(1)
	public void delete_getById_create() {
		LOG.info("--- Test delete_getById_create");

		assertTrue(locationService.getAll().isEmpty());
		
	    Location location = locationService.createOrUpdate(new RandomLocation("A"));

		LOG.info("Location getById: {}", location);
		
		assertEquals("A", location.getLocationId());
		
		// Delete the location
		locationService.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		LOG.info("Location deleted: {}", location.getLocationId());
		
		location = locationService.createOrUpdate(new RandomLocation("A", "Test"));

		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertNotNull(location.getUpdateTimestamp());
		LOG.info("Location created: {}", location);

		// Delete the location
		locationService.delete(location);
		LOG.info("Location deleted: {}", location.getLocationId());
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		location = locationService.createOrUpdate(new RandomLocation("A", "Test", ts));

		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertEquals(ts, location.getUpdateTimestamp());
		LOG.info("Location created: {}", location);
	}

	/**
	 * Test modify location position values
	 */
	@Test
	@InSequence(6)
	public void modifyLocationPositionValues() {
		LOG.info("--- Test modifyLocationPositionValues");
		
		assumeTrue(locationService.getAll().isEmpty());
		
		Location locA = locationService.createOrUpdate(new RandomLocation("A", "Test"));
		
		// LogicalPosition
		assertTrue(locA.getPosition() instanceof LogicalPosition);
		
		((LogicalPosition) locA.getPosition()).setCoord("V");
		
		locA = locationService.createOrUpdate(locA);
		
		assertNotNull(locA);
		assertEquals("V", ((LogicalPosition) locA.getPosition()).getCoord());

		LOG.info("LocA: {}", locA);
		
		// RelativePosition
		Location locB = locationService.createOrUpdate(new RandomLocation(new RelativPosition(), "B"));
		
		assertNotNull(locB);
		// Check the DEFAULT values
		assertEquals(0, ((RelativPosition) locB.getPosition()).getXCoord());
		assertEquals(0, ((RelativPosition) locB.getPosition()).getYCoord());
		assertEquals(0, ((RelativPosition) locB.getPosition()).getZCoord());
		
		// Modify
		((RelativPosition) locB.getPosition()).setXCoord(4);
		((RelativPosition) locB.getPosition()).setYCoord(5);
		((RelativPosition) locB.getPosition()).setZCoord(6);
		
		locB = locationService.createOrUpdate(locB);
		
		assertNotNull(locB);
		assertEquals(4, ((RelativPosition) locB.getPosition()).getXCoord());
		assertEquals(5, ((RelativPosition) locB.getPosition()).getYCoord());
		assertEquals(6, ((RelativPosition) locB.getPosition()).getZCoord());
		
		// Easy way
		((RelativPosition) locB.getPosition()).setCoord(9, 9, 9);
		
		locB = locationService.createOrUpdate(locB);

		assertNotNull(locB);
		assertEquals(9, ((RelativPosition) locB.getPosition()).getXCoord());
		assertEquals(9, ((RelativPosition) locB.getPosition()).getYCoord());
		assertEquals(9, ((RelativPosition) locB.getPosition()).getZCoord());

		LOG.info("LocB: {}", locB);

		// AbsolutPosition
		Location locF = locationService.createOrUpdate(new RandomLocation(
				new AbsolutPosition(3.0f, 2.5f, 6.1f), "F"));
		
		assertNotNull(locF);
		// Check the values
		assertEquals(3.0f, ((AbsolutPosition) locF.getPosition()).getXCoord(), 0.0001f);
		assertEquals(2.5f, ((AbsolutPosition) locF.getPosition()).getYCoord(), 0.0001f);
		assertEquals(6.1f, ((AbsolutPosition) locF.getPosition()).getZCoord(), 0.0001f);
		
		assertEquals(locF.getPosition(), locF.getPosition());
		assertNotEquals(null, locF.getPosition());

		Location locD = locationService.createOrUpdate(new RandomLocation(new AbsolutPosition(), "D"));

		assertNotNull(locD);
		// Check the DEFAULT values
		assertEquals(0.0f, ((AbsolutPosition) locD.getPosition()).getXCoord(), 0.0001f);
		assertEquals(0.0f, ((AbsolutPosition) locD.getPosition()).getYCoord(), 0.0001f);
		assertEquals(0.0f, ((AbsolutPosition) locD.getPosition()).getZCoord(), 0.0001f);
		
		// Modify
		((AbsolutPosition) locD.getPosition()).setXCoord(4.0f);
		((AbsolutPosition) locD.getPosition()).setYCoord(5.0f);
		((AbsolutPosition) locD.getPosition()).setZCoord(6.0f);
		
		locD = locationService.createOrUpdate(locD);
		
		assertNotNull(locD);
		assertEquals(4.0f, ((AbsolutPosition) locD.getPosition()).getXCoord(), 0.0001f);
		assertEquals(5.0f, ((AbsolutPosition) locD.getPosition()).getYCoord(), 0.0001f);
		assertEquals(6.0f, ((AbsolutPosition) locD.getPosition()).getZCoord(), 0.0001f);
		
		// Easy way
		((AbsolutPosition) locD.getPosition()).setCoord(9.0f, 9.0f, 9.0f);
		
		locD = locationService.createOrUpdate(locD);

		assertNotNull(locD);
		assertEquals(9.0f, ((AbsolutPosition) locD.getPosition()).getXCoord(), 0.0001f);
		assertEquals(9.0f, ((AbsolutPosition) locD.getPosition()).getYCoord(), 0.0001f);
		assertEquals(9.0f, ((AbsolutPosition) locD.getPosition()).getZCoord(), 0.0001f);

		LOG.info("LocD: {}", locD);
	}
	
	/**
	 * Test getAll method
	 */
	@Test
	@InSequence(8)
	public void getAll() {
		LOG.info("--- Test getAll");
		
		assertTrue(locationService.getAll().isEmpty());
		
		// Prepare some locations; 7 locations
		locationService.createOrUpdate(new RandomLocation("A", "Test"));
		locationService.createOrUpdate(new RandomLocation(new RelativPosition(), "B"));
		locationService.createOrUpdate(new RandomLocation("C", "Test"));
		locationService.createOrUpdate(new RandomLocation(new AbsolutPosition(), "D"));
		locationService.createOrUpdate(new RandomLocation("E", "Test"));
		locationService.createOrUpdate(new RandomLocation(new LogicalPosition("F"), "F", "Test"));
		locationService.createOrUpdate(new RandomLocation(new LogicalPosition("G"), "G", "Test",
				new Timestamp(System.currentTimeMillis())));

		// Get them all and output
		List<Location> locations = locationService.getAll();
		locations.stream().forEach( l -> LOG.info(l.toString()) );

		assertFalse(locations.isEmpty());
		assertEquals(7, locations.size());
		
		locations = locationService.getAll(0, 3);
		assertEquals(3, locations.size());
	}
	
	/**
	 * Test delete a location with handling units on it
	 */
	@Test
	@InSequence(11)
	public void deleteLocationWithHandlingUnits() {
		LOG.info("--- Test deleteLocationWithHandlingUnits");
		
		assertTrue(locationService.getAll().isEmpty());
		assertTrue(handlingUnitService.getAll().isEmpty());
		
		// Prepare a locationService
		locationService.createOrUpdate(new RandomLocation("A", "Test"));
		Location locA = locationService.getById("A");
		LOG.info("Location prepared: {}", locA);
		
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

			LOG.info("5 HandlingUnits dropped to {}     {}", locA.getLocationId(), locA);

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
			LOG.info("Location deleted: {}", locA.getLocationId());

			// MANDATORY reread
			hU2 = handlingUnitService.getById("2");
			hU5 = handlingUnitService.getById("5");

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
	@InSequence(14)
	public void deleteLocationWithOneHandlingUnit() {
		LOG.info("--- Test deleteLocationWithOneHandlingUnit");
		
		assertTrue(locationService.getAll().isEmpty());
		assertTrue(handlingUnitService.getAll().isEmpty());
		
		// Prepare a location
		Location locA = locationService.createOrUpdate(new RandomLocation("A", "Test"));
		
		// Test the special toString also
		assumeTrue(locA.toString().contains("HandlingUnits RANDOM=[]"));
		
		LOG.info("Location prepared: {}", locA);
		
		// Drop to make a relation
		try {
			handlingUnitService.dropTo(locA, new HandlingUnit("8", "Test"));
		
			// MANDATORY reread
			locA = locationService.getById("A");
			assertNotNull(locA);	
			assertFalse(locA.getHandlingUnits().isEmpty());
			assertEquals(1, locA.getHandlingUnits().size());

			// Test the special toString also
			assertTrue(locA.toString().contains("HandlingUnits RANDOM=[\"8\"]"));

			assertFalse(locA.getHandlingUnits().contains(new HandlingUnit("2")));
		
			LOG.info("1 HandlingUnit dropped to {}   {}", locA.getLocationId(), locA);

			LOG.info("Sample hU8");
			// MANDATORY reread
			HandlingUnit hU8 = handlingUnitService.getById("8");
			LOG.info(hU8);
		
			// Now delete the location
			// MANDATORY reread
			locA = locationService.getById("A");
			locationService.delete(locA);
			LOG.info("Location deleted: {}", locA.getLocationId());
		
			// MANDATORY reread
			hU8 = handlingUnitService.getById("8");
		
			assertNotNull(hU8);
		
			assertNull(hU8.getLocation());
		
			LOG.info("Sample hU8 has no longer a location");
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
	@InSequence(17)
	public void getAllWithFreeCapacity() {
		LOG.info("--- Test getAllWithFreeCapacity");
		
		assertTrue(locationService.getAll().isEmpty());

		locationService.createOrUpdate(new RandomLocation("A"));
		locationService.createOrUpdate(new RandomLocation(new RelativPosition(), "B"));
		locationService.createOrUpdate(new RandomLocation("C"));
		locationService.createOrUpdate(new RandomLocation(new AbsolutPosition(), "D"));
		locationService.createOrUpdate(new RandomLocation("E"));
		

		LOG.info("Locations created: {}", locationService.getAll().size());
		
		List<Location> freeCapacityLocations = locationService.getAllWithFreeCapacity();
		
		assertEquals(5, freeCapacityLocations.size());

		Location expLocation = locationService.getById("C");
		assertTrue(freeCapacityLocations.contains(expLocation));

		// Now make location C full
		Location location = locationService.getById("C");
		location.getDimension().setMaxCapacity(1);
		location = locationService.createOrUpdate(location);
		
		try {
			handlingUnitService.dropTo(location, new HandlingUnit("1"));
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);			
		}
		
		freeCapacityLocations = locationService.getAllWithFreeCapacity();
		assertEquals(4, freeCapacityLocations.size());
	}

	/**
	 * Test get all full locations
	 */
	@Test
	@InSequence(26)
	public void getAllFull() {
		LOG.info("--- Test getAllFull");
		
		assertTrue(locationService.getAll().isEmpty());

		Location locA = locationService.createOrUpdate(new RandomLocation("A"));

		LOG.info("Locations created: {}", locationService.getAll().size());
		
		assertTrue(locationService.getAllFull().isEmpty());
		
		locA.getDimension().setMaxCapacity(2);
		
		locA = locationService.createOrUpdate(locA);
		
		// Drop to make a relation
		try {
			handlingUnitService.dropTo(locA, new HandlingUnit("1", "Test"));

			locA = locationService.getById("A");
			handlingUnitService.dropTo(locA, new HandlingUnit("2", "Test"));
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);			
		}

		// MANDATORY reread
		locA = locationService.getById("A");
		assertNotNull(locA);
		assertFalse(locationService.getAllFull().isEmpty());
		assertEquals(1, locationService.getAllFull().size());
	}
}
