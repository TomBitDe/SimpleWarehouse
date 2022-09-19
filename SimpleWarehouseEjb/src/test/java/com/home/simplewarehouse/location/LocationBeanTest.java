package com.home.simplewarehouse.location;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.location.model.Location;
import com.home.simplewarehouse.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the location bean.
 */
@RunWith(Arquillian.class)
public class LocationBeanTest {
	private static final Logger LOG = LogManager.getLogger(LocationBeanTest.class);

	@EJB
	LocationLocal locationLocal;
	
	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				/* Put the test-*.xml in JARs META-INF folder as *.xml */
				.addAsManifestResource(new File("src/test/resources/META-INF/test-persistence.xml"), "persistence.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-ejb-jar.xml"), "ejb-jar.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-glassfish-ejb-jar.xml"), "glassfish-ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(
						LocationLocal.class, LocationBean.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		return archive;
	}

	/**
	 * Simple location with no reference to handling units
	 */
	@Test
	@InSequence(0)
	public void createAndGetById() {
		LOG.info("Test createAndGetById");

		Location expLocation = new Location("A");

		locationLocal.create(expLocation);
		Location location = locationLocal.getById(expLocation.getId());
		assertEquals(expLocation, location);
	}
	
	@Test
	@InSequence(1)
	public void deleteById() {
		LOG.info("Test deleteById");

		if (locationLocal.getById("A") == null) { 
		    locationLocal.create(new Location("A"));
		}
		
		Location location = locationLocal.getById("A");
		assertEquals("A", location.getId());
		
		// Delete returns the deleted location
		location = locationLocal.delete(location.getId());
		assertNotNull(location);
		assertEquals("A", location.getId());
		
		// Delete returns null because the location does not exist
		assertNull(locationLocal.delete(location.getId()));
	}

	@Test
	@InSequence(2)
	public void deleteByLocation() {
		LOG.info("Test deleteByLocation");

		if (locationLocal.getById("A") == null) { 
		    locationLocal.create(new Location("A"));
		}
		
		Location location = locationLocal.getById("A");
		assertEquals("A", location.getId());
		
		// Delete returns the deleted location
		location = locationLocal.delete(location);
		assertNotNull(location);
		assertEquals("A", location.getId());
		
		// Delete returns null because the location does not exist
		assertNull(locationLocal.delete(location));
	}
	
	@Test
	@InSequence(3)
	public void getAll() {
		LOG.info("Test getAll");
		
		// Cleanup
		List<Location> locations = locationLocal.getAll();
		
		locations.stream().forEach(l -> locationLocal.delete(l));
		
		locations = locationLocal.getAll();
		
		assertTrue(locations.isEmpty());
		
		// Prepare some locations
		locationLocal.create(new Location("A"));
		locationLocal.create(new Location("B"));
		locationLocal.create(new Location("C"));
		locationLocal.create(new Location("D"));
		locationLocal.create(new Location("E"));

		// Another test
		locations = locationLocal.getAll();

		assertFalse(locations.isEmpty());
		assertEquals(5, locations.size());
		
		//Cleanup
		locations.stream().forEach(l -> locationLocal.delete(l));
	}
}
