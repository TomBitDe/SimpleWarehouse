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
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.model.AbsolutPosition;
import com.home.simplewarehouse.model.Dimension;
import com.home.simplewarehouse.model.EntityBase;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.LogicalPosition;
import com.home.simplewarehouse.model.RelativPosition;
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
		List<Location> locations = locationService.getAll();
		
		locations.stream().forEach(l -> locationService.delete(l));
		
		// Cleanup handling units
		List<HandlingUnit> handlingUnits = handlingUnitService.getAll();
		
		handlingUnits.stream().forEach(h -> handlingUnitService.delete(h));		

		LOG.trace("<-- afterTest()");
	}

	/**
	 * Simple location with no reference to handling units
	 */
	@Test
	@InSequence(0)
	public void create_getById() {
		LOG.info("--- Test create_getById");

		assertTrue(locationService.getAll().isEmpty());
		
		Location location = null;
		
		try {
			location = locationService.createOrUpdate(null);

			Assert.fail("Exception expected");
		}
		catch (EJBException ejbex) {
			LOG.info(ejbex.getMessage());
		}

		try {
			location = locationService.createOrUpdate(new Location(null));

			Assert.fail("Exception expected");
		}
		catch (EJBException ejbex) {
			LOG.info(ejbex.getMessage());
		}
		
		assertNull(location);
		
		Location expLocation = new Location("A");

		location = locationService.createOrUpdate(expLocation);

		assertNotNull(location);
		assertEquals(expLocation, location);
		assertEquals(EntityBase.USER_DEFAULT, location.getUpdateUserId());
		assertNotNull(location.getUpdateTimestamp());
		assertEquals(1, location.getVersion());
		
		LOG.info(location);
		
		expLocation.setUpdateUserId("TestUpdate");
		
		location = locationService.createOrUpdate(expLocation);
		
		assertEquals(expLocation, location);
		
		LOG.info(location);
		
		assertNull(locationService.getById("B"));
		
		expLocation = new Location("C");
		expLocation.setDimension(null);
		expLocation.setLocationStatus(null);
		expLocation.setPosition(null);

		location = locationService.createOrUpdate(expLocation);
		
		// Defaults are set because null does not fit
		assertNotNull(location.getDimension());
		assertNotNull(location.getLocationStatus());
		assertNotNull(location.getPosition());
		assertTrue(location.getPosition() instanceof LogicalPosition);
				
		LOG.info(location);

		expLocation = new Location("D");
		expLocation.setPosition(new RelativPosition(1, 1, 1));

		location = locationService.createOrUpdate(expLocation);
		
		assertNotNull(location.getPosition());
		assertTrue(location.getPosition() instanceof RelativPosition);
		
		LOG.info(location);

		expLocation = new Location("F");
		expLocation.setPosition(new AbsolutPosition(1.25f, 3.12f, 1.01f));

		location = locationService.createOrUpdate(expLocation);
		
		assertNotNull(location.getPosition());
		assertTrue(location.getPosition() instanceof AbsolutPosition);
		
		LOG.info(location);
	}
	
	/**
	 * Test the delete, getById and create sequence
	 */
	@Test
	@InSequence(1)
	public void delete_getById_create() {
		LOG.info("--- Test delete_getById_create");

		assertTrue(locationService.getAll().isEmpty());
		
	    Location location = locationService.createOrUpdate(new Location("A"));

		LOG.info("Location getById: " + location);
		
		assertEquals("A", location.getLocationId());
		
		// Delete the locationService
		locationService.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		LOG.info("Location deleted: " + location.getLocationId());
		
		location = locationService.createOrUpdate(new Location("A", "Test"));

		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertNotNull(location.getUpdateTimestamp());
		LOG.info("Location created: " + location);

		// Delete the locationService
		locationService.delete(location);
		LOG.info("Location deleted: " + location.getLocationId());
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		location = locationService.createOrUpdate(new Location("A", "Test", ts));

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

		assertTrue(locationService.getAll().isEmpty());
		
	    Location location = locationService.createOrUpdate(new Location("A"));

		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		
		// Delete the location
		locationService.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());

		// MANDATORY reread
		assertNull(locationService.getById("A"));
		LOG.info("Location deleted: " + location.getLocationId());
		
	    // MANDATORY reread
		location = locationService.getById("A");
		assertNull(location);
		
		// Delete null
		location = null;
		locationService.delete(location);
		assertTrue(true);

		// Delete locationId null
		location = locationService.createOrUpdate(new Location("A"));
		assertNotNull(location);
		location.setLocationId(null);
		locationService.delete(location);
		location = locationService.getById("A");
		assertNotNull(location);
		
		// Now delete by id
		locationService.delete("A");
		location = locationService.getById("A");
		assertNull(location);

		locationService.delete("AAAAA");
		location = locationService.getById("AAAAA");
		assertNull(location);

		// Delete id null
		try {
			locationService.delete((String)null);

			Assert.fail("Exception expected");
		}
		catch (EJBException ex) {
			assertTrue(true);
		}
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
		locationService.createOrUpdate(new Location("A", "Test"));
		locationService.createOrUpdate(new Location("B", "Test"));
		locationService.createOrUpdate(new Location("C", "Test"));
		locationService.createOrUpdate(new Location("D", "Test"));
		locationService.createOrUpdate(new Location("E", "Test"));

		// Get them all and output
		List<Location> locations = locationService.getAll();
		locations.stream().forEach( l -> LOG.info(l.toString()) );

		assertFalse(locations.isEmpty());
		assertEquals(5, locations.size());
		
		locations = locationService.getAll(0, 3);
		assertEquals(3, locations.size());
		
		try {
			locations = locationService.getAll(-3, 3);
			
			Assert.fail("Exception expected");
		}
		catch (EJBException ex) {
			assertTrue(true);
		}

		try {
			locations = locationService.getAll(2, 0);
			
			Assert.fail("Exception expected");
		}
		catch (EJBException ex) {
			assertTrue(true);
		}
		
		assertEquals(locationService.getAll().size(), locationService.count());
	}
	
	/**
	 * Test delete a location with handling units on it
	 */
	@Test
	@InSequence(4)
	public void deleteLocationWithHandlingUnits() {
		LOG.info("--- Test deleteLocationWithHandlingUnits");
		
		assertTrue(locationService.getAll().isEmpty());
		assertTrue(handlingUnitService.getAll().isEmpty());
		
		// Prepare a locationService
		locationService.createOrUpdate(new Location("A", "Test"));
		Location locA = locationService.getById("A");
		LOG.info("Location prepared: " + locA);
		
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
			LOG.info("Location deleted: " + locA.getLocationId());

			// MANDATORY reread
			hU2 = handlingUnitService.getById("2");
			hU5 = handlingUnitService.getById("5");

			assertNotNull(hU2);
			assertNotNull(hU5);

			assertNull(hU2.getLocation());
			assertNull(hU5.getLocation());

			LOG.info("Sample hU2 and hU5 have no longer a locationService");
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
		
		assertTrue(locationService.getAll().isEmpty());
		assertTrue(handlingUnitService.getAll().isEmpty());
		
		// Prepare a location
		Location locA = locationService.createOrUpdate(new Location("A", "Test"));
		
		// Test the special toString also
		assumeTrue(locA.toString().contains("HandlingUnits RANDOM=[]"));
		
		LOG.info("Location prepared: " + locA);
		
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
		
			LOG.info("1 HandlingUnit dropped to " + locA.getLocationId() + "   " + locA);

			LOG.info("Sample hU8");
			// MANDATORY reread
			HandlingUnit hU8 = handlingUnitService.getById("8");
			LOG.info(hU8);
		
			// Now delete the location
			// MANDATORY reread
			locA = locationService.getById("A");
			locationService.delete(locA);
			LOG.info("Location deleted: " + locA.getLocationId());
		
			// MANDATORY reread
			hU8 = handlingUnitService.getById("8");
		
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
		
		assertTrue(locationService.getAll().isEmpty());

		locationService.createOrUpdate(new Location("A"));
		locationService.createOrUpdate(new Location("B"));
		locationService.createOrUpdate(new Location("C"));
		locationService.createOrUpdate(new Location("D"));
		

		LOG.info("Locations created: " + locationService.getAll().size());
		
		List<Location> freeCapacityLocations = locationService.getAllWithFreeCapacity();
		
		assertEquals(4, freeCapacityLocations.size());

		Location expLocation = locationService.getById("C");
		assertTrue(freeCapacityLocations.contains(expLocation));

		// Now make location B full
		Location location = locationService.getById("B");
		Dimension dim = location.getDimension();
		dim.setMaxCapacity(1);
		location.setDimension(dim);
		location = locationService.createOrUpdate(location);
		
		try {
			handlingUnitService.dropTo(location, new HandlingUnit("1"));
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);			
		}
		
		freeCapacityLocations = locationService.getAllWithFreeCapacity();
		assertEquals(3, freeCapacityLocations.size());
	}

	/**
	 * Test get handling units on a location
	 */
	@Test
	@InSequence(8)
	public void getHandlingUnits() {
		LOG.info("--- Test getHandlingUnits");
		
		assertTrue(locationService.getAll().isEmpty());

		locationService.createOrUpdate(new Location("A"));

		LOG.info("Locations created: " + locationService.getAll().size());
		Location locA = locationService.getById("A");
		
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
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);			
		}

		assertNotNull(locA);
		assertFalse(locationService.getHandlingUnits(locA).isEmpty());
		assertEquals(5, locationService.getHandlingUnits(locA).size());
		
		try {
			locationService.getHandlingUnits(null);
			
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
			locationService.getHandlingUnits(locA);
			
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
		
		assertTrue(locationService.getAll().isEmpty());

		locationService.createOrUpdate(new Location("A"));

		LOG.info("Locations created: " + locationService.getAll().size());
		Location locA = locationService.getById("A");
		
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
		assertFalse(locationService.getAvailablePicks(locA).isEmpty());
		assertEquals(2, locationService.getAvailablePicks(locA).size());
		
		try {
			locationService.getAvailablePicks(null);
			
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
			locationService.getAvailablePicks(locA);
			
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
		
		assertTrue(locationService.getAll().isEmpty());

		locationService.createOrUpdate(new Location("A"));

		LOG.info("Locations created: " + locationService.getAll().size());
		Location locA = locationService.getById("A");
		
		assertTrue(locationService.getAllFull().isEmpty());
		
		locA.getDimension().setMaxCapacity(2);
		locA.setDimension(locA.getDimension());
		
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
