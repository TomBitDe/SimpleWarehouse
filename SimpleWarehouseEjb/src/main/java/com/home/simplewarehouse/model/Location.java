package com.home.simplewarehouse.model;

import static javax.persistence.LockModeType.NONE;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Any location.
 */
@Entity
@Table(name="LOCATION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="ACCESS_LIMIT", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("LOCATION")
@NamedQuery(name = "findAllLocations", query = "select l from Location l", lockMode = NONE)
public class Location extends EntityBase implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(Location.class);
    
    private static final String ID_FORMATTER = "locationId={0}";

    @Id
    @Column(name = "LOCATION_ID", nullable = false)
    private String locationId;
    
    @Version
    private int version;
    
    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "LOCATION_ID")
    private LocationStatus locationStatus;
    
    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "LOCATION_ID")
    private Dimension dimension;
    
    public Location() {
    	super();
    }
    
    public Location(String id) {
    	super();
    	this.locationId = id;
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(USER_DEFAULT);
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
    	LOG.debug(ID_FORMATTER, this.locationId);
        return this.locationId;
    }

    public void setLocationId(final String id) {
        this.locationId = id;
        LOG.debug(ID_FORMATTER, this.locationId);
    }

	public int getVersion() {
		return version;
	}

	protected void setVersion(int version) {
		this.version = version;
	}

	public LocationStatus getLocationStatus() {
		return locationStatus;
	}

	public void setLocationStatus(LocationStatus locationStatus) {
		this.locationStatus = locationStatus;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
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
	
	private String toString(List<HandlingUnit> list) {
		StringBuilder builder = new StringBuilder();

		builder.append("[");
		
		if (list == null) {
			builder.append("null");
		}
		else {
		    list.stream().forEach(item -> builder
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
		    .append(", ")
		    .append(locationStatus)
		    .append(", ")
		    .append(dimension)
		    .append(", handlingUnits=")
		    .append(toString(getHandlingUnits()))
			.append(", ")
			.append(super.toString())
			.append("]");
		
		return builder.toString();
	}

	public boolean addHandlingUnit(HandlingUnit handlingUnit) {
		return false;
	}

	public List<HandlingUnit> getHandlingUnits() {
		return new LinkedList<>();
	}

	public boolean removeHandlingUnit(HandlingUnit handlingUnit) {
		return false;
	}
}
