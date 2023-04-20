package com.home.simplewarehouse.usecase;

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

import com.home.simplewarehouse.handlingunit.HandlingUnitLocal;
import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
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
public class HandlingUnitComposingUseCasesTest {
	private static final Logger LOG = LogManager.getLogger(HandlingUnitComposingUseCasesTest.class);

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
	public HandlingUnitComposingUseCasesTest() {
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
	 * Simple handling unit assign four on base<br>
	 * <br>
     * <pre>{@code
     * 
     *      +-------------------- base -------------------+
     *
     *      +-- hu2 --+
     *      +-------------------- base -------------------+
     *
     *      +-- hu2 --+ +-- hu3 --+
     *      +-------------------- base -------------------+
     *
     *      +-- hu2 --+ +-- hu3 --+ +-- hu4 --+
     *      +-------------------- base -------------------+
     *
     *      +-- hu2 --+ +-- hu3 --+ +-- hu4 --+ +-- hu5 --+
     *      +-------------------- base -------------------+
     *
     * }</pre>
	 */
	@Test
	@InSequence(3)
	public void assignFourOnBase() {
		LOG.info("--- Test assignFourOnBase");

		assertTrue(handlingUnitLocal.getAll().isEmpty());

		// Create the base handling unit
		HandlingUnit base = handlingUnitLocal.create(new HandlingUnit("1"));

		// Now place some handling units on base
		base = handlingUnitLocal.assign(new HandlingUnit("2"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("3"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("4"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("5"), base);

		LOG.info(base);
		// The base now contains other handling units (see above)
		assertEquals(4, base.getContains().size());
		// The base has no base
		assertNull(base.getBaseHU());
		
		// Check if base contains the following handling units
		assertTrue(base.getContains().contains(handlingUnitLocal.getById("2")));
		assertTrue(base.getContains().contains(handlingUnitLocal.getById("3")));
		assertTrue(base.getContains().contains(handlingUnitLocal.getById("4")));
		assertTrue(base.getContains().contains(handlingUnitLocal.getById("5")));
		
		for (HandlingUnit item : base.getContains()) {
			// All other handling units are on base
			assertEquals(base, item.getBaseHU());
		}
	}
	
	/**
	 * Simple handling unit assign stacks of handlingUnits
	 * <br>
     * <pre>{@code
     * 
     *      +-- hu2 --+
     *      +-------------------- base hu1 ---------------+
     *
     *      +-- hu4 --+
     *      +------- hu3 ---------+
     *
     *      +-- hu4 --+ +-- hu5 --+
     *      +------- hu3 ---------+
     *
     *                  +-- hu4 --+ +-- hu5 --+
     *      +-- hu2 --+ +------- hu3 ---------+
     *      +-------------------- base hu1 ---------------+
     *
     *      +-- hu7 --+
     *      +-- hu6 --+
     *
     *      +-- hu8 --+
     *      +-- hu7 --+
     *      +-- hu6 --+
     *
     *                                          +-- hu8 --+
     *                  +-- hu4 --+ +-- hu5 --+ +-- hu7 --+
     *      +-- hu2 --+ +------- hu3 ---------+ +-- hu6 --+
     *      +-------------------- base hu1 ---------------+
     *
     * }</pre>
	 */
	@Test
	@InSequence(6)
	public void assignStacksOnBase() {
		LOG.info("--- Test assignStacksOnBase");

		assertTrue(handlingUnitLocal.getAll().isEmpty());

		HandlingUnit base = handlingUnitLocal.assign(new HandlingUnit("2"), new HandlingUnit("1"));
		HandlingUnit hu3 = handlingUnitLocal.assign(new HandlingUnit("4"), new HandlingUnit("3"));
		hu3 = handlingUnitLocal.assign(new HandlingUnit("5"), hu3);
		base = handlingUnitLocal.assign(hu3, base);
		HandlingUnit hu6 = handlingUnitLocal.assign(new HandlingUnit("7"), new HandlingUnit("6"));
		HandlingUnit hu7 = handlingUnitLocal.getById("7");
		hu7 = handlingUnitLocal.assign(new HandlingUnit("8"), hu7);
		base = handlingUnitLocal.assign(hu6, base);
		
		LOG.info(base);
		assertEquals(7, base.getContains().size());
		LOG.info(hu3);
		assertEquals(2, hu3.getContains().size());
		LOG.info(hu6);
		assertEquals(2, hu6.getContains().size());
		
		LOG.info(handlingUnitLocal.getById("2"));
		assertEquals(0, handlingUnitLocal.getById("2").getContains().size());
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

		base = handlingUnitLocal.assign(new HandlingUnit("2"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("3"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("4"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("5"), base);
		
		Set<HandlingUnit> baseContains = base.getContains();
		for (HandlingUnit item : baseContains) {
			// Reread is mandatory
			item = handlingUnitLocal.getById(item.getId());
			base = handlingUnitLocal.remove(item, base);
		}
		
		assertTrue(base.getContains().isEmpty());
	}
	
	/**
	 * Simple handling unit remove from stack
	 */
	@Test
	@InSequence(12)
	public void removeFromStack() {
		LOG.info("--- Test removeFromStack");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());

		HandlingUnit base = handlingUnitLocal.create(new HandlingUnit("1"));
		HandlingUnit hu3 =  handlingUnitLocal.create(new HandlingUnit("3"));
		HandlingUnit hu6 =  handlingUnitLocal.create(new HandlingUnit("6"));

		base = handlingUnitLocal.assign(new HandlingUnit("2"), base);
		hu3 = handlingUnitLocal.assign(new HandlingUnit("4"), hu3);
		hu3 = handlingUnitLocal.assign(new HandlingUnit("5"), hu3);
		base = handlingUnitLocal.assign(hu3, base);
		hu6 = handlingUnitLocal.assign(new HandlingUnit("7"), hu6);
		base = handlingUnitLocal.assign(hu6, base);
		
		hu3 = handlingUnitLocal.getById("3");
		Set<HandlingUnit> hu3Contains = hu3.getContains();
		for (HandlingUnit item : hu3Contains) {
			// Reread is mandatory
			item = handlingUnitLocal.getById(item.getId());
			hu3 = handlingUnitLocal.remove(item, hu3);
		}	
		assertTrue(hu3.getContains().isEmpty());
		
		base = handlingUnitLocal.getById(base.getId());
		hu6 = handlingUnitLocal.getById("6");
		base = handlingUnitLocal.remove(hu6, base);
		assertEquals(1, base.getContains().size());
		
		base = handlingUnitLocal.getById(base.getId());
		HandlingUnit hu2 = handlingUnitLocal.getById("2");
		base = handlingUnitLocal.remove(hu2, base);
		assertEquals(0, base.getContains().size());
	}

	/**
	 * Move handling unit remove to other handling unit
	 */
	@Test
	@InSequence(15)
	public void moveToOtherHandlingUnit() {
		LOG.info("--- Test moveToOtherHandlingUnit");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());

		HandlingUnit base = handlingUnitLocal.create(new HandlingUnit("1"));

		base = handlingUnitLocal.assign(new HandlingUnit("2"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("3"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("4"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("5"), base);
		
		HandlingUnit other = handlingUnitLocal.create(new HandlingUnit("6"));
		
		other = handlingUnitLocal.assign(new HandlingUnit("7"), other);

		HandlingUnit hu4 = handlingUnitLocal.getById("4");
		assertTrue(hu4.getContains().isEmpty());
		
		hu4 = handlingUnitLocal.move(hu4, other);
		
		assertEquals(handlingUnitLocal.getById("4"), hu4);
		
		base = handlingUnitLocal.getById("1");
		assertFalse(base.getContains().isEmpty());
		assertFalse(base.getContains().contains(hu4));
		
		other = handlingUnitLocal.getById("6");
		assertFalse(other.getContains().isEmpty());
		
		assertTrue(other.getContains().contains(handlingUnitLocal.getById("7")));
		
		assertTrue(other.getContains().contains(hu4));
		
		assertEquals(other, hu4.getBaseHU());
	}

	/**
	 * Simple handling unit empty from stack
	 */
	@Test
	@InSequence(18)
	public void emptyFromStack() {
		LOG.info("--- Test emptyFromStack");

		HandlingUnit base = handlingUnitLocal.create(new HandlingUnit("1"));
		HandlingUnit hu3 =  handlingUnitLocal.create(new HandlingUnit("3"));
		HandlingUnit hu6 =  handlingUnitLocal.create(new HandlingUnit("6"));

		base = handlingUnitLocal.assign(new HandlingUnit("2"), base);
		hu3 = handlingUnitLocal.assign(new HandlingUnit("4"), hu3);
		hu3 = handlingUnitLocal.assign(new HandlingUnit("5"), hu3);
		base = handlingUnitLocal.assign(hu3, base);
		hu6 = handlingUnitLocal.assign(new HandlingUnit("7"), hu6);
		base = handlingUnitLocal.assign(hu6, base);
		
		Set<HandlingUnit> freed = handlingUnitLocal.free(base);
		// Reread base is a must
		base = handlingUnitLocal.getById("1");
		assertEquals(0, base.getContains().size());
		assertEquals(3, freed.size());
	}
}