package com.home.simplewarehouse.views;

import java.io.Serializable;

/**
 * Simplified view on Location with selection.
 */
public class SimpleZone extends SelectableView implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The zone id
	 */
	private String id;
	private int rating;
	
	/**
	 * Constructor is mandatory
	 * 
	 * @param locationId the location id
	 * @param selected the selection flag
	 */
	public SimpleZone(String id, boolean selected) {
		super(selected);
		this.id = id;
	}

	/**
	 * Gets the zone id
	 * 
	 * @return the zone id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the zone id
	 * 
	 * @param id the zone id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the zone rating
	 * 
	 * @return the zone rating
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * Sets the zone rating
	 * 
	 * @param rating the zone rating
	 */
	public void setRating(int rating) {
		this.rating = rating;
	}
}
