package com.home.simplewarehouse.patterns.singleton.simplecache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.model.Dimension;
import com.home.simplewarehouse.patterns.singleton.simplecache.model.ApplConfig;

/**
 * Test the Application Configuration Manager. 
 */
@RunWith(Arquillian.class)
public class ApplConfigManagerTest {
	private static final Logger LOG = LogManager.getLogger(ApplConfigManagerTest.class);

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
	private ApplConfigManager applConfigManager;
	
	@EJB
	private ApplConfigService applConfigService;

	/**
	 * Mandatory default constructor
	 */
	public ApplConfigManagerTest() {
		super();
		// DO NOTHING HERE!
	}

	/**
	 * Test the getAll method
	 */
	@Test
	@InSequence(1)
	public void getAllTest()
	{
		LOG.debug("--> getAllTest");

		List<ApplConfig> configList = applConfigManager.getAll();
		assertTrue(configList.isEmpty());
		
		LOG.debug("<-- getAllTest");
	}

	/**
	 * Test the getContent method
	 */
	@Test
	@InSequence(3)
	public void getContentTest()
	{
		LOG.debug("--> getContentTest");

		List<ApplConfig> configList = applConfigService.getContent();
		assertTrue(configList.isEmpty());

		LOG.debug("<-- getContentTest");
	}

	/**
	 * Test the getContent range method
	 */
	@Test
	@InSequence(5)
	public void getContentRangeTest()
	{
		LOG.debug("--> getContentRangeTest");

		List<ApplConfig> configList = applConfigService.getContent(0, 5);
		assertTrue(configList.isEmpty());
		
		try {
			configList = applConfigService.getContent(-6, 0);
			
			Assert.fail("Exception expected!");
		}
		catch(EJBException ex) {
			assertTrue(ex.getMessage(), true);
		}
		
		try {
			configList = applConfigService.getContent(0, -4);
			
			Assert.fail("Exception expected!");
		}
		catch(EJBException ex) {
			assertTrue(ex.getMessage(), true);
		}

		LOG.debug("<-- getContentRangeTest");
	}
	
	/**
	 * Test create and getById
	 */
	@Test
	@InSequence(7)
	public void createAndGetByIdTest()
	{
		ApplConfig entry = applConfigService.create(new ApplConfig("DUMMY_F", "Value_1"));
		assertNotNull(entry);
		assertEquals("DUMMY_F", entry.getKeyVal());
		
		entry = applConfigService.getById("DUMMY_F");
		assertNotNull(entry);
		
		entry = applConfigService.getById("DUMMY_Z");
		assertNull(entry);
		
		// Test equals also
		entry = applConfigService.getById("DUMMY_F");
		assertFalse(entry.equals(new Dimension()));
	}
	
	/**
	 * Test update and delete
	 */
	@Test
	@InSequence(9)
	public void updateAndDeleteTest()
	{
		ApplConfig entry = applConfigService.getById("DUMMY_F");
		assertNotNull(entry);
		assertNotNull(entry.toString());
		assertTrue(entry.toString().startsWith("ApplConfig [keyVal="));
		assertNotEquals(0, entry.getVersion());
		assertNotEquals(null, entry);
		ApplConfig other = entry;
		assertEquals(entry, other);
		assertSame(entry, other);
		
		entry.setParamVal("Value_2");
		
		assertNotNull(entry = applConfigService.update(entry));
		
		assertEquals("Value_2", entry.getParamVal());

		entry.setKeyVal("DUMMY_C");
		
		assertNull(entry = applConfigService.update(entry));
		
		assertNotNull(entry = applConfigService.delete("DUMMY_F"));
		
		assertNull(entry = applConfigService.delete("DUMMY_F"));
	}
	
	/**
	 * Test count and refresh
	 */
	@Test
	@InSequence(11)
	public void countAndRefreshTest()
	{
		if (applConfigService.getById("DUMMY_F") == null) {
			applConfigService.create(new ApplConfig("DUMMY_F", "Value_1"));
		}
		assertNotNull(applConfigService.getById("DUMMY_F"));
		
		assertEquals(1, applConfigService.count());
		
		ApplConfig entry = applConfigService.create(new ApplConfig("DUMMY_W", "Value_5"));
		assertNotNull(entry);
		
		// Just call it to cover
		applConfigService.refresh();

		assertEquals(2, applConfigService.count());
	}
}
