package com.home.simplewarehouse.views;

import java.io.Serializable;

/**
 * Simplified view on HandingUnit with selection.
 */
public class SimpleHandlingUnit implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
    private boolean selected;

    /**
     * Constructor is mandatory
     * 
     * @param id the handlingunit id
     * @param selected the selection flag
     */
	public SimpleHandlingUnit(String id, boolean selected) {
		super();
		this.id = id;
		this.selected = selected;
	}

	/**
	 * Gets the id
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id
	 * 
	 * @param id the id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the selection status
	 * 
	 * @return the selection status
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets the selection status
	 * 
	 * @param selected the status
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
