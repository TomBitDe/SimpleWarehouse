package com.home.simplewarehouse.views;

import java.io.Serializable;

public class SimpleHandlingUnit implements Serializable{
	private static final long serialVersionUID = 1L;

	private String id;
    private boolean selected;

	public SimpleHandlingUnit(String id, boolean selected) {
		super();
		this.id = id;
		this.selected = selected;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
