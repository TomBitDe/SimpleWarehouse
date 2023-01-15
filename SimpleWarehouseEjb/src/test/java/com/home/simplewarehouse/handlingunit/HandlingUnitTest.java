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

import com.home.simplewarehouse.location.CapacityExceededException;
import com.home.simplewarehouse.location.DimensionException;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusLocal;
import com.home.simplewarehouse.location.OverheightException;
import com.home.simplewarehouse.location.OverlengthException;
import com.home.simplewarehouse.location.OverwidthException;
import com.home.simplewarehouse.location.WeightExceededException;
import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.HeightCategory;
import com.home.simplewarehouse.model.LengthCategory;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.WidthCategory;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the Handling Unit bean.
 */
@RunWith(Arquillian.class)
public class HandlingUnitTest {
	private static final Logger LOG = LogManager.getLogger(HandlingUnitTest.class);

	private static final int CAPACITY_MAX = 2;
	private static final int WEIGHT_MAX = 960;
	private static final HeightCategory HEIGHT_MAX = HeightCategory.MIDDLE;
	private static final WidthCategory WIDTH_MAX = WidthCategory.WIDE;
	private static final LengthCategory LENGTH_MAX = LengthCategory.SHORT;
	
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
	 * What to do before an individual test will be executed (each test)
	 */
	@Before
	public void beforeTest() {
		LOG.trace("--> beforeTest()");
		
		LOG.trace("<-- beforeTest()");		
	}
	
