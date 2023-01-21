package com.home.simplewarehouse.model;

import static javax.persistence.LockModeType.NONE;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Location with access limit RANDOM.
 */
@Entity
@Table(name="LOCATION")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="ACCESS_LIMIT", discriminatorType=DiscriminatorType.STRING, length=20)
@DiscriminatorValue("RANDOM")
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
    
    @OneToMany( mappedBy="location"
    		, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }
    		, fetch = FetchType.EAGER )
	protected Set<HandlingUnit> handlingUnits = new HashSet<>();

    /**
     * Default Random Location
     */
    public Location() {
    	super();
    }
    
    /**
     * Random Location with id
     * 
     * @param id the given id
     */
    public Location(String id) {
    	super();
    	this.locationId = id;
    }
    
    /**
     * Random Location with id and user
     * 
     * @param id the given id
     * @param user the given user
     */
    public Location(String id, String user) {
    	super(user);
    	this.locationId = id;
    }
    
    /**
     * Random Location with id, user and timestamp
     * 
     * @param id the given id
     * @param user the given user
     * @param timestamp the given timestamp
     */
    public Location(String id, String user, Timestamp timestamp) {
    	super(user, timestamp);
    	this.locationId = id;
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

	/**
	 * Gets the Location status of this Location
	 * 
	 * @return the Location status
	 */
	public LocationStatus getLocationStatus() {
		return locationStatus;
	}

	/**
	 * Sets the Location status for this location
	 * 
	 * @param locationStatus the Location status
	 */
	public void setLocationStatus(LocationStatus locationStatus) {
		this.locationStatus = locationStatus;
	}

	/**
	 * Gets the Dimension assigned to this Location
	 * 
	 * @return the assigned Dimension
	 */
	public Dimension getDimension() {
		return dimension;
	}

	/**
	 * Assigns the Dimension to this Location
	 * 
	 * @param dimension the Dimension to assign
	 */
	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	/**
	 * Gets all HandlingUnits located on this Location
	 * 
	 * @return the List of HandlingUnits
	 */
	public List<HandlingUnit> getHandlingUnits() {
		LOG.trace("--> getHandlingUnits()");

		List<HandlingUnit> ret = new ArrayList<>();
		
		handlingUnits.forEach(ret::add);
		
		LOG.trace("<-- getHandlingUnits()");

		return ret;
	}

	/**
	 * Add this HandlingUnit to the Location
	 * 
	 * @param handlingUnit the HandlingUnit
	 * 
	 * @return true if add succeeded, else false
	 */
	public boolean addHandlingUnit(HandlingUnit handlingUnit) {
		LOG.trace("--> addHandlingUnit()");

		handlingUnit.setLocation(this);
		handlingUnit.setLocaPos(null);
		
		LOG.trace("<-- addHandlingUnit()");
		
		return this.handlingUnits.add(handlingUnit);
	}
	
	/**
	 * Remove this HandlingUnit from the Location
	 * 
	 * @param handlingUnit the HandlingUnit
	 * 
	 * @return true if add succeeded, else false
	 */
	public boolean removeHandlingUnit(HandlingUnit handlingUnit) {
		LOG.trace("--> removeHandlingUnit()");
		
		boolean b = handlingUnits.remove( handlingUnit );
	    
		if ( b ) {
			handlingUnit.setLocation(null);
			handlingUnit.setLocaPos(null);
		}
	    
		LOG.trace("<-- removeHandlingUnit()");

		return b;
	}
	
	/**
	 * Gets all the HandlingUnits possible to Pick from the Location
	 * 
	 * @return the List of HandlingUnits
	 */
	public List<HandlingUnit> getAvailablePicks() {
		LOG.trace("--> getAvailablePicks()");

		List<HandlingUnit> ret = getHandlingUnits().stream().collect( Collectors.toList() );
		
		LOG.trace("<-- getAvailablePicks()");

		return ret;
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
	
	protected String toString(List<HandlingUnit> list) {
		StringBuilder builder = new StringBuilder();

		builder.append("RANDOM=[");
		
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
		builder.append("Location [")
			.append(System.lineSeparator() + "\tlocationId=")
		    .append(locationId)
		    .append(", version=")
		    .append(version)
		    .append(", " + System.lineSeparator() + '\t' + '\t')
		    .append(locationStatus)
		    .append(", " + System.lineSeparator() + '\t' + '\t')
		    .append(dimension)
			.append(", " + System.lineSeparator() + '\t' + '\t')
			.append("HandlingUnits ")
			.append(toString(getHandlingUnits()))
			.append(", " + System.lineSeparator() + '\t')
			.append(super.toString())
			.append("]");
		
		return builder.toString();
	}
}
