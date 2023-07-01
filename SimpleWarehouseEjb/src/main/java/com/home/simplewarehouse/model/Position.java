package com.home.simplewarehouse.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Position values for locations.
 */
@XmlRootElement(name = "Position")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name="POSITION")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE", discriminatorType=DiscriminatorType.STRING, length=20)
@DiscriminatorValue("LOGICAL")
public class Position extends EntityBase implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(Position.class);
    
    /**
     * The id of the associated Location
     */
	@Id
	private String locationId;
	/**
	 * Position identifier
	 */
	@Basic(optional = true)
    @Column(name = "POSITION_ID", nullable = true, length = 80)
	private String positionId;
	/**
	 * Version number for optimistic locking
	 */
    @Version
    private int version;
    
    /**
     * The associated location
     */
    @OneToOne
    @MapsId
    @JoinColumn(name = "LOCATION_ID")
    @XmlTransient
    private Location location;

    /**
     * Gets the position
     * 
     * @return the position id
     */
    public String getPositionId() {
    	return positionId;
    }
    /**
     * Sets the position id
     */
    public void setPositionId(String id) {
    	this.positionId = id;
    }
    
    /**
	 * Gets the version
	 * 
	 * @return the version number
	 */
	public int getVersion() {
		return version;
	}

	protected void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Default constructor
	 */
	public Position() {
		super();
		LOG.trace("--> Position()");
		LOG.trace("<-- Position()");
	}

	/**
	 * Default with id
	 * 
	 * @param locationId
	 */
	public Position(String locationId) {
		super();
		LOG.trace("--> Position({})", locationId);

		this.locationId = locationId;
		this.positionId = locationId;

		LOG.trace("<-- Position()");
	}

	/**
	 * Gets the Location
	 * 
	 * @return the Location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Sets the Location
	 * 
	 * @param location the given Location
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(locationId, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		return Objects.equals(locationId, other.locationId) && version == other.version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Position [")
		    .append("locationId=")
		    .append(locationId)
		    .append("positionId=")
		    .append(positionId)
		    .append(", version=")
		    .append(version)
		    .append(", ")
			.append(super.toString())
			.append("]");
		
		return builder.toString();
	}
}