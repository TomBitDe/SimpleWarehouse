package com.home.simplewarehouse.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Any kind of handling unit.<br>
 * <br>
 */
@Entity
@Table(name="HANDLING_UNIT")
public class HandlingUnit extends EntityBase implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(HandlingUnit.class);

    @Id
    @Column(name = "ID", nullable = false, length = 200)
    private String id;

    @Version
    private int version;
    
    @ManyToOne( cascade=CascadeType.PERSIST )
    private Location location;
    
    public HandlingUnit() {
    	super();
    }
    
    public HandlingUnit(String id) {
    	super();
    	this.id = id;
    }

    public HandlingUnit(String id, String user) {
    	super();
    	this.id = id;
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(user);
    }
    
    public HandlingUnit(String id, String user, Timestamp timestamp) {
    	super();
    	this.id = id;
    	super.setUpdateTimestamp(timestamp);
    	super.setUpdateUserId(user);
    }

    public String getId() {
    	LOG.debug("id=" + id);
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    	LOG.debug("id=" + id);
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

	protected void setLocation(Location location) {
		this.location = location;
	}
	
	public void dropTo(Location location) {
		if (location == null) {
			throw new IllegalArgumentException("Location is null");
		}
		else {
			setLocation(location);
			location.addHandlingUnit(this);
		}
	}
	
	public void pickFrom(Location location) throws LocationIsEmptyException, HandlingUnitNotOnLocationException {
		if (location == null) {
			throw new IllegalArgumentException("Location is null");
		}
		if (location.getHandlingUnits() == null) {
			throw new IllegalStateException("Location has illegal state (HandlingUnits is null)");
		}
		if (location.getHandlingUnits().isEmpty()) {
			throw new LocationIsEmptyException("Location [" + location.getId() + "] is EMPTY");
		}
		
		if (! location.removeHandlingUnit(this)) {
			throw new HandlingUnitNotOnLocationException("Handling unit not on Location [" + location.getId() + ']');
		}
		
		setLocation(null);
	}

	@Override
	public int hashCode() {
		// Only id; this is a must. Otherwise stack overflow
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HandlingUnit other = (HandlingUnit) obj;
		
		// Only id; this is a must. Otherwise stack overflow
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HandlingUnit [id=")
		    .append(id)
		    .append(", version=")
		    .append(version)
		    .append(", location=")
			.append(location)
			.append(", ")
			.append(super.toString())			
			.append("]");
		
		return builder.toString();
	}
	
	
}
