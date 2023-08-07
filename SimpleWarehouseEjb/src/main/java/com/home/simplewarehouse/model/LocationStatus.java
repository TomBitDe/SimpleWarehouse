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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Status values valid for all locations.
 */
@XmlRootElement(name = "LocationStatus")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name="LOCATION_STATUS")
@NamedQuery(name = "findAllLocationStatuses", query = "select l from LocationStatus l", lockMode = NONE)
@NamedQuery(name = "findAllLocationWithErrorStatus", query = "select l.location from LocationStatus l where l.errorStatus = ?1", lockMode = NONE) 
@NamedQuery(name = "findAllLocationWithLtosStatus", query = "select l.location from LocationStatus l where l.ltosStatus = ?1", lockMode = NONE) 
@NamedQuery(name = "findAllLocationWithLockStatus", query = "select l.location from LocationStatus l where l.lockStatus = ?1", lockMode = NONE) 
public class LocationStatus extends EntityBase implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(LocationStatus.class);
    
    private static final String ID_FORMATTER = "locationId={0}";
    
    /**
     * The default ErrorStatus
     */
    public static final ErrorStatus ERROR_STATUS_DEFAULT = ErrorStatus.NONE;
    /**
     * The default LtosStatus
     */
    public static final LtosStatus LTOS_STATUS_DEFAULT = LtosStatus.NO;
    /**
     * The default LockStatus
     */
    public static final LockStatus LOCK_STATUS_DEFAULT = LockStatus.UNLOCKED;
    
    /**
     * The id of the associated Location
     */
	@Id
	private String locationId;
	/**
	 * Location error status
	 */
	@Basic(optional = false)
    @Column(name = "ERROR_STATUS", nullable = false, length = 80)
	private String errorStatus;
	/**
	 * Location ltosStatus status (long time out of service)
	 */
	@Basic(optional = false)
	@Column(name = "LTOS_STATUS", nullable = false, length = 80)
	private String ltosStatus;
	/**
	 * Lock status of the location
	 */
	@Basic(optional = false)
	@Column(name = "LOCK_STATUS", nullable = false, length = 80)
	private String lockStatus;
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
     * Sets the default values for all statuses
     */
    private void setStatusesDefaults() {
    	this.errorStatus = ERROR_STATUS_DEFAULT.name();
    	this.ltosStatus = LTOS_STATUS_DEFAULT.name();
    	this.lockStatus = LOCK_STATUS_DEFAULT.name();
    }
 
    /**
     * Set the defaults for the location status in default constructor
     */
    public LocationStatus() {
    	super();
    	
    	setStatusesDefaults();
    }
    
    /**
     * Create this LocationStatus
     * 
     * @param locationId the given location id
     */
    public LocationStatus(String locationId) {
    	super();
    	
    	this.locationId = locationId;
    	
    	setStatusesDefaults();
    }
    
    /**
     * Create this LocationStatus
     * 
     * @param locationId the given location id
     * @param user the given user name
     */
    public LocationStatus(String locationId, String user) {
    	super(user);
    	
    	this.locationId = locationId;
    	
    	setStatusesDefaults();
    }
    
    /**
     * Create this LocationStatus
     * 
     * @param locationId the given location id
     * @param errorStatus the given error status
     * @param ltosStatus the given ltos status
     * @param lockStatus the given lock status
     * @param user the given user name
     */
	public LocationStatus(String locationId, ErrorStatus errorStatus, LtosStatus ltosStatus, LockStatus lockStatus, String user) {
		super(user);
		
		this.locationId = locationId;
		
		if (errorStatus == null) {
			String defVal = ERROR_STATUS_DEFAULT.name();
			
			LOG.info("Invalid parameter errorStatus ({}); set DEFAULT value ({})", errorStatus
					, defVal);
			this.errorStatus = defVal;
		}
		else {
		    this.errorStatus = errorStatus.name();
		}
		
		if (ltosStatus == null) {
			String defVal = LTOS_STATUS_DEFAULT.name();
			
			LOG.info("Invalid parameter ltosStatus ({}); set DEFAULT value ({})", ltosStatus
					, defVal);
			this.ltosStatus = defVal;
		}
		else {
		    this.ltosStatus = ltosStatus.name();
		}
		
		if (lockStatus == null) {
			String defVal = LOCK_STATUS_DEFAULT.name();
			
			LOG.info("Invalid parameter lockStatus ({}); set DEFAULT value ({})", lockStatus
					, defVal);
			this.lockStatus = defVal;
		}
		else {
			this.lockStatus = lockStatus.name();
		}
	}

	/**
	 * Gets the Location id
	 * 
	 * @return the id
	 */
	public String getLocationId() {
		LOG.debug(ID_FORMATTER, this.locationId);
		return this.locationId;
	}

	/**
	 * Sets the Location id
	 * 
	 * @param locationId the id
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
		LOG.debug(ID_FORMATTER, this.locationId);
	}

	/**
	 * Gets the ErrorStatus
	 * 
	 * @return the ErrorStatus
	 */
	public ErrorStatus getErrorStatus() {
		return ErrorStatus.valueOf(this.errorStatus);
	}

	/**
	 * Sets the ErrorStatus
	 * 
	 * @param errorStatus the given ErrorStatus, if <code>null</code> then ERROR
	 */
	public void setErrorStatus(ErrorStatus errorStatus) {
		if (errorStatus == null) {
			this.errorStatus = ErrorStatus.ERROR.name();
		}
		else {
			this.errorStatus = errorStatus.name();
		}
	}

	/**
	 * Gets the LtosStatus
	 * 
	 * @return the LtosStatus
	 */
	public LtosStatus getLtosStatus() {
		return LtosStatus.valueOf(this.ltosStatus);
	}

	/**
	 * Sets the LtosStatus
	 * 
	 * @param ltosStatus the given LtosStatus, if <code>null</code> then YES
	 */
	public void setLtosStatus(LtosStatus ltosStatus) {
		if (ltosStatus == null) {
			this.ltosStatus = LtosStatus.YES.name();
		}
		else {
			this.ltosStatus = ltosStatus.name();
		}
	}
	
	/**
	 * Gets the LockStatus
	 * 
	 * @return the LockStatus
	 */
	public LockStatus getLockStatus() {
		return LockStatus.valueOf(lockStatus);
	}

	/**
	 * Sets the LockStatus
	 * 
	 * @param lockStatus the given LockStatus, if <code>null</code> then LOCKED
	 */
	public void setLockStatus(LockStatus lockStatus) {
		if (lockStatus == null) {
			this.lockStatus = LockStatus.LOCKED.name();
		}
		else {
			this.lockStatus = lockStatus.name();
		}
	}

	/**
	 * Gets the version
	 * 
	 * @return the version number
	 */
	private int getVersion() {
		return version;
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
		LocationStatus other = (LocationStatus) obj;
		return Objects.equals(locationId, other.locationId) && version == other.version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("LocationStatus [")
		    .append("locationId=")
		    .append(getLocationId())
		    .append(", errorStatus=")
		    .append(getErrorStatus())
		    .append(", ltosStatus=")
		    .append(getLtosStatus())
		    .append(", lockStatus=")
		    .append(getLockStatus())
		    .append(", version=")
		    .append(getVersion())
		    .append(", ")
			.append(super.toString())
			.append("]");
		
		return builder.toString();
	}
}
