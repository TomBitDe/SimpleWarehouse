package com.home.simplewarehouse.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
import com.home.simplewarehouse.handlingunit.HandlingUnitLocal;
import com.home.simplewarehouse.location.DimensionBean;
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
 * Test drop use cases.
 */
@RunWith(Arquillian.class)
public class DropUseCasesTest {
	private static final Logger LOG = LogManager.getLogger(DropUseCasesTest.class);

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
	 * Mandatory default constructor<br>
	 */
	public DropUseCasesTest() {
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
		HandlingUnit hu = unitLocal.getById(unitId);
		// Check hu1 exists
		assertNotNull(hu);
		// Check hu1 is not placed elsewhere
		assertNull(hu.getLocation());
		
		return hu;
	}

	private Location prepareLocationAndCheck(final String locationId) {
		Location loc = locationLocal.getById(locationId);
		// Check lA exists
		assertNotNull(loc);
		// Check lA is empty
		assertTrue(loc.getHandlingUnits().isEmpty());
		
		return loc;
	}
	
	private Location reRead(final Location loc) {
		return locationLocal.getById(loc.getLocationId());
	}
	
	private HandlingUnit reRead(final HandlingUnit hu) {
		return unitLocal.getById(hu.getId());
	}

	/**
	 * Not an exceptional case<br>
	 * <br>
     * <pre>{@code
     * 
     *      +----------+                       +----------+
     *      | lA       |   hu1                 | lA       |
     *      |          |                       |          |
     *      |  EMPTY   |   dropTo(lA, hu1)     |  hu1     |
     *      |          |                       |          |
     *      |          |                       |          |
     *      +----------+                       +----------+
     *                      
     * }</pre>
	 * <br>
	 * All preconditions are fulfilled:<br>
	 * - location is EMPTY<br>
	 * - location is in "normal" state<br>
	 * <br>
	 * Expected is that after dropTo:<br>
	 * - no exception is raised<br>
	 * - the location is no longer EMPTY<br> 
	 * - the location contains the handlingUnit<br>
	 * - the location is still in "normal" state<br>
	 * - the handlingUnit is connected to the location<br>
	 */
	@Test
	@InSequence(1)
	public void dropToLocation() {
		HandlingUnit hu1 = prepareUnitAndCheck("1");
		
		Location lA = prepareLocationAndCheck("A");
		
		// Drop hu1 on lA
		unitLocal.dropTo(lA, hu1);
		
		// MANDATORY read because of dropTo
		lA = reRead(lA);
		hu1 = reRead(hu1);
		
		// Check location is no longer EMPTY
		assertFalse(lA.getHandlingUnits().isEmpty());
		
		// Check lA contains hu1
		assertTrue(lA.getHandlingUnits().contains(hu1));
		
		// Check if location is in status NONE
		assertEquals(ErrorStatus.NONE, lA.getLocationStatus().getErrorStatus());
		
		// Check hu1 is linked to lA
		assertEquals(lA, hu1.getLocation());

		LOG.info("Expected:\n\t{}\n\tis on\n\t{}", hu1, lA);
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
	 * TODO: what should happen with the unit?<br>
	 */
	
}
