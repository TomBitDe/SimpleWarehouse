package com.home.simplewarehouse.handlingunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;

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

import com.home.simplewarehouse.location.DimensionException;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusService;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.RandomLocation;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;
import com.home.simplewarehouse.zone.ZoneBean;
import com.home.simplewarehouse.zone.ZoneService;

/**
 * Test the Handling Unit bean for composing HandlingUnits.
 */
@RunWith(Arquillian.class)
public class HandlingUnitComposingTest {
	private static final Logger LOG = LogManager.getLogger(HandlingUnitComposingTest.class);

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
						ZoneService.class, ZoneBean.class,
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
	public HandlingUnitComposingTest() {
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

	/**
	 * Simple handling unit assign one on base
	 */
	@Test
	@InSequence(0)
	public void assignOneOnBase() {
		LOG.info("--- Test assignOneOnBase");

		assertTrue(handlingUnitService.getAll().isEmpty());

		HandlingUnit base = handlingUnitService.createOrUpdate(new HandlingUnit("1"));
		assertEquals(null, base.getBaseHU());
		assertTrue(base.getContains().isEmpty());
		LOG.info(base);
		
		HandlingUnit hu2 =  handlingUnitService.createOrUpdate(new HandlingUnit("2"));
		assertEquals(null, hu2.getBaseHU());
		assertTrue(hu2.getContains().isEmpty());
		LOG.info(hu2);
		
		// Now place a single handling unit on base
		base = handlingUnitService.assign(hu2, base);
		
		assertEquals(1, base.getContains().size());
		assertTrue(base.getContains().contains(hu2));
		assertNull(base.getBaseHU());
		LOG.info(base);
		
		hu2 = handlingUnitService.getById("2");
		assertTrue(hu2.getContains().isEmpty());
		assertEquals(base, hu2.getBaseHU());
		LOG.info(hu2);
		
		// Now with ids
		HandlingUnit otherBase = handlingUnitService.createOrUpdate(new HandlingUnit("3"));
		LOG.info(otherBase);
		
		HandlingUnit hu4 =  handlingUnitService.createOrUpdate(new HandlingUnit("4"));
		LOG.info(hu4);
		
		// Now place a single handling unit on base
		otherBase = handlingUnitService.assign("4", "3");
		
		assertEquals(1, otherBase.getContains().size());
		assertTrue(otherBase.getContains().contains(hu4));
		assertNull(otherBase.getBaseHU());
		LOG.info(otherBase);
		
		hu4 = handlingUnitService.getById("4");
		assertTrue(hu4.getContains().isEmpty());
		assertEquals(otherBase, hu4.getBaseHU());
		LOG.info(hu4);
		
		// Now some edge cases
		try {
			hu4 = null;
			otherBase = null;
			
			handlingUnitService.assign(hu4, otherBase);
			
			Assert.fail("Exception expected");
		}
		catch (EJBException ex) {
			assertTrue("Exception is: " + ex.getMessage(), true);
		}
		
		try {
			handlingUnitService.assign((String) null, (String) null);

			Assert.fail("Exception expected");
		}
		catch (EJBException ex) {
			assertTrue("Exception is: " + ex.getMessage(), true);
		}
	}
	
	/**
	 * Simple handling unit remove from base
	 */
	@Test
	@InSequence(9)
	public void removeFromBase() {
		LOG.info("--- Test removeFromBase");
		
		assertTrue(handlingUnitService.getAll().isEmpty());

		HandlingUnit base = handlingUnitService.createOrUpdate(new HandlingUnit("1"));

		// Now place some handling units on base
		base = handlingUnitService.assign(new HandlingUnit("2"), base);
		base = handlingUnitService.assign(new HandlingUnit("3"), base);
		base = handlingUnitService.assign(new HandlingUnit("4"), base);
		base = handlingUnitService.assign(new HandlingUnit("5"), base);
		
		// Get the set of all handling units on base
		Set<HandlingUnit> baseContains = base.getContains();
		for (HandlingUnit item : baseContains) {
			// Reread is mandatory
			item = handlingUnitService.getById(item.getId());
			// Remove the handling unit from base
			base = handlingUnitService.remove(item, base);
		}
		
		assertTrue(base.getContains().isEmpty());
		
		// Now with ids; still have the Handling Units from above
		base = handlingUnitService.assign("2", "1");
		base = handlingUnitService.assign("3", "1");
		base = handlingUnitService.assign("4", "1");
		base = handlingUnitService.assign("5", "1");
		
		// Get the set of all handling units on base
	    baseContains = base.getContains();
		for (HandlingUnit item : baseContains) {
			// Reread is mandatory
			item = handlingUnitService.getById(item.getId());
			// Remove the handling unit from base
			base = handlingUnitService.remove(item.getId(), "1");
		}
		
		assertTrue(base.getContains().isEmpty());
		
		HandlingUnit hu8 = handlingUnitService.remove(new HandlingUnit("9"), new HandlingUnit("8"));
		assertNotNull(hu8);
		
		// Now some edge cases
		try {
			handlingUnitService.remove((String) null, (String) null);

			Assert.fail("Exception expected");
		}
		catch (EJBException ex) {
			assertTrue("Exception is: " + ex.getMessage(), true);
		}
	}
	
	/**
	 * Move handling unit to other handling unit
	 */
	@Test
	@InSequence(15)
	public void moveToOtherHandlingUnit() {
		LOG.info("--- Test moveToOtherHandlingUnit");
		
		assertTrue(handlingUnitService.getAll().isEmpty());

		HandlingUnit base = handlingUnitService.createOrUpdate(new HandlingUnit("1"));

		// Now place some handling units on base
		base = handlingUnitService.assign(new HandlingUnit("2"), base);
		base = handlingUnitService.assign(new HandlingUnit("3"), base);
		base = handlingUnitService.assign(new HandlingUnit("4"), base);
		base = handlingUnitService.assign(new HandlingUnit("5"), base);
		
		// Now create an other handling unit as another base
		HandlingUnit other = handlingUnitService.createOrUpdate(new HandlingUnit("6"));
		
		// Now place a handling unit on the other base
		other = handlingUnitService.assign(new HandlingUnit("7"), other);

		// Check a handling unit placed on base
		HandlingUnit hu4 = handlingUnitService.getById("4");
		assertTrue(hu4.getContains().isEmpty());
		assertEquals(base, hu4.getBaseHU());
		
		// Now move the handling unit from base to other
		hu4 = handlingUnitService.move(hu4, other);
		
		assertEquals(handlingUnitService.getById("4"), hu4);
		
		// Reread base
		base = handlingUnitService.getById("1");
		
		// Base still contains handling units
		assertFalse(base.getContains().isEmpty());
		// But not the one moved to other
		assertFalse(base.getContains().contains(hu4));
		
		// Reread other
		other = handlingUnitService.getById("6");
		
		// Other contains handling units
		assertFalse(other.getContains().isEmpty());
		
		// The one assigned first
		assertTrue(other.getContains().contains(handlingUnitService.getById("7")));
		
		// And the moved one
		assertTrue(other.getContains().contains(hu4));
		
		// Other is the base of the moved handling unit now
		assertEquals(other, hu4.getBaseHU());
		
		// Now with ids
		HandlingUnit hu2 = handlingUnitService.getById("2");
		assertEquals(base.getId(), hu2.getBaseHU().getId());
		
		hu2 = handlingUnitService.move(hu2.getId(), other.getId());
		// Mandatory reread
		other = handlingUnitService.getById(other.getId());
		base = handlingUnitService.getById(base.getId());
		assertTrue(other.getContains().contains(hu2));
		assertFalse(base.getContains().contains(hu4));
		
		HandlingUnit hu9 = handlingUnitService.move(new HandlingUnit("9"), new HandlingUnit("8"));
		assertNotNull(hu9);
	}
	
	/**
	 * Simple handling unit empty the base
	 */
	@Test
	@InSequence(18)
	public void emptyTheBase() {
		LOG.info("--- Test emptyTheBase");

		HandlingUnit base = handlingUnitService.createOrUpdate(new HandlingUnit("1"));

		// Now place some handling units on base
		base = handlingUnitService.assign(new HandlingUnit("2"), base);
		base = handlingUnitService.assign(new HandlingUnit("3"), base);
		base = handlingUnitService.assign(new HandlingUnit("6"), base);
		
		// Now free base
		Set<HandlingUnit> freed = handlingUnitService.free(base);
		
		// Reread base is a must
		base = handlingUnitService.getById("1");
		
		// Base has no handling units any longer
		assertTrue(base.getContains().isEmpty());
		// The amount of handling units freed from base
		assertEquals(3, freed.size());
		
		// Now free with base id
		base = handlingUnitService.assign(handlingUnitService.getById("2").getId(), base.getId());
		base = handlingUnitService.assign(handlingUnitService.getById("3").getId(), base.getId());
		base = handlingUnitService.assign(handlingUnitService.getById("6").getId(), base.getId());
		
		// Now free base
		freed = handlingUnitService.free("1");
		
		// Reread base is a must
		base = handlingUnitService.getById("1");
		
		// Base has no handling units any longer
		assertTrue(base.getContains().isEmpty());
		// The amount of handling units freed from base
		assertEquals(3, freed.size());
		
		HandlingUnit otherBase = new HandlingUnit("10");
		try {
			freed = handlingUnitService.free(otherBase);

			assertTrue(freed.isEmpty());
		}
		catch (EJBException ex) {
			Assert.fail("Exception not expected: " + ex.getMessage());
		}
		
		try {
			handlingUnitService.free((String) null);

			Assert.fail("Exception expected");
		}
		catch (EJBException ex) {
			assertTrue("Exception is: " + ex.getMessage(), true);
		}
	}

	/**
	 * Simple handling unit assign one on location to base 
	 */
	@Test
	@InSequence(21)
	public void assignOneOnLocationToBase() {
		LOG.info("--- Test assignOneOnLocationToBase");

		try {
			assertTrue(handlingUnitService.getAll().isEmpty());

			// With ids
			HandlingUnit base = handlingUnitService.createOrUpdate(new HandlingUnit("3"));
			LOG.info(base);

			HandlingUnit hu4 = handlingUnitService.createOrUpdate(new HandlingUnit("4"));

			Location locA = locationService.createOrUpdate(new RandomLocation("A"));
			handlingUnitService.dropTo(locA, hu4);

			hu4 = handlingUnitService.getById("4");
			LOG.info(hu4);

			assertTrue(locationService.getHandlingUnits(locA).contains(hu4));
			
			// Now place a single handling unit on base
			base = handlingUnitService.assign("4", "3");
			
			assertEquals(1, base.getContains().size());
			assertTrue(base.getContains().contains(hu4));
			assertNull(base.getBaseHU());
			LOG.info(base);

			hu4 = handlingUnitService.getById("4");
			assertTrue(hu4.getContains().isEmpty());
			assertEquals(base, hu4.getBaseHU());
			
			// hu4 should be removed from locA
			assertTrue(locationService.getHandlingUnits(locA).isEmpty());
			
			LOG.info(hu4);
		}
		catch (DimensionException dimex) {
			Assert.fail("Not expected: " + dimex);
		}
	}
	
	/**
	 * Some edge cases to test
	 */
	@Test
	@InSequence(35)
	public void edgeCases() {
		try {
			handlingUnitService.move((String) null, (String) null);

			Assert.fail("Exception expected");
		}
		catch (EJBException ex) {
			assertTrue("Exception is: " + ex.getMessage(), true);
		}
	}
}