	/**
	 * What to do after an individual test has been executed (each test)<br>
	 * <br>
	 * Cleanup the sample data
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
		
		// Delete the handlingUnit
		handlingUnitLocal.delete(handlingUnit);
		assertNotNull(handlingUnit);

	    // MANDATORY reread
		assertEquals("1", handlingUnit.getId());
		LOG.info(handlingUnit);
		
		// Delete null
		handlingUnit = null;
		handlingUnitLocal.delete(handlingUnit);
		assertTrue(true);
		
		handlingUnitLocal.create(new HandlingUnit("1"));
		handlingUnit = handlingUnitLocal.getById("1");
		assertNotNull(handlingUnit);
		handlingUnit.setId(null);
		handlingUnitLocal.delete(handlingUnit);
		handlingUnit = handlingUnitLocal.getById("1");
		assertNotNull(handlingUnit);
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
		
		try {
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
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
	}
	
	/**
	 * Test the dropTo method to create reference to a none existing location
	 */
	@InSequence(5)
	public void dropToNull() {
		LOG.info("--- Test dropToNull");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
		
		// Prepare a handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("1"));
		
	    // MANDATORY reread
		HandlingUnit hU1 = handlingUnitLocal.getById("1");

		// Check invalid drop to location null
		try {
			handlingUnitLocal.dropTo(null, hU1);

			Assert.fail("Exception expected");
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
		catch (EJBException ex) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test the dropTo method to create reference to a none existing handlingUnit
	 */
	@InSequence(8)
	public void dropNullTo() {
		LOG.info("--- Test dropNullTo");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
		
		// Prepare a handling unit and a location
		locationLocal.create(new Location("A"));
		
	    // MANDATORY reread
		Location lOA = locationLocal.getById("A");

		// Check invalid drop to handlingUnit null
		try {
			handlingUnitLocal.dropTo(lOA, null);

			Assert.fail("Exception expected");
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
		catch (EJBException ex) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test the pickFrom method to remove the reference to a location
	 * 
	 * @throws LocationIsEmptyException in case the Location is empty
	 * @throws HandlingUnitNotOnLocationException in case the Location does not contain the HandlingUnit
	 */
	@Test
	@InSequence(10)
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
		try {
			handlingUnitLocal.dropTo(lOA, hU1);
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
		
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
		
		// Some exceptional cases
		try {
			handlingUnitLocal.pickFrom(null, hU1);
			
			Assert.fail("Expected an Exception to be thrown");
		}
		catch (EJBException ex) {
			// Location is null not allowed
			LOG.info("Expected exception: " + ex.getMessage());
		}

		try {
			handlingUnitLocal.pickFrom(lOA, null);
			
			Assert.fail("Expected an Exception to be thrown");
		}
		catch (EJBException ex) {
			// Location is null not allowed
			LOG.info("Expected exception: " + ex.getMessage());
		}
	}
	
	/**
	 * Test the pickFrom method to remove the reference to an EMPTY location
	 * 
	 * @throws LocationIsEmptyException in case the Location is empty
	 * @throws HandlingUnitNotOnLocationException in case the Location does not contain the HandlingUnit
	 */
	@Test
	@InSequence(14)
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
	@InSequence(18)
	public void pickFromLocationNotContaining() throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.info("--- Test pickFromLocationNotContaining");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
				
		// Prepare handling units and a location
		handlingUnitLocal.create(new HandlingUnit("1"));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		Location lOA = locationLocal.getById("A");
		
		try {
			handlingUnitLocal.dropTo(lOA, hU1);
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
		
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
	@InSequence(20)
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
		try {
			handlingUnitLocal.dropTo(lOA, hU1);
		
			// MANDATORY reread
			lOA = locationLocal.getById("A");
		
			handlingUnitLocal.dropTo(lOA, hU2);
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
		
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
	@InSequence(23)
	public void doubleDropSameLocation() {
		LOG.info("--- Test doubleDropSameLocation");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());

		// Prepare handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("2"));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU2 = handlingUnitLocal.getById("2");
		Location lOA = locationLocal.getById("A");
		
		try {
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
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
	}

	/**
	 * Test double drop to other location
	 */
	@Test
	@InSequence(25)
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

		try {
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
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
	}

	/**
	 * Test drop to location capacity exceeds
	 */
	@Test
	@InSequence(28)
	public void dropToLocationCapacityExceeds() {
		LOG.info("--- Test dropToLocationCapacityExceeds");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());

		// Prepare handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("2"));
		handlingUnitLocal.create(new HandlingUnit("3"));
		handlingUnitLocal.create(new HandlingUnit("4"));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU2 = handlingUnitLocal.getById("2");
		HandlingUnit hU3 = handlingUnitLocal.getById("3");
		HandlingUnit hU4 = handlingUnitLocal.getById("4");
		Location lOA = locationLocal.getById("A");
		
		// Now set the capacity to limit
		lOA.getDimension().setMaxCapacity(2);

		try {
			// Drop to make a relation
			handlingUnitLocal.dropTo(lOA, hU2);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(lOA, hU3);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
		}
		catch (DimensionException dimex) {
			Assert.fail("Unexpected exception: " +  dimex.getMessage());
		}

		try {
			handlingUnitLocal.dropTo(lOA, hU4);
			// MANDATORY reread
			lOA = locationLocal.getById("A");

			Assert.fail("Exception expected");
		}
		catch (CapacityExceededException capex) {
			assertTrue(true);
			LOG.info(capex.getMessage());

			// MANDATORY reread
			lOA = locationLocal.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertEquals(CAPACITY_MAX, lOA.getHandlingUnits().size());
		}
		catch (DimensionException wex) {
			Assert.fail("Unexpected exception: " +  wex.getMessage());
		}
	}

	/**
	 * Test drop to location weight exceeds
	 */
	@Test
	@InSequence(30)
	public void dropToLocationWeightExceeds() {
		LOG.info("--- Test dropToLocationWeightExceeds");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());

		// Prepare handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("2", 500));
		handlingUnitLocal.create(new HandlingUnit("3", 300));
		handlingUnitLocal.create(new HandlingUnit("4", 190));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU2 = handlingUnitLocal.getById("2");
		HandlingUnit hU3 = handlingUnitLocal.getById("3");
		HandlingUnit hU4 = handlingUnitLocal.getById("4");
		Location lOA = locationLocal.getById("A");
		
		// Now set the weight to limit
		lOA.getDimension().setMaxWeight(WEIGHT_MAX);

		try {
			// Drop to make a relation
			handlingUnitLocal.dropTo(lOA, hU2);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(lOA, hU3);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
		}
		catch (DimensionException dimex) {
			Assert.fail("Unexpected exception: " +  dimex.getMessage());
		}

		try {
			handlingUnitLocal.dropTo(lOA, hU4);
			// MANDATORY reread
			lOA = locationLocal.getById("A");

			Assert.fail("Exception expected");
		}
		catch (WeightExceededException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationLocal.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(locationLocal.overweight(lOA, hU4.getWeight()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}
	}

	/**
	 * Test drop to location overheight
	 */
	@Test
	@InSequence(33)
	public void dropToLocationOverheight() {
		LOG.info("--- Test dropToLocationOverheight");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());

		// Prepare handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("1", 0, 0.0f, HeightCategory.MIDDLE));
		handlingUnitLocal.create(new HandlingUnit("2", 0, 0.0f, HeightCategory.HIGH));
		handlingUnitLocal.create(new HandlingUnit("3", 0, 0.0f, HeightCategory.LOW));
		handlingUnitLocal.create(new HandlingUnit("4", 0, 0.0f, HeightCategory.TOO_HIGH));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		HandlingUnit hU2 = handlingUnitLocal.getById("2");
		HandlingUnit hU3 = handlingUnitLocal.getById("3");
		HandlingUnit hU4 = handlingUnitLocal.getById("4");
		Location lOA = locationLocal.getById("A");
		
		// Now set the height to limit
		lOA.getDimension().setMaxHeight(HEIGHT_MAX);

		try {
			// Drop to make a relation
			handlingUnitLocal.dropTo(lOA, hU1);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
			// Drop to make a relation
			handlingUnitLocal.dropTo(lOA, hU3);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
		}
		catch (DimensionException dimex) {
			Assert.fail("Unexpected exception: " +  dimex.getMessage());
		}

		try {
			handlingUnitLocal.dropTo(lOA, hU2);
			// MANDATORY reread
			lOA = locationLocal.getById("A");

			Assert.fail("Exception expected");
		}
		catch (OverheightException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationLocal.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(locationLocal.overheight(lOA, hU2.getHeight()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}

		try {
			handlingUnitLocal.dropTo(lOA, hU4);
			// MANDATORY reread
			lOA = locationLocal.getById("A");

			Assert.fail("Exception expected");
		}
		catch (OverheightException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationLocal.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(locationLocal.overheight(lOA, hU4.getHeight()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}
	}

	/**
	 * Test drop to location overheight
	 */
	@Test
	@InSequence(36)
	public void dropToLocationOverwidth() {
		LOG.info("--- Test dropToLocationOverwidth");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());

		// Prepare handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("1", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.NOT_RELEVANT, WidthCategory.NARROW));
		handlingUnitLocal.create(new HandlingUnit("2", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.NOT_RELEVANT, WidthCategory.MIDDLE));
		handlingUnitLocal.create(new HandlingUnit("3", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.NOT_RELEVANT, WidthCategory.WIDE));
		handlingUnitLocal.create(new HandlingUnit("4", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.NOT_RELEVANT, WidthCategory.TOO_WIDE));
		handlingUnitLocal.create(new HandlingUnit("5", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.NOT_RELEVANT, WidthCategory.UNKNOWN));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		HandlingUnit hU2 = handlingUnitLocal.getById("2");
		HandlingUnit hU3 = handlingUnitLocal.getById("3");
		HandlingUnit hU4 = handlingUnitLocal.getById("4");
		HandlingUnit hU5 = handlingUnitLocal.getById("5");
		Location lOA = locationLocal.getById("A");
		
		// Now set the height to limit
		lOA.getDimension().setMaxWidth(WIDTH_MAX);

		try {
			// Drop to make a relation
			handlingUnitLocal.dropTo(lOA, hU1);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
			// Drop to make a relation
			handlingUnitLocal.dropTo(lOA, hU2);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
			handlingUnitLocal.dropTo(lOA, hU3);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
		}
		catch (DimensionException dimex) {
			Assert.fail("Unexpected exception: " +  dimex.getMessage());
		}

		try {
			handlingUnitLocal.dropTo(lOA, hU4);
			// MANDATORY reread
			lOA = locationLocal.getById("A");

			Assert.fail("Exception expected");
		}
		catch (OverwidthException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationLocal.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(locationLocal.overwidth(lOA, hU4.getWidth()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}

		try {
			handlingUnitLocal.dropTo(lOA, hU5);
			// MANDATORY reread
			lOA = locationLocal.getById("A");

			Assert.fail("Exception expected");
		}
		catch (OverwidthException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationLocal.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(locationLocal.overwidth(lOA, hU5.getWidth()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}
	}

	/**
	 * Test drop to location overlength
	 */
	@Test
	@InSequence(39)
	public void dropToLocationOverlength() {
		LOG.info("--- Test dropToLocationOverlength");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());

		// Prepare handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("1", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.SHORT));
		handlingUnitLocal.create(new HandlingUnit("2", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.MIDDLE));
		handlingUnitLocal.create(new HandlingUnit("3", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.LONG));
		handlingUnitLocal.create(new HandlingUnit("4", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.TOO_LONG));
		handlingUnitLocal.create(new HandlingUnit("5", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.UNKNOWN));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		HandlingUnit hU2 = handlingUnitLocal.getById("2");
		HandlingUnit hU3 = handlingUnitLocal.getById("3");
		HandlingUnit hU4 = handlingUnitLocal.getById("4");
		HandlingUnit hU5 = handlingUnitLocal.getById("5");
		Location lOA = locationLocal.getById("A");
		
		// Now set the height to limit
		lOA.getDimension().setMaxLength(LENGTH_MAX);
		
		try {
			// Drop to make a relation
			handlingUnitLocal.dropTo(lOA, hU1);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
		}
		catch (DimensionException dimex) {
			Assert.fail("Unexpected exception: " +  dimex.getMessage());
		}

		try {
			handlingUnitLocal.dropTo(lOA, hU2);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
			
			handlingUnitLocal.dropTo(lOA, hU3);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
			
			handlingUnitLocal.dropTo(lOA, hU4);
			// MANDATORY reread
			lOA = locationLocal.getById("A");
			
			handlingUnitLocal.dropTo(lOA, hU5);
			// MANDATORY reread
			lOA = locationLocal.getById("A");

			Assert.fail("Exception expected");
		}
		catch (OverlengthException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationLocal.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			
			assertTrue(locationLocal.overlength(lOA, hU2.getLength()));
			assertTrue(locationLocal.overlength(lOA, hU3.getLength()));
			assertTrue(locationLocal.overlength(lOA, hU4.getLength()));
			assertTrue(locationLocal.overlength(lOA, hU5.getLength()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}
	}
}
