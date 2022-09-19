package com.home.simplewarehouse.handlingunit.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.location.model.Location;

/**
 * Any kind of handling unit.<br>
 * <br>
 */
@Entity
@Table(name="HANDLING_UNIT")
public class HandlingUnit implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(HandlingUnit.class);

    @Id
    @Column(name = "ID", nullable = false, length = 200)
    private String id;

    @Version
    private int version;
    
    @ManyToOne( cascade=CascadeType.PERSIST )
    private Location location;
    
    public HandlingUnit() {
    	super();
    }
    
    public HandlingUnit(String id) {
    	super();
    	this.id = id;
    }

    public String getId() {
    	LOG.debug("id=" + id);
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    	LOG.debug("id=" + id);
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
		return Objects.hash(id, location);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HandlingUnit other = (HandlingUnit) obj;
		return Objects.equals(id, other.id) && Objects.equals(location, other.location);
	}
}
