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
//@Table(name="ABSOLUT_POSITION")
@DiscriminatorValue("ABSOLUT")
public class AbsolutPosition extends Position {
    private static final long serialVersionUID = 1L;
    
	/**
	 * X coordinate in millimeter
	 */
	@Basic(optional = true)
    @Column(name = "X_ABSOLUT", nullable = true)
	float xCoord;
	/**
	 * Y coordinate in millimeter
	 */
	@Basic(optional = true)
    @Column(name = "Y_ABSOLUT", nullable = true)
	float yCoord;
	/**
	 * Z coordinate in millimeter
	 */
	@Basic(optional = true)
    @Column(name = "Z_ABSOLUT", nullable = true)
	float zCoord;

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
    	setxCoord(x);
    	setyCoord(y);
    	setzCoord(z);
    }
    
	/**
	 * Gets the x coordinate
	 * 
	 * @return the xCoord
	 */
	public float getxCoord() {
		return xCoord;
	}

	/**
	 * Sets the x coordinate
	 * 
	 * @param xCoord the xCoord to set
	 */
	public void setxCoord(float xCoord) {
		this.xCoord = xCoord;
	}

	/**
	 * Gets the y coordinate
	 * 
	 * @return the yCoord
	 */
	public float getyCoord() {
		return yCoord;
	}

	/**
	 * Sets the y coordinate
	 * 
	 * @param yCoord the yCoord to set
	 */
	public void setyCoord(float yCoord) {
		this.yCoord = yCoord;
	}

	/**
	 * Gets the z coordinate
	 * 
	 * @return the zCoord
	 */
	public float getzCoord() {
		return zCoord;
	}

	/**
	 * Sets the z coordinate
	 * 
	 * @param zCoord the zCoord to set
	 */
	public void setzCoord(float zCoord) {
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
    	setxCoord(x);
    	setyCoord(y);
    	setzCoord(z);
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
	    .append(", xCoord=").append(getxCoord())
	    .append(", yCoord=").append(getyCoord())
	    .append(", zCoord=").append(getzCoord())
	    .append("]");

		return builder.toString();
	}
}