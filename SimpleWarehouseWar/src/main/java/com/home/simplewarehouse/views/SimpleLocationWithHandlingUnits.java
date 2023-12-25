package com.home.simplewarehouse.views;

public class SimpleLocationWithHandlingUnits {
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
