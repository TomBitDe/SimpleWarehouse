package com.home.simplewarehouse.zone;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
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

    @Resource
    private MessageDrivenContext mdbContext;

    @Resource(mappedName = "queue/Error")
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
        try {
            if (!(message instanceof TextMessage)) {
                LOG.warn("Unexpected message type: {}", message);
                return;
            }

            String payload = ((TextMessage) message).getText();
            LOG.info("Received message: [{}]", payload);

            // Business logic
            zoneService.clear(payload);

            LOG.info("Message successfully processed.");

        }
        catch (BusinessException be) {
            // Business error → send immediately to ErrorQueue
            LOG.error("Business error, forwarding to ErrorQueue: {}", be.getMessage(), be);
            sendToErrorQueue(message, be);

        }
        catch (Exception ex) {
            try {
                int attempt = message.getIntProperty("JMSXDeliveryCount");
                LOG.warn("Technical error on delivery attempt {}: {}", attempt, ex.getMessage(), ex);

                if (attempt < 3) {
                    // Force redelivery
                    mdbContext.setRollbackOnly();
                }
                else {
                    // After 3 attempts → move to ErrorQueue
                    LOG.error("Max delivery attempts reached, moving message to ErrorQueue");
                    sendToErrorQueue(message, ex);
                }
            }
            catch (JMSException jmsEx) {
                LOG.fatal("Could not read JMSXDeliveryCount, moving message directly to ErrorQueue", jmsEx);
                sendToErrorQueue(message, ex);
            }
        }
    }

    private void sendToErrorQueue(Message originalMessage, Exception cause) {
        try (JMSContext context = connectionFactory.createContext()) {
            JMSProducer producer = context.createProducer();

            TextMessage errorMsg = context.createTextMessage();
            errorMsg.setText("Processing error: " + cause.getMessage());

            // Add original payload
            if (originalMessage instanceof TextMessage) {
                try {
                    errorMsg.setStringProperty(
                        "originalPayload",
                        ((TextMessage) originalMessage).getText()
                    );
                }
                catch (JMSException jmsEx) {
                    errorMsg.setStringProperty("originalPayload", "<unreadable>");
                    errorMsg.setStringProperty("payloadReadError", jmsEx.getMessage());
                }
            }

            // Add error type and delivery count
            errorMsg.setStringProperty("errorType", cause.getClass().getSimpleName());
            errorMsg.setIntProperty("deliveryCount",
                originalMessage.getIntProperty("JMSXDeliveryCount"));

            producer.send(errorQueue, errorMsg);

            LOG.info("Message moved to ErrorQueue.");
        }
        catch (Exception e) {
            LOG.fatal("Failed to send message to ErrorQueue: {}", e.getMessage(), e);
        }
    }
}
