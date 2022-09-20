package com.home.simplewarehouse.util.model;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Important attributes (content) for tracking when using entities.
 */
@MappedSuperclass
public class EntityBase {
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
    
	public Timestamp getUpdateTimestamp() {
		return updateTimestamp;
	}
	public void setUpdateTimestamp(Timestamp updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}
	
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
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
