package com.home.simplewarehouse.model;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Important attributes (content) for tracking when using entities.
 */
@MappedSuperclass
public class EntityBase {
	private static final Logger LOG = LogManager.getLogger(EntityBase.class);
	
	/**
	 * The user default value
	 */
	public static final String USER_DEFAULT = "System";
	
    /**
     * The update time stamp of the entity
     */
    @Basic(optional = false)
    @Column(name = "UPDATE_TS", nullable = false)
    private Timestamp updateTimestamp;
    /**
     * The user id of the user who has done the last update
     */
    @Basic(optional = false)
    @Column(name = "UPDATE_USER", nullable = false)
    private String updateUserId;
    
    /**
     * Set the defaults for the entity base in default constructor
     */
    public EntityBase() {
    	this.updateUserId = USER_DEFAULT;
    	this.updateTimestamp = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Entity base with user
     * 
     * @param user the given user
     */
    public EntityBase(String user) {
    	setUpdateUserId(user);
    	this.updateTimestamp = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Entity base with user and timestamp
     * 
     * @param user the given user
     * @param timestamp the given timestamp
     */
    public EntityBase(String user, Timestamp timestamp) {
    	setUpdateUserId(user);
    	setUpdateTimestamp(timestamp);
    }
    
    /**
     * Get the time stamp of the last update
     * 
     * @return the time stamp
     */
	public Timestamp getUpdateTimestamp() {
		return updateTimestamp;
	}
	
	/**
	 * Set the time stamp of the last update
	 * 
	 * @param updateTimestamp the time stamp
	 */
	public void setUpdateTimestamp(Timestamp updateTimestamp) {
		if (updateTimestamp == null) {
			Timestamp defaultTimestamp = new Timestamp(System.currentTimeMillis());
			
			LOG.info("Invalid parameter updateTimestamp ({}); set DEFAULT value ({})", updateTimestamp
					, defaultTimestamp);
			this.updateTimestamp = defaultTimestamp;		}
		else {
			this.updateTimestamp = updateTimestamp;
		}
	}
	
	/**
	 * Get the user id of the last update
	 * 
	 * @return the user id
	 */
	public String getUpdateUserId() {
		return updateUserId;
	}
	
	/**
	 * Set the user id of the last update
	 * 
	 * @param updateUserId the user id
	 */
	public void setUpdateUserId(String updateUserId) {
		if (updateUserId == null) {
			LOG.info("Invalid parameter updateUserId ({}); set DEFAULT value ({})", updateUserId
					, USER_DEFAULT);
			this.updateUserId = USER_DEFAULT;
		}
		else {
		    this.updateUserId = updateUserId;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("updateTimestamp=")
		    .append(updateTimestamp)
			.append(", updateUserId=")
			.append(updateUserId);
		
		return builder.toString();
	}
}
