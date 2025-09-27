package com.home.simplewarehouse.zone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.home.simplewarehouse.handlingunit.HandlingUnitBean;
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.location.LocationBean;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.Zone;
import com.home.simplewarehouse.patterns.exceptions.BusinessException;
import com.home.simplewarehouse.patterns.mdb.CommonJmsUtility;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

@RunWith(Arquillian.class)
public class ZoneQueueListenerTest extends CommonJmsUtility {
	private static final Logger LOG = LogManager.getLogger(ZoneQueueListenerTest.class);

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
				.addAsManifestResource(new File("src/test/resources/META-INF/test-glassfish-ejb-jar.xml"),
						"glassfish-ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(
						ZoneQueueListenerBean.class,
						ZoneService.class, ZoneBean.class, Zone.class,
						LocationService.class, LocationBean.class, Location.class,
						HandlingUnitService.class, HandlingUnitBean.class, HandlingUnit.class,
						PerformanceAuditor.class,
						MonitoringResource.class
						);

		LOG.debug(archive.toString(true));

		LOG.trace("<-- createTestArchive()");
		return archive;
	}
	
	@Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
	
	@Resource(lookup = "queue/Zone")
    private Queue zoneQueue;
	
	@Resource(lookup = "queue/Error")
    private Queue errorQueue;

	/**
	 * Mandatory default constructor
	 */
	public ZoneQueueListenerTest() {
		super();
		// DO NOTHING HERE!
	}

	/**
	 * What to do before an individual test will be executed (each test)
	 */
	@Before
	public void beforeTest() throws BusinessException {
		LOG.trace("--> beforeTest()");
		
		LOG.trace("<-- beforeTest()");		
	}
	
	/**
	 * What to do after an individual test will be executed (each test)
	 * @throws BusinessException 
	 */
	@After
	public void afterTest() throws BusinessException {
		LOG.trace("--> afterTest()");

		LOG.trace("<-- afterTest()");
	}

	@Test
	@InSequence(0)
    public void testBusinessExceptionGoesToErrorQueue() throws JMSException {
		Connection connection = connectionFactory.createConnection();
		assertNotNull(connection);
		LOG.info("Connection created...");

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		assertNotNull(session);
		LOG.info("Session created...");

		connection.start();
		LOG.info("Connection started...");

		MessageProducer producer = session.createProducer(zoneQueue);
		assertNotNull(producer);
		LOG.info("Message Producer created...");

		Message message = session.createTextMessage("Invalid payload");
		assertNotNull(message);
		LOG.info("Text Message created...");

		producer.send(message);
		
		try (JMSContext context = connectionFactory.createContext()) {
			try (JMSConsumer consumer = context.createConsumer(errorQueue)) {
                Message consumed = consumer.receive(1000);
                
                if (consumed instanceof TextMessage) {
                	TextMessage errorMsg = (TextMessage) consumed;
                	
                    assertNotNull(errorMsg);
                
                    LOG.info("errorMsg: {}", errorMsg);

                    assertEquals("Invalid payload", errorMsg.getStringProperty("originalPayload"));
                
                    LOG.info("errorMsg.getText(): {}", errorMsg.getText());
                }
            }
		}
    }

    @Test
	@InSequence(10)
    public void testNormalProcessingSucceeds() throws JMSException {
		Connection connection = connectionFactory.createConnection();
		assertNotNull(connection);
		LOG.info("Connection created...");

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		assertNotNull(session);
		LOG.info("Session created...");

		connection.start();
		LOG.info("Connection started...");

		MessageProducer producer = session.createProducer(zoneQueue);
		assertNotNull(producer);
		LOG.info("Message Producer created...");

		Message message = session.createTextMessage("12345");
		assertNotNull(message);
		LOG.info("Text Message created...");

		producer.send(message);
		
		try (JMSContext context = connectionFactory.createContext()) {
			try (JMSConsumer consumer = context.createConsumer(errorQueue)) {
                Message consumed = consumer.receive(1000);

                // No message in ErrorQueue               
                assertNull(consumed);
            }
		}
    }

    @Test
	@InSequence(20)
    public void testRedeliverySimulation() throws JMSException {
		Connection connection = connectionFactory.createConnection();
		assertNotNull(connection);
		LOG.info("Connection created...");

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		assertNotNull(session);
		LOG.info("Session created...");

		connection.start();
		LOG.info("Connection started...");

		MessageProducer producer = session.createProducer(zoneQueue);
		assertNotNull(producer);
		LOG.info("Message Producer created...");

		Message message = session.createTextMessage("trigger runtime error");
		assertNotNull(message);
		LOG.info("Text Message created...");

        message.setIntProperty("JMSXDeliveryCount", 1); // simulate first try

        try {
		    producer.send(message);
		}
        catch (EJBException ignored) {
        }
		
        message.setIntProperty("JMSXDeliveryCount", 2); // simulate second try

        try {
		    producer.send(message);
		}
        catch (EJBException ignored) {
        }

        message.setIntProperty("JMSXDeliveryCount", 3); // simulate third try

        try {
		    producer.send(message);
		}
        catch (EJBException ignored) {
        }

		try (JMSContext context = connectionFactory.createContext()) {
			try (JMSConsumer consumer = context.createConsumer(errorQueue)) {
                Message consumed = consumer.receive(1000);

                // Now message in ErrorQueue               
                assertNotNull(consumed);

                if (consumed instanceof TextMessage) {
                	TextMessage errorMsg = (TextMessage) consumed;
                	
                    assertNotNull(errorMsg);
                
                    LOG.info("errorMsg: {}", errorMsg);

                    assertEquals("trigger runtime error", errorMsg.getStringProperty("originalPayload"));
                
                    LOG.info("errorMsg.getText(): {}", errorMsg.getText());
                }
            }
		}
    }

}
