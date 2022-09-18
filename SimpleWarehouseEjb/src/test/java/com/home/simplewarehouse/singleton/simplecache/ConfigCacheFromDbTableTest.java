package com.home.simplewarehouse.singleton.simplecache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConfigCacheFromDbTableTest {
	private static final Logger LOG = LogManager.getLogger(ConfigCacheFromDbTableTest.class);

	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				/* Put the test-ejb-jar.xml in JARs META-INF folder as ejb-jar.xml */
				.addAsManifestResource(new File("src/test/resources/META-INF/test-persistence.xml"), "persistence.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-ejb-jar.xml"), "ejb-jar.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-glassfish-ejb-jar.xml"),
						"glassfish-ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(ConfigCache.class, ConfigCacheBean.class,
						CacheDataProvider.class, CacheDataFromDbTable.class,
						ApplConfigManager.class, ApplConfigManagerBean.class);

		LOG.debug(archive.toString(true));

		return archive;
	}

	@EJB
	ConfigCache configCache;

	@Test
	@InSequence(1)
	public void getDataNoDefaultTest()
	{
		LOG.debug("--> getDataNoDefaultTest");

		String val = configCache.getData("kahdadhajkh");
		assertNull(val);

		val = configCache.getData("Key1");
		assertEquals(null, val);

		val = configCache.getData("Key2");
		assertEquals(null, val);

		val = configCache.getData("Key3");
		assertEquals(null, val);

		val = configCache.getData("Key4");
		assertEquals(null, val);

		LOG.debug("<-- getDataTest");
	}

	@Test
	@InSequence(2)
	public void getDataWithDefaultTest()
	{
		LOG.debug("--> getDataWithDefaultTest");

		String val = configCache.getData("kahdadhajkh", "ABC");
		assertEquals("ABC", val);

		int numI = configCache.getData("Key5", 6);
		assertEquals(6, numI);

		numI = configCache.getData("Key99", 2);
		assertEquals(2, numI);

		long numL= configCache.getData("Key6", 77L);
		assertEquals(77L, numL);

		numL = configCache.getData("Key99", 11L);
		assertEquals(11L, numL);

		LOG.debug("<-- getDataWithDefaultTest");
	}
}
