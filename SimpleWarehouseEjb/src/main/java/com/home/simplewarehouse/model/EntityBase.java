package com.home.simplewarehouse.model;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Important attributes (content) for tracking when using entities.
 */
@MappedSuperclass
public class EntityBase {
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
    	this.updateTimestamp = new Timestamp(System.currentTimeMillis());
    	this.updateUserId = USER_DEFAULT;
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
			this.updateTimestamp = new Timestamp(System.currentTimeMillis());		}
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
