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
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.handlingunit.LocationIsEmptyException;
import com.home.simplewarehouse.location.DimensionBean;
import com.home.simplewarehouse.location.DimensionException;
import com.home.simplewarehouse.location.DimensionService;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusService;
import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.topology.SampleWarehouseBean;
import com.home.simplewarehouse.topology.SampleWarehouseService;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test pick use cases for a LIFO access locationService.
 */
@RunWith(Arquillian.class)
public class LifoAccessPickUseCasesTest {
	private static final Logger LOG = LogManager.getLogger(LifoAccessPickUseCasesTest.class);

	@EJB
	private SampleWarehouseService sampleWarehouseService;
	
	@EJB
	private LocationStatusService locationStatusService;
	
	@EJB
	private DimensionService dimensionService;
	
	@EJB
	private HandlingUnitService unitLocal;
	
	@EJB
	private LocationService locationService;
	
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
						SampleWarehouseService.class, SampleWarehouseBean.class,
						DimensionService.class, DimensionBean.class,
						LocationStatusService.class, LocationStatusBean.class,
						LocationService.class, LocationBean.class,
						HandlingUnitService.class, HandlingUnitBean.class,
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
	public LifoAccessPickUseCasesTest() {
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
		
		sampleWarehouseService.initialize();
				
		// Have locations and units ?
		assertFalse(unitLocal.getAll().isEmpty());
		assertFalse(locationService.getAll().isEmpty());

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
		
		sampleWarehouseService.cleanup();

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

		Location loc = locationService.getById(locationId);
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
		
		return locationService.getById(loc.getLocationId());
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
     *      +----------+                +----------+
     *      | lA       |                | lA       |
     *      |          |                |          |
     *      |  hu1     |                |          |
     *      |  hu2     |                |  hu1     |
     *      |  hu3     |                |  hu2     |
     *      |  hu4     |  pickFrom(lA)  |  hu3     |
     *      +----------+                +----------+
     *                                         
     *                                     hu4
     *                      
     * }</pre>
	 * <br>
	 * All preconditions are fulfilled:<br>
	 * - locationService is filled with related handlingUnitService<br>
	 * - locationService is in "normal" state<br>
	 * <br>
	 * Expected is that after pickFrom:<br>
	 * - no exception is raised<br>
	 * - last in first out<br>
	 * - the handlingUnitService is not connected to the locationService<br>
	 * - the locationService no longer contains the handlingUnitService<br> 
	 * - the locationService is not in ERROR<br>
	 */
	@Test
	@InSequence(1)
	public void pickFromLifo() {
		HandlingUnit hu1 = prepareUnitAndCheck("1");
		HandlingUnit hu2 = prepareUnitAndCheck("2");
		HandlingUnit hu3 = prepareUnitAndCheck("3");
		HandlingUnit hu4 = prepareUnitAndCheck("4");
		
		Location lA = prepareLocationAndCheck("LIFO_A");
		
		try {
			// Drop on lA to prepare for the test case
			unitLocal.dropTo(lA, hu1);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu2);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu3);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu4); // Last in
			lA = reRead(lA);

			HandlingUnit picked = null;

			// Test now
			picked = unitLocal.pickFrom(lA);

			// MANDATORY read again because of pickFrom
			lA = reRead(lA);

			// After pick last in 4 first out must be 4
			assertEquals("4", picked.getId());
			
			// Check picked
			hu4 = reRead(hu4);
			assertEquals(hu4, picked);
			
			// Check lA no longer contains picked
			assertFalse(lA.getHandlingUnits().contains(picked));
						
			// Check picked is not linked to lA and has locaPos null
			assertNull(picked.getLocation());
			assertNull(picked.getLocaPos());

			// Check if locationService is in ERROR
			assertNotEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());

			LOG.info("Expected:\n\t{}\n\tis not on\n\t{}", picked, lA);
			LOG.info("Location is NOT in ERROR as expected:\n\t{}", lA.getLocationStatus());
		}
		catch (LocationIsEmptyException | DimensionException ex) {
			Assert.fail("Not expected: " + ex);
		}
	}
	
	/**
	 * Not an exceptional case<br>
	 * <br>
     * <pre>{@code
     * 
     *      +----------+                +----------+                +----------+
     *      | lA       |                | lA       |                | lA       |
     *      |          |                |          |                |          |
     *      |  hu1     |                |          |                |          |
     *      |  hu2     |                |  hu1     |                |          |
     *      |  hu3     |                |  hu2     |                |  hu1     |
     *      |  hu4     |  pickFrom(lA)  |  hu3     |  pickFrom(lA)  |  hu2     |
     *      +----------+                +----------+                +----------+  ...
     *                                                                     
     *                                     hu4                         hu3
     *                      
     * }</pre>
	 * <br>
	 * All preconditions are fulfilled:<br>
	 * - locationService is filled with related handlingUnitService<br>
	 * - locationService is in "normal" state<br>
	 * <br>
	 * Expected is that after pickFrom:<br>
	 * - no exception is raised<br>
	 * - last in first out<br>
	 * - the handlingUnitService is not connected to the locationService<br>
	 * - the locationService no longer contains the handlingUnitService<br> 
	 * - the locationService is not in ERROR<br>
	 */
	@Test
	@InSequence(3)
	public void manyPicksFromLifo() {
		HandlingUnit hu1 = prepareUnitAndCheck("1");
		HandlingUnit hu2 = prepareUnitAndCheck("2");
		HandlingUnit hu3 = prepareUnitAndCheck("3");
		HandlingUnit hu4 = prepareUnitAndCheck("4");
		HandlingUnit hu5 = prepareUnitAndCheck("5");
		HandlingUnit hu6 = prepareUnitAndCheck("6");
		
		Location lA = prepareLocationAndCheck("LIFO_A");
		
		try {
			// Drop on lA to prepare for the test case
			unitLocal.dropTo(lA, hu1);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu2);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu3);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu4);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu5);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu6); // Last in
			lA = reRead(lA);

			HandlingUnit picked = null;

			// Test now
			picked = unitLocal.pickFrom(lA);

			// MANDATORY read again because of pickFrom
			lA = reRead(lA);

			// After pick last in 6 first out must be 6
			assertEquals("6", picked.getId());

			// Check lA no longer contains picked
			assertFalse(lA.getHandlingUnits().contains(picked));
						
			// Check picked is not linked to lA and has locaPos null
			assertNull(picked.getLocation());
			assertNull(picked.getLocaPos());

			// Check if locationService is in ERROR
			assertNotEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());

			picked = unitLocal.pickFrom(lA);
			lA = reRead(lA);
			assertEquals("5", picked.getId());
			
			picked = unitLocal.pickFrom(lA);
			lA = reRead(lA);
			assertEquals("4", picked.getId());
			
			picked = unitLocal.pickFrom(lA);
			lA = reRead(lA);
			assertEquals("3", picked.getId());
			
			picked = unitLocal.pickFrom(lA);
			lA = reRead(lA);
			assertEquals("2", picked.getId());
			
			// Check if locationService is in ERROR
			assertNotEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());
			LOG.info("Location is NOT in ERROR as expected:\n\t{}", lA.getLocationStatus());
		}
		catch (LocationIsEmptyException | DimensionException ex) {
			Assert.fail("Not expected: " + ex);
		}
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
     *      |  EMPTY   |   pickFrom(lA)        |  EMPTY   |
     *      |          |                       |          |
     *      |          |   LocationIsEmpty     |          |
     *      +----------+      Exception        +----------+
     *                                         
     *                                         
     *                      
     * }</pre>
	 * <br>
	 * Preconditions not fulfilled:<br>
	 * - LIFO is NOT filled with any handlingUnitService<br>
	 * <br>
	 * Expected is that after pickFrom:<br>
	 * - a LocationIsEmptyException is raised<br>
	 * - the locationService does not contain any handlingUnitService<br> 
	 * - the locationService is NOT in ERROR because is was EMPTY before and is ready for further actions<br>
     * <br>
	 */
	@Test
	@InSequence(10)
	public void pickFromEmptyLifo() {
		Location lA = prepareLocationAndCheck("LIFO_A");
		
		HandlingUnit picked = null;
		
		try {
			picked = unitLocal.pickFrom(lA);
		}
		catch (LocationIsEmptyException le) {
			// MANDATORY read again because of pickFrom
			lA = reRead(lA);

			// Check lA is EMPTY
			assertTrue(lA.getHandlingUnits().isEmpty());
			
			// Check picked is null
			assertNull(picked);

			// Check if locationService is not in ERROR
			assertNotEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());
			
			LOG.info("Expected:\n\t{}\n\tis not on\n\t{}", picked, lA);
			LOG.info("Location is NOT in ERROR:\n\t{}", lA.getLocationStatus());
		}
	}
}
