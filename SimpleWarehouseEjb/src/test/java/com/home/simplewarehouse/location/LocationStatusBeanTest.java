package com.home.simplewarehouse.location;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
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
import com.home.simplewarehouse.model.ErrorStatus;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.LocationStatus;
import com.home.simplewarehouse.model.LockStatus;
import com.home.simplewarehouse.model.LtosStatus;
import com.home.simplewarehouse.model.RandomLocation;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;
import com.home.simplewarehouse.zone.ZoneBean;
import com.home.simplewarehouse.zone.ZoneService;

/**
 * Test the LocationStatus bean.
 */
@RunWith(Arquillian.class)
public class LocationStatusBeanTest {
	private static final Logger LOG = LogManager.getLogger(LocationStatusBeanTest.class);

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
						LocationStatusService.class, LocationStatusBean.class,
						LocationService.class, LocationBean.class,
						HandlingUnitService.class, HandlingUnitBean.class,
						ZoneService.class, ZoneBean.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		LOG.trace("<-- createTestArchive()");
		return archive;
	}

	@EJB
	LocationStatusService locationStatusService;
	
	@EJB
	LocationService locationService;
	
	/**
	 * Mandatory default constructor
	 */
	public LocationStatusBeanTest() {
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
	 * What to do after an individual test will be executed (each test)
	 */
	@After
	public void afterTest() {
		LOG.trace("--> afterTest()");

		// Cleanup locations
		List<Location> locations = locationService.getAll();
		
		locations.stream().forEach(l -> locationService.delete(l));
		
		// No need to cleanup locationService statuses because its done by cleanup locations (1:1 relation)

		LOG.trace("<-- afterTest()");
	}

	/**
	 * Location status is always linked to location
	 */
	@Test
	@InSequence(0)
	public void create_getById() {
		LOG.info("--- Test create_getById");

		assertTrue(locationService.getAll().isEmpty());
		assertTrue(locationStatusService.getAll().isEmpty());
		
		Location expLocation = new RandomLocation("A");
		
		Location location = locationService.createOrUpdate(expLocation);
		assertEquals(expLocation, location);
		
		LocationStatus locationStatus = location.getLocationStatus();		
		LOG.info("LocationStatus getById: " + locationStatus);
		
		// Now check the corresponding LocationStatus
		assertEquals("A", locationStatus.getLocationId());
		assertEquals(LocationStatus.ERROR_STATUS_DEFAULT, locationStatus.getErrorStatus());
		assertEquals(LocationStatus.LTOS_STATUS_DEFAULT, locationStatus.getLtosStatus());
		assertEquals(LocationStatus.LOCK_STATUS_DEFAULT, locationStatus.getLockStatus());
		
		// This one has not been created
	    // MANDATORY reread
		assertNull(locationStatusService.getById("B"));
	}
	
	/**
	 * Test the delete, getById and create sequence
	 */
	@Test
	@InSequence(1)
	public void delete_getById_create() {
		LOG.info("--- Test delete_getById_create");

		assertTrue(locationService.getAll().isEmpty());
		assertTrue(locationStatusService.getAll().isEmpty());
		
		Location expLocation = new RandomLocation("A");
		
		locationService.createOrUpdate(expLocation);
		assertEquals(expLocation, locationService.getById("A"));
		
		LOG.info("Location prepared: " + expLocation);
		LOG.info("LocationStatus implicite prepared: "+ expLocation.getLocationStatus());
		
		// Delete the location
		locationService.delete(expLocation);
		
		// Now check the location
	    // MANDATORY reread
		assertNull(locationService.getById("A"));
		assertNull(locationStatusService.getById("A"));
	}
	

	/**
	 * Test modify dimension
	 */
	@Test
	@InSequence(4)
	public void modifyLocationStatus() {
		LOG.info("--- Test modifyLocationStatus");

		assertTrue(locationService.getAll().isEmpty());
		assertTrue(locationStatusService.getAll().isEmpty());
		
		// With Dimension DEFAULTS
		Location location = new RandomLocation("A");
		// Now change
		location.setLocationStatus(ErrorStatus.NONE, LtosStatus.YES, LockStatus.DROP_LOCKED);
		
		Location expLocation = locationService.createOrUpdate(location);

		assertEquals("A", expLocation.getLocationId());
		assertEquals(ErrorStatus.NONE, expLocation.getLocationStatus().getErrorStatus());
		assertEquals(LtosStatus.YES, expLocation.getLocationStatus().getLtosStatus());
		assertEquals(LockStatus.DROP_LOCKED, expLocation.getLocationStatus().getLockStatus());
		
		// Change again
		location = expLocation;
		location.getLocationStatus().setErrorStatus(null);;
		location.getLocationStatus().setLtosStatus(null);
		location.getLocationStatus().setLockStatus(null);
		
		expLocation = locationService.createOrUpdate(location);

		assertEquals("A", expLocation.getLocationId());
		assertEquals(ErrorStatus.ERROR, expLocation.getLocationStatus().getErrorStatus());
		assertEquals(LtosStatus.YES, expLocation.getLocationStatus().getLtosStatus());
		assertEquals(LockStatus.LOCKED, expLocation.getLocationStatus().getLockStatus());

		// Change again
		location = expLocation;
		location.getLocationStatus().setErrorStatus(ErrorStatus.NONE);;
		location.getLocationStatus().setLtosStatus(LtosStatus.NO);
		location.getLocationStatus().setLockStatus(LockStatus.PICK_LOCKED);
		
		expLocation = locationService.createOrUpdate(location);

		assertEquals("A", expLocation.getLocationId());
		assertEquals(ErrorStatus.NONE, expLocation.getLocationStatus().getErrorStatus());
		assertEquals(LtosStatus.NO, expLocation.getLocationStatus().getLtosStatus());
		assertEquals(LockStatus.PICK_LOCKED, expLocation.getLocationStatus().getLockStatus());

		location = new RandomLocation("B");
		// Change again
		location.setLocationStatus(ErrorStatus.NONE, LtosStatus.NO, LockStatus.UNLOCKED);
		
		expLocation = locationService.createOrUpdate(location);

		assertEquals("B", expLocation.getLocationId());
		assertEquals(ErrorStatus.NONE, expLocation.getLocationStatus().getErrorStatus());
		assertEquals(LtosStatus.NO, expLocation.getLocationStatus().getLtosStatus());
		assertEquals(LockStatus.UNLOCKED, expLocation.getLocationStatus().getLockStatus());
	}
	
	/**
	 * Test equals
	 */
	@Test
	@InSequence(8)
	public void equalsTest() {
		LOG.info("--- Test equalsTest");

		assertTrue(locationService.getAll().isEmpty());
		assertTrue(locationStatusService.getAll().isEmpty());
		
		// With Dimension DEFAULTS
		Location location = new RandomLocation("A");
		
		Location expLocation = locationService.createOrUpdate(location);
		
		assertEquals(expLocation.getLocationStatus(), location.getLocationStatus());
		
		expLocation = locationService.createOrUpdate(new RandomLocation("B"));
		
		assertNotEquals(expLocation.getLocationStatus(), location.getLocationStatus());
		assertNotEquals(expLocation.getLocationStatus(), null);
		assertNotEquals(expLocation, location.getLocationStatus());
	}
}
