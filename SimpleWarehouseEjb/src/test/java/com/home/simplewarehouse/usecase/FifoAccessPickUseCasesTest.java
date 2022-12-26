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
import com.home.simplewarehouse.handlingunit.LocationIsEmptyException;
import com.home.simplewarehouse.location.DimensionBean;
import com.home.simplewarehouse.location.DimensionLocal;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.CapacityExceededException;
import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusLocal;
import com.home.simplewarehouse.location.WeightExceededException;
import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.topology.SampleWarehouseBean;
import com.home.simplewarehouse.topology.SampleWarehouseLocal;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test pick use cases for a FIFO access location.
 */
@RunWith(Arquillian.class)
public class FifoAccessPickUseCasesTest {
	private static final Logger LOG = LogManager.getLogger(FifoAccessPickUseCasesTest.class);

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
	public FifoAccessPickUseCasesTest() {
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
		LOG.trace("--> reRead()", hu);

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
     *      |  hu4     |                |          |
     *      |  hu3     |                |  hu4     |
     *      |  hu2     |                |  hu3     |
     *      |  hu1     |  pickFrom(lA)  |  hu2     |
     *      +----------+                +----------+
     *                                         
     *                                     hu1
     *                      
     * }</pre>
	 * <br>
	 * All preconditions are fulfilled:<br>
	 * - location is filled with related handlingUnit<br>
	 * - location is in "normal" state<br>
	 * <br>
	 * Expected is that after pickFrom:<br>
	 * - no exception is raised<br>
	 * - first in first out<br>
	 * - the handlingUnit is not connected to the location<br>
	 * - the location no longer contains the handlingUnit<br> 
	 * - the location is not in ERROR<br>
	 */
	@Test
	@InSequence(1)
	public void pickFromFifo() {
		HandlingUnit hu1 = prepareUnitAndCheck("1");
		HandlingUnit hu2 = prepareUnitAndCheck("2");
		HandlingUnit hu3 = prepareUnitAndCheck("3");
		HandlingUnit hu4 = prepareUnitAndCheck("4");
		
		Location lA = prepareLocationAndCheck("FIFO_A");
		
		try {
			// Drop on lA to prepare for the test case
			unitLocal.dropTo(lA, hu1); // First in
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu2);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu3);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu4);
			lA = reRead(lA);

			HandlingUnit picked = null;

			// Test now
			picked = unitLocal.pickFrom(lA);

			// MANDATORY read again because of pickFrom
			lA = reRead(lA);
			
			// After pick first in 1 first out must be 1
			assertEquals("1", picked.getId());
			
			// Check picked
			hu1 = reRead(hu1);
			assertEquals(hu1, picked);
			
			// Check lA no longer contains picked
			assertFalse(lA.getHandlingUnits().contains(picked));
						
			// Check picked is not linked to lA and has locaPos null
			assertNull(picked.getLocation());
			assertNull(picked.getLocaPos());

			// Check if location is in ERROR
			assertNotEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());

			LOG.info("Expected:\n\t{}\n\tis not on\n\t{}", picked, lA);
			LOG.info("Location is NOT in ERROR as expected:\n\t{}", lA.getLocationStatus());
		}
		catch (LocationIsEmptyException | CapacityExceededException | WeightExceededException ex) {
			Assert.fail("Not expected: " + ex);
		}
	}
	
	/**
	 * Not an exceptional case<br>
	 * <br>
     * <pre>{@code
     * 
     *      +----------+                +----------+
     *      | lA       |                | lA       |
     *      |          |                |          |
     *      |  hu4     |                |          |
     *      |  hu3     |                |  hu4     |
     *      |  hu2     |                |  hu3     |
     *      |  hu1     |  pickFrom(lA)  |  hu2     |
     *      +----------+                +----------+
     *                                         
     *                                     hu1
     *                      
     * }</pre>
	 * <br>
	 * All preconditions are fulfilled:<br>
	 * - location is filled with related handlingUnit<br>
	 * - location is in "normal" state<br>
	 * <br>
	 * Expected is that after pickFrom:<br>
	 * - no exception is raised<br>
	 * - first in first out<br>
	 * - the handlingUnit is not connected to the location<br>
	 * - the location no longer contains the handlingUnit<br> 
	 * - the location is not in ERROR<br>
	 */
	@Test
	@InSequence(3)
	public void manyPicksFromFifo() {
		HandlingUnit hu1 = prepareUnitAndCheck("1");
		HandlingUnit hu2 = prepareUnitAndCheck("2");
		HandlingUnit hu3 = prepareUnitAndCheck("3");
		HandlingUnit hu4 = prepareUnitAndCheck("4");
		HandlingUnit hu5 = prepareUnitAndCheck("5");
		HandlingUnit hu6 = prepareUnitAndCheck("6");
		
		Location lA = prepareLocationAndCheck("FIFO_A");
		
		try {
			// Drop on lA to prepare for the test case
			unitLocal.dropTo(lA, hu1); // First in
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu2);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu3);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu4);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu5);
			lA = reRead(lA);
			unitLocal.dropTo(lA, hu6);
			lA = reRead(lA);

			HandlingUnit picked = null;

			// Test now
			picked = unitLocal.pickFrom(lA);

			// MANDATORY read again because of pickFrom
			lA = reRead(lA);

			// After pick first in 1 first out must be 1
			assertEquals("1", picked.getId());

			// Check lA no longer contains picked
			assertFalse(lA.getHandlingUnits().contains(picked));

			// Check picked is not linked to lA and has locaPos null
			assertNull(picked.getLocation());
			assertNull(picked.getLocaPos());

			// Check if location is in ERROR
			assertNotEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());

			picked = unitLocal.pickFrom(lA);
			lA = reRead(lA);
			assertEquals("2", picked.getId());
			
			picked = unitLocal.pickFrom(lA);
			lA = reRead(lA);
			assertEquals("3", picked.getId());
			
			picked = unitLocal.pickFrom(lA);
			lA = reRead(lA);
			assertEquals("4", picked.getId());
			
			picked = unitLocal.pickFrom(lA);
			lA = reRead(lA);
			assertEquals("5", picked.getId());
			
			// Check if location is in ERROR
			assertNotEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());
			LOG.info("Location is NOT in ERROR as expected:\n\t{}", lA.getLocationStatus());
		}
		catch (LocationIsEmptyException | CapacityExceededException | WeightExceededException ex) {
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
	 * - FIFO is NOT filled with any handlingUnit<br>
	 * <br>
	 * Expected is that after pickFrom:<br>
	 * - a LocationIsEmptyException is raised<br>
	 * - the location does not contain any handlingUnit<br> 
	 * - the location is NOT in ERROR because is was EMPTY before and is ready for further actions<br>
     * <br>
	 */
	@Test
	@InSequence(10)
	public void pickFromEmptyFifo() {
		Location lA = prepareLocationAndCheck("FIFO_A");
		
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

			// Check if location is not in ERROR
			assertNotEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());
			
			LOG.info("Expected:\n\t{}\n\tis not on\n\t{}", picked, lA);
			LOG.info("Location is NOT in ERROR:\n\t{}", lA.getLocationStatus());
		}
	}
}
