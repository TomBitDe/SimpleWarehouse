package com.home.simplewarehouse.war.websocket.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Location {
	private static final Logger LOG = LogManager.getLogger(Location.class);

    private String id;
    private String type;
    private int version;
	private Set<String> handlingUnitIds = new HashSet<>();

    public Location() {
    	super();

    	LOG.debug("--> Location");
    	LOG.debug("<-- Location");
    }

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the handlingUnits
	 */
	public Set<String> getHandlingUnitIds() {
		return handlingUnitIds;
	}

	/**
	 * @param handlingUnits the handlingUnits to set
	 */
	public void setHandlingUnitIds(Set<String> handlingUnitIds) {
		this.handlingUnitIds = handlingUnitIds;
	}
}
