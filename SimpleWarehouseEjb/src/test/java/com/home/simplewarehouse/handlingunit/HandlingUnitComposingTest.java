package com.home.simplewarehouse.handlingunit;

import static org.junit.Assert.assertEquals;
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
		assertEquals(null, base.getBase());
		assertTrue(base.getContains().isEmpty());
		LOG.info(base);
		
		HandlingUnit hu2 =  handlingUnitLocal.create(new HandlingUnit("2"));
		assertEquals(null, hu2.getBase());
		assertTrue(hu2.getContains().isEmpty());
		LOG.info(hu2);
		
		base = handlingUnitLocal.assign(hu2, base);
		
		assertEquals(1, base.getContains().size());
		assertTrue(base.getContains().contains(hu2));
		LOG.info(base);
		
		hu2 = handlingUnitLocal.getById("2");
		assertTrue(hu2.getContains().isEmpty());
		assertEquals(base, hu2.getBase());
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
		HandlingUnit hu2 =  handlingUnitLocal.create(new HandlingUnit("2"));
		HandlingUnit hu3 =  handlingUnitLocal.create(new HandlingUnit("3"));
		HandlingUnit hu4 =  handlingUnitLocal.create(new HandlingUnit("4"));
		HandlingUnit hu5 =  handlingUnitLocal.create(new HandlingUnit("5"));

		base = handlingUnitLocal.assign(hu2, base);
		base = handlingUnitLocal.assign(hu3, base);
		base = handlingUnitLocal.assign(hu4, base);
		base = handlingUnitLocal.assign(hu5, base);

		LOG.info(base);
		assertEquals(4, base.getContains().size());
	}
}