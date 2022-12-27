package com.home.simplewarehouse.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import static javax.persistence.LockModeType.NONE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Any kind of handling unit.
 */
@Entity
@Table(name="HANDLING_UNIT")
@NamedQuery(name = "findAllHandlingUnits", query = "select h from HandlingUnit h", lockMode = NONE)
public class HandlingUnit extends EntityBase implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(HandlingUnit.class);
    
    private static final int WEIGHT_DEFAULT = 0;
    private static final String ID_FORMATTER = "id={0}";
    private static final String ID_WEIGHT_FORMATTER = "id={0} weight={1}";

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "LOCAPOS", nullable = true)
    private Integer locaPos = null;
    
    @Column(name = "WEIGHT", nullable = false)
    private Integer weight;
    
    @Version
    private int version;
    
    @ManyToOne(targetEntity = Location.class
    		, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }
    		, fetch = FetchType.LAZY) // LAZY for better performance)
	@JoinColumn(name = "LOCATION_ID", nullable = true)
    private Location location;
    
    public HandlingUnit() {
    	super();
    }
    
    public HandlingUnit(String id) {
    	super();
    	this.id = id;
    	setWeight(WEIGHT_DEFAULT);
    }

    public HandlingUnit(String id, int weight) {
    	super();
    	this.id = id;
    	setWeight(weight);
    }

    public HandlingUnit(String id, String user) {
    	super(user);
    	this.id = id;
    	setWeight(WEIGHT_DEFAULT);
    }
    
    public HandlingUnit(String id, String user, Timestamp timestamp) {
    	super(user, timestamp);
    	this.id = id;
    	setWeight(WEIGHT_DEFAULT);
    }

    public HandlingUnit(String id, int weight, String user, Timestamp timestamp) {
    	super(user, timestamp);
    	this.id = id;
    	setWeight(weight);
    }

    public String getId() {
    	LOG.debug(ID_FORMATTER, this.id);
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    	LOG.debug(ID_FORMATTER, this.id);
    }

    public int getWeight() {
    	LOG.debug(ID_WEIGHT_FORMATTER, this.id, this.weight);
        return this.weight;
    }

    public void setWeight(int weight) {
    	if (weight < 0) {
    		this.weight = WEIGHT_DEFAULT;
    	}
    	else {
            this.weight = weight;
    	}
    	LOG.debug(ID_WEIGHT_FORMATTER, this.id, this.weight);
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
	
	public Integer getLocaPos() {
		return locaPos;
	}

	public void setLocaPos(Integer locaPos) {
		this.locaPos = locaPos;
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
		builder.append("HandlingUnit [")
			.append(System.lineSeparator() + "\tid=")
		    .append(id)
		    .append(", weight=")
		    .append(weight)		    
		    .append(", locaPos=")
		    .append(locaPos == null ? "null" : locaPos)		    
		    .append(", version=")
		    .append(version)
		    .append(", ")
		    .append(location == null ? "Location=null" : location)
		    .append(", " + System.lineSeparator() + '\t')
			.append(super.toString())			
			.append("]");
		
		return builder.toString();
	}
	
	
}
