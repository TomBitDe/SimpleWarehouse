package com.home.simplewarehouse.views;

import java.io.Serializable;

/**
 * Simplified view on Locations containing HandingUnits with selection.
 */
public class SimpleLocationWithHandlingUnits extends SelectableView implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The locations id
	 */
	private String locationId;
	/**
	 * The handling units comma separated
	 */
	private String handlingUnits;
	
	/**
	 * Constructor with location, handlingunit and selected flag
	 * 
	 * @param locationId the locations id
	 * @param handlingUnits the handlingunits id
	 * @param selected the selected flag
	 */
	public SimpleLocationWithHandlingUnits(String locationId, String handlingUnits, boolean selected) {
		super(selected);
		this.locationId = locationId;
		this.handlingUnits = handlingUnits;
	}

	/**
	 * Gets the locations id
	 * 
	 * @return the id
	 */
	public String getLocationId() {
		return locationId;
	}

	/**
	 * Sets the locations id
	 * 
	 * @param locationId the id
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	/**
	 * Gets the handling units
	 * 
	 * @return the handling units
	 */
	public String getHandlingUnits() {
		return handlingUnits;
	}

	/**
	 * Sets the handling units
	 * 
	 * @param handlingUnits the handling units
	 */
	public void setHandlingUnits(String handlingUnits) {
		this.handlingUnits = handlingUnits;
	}
}
