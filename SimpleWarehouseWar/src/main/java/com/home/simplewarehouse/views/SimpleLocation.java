package com.home.simplewarehouse.views;

import java.io.Serializable;

/**
 * Simplified view on Location with selection.
 */
public class SimpleLocation implements Serializable {
	private static final long serialVersionUID = 1L;

	private String locationId;
	private boolean selected;
	
	public SimpleLocation(String locationId, boolean selected) {
		super();
		this.locationId = locationId;
		this.selected = selected;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
