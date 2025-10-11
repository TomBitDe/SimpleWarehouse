package com.home.simplewarehouse.utils.mdb;

import java.util.Enumeration;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class QueueUtils {
	private static final Logger LOG = LogManager.getLogger(QueueUtils.class);
	
	private static final String JMS_CONNECTION_FACTORY = "jms/__defaultConnectionFactory";

    private QueueUtils() {
        // Utility class → prevent instantiation
    }

    /**
     * Returns the number of messages currently in the specified JMS queue.
     *
     * @param queueJndiName the JNDI name of the queue (e.g. "queue/Zone")
     * @return the number of messages currently pending in the queue
     * @throws JMSException if a JMS error occurs
     * @throws NamingException if the JNDI lookup fails
     */
    public static int getMessageCount(String queueJndiName) throws JMSException, NamingException {
        InitialContext ctx = new InitialContext();
        ConnectionFactory connectionFactory =
                (ConnectionFactory) ctx.lookup(JMS_CONNECTION_FACTORY);
        Queue queue = (Queue) ctx.lookup(queueJndiName);

        try (JMSContext jmsContext = connectionFactory.createContext()) {
            QueueBrowser browser = jmsContext.createBrowser(queue);
            Enumeration<?> enumeration = browser.getEnumeration();

            int count = 0;
            while (enumeration.hasMoreElements()) {
                enumeration.nextElement();
                count++;
            }

            return count;
        }
    }

    /**
     * Remove all content of a given queue
     * 
     * @param queueJndiName the JNDI name of the queue (e.g. "queue/Zone")
     * @param timeout the timeout to wait on queue for receiving
     * @throws NamingException 
     */
    public static void clearQueue(String queueJndiName, int timeout) throws NamingException {
        InitialContext ctx = new InitialContext();
        ConnectionFactory connectionFactory =
                (ConnectionFactory) ctx.lookup(JMS_CONNECTION_FACTORY);
        Queue queue = (Queue) ctx.lookup(queueJndiName);

        try (JMSContext context = connectionFactory.createContext()) {
			try (JMSConsumer consumer = context.createConsumer(queue)) {
                Message consumed = consumer.receive(timeout);

                while (consumed != null) {
                	consumed = consumer.receive(timeout);
                }
            }
		}		
	}

	/**
     * Remove all content of a given queue and count the messages removed
	 * 
     * @param queueJndiName the JNDI name of the queue (e.g. "queue/Zone")
     * @param timeout the timeout to wait on queue for receiving
     * 
	 * @return the number of removed messages
	 * 
	 * @throws JMSException
	 * @throws NamingException 
	 */
    public static int clearQueueWithCount(String queueJndiName, int timeout) throws JMSException, NamingException {
        InitialContext ctx = new InitialContext();
        ConnectionFactory connectionFactory =
                (ConnectionFactory) ctx.lookup(JMS_CONNECTION_FACTORY);
        Queue queue = (Queue) ctx.lookup(queueJndiName);

        int count = 0;
		
		try (JMSContext context = connectionFactory.createContext()) {
			try (JMSConsumer consumer = context.createConsumer(queue)) {
                Message consumed = consumer.receive(timeout);

                while (consumed != null) {
                	++ count;
                	consumed = consumer.receive(timeout);
                }
            }
		}
		
		LOG.info("Cleared [{}] messages from queue >{}<", count, queue.getQueueName());
		
		return count;
	}
}
