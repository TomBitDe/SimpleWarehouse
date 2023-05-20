package com.home.simplewarehouse.war.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.war.websocket.model.Device;

/**
 * Handle the Web Socket session and the entity that has to be controlled.
 */
@ApplicationScoped
public class DeviceSessionHandler {
	private static final Logger LOG = LogManager.getLogger(DeviceSessionHandler.class);

	private int deviceId = 0;
    private final Set<Session> sessions = new HashSet<>();
    private final Set<Device> devices = new HashSet<>();

    /**
     * Add a session to the Set of controlled sessions
     *
     * @param session the session to handle
     */
    public void addSession(Session session) {
    	LOG.debug("--> addSession");

        sessions.add(session);
        for (Device device : devices) {
            JsonObject addMessage = createAddMessage(device);
            sendToSession(session, addMessage);
        }

    	LOG.debug("<-- addSession");
    }

    /**
     * Remove a session from the Set of controlled sessions
     *
     * @param session the session to handle
     */
    public void removeSession(Session session) {
    	LOG.debug("--> removeSession");

    	sessions.remove(session);

    	LOG.debug("<-- removeSession");
    }

    /**
     * Get a list of all managed entities
     *
     * @return the entities (Devices)
     */
    public List<Device> getDevices() {
    	LOG.debug("--> getDevices");

    	LOG.debug("<-- getDevices");
        return new ArrayList<>(devices);
    }

    /**
     * Add an entity (Device) to the set of managed entities
     *
     * @param device the entity to add
     */
    public void addDevice(Device device) {
    	LOG.debug("--> addDevice");

        device.setId(deviceId);
        devices.add(device);
        deviceId++;
        JsonObject addMessage = createAddMessage(device);
        sendToAllConnectedSessions(addMessage);

        LOG.debug("<-- addDevice");
    }

    /**
     * Remove an entity (Device) from the set of managed entities
     *
     * @param id the id of the entity to remove
     */
    public void removeDevice(int id) {
    	LOG.debug("--> removeDevice");

        Device device = getDeviceById(id);
        if (device != null) {
            devices.remove(device);
            JsonProvider provider = JsonProvider.provider();
            JsonObject removeMessage = provider.createObjectBuilder()
                    .add("action", "remove")
                    .add("id", id)
                    .build();
            sendToAllConnectedSessions(removeMessage);
        }

        LOG.debug("<-- removeDevice");
    }

    /**
     * Switch the entity (Device) on/off
     *
     * @param id the id of the entity to change its internal state
     */
    public void toggleDevice(int id) {
    	LOG.debug("--> toggleDevice");

        JsonProvider provider = JsonProvider.provider();
        Device device = getDeviceById(id);
        if (device != null) {
            if ("On".equals(device.getStatus())) {
                device.setStatus("Off");
            } else {
                device.setStatus("On");
            }
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "toggle")
                    .add("id", device.getId())
                    .add("status", device.getStatus())
                    .build();
            sendToAllConnectedSessions(updateDevMessage);
        }

        LOG.debug("<-- toggleDevice");
    }

    /**
     * Get the entity (Device) by its id
     *
     * @param id the id to use
     *
     * @return the matching entity (Device) or null in case of no match
     */
    private Device getDeviceById(int id) {
    	LOG.debug("--> getDeviceById");

    	for (Device device : devices) {
            if (device.getId() == id) {
                LOG.debug("<-- getDeviceById");

                return device;
            }
        }

        LOG.debug("<-- getDeviceById");

        return null;
    }

    /**
     * Create a JSON object out of an entity (Device) for further processing
     *
     * @param device the given entity (Device)
     *
     * @return the related JSON object
     */
    private JsonObject createAddMessage(Device device) {
    	LOG.debug("--> createAddMessage");

        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "add")
                .add("id", device.getId())
                .add("name", device.getName())
                .add("type", device.getType())
                .add("status", device.getStatus())
                .add("description", device.getDescription())
                .build();

        LOG.debug("<-- createAddMessage");

        return addMessage;
    }

    /**
     * Send a JSON object to all connected sessions
     *
     * @param message the JSON object to send
     */
    private void sendToAllConnectedSessions(JsonObject message) {
    	LOG.debug("--> sendToAllConnectedSessions");

        for (Session session : sessions) {
            sendToSession(session, message);
        }

        LOG.debug("<-- sendToAllConnectedSessions");
    }

    /**
     * Send a JSON object to a specific session
     *
     * @param session the given session
     * @param message the JSON object to send
     */
    private void sendToSession(Session session, JsonObject message) {
    	LOG.debug("--> sendToSession");

    	try {
            session.getBasicRemote().sendText(message.toString());
        }
        catch (IOException ex) {
            sessions.remove(session);
            LOG.fatal(ex);
        }
    	finally {
        	LOG.debug("<-- sendToSession");
    	}
    }
}
