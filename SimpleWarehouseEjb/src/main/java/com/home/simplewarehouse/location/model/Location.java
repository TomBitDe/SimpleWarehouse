package com.home.simplewarehouse.location.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.util.model.EntityAudit;

/**
 * Any storage location.<br>
 * <br>
 * A capacity of 0 means that an undefined number of goods can be stored in the location.<br>
 * A maxWeight of 0 means that the weight is not relevant for storing goods in the location.<br>
 * A maxHeight of 0 means that the height is not relevant for storing goods in the location.<br>
 * A maxLength of 0 means that the length is not relevant for storing goods in the location.<br>
 * A maxWidth of 0 means that the width is not relevant for storing goods in the location.<br>
 */
@Entity
@Table(name="LOCATION")
public class Location extends EntityAudit {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(Location.class);

    @Id
    @Column(name = "ID")
    private String id;

    public String getId() {
    	LOG.debug("id=" + id);
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
        LOG.debug("id=" + id);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(id);
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
		Location other = (Location) obj;
		
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Location [id=").append(id).append("]");
		
		return builder.toString();
	}
}
