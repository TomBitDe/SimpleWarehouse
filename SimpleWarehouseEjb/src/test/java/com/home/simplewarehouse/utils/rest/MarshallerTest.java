package com.home.simplewarehouse.utils.rest;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.junit.InSequence;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.home.simplewarehouse.model.Dimension;
import com.home.simplewarehouse.model.EntityBase;
import com.home.simplewarehouse.model.FifoLocation;
import com.home.simplewarehouse.model.LifoLocation;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.LocationStatus;
import com.home.simplewarehouse.patterns.singleton.simplecache.model.ApplConfig;

@RunWith(JUnit4.class)
public class MarshallerTest {
	private static final Logger LOG = LogManager.getLogger(MarshallerTest.class);

	private static File file = new File("../logs/marshaller.xml");
	
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
		
		file.delete();
		
		LOG.trace("<-- setUp()");		
    }

    /**
	 * What to do after all tests have been executed
	 */
    @AfterClass
    public static void tearDown() {
		LOG.trace("--> tearDown()");
		
		
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

	@Test
	@InSequence(0)
	public void marshallApplConfig()
	{
        ApplConfig config = new ApplConfig("KeyValue_1", "ParamValue_1");
        
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ApplConfig.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(config, file);
			jaxbMarshaller.marshal(config, System.out);
		}
		catch (Exception ex) {
			LOG.fatal(ex.getMessage());
			LOG.fatal(ex.toString());
			
			Assert.fail("Exception not expected");
        }
	}
	
	@Test
	@InSequence(3)
	public void marshallEntityBase()
	{
		EntityBase entityBase = new EntityBase();
        
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(EntityBase.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(entityBase, file);
			jaxbMarshaller.marshal(entityBase, System.out);
		}
		catch (Exception ex) {
			LOG.fatal(ex.getMessage());
			LOG.fatal(ex.toString());
			
			Assert.fail("Exception not expected");
        }
	}
	
	@Test
	@InSequence(6)
	public void marshallDimension() {
		Dimension dimension = new Dimension("Loc_1");

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Dimension.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(dimension, file);
			jaxbMarshaller.marshal(dimension, System.out);
		}
		catch (Exception ex) {
			LOG.fatal(ex.getMessage());
			LOG.fatal(ex.toString());
			
			Assert.fail("Exception not expected");
        }
	}
	
	@Test
	@InSequence(9)
	public void marshallLocationStatus() {
		LocationStatus locationStatus = new LocationStatus("Loc_1");

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(LocationStatus.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(locationStatus, file);
			jaxbMarshaller.marshal(locationStatus, System.out);
		}
		catch (Exception ex) {
			LOG.fatal(ex.getMessage());
			LOG.fatal(ex.toString());
			
			Assert.fail("Exception not expected");
        }
	}
	
	@Test
	@InSequence(12)
	public void marshallLocation()
	{
        Location location = new Location("Loc_1");
        
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Location.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(location, file);
			jaxbMarshaller.marshal(location, System.out);
		}
		catch (Exception ex) {
			LOG.fatal(ex.getMessage());
			LOG.fatal(ex.toString());
			
			Assert.fail("Exception not expected");
        }
	}

	@Test
	@InSequence(15)
	public void marshallFifoLocation()
	{
		FifoLocation fifo = new FifoLocation("Loc_1");
        
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FifoLocation.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(fifo, file);
			jaxbMarshaller.marshal(fifo, System.out);
		}
		catch (Exception ex) {
			LOG.fatal(ex.getMessage());
			LOG.fatal(ex.toString());
			
			Assert.fail("Exception not expected");
        }
	}

	@Test
	@InSequence(18)
	public void marshallLifoLocation()
	{
		LifoLocation lifo = new LifoLocation("Loc_1");
        
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(LifoLocation.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(lifo, file);
			jaxbMarshaller.marshal(lifo, System.out);
		}
		catch (Exception ex) {
			LOG.fatal(ex.getMessage());
			LOG.fatal(ex.toString());
			
			Assert.fail("Exception not expected");
        }
	}

	@Test
	@InSequence(21)
	public void marshallHandlingUnit()
	{
		HandlingUnit handlingUnit = new HandlingUnit("HU_B");
		handlingUnit.setBaseHU(new HandlingUnit("HU_A"));
		handlingUnit.setLocation(new Location("LOC_2"));
		Set<HandlingUnit> contains = new HashSet<>();
		contains.add(new HandlingUnit("HU_C"));
		contains.add(new HandlingUnit("HU_D"));
		contains.add(new HandlingUnit("HU_E"));
		handlingUnit.setContains(contains);
        
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(HandlingUnit.class);
			Marshaller jaxbMarshaller = defineMarshaller(jaxbContext);

			jaxbMarshaller.marshal(handlingUnit, file);
			jaxbMarshaller.marshal(handlingUnit, System.out);
		}
		catch (Exception ex) {
			LOG.fatal(ex.getMessage());
			LOG.fatal(ex.toString());
			
			Assert.fail("Exception not expected");
        }
	}

	public static Marshaller defineMarshaller(JAXBContext jaxbContext) throws Exception {
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		return jaxbMarshaller;
	}
}
