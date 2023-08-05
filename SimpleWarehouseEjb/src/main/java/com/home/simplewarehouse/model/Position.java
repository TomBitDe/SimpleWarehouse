package com.home.simplewarehouse.model;

import java.io.Serializable;
import java.util.Objects;

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
 * Position for locations.
 */
@XmlRootElement(name = "Position")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name="POSITION")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE", discriminatorType=DiscriminatorType.STRING, length=20)
@DiscriminatorValue("NONE")
public class Position implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(Position.class);
    
    /**
     * The id of the associated Location
     */
	@Id
	private String locationId;
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
	 * Default constructor
	 */
	public Position() {
		super();
		LOG.trace("--> Position()");
		LOG.trace("<-- Position()");
	}

	/**
	 * Default with location id
	 * 
	 * @param locationId the location id
	 */
	public Position(String locationId) {
		super();
		LOG.trace("--> Position({})", locationId);

		this.locationId = locationId;

		LOG.trace("<-- Position()");
	}
	
	/**
	 * Gets the location id
	 * 
	 * @return the location id
	 */
	public String getLocationId() {
		return this.locationId;
	}
	
	/**
	 * Sets the location id
	 * 
	 * @param id the location id
	 */
	public void setLocationId(String id) {
		this.locationId = id;
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
		    .append(getLocationId())
		    .append(", version=")
		    .append(getVersion())
		    .append(", ")
			.append(super.toString())
			.append("]");
		
		return builder.toString();
	}
}