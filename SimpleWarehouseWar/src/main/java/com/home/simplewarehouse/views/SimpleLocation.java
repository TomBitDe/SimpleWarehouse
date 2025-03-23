package com.home.simplewarehouse.views;

import java.io.Serializable;

/**
 * Simplified view on Location with selection.
 */
public class SimpleLocation extends SelectableView implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The locations id
	 */
	private String locationId;
	/**
	 * The type
	 */
	private String type;
	/**
	 * The zones
	 */
	private String zones;
	
	/**
	 * Constructor is mandatory
	 * 
	 * @param locationId the location id
	 * @param type the location type
	 * @param zones the zones
	 * @param selected the selection flag
	 */
	public SimpleLocation(String locationId, String type, String zones, boolean selected) {
		super(selected);
		this.locationId = locationId;
		this.type = type;
		this.zones = zones;
	}

	/**
	 * Gets the location id
	 * 
	 * @return the location id
	 */
	public String getLocationId() {
		return locationId;
	}

	/**
	 * Sets the location id
	 * 
	 * @param locationId the location id
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	/**
	 * Gets the location type
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the location type
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the Zones
	 * 
	 * @return the zones
	 */
	public String getZones() {
		return zones;
	}

	/**
	 * Sets the Zones
	 * 
	 * @param zones the zones to set
	 */
	public void setZones(String zones) {
		this.zones = zones;
	}
}
