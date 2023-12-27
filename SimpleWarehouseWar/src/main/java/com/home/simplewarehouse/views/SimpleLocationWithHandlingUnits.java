package com.home.simplewarehouse.views;

import java.io.Serializable;

/**
 * Simplified view on Locations containing HandingUnits with selection.
 */
public class SimpleLocationWithHandlingUnits implements Serializable {
	private static final long serialVersionUID = 1L;

	private String locationId;
	private String handlingUnits;
	private boolean selected;
	
	public SimpleLocationWithHandlingUnits(String locationId, String handlingUnits, boolean selected) {
		super();
		this.locationId = locationId;
		this.handlingUnits = handlingUnits;
		this.selected = selected;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHandlingUnits() {
		return handlingUnits;
	}

	public void setHandlingUnits(String handlingUnits) {
		this.handlingUnits = handlingUnits;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
