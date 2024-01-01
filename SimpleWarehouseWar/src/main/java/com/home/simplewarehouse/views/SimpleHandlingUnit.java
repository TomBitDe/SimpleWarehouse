package com.home.simplewarehouse.views;

import java.io.Serializable;

/**
 * Simplified view on HandingUnit with selection.
 */
public class SimpleHandlingUnit extends SelectableView implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The handling units id
	 */
	private String id;

    /**
     * Constructor is mandatory
     * 
     * @param id the handlingunit id
     * @param selected the selection flag
     */
	public SimpleHandlingUnit(String id, boolean selected) {
		super(selected);
		this.id = id;
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
}
