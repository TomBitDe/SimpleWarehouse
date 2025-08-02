package com.home.simplewarehouse.patterns.mdb;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

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

import com.home.simplewarehouse.utils.telemetryprovider.monitoring.PerformanceAuditor;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

@RunWith(Arquillian.class)
public class InjectionQueueTest extends CommonJmsUtility {
	private static final Logger LOG = LogManager.getLogger(InjectionQueueTest.class);

	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				/* Put the test-ejb-jar.xml in JARs META-INF folder as ejb-jar.xml */
				.addAsManifestResource(new File("src/test/resources/META-INF/test-ejb-jar.xml"), "ejb-jar.xml")
				.addAsManifestResource(new File("src/test/resources/META-INF/test-glassfish-ejb-jar.xml"),
						"glassfish-ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(
						MsgQueueConsumer1Bean.class,
						PerformanceAuditor.class,
						MonitoringResource.class
		                );

		LOG.debug(archive.toString(true));

		return archive;
	}

	@Resource(lookup = "jms/__defaultConnectionFactory")
	private ConnectionFactory factory;

	@Resource(lookup = "queue/Queue1")
	private Queue queue1;

	@Test
	@InSequence(0)
	public void shouldBeAbleToSendMessage() throws Exception {
		Connection connection = factory.createConnection();
		assertNotNull(connection);
		LOG.info("Connection created...");

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		assertNotNull(session);
		LOG.info("Session created...");

		connection.start();
		LOG.info("Connection started...");

		MessageProducer producer = session.createProducer(queue1);
		assertNotNull(producer);
		LOG.info("Message Producer created...");

		Message message = session.createTextMessage("Ping");
		assertNotNull(message);
		LOG.info("Text Message created...");

		producer.send(message);

		LOG.info("Message [" + message.getBody(String.class) + "] send");
	}

	@Test
	@InSequence(10)
	public void sendManyMessages() throws Exception {
		Connection connection = factory.createConnection();
		assertNotNull(connection);
		LOG.info("Connection created...");

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		assertNotNull(session);
		LOG.info("Session created...");

		connection.start();
		LOG.info("Connection started...");

		MessageProducer producer = session.createProducer(queue1);
		assertNotNull(producer);
		LOG.info("Message Producer created...");

		Message message;

		for (int idx=0; idx < 100; ++idx) {
			message = session.createTextMessage("Message [" + idx + "]");
			LOG.info("Text Message created...");

			producer.send(message);

			LOG.info("Message [" +  message.getBody(String.class) + "] send");
		}

	}
}