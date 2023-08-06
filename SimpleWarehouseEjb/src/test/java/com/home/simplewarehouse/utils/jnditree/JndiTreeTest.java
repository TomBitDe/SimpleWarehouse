package com.home.simplewarehouse.utils.jnditree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.ejb.EJB;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the GoodMorning in TelemetryProvider.
 */
@RunWith(Arquillian.class)
public class JndiTreeTest {
	private static final Logger LOG = LogManager.getLogger(JndiTreeTest.class);

	/**
	 * Configure the deployment.<br>
	 * Add all needed EJB interfaces and beans for the test.
	 * 
	 * @return the archive
	 */
	@Deployment
	public static Archive<?> createTestArchive() {
		LOG.trace("--> createTestArchive()");
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				/* Put the test-*.xml in JARs META-INF folder as *.xml */
				.addAsManifestResource(new File("src/test/resources/META-INF/test-persistence.xml"), "persistence.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-ejb-jar.xml"), "ejb-jar.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-glassfish-ejb-jar.xml"), "glassfish-ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(
						JndiTree.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		LOG.trace("<-- createTestArchive()");
		return archive;
	}

	@EJB
	JndiTree jndiTree;
	
	/**
	 * Mandatory default constructor
	 */
	public JndiTreeTest() {
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

		LOG.trace("<-- afterTest()");
	}

	/**
	 * Call complete tree
	 */
	@Test
	@InSequence(0)
	public void testTreeAsText() {
		LOG.info("--> testTreeAsText()");

		String text = jndiTree.getJndiTreeAsText();

		assertFalse(text.isEmpty());
		
		assertTrue(text.contains("JndiTree"));
		assertTrue(text.contains("MonitoringResource"));

		LOG.info("<-- testTreeAsText()");
	}

	/**
	 * Call single entry in tree
	 */
	@Test
	@InSequence(1)
	public void testEntryAsText() {
		LOG.info("--> testEntryAsText()");

		String text = jndiTree.getEntryAsText("JndiTree");

		assertFalse(text.isEmpty());
		
		assertTrue(text.contains("/JndiTree"));
		
		text = jndiTree.getEntryAsText("Dummy");
		
		assertFalse(text.contains("/Dummy"));

		LOG.info("<-- testEntryAsText()");
	}
}
