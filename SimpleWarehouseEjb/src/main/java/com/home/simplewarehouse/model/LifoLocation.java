package com.home.simplewarehouse.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@DiscriminatorValue("LIFO")
public class LifoLocation extends Location implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(LifoLocation.class);
    
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
	public boolean addHandlingUnit(HandlingUnit handlingUnit) {
		LOG.trace("--> addHandlingUnit()");

		getHandlingUnits().forEach(h -> h.setLocaPos(h.getLocaPos() + 1));
		
		handlingUnit.setLocation(this);		
		handlingUnit.setLocaPos(1);
		
		LOG.trace("<-- addHandlingUnit()");
		
		return getHandlingUnits().add(handlingUnit);
	}
	
	@Override
	public boolean removeHandlingUnit(HandlingUnit handlingUnit) {
		LOG.trace("--> removeHandlingUnit()");
		
		boolean b = getHandlingUnits().remove( handlingUnit );
	    
		if ( b ) {
			handlingUnit.setLocation(null);
			handlingUnit.setLocaPos(null);
			
			getHandlingUnits().forEach(h -> h.setLocaPos(h.getLocaPos() - 1));
		}
	    
		LOG.trace("<-- removeHandlingUnit()");

		return b;
	}
	
	@Override
	protected String toString(List<HandlingUnit> list) {
		StringBuilder builder = new StringBuilder();

		builder.append("LIFO [");
		
		if (list == null) {
			builder.append("null");
		}
		else {
		    list.stream().forEach(item -> builder
		    		.append('"')
		    		.append(item.getId())
		    		.append(" Pos ")
		    		.append(item.getLocaPos())
		    		.append('"')
		    		.append(" "));
		}
		
		// Replace trailing " " by "]" (see above) or just append "]"
		int idx = builder.lastIndexOf(" ");
		if (idx > 0) {
		    builder.replace(idx, idx + 1, "]");
		}
		else {
		    builder.append("]");
		}
		
		return builder.toString();
	}
}
