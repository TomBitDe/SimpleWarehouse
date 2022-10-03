package com.home.simplewarehouse.handlingunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the Handling Unit bean.
 */
@RunWith(Arquillian.class)
public class HandlingUnitTest {
	private static final Logger LOG = LogManager.getLogger(HandlingUnitTest.class);

	@EJB
	HandlingUnitLocal handlingUnitLocal;
	
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
						HandlingUnitLocal.class, HandlingUnitBean.class,
						LocationLocal.class, LocationBean.class,
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
	 * Simple handling unit with no reference to a handlingUnit
	 */
	@Test
	@InSequence(0)
	public void createAndGetById() {
		LOG.info("--- Test createAndGetById");

		assertTrue(handlingUnitLocal.getAll().isEmpty());

		HandlingUnit expHandlingUnit = new HandlingUnit("1");

		handlingUnitLocal.create(expHandlingUnit);
		HandlingUnit handlingUnit = handlingUnitLocal.getById(expHandlingUnit.getId());
		assertEquals(expHandlingUnit, handlingUnit);
		
		LOG.info(handlingUnit);
	}

	@Test
	@InSequence(2)
	public void deleteByHandlingUnit() {
		LOG.info("--- Test deleteByHandlingUnit");

		assertTrue(handlingUnitLocal.getAll().isEmpty());

		handlingUnitLocal.create(new HandlingUnit("1"));

	    HandlingUnit handlingUnit = handlingUnitLocal.getById("1");
		assertEquals("1", handlingUnit.getId());
		
		// Delete returns the deleted handlingUnit
		handlingUnitLocal.delete(handlingUnit);
		assertNotNull(handlingUnit);
		assertEquals("1", handlingUnit.getId());
		LOG.info(handlingUnit);
	}
	
	@Test
	@InSequence(3)
	public void getAll() {
		LOG.info("--- Test getAll");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		
		// Prepare some handling units
		handlingUnitLocal.create(new HandlingUnit("1"));
		handlingUnitLocal.create(new HandlingUnit("2"));
		handlingUnitLocal.create(new HandlingUnit("3"));
		handlingUnitLocal.create(new HandlingUnit("4"));
		handlingUnitLocal.create(new HandlingUnit("5"));

		// Another test
		List<HandlingUnit> handlingUnits = handlingUnitLocal.getAll();

		assertFalse(handlingUnits.isEmpty());
		assertEquals(5, handlingUnits.size());
		
		handlingUnits.forEach(hU -> LOG.info(hU));
	}
	
	@Test
	@InSequence(4)
	public void dropTo() {
		LOG.info("--- Test dropTo");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
		
		// Prepare a handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("1"));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		Location lOA = locationLocal.getById("A");
		
		// Now drop
		handlingUnitLocal.dropTo(lOA, hU1);
		
		LOG.info(hU1);
		
		// Handling Unit is on location now
		assertTrue(hU1.getLocation().equals(lOA));
		
		assertFalse(lOA.getHandlingUnits().isEmpty());
		assertEquals(1, lOA.getHandlingUnits().size());
		
		// Location must contain handling unit now
		assertTrue(lOA.getHandlingUnits().contains(hU1));
		
		LOG.info(lOA);
	}
	
	@Test(expected = EJBException.class)
	@InSequence(5)
	public void dropToNull() {
		LOG.info("--- Test dropToNull");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
		
		// Prepare a handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("1"));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		Location lOA = null;
		
		// Now drop
		handlingUnitLocal.dropTo(lOA, hU1);
	}
	
	@Test
	@InSequence(6)
	public void pickFrom() throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.info("--- Test pickFrom");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
		
		// Prepare a handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("1"));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		Location lOA = locationLocal.getById("A");
		
		// To prepare the pick do a drop before
		handlingUnitLocal.dropTo(lOA, hU1);
		
		// Handling Unit is on location now
		assertTrue(hU1.getLocation().equals(lOA));
		
		assertFalse(lOA.getHandlingUnits().isEmpty());
		assertEquals(1, lOA.getHandlingUnits().size());
		
		// Location must contain handling unit now
		assertTrue(lOA.getHandlingUnits().contains(hU1));
		
		LOG.info(hU1);
		LOG.info(lOA);

		// Now do the pick
		handlingUnitLocal.pickFrom(lOA, hU1);
		
		assertNull(hU1.getLocation());
		assertFalse(lOA.getHandlingUnits().contains(hU1));
		LOG.info(hU1);
		LOG.info(lOA);
	}
	
	@Test(expected = LocationIsEmptyException.class)
	@InSequence(7)
	public void pickFromEmptyLocation() throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.info("--- Test pickFromEmptyLocation");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
				
		// Prepare a handling unit
		handlingUnitLocal.create(new HandlingUnit("1"));
		
		// Pick now
		HandlingUnit handlingUnit = handlingUnitLocal.getById("1");
		
		// Location is EMPTY because just newly created
		handlingUnitLocal.pickFrom(new Location("A"), handlingUnit);
	}

	@Test(expected = HandlingUnitNotOnLocationException.class)
	@InSequence(8)
	public void pickFromLocationNotContaining() throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		LOG.info("--- Test pickFromLocationNotContaining");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());
				
		// Prepare handling units and a location
		handlingUnitLocal.create(new HandlingUnit("1"));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		Location lOA = locationLocal.getById("A");
		
		handlingUnitLocal.dropTo(lOA, hU1);
		
		handlingUnitLocal.create(new HandlingUnit("2"));

		// Pick now
		HandlingUnit hU2 = handlingUnitLocal.getById("2");
		
		LOG.info(hU2);
		
		// Location contains hU1 but not hU2
		handlingUnitLocal.pickFrom(lOA, hU2);
	}
	
	@Test
	@InSequence(9)
	public void deleteHandlingUnitOnLocation() {
		LOG.info("---Test deleteHandlingUnitOnLocation");
		
		assertTrue(handlingUnitLocal.getAll().isEmpty());
		assertTrue(locationLocal.getAll().isEmpty());

		// Prepare handling unit and a location
		handlingUnitLocal.create(new HandlingUnit("1"));
		handlingUnitLocal.create(new HandlingUnit("2"));
		
		locationLocal.create(new Location("A"));
		
		HandlingUnit hU1 = handlingUnitLocal.getById("1");
		HandlingUnit hU2 = handlingUnitLocal.getById("2");
		Location lOA = locationLocal.getById("A");
		
		// Drop to make a relation
		handlingUnitLocal.dropTo(lOA, hU1);
		handlingUnitLocal.dropTo(lOA, hU2);
		
		// Now delete a handling unit that is related to a location
		handlingUnitLocal.delete(hU1);
		
		LOG.info("Delete: " + hU1);
		
		lOA = locationLocal.getById("A");
		
		// Check the location
		assertNotNull(lOA);
		assertFalse(lOA.getHandlingUnits().isEmpty());
		assertTrue(lOA.getHandlingUnits().contains(hU2));
		assertFalse(lOA.getHandlingUnits().contains(hU1));
		
		LOG.info(lOA);
	}
}
