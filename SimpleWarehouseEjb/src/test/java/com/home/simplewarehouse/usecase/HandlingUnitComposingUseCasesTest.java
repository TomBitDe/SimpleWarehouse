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
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;
import com.home.simplewarehouse.zone.ZoneBean;
import com.home.simplewarehouse.zone.ZoneService;

/**
 * Test the Handling Unit bean for composing HandlingUnits.
 */
@RunWith(Arquillian.class)
public class HandlingUnitComposingUseCasesTest {
	private static final Logger LOG = LogManager.getLogger(HandlingUnitComposingUseCasesTest.class);

	@EJB
	HandlingUnitService handlingUnitService;
		
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

		// Cleanup handling units
		List<HandlingUnit> handlingUnits = handlingUnitService.getAll();
		
		handlingUnits.stream().forEach(h -> handlingUnitService.delete(h));		

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

		assertTrue(handlingUnitService.getAll().isEmpty());

		// Create the base handling unit
		HandlingUnit base = handlingUnitService.createOrUpdate(new HandlingUnit("1"));

		// Now place some handling units on base
		base = handlingUnitService.assign(new HandlingUnit("2"), base);
		base = handlingUnitService.assign(new HandlingUnit("3"), base);
		base = handlingUnitService.assign(new HandlingUnit("4"), base);
		base = handlingUnitService.assign(new HandlingUnit("5"), base);

		LOG.info(base);
		// The base now contains other handling units (see above)
		assertEquals(4, base.getContains().size());
		// The base has no base
		assertNull(base.getBaseHU());
		
		// Check if base contains the following handling units
		assertTrue(base.getContains().contains(handlingUnitService.getById("2")));
		assertTrue(base.getContains().contains(handlingUnitService.getById("3")));
		assertTrue(base.getContains().contains(handlingUnitService.getById("4")));
		assertTrue(base.getContains().contains(handlingUnitService.getById("5")));
		
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

		assertTrue(handlingUnitService.getAll().isEmpty());

		HandlingUnit base = handlingUnitService.assign(new HandlingUnit("2"), new HandlingUnit("1"));
		HandlingUnit hu3 = handlingUnitService.assign(new HandlingUnit("4"), new HandlingUnit("3"));
		hu3 = handlingUnitService.assign(new HandlingUnit("5"), hu3);
		base = handlingUnitService.assign(hu3, base);
		HandlingUnit hu6 = handlingUnitService.assign(new HandlingUnit("7"), new HandlingUnit("6"));
		HandlingUnit hu7 = handlingUnitService.getById("7");
		hu7 = handlingUnitService.assign(new HandlingUnit("8"), hu7);
		base = handlingUnitService.assign(hu6, base);
		
		handlingUnitService.logContains(base);
		handlingUnitService.logFlatContains(base);
		assertEquals(3, base.getContains().size());
		assertEquals(7, handlingUnitService.flatContains(base).size());

		handlingUnitService.logContains(handlingUnitService.getById("2"));
		handlingUnitService.logFlatContains(handlingUnitService.getById("2"));
		assertTrue(handlingUnitService.getById("2").getContains().isEmpty());
		assertTrue(handlingUnitService.flatContains(handlingUnitService.getById("2")).isEmpty());
		
		handlingUnitService.logContains(hu3);
		handlingUnitService.logFlatContains(hu3);
		assertEquals(2, hu3.getContains().size());

		HandlingUnit hu4 = handlingUnitService.getById("4");
		handlingUnitService.logContains(hu4);
		handlingUnitService.logFlatContains(hu4);
		assertTrue(hu4.getContains().isEmpty());
		
		HandlingUnit hu5 = handlingUnitService.getById("5");
		handlingUnitService.logContains(hu5);
		handlingUnitService.logFlatContains(hu5);
		assertTrue(hu5.getContains().isEmpty());
		
		assertEquals(2, handlingUnitService.flatContains(hu3).size());
		
