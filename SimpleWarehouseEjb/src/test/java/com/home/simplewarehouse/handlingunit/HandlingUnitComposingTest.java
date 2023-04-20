package com.home.simplewarehouse.handlingunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Set;

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

import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusLocal;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the Handling Unit bean for composing HandlingUnits.
 */
@RunWith(Arquillian.class)
public class HandlingUnitComposingTest {
	private static final Logger LOG = LogManager.getLogger(HandlingUnitComposingTest.class);

	@EJB
	HandlingUnitLocal handlingUnitLocal;
	
	@EJB
	LocationLocal locationLocal;
	
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
						HandlingUnitLocal.class, HandlingUnitBean.class,
						LocationLocal.class, LocationBean.class,
						LocationStatusLocal.class, LocationStatusBean.class,
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
		List<Location> locations = locationLocal.getAll();
		
		locations.stream().forEach(l -> locationLocal.delete(l));
		
		// Cleanup handling units
		List<HandlingUnit> handlingUnits = handlingUnitLocal.getAll();
		
		handlingUnits.stream().forEach(h -> handlingUnitLocal.delete(h));		

		LOG.trace("<-- afterTest()");		
	}

	/**
	 * Simple handling unit assign one on base
	 */
	@Test
	@InSequence(0)
	public void assignOneOnBase() {
		LOG.info("--- Test assignOneOnBase");

		assertTrue(handlingUnitLocal.getAll().isEmpty());

		HandlingUnit base = handlingUnitLocal.create(new HandlingUnit("1"));
		assertEquals(null, base.getBaseHU());
		assertTrue(base.getContains().isEmpty());
		LOG.info(base);
		
		HandlingUnit hu2 =  handlingUnitLocal.create(new HandlingUnit("2"));
		assertEquals(null, hu2.getBaseHU());
		assertTrue(hu2.getContains().isEmpty());
		LOG.info(hu2);
		
		// Now place a single handling unit on base
		base = handlingUnitLocal.assign(hu2, base);
		
		assertEquals(1, base.getContains().size());
		assertTrue(base.getContains().contains(hu2));
		assertNull(base.getBaseHU());
		LOG.info(base);
		
		hu2 = handlingUnitLocal.getById("2");
		assertTrue(hu2.getContains().isEmpty());
		assertEquals(base, hu2.getBaseHU());
		LOG.info(hu2);
	}
	
	/**
	 * Simple handling unit assign four on base
	 */
	@Test
	@InSequence(3)
	public void assignFourOnBase() {
		LOG.info("--- Test assignFourOnBase");

		assertTrue(handlingUnitLocal.getAll().isEmpty());

		HandlingUnit base = handlingUnitLocal.create(new HandlingUnit("1"));

		// Now place some handling units on base
		base = handlingUnitLocal.assign(new HandlingUnit("2"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("3"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("4"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("5"), base);

		LOG.info(base);
		assertEquals(4, base.getContains().size());
	}
	
	/**
	 * Simple handling unit remove from base
	 */
	@Test
	@InSequence(9)
	public void removeFromBase() {
		LOG.info("--- Test removeFromBase");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());

		HandlingUnit base = handlingUnitLocal.create(new HandlingUnit("1"));

		// Now place some handling units on base
		base = handlingUnitLocal.assign(new HandlingUnit("2"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("3"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("4"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("5"), base);
		
		// Get the set of all handling units on base
		Set<HandlingUnit> baseContains = base.getContains();
		for (HandlingUnit item : baseContains) {
			// Reread is mandatory
			item = handlingUnitLocal.getById(item.getId());
			// Remove the handling unit from base
			base = handlingUnitLocal.remove(item, base);
		}
		
		assertTrue(base.getContains().isEmpty());
	}
	
	/**
	 * Move handling unit to other handling unit
	 */
	@Test
	@InSequence(15)
	public void moveToOtherHandlingUnit() {
		LOG.info("--- Test moveToOtherHandlingUnit");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());

		HandlingUnit base = handlingUnitLocal.create(new HandlingUnit("1"));

		// Now place some handling units on base
		base = handlingUnitLocal.assign(new HandlingUnit("2"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("3"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("4"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("5"), base);
		
		// Now create an other handling unit as another base
		HandlingUnit other = handlingUnitLocal.create(new HandlingUnit("6"));
		
		// Now place a handling unit on the other base
		other = handlingUnitLocal.assign(new HandlingUnit("7"), other);

		// Check a handling unit placed on base
		HandlingUnit hu4 = handlingUnitLocal.getById("4");
		assertTrue(hu4.getContains().isEmpty());
		assertEquals(base, hu4.getBaseHU());
		
		// Now move the handling unit from base to other
		hu4 = handlingUnitLocal.move(hu4, other);
		
		assertEquals(handlingUnitLocal.getById("4"), hu4);
		
		// Reread base
		base = handlingUnitLocal.getById("1");
		
		// Base still contains handling units
		assertFalse(base.getContains().isEmpty());
		// But not the one moved to other
		assertFalse(base.getContains().contains(hu4));
		
		// Reread other
		other = handlingUnitLocal.getById("6");
		
		// Other contains handling units
		assertFalse(other.getContains().isEmpty());
		
		// The one assigned first
		assertTrue(other.getContains().contains(handlingUnitLocal.getById("7")));
		
		// And the moved one
		assertTrue(other.getContains().contains(hu4));
		
		// Other is the base of the moved handling unit now
		assertEquals(other, hu4.getBaseHU());
	}
	
	/**
	 * Simple handling unit empty the base
	 */
	@Test
	@InSequence(18)
	public void emptyTheBase() {
		LOG.info("--- Test emptyTheBase");

		HandlingUnit base = handlingUnitLocal.create(new HandlingUnit("1"));

		// Now place some handling units on base
		base = handlingUnitLocal.assign(new HandlingUnit("2"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("3"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("6"), base);
		
		// Now free base
		Set<HandlingUnit> freed = handlingUnitLocal.free(base);
		
		// Reread base is a must
		base = handlingUnitLocal.getById("1");
		
		// Base has no handling units any longer
		assertEquals(0, base.getContains().size());
		// The amount of handling units freed from base
		assertEquals(3, freed.size());
	}
}