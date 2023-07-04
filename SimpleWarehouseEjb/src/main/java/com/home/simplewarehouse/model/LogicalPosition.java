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
 * Position values for locations.
 */
@XmlRootElement(name = "LogicalPosition")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
//@Table(name="LOGICAL_POSITION")
@DiscriminatorValue("LOGICAL")
public class LogicalPosition extends Position {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(LogicalPosition.class);
    
	/**
	 * Position identifier
	 */
	@Basic(optional = true)
    @Column(name = "POSITION_ID", nullable = true, length = 80)
	private String positionId;

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
	 * Default constructor
	 */
	public LogicalPosition() {
		super();
		LOG.trace("--> LogicalPosition()");
		LOG.trace("<-- LogicalPosition()");
	}

	/**
	 * Constructor with location and position id
	 * 
	 * @param locationId the location id
	 * @param positionId the position id
	 */
	public LogicalPosition(String locationId, String positionId) {
		super(locationId);
		LOG.trace("--> LogicalPosition({}, {})", locationId, positionId);
		
		this.positionId = positionId;
		
		LOG.trace("<-- LogicalPosition()");
	}
	
	/**
	 * Constructor with position identifier
	 * 
	 * @param positionId the position identifier
	 */
	public LogicalPosition(String positionId) {
		super(positionId);
		LOG.trace("--> LogicalPosition({})", positionId);
		
		this.positionId = positionId;
		
		LOG.trace("<-- LogicalPosition()");
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
		if (getClass() != obj.getClass())
			return false;
		LogicalPosition other = (LogicalPosition) obj;
		return Objects.equals(positionId, other.positionId);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("LogicalPosition [")
		    .append("positionId=")
		    .append(positionId)
		    .append(", ")
			.append(super.toString())
			.append("]");
		
		return builder.toString();
	}
}