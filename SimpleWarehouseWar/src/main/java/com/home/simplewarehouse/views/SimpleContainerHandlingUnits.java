package com.home.simplewarehouse.views;

import java.io.Serializable;

/**
 * Simplified view on HandingUnit compositions with selection.
 */
public class SimpleContainerHandlingUnits extends SelectableView implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The base id
	 */
	private String baseId;
	/**
	 * The composition handling units comma separated
	 */
	private String handlingUnits;
	
	/**
	 * Constructor with location, handlingunit and selected flag
	 * 
	 * @param baseId the handlingunit id
	 * @param handlingUnits the composition handlingunit ids
	 * @param selected the selected flag
	 */
	public SimpleContainerHandlingUnits(String baseId, String handlingUnits, boolean selected) {
		super(selected);
		this.baseId = baseId;
		this.handlingUnits = handlingUnits;
	}

	/**
	 * Gets the base handlingunit id
	 * 
	 * @return the id
	 */
	public String getBaseId() {
		return baseId;
	}

	/**
	 * Sets the base handlingunit id
	 * 
	 * @param baseId the id
	 */
	public void setBaseId(String baseId) {
		this.baseId = baseId;
	}

	/**
	 * Gets the composition handling units
	 * 
	 * @return the composition handling units
	 */
	public String getHandlingUnits() {
		return handlingUnits;
	}

	/**
	 * Sets the composition handling units
	 * 
	 * @param handlingUnits the composition handling units
	 */
	public void setHandlingUnits(String handlingUnits) {
		this.handlingUnits = handlingUnits;
	}
}
