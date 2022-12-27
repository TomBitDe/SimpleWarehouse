package com.home.simplewarehouse.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
import com.home.simplewarehouse.handlingunit.HandlingUnitLocal;
import com.home.simplewarehouse.handlingunit.HandlingUnitNotOnLocationException;
import com.home.simplewarehouse.handlingunit.LocationIsEmptyException;
import com.home.simplewarehouse.location.DimensionBean;
import com.home.simplewarehouse.location.DimensionException;
import com.home.simplewarehouse.location.DimensionLocal;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusLocal;
import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.topology.SampleWarehouseBean;
import com.home.simplewarehouse.topology.SampleWarehouseLocal;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test pick use cases for a RANDOM access location.
 */
@RunWith(Arquillian.class)
public class RandomAccessPickUseCasesTest {
	private static final Logger LOG = LogManager.getLogger(RandomAccessPickUseCasesTest.class);

	@EJB
	private SampleWarehouseLocal sampleWarehouseLocal;
	
	@EJB
	private LocationStatusLocal locationStatusLocal;
	
	@EJB
	private DimensionLocal dimensionLocal;
	
	@EJB
	private HandlingUnitLocal unitLocal;
	
	@EJB
	private LocationLocal locationLocal;
	
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
						SampleWarehouseLocal.class, SampleWarehouseBean.class,
						DimensionLocal.class, DimensionBean.class,
						LocationStatusLocal.class, LocationStatusBean.class,
						LocationLocal.class, LocationBean.class,
						HandlingUnitLocal.class, HandlingUnitBean.class,
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
	public RandomAccessPickUseCasesTest() {
		// DO NOTHING HERE!
	}

	/**
	 * What to do before ALL tests will be executed (once before all tests)
	 */
	@BeforeClass
	public static void beforeClass() {
		LOG.trace("--> beforeClass()");
		
		LOG.trace("<-- beforeClass()");		
	}
	
	/**
	 * What to do after ALL tests have been done (once after all tests)
	 */
	@AfterClass
	public static void afterClass() {
		LOG.trace("--> afterClass()");

		LOG.trace("<-- afterClass()");
	}
	
	/**
	 * What to do before an individual test will be executed (each test)<br>
	 * <br>
	 * Initialize with the SampleWarehouse data
	 */
	@Before
	public void beforeTest() {
		LOG.trace("--> beforeTest()");
		
		sampleWarehouseLocal.initialize();
				
		// Have locations and units ?
		assertFalse(unitLocal.getAll().isEmpty());
		assertFalse(locationLocal.getAll().isEmpty());

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
		
		sampleWarehouseLocal.cleanup();

		LOG.trace("<-- afterTest()");
	}
	
	private HandlingUnit prepareUnitAndCheck(final String unitId) {
		LOG.trace("--> prepareUnitAndCheck()");

		HandlingUnit hu = unitLocal.getById(unitId);
		// Check hu exists
		assertNotNull(hu);
		// Check hu is not placed elsewhere
		assertNull(hu.getLocation());
		
		LOG.trace("<-- prepareUnitAndCheck()");

		return hu;
	}

	private Location prepareLocationAndCheck(final String locationId) {
		LOG.trace("--> prepareLocationAndCheck()");

		Location loc = locationLocal.getById(locationId);
		// Check loc exists
		assertNotNull(loc);
		// Check loc is empty
		assertTrue(loc.getHandlingUnits().isEmpty());
		
		LOG.trace("<-- prepareLocationAndCheck()");

		return loc;
	}
	
	private Location reRead(final Location loc) {
		LOG.trace("--> reRead()");

		if (loc == null) {
			LOG.warn("loc == null");
			return loc;
		}
		
		LOG.trace("<-- reRead({})", loc);
		
		return locationLocal.getById(loc.getLocationId());
	}
	
	private HandlingUnit reRead(final HandlingUnit hu) {
		LOG.trace("--> reRead()");

		if (hu == null) {
			LOG.warn("hu == null");
			return hu;
		}

		LOG.trace("<-- reRead({})", hu);

		return unitLocal.getById(hu.getId());
	}

