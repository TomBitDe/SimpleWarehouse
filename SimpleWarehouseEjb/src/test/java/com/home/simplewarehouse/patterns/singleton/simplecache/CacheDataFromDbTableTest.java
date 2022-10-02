package com.home.simplewarehouse.patterns.singleton.simplecache;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Map;

import javax.ejb.EJB;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CacheDataFromDbTableTest {
	private static final Logger LOG = LogManager.getLogger(CacheDataFromDbTableTest.class);

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

	@Test
	public void loadCacheDataTest()
	{
		LOG.debug("--> loadCacheDataTest");

		Map<String, String> configMap = cacheDataProvider.loadCacheData();
		// Not known if db or properties is the source. So test only the following
		assertNotNull(configMap);

		LOG.debug("<-- loadCacheDataTest");
	}
}
