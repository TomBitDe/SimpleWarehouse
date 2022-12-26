package com.home.simplewarehouse.model;

import static javax.persistence.LockModeType.NONE;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Any dimension.
 * <p>
 * A maxCapacity of 0 means that an undefined number of goods can be stored in the location.<br>
 * A maxWeight of 0 means that the weight is not relevant for storing goods in the location.<br>
 * A maxHeight of 0 means that the height is not relevant for storing goods in the location.<br>
 * A maxLength of 0 means that the length is not relevant for storing goods in the location.<br>
 * A maxWidth of 0 means that the width is not relevant for storing goods in the location.<br>
 */
@Entity
@Table(name="DIMENSION")
@NamedQuery(name = "findAllDimensions", query = "select d from Dimension d", lockMode = NONE)
public class Dimension extends EntityBase implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(Dimension.class);
    
    private static final String ID_FORMATTER = "locationId={0}";
    
    public static final int CAPACITY_DEFAULT = 0;
    public static final int WEIGHT_DEFAULT = 0;
    
	@Id
	private String locationId;
	
	@Basic(optional = false)
	@Column(name = "MAX_CAPACITY", nullable = false)
	private int maxCapacity;
		
	@Basic(optional = false)
	@Column(name = "MAX_WEIGHT", nullable = false)
	private int maxWeight;
		
    @Version
    private int version;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "LOCATION_ID")
    private Location location;
    
    private void setDimensionDefaults() {
    	this.maxCapacity = CAPACITY_DEFAULT;
    	this.maxWeight = WEIGHT_DEFAULT;
    }
 
    /**
     * Set the defaults for the dimension in default constructor
     */
    public Dimension() {
    	super();
    	
    	setDimensionDefaults();
    }
    
    public Dimension(String locationId) {
    	super();
    	
    	this.locationId = locationId;
    	
    	setDimensionDefaults();
    }
    
    public Dimension(String locationId, String user) {
    	super(user);
    	
    	this.locationId = locationId;
    	
    	setDimensionDefaults();
    }
    
	public Dimension(String locationId, int maxCapacity, String user) {
		super(user);
		
		this.locationId = locationId;
		
    	setDimensionDefaults();

    	setMaxCapacity(maxCapacity);
    }

	public Dimension(String locationId, int maxCapacity, int maxWeight, String user) {
		super(user);
		
		this.locationId = locationId;
		
    	setDimensionDefaults();

    	setMaxCapacity(maxCapacity);
    	setMaxWeight(maxWeight);
	}

	public String getLocationId() {
		LOG.debug(ID_FORMATTER, this.locationId);
		return this.locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
		LOG.debug(ID_FORMATTER, this.locationId);
	}

	public int getMaxCapacity() {
		return this.maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
    	if (maxCapacity < 0) {
			LOG.info("Invalid parameter maxCapacity ({}); keep DEFAULT value ({})", maxCapacity
					, CAPACITY_DEFAULT);
		}
		else {
		    this.maxCapacity = maxCapacity;
		}
	}

	public int getMaxWeight() {
		return this.maxWeight;
	}

	public void setMaxWeight(int maxWeight) {
    	if (maxWeight < 0) {
			LOG.info("Invalid parameter maxWeight ({}); keep DEFAULT value ({})", maxWeight
					, WEIGHT_DEFAULT);
		}
		else {
		    this.maxWeight = maxWeight;
		}
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
		Dimension other = (Dimension) obj;
		return Objects.equals(locationId, other.locationId) && version == other.version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Dimension [")
		    .append("locationId=")
		    .append(locationId)
		    .append(", maxCapacity=")
		    .append(maxCapacity)
		    .append(", version=")
		    .append(version)
		    .append(", ")
			.append(super.toString())
			.append("]");
		
		return builder.toString();
	}
}
