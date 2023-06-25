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

import com.home.simplewarehouse.war.websocket.model.Location;

/**
 * Handle the Web Socket session and the entities that have to be controlled.
 */
@ApplicationScoped
public class LocationSessionHandler {
	private static final Logger LOG = LogManager.getLogger(LocationSessionHandler.class);
	
	private static final String ACTION = "action";

    private final Set<Session> sessions = new HashSet<>();
    private final Set<Location> locations = new HashSet<>();

    /**
     * Not needed but done
     */
    public LocationSessionHandler() {
    	super();
    	
    	LOG.trace("--> LocationSessionHandler");
    	LOG.trace("<-- LocationSessionHandler");
    }
    
    /**
     * Add a session to the Set of controlled sessions
     *
     * @param session the session to handle
     */
    public void addSession(Session session) {
    	LOG.trace("--> addSession");

        sessions.add(session);
        for (Location location : locations) {
            JsonObject addMessage = createAddMessage(location);
            sendToSession(session, addMessage);
        }

    	LOG.trace("<-- addSession");
    }

    /**
     * Remove a session from the Set of controlled sessions
     *
     * @param session the session to handle
     */
    public void removeSession(Session session) {
    	LOG.trace("--> removeSession");

    	sessions.remove(session);

    	LOG.trace("<-- removeSession");
    }

    /**
     * Get a list of all managed entities
     *
     * @return the entities (Locations)
     */
    public List<Location> getLocations() {
    	LOG.trace("--> getLocations");

    	LOG.trace("<-- getLocations");
        return new ArrayList<>(locations);
    }

    /**
     * Add an entity (Location) to the set of managed entities
     *
     * @param location the entity to add
     */
    public void addLocation(Location location) {
    	LOG.trace("--> addLocation");

    	LOG.debug("Location={}", location);
        location.setId(location.getId());
        locations.add(location);
        JsonObject addMessage = createAddMessage(location);
        LOG.debug("JsonObject={}", addMessage);
        
        sendToAllConnectedSessions(addMessage);

        LOG.trace("<-- addLocation");
    }

    /**
     * Remove an entity (Location) from the set of managed entities
     *
     * @param id the id of the entity to remove
     */
    public void removeLocation(String id) {
    	LOG.trace("--> removeLocation");

    	Location location = getLocationById(id);
        if (location != null) {
            locations.remove(location);
            JsonProvider provider = JsonProvider.provider();
            JsonObject removeMessage = provider.createObjectBuilder()
                    .add(ACTION, "remove")
                    .add("id", id)
                    .build();
            LOG.debug("JsonObject={}", removeMessage);
            
            sendToAllConnectedSessions(removeMessage);
        }

        LOG.trace("<-- removeLocation");
    }

    /**
     * Get the entity (Location) by its id
     *
     * @param id the id to use
     *
     * @return the matching entity (Location) or null in case of no match
     */
    private Location getLocationById(String id) {
    	LOG.trace("--> getLocationById({})", id);

    	for (Location location : locations) {
            if (location.getId().equals(id)) {
                LOG.debug("<-- getLocationById");

                return location;
            }
        }

        LOG.trace("<-- getLocationById");

        return null;
    }

    /**
     * Create a JSON object out of an entity (Location) for further processing
     *
     * @param location the given entity (Location)
     *
     * @return the related JSON object
     */
    private JsonObject createAddMessage(Location location) {
    	LOG.trace("--> createAddMessage");

        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add(ACTION, "add")
                .add("id", location.getId())
                .add("type", location.getType())
                .add("version", location.getVersion())
//                .add("handlingUnitIds", location.getHandlingUnitIds())
                .build();

        LOG.trace("<-- createAddMessage");

        return addMessage;
    }

    /**
     * Send a JSON object to all connected sessions
     *
     * @param message the JSON object to send
     */
    private void sendToAllConnectedSessions(JsonObject message) {
    	LOG.trace("--> sendToAllConnectedSessions");

        for (Session session : sessions) {
            sendToSession(session, message);
        }

        LOG.trace("<-- sendToAllConnectedSessions");
    }

    /**
     * Send a JSON object to a specific session
     *
     * @param session the given session
     * @param message the JSON object to send
     */
    private void sendToSession(Session session, JsonObject message) {
    	LOG.trace("--> sendToSession");

    	try {
            session.getBasicRemote().sendText(message.toString());
        }
        catch (IOException ex) {
            sessions.remove(session);
            LOG.fatal(ex);
        }
    	finally {
        	LOG.trace("<-- sendToSession");
    	}
    }
}
