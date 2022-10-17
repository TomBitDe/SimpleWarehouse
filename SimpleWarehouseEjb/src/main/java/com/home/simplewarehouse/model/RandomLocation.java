package com.home.simplewarehouse.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.LinkedList;
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
@DiscriminatorValue("RANDOM")
@Table(name = "RANDOM_LOCATION")
public class RandomLocation extends Location implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(RandomLocation.class);
    
    @OneToMany( mappedBy="location"
    		, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }
    		, fetch = FetchType.EAGER )
	private Set<HandlingUnit> handlingUnits = new HashSet<>();

    public RandomLocation() {
    	super();
    }

    public RandomLocation(String id) {
    	super();
    	super.setLocationId(id);
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(USER_DEFAULT);
    }
    
    public RandomLocation(String id, String user) {
    	super();
    	super.setLocationId(id);
    	super.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
    	super.setUpdateUserId(user);
    }
    
    public RandomLocation(String id, String user, Timestamp timestamp) {
    	super();
    	super.setLocationId(id);
    	super.setUpdateTimestamp(timestamp);
    	super.setUpdateUserId(user);
    }

    @Override
	public LinkedList<HandlingUnit> getHandlingUnits() {
		LOG.trace("--> getHandlingUnits()");

		LinkedList<HandlingUnit> ret = new LinkedList<>();
		
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
