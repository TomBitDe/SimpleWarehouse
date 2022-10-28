package com.home.simplewarehouse.handlingunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusLocal;
import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the Handling Unit bean.
 */
@RunWith(Arquillian.class)
public class HandlingUnitTest {
	private static final Logger LOG = LogManager.getLogger(HandlingUnitTest.class);

	@EJB
	HandlingUnitLocal handlingUnitLocal;
	
	@EJB
	LocationLocal locationLocal;
	
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
						HandlingUnitLocal.class, HandlingUnitBean.class,
						LocationLocal.class, LocationBean.class,
						LocationStatusLocal.class, LocationStatusBean.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		LOG.trace("<-- createTestArchive()");
		
		return archive;
	}
	
	/**
	 * Mandatory default constructor
	 */
	public HandlingUnitTest() {
		super();
		// DO NOTHING HERE!
	}
	
	/**
	 * What to do before an individual test will be executed (each test)<br>
	 * <br>
	 * Initialize with the SampleWarehouse data
	 */
	@Before
	public void beforeTest() {
		LOG.trace("--> beforeTest()");
		
		LOG.trace("<-- beforeTest()");		
	}
	
	/**
	 * What to do after an individual test has been executed (each test)<br>
	 * <br>
	 * Cleanup the SampleWarehouse data
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
	 * Simple handling unit with no reference to a location
	 */
	@Test
	@InSequence(0)
	public void createAndGetById() {
		LOG.info("--- Test createAndGetById");

		assertTrue(handlingUnitLocal.getAll().isEmpty());

		HandlingUnit expHandlingUnit = new HandlingUnit("1");

		handlingUnitLocal.create(expHandlingUnit);
		HandlingUnit handlingUnit = handlingUnitLocal.getById(expHandlingUnit.getId());
		assertEquals(expHandlingUnit, handlingUnit);
		
		LOG.info(handlingUnit);
		
		expHandlingUnit = new HandlingUnit("2", null);

		handlingUnitLocal.create(expHandlingUnit);
		handlingUnit = handlingUnitLocal.getById(expHandlingUnit.getId());
		assertEquals(expHandlingUnit, handlingUnit);
		assertNotNull(handlingUnit.getUpdateUserId());
		assertNotNull(handlingUnit.getUpdateTimestamp());
		
		LOG.info(handlingUnit);
		
		expHandlingUnit = new HandlingUnit("3", null, null);

		handlingUnitLocal.create(expHandlingUnit);
		handlingUnit = handlingUnitLocal.getById(expHandlingUnit.getId());
		assertEquals(expHandlingUnit, handlingUnit);
		assertNotNull(handlingUnit.getUpdateUserId());
		assertNotNull(handlingUnit.getUpdateTimestamp());
		
		LOG.info(handlingUnit);
		
		expHandlingUnit = new HandlingUnit("4", "Scott Tiger");

		handlingUnitLocal.create(expHandlingUnit);
		handlingUnit = handlingUnitLocal.getById(expHandlingUnit.getId());
		assertEquals(expHandlingUnit, handlingUnit);
		assertEquals(expHandlingUnit.getUpdateUserId(), handlingUnit.getUpdateUserId());
		assertNotNull(handlingUnit.getUpdateTimestamp());
		
		LOG.info(handlingUnit);
		
		expHandlingUnit = new HandlingUnit("5", "Willi", new Timestamp(System.currentTimeMillis()));

		handlingUnitLocal.create(expHandlingUnit);
		handlingUnit = handlingUnitLocal.getById(expHandlingUnit.getId());
		assertEquals(expHandlingUnit, handlingUnit);
		assertEquals(expHandlingUnit.getUpdateUserId(), handlingUnit.getUpdateUserId());
		assertEquals(expHandlingUnit.getUpdateTimestamp(), handlingUnit.getUpdateTimestamp());
		
		LOG.info(handlingUnit);
	}

	/**
	 * Test delete of a simple handling unit with no reference to a location
	 */
	@Test
	@InSequence(2)
	public void deleteByHandlingUnit() {
		LOG.info("--- Test deleteByHandlingUnit");

		assertTrue(handlingUnitLocal.getAll().isEmpty());

		handlingUnitLocal.create(new HandlingUnit("1"));

	    HandlingUnit handlingUnit = handlingUnitLocal.getById("1");
	    
	    // MANDATORY reread
		assertEquals("1", handlingUnit.getId());
		
		// Delete returns the deleted handlingUnit
		handlingUnitLocal.delete(handlingUnit);
		assertNotNull(handlingUnit);

	    // MANDATORY reread
		assertEquals("1", handlingUnit.getId());
		LOG.info(handlingUnit);
	}
	
	/**
	 * Test the getAll method
	 */
	@Test
	@InSequence(3)
	public void getAll() {
		LOG.info("--- Test getAll");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		
		// Prepare some handling units
		handlingUnitLocal.create(new HandlingUnit("1"));
		handlingUnitLocal.create(new HandlingUnit("2"));
		handlingUnitLocal.create(new HandlingUnit("3"));
		handlingUnitLocal.create(new HandlingUnit("4"));
		handlingUnitLocal.create(new HandlingUnit("5"));

		// Another test
		List<HandlingUnit> handlingUnits = handlingUnitLocal.getAll();

		assertFalse(handlingUnits.isEmpty());
		assertEquals(5, handlingUnits.size());
		
		handlingUnits.forEach(hU -> LOG.info(hU));
	}
	
	/**
	 * Test the dropTo method to create reference to a location
	 */
	@Test
	@InSequence(4)
	public void dropTo() {
		LOG.info("--- Test dropTo");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
		
		// Prepare a handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("1"));
		
		locationLocal.create(new Location("A"));
		
	    // MANDATORY reread
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		Location lOA = locationLocal.getById("A");
		
		// Now drop
		handlingUnitLocal.dropTo(lOA, hU1);

	    // MANDATORY reread
		hU1 = handlingUnitLocal.getById("1");
		lOA = locationLocal.getById("A");

		LOG.info(hU1);
		
		// Handling Unit is on location now
		assertEquals(lOA, hU1.getLocation());
		
		assertFalse(lOA.getHandlingUnits().isEmpty());
		assertEquals(1, lOA.getHandlingUnits().size());
		
		// Location must contain handling unit now
		assertTrue(lOA.getHandlingUnits().contains(hU1));
		
		LOG.info(lOA);
	}
	
	/**
	 * Test the dropTo method to create reference to a none existing location
	 */
	@Test(expected = EJBException.class)
	@InSequence(5)
	public void dropToNull() {
		LOG.info("--- Test dropToNull");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
		
		// Prepare a handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("1"));
		
	    // MANDATORY reread
		HandlingUnit hU1 = handlingUnitLocal.getById("1");

		// For the test case
		Location lOA = null;
		
		// Now drop
		handlingUnitLocal.dropTo(lOA, hU1);
	}
	
	/**
	 * Test the pickFrom method to remove the reference to a location
	 * 
	 * @throws LocationIsEmptyException in case the Location is empty
	 * @throws HandlingUnitNotOnLocationException in case the Location does not contain the HandlingUnit
	 */
	@Test
	@InSequence(6)
	public void pickFrom() throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.info("--- Test pickFrom");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
		
		// Prepare a handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("1"));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		Location lOA = locationLocal.getById("A");
		
		// To prepare the pick do a drop before
		handlingUnitLocal.dropTo(lOA, hU1);
		
	    // MANDATORY reread
		hU1 = handlingUnitLocal.getById("1");
		lOA = locationLocal.getById("A");

		// Handling Unit is on location now
		assertEquals(lOA, hU1.getLocation());
		
		assertFalse(lOA.getHandlingUnits().isEmpty());
		assertEquals(1, lOA.getHandlingUnits().size());
		
		// Location must contain handling unit now
		assertTrue(lOA.getHandlingUnits().contains(hU1));
		
		LOG.info(hU1);
		LOG.info(lOA);

		// Now do the pick
		handlingUnitLocal.pickFrom(lOA, hU1);

	    // MANDATORY reread
		hU1 = handlingUnitLocal.getById("1");
		lOA = locationLocal.getById("A");
		
		assertNull(hU1.getLocation());
		assertFalse(lOA.getHandlingUnits().contains(hU1));
		LOG.info(hU1);
		LOG.info(lOA);
	}
	
	/**
	 * Test the pickFrom method to remove the reference to an EMPTY location
	 * 
	 * @throws LocationIsEmptyException in case the Location is empty
	 * @throws HandlingUnitNotOnLocationException in case the Location does not contain the HandlingUnit
	 */
	@Test
	@InSequence(7)
	public void pickFromEmptyLocation() throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.info("--- Test pickFromEmptyLocation");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
				
		// Prepare a handling unit
		handlingUnitLocal.create(new HandlingUnit("1"));
		
		HandlingUnit handlingUnit = handlingUnitLocal.getById("1");
		LOG.info("Prepared: {}", handlingUnit);
		
		// Prepare a location
		locationLocal.create(new Location("A"));
		
		Location location = locationLocal.getById("A");
		LOG.info("Prepared: {}", location);
		
		// Pick now
		try {
			LOG.info("Pick now");
		    handlingUnitLocal.pickFrom(location, handlingUnit);
		    
		    Assert.fail("Expected an LocationIsEmptyException to be thrown");
		}
		catch (LocationIsEmptyException isEmpty) {
			// Location is EMPTY because just newly created
			LOG.info("Expected exception: " + isEmpty.getMessage());
		}
	}

	/**
	 * Test pickFrom a Location that does not contain the handling unit
	 * 
	 * @throws LocationIsEmptyException in case the location is EMPTY
	 * @throws HandlingUnitNotOnLocationException in case the handling unit is not on that location
	 */
	@Test
	@InSequence(8)
	public void pickFromLocationNotContaining() throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.info("--- Test pickFromLocationNotContaining");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
				
		// Prepare handling units and a location
		handlingUnitLocal.create(new HandlingUnit("1"));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		Location lOA = locationLocal.getById("A");
		
		handlingUnitLocal.dropTo(lOA, hU1);
		
	    // MANDATORY reread
		hU1 = handlingUnitLocal.getById("1");
		lOA = locationLocal.getById("A");

		handlingUnitLocal.create(new HandlingUnit("2"));

		HandlingUnit hU2 = handlingUnitLocal.getById("2");
		
		LOG.info(hU2);
		
		// Pick now
		try {
			LOG.info("Pick now");
			handlingUnitLocal.pickFrom(lOA, hU2);
		}
		catch(HandlingUnitNotOnLocationException isNotOnLocation) {
			// Location contains hU1 but not hU2
			LOG.info("Exception: " + isNotOnLocation.getMessage());
		}
		
		// Check location is set to ERROR for manual adjustment (Inventur)
	    // MANDATORY reread
		lOA = locationLocal.getById("A");
		assertEquals(ErrorStatus.ERROR,lOA.getLocationStatus().getErrorStatus());
		
		LOG.info("Locations in ERROR");
		locationLocal.getAllInErrorStatus(ErrorStatus.ERROR).forEach(loc -> LOG.info(loc));
	}
	
	/**
	 * Test delete a handling unit with reference to a location
	 */
	@Test
	@InSequence(9)
	public void deleteHandlingUnitOnLocation() {
		LOG.info("--- Test deleteHandlingUnitOnLocation");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());

		// Prepare handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("1"));
		handlingUnitLocal.create(new HandlingUnit("2"));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		HandlingUnit hU2 = handlingUnitLocal.getById("2");
		Location lOA = locationLocal.getById("A");
		
		// Drop to make a relation
		handlingUnitLocal.dropTo(lOA, hU1);
		
	    // MANDATORY reread
		lOA = locationLocal.getById("A");
		
		handlingUnitLocal.dropTo(lOA, hU2);
		
	    // MANDATORY reread
		hU1 = handlingUnitLocal.getById("1");
		
		// Now delete a handling unit that is related to a location
		LOG.info("Delete: " + hU1);
		handlingUnitLocal.delete(hU1);
		
	    // MANDATORY reread
		hU1 = handlingUnitLocal.getById("1");
		
		assertNull(hU1);
		
	    // MANDATORY reread
		lOA = locationLocal.getById("A");
		
		// Check the location
		assertNotNull(lOA);
		assertFalse(lOA.getHandlingUnits().isEmpty());
		assertTrue(lOA.getHandlingUnits().contains(hU2));
		assertEquals(1, lOA.getHandlingUnits().size());
		
		LOG.info(lOA);
	}
	
	/**
	 * Test a double drop to the same location
	 */
	@Test
	@InSequence(10)
	public void doubleDropSameLocation() {
		LOG.info("--- Test doubleDropSameLocation");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());

		// Prepare handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("2"));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU2 = handlingUnitLocal.getById("2");
		Location lOA = locationLocal.getById("A");
		
		// Drop to make a relation
		handlingUnitLocal.dropTo(lOA, hU2);
		
	    // MANDATORY reread
		hU2 = handlingUnitLocal.getById("2");
		lOA = locationLocal.getById("A");
		LOG.info("First drop: " + hU2);
		LOG.info("First drop: " + lOA);
		
		// Now drop again to same location
		handlingUnitLocal.dropTo(lOA, hU2);
		
	    // MANDATORY reread
		hU2 = handlingUnitLocal.getById("2");
		lOA = locationLocal.getById("A");
		LOG.info("Second drop: " + hU2);
		LOG.info("Second drop: " + lOA);
		
		// Check the location
		assertNotNull(lOA);
		assertFalse(lOA.getHandlingUnits().isEmpty());
		assertTrue(lOA.getHandlingUnits().contains(hU2));
		assertEquals(1, lOA.getHandlingUnits().size());
		LOG.info(lOA);

		// Check the handling unit
		assertNotNull(hU2);
		assertNotNull(hU2.getLocation());
		assertEquals(lOA.getLocationId(), hU2.getLocation().getLocationId());
		LOG.info(hU2);
	}

	/**
	 * Test double drop to other location
	 */
	@Test
	@InSequence(11)
	public void doubleDropOtherLocation() {
		LOG.info("--- Test doubleDropOtherLocation");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());

		// Prepare handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("2"));
		
		locationLocal.create(new Location("A"));
		locationLocal.create(new Location("B"));
		
		HandlingUnit hU2 = handlingUnitLocal.getById("2");
		Location lOA = locationLocal.getById("A");
		Location lOB = locationLocal.getById("B");
		
		// Drop to make a relation
		handlingUnitLocal.dropTo(lOA, hU2);

	    // MANDATORY reread
		hU2 = handlingUnitLocal.getById("2");
		lOA = locationLocal.getById("A");
		lOB = locationLocal.getById("B");
		LOG.info("First drop: " + hU2);
		LOG.info("First drop: " + lOA);
		LOG.info("First drop: " + lOB);
		
		// Now drop again to other location
		handlingUnitLocal.dropTo(lOB, hU2);

	    // MANDATORY reread
		hU2 = handlingUnitLocal.getById("2");
		lOA = locationLocal.getById("A");
		lOB = locationLocal.getById("B");
		LOG.info("Second drop: " + hU2);
		LOG.info("Second drop: " + lOA);
		LOG.info("Second drop: " + lOB);
		
		// Check the locations
		assertNotNull(lOA);
		assertTrue(lOA.getHandlingUnits().isEmpty());
		assertEquals(ErrorStatus.ERROR, lOA.getLocationStatus().getErrorStatus());

		assertNotNull(lOB);
		assertFalse(lOB.getHandlingUnits().isEmpty());
		assertTrue(lOB.getHandlingUnits().contains(hU2));
		assertEquals(1, lOB.getHandlingUnits().size());
		assertEquals(ErrorStatus.NONE, lOB.getLocationStatus().getErrorStatus());

		LOG.info("Locations in ERROR");
		locationLocal.getAllInErrorStatus(ErrorStatus.ERROR).forEach(loc -> LOG.info(loc));
		LOG.info("Locations NOT in ERROR");
		locationLocal.getAllInErrorStatus(ErrorStatus.NONE).forEach(loc -> LOG.info(loc));

		// Check the handling unit
		assertNotNull(hU2);
		assertNotNull(hU2.getLocation());
		assertEquals(lOB.getLocationId(), hU2.getLocation().getLocationId());
	}
}
