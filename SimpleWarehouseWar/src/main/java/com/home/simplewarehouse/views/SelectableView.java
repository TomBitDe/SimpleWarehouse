package com.home.simplewarehouse.views;

/**
 * Class to make a view selectable.
 */
public class SelectableView {
	private boolean selected;

	/**
	 * Mandatory default constructor with selected false
	 */
	public SelectableView() {
		super();
		
		this.selected = false;
	}
	
	/**
	 * Constructor to set the selection flag
	 * 
	 * @param selected the selection flag
	 */
	public SelectableView(boolean selected) {
		this.selected = selected;
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
