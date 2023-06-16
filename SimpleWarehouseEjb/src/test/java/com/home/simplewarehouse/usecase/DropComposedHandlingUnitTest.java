package com.home.simplewarehouse.usecase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.location.DimensionException;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusService;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the Handling Unit bean for composedHandlingUnits drop.
 */
@RunWith(Arquillian.class)
public class DropComposedHandlingUnitTest {
	private static final Logger LOG = LogManager.getLogger(DropComposedHandlingUnitTest.class);

	@EJB
	HandlingUnitService handlingUnitService;
	
	@EJB
	LocationService locationService;
	
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
						HandlingUnitService.class, HandlingUnitBean.class,
						LocationService.class, LocationBean.class,
						LocationStatusService.class, LocationStatusBean.class,
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
	public DropComposedHandlingUnitTest() {
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
	 * What to do after an individual test has been executed (each test)<br>
	 * <br>
	 * Cleanup the sample data
	 */
	@After
	public void afterTest() {
		LOG.trace("--> afterTest()");

		// Cleanup locations
		List<Location> locations = locationService.getAll();
		
		locations.stream().forEach(l -> locationService.delete(l));
		
		// Cleanup handling units
		List<HandlingUnit> handlingUnits = handlingUnitService.getAll();
		
		handlingUnits.stream().forEach(h -> handlingUnitService.delete(h));		

		LOG.trace("<-- afterTest()");		
	}

	@Test
	@InSequence(3)
	public void dropComposed() {
		LOG.info("--- Test dropComposed");

		assertTrue(handlingUnitService.getAll().isEmpty());
		
		// Create the base handling unit
		HandlingUnit base = handlingUnitService.createOrUpdate(new HandlingUnit("1"));
		assertNotNull(base);

		// Now place some handling units on base
		base = handlingUnitService.assign(new HandlingUnit("2"), base);
		assertNotNull(base);
		HandlingUnit hU2 = handlingUnitService.assign(new HandlingUnit("3"), handlingUnitService.getById("2"));
		assertNotNull(hU2);
		HandlingUnit hU3 = handlingUnitService.assign(new HandlingUnit("4"), handlingUnitService.getById("3"));
		assertNotNull(hU3);
		HandlingUnit hU4 = handlingUnitService.getById("4");
		assertNotNull(hU4);

		// Create a location
		Location lOA = locationService.createOrUpdate(new Location("A"));
		assertNotNull(lOA);
		
		// Now test the drop
		try {
			handlingUnitService.dropTo(lOA, hU3);
			
			lOA = locationService.getById(lOA.getLocationId());
			assertNotNull(lOA);
			
			hU3 = handlingUnitService.getById(hU3.getId());
			assertTrue(locationService.getHandlingUnits(lOA).contains(hU3));
			hU4 = handlingUnitService.getById(hU4.getId());
			assertTrue(handlingUnitService.flatContains(hU3).contains(hU4));
			
			assertFalse(handlingUnitService.flatContains(base).contains(hU3));
			assertFalse(handlingUnitService.flatContains(base).contains(hU4));

			hU2 = handlingUnitService.getById(hU2.getId());
			assertTrue(handlingUnitService.flatContains(base).contains(hU2));
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
	}
}
