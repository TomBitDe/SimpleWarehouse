package com.home.simplewarehouse.zone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
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

import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.model.EntityBase;
import com.home.simplewarehouse.model.Zone;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the Zone bean.
 */
@RunWith(Arquillian.class)
public class ZoneBeanTest {
	private static final Logger LOG = LogManager.getLogger(ZoneBeanTest.class);

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
						ZoneService.class, ZoneBean.class,
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
	ZoneService zoneService;
	
	/**
	 * Mandatory default constructor
	 */
	public ZoneBeanTest() {
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
	 * What to do after an individual test will be executed (each test)
	 */
	@After
	public void afterTest() {
		LOG.trace("--> afterTest()");

		// Cleanup zones
		Set<Zone> zones = zoneService.getAll();
		
		zones.stream().forEach(z -> zoneService.delete(z));
		
		LOG.trace("<-- afterTest()");
	}

	@Test
	@InSequence(0)
	public void create_getById() {
		LOG.info("--- Test create_getById");

		assertTrue(zoneService.getAll().isEmpty());
		
		Zone zone;

		try {
			zoneService.createOrUpdate(null);

			Assert.fail("Exception expected");
		}
		catch (EJBException ejbex) {
			LOG.info(ejbex.getCause().toString() + " : " + ejbex.getCause().getMessage());
		}
		
		Zone expZone = new Zone("Cooler");
		zone = zoneService.createOrUpdate(new Zone("Cooler"));
		
		assertNotNull(zone);
		assertEquals(expZone.getId(), zone.getId());
		assertEquals(Zone.RATING_DEFAULT, zone.getRating());
		assertEquals(EntityBase.USER_DEFAULT, zone.getUpdateUserId());
		assertNotNull(zone.getUpdateTimestamp());
		LOG.info(zone);
		
		expZone = new Zone("Freezer", 5);
		zone = zoneService.createOrUpdate(new Zone("Freezer", 5));
		
		assertNotNull(zone);
		assertEquals(expZone.getId(), zone.getId());
		assertEquals(expZone.getRating(), zone.getRating());		
		LOG.info(zone);
	}
	
	/**
	 * Test modify zone
	 */
	@Test
	@InSequence(5)
	public void modifyZone() {
		LOG.info("--- Test modifyZone");
		
		assumeTrue(zoneService.getAll().isEmpty());
		
		Zone cooler = zoneService.createOrUpdate(new Zone("Cooler"));
		
		assertNotNull(cooler);
		assertEquals(Zone.RATING_DEFAULT, cooler.getRating());
		LOG.info(cooler);

		// Zone modification
		cooler.setRating(5);
		
		cooler = zoneService.createOrUpdate(cooler);
		
		assertNotNull(cooler);
		assertEquals(5, cooler.getRating());
		LOG.info(cooler);
	}

	/**
	 * Test remove zone
	 */
	@Test
	@InSequence(10)
	public void removeZone() {
		LOG.info("--- Test removeZone");
		
		assumeTrue(zoneService.getAll().isEmpty());
		
		Zone cooler = zoneService.createOrUpdate(new Zone("Cooler"));
		
		assertNotNull(cooler);
		LOG.info(cooler);

		Zone freezer = zoneService.createOrUpdate(new Zone("Freezer"));
		
		assertNotNull(freezer);
		LOG.info(freezer);
		
		// Zone delete
		zoneService.delete("Cooler");
		cooler = zoneService.getById("Cooler");
		
		assertNull(cooler);
		
		zoneService.delete(freezer);
		freezer = zoneService.getById("Freezer");

		assertNull(freezer);
	}
}
