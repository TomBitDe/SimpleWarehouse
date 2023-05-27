package com.home.simplewarehouse.war.websocket;

import java.io.StringReader;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.war.websocket.model.Location;

/**
 * The Web Socket Server handles all the general tasks of a Web Socket implementation.<br>
 * <p>
 * The session handler is injected and the session is controlled from here.<br>
 */
@ApplicationScoped
@ServerEndpoint("/actions")
public class DeviceWebSocketServer {
	private static final Logger LOG = LogManager.getLogger(DeviceWebSocketServer.class);
	
	private static final String ACTION = "action";

    @Inject
    private DeviceSessionHandler sessionHandler;

    /**
     * On open add the session using the session handler
     *
     * @param session the given session
     */
	@OnOpen
	public void open(Session session) {
		LOG.debug("--> open");

		sessionHandler.addSession(session);

		LOG.debug("<-- open");
	}

	/**
	 * On close remove the session using the session handler
	 *
	 * @param session the given session
	 */
	@OnClose
	public void close(Session session) {
		LOG.debug("--> close");

		sessionHandler.removeSession(session);

		LOG.debug("<-- close");
	}

	/**
	 * On error log the error message
	 *
	 * @param error the error
	 */
	@OnError
	public void onError(Throwable error) {
		LOG.fatal(error);
	}

	/**
	 * On message dispatch to the related method in the session handler
	 *
	 * @param message the received message to use for dispatching
	 * @param session the given session
	 */
	@OnMessage
	public void handleMessage(String message, Session session) {
		LOG.debug("--> handleMessage");

        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            LOG.info("jsonMessage action = <{}>", jsonMessage.getString(ACTION));

            if ("add".equals(jsonMessage.getString(ACTION))) {
            	Location location = new Location();
            	location.setId(jsonMessage.getString("id"));
            	location.setType(jsonMessage.getString("type"));
                sessionHandler.addLocation(location);
            }

            if ("remove".equals(jsonMessage.getString(ACTION))) {
                String id = jsonMessage.getString("id");
                sessionHandler.removeLocation(id);
            }
        }

        LOG.debug("<-- handleMessage");
	}
}
