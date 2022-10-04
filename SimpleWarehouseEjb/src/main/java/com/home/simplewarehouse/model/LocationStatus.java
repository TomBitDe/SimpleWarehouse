package com.home.simplewarehouse.model;

import java.io.Serializable;
import java.sql.Timestamp;

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
 * Status values valid for all locations.
 */
@Entity
@Table(name="LOCATION_STATUS")
public class LocationStatus extends EntityBase implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(LocationStatus.class);
    
	@Id
	private String locationId;
	
	/**
	 * Location error status
	 */
	@Basic(optional = false)
    @Column(name = "ERROR_STATUS")
	private String errorStatus;
	
	/**
	 * Location ltos status (long time out of service)
	 */
	@Basic(optional = false)
	@Column(name = "LTOS")
	private String ltos;
	
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
 
    /**
     * Set the defaults for the location status in default constructor
     */
    public LocationStatus() {
    }
    
    public LocationStatus(String id) {
    	super();
    	
    	this.locationId = id;
    	
    	this.errorStatus = ErrorStatus.NONE.name();
    	this.ltos = Ltos.NO.name();
    	this.lockStatus = LockStatus.UNLOCKED.name();
    	
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId("System");
    }
    
	public String getLocationId() {
		LOG.debug("locationId=" + this.locationId);
		return this.locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
		LOG.debug("locationId=" + this.locationId);
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

	public Ltos getLtos() {
		return Ltos.valueOf(this.ltos);
	}

	public void setLtos(Ltos ltos) {
		if (ltos == null) {
			this.ltos = Ltos.YES.name();
		}
		else {
			this.ltos = ltos.name();
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("LocationStatus [")
		    .append("locationId=")
		    .append(locationId)
		    .append(", errorStatus=")
		    .append(errorStatus)
		    .append(", ltos=")
		    .append(ltos)
		    .append(", lockStatus=")
		    .append(lockStatus)
		    .append("]");
		
		return builder.toString();
	}
}