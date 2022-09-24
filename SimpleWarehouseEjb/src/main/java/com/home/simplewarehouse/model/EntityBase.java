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
    @Column(name = "UPDATE_TS")
    private Timestamp updateTimestamp;
    /**
     * The user id of the user who has done the last update
     */
    @Basic(optional = false)
    @Column(name = "UPDATE_USER")
    private String updateUserId;
    
    public EntityBase() {
    	this.updateTimestamp = new Timestamp(System.currentTimeMillis());
    	this.updateUserId = USER_DEFAULT;
    }
    
	public Timestamp getUpdateTimestamp() {
		return updateTimestamp;
	}
	public void setUpdateTimestamp(Timestamp updateTimestamp) {
		if (updateTimestamp == null) {
			updateTimestamp = new Timestamp(System.currentTimeMillis());		}
		else {
			this.updateTimestamp = updateTimestamp;
		}
	}
	
	public String getUpdateUserId() {
		return updateUserId;
	}
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
		
		builder.append("EntityAudit [updateTimestamp=")
		    .append(updateTimestamp)
			.append(", updateUserId=")
			.append(updateUserId)
		    .append("]");
		
		return builder.toString();
	}
}
