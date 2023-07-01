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
 * Position relative positions for locations.
 */
@XmlRootElement(name = "RelativPosition")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@DiscriminatorValue("RELATIV")
public class RelativPosition extends Position implements Serializable {
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
     * Default Absolute Position
     */
    public RelativPosition() {
    	super();
    }

    /**
     * Absolute Position with id
     * 
     * @param id the given id
     */
    public RelativPosition(String id) {
    	super(id);
    }
    
	/**
	 * @return the xCoord
	 */
	public int getxCoord() {
		return xCoord;
	}

	/**
	 * @param xCoord the xCoord to set
	 */
	public void setxCoord(int xCoord) {
		this.xCoord = xCoord;
	}

	/**
	 * @return the yCoord
	 */
	public int getyCoord() {
		return yCoord;
	}

	/**
	 * @param yCoord the yCoord to set
	 */
	public void setyCoord(int yCoord) {
		this.yCoord = yCoord;
	}

	/**
	 * @return the zCoord
	 */
	public int getzCoord() {
		return zCoord;
	}

	/**
	 * @param zCoord the zCoord to set
	 */
	public void setzCoord(int zCoord) {
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
		RelativPosition other = (RelativPosition) obj;
		return xCoord == other.xCoord && yCoord == other.yCoord && zCoord == other.zCoord;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RelativPosition [xCoord=").append(xCoord)
		    .append(", yCoord=").append(yCoord)
		    .append(", zCoord=").append(zCoord)
		    .append("]");

		return builder.toString();
	}
}