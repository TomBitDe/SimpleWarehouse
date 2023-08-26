package com.home.simplewarehouse.war.websocket.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Adapted Location VO.
 */
public class Location {
	private static final Logger LOG = LogManager.getLogger(Location.class);

    private String id;
    private String type;
    private int version;
	private Set<String> handlingUnitIds = new HashSet<>();

	/**
	 * Needed constructor
	 */
    public Location() {
    	super();

    	LOG.trace("--> Location");
    	LOG.trace("<-- Location");
    }

	/**
	 * Gets the Locations id
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the Locations id
	 * 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the Locations type
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the Locations type
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the Locations version
	 * 
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Sets the Locations version
	 * 
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Gets the Locations containing Handling Unit ids
	 * 
	 * @return the handlingUnitIds
	 */
	public Set<String> getHandlingUnitIds() {
		return handlingUnitIds;
	}

	/**
	 * Sets the Locations containing Handling Unit ids
	 * 
	 * @param handlingUnitIds the handlingUnits to set
	 */
	public void setHandlingUnitIds(Set<String> handlingUnitIds) {
		this.handlingUnitIds = handlingUnitIds;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		return Objects.equals(id, other.id) && version == other.version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Location [id=").append(id).append(", type=").append(type).append(", version=").append(version)
				.append(", handlingUnitIds=").append(handlingUnitIds).append("]");
		return builder.toString();
	}
}
