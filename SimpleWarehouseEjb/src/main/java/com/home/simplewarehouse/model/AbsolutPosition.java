package com.home.simplewarehouse.model;

import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Position values in millimeter for locations.
 */
@XmlRootElement(name = "AbsolutPosition")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@DiscriminatorValue("ABSOLUT")
public class AbsolutPosition extends Position {   
	private static final long serialVersionUID = 6651162041604613465L;
	/**
	 * X coordinate in millimeter
	 */
	@Basic(optional = false)
    @Column(name = "X_ABSOLUT", nullable = true)
	float xCoord = 0.0f;
	/**
	 * Y coordinate in millimeter
	 */
	@Basic(optional = false)
    @Column(name = "Y_ABSOLUT", nullable = true)
	float yCoord = 0.0f;
	/**
	 * Z coordinate in millimeter
	 */
	@Basic(optional = false)
    @Column(name = "Z_ABSOLUT", nullable = true)
	float zCoord = 0.0f;

    /**
     * Default Absolute Position
     */
    public AbsolutPosition() {
    	super();
    }

    /**
     * Absolute Position with coordinates
     * 
     * @param x the given x coordinate
     * @param y the given y coordinate
     * @param z the given z coordinate
     */
    public AbsolutPosition(float x, float y, float z) {
    	setXCoord(x);
    	setYCoord(y);
    	setZCoord(z);
    }
    
	/**
	 * Gets the x coordinate
	 * 
	 * @return the xCoord
	 */
	public float getXCoord() {
		return xCoord;
	}

	/**
	 * Sets the x coordinate
	 * 
	 * @param xCoord the xCoord to set
	 */
	public void setXCoord(float xCoord) {
		this.xCoord = xCoord;
	}

	/**
	 * Gets the y coordinate
	 * 
	 * @return the yCoord
	 */
	public float getYCoord() {
		return yCoord;
	}

	/**
	 * Sets the y coordinate
	 * 
	 * @param yCoord the yCoord to set
	 */
	public void setYCoord(float yCoord) {
		this.yCoord = yCoord;
	}

	/**
	 * Gets the z coordinate
	 * 
	 * @return the zCoord
	 */
	public float getZCoord() {
		return zCoord;
	}

	/**
	 * Sets the z coordinate
	 * 
	 * @param zCoord the zCoord to set
	 */
	public void setZCoord(float zCoord) {
		this.zCoord = zCoord;
	}

	/**
	 * Sets the coordinates of the Position
	 * 
	 * @param x the x coordinate 
	 * @param y the y coordinate 
	 * @param z the z coordinate 
	 */
	public void setCoord(float x, float y, float z) {
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
		if (!(obj instanceof AbsolutPosition))
			return false;
		AbsolutPosition other = (AbsolutPosition) obj;
		return Float.floatToIntBits(xCoord) == Float.floatToIntBits(other.xCoord)
				&& Float.floatToIntBits(yCoord) == Float.floatToIntBits(other.yCoord)
				&& Float.floatToIntBits(zCoord) == Float.floatToIntBits(other.zCoord);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbsolutPosition [ ")
	    .append(super.toString())
	    .append(", xCoord=").append(getXCoord())
	    .append(", yCoord=").append(getYCoord())
	    .append(", zCoord=").append(getZCoord())
	    .append("]");

		return builder.toString();
	}
}