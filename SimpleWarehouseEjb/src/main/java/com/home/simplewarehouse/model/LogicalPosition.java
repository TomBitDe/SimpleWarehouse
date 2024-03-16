package com.home.simplewarehouse.model;

import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logical position for locations (just a position id).
 */
@XmlRootElement(name = "LogicalPosition")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@DiscriminatorValue("LOGICAL")
public class LogicalPosition extends Position {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(LogicalPosition.class);
    
    private static final String DEFAULT_LOG_TRACE_EXIT = "<-- LogicalPosition()";
    
	/**
	 * Position identifier
	 */
	@Basic(optional = true)
    @Column(name = "POSITION_ID", nullable = true, length = 80)
	private String positionId;

	/**
	 * Default constructor
	 */
	public LogicalPosition() {
		super();
		LOG.trace("--> LogicalPosition()");
		LOG.trace(DEFAULT_LOG_TRACE_EXIT);
	}

	/**
	 * Constructor with position id
	 * 
	 * @param positionId the position id
	 */
	public LogicalPosition(String positionId) {
		LOG.trace("--> LogicalPosition({})", positionId);
		
		setPositionId(positionId);
		
		LOG.trace(DEFAULT_LOG_TRACE_EXIT);
	}
	
	/**
     * Gets the position
     * 
     * @return the position id
     */
    public String getPositionId() {
    	return positionId;
    }
    /**
     * Sets the position
     * 
     * @param id the position id
     */
    public void setPositionId(String id) {
    	this.positionId = id;
    }
    
    /**
     * Get the coordinate value
     * 
     * @return the coordinate
     */
    public String getCoord() {
    	return getPositionId();
    }
    
    /**
     * Sets the coordinate 
     * @param positionId the position id
     */
    public void setCoord(String positionId) {
    	setPositionId(positionId);
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(positionId);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof LogicalPosition))
			return false;
		LogicalPosition other = (LogicalPosition) obj;
		return Objects.equals(positionId, other.positionId);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("LogicalPosition [")
	        .append(super.toString())
		    .append(", positionId=")
		    .append(getPositionId())
			.append("]");
		
		return builder.toString();
	}
}