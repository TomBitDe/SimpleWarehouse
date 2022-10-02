package com.home.simplewarehouse.telemetryprovider;

import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;
import com.home.simplewarehouse.utils.telemetryprovider.requestcounter.GoodMorning;

/**
 * Test the GoodMorning in TelemetryProvider.
 */
@RunWith(Arquillian.class)
public class TelemetryProviderTest {
	private static final Logger LOG = LogManager.getLogger(TelemetryProviderTest.class);

	@EJB
	GoodMorning goodMorning;

	@Deployment
	public static Archive<?> createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				/* Put the test-ejb-jar.xml in JARs META-INF folder as ejb-jar.xml */
				.addAsManifestResource(new File("src/test/resources/META-INF/test-glassfish-ejb-jar.xml"), "glassfish-ejb-jar.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-ejb-jar.xml"), "ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(
						GoodMorning.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		return archive;
	}

	/**
	 * Just call method say of GoodMorning
	 */
	@Test
	public void testGoodMorningSay() {
		LOG.info("--> testGoodMorningSay()");

		goodMorning.say();

		assertTrue(true);

		LOG.info("<-- testGoodMorningSay()");
	}

	/**
	 * Call method tooEarly of GoodMorming to throw an exception
	 */
	@Test(expected = EJBException.class)
	public void testGoodMorningTooEarly() {
		LOG.info("--> testGoodMorningTooEarly()");

		goodMorning.tooEarly();

		LOG.info("<-- testGoodMorningTooEarly()");
	}
}
