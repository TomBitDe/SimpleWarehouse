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
import javax.persistence.NamedQuery;
import static javax.persistence.LockModeType.NONE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Status values valid for all locations.
 */
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
    
    public static final ErrorStatus ERROR_STATUS_DEFAULT = ErrorStatus.NONE;
    public static final LtosStatus LTOS_STATUS_DEFAULT = LtosStatus.NO;
    public static final LockStatus LOCK_STATUS_DEFAULT = LockStatus.UNLOCKED;
    
	@Id
	private String locationId;
	
	/**
	 * Location error status
	 */
	@Basic(optional = false)
    @Column(name = "ERROR_STATUS")
	private String errorStatus;
	
	/**
	 * Location ltosStatus status (long time out of service)
	 */
	@Basic(optional = false)
	@Column(name = "LTOS_STATUS")
	private String ltosStatus;
	
	/**
	 * Lock status of the location
	 */
	@Basic(optional = false)
	@Column(name = "LOCK_STATUS")
	private String lockStatus;
	
    @Version
    private int version;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "LOCATION_ID")
    private Location location;
    
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
    	
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(USER_DEFAULT);
   }
    
    public LocationStatus(String locationId) {
    	super();
    	
    	this.locationId = locationId;
    	
    	setStatusesDefaults();
    	
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(USER_DEFAULT);
    }
    
    public LocationStatus(String locationId, String user) {
    	super();
    	
    	this.locationId = locationId;
    	
    	setStatusesDefaults();
    	
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(user);
    }
    
	public LocationStatus(String locationId, ErrorStatus errorStatus, LtosStatus ltosStatus, LockStatus lockStatus, String user) {
		super();
		
		this.locationId = locationId;
		
		if (errorStatus == null)
			this.errorStatus = ErrorStatus.NONE.name();
		else
		    this.errorStatus = errorStatus.name();
		
		if (ltosStatus == null)
			this.ltosStatus = LtosStatus.NO.name();
		else
		    this.ltosStatus = ltosStatus.name();
		
		if (lockStatus == null)
			this.lockStatus = LockStatus.UNLOCKED.name();
		else
			this.lockStatus = lockStatus.name();
		
		super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(user);
	}

	public String getLocationId() {
		LOG.debug(ID_FORMATTER, this.locationId);
		return this.locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
		LOG.debug(ID_FORMATTER, this.locationId);
	}

	public ErrorStatus getErrorStatus() {
		return ErrorStatus.valueOf(this.errorStatus);
	}

	public void setErrorStatus(ErrorStatus errorStatus) {
		if (errorStatus == null) {
			this.errorStatus = ErrorStatus.ERROR.name();
		}
		else {
			this.errorStatus = errorStatus.name();
		}
	}

	public LtosStatus getLtosStatus() {
		return LtosStatus.valueOf(this.ltosStatus);
	}

	public void setLtosStatus(LtosStatus ltosStatus) {
		if (ltosStatus == null) {
			this.ltosStatus = LtosStatus.YES.name();
		}
		else {
			this.ltosStatus = ltosStatus.name();
		}
	}
	
	public LockStatus getLockStatus() {
		return LockStatus.valueOf(lockStatus);
	}

	public void setLockStatus(LockStatus lockStatus) {
		if (lockStatus == null) {
			this.lockStatus = LockStatus.LOCKED.name();
		}
		else {
			this.lockStatus = lockStatus.name();
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
		LocationStatus other = (LocationStatus) obj;
		return Objects.equals(locationId, other.locationId) && version == other.version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("LocationStatus [")
		    .append("locationId=")
		    .append(locationId)
		    .append(", errorStatus=")
		    .append(errorStatus)
		    .append(", ltosStatus=")
		    .append(ltosStatus)
		    .append(", lockStatus=")
		    .append(lockStatus)
		    .append(", version=")
		    .append(version)
		    .append(", ")
			.append(super.toString())
			.append("]");
		
		return builder.toString();
	}
}
