package com.home.simplewarehouse.location;

import static org.junit.Assert.assertEquals;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.model.EntityBase;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.Dimension;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the Dimension bean.
 */
@RunWith(Arquillian.class)
public class DimensionBeanTest {
	private static final Logger LOG = LogManager.getLogger(DimensionBeanTest.class);

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

	@EJB
	DimensionService dimensionService;
	
	@EJB
	LocationService locationService;
	
	/**
	 * Mandatory default constructor
	 */
	public DimensionBeanTest() {
		super();
		// DO NOTHING HERE!
	}

	/**
	 * All that is needed to be done before any test
	 */
	@Before
	public void beforeTest() {
		LOG.trace("--> beforeTest()");
		
		LOG.trace("<-- beforeTest()");		
	}
	
	/**
	 * All that is needed to be done after any test
	 */
	@After
	public void afterTest() {
		LOG.trace("--> afterTest()");

		// Cleanup locations
		List<Location> locations = locationService.getAll();
		
		locations.stream().forEach(l -> locationService.delete(l));
		
		// No need to cleanup dimensionService because its done by cleanup locations (1:1 relation)

		LOG.trace("<-- afterTest()");
	}

	/**
	 * Dimension is linked to location
	 */
	@Test
	@InSequence(0)
	public void create_getById() {
		LOG.info("--- Test create_getById");

		assertTrue(locationService.getAll().isEmpty());
		assertTrue(dimensionService.getAll().isEmpty());
		
		Location expLocation = locationService.createOrUpdate(new Location("A"));
		assertEquals(expLocation, locationService.getById("A"));
		
		Dimension dimension = dimensionService.getById(expLocation.getLocationId());		
		LOG.info("Dimension getById: " + dimension);
		
		// Now check the corresponding Dimension
		assertEquals(expLocation.getLocationId(), dimension.getLocationId());
		assertEquals(expLocation.getDimension().getMaxCapacity(), dimension.getMaxCapacity());
		assertEquals(EntityBase.USER_DEFAULT, dimension.getUpdateUserId());
		assertNotNull(dimension.getUpdateTimestamp());
		
		// This one has not been created
		assertNull(dimensionService.getById("B"));
	}

	/**
	 * Test the delete, getById and create sequence
	 */
	@Test
	@InSequence(1)
	public void delete_getById_create() {
		LOG.info("--- Test delete_getById_create");

		assertTrue(locationService.getAll().isEmpty());
		assertTrue(dimensionService.getAll().isEmpty());
		
		Location expLocation = locationService.createOrUpdate(new Location("A"));

		assertEquals(expLocation, locationService.getById("A"));
		
		LOG.info("Location prepared: " + expLocation);
		LOG.info("Dimension implicite prepared: "+ expLocation.getDimension());
		
		// Delete the location
		locationService.delete(expLocation);
		
		// Now check the location
	    // MANDATORY reread
		assertNull(locationService.getById("A"));
		assertNull(dimensionService.getById("A"));
	}
}
