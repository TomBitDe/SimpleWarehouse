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
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
import com.home.simplewarehouse.handlingunit.HandlingUnitLocal;
import com.home.simplewarehouse.model.EntityBase;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the location bean.
 */
@RunWith(Arquillian.class)
public class LocationBeanTest {
	private static final Logger LOG = LogManager.getLogger(LocationBeanTest.class);

	@EJB
	LocationLocal locationLocal;
	
	@EJB
	HandlingUnitLocal handlingUnitLocal;
	
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
		
		// Cleanup handling units
		List<HandlingUnit> handlingUnits = handlingUnitLocal.getAll();
		
		handlingUnits.stream().forEach(h -> handlingUnitLocal.delete(h));		

		LOG.trace("<-- afterTest()");
	}

	/**
	 * Simple location with no reference to handling units
	 */
	@Test
	@InSequence(0)
	public void create_getById() {
		LOG.info("--- Test create_getById");

		assertTrue(locationLocal.getAll().isEmpty());
		
		Location expLocation = new Location("A");

		locationLocal.create(expLocation);
		LOG.info("Location created: " + expLocation);
		Location location = locationLocal.getById(expLocation.getLocationId());		
		LOG.info("Location getById: " + location);
		
		assertEquals(expLocation, location);
		assertEquals(EntityBase.USER_DEFAULT, location.getUpdateUserId());
		assertNotNull(location.getUpdateTimestamp());
		
		assertNull(locationLocal.getById("B"));
	}
	
	@Test
	@InSequence(1)
	public void delete_getById_create() {
		LOG.info("--- Test delete_getById_create");

		assertTrue(locationLocal.getAll().isEmpty());
		
	    locationLocal.create(new Location("A"));

	    Location location = locationLocal.getById("A");
		LOG.info("Location getById: " + location);
		
		assertEquals("A", location.getLocationId());
		
		// Delete the location
		locationLocal.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		LOG.info("Location deleted: " + location.getLocationId());
		
		locationLocal.create(new Location("A", "Test"));
		location = locationLocal.getById("A");				
		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertNotNull(location.getUpdateTimestamp());
		LOG.info("Location created: " + location);

		// Delete the location
		locationLocal.delete(location);
		LOG.info("Location deleted: " + location.getLocationId());
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		locationLocal.create(new Location("A", "Test", ts));
		location = locationLocal.getById("A");
		assertNotNull(location);
		assertEquals("Test", location.getUpdateUserId());
		assertEquals(ts, location.getUpdateTimestamp());
		LOG.info("Location created: " + location);
	}

	@Test
	@InSequence(2)
	public void deleteByLocation() {
		LOG.info("--- Test deleteByLocation");

		assertTrue(locationLocal.getAll().isEmpty());
		
	    locationLocal.create(new Location("A"));

	    Location location = locationLocal.getById("A");
		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		
		// Delete the location
		locationLocal.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getLocationId());
		assertNull(locationLocal.getById("A"));
		LOG.info("Location deleted: " + location.getLocationId());
	}
	
	@Test
	@InSequence(3)
	public void getAll() {
		LOG.info("--- Test getAll");
		
		assertTrue(locationLocal.getAll().isEmpty());
		
		// Prepare some locations; 5 locations
		locationLocal.create(new Location("A", "Test"));
		locationLocal.create(new Location("B", "Test"));
		locationLocal.create(new Location("C", "Test"));
		locationLocal.create(new Location("D", "Test"));
		locationLocal.create(new Location("E", "Test"));

		// Get them all and output
		List<Location> locations = locationLocal.getAll();
		locations.stream().forEach( l -> LOG.info(l.toString()) );

		assertFalse(locations.isEmpty());
		assertEquals(5, locations.size());
	}
	
	@Test
	@InSequence(4)
	public void deleteLocationWithHandlingUnits() {
		LOG.info("--- Test deleteLocationWithHandlingUnits");
		
		assertTrue(locationLocal.getAll().isEmpty());
		
		// Prepare a location
		locationLocal.create(new Location("A", "Test"));
		Location locA = locationLocal.getById("A");
		LOG.info("Location prepared: " + locA);
		
		// Drop to make a relation
		new HandlingUnit("1", "Test").dropTo(locA);
		HandlingUnit hU2 = new HandlingUnit("2", "Test");
		hU2.dropTo(locA);
		new HandlingUnit("3", "Test").dropTo(locA);
		new HandlingUnit("4", "Test").dropTo(locA);
		HandlingUnit hU5 = new HandlingUnit("5", "Test");
		hU5.dropTo(locA);
		
		assertNotNull(locA);	
		assertFalse(locA.getHandlingUnits().isEmpty());
		assertEquals(5, locA.getHandlingUnits().size());
		
		assertFalse(locA.getHandlingUnits().contains(new HandlingUnit("12")));
		
		LOG.info("5 HandlingUits dropped to " + locA.getLocationId() + "   " + locA);

		LOG.info("Sample hU2 and hU5");
		LOG.info(hU2);
		LOG.info(hU5);
		
		// Now delete the location
		locationLocal.delete(locA);
		LOG.info("Location deleted: " + locA.getLocationId());
		
		hU2 = handlingUnitLocal.getById("2");
		hU5 = handlingUnitLocal.getById("5");
		
		assertNotNull(hU2);
		assertNotNull(hU5);
		
		assertNull(hU2.getLocation());
		assertNull(hU5.getLocation());
		
		LOG.info("Sample hU2 and hU5 have no longer a location");
		LOG.info(hU2);
		LOG.info(hU5);
	}
}
