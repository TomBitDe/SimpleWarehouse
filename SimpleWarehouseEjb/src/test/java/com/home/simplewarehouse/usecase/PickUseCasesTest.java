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
 * Test pick use cases.
 */
@RunWith(Arquillian.class)
public class PickUseCasesTest {
	private static final Logger LOG = LogManager.getLogger(PickUseCasesTest.class);

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

	@BeforeClass
	public static void beforeClass() {
		LOG.trace("--> beforeClass()");
		
		LOG.trace("<-- beforeClass()");		
	}
	
	@AfterClass
	public static void afterClass() {
		LOG.trace("--> afterClass()");

		LOG.trace("<-- afterClass()");
	}
	
	@Before
	public void beforeTest() {
		LOG.trace("--> beforeTest()");
		
		sampleWarehouseLocal.initialize();
				
		LOG.trace("<-- beforeTest()");		
	}
	
	@After
	public void afterTest() {
		LOG.trace("--> afterTest()");
		
		sampleWarehouseLocal.cleanup();

		LOG.trace("<-- afterTest()");
	}

	/**
	 * Expected is that the empty location is NOT set to ERROR
	 * TODO: what should happen with the unit?
	 */
	@Test
	@InSequence(1)
	public void pickFromEmptyLocation() {
		// Check preconditions
		assertFalse(unitLocal.getAll().isEmpty());
		assertFalse(locationLocal.getAll().isEmpty());
		
		HandlingUnit hu1 = unitLocal.getById("1");
		// Check hu1 exists
		assertNotNull(hu1);
		// Check hu1 is not placed elsewhere
		assertNull(hu1.getLocation());
		
		Location lA = locationLocal.getById("A");
		// Check lA exists
		assertNotNull(lA);
		// Check lA is empty
		assertTrue(lA.getHandlingUnits().isEmpty());
		
		try {
			unitLocal.pickFrom(lA, hu1);
		}
		catch (LocationIsEmptyException le) {
			// MANDATORY read again because of pickFrom
			hu1= unitLocal.getById(hu1.getId());
			lA = locationLocal.getById(lA.getLocationId());

			LOG.info("Expected:\n\t{}\n\tis not on\n\t{}", hu1, lA);
			
			// Check if location is in ERROR now
			assertNotEquals(ErrorStatus.ERROR, lA.getLocationStatus().getErrorStatus());
			LOG.info("Location is NOT in ERROR:\n\t{}", lA.getLocationStatus());
		}
		catch (HandlingUnitNotOnLocationException no) {
			Assert.fail("Not expected: " + no);
		}
	}

	/**
	 * Expected is that the filled location is SET to ERROR
	 * TODO: what should happen with the unit?
	 */
	@Test
	@InSequence(2)
	public void pickFromFilledLocationButUnitIsNotPlacedAnywhere() {
		// Check preconditions
		assertFalse(unitLocal.getAll().isEmpty());
		assertFalse(locationLocal.getAll().isEmpty());
		
		// Fill the lB with hu2
		Location lB = locationLocal.getById("B");
		assertNotNull(lB);
		
		HandlingUnit hu2 = unitLocal.getById("2");
		assertNotNull(hu2);
		
		unitLocal.dropTo(lB, hu2);
		
		// Check lB is filled with hu2 only
		// MANDATORY read again because of dropTo before
		lB = locationLocal.getById(lB.getLocationId());
		hu2 = unitLocal.getById(hu2.getId());
		assertEquals(1, locationLocal.getAllContaining(hu2).size());
		assertEquals(lB.getLocationId(), locationLocal.getAllContaining(hu2).get(0).getLocationId());
		
		// Take hu3
		HandlingUnit hu3 = unitLocal.getById("3");
		assertNotNull(hu3);
		assertNull(hu3.getLocation());
		
		// Pick hu3 from lB now
		try {
			// MANDATORE read again of lB and hu3 is done before :-)
			unitLocal.pickFrom(lB, hu3);
		}
		catch (HandlingUnitNotOnLocationException no) {
			// MANDATORY read again because of pickFrom
			lB = locationLocal.getById(lB.getLocationId());
			hu3= unitLocal.getById(hu3.getId());
			
			LOG.info("Expected:\n\t{}\n\tis not on\n\t{}", hu3, lB);
			
			// Check if location is in ERROR now
			assertEquals(ErrorStatus.ERROR, lB.getLocationStatus().getErrorStatus());
			LOG.info("Location is in ERROR:\n\t{}", lB.getLocationStatus());
		}
		catch (LocationIsEmptyException le) {
			Assert.fail("Not expected: " + le);			
		}
	}
}
