package com.home.simplewarehouse.handlingunit.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.util.model.EntityAudit;

/**
 * Any kind of handling unit.<br>
 * <br>
 */
@Entity
@Table(name="HANDLING_UNIT")
public class HandlingUnit extends EntityAudit {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(HandlingUnit.class);

    @Id
    @Column(name = "ID")
    private String id;

    public String getId() {
    	LOG.debug("id=" + id);
        return this.id;
    }

    public void setId(String id) {
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
		HandlingUnit other = (HandlingUnit) obj;
		
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HandlingUnit [id=").append(id).append("]");
		
		return builder.toString();
	}
}
