package com.home.simplewarehouse.model;

import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Relative position values for locations.
 */
@XmlRootElement(name = "RelativPosition")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "RELATIV_POSITION")
@PrimaryKeyJoinColumn(name = "LOCATION_ID")
public class RelativPosition extends Position {
	private static final long serialVersionUID = 233237933470941803L;
	
	/**
	 * X coordinate in millimeter
	 */
	@Basic(optional = false)
    @Column(name = "X_RELATIV", nullable = false)
	int xCoord = 0;
	/**
	 * Y coordinate in millimeter
	 */
	@Basic(optional = false)
    @Column(name = "Y_RELATIV", nullable = false)
	int yCoord = 0;
	/**
	 * Z coordinate in millimeter
	 */
	@Basic(optional = false)
    @Column(name = "Z_RELATIV", nullable = false)
	int zCoord = 0;

    /**
     * Default Relative Position
     */
    public RelativPosition() {
    	super();
    }

    /**
     * Relative Position with coordinates
     * 
     * @param x the given x coordinate
     * @param y the given y coordinate
     * @param z the given z coordinate
     */
    public RelativPosition(int x, int y, int z) {
    	super();
    	setXCoord(x);
    	setYCoord(y);
    	setZCoord(z);
    }
    
	/**
	 * Gets the x coordinate
	 * 
	 * @return the xCoord
	 */
	public int getXCoord() {
		return xCoord;
	}

	/**
	 * Sets the x coordinate
	 * 
	 * @param xCoord the xCoord to set
	 */
	public void setXCoord(int xCoord) {
		this.xCoord = xCoord;
	}

	/**
	 * Gets the y coordinate
	 * 
	 * @return the yCoord
	 */
	public int getYCoord() {
		return yCoord;
	}

	/**
	 * Sets the y coordinate
	 * 
	 * @param yCoord the yCoord to set
	 */
	public void setYCoord(int yCoord) {
		this.yCoord = yCoord;
	}

	/**
	 * Gets the z coordinate
	 * 
	 * @return the zCoord
	 */
	public int getZCoord() {
		return zCoord;
	}

	/**
	 * Sets the z coordinate
	 * 
	 * @param zCoord the zCoord to set
	 */
	public void setZCoord(int zCoord) {
		this.zCoord = zCoord;
	}
	
	/**
	 * Sets the coordinates of the Position
	 * 
	 * @param x the x coordinate 
	 * @param y the y coordinate 
	 * @param z the z coordinate 
	 */
	public void setCoord(int x, int y, int z) {
		setXCoord(x);
		setYCoord(y);
		setZCoord(z);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(xCoord, yCoord, zCoord);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof RelativPosition))
			return false;
		RelativPosition other = (RelativPosition) obj;
		return xCoord == other.xCoord && yCoord == other.yCoord && zCoord == other.zCoord;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RelativPosition [ ")
		    .append(super.toString())
		    .append(", xCoord=").append(getXCoord())
		    .append(", yCoord=").append(getYCoord())
		    .append(", zCoord=").append(getZCoord())
		    .append("]");

		return builder.toString();
	}
}