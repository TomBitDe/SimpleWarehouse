package com.home.simplewarehouse.views;

import java.io.Serializable;

public class SimpleLocationWithHandlingUnits implements Serializable {
	private static final long serialVersionUID = 1L;

	private String locationId;
	private String handlingUnits;
	
	public SimpleLocationWithHandlingUnits(String locationId, String handlingUnits) {
		super();
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
