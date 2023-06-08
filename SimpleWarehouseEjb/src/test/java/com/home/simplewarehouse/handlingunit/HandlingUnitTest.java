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
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusService;
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

	private static final int WEIGHT_960 = 960;
	
	@EJB
	HandlingUnitService handlingUnitService;
	
	@EJB
	LocationService locationService;
	
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
						HandlingUnitService.class, HandlingUnitBean.class,
						LocationService.class, LocationBean.class,
						LocationStatusService.class, LocationStatusBean.class,
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
		List<Location> locations = locationService.getAll();
		
		locations.stream().forEach(l -> locationService.delete(l));
		
		// Cleanup handling units
		List<HandlingUnit> handlingUnits = handlingUnitService.getAll();
		
		handlingUnits.stream().forEach(h -> handlingUnitService.delete(h));		

		LOG.trace("<-- afterTest()");		
	}

	/**
	 * Simple handling unit null or with id null
	 */
	@Test
	@InSequence(0)
	public void createNullOrWithIdNull() {
		LOG.info("--- Test createNullOrWithIdNull");

		assertTrue(handlingUnitService.getAll().isEmpty());

		HandlingUnit handlingUnit = null;
		
		try {
			handlingUnit = handlingUnitService.createOrUpdate(null);

			Assert.fail("Exception expected");
		}
		catch (EJBException ejbex) {
			LOG.info(ejbex.getMessage());
		}

		try {
			handlingUnit = handlingUnitService.createOrUpdate(new HandlingUnit(null));

			Assert.fail("Exception expected");
		}
		catch (EJBException ejbex) {
			LOG.info(ejbex.getMessage());
		}
		
		assertNull(handlingUnit);
	}

	/**
	 * Simple handling unit with no reference to a locationService
	 */
	@Test
	@InSequence(1)
	public void createAndGetById() {
		LOG.info("--- Test createAndGetById");

		assertTrue(handlingUnitService.getAll().isEmpty());

		HandlingUnit expHandlingUnit = new HandlingUnit("1");
		
		assertEquals(false, expHandlingUnit.equals(null));
		assertEquals(false, expHandlingUnit.equals(new Location("A")));

		HandlingUnit handlingUnit = handlingUnitService.createOrUpdate(expHandlingUnit);
		assertEquals(expHandlingUnit, handlingUnit);
		
		LOG.info(handlingUnit);
		
		expHandlingUnit.setVolume(10.0f);
		expHandlingUnit.setHeight(HeightCategory.MIDDLE);
		expHandlingUnit.setWeight(WEIGHT_960);
		
		handlingUnit = handlingUnitService.createOrUpdate(expHandlingUnit);
		assertEquals(expHandlingUnit, handlingUnit);
		
		LOG.info(handlingUnit);
		
		expHandlingUnit = new HandlingUnit("2", null);

		handlingUnit = handlingUnitService.createOrUpdate(expHandlingUnit);
		assertEquals(expHandlingUnit, handlingUnit);
		assertNotNull(handlingUnit.getUpdateUserId());
		assertNotNull(handlingUnit.getUpdateTimestamp());
		
		LOG.info(handlingUnit);
		
		expHandlingUnit = new HandlingUnit("3", null, null);

		handlingUnit = handlingUnitService.createOrUpdate(expHandlingUnit);
		assertEquals(expHandlingUnit, handlingUnit);
		assertNotNull(handlingUnit.getUpdateUserId());
		assertNotNull(handlingUnit.getUpdateTimestamp());
		
		LOG.info(handlingUnit);
		
		expHandlingUnit = new HandlingUnit("4", "Scott Tiger");

		handlingUnit = handlingUnitService.createOrUpdate(expHandlingUnit);
		assertEquals(expHandlingUnit, handlingUnit);
		assertEquals(expHandlingUnit.getUpdateUserId(), handlingUnit.getUpdateUserId());
		assertNotNull(handlingUnit.getUpdateTimestamp());
		
		LOG.info(handlingUnit);
		
		expHandlingUnit = new HandlingUnit("5", "Willi", new Timestamp(System.currentTimeMillis()));

		handlingUnit = handlingUnitService.createOrUpdate(expHandlingUnit);
		assertEquals(expHandlingUnit, handlingUnit);
		assertEquals(expHandlingUnit.getUpdateUserId(), handlingUnit.getUpdateUserId());
		assertEquals(expHandlingUnit.getUpdateTimestamp(), handlingUnit.getUpdateTimestamp());
		
		LOG.info(handlingUnit);

		expHandlingUnit = new HandlingUnit("6", -10, -20.0f);

		handlingUnit = handlingUnitService.createOrUpdate(expHandlingUnit);
		assertEquals(HandlingUnit.WEIGHT_DEFAULT, handlingUnit.getWeight());
		assertEquals(HandlingUnit.VOLUME_DEFAULT, handlingUnit.getVolume(), 0.0f);

		LOG.info(handlingUnit);	

		expHandlingUnit = new HandlingUnit("7");
		expHandlingUnit.setHeight(null);
		expHandlingUnit.setLength(null);
		expHandlingUnit.setWidth(null);
		handlingUnit = handlingUnitService.createOrUpdate(expHandlingUnit);
		assertEquals(HeightCategory.NOT_RELEVANT, handlingUnit.getHeight());
		assertEquals(LengthCategory.NOT_RELEVANT, handlingUnit.getLength());
		assertEquals(WidthCategory.NOT_RELEVANT, handlingUnit.getWidth());
		
		assertEquals(1, expHandlingUnit.getVersion());
			
		LOG.info(handlingUnit);	
	}

	/**
	 * Test delete of a simple handling unit with no reference to a locationService
	 */
	@Test
	@InSequence(2)
	public void deleteByHandlingUnit() {
		LOG.info("--- Test deleteByHandlingUnit");

		assertTrue(handlingUnitService.getAll().isEmpty());

	    HandlingUnit handlingUnit = handlingUnitService.createOrUpdate(new HandlingUnit("1"));
	    
		assertEquals("1", handlingUnit.getId());
		
		// Delete the handlingUnit
		handlingUnitService.delete(handlingUnit);
		assertNull(handlingUnitService.getById("1"));

		// Delete null
		handlingUnit = null;
		handlingUnitService.delete(handlingUnit);
		assertTrue(true);
		
		handlingUnit = handlingUnitService.createOrUpdate(new HandlingUnit("1"));
		assertNotNull(handlingUnit);
		handlingUnit.setId(null);
		handlingUnitService.delete(handlingUnit);
		handlingUnit = handlingUnitService.getById("1");
		assertNotNull(handlingUnit);
		
		// "1" still exists because delete above was done with null
		// now delete with String id
		handlingUnitService.delete("1");
		handlingUnit = handlingUnitService.getById("1");
		assertNull(handlingUnit);

		handlingUnitService.delete("99");
		handlingUnit = handlingUnitService.getById("99");
		assertNull(handlingUnit);
		
		try {
			handlingUnitService.delete((String)null);

			Assert.fail("Exception expected");
		}
		catch (EJBException ex) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test the getAll method
	 */
	@Test
	@InSequence(3)
	public void getAll() {
		LOG.info("--- Test getAll");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		
		// Prepare some handling units
		handlingUnitService.createOrUpdate(new HandlingUnit("1"));
		handlingUnitService.createOrUpdate(new HandlingUnit("2"));
		handlingUnitService.createOrUpdate(new HandlingUnit("3"));
		handlingUnitService.createOrUpdate(new HandlingUnit("4"));
		handlingUnitService.createOrUpdate(new HandlingUnit("5"));

		// Another test
		List<HandlingUnit> handlingUnits = handlingUnitService.getAll();

		assertFalse(handlingUnits.isEmpty());
		assertEquals(5, handlingUnits.size());
		
		handlingUnits.forEach(hU -> LOG.info(hU));

		handlingUnits = handlingUnitService.getAll(3, 9);
		assertEquals(2, handlingUnits.size());
		
		try {
			handlingUnits = handlingUnitService.getAll(-3, 3);
			
			Assert.fail("Exception expected");
		}
		catch (EJBException ex) {
			assertTrue(true);
		}

		try {
			handlingUnits = handlingUnitService.getAll(2, 0);
			
			Assert.fail("Exception expected");
		}
		catch (EJBException ex) {
			assertTrue(true);
		}
		
		assertEquals(handlingUnitService.getAll().size(), handlingUnitService.count());
	}
	
	/**
	 * Test the dropTo method to create reference to a locationService
	 */
	@Test
	@InSequence(4)
	public void dropTo() {
		LOG.info("--- Test dropTo");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());
		
		try {
			// Now drop
			handlingUnitService.dropTo(new Location("A"), new HandlingUnit("1"));

			// MANDATORY reread
			HandlingUnit hU1 = handlingUnitService.getById("1");
			Location lOA = locationService.getById("A");

			LOG.info(hU1);

			// Handling Unit is on locationService now
			assertEquals(lOA, hU1.getLocation());

			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertEquals(1, lOA.getHandlingUnits().size());

			// Location must contain handling unit now
			assertTrue(lOA.getHandlingUnits().contains(hU1));

			LOG.info(lOA);
			
			// Now by id
			HandlingUnit hU2 = handlingUnitService.createOrUpdate(new HandlingUnit("2"));
			Location lOB = locationService.createOrUpdate(new Location("B"));
			
			handlingUnitService.dropTo("B", "2");

			// MANDATORY reread
			hU2 = handlingUnitService.getById("2");
			lOB = locationService.getById("B");

			LOG.info(hU2);

			// Handling Unit is on locationService now
			assertEquals(lOB, hU2.getLocation());

			assertFalse(lOB.getHandlingUnits().isEmpty());
			assertEquals(1, lOB.getHandlingUnits().size());

			// Location must contain handling unit now
			assertTrue(lOB.getHandlingUnits().contains(hU2));

			LOG.info(lOB);
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
	}
	
	/**
	 * Test the dropTo method to create reference to a none existing locationService
	 */
	@Test
	@InSequence(5)
	public void dropToNull() {
		LOG.info("--- Test dropToNull");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());
		
		HandlingUnit hu1 = new HandlingUnit("1");
		
		// Check invalid drop to locationService null
		try {
			handlingUnitService.dropTo(null, hu1);

			Assert.fail("Exception expected");
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
		catch (EJBException ex) {
			assertTrue(true);
		}

		// Now by id
		try {
			handlingUnitService.dropTo(null, "1");

			Assert.fail("Exception expected");
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
		catch (EJBException ejbex) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test the dropTo method to create reference to a none existing handlingUnitService
	 */
	@Test
	@InSequence(8)
	public void dropNullTo() {
		LOG.info("--- Test dropNullTo");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());
		
		// Check invalid drop to handlingUnitService null
		try {
			handlingUnitService.dropTo(new Location("A"), null);

			assertTrue("No exception expected", true);
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}

		try {
			handlingUnitService.dropTo("A", null);

			Assert.fail("Exception expected");
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
		catch (EJBException ejbex) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test the pickFrom method to remove the reference to a locationService
	 * 
	 * @throws LocationIsEmptyException in case the Location is empty
	 * @throws HandlingUnitNotOnLocationException in case the Location does not contain the HandlingUnit
	 */
	@Test
	@InSequence(10)
	public void pickFrom() throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.info("--- Test pickFrom");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());
		
		// To prepare the pick do a drop before
		try {
			handlingUnitService.dropTo(new Location("A"), new HandlingUnit("1"));
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
		
	    // MANDATORY reread
		HandlingUnit hU1 = handlingUnitService.getById("1");
		Location lOA = locationService.getById("A");

		// Handling Unit is on location now
		assertEquals(lOA, hU1.getLocation());
		
		assertFalse(lOA.getHandlingUnits().isEmpty());
		assertEquals(1, lOA.getHandlingUnits().size());
		
		// Location must contain handling unit now
		assertTrue(lOA.getHandlingUnits().contains(hU1));
		
		LOG.info(hU1);
		LOG.info(lOA);

		// Now do the pick
		handlingUnitService.pickFrom(lOA, hU1);

	    // MANDATORY reread
		hU1 = handlingUnitService.getById("1");
		lOA = locationService.getById("A");
		
		assertNull(hU1.getLocation());
		assertFalse(lOA.getHandlingUnits().contains(hU1));
		LOG.info(hU1);
		LOG.info(lOA);
		
		// Some exceptional cases
		try {
			handlingUnitService.pickFrom(null, hU1);
			
			Assert.fail("Expected an Exception to be thrown");
		}
		catch (EJBException ex) {
			// Location is null not allowed
			LOG.info("Expected exception: " + ex.getMessage());
		}

		try {
			handlingUnitService.pickFrom(lOA, null);
			
			Assert.fail("Expected an Exception to be thrown");
		}
		catch (EJBException ex) {
			// Location is null not allowed
			LOG.info("Expected exception: " + ex.getMessage());
		}

		// Now with ids
		try {
			handlingUnitService.dropTo(new Location("B"), new HandlingUnit("2"));
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
		
	    // MANDATORY reread
		HandlingUnit hU2 = handlingUnitService.getById("2");
		Location lOB = locationService.getById("B");

		// Handling Unit is on location now
		assertEquals(lOB, hU2.getLocation());
		
		// Now do the pick
		handlingUnitService.pickFrom(lOB.getLocationId(), hU2.getId());

	    // MANDATORY reread
		hU2 = handlingUnitService.getById("2");
		lOB = locationService.getById("B");
		
		assertNull(hU2.getLocation());
		assertFalse(lOB.getHandlingUnits().contains(hU2));
		
		try {
			handlingUnitService.pickFrom("B", (String)null);
			
			Assert.fail("Expected an Exception to be thrown");
		}
		catch (EJBException ex) {
			// Location is null not allowed
			LOG.info("Expected exception: " + ex.getMessage());
		}
		
		try {
			handlingUnitService.pickFrom((String)null, (String)null);
			
			Assert.fail("Expected an Exception to be thrown");
		}
		catch (EJBException ex) {
			// Location is null not allowed
			LOG.info("Expected exception: " + ex.getMessage());
		}
		
		LOG.info(hU2);
		LOG.info(lOB);
	}
	
	/**
	 * Test the pickFrom method to remove the reference to an EMPTY locationService
	 * 
	 * @throws LocationIsEmptyException in case the Location is empty
	 * @throws HandlingUnitNotOnLocationException in case the Location does not contain the HandlingUnit
	 */
	@Test
	@InSequence(14)
	public void pickFromEmptyLocation() throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.info("--- Test pickFromEmptyLocation");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());
				
		// Pick now
		try {
			LOG.info("Pick now");
		    handlingUnitService.pickFrom(new Location("A"), new HandlingUnit("1"));
		    
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
	 * @throws LocationIsEmptyException in case the locationService is EMPTY
	 * @throws HandlingUnitNotOnLocationException in case the handling unit is not on that locationService
	 */
	@Test
	@InSequence(18)
	public void pickFromLocationNotContaining() throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.info("--- Test pickFromLocationNotContaining");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());
				
		try {
			handlingUnitService.dropTo(new Location("A"), new HandlingUnit("1"));
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
		
	    // MANDATORY reread
		Location lOA = locationService.getById("A");

		// Pick now
		try {
			LOG.info("Pick now");
			handlingUnitService.pickFrom(lOA, new HandlingUnit("2"));
		}
		catch(HandlingUnitNotOnLocationException isNotOnLocation) {
			// Location contains hU1 but not hU2
			LOG.info("Exception: " + isNotOnLocation.getMessage());
		}
		
		// Check locationService is set to ERROR for manual adjustment (Inventur)
	    // MANDATORY reread
		lOA = locationService.getById("A");
		assertEquals(ErrorStatus.ERROR,lOA.getLocationStatus().getErrorStatus());
		
		LOG.info("Locations in ERROR");
		locationService.getAllInErrorStatus(ErrorStatus.ERROR).forEach(loc -> LOG.info(loc));
	}
	
	/**
	 * Test delete a handling unit with reference to a locationService
	 */
	@Test
	@InSequence(20)
	public void deleteHandlingUnitOnLocation() {
		LOG.info("--- Test deleteHandlingUnitOnLocation");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());

		Location lOA;
		
		// Drop to make a relation
		try {
			handlingUnitService.dropTo(new Location("A"), new HandlingUnit("1"));
		
			// MANDATORY reread
			lOA = locationService.getById("A");
		
			handlingUnitService.dropTo(lOA, new HandlingUnit("2"));

			// MANDATORY reread
			HandlingUnit hU1 = handlingUnitService.getById("1");
			HandlingUnit hU2 = handlingUnitService.getById("2");
			
			// Now delete a handling unit that is related to a locationService
			LOG.info("Delete: " + hU1);
			handlingUnitService.delete(hU1);
			
		    // MANDATORY reread
			hU1 = handlingUnitService.getById("1");
			
			assertNull(hU1);
			
		    // MANDATORY reread
			lOA = locationService.getById("A");
			
			// Check the locationService
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(lOA.getHandlingUnits().contains(hU2));
			assertEquals(1, lOA.getHandlingUnits().size());
			
			LOG.info(lOA);
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
	}
	
	/**
	 * Test a double drop to the same locationService
	 */
	@Test
	@InSequence(23)
	public void doubleDropSameLocation() {
		LOG.info("--- Test doubleDropSameLocation");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());

		try {
			// Drop to make a relation
			handlingUnitService.dropTo(new Location("A"), new HandlingUnit("2"));

			// MANDATORY reread
			HandlingUnit hU2 = handlingUnitService.getById("2");
			Location lOA = locationService.getById("A");
			LOG.info("First drop: " + hU2);
			LOG.info("First drop: " + lOA);

			// Now drop again to same locationService
			handlingUnitService.dropTo(lOA, hU2);

			// MANDATORY reread
			hU2 = handlingUnitService.getById("2");
			lOA = locationService.getById("A");
			LOG.info("Second drop: " + hU2);
			LOG.info("Second drop: " + lOA);

			// Check the locationService
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
	 * Test double drop to other locationService
	 */
	@Test
	@InSequence(25)
	public void doubleDropOtherLocation() {
		LOG.info("--- Test doubleDropOtherLocation");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());

		try {
			// Drop to make a relation
			handlingUnitService.dropTo(new Location("A"), new HandlingUnit("2"));

			// MANDATORY reread
			HandlingUnit hU2 = handlingUnitService.getById("2");
			Location lOA = locationService.getById("A");
			LOG.info("First drop: " + hU2);
			LOG.info("First drop: " + lOA);

			// Now drop again to other locationService
			handlingUnitService.dropTo(new Location("B"), hU2);

			// MANDATORY reread
			hU2 = handlingUnitService.getById("2");
			lOA = locationService.getById("A");
			Location lOB = locationService.getById("B");
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
			locationService.getAllInErrorStatus(ErrorStatus.ERROR).forEach(loc -> LOG.info(loc));
			LOG.info("Locations NOT in ERROR");
			locationService.getAllInErrorStatus(ErrorStatus.NONE).forEach(loc -> LOG.info(loc));

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
	 * Test drop to locationService capacity exceeds
	 */
	@Test
	@InSequence(28)
	public void dropToLocationCapacityExceeds() {
		LOG.info("--- Test dropToLocationCapacityExceeds");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());

		// Prepare a locationService
		locationService.createOrUpdate(new Location("A"));		
		Location lOA = locationService.getById("A");
		
		// Now set the capacity to limit
		lOA.getDimension().setMaxCapacity(2);

		try {
			// Drop to make a relation
			handlingUnitService.dropTo(lOA, new HandlingUnit("2"));
			// MANDATORY reread
			lOA = locationService.getById("A");
			handlingUnitService.dropTo(lOA, new HandlingUnit("3"));
			// MANDATORY reread
			lOA = locationService.getById("A");
		}
		catch (DimensionException dimex) {
			Assert.fail("Unexpected exception: " +  dimex.getMessage());
		}

		try {
			handlingUnitService.dropTo(lOA, new HandlingUnit("4"));
			// MANDATORY reread
			lOA = locationService.getById("A");

			Assert.fail("Exception expected");
		}
		catch (CapacityExceededException capex) {
			assertTrue(true);
			LOG.info(capex.getMessage());

			// MANDATORY reread
			lOA = locationService.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertEquals(2, lOA.getHandlingUnits().size());
		}
		catch (DimensionException wex) {
			Assert.fail("Unexpected exception: " +  wex.getMessage());
		}
	}

	/**
	 * Test drop to locationService weight exceeds
	 */
	@Test
	@InSequence(30)
	public void dropToLocationWeightExceeds() {
		LOG.info("--- Test dropToLocationWeightExceeds");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());

		// Prepare handling unit and a locationService
		locationService.createOrUpdate(new Location("A"));
		Location lOA = locationService.getById("A");
		
		// Now set the weight to limit
		lOA.getDimension().setMaxWeight(WEIGHT_960);

		try {
			// Drop to make a relation
			handlingUnitService.dropTo(lOA, new HandlingUnit("2", 500));
			// MANDATORY reread
			lOA = locationService.getById("A");
			handlingUnitService.dropTo(lOA, new HandlingUnit("3", 300));
			// MANDATORY reread
			lOA = locationService.getById("A");
		}
		catch (DimensionException dimex) {
			Assert.fail("Unexpected exception: " +  dimex.getMessage());
		}

		try {
			handlingUnitService.dropTo(lOA, new HandlingUnit("4", 190));
			// MANDATORY reread
			lOA = locationService.getById("A");

			Assert.fail("Exception expected");
		}
		catch (WeightExceededException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationService.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(locationService.overweight(lOA, handlingUnitService.getById("4").getWeight()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}
	}

	/**
	 * Test drop to locationService overheight
	 */
	@Test
	@InSequence(33)
	public void dropToLocationOverheight() {
		LOG.info("--- Test dropToLocationOverheight");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());

		// Prepare a location
		Location lOA = locationService.createOrUpdate(new Location("A"));
		
		// Now set the height to limit
		lOA.getDimension().setMaxHeight(HeightCategory.MIDDLE);
		lOA = locationService.createOrUpdate(lOA);

		try {
			// Drop to make a relation
			handlingUnitService.dropTo(lOA, new HandlingUnit("1", 0, 0.0f, HeightCategory.MIDDLE));
			// MANDATORY reread
			lOA = locationService.getById("A");
			// Drop to make a relation
			handlingUnitService.dropTo(lOA, new HandlingUnit("3", 0, 0.0f, HeightCategory.LOW));
			// MANDATORY reread
			lOA = locationService.getById("A");
		}
		catch (DimensionException dimex) {
			Assert.fail("Unexpected exception: " +  dimex.getMessage());
		}

		try {
			handlingUnitService.dropTo(lOA, new HandlingUnit("2", 0, 0.0f, HeightCategory.HIGH));
			// MANDATORY reread
			lOA = locationService.getById("A");

			Assert.fail("Exception expected");
		}
		catch (OverheightException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationService.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(locationService.overheight(lOA, handlingUnitService.getById("2").getHeight()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}

		try {
			handlingUnitService.dropTo(lOA, new HandlingUnit("4", 0, 0.0f, HeightCategory.TOO_HIGH));
			// MANDATORY reread
			lOA = locationService.getById("A");

			Assert.fail("Exception expected");
		}
		catch (OverheightException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationService.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(locationService.overheight(lOA, handlingUnitService.getById("4").getHeight()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}

		lOA.getDimension().setMaxHeight(HeightCategory.LOW);
		try {
			handlingUnitService.dropTo(lOA, new HandlingUnit("5", 0, 0.0f, HeightCategory.MIDDLE));

			Assert.fail("Exception expected");
		}
		catch (OverheightException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationService.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(locationService.overheight(lOA, handlingUnitService.getById("5").getHeight()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}

		try {
			handlingUnitService.dropTo(lOA, new HandlingUnit("6", 0, 0.0f, HeightCategory.HIGH));

			Assert.fail("Exception expected");
		}
		catch (OverheightException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationService.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(locationService.overheight(lOA, handlingUnitService.getById("6").getHeight()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}
	}

	/**
	 * Test drop to locationService overwidth
	 */
	@Test
	@InSequence(36)
	public void dropToLocationOverwidth() {
		LOG.info("--- Test dropToLocationOverwidth");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());

		// Prepare handling unit and a locationService
		HandlingUnit hU1 = handlingUnitService.createOrUpdate(new HandlingUnit("1", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.NOT_RELEVANT, WidthCategory.NARROW));
		HandlingUnit hU2 = handlingUnitService.createOrUpdate(new HandlingUnit("2", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.NOT_RELEVANT, WidthCategory.MIDDLE));
		HandlingUnit hU3 = handlingUnitService.createOrUpdate(new HandlingUnit("3", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.NOT_RELEVANT, WidthCategory.WIDE));
		HandlingUnit hU4 = handlingUnitService.createOrUpdate(new HandlingUnit("4", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.NOT_RELEVANT, WidthCategory.TOO_WIDE));
		HandlingUnit hU5 = handlingUnitService.createOrUpdate(new HandlingUnit("5", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.NOT_RELEVANT, WidthCategory.UNKNOWN));
		
		locationService.createOrUpdate(new Location("A"));
		Location lOA = locationService.getById("A");
		
		// Now set the height to limit
		lOA.getDimension().setMaxWidth(WidthCategory.WIDE);

		try {
			// Drop to make a relation
			handlingUnitService.dropTo(lOA, hU1);
			// MANDATORY reread
			lOA = locationService.getById("A");
			// Drop to make a relation
			handlingUnitService.dropTo(lOA, hU2);
			// MANDATORY reread
			lOA = locationService.getById("A");
			handlingUnitService.dropTo(lOA, hU3);
			// MANDATORY reread
			lOA = locationService.getById("A");
		}
		catch (DimensionException dimex) {
			Assert.fail("Unexpected exception: " +  dimex.getMessage());
		}

		try {
			handlingUnitService.dropTo(lOA, hU4);
			// MANDATORY reread
			lOA = locationService.getById("A");

			Assert.fail("Exception expected");
		}
		catch (OverwidthException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationService.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(locationService.overwidth(lOA, hU4.getWidth()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}

		try {
			handlingUnitService.dropTo(lOA, hU5);
			// MANDATORY reread
			lOA = locationService.getById("A");

			Assert.fail("Exception expected");
		}
		catch (OverwidthException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationService.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			assertTrue(locationService.overwidth(lOA, hU5.getWidth()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}
	}

	/**
	 * Test drop to locationService overlength
	 */
	@Test
	@InSequence(39)
	public void dropToLocationOverlength() {
		LOG.info("--- Test dropToLocationOverlength");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());

		// Prepare handling unit and a locationService
		HandlingUnit hU1 = handlingUnitService.createOrUpdate(new HandlingUnit("1", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.SHORT));
		HandlingUnit hU2 = handlingUnitService.createOrUpdate(new HandlingUnit("2", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.MIDDLE));
		HandlingUnit hU3 = handlingUnitService.createOrUpdate(new HandlingUnit("3", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.LONG));
		HandlingUnit hU4 = handlingUnitService.createOrUpdate(new HandlingUnit("4", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.TOO_LONG));
		HandlingUnit hU5 = handlingUnitService.createOrUpdate(new HandlingUnit("5", 0, 0.0f, HeightCategory.NOT_RELEVANT, LengthCategory.UNKNOWN));
		
		locationService.createOrUpdate(new Location("A"));
		Location lOA = locationService.getById("A");
		
		// Now set the height to limit
		lOA.getDimension().setMaxLength(LengthCategory.SHORT);
		
		try {
			// Drop to make a relation
			handlingUnitService.dropTo(lOA, hU1);
			// MANDATORY reread
			lOA = locationService.getById("A");
		}
		catch (DimensionException dimex) {
			Assert.fail("Unexpected exception: " +  dimex.getMessage());
		}

		try {
			handlingUnitService.dropTo(lOA, hU2);
			// MANDATORY reread
			lOA = locationService.getById("A");
			
			handlingUnitService.dropTo(lOA, hU3);
			// MANDATORY reread
			lOA = locationService.getById("A");
			
			handlingUnitService.dropTo(lOA, hU4);
			// MANDATORY reread
			lOA = locationService.getById("A");
			
			handlingUnitService.dropTo(lOA, hU5);
			// MANDATORY reread
			lOA = locationService.getById("A");

			Assert.fail("Exception expected");
		}
		catch (OverlengthException wex) {
			assertTrue(true);
			LOG.info(wex.getMessage());

			// MANDATORY reread
			lOA = locationService.getById("A");

			// Check the locations
			assertNotNull(lOA);
			assertFalse(lOA.getHandlingUnits().isEmpty());
			
			assertTrue(locationService.overlength(lOA, hU2.getLength()));
			assertTrue(locationService.overlength(lOA, hU3.getLength()));
			assertTrue(locationService.overlength(lOA, hU4.getLength()));
			assertTrue(locationService.overlength(lOA, hU5.getLength()));
		}
		catch (DimensionException capex) {
			Assert.fail("Unexpected exception: " +  capex.getMessage());
		}
	}
	
	/**
	 * Test overheight
	 */
	@Test
	@InSequence(42)
	public void checkOverheight() {
		LOG.info("--- Test checkOverheight");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());

		// Prepare a location
		Location lOA = locationService.createOrUpdate(new Location("A"));
		
		// Now set the height to limit
		lOA.getDimension().setMaxHeight(HeightCategory.HIGH);
		lOA = locationService.createOrUpdate(lOA);
		
		assertFalse(locationService.overheight(lOA, HeightCategory.NOT_RELEVANT));
		assertFalse(locationService.overheight(lOA, HeightCategory.HIGH));
		assertFalse(locationService.overheight(lOA, HeightCategory.MIDDLE));
		assertFalse(locationService.overheight(lOA, HeightCategory.LOW));
		assertTrue(locationService.overheight(lOA, HeightCategory.TOO_HIGH));
		assertTrue(locationService.overheight(lOA, HeightCategory.UNKNOWN));
		
		// Now change the limit
		lOA.getDimension().setMaxHeight(HeightCategory.LOW);
		lOA = locationService.createOrUpdate(lOA);
		
		assertFalse(locationService.overheight(lOA, HeightCategory.NOT_RELEVANT));
		assertTrue(locationService.overheight(lOA, HeightCategory.HIGH));
		assertTrue(locationService.overheight(lOA, HeightCategory.MIDDLE));
		assertFalse(locationService.overheight(lOA, HeightCategory.LOW));
		assertTrue(locationService.overheight(lOA, HeightCategory.TOO_HIGH));
		assertTrue(locationService.overheight(lOA, HeightCategory.UNKNOWN));
	}

	/**
	 * Test overlength
	 */
	@Test
	@InSequence(45)
	public void checkOverlength() {
		LOG.info("--- Test checkOverlength");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());

		// Prepare a location
		Location lOA = locationService.createOrUpdate(new Location("A"));
		
		// Now set the length to limit
		lOA.getDimension().setMaxLength(LengthCategory.LONG);
		lOA = locationService.createOrUpdate(lOA);
		
		assertFalse(locationService.overlength(lOA, LengthCategory.NOT_RELEVANT));
		assertFalse(locationService.overlength(lOA, LengthCategory.LONG));
		assertFalse(locationService.overlength(lOA, LengthCategory.MIDDLE));
		assertFalse(locationService.overlength(lOA, LengthCategory.SHORT));
		assertTrue(locationService.overlength(lOA, LengthCategory.TOO_LONG));
		assertTrue(locationService.overlength(lOA, LengthCategory.UNKNOWN));
		
		// Now change the limit
		lOA.getDimension().setMaxLength(LengthCategory.SHORT);
		lOA = locationService.createOrUpdate(lOA);
		
		assertFalse(locationService.overlength(lOA, LengthCategory.NOT_RELEVANT));
		assertTrue(locationService.overlength(lOA, LengthCategory.LONG));
		assertTrue(locationService.overlength(lOA, LengthCategory.MIDDLE));
		assertFalse(locationService.overlength(lOA, LengthCategory.SHORT));
		assertTrue(locationService.overlength(lOA, LengthCategory.TOO_LONG));
		assertTrue(locationService.overlength(lOA, LengthCategory.UNKNOWN));

		// Now change the limit
		lOA.getDimension().setMaxLength(LengthCategory.MIDDLE);
		lOA = locationService.createOrUpdate(lOA);
		
		assertFalse(locationService.overlength(lOA, LengthCategory.NOT_RELEVANT));
		assertTrue(locationService.overlength(lOA, LengthCategory.LONG));
		assertFalse(locationService.overlength(lOA, LengthCategory.MIDDLE));
		assertFalse(locationService.overlength(lOA, LengthCategory.SHORT));
		assertTrue(locationService.overlength(lOA, LengthCategory.TOO_LONG));
		assertTrue(locationService.overlength(lOA, LengthCategory.UNKNOWN));

		// Now change the capacity
		lOA.getDimension().setMaxCapacity(9);
		lOA = locationService.createOrUpdate(lOA);

		assertFalse(locationService.overlength(lOA, LengthCategory.NOT_RELEVANT));
		assertFalse(locationService.overlength(lOA, LengthCategory.LONG));
		assertFalse(locationService.overlength(lOA, LengthCategory.MIDDLE));
		assertFalse(locationService.overlength(lOA, LengthCategory.SHORT));
		assertFalse(locationService.overlength(lOA, LengthCategory.TOO_LONG));
		assertFalse(locationService.overlength(lOA, LengthCategory.UNKNOWN));
	}

	/**
	 * Test overwidth
	 */
	@Test
	@InSequence(48)
	public void checkOverwidth() {
		LOG.info("--- Test checkOverwidth");
		
		assertTrue(handlingUnitService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());

		// Prepare a location
		Location lOA = locationService.createOrUpdate(new Location("A"));
		
		// Now set the length to limit
		lOA.getDimension().setMaxWidth(WidthCategory.WIDE);
		lOA = locationService.createOrUpdate(lOA);
		
		assertFalse(locationService.overwidth(lOA, WidthCategory.NOT_RELEVANT));
		assertFalse(locationService.overwidth(lOA, WidthCategory.WIDE));
		assertFalse(locationService.overwidth(lOA, WidthCategory.MIDDLE));
		assertFalse(locationService.overwidth(lOA, WidthCategory.NARROW));
		assertTrue(locationService.overwidth(lOA, WidthCategory.TOO_WIDE));
		assertTrue(locationService.overwidth(lOA, WidthCategory.UNKNOWN));

		// Now change the limit
		lOA.getDimension().setMaxWidth(WidthCategory.MIDDLE);
		lOA = locationService.createOrUpdate(lOA);
		
		assertFalse(locationService.overwidth(lOA, WidthCategory.NOT_RELEVANT));
		assertTrue(locationService.overwidth(lOA, WidthCategory.WIDE));
		assertFalse(locationService.overwidth(lOA, WidthCategory.MIDDLE));
		assertFalse(locationService.overwidth(lOA, WidthCategory.NARROW));
		assertTrue(locationService.overwidth(lOA, WidthCategory.TOO_WIDE));
		assertTrue(locationService.overwidth(lOA, WidthCategory.UNKNOWN));

		// Now change the limit
		lOA.getDimension().setMaxWidth(WidthCategory.NARROW);
		lOA = locationService.createOrUpdate(lOA);
		
		assertFalse(locationService.overwidth(lOA, WidthCategory.NOT_RELEVANT));
		assertTrue(locationService.overwidth(lOA, WidthCategory.WIDE));
		assertTrue(locationService.overwidth(lOA, WidthCategory.MIDDLE));
		assertFalse(locationService.overwidth(lOA, WidthCategory.NARROW));
		assertTrue(locationService.overwidth(lOA, WidthCategory.TOO_WIDE));
		assertTrue(locationService.overwidth(lOA, WidthCategory.UNKNOWN));
	}
}
