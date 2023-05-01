package com.home.simplewarehouse.timed.scenarios;

import static org.junit.Assert.assertNotNull;

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
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.topology.SampleWarehouseBean;
import com.home.simplewarehouse.topology.SampleWarehouseLocal;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the pick drop random locationService bean.
 */
@RunWith(Arquillian.class)
public class PickDropRandomLocationTest {
	private static final Logger LOG = LogManager.getLogger(PickDropRandomLocationTest.class);
	
	@EJB
	SampleWarehouseLocal sampleWarehouseLocal;
	
	@EJB
	DropPickRandomLocationLocal1 dropPickRandomLocationLocal1;
	
	@EJB
	DropPickRandomLocationLocal2 dropPickRandomLocationLocal2;

	@EJB
	LocationService locationService;
	
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
						DropPickRandomLocationLocal1.class, DropPickRandomLocationBean1.class,
						DropPickRandomLocationLocal2.class, DropPickRandomLocationBean2.class,
						HandlingUnitService.class, HandlingUnitBean.class,
						LocationService.class, LocationBean.class,
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
	public PickDropRandomLocationTest() {
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
	 * What to do after an individual test has been executed (each test)
	 */
	@After
	public void afterTest() {
		LOG.trace("--> afterTest()");
		
		sampleWarehouseLocal.cleanup();

		LOG.trace("<-- afterTest()");		
	}

	/**
	 * Call a scenario (only this is tested)
	 */
	@Test
	@InSequence(0)
	public void processScenario1() {
		LOG.info("--- Test processScenario1");
		
		dropPickRandomLocationLocal1.processScenario();
		
		// Just satisfy the test
		assertNotNull(locationService.getAll());
	}

	/**
	 * Call a scenario (only this is tested)
	 */
	@Test
	@InSequence(0)
	public void processScenario2() {
		LOG.info("--- Test processScenario2");
		
		dropPickRandomLocationLocal2.processScenario();
		
		// Just satisfy the test
		assertNotNull(locationService.getAll());
	}
}
