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

import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
import com.home.simplewarehouse.handlingunit.HandlingUnitLocal;
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
     *      +-------------------- base hu1 ---------------+
     *
     *      +-- hu2 --+
     *      +-------------------- base hu1 ---------------+
     *
     *      +-- hu2 --+ +-- hu3 --+
     *      +-------------------- base hu1 ---------------+
     *
     *      +-- hu2 --+ +-- hu3 --+ +-- hu4 --+
     *      +-------------------- base hu1 ---------------+
     *
     *      +-- hu2 --+ +-- hu3 --+ +-- hu4 --+ +-- hu5 --+
     *      +-------------------- base hu1 ---------------+
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
			// All other handling units contain nothing
			assertTrue(item.getContains().isEmpty());
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
		
		handlingUnitLocal.logContains(base);
		handlingUnitLocal.logFlatContains(base);
		assertEquals(3, base.getContains().size());
		assertEquals(7, handlingUnitLocal.flatContains(base).size());

		handlingUnitLocal.logContains(handlingUnitLocal.getById("2"));
		handlingUnitLocal.logFlatContains(handlingUnitLocal.getById("2"));
		assertTrue(handlingUnitLocal.getById("2").getContains().isEmpty());
		assertTrue(handlingUnitLocal.flatContains(handlingUnitLocal.getById("2")).isEmpty());
		
		handlingUnitLocal.logContains(hu3);
		handlingUnitLocal.logFlatContains(hu3);
		assertEquals(2, hu3.getContains().size());

		HandlingUnit hu4 = handlingUnitLocal.getById("4");
		handlingUnitLocal.logContains(hu4);
		handlingUnitLocal.logFlatContains(hu4);
		assertTrue(hu4.getContains().isEmpty());
		
		HandlingUnit hu5 = handlingUnitLocal.getById("5");
		handlingUnitLocal.logContains(hu5);
		handlingUnitLocal.logFlatContains(hu5);
		assertTrue(hu5.getContains().isEmpty());
		
		assertEquals(2, handlingUnitLocal.flatContains(hu3).size());
		
		hu6 = handlingUnitLocal.getById("6");
		handlingUnitLocal.logContains(hu6);
		handlingUnitLocal.logFlatContains(hu6);
		assertEquals(1, hu6.getContains().size());
		assertTrue(hu6.getContains().contains(hu7));
		
		hu7 = handlingUnitLocal.getById("7");
		handlingUnitLocal.logContains(hu7);
		handlingUnitLocal.logFlatContains(hu7);
		assertEquals(1, hu7.getContains().size());
		assertTrue(hu7.getContains().contains(handlingUnitLocal.getById("8")));
		
		assertTrue(handlingUnitLocal.getById("8").getContains().isEmpty());
		
		assertEquals(2, handlingUnitLocal.flatContains(hu6).size());
	}
	
	/**
	 * Simple handling unit remove from stack
	 * <br>
     * <pre>{@code
     * 
     *                  +-- hu4 --+ +-- hu5 --+ +-- hu7 --+
     *      +-- hu2 --+ +------- hu3 ---------+ +-- hu6 --+
     *      +-------------------- base hu1 ---------------+
     *
     * remove(hu4, base)
     * remove(hu5, base)
     * 
     *                                          +-- hu7 --+
     *      +-- hu2 --+ +------- hu3 ---------+ +-- hu6 --+
     *      +-------------------- base hu1 ---------------+
     *
     * remove(hu6, base)
     * 
     *      +-- hu2 --+ +------- hu3 ---------+
     *      +-------------------- base hu1 ---------------+
     *
     * remove(hu2, base)
     * 
     *                  +------- hu3 ---------+
     *      +-------------------- base hu1 ---------------+
     *
     * }</pre>
	 */
	@Test
	@InSequence(12)
	public void removeFromStack() {
		LOG.info("--- Test removeFromStack");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());

		HandlingUnit base = handlingUnitLocal.assign(new HandlingUnit("2"), new HandlingUnit("1"));
		HandlingUnit hu3 = handlingUnitLocal.assign(new HandlingUnit("4"), new HandlingUnit("3"));
		hu3 = handlingUnitLocal.assign(new HandlingUnit("5"), hu3);
		base = handlingUnitLocal.assign(hu3, base);
		HandlingUnit hu6 = handlingUnitLocal.assign(new HandlingUnit("7"), new HandlingUnit("6"));
		base = handlingUnitLocal.assign(hu6, base);
		
		handlingUnitLocal.logContains(base);
		handlingUnitLocal.logFlatContains(base);

		hu3 = handlingUnitLocal.getById("3");
		Set<HandlingUnit> hu3Contains = hu3.getContains();
		for (HandlingUnit item : hu3Contains) {
			// Reread is mandatory
			hu3 = handlingUnitLocal.remove(handlingUnitLocal.getById(item.getId()), hu3);
		}	
		assertTrue(hu3.getContains().isEmpty());
		
		base = handlingUnitLocal.getById(base.getId());
		handlingUnitLocal.logContains(base);
		handlingUnitLocal.logFlatContains(base);		
		
		base = handlingUnitLocal.getById(base.getId());
		base = handlingUnitLocal.remove(handlingUnitLocal.getById("6"), base);
		
		handlingUnitLocal.logContains(base);
		handlingUnitLocal.logFlatContains(base);		
		assertEquals(2, base.getContains().size());
		
		base = handlingUnitLocal.getById(base.getId());
		base = handlingUnitLocal.remove(handlingUnitLocal.getById("2"), base);
		
		handlingUnitLocal.logContains(base);
		handlingUnitLocal.logFlatContains(base);		
		assertEquals(1, base.getContains().size());
		assertTrue(base.getContains().contains(handlingUnitLocal.getById("3")));
		assertEquals(base, handlingUnitLocal.getById("3").getBaseHU());
		assertNull(base.getBaseHU());
	}

	/**
	 * Move handling unit to other handling unit
	 * <br>
     * <pre>{@code
     * 
     *      +-- hu2 --+ +-- hu3 --+ +-- hu4 --+ +-- hu5 --+
     *      +-------------------- base hu1 ---------------+
     *
     *      +-- hu7 --+
     *      +------------------- other hu6 ---------------+
     *
     * move(hu4, other)
     * 
     *      +-- hu7 --+ +-- hu4 --+
     *      +------------------- other hu6 ---------------+
     *      
     *      +-- hu2 --+ +-- hu3 --+             +-- hu5 --+
     *      +-------------------- base hu1 ---------------+
     *
     * }</pre>
	 */
	@Test
	@InSequence(15)
	public void moveToOtherHandlingUnit() {
		LOG.info("--- Test moveToOtherHandlingUnit");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());

		HandlingUnit base = handlingUnitLocal.assign(new HandlingUnit("2"), new HandlingUnit("1"));
		base = handlingUnitLocal.assign(new HandlingUnit("3"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("4"), base);
		base = handlingUnitLocal.assign(new HandlingUnit("5"), base);
		
		HandlingUnit other = handlingUnitLocal.assign(new HandlingUnit("7"), new HandlingUnit("6"));

		HandlingUnit hu4 = handlingUnitLocal.getById("4");
		assertTrue(hu4.getContains().isEmpty());
		
		// Move handling unit to other
		hu4 = handlingUnitLocal.move(hu4, other);
		
		assertEquals(handlingUnitLocal.getById("4"), hu4);
		
		base = handlingUnitLocal.getById("1");
		assertFalse(base.getContains().isEmpty());
		assertFalse(base.getContains().contains(hu4));
		assertEquals(3, base.getContains().size());
		
		other = handlingUnitLocal.getById("6");
		assertFalse(other.getContains().isEmpty());
		assertTrue(other.getContains().contains(handlingUnitLocal.getById("7")));		
		assertTrue(other.getContains().contains(handlingUnitLocal.getById("4")));
		assertEquals(2, other.getContains().size());
		assertEquals(other, hu4.getBaseHU());
	}
}