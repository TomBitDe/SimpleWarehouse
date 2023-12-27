package com.home.simplewarehouse.views;

import java.io.Serializable;

/**
 * Simplified view on Locations containing HandingUnits with selection.
 */
public class SimpleLocationWithHandlingUnits extends SelectableView implements Serializable {
	private static final long serialVersionUID = 1L;

	private String locationId;
	private String handlingUnits;
	
	public SimpleLocationWithHandlingUnits(String locationId, String handlingUnits, boolean selected) {
		super(selected);
		this.locationId = locationId;
		this.handlingUnits = handlingUnits;
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
}
