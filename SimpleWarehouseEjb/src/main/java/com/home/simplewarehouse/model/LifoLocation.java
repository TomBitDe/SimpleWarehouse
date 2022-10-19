package com.home.simplewarehouse.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@DiscriminatorValue("LIFO")
@Table(name = "LIFO_LOCATION")
public class LifoLocation extends Location implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(LifoLocation.class);
    
    @OneToMany( mappedBy="location"
    		, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }
    		, fetch = FetchType.EAGER )
	private Set<HandlingUnit> handlingUnits = new HashSet<>();

    public LifoLocation() {
    	super();
    }
    
    public LifoLocation(String id) {
    	super(id);
    }
    
    public LifoLocation(String id, String user) {
    	super(id, user);
    }
    
    public LifoLocation(String id, String user, Timestamp timestamp) {
    	super(id, user, timestamp);
    }

	@Override
	public List<HandlingUnit> getHandlingUnits() {
		LOG.trace("--> getHandlingUnits()");

		List<HandlingUnit> ret = new ArrayList<>();
		
		handlingUnits.forEach(ret::add);
		
		LOG.trace("<-- getHandlingUnits()");

		return ret;
	}

	@Override
	public boolean addHandlingUnit(HandlingUnit handlingUnit) {
		LOG.trace("--> addHandlingUnit()");

		handlingUnit.setLocation(this);
		
		LOG.trace("<-- addHandlingUnit()");
		
		return this.handlingUnits.add(handlingUnit);
	}
	
	@Override
	public boolean removeHandlingUnit(HandlingUnit handlingUnit) {
		LOG.trace("--> removeHandlingUnit()");
		
		boolean b = handlingUnits.remove( handlingUnit );
	    
		if ( b ) handlingUnit.setLocation(null);
	    
		LOG.trace("<-- removeHandlingUnit()");

		return b;
	}
}
