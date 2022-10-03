package com.home.simplewarehouse.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Any storage location.
 * <p>
 * A capacity of 0 means that an undefined number of goods can be stored in the location.<br>
 * A maxWeight of 0 means that the weight is not relevant for storing goods in the location.<br>
 * A maxHeight of 0 means that the height is not relevant for storing goods in the location.<br>
 * A maxLength of 0 means that the length is not relevant for storing goods in the location.<br>
 * A maxWidth of 0 means that the width is not relevant for storing goods in the location.<br>
 */
@Entity
@Table(name="LOCATION")
public class Location extends EntityBase implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(Location.class);

    @Id
    @Column(name = "LOCATION_ID", nullable = false, length = 200)
    private String locationId;
    
    @Version
    private int version;
    
    @OneToMany( mappedBy="location"
    		, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }
    		, fetch = FetchType.EAGER )
    private Set<HandlingUnit> handlingUnits = new HashSet<>();

    public Location() {
    	super();
    }
    
    public Location(String id) {
    	super();
    	this.locationId = id;
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId("System");
    }
    
    public Location(String id, String user) {
    	super();
    	this.locationId = id;
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(user);
    }
    
    public Location(String id, String user, Timestamp timestamp) {
    	super();
    	this.locationId = id;
    	super.setUpdateTimestamp(timestamp);
    	super.setUpdateUserId(user);
    }
    
    public String getLocationId() {
    	LOG.debug("locationId=" + locationId);
        return this.locationId;
    }

    public void setLocationId(final String id) {
        this.locationId = id;
        LOG.debug("locationId=" + id);
    }

	public int getVersion() {
		return version;
	}

	protected void setVersion(int version) {
		this.version = version;
	}

	public Set<HandlingUnit> getHandlingUnits() {
		return Collections.unmodifiableSet (handlingUnits);
	}

	public boolean addHandlingUnit(HandlingUnit handlingUnit) {
		handlingUnit.setLocation(this);
		
		return this.handlingUnits.add(handlingUnit);
	}
	
	public boolean removeHandlingUnit(HandlingUnit handlingUnit) {
		boolean b = handlingUnits.remove( handlingUnit );
	    
		if ( b ) handlingUnit.setLocation(null);
	    
		return b;
	}

	@Override
	public int hashCode() {
		// Only locationId; this is a must. Otherwise stack overflow
		return Objects.hash(locationId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		
		// Only locationId; this is a must. Otherwise stack overflow
		return Objects.equals(locationId, other.locationId);
	}
	
	private String toString(Set<HandlingUnit> handlingUnits) {
		StringBuilder builder = new StringBuilder();

		builder.append("[");
		
		if (handlingUnits == null) {
			builder.append("null");
		}
		else {
		    handlingUnits.stream().forEach(item -> builder
		    		.append('"')
		    		.append(item.getId())
		    		.append('"')
		    		.append(" "));
		}
		
		// Replace trailing " " by "]" (see above) or just append "]"
		int idx = builder.lastIndexOf(" ");
		if (idx > 0) {
		    builder.replace(idx, idx + 1, "]");
		}
		else {
		    builder.append("]");
		}
		
		return builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Location [locationId=")
		    .append(locationId)
		    .append(", version=")
		    .append(version)
		    .append(", handlingUnits=")
		    .append(toString(handlingUnits))
			.append(", ")
			.append(super.toString())
			.append("]");
		
		return builder.toString();
	}
	
}
