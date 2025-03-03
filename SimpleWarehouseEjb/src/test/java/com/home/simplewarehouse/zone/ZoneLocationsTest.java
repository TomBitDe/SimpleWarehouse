package com.home.simplewarehouse.zone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeNotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusService;
import com.home.simplewarehouse.model.FifoLocation;
import com.home.simplewarehouse.model.HandlingUnit;
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
	
	@EJB
	HandlingUnitService handlingUnitService;
	
    @PersistenceContext
    private EntityManager entityManager;

	
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
		
		// Cleanup locations
		locationService.getAll().forEach(l -> {
		    locationService.delete(l);
		    entityManager.getEntityManagerFactory().getCache().evict(l.getClass(), l.getLocationId());
		});
		
		// Cleanup zones
		zoneService.deleteAll();
		
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
		locationService.getAll().forEach(l -> {
		    locationService.delete(l);
		    entityManager.getEntityManagerFactory().getCache().evict(l.getClass(), l.getLocationId());
		});
		
		// Cleanup zones
		zoneService.deleteAll();
		
		LOG.trace("<-- afterTest()");
	}

	/**
	 * Test simple assignment
	 */
	@Test
	@InSequence(0)
	public void simpleAssign() {
		LOG.info("--- Test simpleAssign");
		
		assumeFalse(zoneService.getAll().isEmpty());
		assumeFalse(locationService.getAll().isEmpty());

		Zone freezer = zoneService.getById("Freezer");
		
		Location locF = locationService.getById("LOCF");
		
// -- Freezer one location
		zoneService.addLocationTo(locF, freezer);

		assertNotNull(freezer);
		assertTrue(freezer.getLocations().contains(locF));
		
		locF = locationService.getById("LOCF");
		assertTrue(locF.getZones().contains(freezer));
		LOG.info("LOCF={}", locF);

// -- Cooler two Locations
		Zone cooler = zoneService.getById("Cooler");
		Set<Location> coolerLocations = new HashSet<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));

		// Now initialize Cooler to LOCA and LOCB
		zoneService.initZoneBy(cooler, coolerLocations);

		Location locA = locationService.getById("LOCA");
		Location locB = locationService.getById("LOCB");
		assertNotNull(cooler);
		assertTrue(cooler.getLocations().contains(locA));
		assertTrue(cooler.getLocations().contains(locB));
		
		assertTrue(locA.getZones().contains(cooler));
		assertTrue(locB.getZones().contains(cooler));		
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
		Set<Location> coolerLocations = new HashSet<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));
		cooler.setLocations(coolerLocations);
		
		Location locA = locationService.getById("LOCA");
		Location locB = locationService.getById("LOCB");
		
		Set<Zone> A = new HashSet<>();
		A.add(zoneService.getById("Cooler"));
		locA.setZones(A);
		locB.setZones(A);
		
		cooler = zoneService.createOrUpdate(cooler);
		locA = locationService.createOrUpdate(locA);
		locB = locationService.createOrUpdate(locB);

		assertNotNull(cooler);
		assertTrue(cooler.getLocations().contains(locA));
		assertTrue(cooler.getLocations().contains(locB));
		
		assertTrue(locA.getZones().contains(cooler));
		assertTrue(locB.getZones().contains(cooler));
		
		// Now remove cooler
		zoneService.delete(cooler);
		
		assertNull(zoneService.getById("Cooler"));
		
		// Location still exists but is not assigned to cooler any longer
		assertTrue(locationService.getById("LOCA").getZones().isEmpty());
		assertTrue(locationService.getById("LOCB").getZones().isEmpty());
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
		Set<Location> coolerLocations = new HashSet<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));
		cooler.setLocations(coolerLocations);
		
		Location locA = locationService.getById("LOCA");
		Location locB = locationService.getById("LOCB");
		
		Set<Zone> A = new HashSet<>();
		A.add(zoneService.getById("Cooler"));
		locA.setZones(A);
		locB.setZones(A);
		
		cooler = zoneService.createOrUpdate(cooler);
		locA = locationService.createOrUpdate(locA);
		locB = locationService.createOrUpdate(locB);

		// Check
		assertNotNull(cooler);
		assertTrue(cooler.getLocations().contains(locA));
		assertTrue(cooler.getLocations().contains(locB));
		
		assertTrue(locA.getZones().contains(cooler));
		assertTrue(locB.getZones().contains(cooler));
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
	
	/**
	 * Test move a single Location to a Zone
	 */
	@Test
	@InSequence(15)
	public void moveLocation() {
		LOG.info("--- Test moveLocation");

		assumeFalse(zoneService.getAll().isEmpty());
		assumeFalse(locationService.getAll().isEmpty());
		
		// Prepare
		Zone cooler = zoneService.getById("Cooler");
		Set<Location> coolerLocations = new HashSet<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));
		cooler.setLocations(coolerLocations);
		
		Location locA = locationService.getById("LOCA");
		Location locB = locationService.getById("LOCB");
		
		Set<Zone> A = new HashSet<>();
		A.add(zoneService.getById("Cooler"));
		locA.setZones(A);
		locB.setZones(A);

		cooler = zoneService.createOrUpdate(cooler);
		locA = locationService.createOrUpdate(locA);
		locB = locationService.createOrUpdate(locB);

		// Check prepare
		assertNotNull(cooler);
		assertTrue(cooler.getLocations().contains(locA));
		assertTrue(cooler.getLocations().contains(locB));

		assertTrue(locA.getZones().contains(cooler));
		assertTrue(cooler.getLocations().contains(locA));
		
		// Now modify
		Zone freezer = zoneService.getById("Freezer");
		zoneService.moveLocation(locA, cooler, freezer);
		
		// Check
		assertTrue(locA.getZones().contains(freezer));
		assertTrue(freezer.getLocations().contains(locA));
		
		// Reread is mandatory
		cooler = zoneService.getById("Cooler");
		assertFalse(cooler.getLocations().contains(locA));
		assertTrue(cooler.getLocations().contains(locB));
		assertFalse(locA.getZones().contains(cooler));
	}

	/**
	 * Test move Locations to a Zone
	 */
	@Test
	@InSequence(17)
	public void moveLocations() {
		LOG.info("--- Test moveLocations");

		assumeFalse(zoneService.getAll().isEmpty());
		assumeFalse(locationService.getAll().isEmpty());
		
		// Prepare
		Zone cooler = zoneService.getById("Cooler");
		Set<Location> coolerLocations = new HashSet<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));
		cooler.setLocations(coolerLocations);
		
		Location locA = locationService.getById("LOCA");
		Location locB = locationService.getById("LOCB");
		
		Set<Zone> A = new HashSet<>();
		A.add(zoneService.getById("Cooler"));
		locA.setZones(A);
		locB.setZones(A);

		cooler = zoneService.createOrUpdate(cooler);
		locA = locationService.createOrUpdate(locA);
		locB = locationService.createOrUpdate(locB);

		// Check prepare
		assertNotNull(cooler);
		assertTrue(cooler.getLocations().contains(locA));
		assertTrue(cooler.getLocations().contains(locB));

		assertTrue(locA.getZones().contains(cooler));
		assertTrue(cooler.getLocations().contains(locA));
		
		// Now modify
		Zone freezer = zoneService.getById("Freezer");
		Set<Location> locations = new HashSet<>();
		locations.add(locA);
		locations.add(locB);
		zoneService.moveLocations(locations, cooler, freezer);
		
		// Check
		assertTrue(locA.getZones().contains(freezer));
		assertTrue(freezer.getLocations().contains(locA));
		assertTrue(locB.getZones().contains(freezer));
		assertTrue(freezer.getLocations().contains(locB));
		
		// Reread is mandatory
		cooler = zoneService.getById("Cooler");
		assertFalse(cooler.getLocations().contains(locA));
		assertFalse(cooler.getLocations().contains(locB));
		assertTrue(cooler.getLocations().isEmpty());
		assertFalse(locA.getZones().contains(cooler));
	}

	/**
	 * Test clear a Zone
	 */
	@Test
	@InSequence(20)
	public void clearZone() {
		assumeFalse(zoneService.getAll().isEmpty());
		assumeFalse(locationService.getAll().isEmpty());
		
		// Clear already empty zone
		Zone bulk = zoneService.getById("Bulk");
		assumeNotNull(bulk);
		
		zoneService.clear(bulk);
		assertTrue(bulk.getLocations().isEmpty());
		
		// Clear zone with four Locations
		Set<Location> coolerLocations = new HashSet<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));
		coolerLocations.add(locationService.getById("LOCC"));
		coolerLocations.add(locationService.getById("LOCD"));
		zoneService.initZoneBy(zoneService.getById("Cooler"), coolerLocations);
		
		assertEquals(4, zoneService.getById("Cooler").getLocations().size());
		
		assertTrue(locationService.getById("LOCA").getZones().contains(zoneService.getById("Cooler")));
		assertTrue(locationService.getById("LOCB").getZones().contains(zoneService.getById("Cooler")));
		assertTrue(locationService.getById("LOCC").getZones().contains(zoneService.getById("Cooler")));
		assertTrue(locationService.getById("LOCD").getZones().contains(zoneService.getById("Cooler")));
		
		Zone cooler = zoneService.getById("Cooler");
		// Clear zone with content
		zoneService.clear(cooler);
		
		cooler = zoneService.getById("Cooler");
		// Check
		assertTrue(cooler.getLocations().isEmpty());
		assertTrue(locationService.getById("LOCA").getZones().isEmpty());
		assertTrue(locationService.getById("LOCB").getZones().isEmpty());
		assertTrue(locationService.getById("LOCC").getZones().isEmpty());
		assertTrue(locationService.getById("LOCD").getZones().isEmpty());
	}

	/**
	 * Test clear all Zones
	 */
	@Test
	@InSequence(22)
	public void clearAllZones() {
		assumeFalse(zoneService.getAll().isEmpty());
		assumeFalse(locationService.getAll().isEmpty());

		assumeNotNull(zoneService.getById("Cooler"));
		assumeNotNull(zoneService.getById("Freezer"));
		assumeNotNull(zoneService.getById("Bulk"));
		
		Set<Location> coolerLocations = new HashSet<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));
		zoneService.initZoneBy(zoneService.getById("Cooler"), coolerLocations);

		Set<Location> freezerLocations = new HashSet<>();
		freezerLocations.add(locationService.getById("LOCC"));
		freezerLocations.add(locationService.getById("LOCD"));
		freezerLocations.add(locationService.getById("LOCE"));
		zoneService.initZoneBy(zoneService.getById("Freezer"), freezerLocations);
		
		zoneService.addLocationTo(locationService.getById("LOCF"), zoneService.getById("Bulk"));
		
		zoneService.clearAll();
		
		assertFalse(zoneService.getAll().isEmpty());
		assertNotNull(zoneService.getById("Cooler"));
		assertNotNull(zoneService.getById("Freezer"));
		assertNotNull(zoneService.getById("Bulk"));
		assertNotNull(zoneService.getById("AutoStorage"));
		assertNotNull(zoneService.getById("ManuStorage"));
	}

	/**
	 * Test delete all Zones
	 */
	@Test
	@InSequence(25)
	public void deleteAllZones() {
		assumeFalse(zoneService.getAll().isEmpty());
		assumeFalse(locationService.getAll().isEmpty());

		assumeNotNull(zoneService.getById("Cooler"));
		assumeNotNull(zoneService.getById("Freezer"));
		assumeNotNull(zoneService.getById("Bulk"));
		
		Set<Location> coolerLocations = new HashSet<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));
		zoneService.initZoneBy(zoneService.getById("Cooler"), coolerLocations);

		Set<Location> freezerLocations = new HashSet<>();
		freezerLocations.add(locationService.getById("LOCC"));
		freezerLocations.add(locationService.getById("LOCD"));
		freezerLocations.add(locationService.getById("LOCE"));
		zoneService.initZoneBy(zoneService.getById("Freezer"), freezerLocations);
		
		zoneService.addLocationTo(locationService.getById("LOCF"), zoneService.getById("Bulk"));
		
		zoneService.deleteAll();
		
		assertTrue(zoneService.getAll().isEmpty());
	}
	
	/**
	 * Test get all Locations for a Zone
	 */
	@Test
	@InSequence(28)
	public void getAllLocationsForZone() {
		assumeFalse(zoneService.getAll().isEmpty());
		assumeFalse(locationService.getAll().isEmpty());

		assumeNotNull(zoneService.getById("Cooler"));
		assumeNotNull(zoneService.getById("Freezer"));
		assumeNotNull(zoneService.getById("Bulk"));
		
		Set<Location> coolerLocations = new HashSet<>();
		coolerLocations.add(locationService.getById("LOCA"));
		coolerLocations.add(locationService.getById("LOCB"));
		zoneService.initZoneBy(zoneService.getById("Cooler"), coolerLocations);

		Set<Location> freezerLocations = new HashSet<>();
		freezerLocations.add(locationService.getById("LOCC"));
		freezerLocations.add(locationService.getById("LOCD"));
		freezerLocations.add(locationService.getById("LOCE"));
		zoneService.initZoneBy(zoneService.getById("Freezer"), freezerLocations);

		zoneService.addLocationTo(locationService.getById("LOCF"), zoneService.getById("Bulk"));
		
		zoneService.createOrUpdate(new Zone("LowTemp"));
		
		zoneService.addLocationTo(locationService.getById("LOCF"), zoneService.getById("LowTemp"));

		// LOGF is in two Zones
		Set<Location> allLowTemp = zoneService.getAllLocations(zoneService.getById("LowTemp"));
		assertNotNull(allLowTemp);
		assertFalse(allLowTemp.isEmpty());
		assertTrue(allLowTemp.contains(locationService.getById("LOCF")));

		Set<Location> allBulk = zoneService.getAllLocations("Bulk");		
		assertNotNull(allBulk);
		assertFalse(allBulk.isEmpty());
		assertTrue(allLowTemp.contains(locationService.getById("LOCF")));
    }

	/**
	 * Test get all HandlingUnits for a Zone
	 */
	@Test
	@InSequence(31)
	public void getAllHandlingUnitsForZone() {
		assumeFalse(zoneService.getAll().isEmpty());
		assumeFalse(locationService.getAll().isEmpty());

		assumeNotNull(zoneService.getById("Cooler"));
		assumeNotNull(zoneService.getById("Freezer"));
		assumeNotNull(zoneService.getById("Bulk"));
		
		Set<Location> freezerLocations = new HashSet<>();
		freezerLocations.add(locationService.getById("LOCC"));
		freezerLocations.add(locationService.getById("LOCD"));
		freezerLocations.add(locationService.getById("LOCE"));
		zoneService.initZoneBy(zoneService.getById("Freezer"), freezerLocations);
		
		try {
			handlingUnitService.createOrUpdate(new HandlingUnit("HU1"));
			handlingUnitService.createOrUpdate(new HandlingUnit("HU2"));
			handlingUnitService.createOrUpdate(new HandlingUnit("HU3"));
			handlingUnitService.dropTo(locationService.getById("LOCC"), handlingUnitService.getById("HU1"));
			handlingUnitService.dropTo(locationService.getById("LOCC"), handlingUnitService.getById("HU2"));
			handlingUnitService.dropTo(locationService.getById("LOCC"), handlingUnitService.getById("HU3"));

			Set<HandlingUnit> allHus = zoneService.getAllHandlingUnits(zoneService.getById("Freezer"));

			assertNotNull(allHus);
			assertFalse(allHus.isEmpty());
			assertTrue(allHus.contains(handlingUnitService.getById("HU2")));
		}
		catch (Exception e) {
			Assert.fail("Not expected: " + e);
		}
	}
}