	/**
	 * Not an exceptional case<br>
	 * <br>
     * <pre>{@code
     * 
     *      +----------+                       +----------+
     *      | lA       |                       | lA       |
     *      |          |                       |          |
     *      |  hu1     |   pickFrom(lA, hu1)   |  EMPTY   |
     *      |          |                       |          |
     *      |          |                       |          |
     *      +----------+                       +----------+
     *                                         
     *                                          hu1
     *                      
     * }</pre>
	 * <br>
	 * All preconditions are fulfilled:<br>
	 * - location is filled with related handlingUnit<br>
	 * - location is in "normal" state<br>
	 * <br>
	 * Expected is that after pickFrom:<br>
	 * - no exception is raised<br>
	 * - the handlingUnit is not connected to the location<br>
	 * - the location no longer contains the handlingUnit<br> 
	 * - the location is not in ERROR<br>
	 */
	@Test
	@InSequence(1)
	public void pickFromLocation() {
		HandlingUnit hu1 = prepareUnitAndCheck("1");
		
		Location lA = prepareLocationAndCheck("A");
		
		try {
			// Drop hu1 on lA to prepare for the test case
			unitLocal.dropTo(lA, hu1);

			// MANDATORY read again because of dropTo
			lA = reRead(lA);
			hu1 = reRead(hu1);

			// Test now
			unitLocal.pickFrom(lA, hu1);
		}
		catch (LocationIsEmptyException | DimensionException| HandlingUnitNotOnLocationException ex) {
			Assert.fail("Not expected: " + ex);
		}

		// MANDATORY read again because of pickFrom
		lA = reRead(lA);
		hu1 = reRead(hu1);
		
		// Check lA no longer contains hu1
		assertFalse(lA.getHandlingUnits().contains(hu1));
		
		// Check hu1 is not linked to lA
		assertNull(hu1.getLocation());

		// Check if location is in ERROR
		assertNotEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());

