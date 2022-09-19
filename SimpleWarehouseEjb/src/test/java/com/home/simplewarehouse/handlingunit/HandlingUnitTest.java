package com.home.simplewarehouse.handlingunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

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

import com.home.simplewarehouse.handlingunit.model.HandlingUnit;
import com.home.simplewarehouse.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * Test the handling unit bean.
 */
@RunWith(Arquillian.class)
public class HandlingUnitTest {
	private static final Logger LOG = LogManager.getLogger(HandlingUnitTest.class);

	@EJB
	HandlingUnitLocal handlingUnitLocal;
	
	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				/* Put the test-*.xml in JARs META-INF folder as *.xml */
				.addAsManifestResource(new File("src/test/resources/META-INF/test-persistence.xml"), "persistence.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-ejb-jar.xml"), "ejb-jar.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-glassfish-ejb-jar.xml"), "glassfish-ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(
						HandlingUnitLocal.class, HandlingUnitBean.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		return archive;
	}

	/**
	 * Simple handling unit with no reference to a handlingUnit
	 */
	@Test
	@InSequence(0)
	public void createAndGetById() {
		LOG.info("Test createAndGetById");

		HandlingUnit expHandlingUnit = new HandlingUnit("1");

		handlingUnitLocal.create(expHandlingUnit);
		HandlingUnit handlingUnit = handlingUnitLocal.getById(expHandlingUnit.getId());
		assertEquals(expHandlingUnit, handlingUnit);
	}
	@Test
	@InSequence(1)
	public void deleteById() {
		LOG.info("Test deleteById");

		if (handlingUnitLocal.getById("1") == null) { 
		    handlingUnitLocal.create(new HandlingUnit("1"));
		}
		
		HandlingUnit handlingUnit = handlingUnitLocal.getById("1");
		assertEquals("1", handlingUnit.getId());
		
		// Delete returns the deleted handlingUnit
		handlingUnit = handlingUnitLocal.delete(handlingUnit.getId());
		assertNotNull(handlingUnit);
		assertEquals("1", handlingUnit.getId());
		
		// Delete returns null because the handlingUnit does not exist
		assertNull(handlingUnitLocal.delete(handlingUnit.getId()));
	}

	@Test
	@InSequence(2)
	public void deleteByHandlingUnit() {
		LOG.info("Test deleteByHandlingUnit");

		if (handlingUnitLocal.getById("1") == null) { 
		    handlingUnitLocal.create(new HandlingUnit("1"));
		}
		
		HandlingUnit handlingUnit = handlingUnitLocal.getById("1");
		assertEquals("1", handlingUnit.getId());
		
		// Delete returns the deleted handlingUnit
		handlingUnit = handlingUnitLocal.delete(handlingUnit);
		assertNotNull(handlingUnit);
		assertEquals("1", handlingUnit.getId());
		
		// Delete returns null because the handlingUnit does not exist
		assertNull(handlingUnitLocal.delete(handlingUnit));
	}
	
	@Test
	@InSequence(3)
	public void getAll() {
		LOG.info("Test getAll");
		
		// Cleanup
		List<HandlingUnit> handlingUnits = handlingUnitLocal.getAll();
		
		handlingUnits.stream().forEach(l -> handlingUnitLocal.delete(l));
		
		handlingUnits = handlingUnitLocal.getAll();
		
		assertTrue(handlingUnits.isEmpty());
		
		// Prepare some handling units
		handlingUnitLocal.create(new HandlingUnit("1"));
		handlingUnitLocal.create(new HandlingUnit("2"));
		handlingUnitLocal.create(new HandlingUnit("3"));
		handlingUnitLocal.create(new HandlingUnit("4"));
		handlingUnitLocal.create(new HandlingUnit("5"));

		// Another test
		handlingUnits = handlingUnitLocal.getAll();

		assertFalse(handlingUnits.isEmpty());
		assertEquals(5, handlingUnits.size());
		
		//Cleanup
		handlingUnits.stream().forEach(l -> handlingUnitLocal.delete(l));
	}
}
