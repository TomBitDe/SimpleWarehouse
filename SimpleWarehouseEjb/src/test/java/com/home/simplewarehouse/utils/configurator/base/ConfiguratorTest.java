package com.home.simplewarehouse.utils.configurator.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Map;

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
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.patterns.singleton.simplecache.CacheDataProvider;
import com.home.simplewarehouse.patterns.singleton.simplecache.ValueSourceEntry;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the Configurator.
 */
@RunWith(Arquillian.class)
public class ConfiguratorTest {
	private static final Logger LOG = LogManager.getLogger(ConfiguratorTest.class);

	/**
	 * Configure the deployment.<br>
	 * Add all needed EJB interfaces and beans for the test.
	 * 
	 * @return the archive
	 */
	@Deployment
	public static Archive<?> createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				/* Put the test-ejb-jar.xml in JARs META-INF folder as ejb-jar.xml */
				.addAsManifestResource(new File("src/test/resources/META-INF/test-glassfish-ejb-jar.xml"), "glassfish-ejb-jar.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-ejb-jar.xml"), "ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(
						Configurator.class,
						CacheDataProvider.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		return archive;
	}

	@EJB
	Configurator configurator;
	
	/**
	 * Mandatory default constructor
	 */
	public ConfiguratorTest() {
		super();
		// DO NOTHING HERE!
	}

	/**
	 * Test getting the configuration
	 */
	@Test
	@InSequence(0)
	public void testGetConfigurationMap() {
		LOG.info("--> testGetConfiguration()");

		Map<String, ValueSourceEntry> configuration = configurator.getConfigurationMap();

		assertFalse(configuration.isEmpty());
		
		String str = configurator.getConfiguration();
		
		assertFalse(str.isEmpty());

		LOG.info("<-- testGetConfiguration()");
	}

	/**
	 * Test getting an entry
	 */
	@Test
	@InSequence(3)
	public void testGetEntry() {
		LOG.info("--> testGetEntry()");

		Map<String, ValueSourceEntry> configuration = configurator.getConfigurationMap();

		assertFalse(configuration.isEmpty());
		
		String str = configurator.getEntry(configuration.keySet().stream().findFirst().get());
		
		assertFalse(str.isEmpty());
		
		str = configurator.getEntry("notExists", "DefaultValue");
		
		assertEquals("DefaultValue", str);
		
		str = configurator.getEntry(null);
		
		assertNull(str);
		
		str = configurator.getEntry(null, "NullDefault");
		
		assertEquals("NullDefault", str);

		LOG.info("<-- testGetEntry()");
	}
	
	/**
	 * Test putting an entry
	 */
	@Test
	@InSequence(5)
	public void testPutEntry() {
		LOG.info("--> testPutEntry()");

		configurator.putEntry("NewKey1", "NewValue1");
		String str = configurator.getEntry("NewKey1");
		
		assertEquals("NewValue1", str);
		
		LOG.info("<-- testPutEntry()");
	}
	
	/**
	 * Test deleting an entry
	 */
	@Test
	@InSequence(7)
	public void testDeleteEntry() {
		LOG.info("--> testDeleteEntry()");

		configurator.putEntry("NewKey1", "NewValue1");
		String str = configurator.getEntry("NewKey1");
		
		assertEquals("NewValue1", str);
		
		configurator.deleteEntry("NewKey1");
		
		str = configurator.getEntry("NewKey1");
		
		assertNull(str);
		
		configurator.deleteEntry(null);
		
		LOG.info("<-- testDeleteEntry()");
	}
}
