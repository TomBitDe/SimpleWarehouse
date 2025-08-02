package com.home.simplewarehouse.patterns.mdb;

/**
 * MsgQueueProducer1 Session local interface definition
 */
public interface MsgQueueProducer1 {
	/**
	 * Send a simple text message
	 */
	public void shouldBeAbleToSendMessage();
	/**
	 * Send an amount of simple text messages
	 *
	 * @param noMsgs the amount of messages to send
	 */
	public void sendManyMessages(int noMsgs);
}
