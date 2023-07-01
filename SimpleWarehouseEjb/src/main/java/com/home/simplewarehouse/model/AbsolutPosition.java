package com.home.simplewarehouse.model;

import java.io.Serializable;
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
public class AbsolutPosition extends Position implements Serializable {
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
     * Absolute Position with id
     * 
     * @param id the given id
     */
    public AbsolutPosition(String id) {
    	super(id);
    }
    
	/**
	 * @return the xCoord
	 */
	public float getxCoord() {
		return xCoord;
	}

	/**
	 * @param xCoord the xCoord to set
	 */
	public void setxCoord(long xCoord) {
		this.xCoord = xCoord;
	}

	/**
	 * @return the yCoord
	 */
	public float getyCoord() {
		return yCoord;
	}

	/**
	 * @param yCoord the yCoord to set
	 */
	public void setyCoord(long yCoord) {
		this.yCoord = yCoord;
	}

	/**
	 * @return the zCoord
	 */
	public float getzCoord() {
		return zCoord;
	}

	/**
	 * @param zCoord the zCoord to set
	 */
	public void setzCoord(long zCoord) {
		this.zCoord = zCoord;
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
		if (getClass() != obj.getClass())
			return false;
		AbsolutPosition other = (AbsolutPosition) obj;
		return xCoord == other.xCoord && yCoord == other.yCoord && zCoord == other.zCoord;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbsolutPosition [xCoord=").append(xCoord)
		    .append(", yCoord=").append(yCoord)
		    .append(", zCoord=").append(zCoord)
		    .append("]");

		return builder.toString();
	}
}