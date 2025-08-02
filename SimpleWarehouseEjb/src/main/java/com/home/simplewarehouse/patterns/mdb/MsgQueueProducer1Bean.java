package com.home.simplewarehouse.patterns.mdb;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simple queue text message producer session bean.
 */
@Stateless
@Local(com.home.simplewarehouse.patterns.mdb.MsgQueueProducer1.class)
//@Interceptors(PerformanceAuditor.class)
public class MsgQueueProducer1Bean implements MsgQueueProducer1 {
	private static final Logger LOG = LogManager.getLogger(MsgQueueProducer1Bean.class.getName());

	@Resource(lookup = "jms/__defaultConnectionFactory")
	private ConnectionFactory factory;

	@Resource(lookup = "queue/Queue1")
	private Queue queue1;

	public MsgQueueProducer1Bean() {
		super();
		LOG.trace("--> MsgQueueProducer1Bean");
		LOG.trace("<-- MsgQueueProducer1Bean");
	}

	@Override
	public void shouldBeAbleToSendMessage() {
		LOG.trace("--> shouldBeAbleToSendMessage");

		Connection connection = null;

		try {
			connection = factory.createConnection();
			LOG.info("Connection created...");

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			LOG.info("Session created...");

			connection.start();
			LOG.info("Connection started...");

			MessageProducer producer = session.createProducer(queue1);
			LOG.info("Message Producer created...");

			Message message = session.createTextMessage("Ping");
			LOG.info("Text Message created...");

			producer.send(message);

			LOG.info("Message [{}] send", message.getBody(String.class));
		}
		catch (JMSException jmsEx) {
			LOG.error(jmsEx.getMessage());
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
				}
				catch (JMSException e) {
					LOG.info(e.getMessage());
				}
			}
		}

		LOG.trace("<-- shouldBeAbleToSendMessage");
	}

	@Override
	public void sendManyMessages(int noMsgs) {
		LOG.trace("--> sendManyMessages");

		Connection connection = null;
		
		try {
			connection = factory.createConnection();
			LOG.info("Connection created...");

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			LOG.info("Session created...");

			connection.start();
			LOG.info("Connection started...");

			MessageProducer producer = session.createProducer(queue1);
			LOG.info("Message Producer created...");

			Message message;

			for (int idx=0; idx < noMsgs; ++idx) {
				message = session.createTextMessage("Message [" + idx + "]");
				LOG.info("Text Message created...");

				producer.send(message);

				LOG.info("Message [{}] send", message.getBody(String.class));
			}
		}
		catch (JMSException jmsEx) {
			LOG.error(jmsEx.getMessage());
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
				}
				catch (JMSException e) {
					LOG.info(e.getMessage());
				}
			}
		}

		LOG.trace("<-- sendManyMessages");
	}
}
