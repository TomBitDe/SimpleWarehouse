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
import com.home.simplewarehouse.handlingunit.HandlingUnitLocal;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.DimensionBean;
import com.home.simplewarehouse.location.DimensionLocal;
import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusLocal;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

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
						SampleWarehouseLocal.class, SampleWarehouseBean.class,
						DimensionLocal.class, DimensionBean.class,
						LocationStatusLocal.class, LocationStatusBean.class,
						LocationLocal.class, LocationBean.class,
						HandlingUnitLocal.class, HandlingUnitBean.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		LOG.trace("<-- createTestArchive()");
		
		return archive;
	}
	
	@EJB
	private SampleWarehouseLocal sampleWarehouseLocal;
	
	@EJB
	private LocationLocal locationLocal;
	
	@EJB
	private LocationStatusLocal locationStatusLocal;
	
	@EJB
	private DimensionLocal dimensionLocal;
	
	@EJB
	private HandlingUnitLocal handlingUnitLocal;

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
		
		sampleWarehouseLocal.initialize();
		
		assertNotNull(locationLocal.getAll());
		assertFalse(locationLocal.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.LOCATION_NUM, locationLocal.getAll().size());
		
		assertNotNull(locationStatusLocal.getAll());
		assertFalse(locationStatusLocal.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.LOCATION_NUM, locationStatusLocal.getAll().size());

		assertNotNull(dimensionLocal.getAll());
		assertFalse(dimensionLocal.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.LOCATION_NUM, dimensionLocal.getAll().size());

		locationLocal.getAll().forEach(l -> LOG.info(l));
		
		assertNotNull(handlingUnitLocal.getAll());
		assertFalse(handlingUnitLocal.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.HANDLING_UNIT_NUM, handlingUnitLocal.getAll().size());
		
		handlingUnitLocal.getAll().forEach(h -> LOG.info(h));
	}
	
	/**
	 * Test the cleanup
	 */
	@Test
	@InSequence(1)
	public void cleanupTest() {
		LOG.info("--- Test cleanupTest");

		sampleWarehouseLocal.cleanup();
		
		assertNotNull(locationLocal.getAll());
		assertTrue(locationLocal.getAll().isEmpty());
		
		assertNotNull(locationStatusLocal.getAll());
		assertTrue(locationStatusLocal.getAll().isEmpty());

		assertNotNull(dimensionLocal.getAll());
		assertTrue(dimensionLocal.getAll().isEmpty());

		assertNotNull(handlingUnitLocal.getAll());
		assertTrue(handlingUnitLocal.getAll().isEmpty());
	}

	/**
	 * Sequence testing for initialization
	 */
	@Test
	@InSequence(2)
	public void secondInitializeTest() {
		LOG.info("--- Test secondInitializeTest");
		
		sampleWarehouseLocal.initialize();
		
		assertNotNull(locationLocal.getAll());
		assertFalse(locationLocal.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.LOCATION_NUM, locationLocal.getAll().size());
		
		assertNotNull(locationStatusLocal.getAll());
		assertFalse(locationStatusLocal.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.LOCATION_NUM, locationStatusLocal.getAll().size());

		assertNotNull(dimensionLocal.getAll());
		assertFalse(dimensionLocal.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.LOCATION_NUM, dimensionLocal.getAll().size());

		locationLocal.getAll().forEach(l -> LOG.info(l));
		
		assertNotNull(handlingUnitLocal.getAll());
		assertFalse(handlingUnitLocal.getAll().isEmpty());
		assertEquals(SampleWarehouseBean.HANDLING_UNIT_NUM, handlingUnitLocal.getAll().size());
		
		handlingUnitLocal.getAll().forEach(h -> LOG.info(h));
	}
	
	/**
	 * Sequence testing for cleanup
	 */
	@Test
	@InSequence(3)
	public void secondCleanupTest() {
		LOG.info("--- Test secondCleanupTest");

		sampleWarehouseLocal.cleanup();
		
		assertNotNull(locationLocal.getAll());
		assertTrue(locationLocal.getAll().isEmpty());
		
		assertNotNull(locationStatusLocal.getAll());
		assertTrue(locationStatusLocal.getAll().isEmpty());

		assertNotNull(dimensionLocal.getAll());
		assertTrue(dimensionLocal.getAll().isEmpty());

		assertNotNull(handlingUnitLocal.getAll());
		assertTrue(handlingUnitLocal.getAll().isEmpty());
	}
}
