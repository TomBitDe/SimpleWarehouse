package com.home.simplewarehouse.patterns.singleton.simplecache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Map;

import javax.ejb.EJB;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test the cache data loading coming from DB table. 
 */
@RunWith(Arquillian.class)
public class CacheDataFromDbTableTest {
	private static final Logger LOG = LogManager.getLogger(CacheDataFromDbTableTest.class);

	/**
	 * Configure the deployment.<br>
	 * Add all needed EJB interfaces and beans for the test.
	 * 
	 * @return the archive
	 */
	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				/* Put the test-ejb-jar.xml in JARs META-INF folder as ejb-jar.xml */
				.addAsManifestResource(new File("src/test/resources/META-INF/test-persistence.xml"), "persistence.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-ejb-jar.xml"), "ejb-jar.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-glassfish-ejb-jar.xml"),
						"glassfish-ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(ApplConfigManager.class, ApplConfigService.class, ApplConfigManagerBean.class
						,ConfigCache.class, ConfigCacheBean.class
						,CacheDataProvider.class
						,CacheDataFromProperties.class);

		LOG.debug(archive.toString(true));

		return archive;
	}

	@EJB
	CacheDataProvider cacheDataProvider;

	/**
	 * Mandatory default constructor
	 */
	public CacheDataFromDbTableTest() {
		super();
		// DO NOTHING HERE!
	}
	
	/**
	 * Test loading of cache data
	 */
	@Test
	@InSequence(3)
	public void loadCacheDataTest()
	{
		LOG.debug("--> loadCacheDataTest");

		Map<String, ValueSourceEntry> configMap = cacheDataProvider.loadCacheData();
		// Not known if db or properties is the source. So test only the following
		assertNotNull(configMap);

		LOG.debug("<-- loadCacheDataTest");
	}
	
	/**
	 * Test ValueSourceEntry
	 */
	@Test
	@InSequence(6)
	public void valueSourceEntryTest()
	{
		LOG.debug("--> valueSourceEntryTest");
		
		Map<String, ValueSourceEntry> configMap = cacheDataProvider.loadCacheData();
		assertNotNull(configMap);
		
		configMap.values().forEach(e -> LOG.debug("Entry: {}", e.toString()));
		
		ValueSourceEntry entry = new ValueSourceEntry("DUMMY_XYZ", "Test");
		ValueSourceEntry same = new ValueSourceEntry("DUMMY_XYZ", "Test");
		
		configMap.put("NEW", entry);
		
		ValueSourceEntry other = new ValueSourceEntry("A", "Test");
		
		other.setValue("DUMMY_VW");
		other.setSource(entry.getSource());
		
		assertEquals(entry, entry);
		assertNotEquals(other, entry);
		assertEquals(same, entry);
		assertNotEquals(entry, null);
		assertNotEquals(null, entry);
		boolean b = configMap.equals(other);
		assertFalse(b);
		assertNotEquals(0, other.hashCode());
		
		LOG.debug("<-- valueSourceEntryTest");
	}
}