		hu6 = handlingUnitService.getById("6");
		handlingUnitService.logContains(hu6);
		handlingUnitService.logFlatContains(hu6);
		assertEquals(1, hu6.getContains().size());
		assertTrue(hu6.getContains().contains(hu7));
		
		hu7 = handlingUnitService.getById("7");
		handlingUnitService.logContains(hu7);
		handlingUnitService.logFlatContains(hu7);
		assertEquals(1, hu7.getContains().size());
		assertTrue(hu7.getContains().contains(handlingUnitService.getById("8")));
		
		assertTrue(handlingUnitService.getById("8").getContains().isEmpty());
		
		assertEquals(2, handlingUnitService.flatContains(hu6).size());
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
		
		assertTrue(handlingUnitService.getAll().isEmpty());

		HandlingUnit base = handlingUnitService.assign(new HandlingUnit("2"), new HandlingUnit("1"));
		HandlingUnit hu3 = handlingUnitService.assign(new HandlingUnit("4"), new HandlingUnit("3"));
		hu3 = handlingUnitService.assign(new HandlingUnit("5"), hu3);
		base = handlingUnitService.assign(hu3, base);
		HandlingUnit hu6 = handlingUnitService.assign(new HandlingUnit("7"), new HandlingUnit("6"));
		base = handlingUnitService.assign(hu6, base);
		
		handlingUnitService.logContains(base);
		handlingUnitService.logFlatContains(base);

		hu3 = handlingUnitService.getById("3");
		Set<HandlingUnit> hu3Contains = hu3.getContains();
		for (HandlingUnit item : hu3Contains) {
			// Reread is mandatory
			hu3 = handlingUnitService.remove(handlingUnitService.getById(item.getId()), hu3);
		}	
		assertTrue(hu3.getContains().isEmpty());
		
		base = handlingUnitService.getById(base.getId());
		handlingUnitService.logContains(base);
		handlingUnitService.logFlatContains(base);		
		
		base = handlingUnitService.getById(base.getId());
		base = handlingUnitService.remove(handlingUnitService.getById("6"), base);
		
		handlingUnitService.logContains(base);
		handlingUnitService.logFlatContains(base);		
		assertEquals(2, base.getContains().size());
		
		base = handlingUnitService.getById(base.getId());
		base = handlingUnitService.remove(handlingUnitService.getById("2"), base);
		
		handlingUnitService.logContains(base);
		handlingUnitService.logFlatContains(base);		
		assertEquals(1, base.getContains().size());
		assertTrue(base.getContains().contains(handlingUnitService.getById("3")));
		assertEquals(base, handlingUnitService.getById("3").getBaseHU());
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
		
		assertTrue(handlingUnitService.getAll().isEmpty());

		HandlingUnit base = handlingUnitService.assign(new HandlingUnit("2"), new HandlingUnit("1"));
		base = handlingUnitService.assign(new HandlingUnit("3"), base);
		base = handlingUnitService.assign(new HandlingUnit("4"), base);
		base = handlingUnitService.assign(new HandlingUnit("5"), base);
		
		HandlingUnit other = handlingUnitService.assign(new HandlingUnit("7"), new HandlingUnit("6"));

		HandlingUnit hu4 = handlingUnitService.getById("4");
		assertTrue(hu4.getContains().isEmpty());
		
		// Move handling unit to other
		hu4 = handlingUnitService.move(hu4, other);
		
		assertEquals(handlingUnitService.getById("4"), hu4);
		
		base = handlingUnitService.getById("1");
		assertFalse(base.getContains().isEmpty());
		assertFalse(base.getContains().contains(hu4));
		assertEquals(3, base.getContains().size());
		
		other = handlingUnitService.getById("6");
		assertFalse(other.getContains().isEmpty());
		assertTrue(other.getContains().contains(handlingUnitService.getById("7")));		
		assertTrue(other.getContains().contains(handlingUnitService.getById("4")));
		assertEquals(2, other.getContains().size());
		assertEquals(other, hu4.getBaseHU());
	}
}