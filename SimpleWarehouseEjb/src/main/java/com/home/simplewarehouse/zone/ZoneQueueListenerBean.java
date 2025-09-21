package com.home.simplewarehouse.zone;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.patterns.exceptions.BusinessException;

/**
 * Simple MDB queue text message consumer.
 */
@MessageDriven(
		activationConfig = {
				@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
				@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1"),
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
				@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
		},
		mappedName = "queue/Zone")
public class ZoneQueueListenerBean implements MessageListener {
	private static final Logger LOG = LogManager.getLogger(ZoneQueueListenerBean.class);

	@Resource(mappedName = "queue/ErrorQueue")
	private Queue errorQueue;

	@Resource(mappedName = "jms/__defaultConnectionFactory")
	private ConnectionFactory connectionFactory;

	@EJB
	private ZoneService zoneService;
	
    /**
     * Default constructor.
     */
    public ZoneQueueListenerBean() {
		super();
		LOG.trace("--> ZoneQueueListenerBean");
		LOG.trace("<-- ZoneQueueListenerBean");
    }

	/**
     * @see MessageListener#onMessage(Message)
     */
    @Override
	public void onMessage(Message message) {
    	try
        {
            LOG.trace("onMessage: Message of Type [{}] received", message.getClass());
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                LOG.info("TextMessage contains this : [{}]", textMessage);
                String payload = ((TextMessage) message).getText();
                LOG.info("TextMessage contains this payload: [{}]", payload);
                
                // Business logic here
                try {
                    zoneService.delete(payload);
                }
                catch (BusinessException be) {
                    LOG.error("BusinessException, redirect to ErrorQueue : {}", be.getMessage(), be);
                    sendToErrorQueue(message, be);
                }
            }
            else {
        	    LOG.info("Other message type. Try toString() = [{}]", message);
		    }
        }
    	catch (JMSException jmsEx) {
            LOG.fatal("JMS-Error during receive: {}", jmsEx.getMessage(), jmsEx);
            throw new EJBException("JMS-Error", jmsEx);
        }
    	catch (RuntimeException ex) {
            LOG.fatal("Unexpected technical error: {}", ex.getMessage(), ex);
            throw new EJBException("Technical error", ex);
        }
    }

    private void sendToErrorQueue(Message originalMessage, Exception cause) {
        try (JMSContext context = connectionFactory.createContext()) {
            JMSProducer producer = context.createProducer();

            // Build the new message
            TextMessage errorMsg = context.createTextMessage();
            errorMsg.setText("Error while processing: " + cause.getMessage());

            // Original payload passthrough
            if (originalMessage instanceof TextMessage) {
                try {
                    errorMsg.setStringProperty(
                        "originalPayload", 
                        ((TextMessage) originalMessage).getText()
                    );
                }
                catch (JMSException jmsEx) {
                    // In case text is not readable
                    errorMsg.setStringProperty("originalPayload", "<unreadable>");
                    errorMsg.setStringProperty("payloadReadError", jmsEx.getMessage());
                }
            }

            producer.send(errorQueue, errorMsg);
        }
        catch (JMSException jmsEx) {
            // Error while building or sending
            LOG.fatal("Error while sending to ErrorQueue: {}", jmsEx.getMessage(), jmsEx);
        }
        catch (RuntimeException ex) {
            // For all cases so the MDB will not die
            LOG.fatal("Unexpected Error in ErrorQueue handling: {}", ex.getMessage(), ex);
        }
    }
}