		LOG.info("Expected:\n\t{}\n\tis not on\n\t{}", hu1, lA);
		LOG.info("Location is NOT in ERROR as expected:\n\t{}", lA.getLocationStatus());
	}
	
	/**
	 * Exceptional case<br>
	 * Location is EMPTY<br>
	 * <br>
     * <pre>{@code
     * 
     *      +----------+                       +----------+
     *      | lA       |                       | lA       |
     *      |          |                       |          |
     *      |  EMPTY   |   pickFrom(lA, hu1)   |  EMPTY   |
     *      |          |                       |          |
     *      |          |   LocationIsEmpty     |          |
     *      +----------+      Exception        +----------+
     *                                         
     *                                          hu1
     *                      
     * }</pre>
	 * <br>
	 * Preconditions not fulfilled:<br>
	 * - location is NOT filled with related handlingUnit<br>
	 * <br>
	 * Expected is that after pickFrom:<br>
	 * - a LocationIsEmptyException is raised<br>
	 * - the handlingUnit is not connected to the location<br>
	 * - the location does not contain the handlingUnit<br> 
	 * - the location is NOT in ERROR because is was EMPTY before and is ready for further actions<br>
     * <br>
	 * TODO: what should happen with the unit?<br>
	 */
	@Test
	@InSequence(10)
	public void pickFromEmptyLocation() {
		HandlingUnit hu1 = prepareUnitAndCheck("1");
		
		Location lA = prepareLocationAndCheck("A");
		
		try {
			unitLocal.pickFrom(lA, hu1);
		}
		catch (LocationIsEmptyException le) {
			// MANDATORY read again because of pickFrom
			lA = reRead(lA);
			hu1 = reRead(hu1);

			// Check lA does not contain hu1
			assertFalse(lA.getHandlingUnits().contains(hu1));
			
			// Check hu1 is not linked to lA
			assertNull(hu1.getLocation());

			// Check if location is not in ERROR
			assertNotEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());
			
			LOG.info("Expected:\n\t{}\n\tis not on\n\t{}", hu1, lA);
			LOG.info("Location is NOT in ERROR:\n\t{}", lA.getLocationStatus());
		}
		catch (HandlingUnitNotOnLocationException no) {
			Assert.fail("Not expected: " + no);
		}
	}

	/**
	 * Exceptional case<br>
	 * Location is filled with other handlingUnit but not the requested one<br>
	 * <br>
     * <pre>{@code
     * 
     *      +----------+                                +----------+
     *      | lB       |                                | lB       |
     *      |          |                                |          |
     *      |  hu2     |       pickFrom(lB, hu3)        |  hu2     |
     *      |          |                                |          |
     *      |          |   HandlingUnitNotOnLocation    | ERROR    |
     *      +----------+          Exception             +----------+
     *                                         
     *                                                   hu3
     *                      
     * }</pre>
	 * <br>
	 * Preconditions not fulfilled:<br>
	 * - location is NOT filled with related handlingUnit<br>
	 * <br>
	 * Expected is that after pickFrom:<br>
	 * - a HandlingUnitNotOnLocationException is raised<br>
	 * - the handlingUnit is not connected to the location<br>
	 * - the location does not contain the handlingUnit<br> 
	 * - the location is SET in ERROR because is was NOT EMPTY before and needs check<br>
	 * - the location is still filled with the other handlingUnit<br>
     * <br>
	 * TODO: what should happen with the unit?<br>
	 */
	@Test
	@InSequence(12)
	public void pickFromFilledLocationButUnitIsNotPlacedAnywhere() {
		// Fill the lB with hu2
		Location lB = prepareLocationAndCheck("B");
		
		HandlingUnit hu2 = prepareUnitAndCheck("2");
		HandlingUnit hu3 = prepareUnitAndCheck("3");
		
		try {
			unitLocal.dropTo(lB, hu2);

			// Check lB is filled with hu2 only
			// MANDATORY read again because of dropTo before
			lB = reRead(lB);
			hu2 = reRead(hu2);
			assertEquals(1, locationLocal.getAllContaining(hu2).size());
			assertEquals(lB.getLocationId(), locationLocal.getAllContaining(hu2).get(0).getLocationId());

			// Pick hu3 from lB now
			// MANDATORE read again of lB and hu3 is done before :-)
			unitLocal.pickFrom(lB, hu3);
		}
		catch (HandlingUnitNotOnLocationException no) {
			// MANDATORY read again because of pickFrom
			lB = reRead(lB);
			hu2 = reRead(hu2);
			hu3 = reRead(hu3);
			
			// Check lB does not contain hu3
			assertFalse(lB.getHandlingUnits().contains(hu3));
			
			// Check hu3 is not linked to lB
			assertNull(hu3.getLocation());

			// Check if location is in ERROR now
			assertEquals(ErrorStatus.ERROR, lB.getLocationStatus().getErrorStatus());

			// Check lB still contains hu2
			assertTrue(lB.getHandlingUnits().contains(hu2));
			// Check hu2 is still linked to lB
			assertEquals(lB, hu2.getLocation());
			
			LOG.info("Expected:\n\t{}\n\tis not on\n\t{}", hu3, lB);
			LOG.info("Location is in ERROR:\n\t{}", lB.getLocationStatus());
		}
		catch (LocationIsEmptyException | DimensionException lec) {
			Assert.fail("Not expected: " + lec);			
		}
	}

	/**
	 * Exceptional case<br>
	 * Requested location is EMPTY. HandlingUnit is placed on other location but not the requested.<br>
	 * <br>
     * <pre>{@code
     * 
     *      +----------+                                +----------+
     *      | lA       |                                | lA       |
     *      |          |                                |          |
     *      |  hu1     |                                |  hu1     |
     *      |          |                                |          |
     *      |          |                                | ERROR    |
     *      +----------+                                +----------+
     *                                                   
     *      +----------+                                +----------+
     *      | lB       |                                | lB       |
     *      |          |                                |          |
     *      |  EMPTY   |       pickFrom(lB, hu1)        |  EMPTY   |
     *      |          |                                |          |
     *      |          |       LocationIsEmpty          |          |
     *      +----------+          Exception             +----------+
     *                      
     * }</pre>
	 * <br>
	 * Preconditions not fulfilled:<br>
	 * - location is NOT filled with the handlingUnit<br>
	 * <br>
	 * Expected is that after pickFrom:<br>
	 * - a HandlingUnitNotOnLocationException is raised<br>
	 * - the handlingUnit is not connected to the location<br>
	 * - the handlingUnit is still connected to the OTHER location<br>
	 * - the location does not contain the handlingUnit<br> 
	 * - the location is not in ERROR because is was EMPTY before and needs no check<br>
	 * - the OTHER location is still filled with the handlingUnit<br>
	 * - the OTHER location is SET in ERROR because is was NOT EMPTY before and needs check<br>
     * <br>
	 */
	@Test
	@InSequence(15)
	public void pickFromEmptyLocationButUnitPlacedSomewhereElse() {
		HandlingUnit hu1 = prepareUnitAndCheck("1");
		
		Location lA = prepareLocationAndCheck("A");
		Location lB = prepareLocationAndCheck("B");
		
		try {
			unitLocal.dropTo(lA, hu1);

			hu1 = reRead(hu1);
			lA = reRead(lA);

			// Test case now
			unitLocal.pickFrom(lB, hu1);		
		}
		catch (LocationIsEmptyException le) {
			// MANDATORY read again because of pickFrom
			hu1 = reRead(hu1);
			lB = reRead(lB);
			lA = reRead(lA);
			
			// Check lB does not contain hu1
			assertFalse(lB.getHandlingUnits().contains(hu1));
			
			// Check hu1 is still linked to lA
			assertEquals(lA, hu1.getLocation());

			// Check if lA is in ERROR now
			assertEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());

			// Check lB is still EMPTY
			assertTrue(lB.getHandlingUnits().isEmpty());
			
			// Check if lB is NOT in ERROR now
			assertEquals(ErrorStatus.NONE, lB.getLocationStatus().getErrorStatus());

			LOG.info("Expected:\n\t{}\n\tis not on\n\t{}", hu1, lA);
			LOG.info("Location is in ERROR:\n\t{}", lA.getLocationStatus());
			LOG.info("Location is in NONE:\n\t{}", lB.getLocationStatus());
		}
		catch (HandlingUnitNotOnLocationException | DimensionException no) {
			Assert.fail("Not expected: " + no);			
		}		
	}
	
	/**
	 * Exceptional case<br>
	 * Requested location is FILLED with other HandlingUnit. Requested HandlingUnit is placed
	 * on other location.<br>
	 * <br>
     * <pre>{@code
     * 
     *      +----------+                                +----------+
     *      | lB       |                                | lB       |
     *      |          |                                |          |
     *      |  hu1     |                                |  hu1     |
     *      |          |                                |          |
     *      |          |                                | ERROR    |
     *      +----------+                                +----------+
     *                                                   
     *      +----------+                                +----------+
     *      | lA       |                                | lA       |
     *      |          |                                |          |
     *      |  hu2     |       pickFrom(lA, hu1)        |  hu2     |
     *      |          |                                |          |
     *      |          |   HandlingUnitNotOnLocation    | ERROR    |
     *      +----------+          Exception             +----------+
     *                      
     * }</pre>
	 * <br>
	 * Preconditions not fulfilled:<br>
	 * - location is NOT filled with the handlingUnit<br>
	 * <br>
	 * Expected is that after pickFrom:<br>
	 * - a HandlingUnitNotOnLocationException is raised<br>
	 * - the handlingUnit is not connected to the location<br>
	 * - the handlingUnit is still connected to the OTHER location<br>
	 * - the location does not contain the handlingUnit<br> 
	 * - the location is SET in ERROR because is was NOT EMPTY before and needs check<br>
	 * - the OTHER location is still filled with the handlingUnit<br>
	 * - the OTHER location is SET in ERROR because is was NOT EMPTY before and needs check<br>
     * <br>
	 */
	@Test
	@InSequence(17)
	public void pickFromFilledLocationButUnitPlacedSomewhereElse() {
		HandlingUnit hu1 = prepareUnitAndCheck("1");
		HandlingUnit hu2 = prepareUnitAndCheck("2");
		
		Location lA = prepareLocationAndCheck("A");
		Location lB = prepareLocationAndCheck("B");
		
		try {
			unitLocal.dropTo(lB, hu1);

			hu1 = reRead(hu1);
			lB = reRead(lB);

			unitLocal.dropTo(lA, hu2);

			hu2 = reRead(hu2);
			lA = reRead(lA);

			// Test case now
			unitLocal.pickFrom(lA, hu1);
		}
		catch (HandlingUnitNotOnLocationException no) {
			// MANDATORY read again because of pickFrom
			hu1 = reRead(hu1);
			hu2 = reRead(hu2);
			lB = reRead(lB);
			lA = reRead(lA);
			
			// Check lB still contains hu1
			assertEquals(lB, hu1.getLocation());
			assertTrue(lB.getHandlingUnits().contains(hu1));

			// Check if lB is in ERROR now
			assertEquals(ErrorStatus.ERROR, lB.getLocationStatus().getErrorStatus());

			// Check lA still contains hu2
			assertTrue(lA.getHandlingUnits().contains(hu2));
			
			// Check if lA is in ERROR now
			assertEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());

			 // Check lA does NOT contain hu1
			assertFalse(lA.getHandlingUnits().contains(hu1));
			
			 // Check lB does NOT contain hu2
			assertFalse(lB.getHandlingUnits().contains(hu2));
			
			LOG.info("Expected:\n\t{}\n\tis NOT on\n\t{}", hu1, lA);
			LOG.info("Location is in ERROR:\n\t{}", lA.getLocationStatus());
			LOG.info("Location is in ERROR:\n\t{}", lB.getLocationStatus());
			LOG.info("Expected:\n\t{}\n\tis on\n\t{}", hu1, lA);
		}
		catch (LocationIsEmptyException | DimensionException le) {
			Assert.fail("Not expected: " + le);			
		}		
	}
}
