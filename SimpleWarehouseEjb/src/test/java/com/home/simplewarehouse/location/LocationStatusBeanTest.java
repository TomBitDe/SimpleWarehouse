package com.home.simplewarehouse.location;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Timestamp;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
import com.home.simplewarehouse.handlingunit.HandlingUnitLocal;
import com.home.simplewarehouse.model.EntityBase;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.LocationStatus;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the location status bean.
 */
@RunWith(Arquillian.class)
public class LocationStatusBeanTest {
	private static final Logger LOG = LogManager.getLogger(LocationStatusBeanTest.class);

	@EJB
	LocationStatusLocal locationStatusLocal;
	
	@EJB
	LocationLocal locationLocal;
	
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

	@Before
	public void beforeTest() {
		LOG.trace("--> beforeTest()");
		
		LOG.trace("<-- beforeTest()");		
	}
	
	@After
	public void afterTest() {
		LOG.trace("--> afterTest()");

		// Cleanup locations
		List<Location> locations = locationLocal.getAll();
		
		locations.stream().forEach(l -> locationLocal.delete(l));
		
		// Cleanup location statuses
		List<LocationStatus> locationStatuses = locationStatusLocal.getAll();
		
		locationStatuses.stream().forEach(ls -> locationStatusLocal.delete(ls));		

		LOG.trace("<-- afterTest()");
	}

	/**
	 * Simple location status with no reference to location
	 */
	@Test
	@InSequence(0)
	public void create_getById() {
		LOG.info("--- Test create_getById");

		assertTrue(locationStatusLocal.getAll().isEmpty());
		
		LocationStatus expLocationStatus = new LocationStatus("A");

		locationStatusLocal.create(expLocationStatus);
		LOG.info("LocationStatus created: " + expLocationStatus);
		LocationStatus locationStatus = locationStatusLocal.getById(expLocationStatus.getLocationId());		
		LOG.info("LocationStatus getById: " + locationStatus);
		
		assertEquals(expLocationStatus, locationStatus);
		assertEquals(EntityBase.USER_DEFAULT, locationStatus.getUpdateUserId());
		assertNotNull(locationStatus.getUpdateTimestamp());
		
		// This one has not been created
		assertNull(locationStatusLocal.getById("B"));
	}
	
	@Test
	@InSequence(1)
	@Ignore
	public void delete_getById_create() {
		LOG.info("--- Test delete_getById_create");

		assertTrue(locationStatusLocal.getAll().isEmpty());
		
		locationStatusLocal.create(new LocationStatus("A"));

	    LocationStatus locationStatus = locationStatusLocal.getById("A");
		LOG.info("LocationStatus getById: " + locationStatus);
		
		assertEquals("A", locationStatus.getLocationId());
		
		// Delete the location status
		locationStatusLocal.delete(locationStatus);
		assertNotNull(locationStatus);
		assertEquals("A", locationStatus.getLocationId());
		LOG.info("Location deleted: " + locationStatus.getLocationId());
		
		locationStatusLocal.create(new LocationStatus("A", "Test"));
		locationStatus = locationStatusLocal.getById("A");				
		assertNotNull(locationStatus);
		assertEquals("Test", locationStatus.getUpdateUserId());
		assertNotNull(locationStatus.getUpdateTimestamp());
		LOG.info("LocationStatus created: " + locationStatus);

		// Delete the location status
		locationStatusLocal.delete(locationStatus);
		LOG.info("Location deleted: " + locationStatus.getLocationId());
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		locationLocal.create(new Location("A", "Test", ts));
		locationStatus = locationStatusLocal.getById("A");
		assertNotNull(locationStatus);
		assertEquals("Test", locationStatus.getUpdateUserId());
		assertEquals(ts, locationStatus.getUpdateTimestamp());
		LOG.info("LocationStatus created: " + locationStatus);
	}

	@Test
	@InSequence(2)
	@Ignore
	public void deleteByLocationStatus() {
		LOG.info("--- Test deleteByLocationStatus");

		assertTrue(locationStatusLocal.getAll().isEmpty());
		
	    locationStatusLocal.create(new LocationStatus("A"));

	    LocationStatus locationStatus = locationStatusLocal.getById("A");
		assertNotNull(locationStatus);
		assertEquals("A", locationStatus.getLocationId());
		
		// Delete the location
		locationStatusLocal.delete(locationStatus);
		assertNotNull(locationStatus);
		assertEquals("A", locationStatus.getLocationId());
		assertNull(locationStatusLocal.getById("A"));
		LOG.info("LocationStatus deleted: " + locationStatus.getLocationId());
		
		locationStatus = locationStatusLocal.getById("A");
		assertNull(locationStatus);
	}
	
	@Test
	@InSequence(3)
	@Ignore
	public void getAll() {
		LOG.info("--- Test getAll");
		
		assertTrue(locationStatusLocal.getAll().isEmpty());
		
		// Prepare some locations statuses ; 5 location statuses
		locationStatusLocal.create(new LocationStatus("A", "Test"));
		locationStatusLocal.create(new LocationStatus("B", "Test"));
		locationStatusLocal.create(new LocationStatus("C", "Test"));
		locationStatusLocal.create(new LocationStatus("D", "Test"));
		locationStatusLocal.create(new LocationStatus("E", "Test"));

		// Get them all and output
		List<LocationStatus> locationStatuses = locationStatusLocal.getAll();
		locationStatuses.stream().forEach( ls -> LOG.info(ls.toString()) );

		assertFalse(locationStatuses.isEmpty());
		assertEquals(5, locationStatuses.size());
	}
	
	@Test
	@InSequence(4)
	@Ignore
	public void deleteLocationWithLocationStatus() {
		LOG.info("--- Test deleteLocationWithLocationStatus");
		
		assertTrue(locationLocal.getAll().isEmpty());
		assertTrue(locationStatusLocal.getAll().isEmpty());
		
		// Prepare a location
		locationLocal.create(new Location("A", "Test"));
		Location locA = locationLocal.getById("A");
		LOG.info("Location prepared: " + locA);
		
		// TODO
		
		// Now delete the location
		locA = locationLocal.getById("A");
		locationLocal.delete(locA);
		LOG.info("Location deleted: " + locA.getLocationId());
	}
}
