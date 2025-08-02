package com.home.simplewarehouse.patterns.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simple MDB queue text message consumer.
 */
@MessageDriven(
		activationConfig = {
				@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
				@ActivationConfigProperty( propertyName = "maxSession", propertyValue = "15"),
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
				@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
		},
		mappedName = "queue/Queue1")
public class MsgQueueConsumer1Bean implements MessageListener {
	private static final Logger LOG = LogManager.getLogger(MsgQueueConsumer1Bean.class);

    /**
     * Default constructor.
     */
    public MsgQueueConsumer1Bean() {
		super();
		LOG.trace("--> MsgQueueConsumer1Bean");
		LOG.trace("<-- MsgQueueConsumer1Bean");
    }

	/**
     * @see MessageListener#onMessage(Message)
     */
    @Override
	public void onMessage(Message message) {
    	try
        {
            LOG.trace("onMessage: Message of Type [" + message.getClass().toString() + "] received");
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                LOG.info("TextMessage contains this text: [" + textMessage.getText() + ']');
            }
            else {
        	    LOG.info("Other message type. Try toString() = [" + message.toString() + ']');
		    }
        }
        catch (JMSException jex)
        {
        	LOG.fatal("Error on message processing: " + jex.getMessage(), jex );
            throw new EJBException ("Error on message processing: " + jex.getMessage(), jex );
        }
    }
}
