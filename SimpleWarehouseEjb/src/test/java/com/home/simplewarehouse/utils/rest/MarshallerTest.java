package com.home.simplewarehouse.utils.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.location.DimensionBean;
import com.home.simplewarehouse.location.DimensionService;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.location.LocationStatusBean;
import com.home.simplewarehouse.location.LocationStatusService;
import com.home.simplewarehouse.model.Dimension;
import com.home.simplewarehouse.model.EntityBase;
import com.home.simplewarehouse.model.FifoLocation;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.LifoLocation;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.LocationStatus;
import com.home.simplewarehouse.patterns.singleton.simplecache.model.ApplConfig;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity.ExceptionStatistics;

/**
 * Test for Entity marshalling.
 */
@RunWith(Arquillian.class)
public class MarshallerTest {
	private static final Logger LOG = LogManager.getLogger(MarshallerTest.class);
	
	private static final String REPORT_FILE_PATH = "../logs/marshaller.xml";

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
						DimensionService.class, DimensionBean.class,
						LocationStatusService.class, LocationStatusBean.class,
						LocationService.class, LocationBean.class,
						HandlingUnitService.class, HandlingUnitBean.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		LOG.trace("<-- createTestArchive()");
		return archive;
	}
	
	@EJB
	HandlingUnitService handlingUnitService;
	
	private static FileOutputStream fos;
	
	/**
	 * Mandatory default constructor
	 */
	public MarshallerTest() {
		super();
		// DO NOTHING HERE!
	}
	
    /**
	 * What to do before all tests will be executed
	 */
    @BeforeClass
    public static void setUp() {
		LOG.trace("--> setUp()");
		
		// Delete old report
		new File(REPORT_FILE_PATH).delete();

		try {
			// Append data to report
			fos = new FileOutputStream(REPORT_FILE_PATH, true);
		}
		catch (Exception e) {
			LOG.fatal(e.getMessage());		
		}
				
		LOG.trace("<-- setUp()");		
    }

    /**
	 * What to do after all tests have been executed
	 */
    @AfterClass
    public static void tearDown() {
		LOG.trace("--> tearDown()");
		
		try {
			fos.close();
		}
		catch (Exception e) {
			LOG.fatal(e.getMessage());		
		}
		
		LOG.trace("<-- tearDown()");		
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
		

		LOG.trace("<-- afterTest()");		
	}

	/**
	 * Test ApplConfig entity
	 */
	@Test
	@InSequence(0)
	public void marshallApplConfig()
	{
        ApplConfig config = new ApplConfig("KeyValue_1", "ParamValue_1");
        
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ApplConfig.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(config, fos);
			jaxbMarshaller.marshal(config, System.out);
		}
		catch (Exception ex) {
			handleException(ex);
        }
	}
	
	/**
	 * Test ExceptionStatistics entry
	 */
	@Test
	@InSequence(1)
	public void marshallExceptionStatistics()
	{
		ExceptionStatistics entry = new ExceptionStatistics("KeyValue_1", 329);
        
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ExceptionStatistics.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(entry, fos);
			jaxbMarshaller.marshal(entry, System.out);
		}
		catch (Exception ex) {
			handleException(ex);
        }
	}
	
	/**
	 * Test EntityBase entity
	 */
	@Test
	@InSequence(3)
	public void marshallEntityBase()
	{
		EntityBase entityBase = new EntityBase();
        
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(EntityBase.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(entityBase, fos);
			jaxbMarshaller.marshal(entityBase, System.out);
		}
		catch (Exception ex) {
			handleException(ex);
		}
	}
	
	/**
	 * Test Dimension entity
	 */
	@Test
	@InSequence(6)
	public void marshallDimension() {
		Dimension dimension = new Dimension(new Location("Loc_1"));

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Dimension.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(dimension, fos);
			jaxbMarshaller.marshal(dimension, System.out);
		}
		catch (Exception ex) {
			handleException(ex);
		}
	}
	
	/**
	 * Test LocationStatus entity
	 */
	@Test
	@InSequence(9)
	public void marshallLocationStatus() {
		LocationStatus locationStatus = new LocationStatus(new Location("Loc_1"));

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(LocationStatus.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(locationStatus, fos);
			jaxbMarshaller.marshal(locationStatus, System.out);
		}
		catch (Exception ex) {
			handleException(ex);
		}
	}
	
	/**
	 * Test Location entity
	 */
	@Test
	@InSequence(12)
	public void marshallLocation()
	{
        Location location = new Location("Loc_1");
        Set<HandlingUnit> placed = new HashSet<>();
        placed.add(new HandlingUnit("HU_A"));
        placed.add(new HandlingUnit("HU_B"));
        location.setHandlingUnits(placed);
        
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Location.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(location, fos);
			jaxbMarshaller.marshal(location, System.out);
		}
		catch (Exception ex) {
			handleException(ex);        }
	}

	/**
	 * Test FifoLocation entity
	 */
	@Test
	@InSequence(15)
	public void marshallFifoLocation()
	{
		FifoLocation fifo = new FifoLocation("Loc_1");
        
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FifoLocation.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(fifo, fos);
			jaxbMarshaller.marshal(fifo, System.out);
		}
		catch (Exception ex) {
			handleException(ex);        }
	}

	/**
	 * Test LifoLocation entity
	 */
	@Test
	@InSequence(18)
	public void marshallLifoLocation()
	{
		LifoLocation lifo = new LifoLocation("Loc_1");
        
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(LifoLocation.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(lifo, fos);
			jaxbMarshaller.marshal(lifo, System.out);
		}
		catch (Exception ex) {
			handleException(ex);        }
	}

	/**
	 * Test HandlingUnit entity
	 */
	@Test
	@InSequence(21)
	public void marshallHandlingUnit()
	{
		try {
			HandlingUnit base = handlingUnitService.createOrUpdate(new HandlingUnit("HU_A"));
			base = handlingUnitService.assign(new HandlingUnit("HU_B"), base);
			base = handlingUnitService.assign(new HandlingUnit("HU_C"), base);
			base = handlingUnitService.assign(new HandlingUnit("HU_D"), base);
			handlingUnitService.dropTo(new Location("LOC_1"), base);
			
			base = handlingUnitService.getById("HU_A");
	        
			JAXBContext jaxbContext = JAXBContext.newInstance(HandlingUnit.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(base, fos);
			jaxbMarshaller.marshal(base, System.out);
			
			jaxbMarshaller.marshal(handlingUnitService.getById("HU_B"), fos);
			jaxbMarshaller.marshal(handlingUnitService.getById("HU_B"), System.out);
			
		}
		catch (Exception ex) {
			handleException(ex);
        }
	}

	private static Marshaller defineMarshaller(JAXBContext jaxbContext) throws Exception {
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		
		return jaxbMarshaller;
	}
	
	private static void handleException(Exception ex) {
		LOG.fatal(ex.getMessage());
		LOG.fatal(ex.toString());
		try {
			fos.flush();
			fos.write(ex.toString().getBytes());
			fos.flush();
		}
		catch (IOException e) {
			LOG.fatal(e.getMessage());
		}
		
		Assert.fail("Exception not expected");
	}
}
