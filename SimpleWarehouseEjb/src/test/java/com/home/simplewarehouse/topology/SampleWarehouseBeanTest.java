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
import com.home.simplewarehouse.location.LocationLocal;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusLocal;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the sample warehouse bean.
 */
@RunWith(Arquillian.class)
public class SampleWarehouseBeanTest {
	private static final Logger LOG = LogManager.getLogger(SampleWarehouseBeanTest.class);
	
	@EJB
	private SampleWarehouseLocal sampleWarehouseLocal;
	
	@EJB
	private LocationLocal locationLocal;
	
	@EJB
	private LocationStatusLocal locationStatusLocal;
	
	
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
	
	@Before
	public void beforeTest() {
		LOG.trace("--> beforeTest()");
		
		LOG.trace("<-- beforeTest()");		
	}
	
	@After
	public void afterTest() {
		LOG.trace("--> afterTest()");

		LOG.trace("<-- afterTest()");		
	}

	@Test
	@InSequence(0)
	public void initializeTest() {
		LOG.info("--- Test initializeTest");
		
		sampleWarehouseLocal.initialize();
		
		assertNotNull(locationLocal.getAll());
		assertFalse(locationLocal.getAll().isEmpty());
		assertEquals(26, locationLocal.getAll().size());
		
		assertNotNull(locationStatusLocal.getAll());
		assertFalse(locationStatusLocal.getAll().isEmpty());
		assertEquals(26, locationStatusLocal.getAll().size());

		locationLocal.getAll().forEach(l -> LOG.info(l));
	}
	
	@Test
	@InSequence(1)
	public void cleanupTest() {
		LOG.info("--- Test cleanupTest");

		sampleWarehouseLocal.cleanup();
		
		assertNotNull(locationLocal.getAll());
		assertTrue(locationLocal.getAll().isEmpty());
		
		assertNotNull(locationStatusLocal.getAll());
		assertTrue(locationStatusLocal.getAll().isEmpty());
	}
}
