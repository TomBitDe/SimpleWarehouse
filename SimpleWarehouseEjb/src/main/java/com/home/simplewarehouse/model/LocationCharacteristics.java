package com.home.simplewarehouse.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Any location characteristics.
 * <p>
 * A capacity of 0 means that an undefined number of goods can be stored in the location.<br>
 * A maxWeight of 0 means that the weight is not relevant for storing goods in the location.<br>
 * A maxHeight of 0 means that the height is not relevant for storing goods in the location.<br>
 * A maxLength of 0 means that the length is not relevant for storing goods in the location.<br>
 * A maxWidth of 0 means that the width is not relevant for storing goods in the location.<br>
 */
@Entity
@Table(name="LOCATION_CHARACTERISTICS")
public class LocationCharacteristics extends EntityBase implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(LocationCharacteristics.class);
    
    public static final int CAPACITY_DEFAULT = 0;
    
	@Id
	private String locationId;
	
	@Basic(optional = false)
	@Column(name = "CAPACITY", nullable = false)
	private int capacity;
		
    @Version
    private int version;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "LOCATION_ID")
    private Location location;
    
    private void setCharacteristicsDefaults() {
    	this.capacity = 0;
    }
 
    /**
     * Set the defaults for the location characteristics in default constructor
     */
    public LocationCharacteristics() {
    	super();
    	
    	setCharacteristicsDefaults();
    	
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(USER_DEFAULT);
    }
    
    public LocationCharacteristics(String locationId) {
    	super();
    	
    	this.locationId = locationId;
    	
    	setCharacteristicsDefaults();
    	
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(USER_DEFAULT);
    }
    
    public LocationCharacteristics(String locationId, String user) {
    	super();
    	
    	this.locationId = locationId;
    	
    	setCharacteristicsDefaults();
    	
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(user);
    }
    
	public LocationCharacteristics(String locationId, int capacity, String user) {
		super();
		
		this.locationId = locationId;
		
		if (capacity < 0)
			this.capacity = 0;
		else
		    this.capacity = capacity;
		
		super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(user);
	}

	public String getLocationId() {
		LOG.debug("locationId=" + this.locationId);
		return this.locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
		LOG.debug("locationId=" + this.locationId);
	}

	public int getCapacity() {
		return this.capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getVersion() {
		return version;
	}

	protected void setVersion(int version) {
		this.version = version;
	}

	public Location getLocation() {
		return location;
	}

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
		LocationCharacteristics other = (LocationCharacteristics) obj;
		return Objects.equals(locationId, other.locationId) && version == other.version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("LocationStatus [")
		    .append("locationId=")
		    .append(locationId)
		    .append(", capacity=")
		    .append(capacity)
		    .append(", version=")
		    .append(version)
		    .append(", ")
			.append(super.toString())
			.append("]");
		
		return builder.toString();
	}
}
