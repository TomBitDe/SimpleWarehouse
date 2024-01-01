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
	 * Constructor is mandatory
	 * 
	 * @param locationId the location id
	 * @param selected the selection flag
	 */
	public SimpleLocation(String locationId, boolean selected) {
		super(selected);
		this.locationId = locationId;
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
}
