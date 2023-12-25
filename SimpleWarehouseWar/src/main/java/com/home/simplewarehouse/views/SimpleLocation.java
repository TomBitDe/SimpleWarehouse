package com.home.simplewarehouse.views;

public class SimpleLocation {
	private String locationId;
	
	public SimpleLocation(String locationId) {
		super();
		this.locationId = locationId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
}
