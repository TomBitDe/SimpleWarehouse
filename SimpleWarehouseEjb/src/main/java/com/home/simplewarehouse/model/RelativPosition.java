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
 * Relative position values for locations.
 */
@XmlRootElement(name = "RelativPosition")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@DiscriminatorValue("RELATIV")
public class RelativPosition extends Position {
    private static final long serialVersionUID = 1L;
    
	/**
	 * X coordinate in millimeter
	 */
	@Basic(optional = true)
    @Column(name = "X_RELATIV", nullable = true)
	int xCoord;
	/**
	 * Y coordinate in millimeter
	 */
	@Basic(optional = true)
    @Column(name = "Y_RELATIV", nullable = true)
	int yCoord;
	/**
	 * Z coordinate in millimeter
	 */
	@Basic(optional = true)
    @Column(name = "Z_RELATIV", nullable = true)
	int zCoord;

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
    	setxCoord(x);
    	setyCoord(y);
    	setzCoord(z);
    }
    
	/**
	 * Gets the x coordinate
	 * 
	 * @return the xCoord
	 */
	public int getxCoord() {
		return xCoord;
	}

	/**
	 * Sets the x coordinate
	 * 
	 * @param xCoord the xCoord to set
	 */
	public void setxCoord(int xCoord) {
		this.xCoord = xCoord;
	}

	/**
	 * Gets the y coordinate
	 * 
	 * @return the yCoord
	 */
	public int getyCoord() {
		return yCoord;
	}

	/**
	 * Sets the y coordinate
	 * 
	 * @param yCoord the yCoord to set
	 */
	public void setyCoord(int yCoord) {
		this.yCoord = yCoord;
	}

	/**
	 * Gets the z coordinate
	 * 
	 * @return the zCoord
	 */
	public int getzCoord() {
		return zCoord;
	}

	/**
	 * Sets the z coordinate
	 * 
	 * @param zCoord the zCoord to set
	 */
	public void setzCoord(int zCoord) {
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
		    .append(", xCoord=").append(getxCoord())
		    .append(", yCoord=").append(getyCoord())
		    .append(", zCoord=").append(getzCoord())
		    .append("]");

		return builder.toString();
	}
}