package com.home.simplewarehouse.zone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import java.io.File;
import java.util.ArrayList;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusService;
import com.home.simplewarehouse.model.FifoLocation;
import com.home.simplewarehouse.model.LifoLocation;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.LocationStatus;
import com.home.simplewarehouse.model.RandomLocation;
import com.home.simplewarehouse.model.Zone;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the Zone bean.
 */
@RunWith(Arquillian.class)
public class ZoneLocationsTest {
	private static final Logger LOG = LogManager.getLogger(ZoneLocationsTest.class);

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
						ZoneService.class, ZoneBean.class, Zone.class,
						HandlingUnitService.class, HandlingUnitBean.class,
						LocationService.class, LocationBean.class, Location.class,
						LifoLocation.class, FifoLocation.class, RandomLocation.class,
						LocationStatusService.class, LocationStatusBean.class, LocationStatus.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		LOG.trace("<-- createTestArchive()");
		return archive;
	}
	
	@EJB
	ZoneService zoneService;
	
	@EJB
	LocationService locationService;
	
	/**
	 * Mandatory default constructor
	 */
	public ZoneLocationsTest() {
		super();
		// DO NOTHING HERE!
	}
	
	/**
	 * What to do before an individual test will be executed (each test)
	 */
	@Before
	public void beforeTest() {
		LOG.trace("--> beforeTest()");
		
		assertTrue(zoneService.getAll().isEmpty());
		assertTrue(locationService.getAll().isEmpty());
		
		Zone cooler = zoneService.createOrUpdate(new Zone("Cooler", 5));
		assertNotNull(cooler);
		LOG.info(cooler);
		
		Zone freezer = zoneService.createOrUpdate(new Zone("Freezer", 10));
		assertNotNull(freezer);
		LOG.info(freezer);
		
		Zone bulk = zoneService.createOrUpdate(new Zone("Bulk", 0));
		assertNotNull(bulk);
		LOG.info(bulk);
		
		Zone autoStorage = zoneService.createOrUpdate(new Zone("AutoStorage", 3));
		assertNotNull(autoStorage);
		LOG.info(autoStorage);
		
		Zone manuStorage = zoneService.createOrUpdate(new Zone("ManuStorage", 1));
		assertNotNull(manuStorage);
		LOG.info(manuStorage);
		
		locationService.createOrUpdate(new RandomLocation("LOCA"));
		locationService.createOrUpdate(new RandomLocation("LOCB"));
		locationService.createOrUpdate(new RandomLocation("LOCC"));
		locationService.createOrUpdate(new LifoLocation("LOCD"));
		locationService.createOrUpdate(new LifoLocation("LOCE"));
		locationService.createOrUpdate(new LifoLocation("LOCF"));
		locationService.createOrUpdate(new FifoLocation("LOCG"));
		locationService.createOrUpdate(new FifoLocation("LOCH"));
		locationService.createOrUpdate(new FifoLocation("LOCI"));

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

		// Cleanup zones
		List<Zone> zones = zoneService.getAll();
		zones.stream().forEach(z -> zoneService.delete(z));
		
		LOG.trace("<-- afterTest()");
	}

	@Test
	@InSequence(0)
	public void simpleAssign() {
		LOG.info("--- Test simpleAssign");
		
		assumeFalse(zoneService.getAll().isEmpty());
		assumeFalse(locationService.getAll().isEmpty());

		Zone freezer = zoneService.getById("Freezer");
		
		Location locF = locationService.getById("LOCF");
		
		// Now add LOCF to Freezer
		zoneService.addLocationTo(locF, freezer);

		assertNotNull(freezer);
		assertTrue(freezer.getLocations().contains(locF));
		
		assertEquals(freezer, locF.getZone());

// -- Cooler two Locations
		Zone cooler = zoneService.getById("Cooler");
		List<Location> coolerLocations = new ArrayList<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));

		// Now initialize Cooler to LOCA and LOCB
		zoneService.initLocationsTo(coolerLocations, cooler);

		Location locA = locationService.getById("LOCA");
		Location locB = locationService.getById("LOCB");
		assertNotNull(cooler);
		assertTrue(cooler.getLocations().contains(locA));
		assertTrue(cooler.getLocations().contains(locB));
		
		assertEquals(cooler, locA.getZone());
		assertEquals(cooler, locB.getZone());		
	}
	
	/**
	 * Test remove zone
	 */
	@Test
	@InSequence(10)
	public void removeZone() {
		LOG.info("--- Test removeZone");
		
		assumeFalse(zoneService.getAll().isEmpty());
		assumeFalse(locationService.getAll().isEmpty());
		
		Zone cooler = zoneService.getById("Cooler");
		List<Location> coolerLocations = new ArrayList<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));
		cooler.setLocations(coolerLocations);
		
		Location locA = locationService.getById("LOCA");
		Location locB = locationService.getById("LOCB");
		
		locA.setZone(cooler);
		locB.setZone(cooler);
		
		cooler = zoneService.createOrUpdate(cooler);
		locA = locationService.createOrUpdate(locA);
		locB = locationService.createOrUpdate(locB);

		assertNotNull(cooler);
		assertTrue(cooler.getLocations().contains(locA));
		assertTrue(cooler.getLocations().contains(locB));
		
		assertEquals(cooler, locA.getZone());
		assertEquals(cooler, locB.getZone());
		
		// Now remove cooler
		zoneService.delete(cooler);
		
		assertNull(zoneService.getById("Cooler"));
		
		// Location still exists but is not assigned to cooler any longer
		assertNull(locationService.getById("LOCA").getZone());
		assertNull(locationService.getById("LOCB").getZone());
	}
	
	/**
	 * Test remove location
	 */
	@Test
	@InSequence(12)
	public void removeLocation() {
		LOG.info("--- Test removeLocation");
		
		assumeFalse(zoneService.getAll().isEmpty());
		assumeFalse(locationService.getAll().isEmpty());
		
		Zone cooler = zoneService.getById("Cooler");
		List<Location> coolerLocations = new ArrayList<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));
		cooler.setLocations(coolerLocations);
		
		Location locA = locationService.getById("LOCA");
		Location locB = locationService.getById("LOCB");
		
		locA.setZone(cooler);
		locB.setZone(cooler);
		
		cooler = zoneService.createOrUpdate(cooler);
		locationService.createOrUpdate(locA);
		locationService.createOrUpdate(locB);

		// Check
		assertNotNull(cooler);
		assertTrue(cooler.getLocations().contains(locationService.getById("LOCA")));
		assertTrue(cooler.getLocations().contains(locationService.getById("LOCB")));
		
		assertEquals(cooler, locationService.getById("LOCA").getZone());
		assertEquals(cooler, locationService.getById("LOCB").getZone());
		LOG.info(cooler);
		
		// Now remove LOCA
		locationService.delete("LOCA");
		
		assertNull(locationService.getById("LOCA"));

		cooler = zoneService.getById("Cooler");
		assertNotNull(cooler);
		
		assertTrue(cooler.getLocations().contains(locB));
		assertEquals(1, cooler.getLocations().size());
		LOG.info(cooler);
	}
	
	@Test
	@InSequence(15)
	public void modifyLocation() {
		assumeFalse(zoneService.getAll().isEmpty());
		assumeFalse(locationService.getAll().isEmpty());
		
		// Prepare
		Zone cooler = zoneService.getById("Cooler");
		List<Location> coolerLocations = new ArrayList<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));
		cooler.setLocations(coolerLocations);
		
		Location locA = locationService.getById("LOCA");
		Location locB = locationService.getById("LOCB");
		
		locA.setZone(cooler);
		locB.setZone(cooler);
		
		cooler = zoneService.createOrUpdate(cooler);
		locA = locationService.createOrUpdate(locA);
		locB = locationService.createOrUpdate(locB);

		// Check prepare
		assertNotNull(cooler);
		assertTrue(cooler.getLocations().contains(locA));
		assertTrue(cooler.getLocations().contains(locB));

		assertEquals(cooler, locA.getZone());
		assertTrue(cooler.getLocations().contains(locA));
		
		// Now modify
		Zone freezer = zoneService.getById("Freezer");
		zoneService.setLocationTo(locA, freezer);
		
		// Check
		assertEquals(freezer, locA.getZone());
		assertTrue(freezer.getLocations().contains(locA));
		
		// Reread is mandatory
		cooler = zoneService.getById("Cooler");
		assertFalse(cooler.getLocations().contains(locA));
		assertNotEquals(cooler, locA.getZone());
		assertTrue(cooler.getLocations().contains(locB));
	}
}
