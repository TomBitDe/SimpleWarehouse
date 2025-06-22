package com.home.simplewarehouse.topology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

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
import com.home.simplewarehouse.location.DimensionBean;
import com.home.simplewarehouse.location.DimensionService;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusService;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;
import com.home.simplewarehouse.zone.ZoneBean;
import com.home.simplewarehouse.zone.ZoneService;

/**
 * Test the Sample Warehouse bean.
 */
@RunWith(Arquillian.class)
public class SampleWarehouseBeanTest {
	private static final Logger LOG = LogManager.getLogger(SampleWarehouseBeanTest.class);	
	
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
						SampleWarehouseService.class, SampleWarehouseBean.class,
						DimensionService.class, DimensionBean.class,
						LocationStatusService.class, LocationStatusBean.class,
						LocationService.class, LocationBean.class,
						ZoneService.class, ZoneBean.class,
						HandlingUnitService.class, HandlingUnitBean.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		LOG.trace("<-- createTestArchive()");
		
		return archive;
	}
	
	@EJB
	private SampleWarehouseService sampleWarehouseService;
	
	@EJB
	private LocationService locationService;
	
	@EJB
	private LocationStatusService locationStatusService;
	
	@EJB
	private DimensionService dimensionService;
	
	@EJB
	private HandlingUnitService handlingUnitService;
	
	@EJB
	private ZoneService zoneService;

	/**
	 * Mandatory default constructor
	 */
	public SampleWarehouseBeanTest() {
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

		LOG.trace("<-- afterTest()");		
	}

	/**
	 * Test the initialization
	 */
	@Test
	@InSequence(0)
	public void initializeTest() {
		LOG.info("--- Test initializeTest");
		
		sampleWarehouseService.initialize();
		
		assertNotNull(locationService.getAll());
		assertFalse(locationService.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.LOCATION_NUM * 3 , locationService.getAll().size());
		
		assertNotNull(locationStatusService.getAll());
		assertFalse(locationStatusService.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.LOCATION_NUM * 3, locationStatusService.getAll().size());

		assertNotNull(dimensionService.getAll());
		assertFalse(dimensionService.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.LOCATION_NUM * 3, dimensionService.getAll().size());

		locationService.getAll().forEach(l -> LOG.info(l));
		
		assertNotNull(handlingUnitService.getAll());
		assertFalse(handlingUnitService.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.HANDLING_UNIT_NUM, handlingUnitService.getAll().size());
		
		handlingUnitService.getAll().forEach(h -> LOG.info(h));
		
		assertNotNull(zoneService.getAll());
		assertFalse(zoneService.getAll().isEmpty());
		assertTrue(4 <= zoneService.count());

		zoneService.getAll().forEach(z -> LOG.info(z));
}
	
	/**
	 * Test the cleanup
	 */
	@Test
	@InSequence(1)
	public void cleanupTest() {
		LOG.info("--- Test cleanupTest");

		sampleWarehouseService.cleanup();
		
		assertNotNull(locationService.getAll());
		assertTrue(locationService.getAll().isEmpty());
		
		assertNotNull(locationStatusService.getAll());
		assertTrue(locationStatusService.getAll().isEmpty());

		assertNotNull(dimensionService.getAll());
		assertTrue(dimensionService.getAll().isEmpty());

		assertNotNull(handlingUnitService.getAll());
		assertTrue(handlingUnitService.getAll().isEmpty());

		assertNotNull(zoneService.getAll());
		assertTrue(zoneService.getAll().isEmpty());
	}

	/**
	 * Sequence testing for initialization
	 */
	@Test
	@InSequence(2)
	public void secondInitializeTest() {
		LOG.info("--- Test secondInitializeTest");
		
		sampleWarehouseService.initialize();
		
		assertNotNull(locationService.getAll());
		assertFalse(locationService.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.LOCATION_NUM * 3, locationService.getAll().size());
		
		assertNotNull(locationStatusService.getAll());
		assertFalse(locationStatusService.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.LOCATION_NUM * 3, locationStatusService.getAll().size());

		assertNotNull(dimensionService.getAll());
		assertFalse(dimensionService.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.LOCATION_NUM * 3, dimensionService.getAll().size());

		locationService.getAll().forEach(l -> LOG.info(l));
		
		assertNotNull(handlingUnitService.getAll());
		assertFalse(handlingUnitService.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.HANDLING_UNIT_NUM, handlingUnitService.getAll().size());
		
		handlingUnitService.getAll().forEach(h -> LOG.info(h));

		assertNotNull(zoneService.getAll());
		assertFalse(zoneService.getAll().isEmpty());
		assertTrue(4 <= zoneService.count());

		zoneService.getAll().forEach(z -> LOG.info(z));
	}
	
	/**
	 * Sequence testing for cleanup
	 */
	@Test
	@InSequence(3)
	public void secondCleanupTest() {
		LOG.info("--- Test secondCleanupTest");

		sampleWarehouseService.cleanup();
		
		assertNotNull(locationService.getAll());
		assertTrue(locationService.getAll().isEmpty());
		
		assertNotNull(locationStatusService.getAll());
		assertTrue(locationStatusService.getAll().isEmpty());

		assertNotNull(dimensionService.getAll());
		assertTrue(dimensionService.getAll().isEmpty());

		assertNotNull(handlingUnitService.getAll());
		assertTrue(handlingUnitService.getAll().isEmpty());

		assertNotNull(zoneService.getAll());
		assertTrue(zoneService.getAll().isEmpty());
	}
}
