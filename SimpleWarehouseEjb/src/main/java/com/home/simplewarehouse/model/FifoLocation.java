package com.home.simplewarehouse.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Location with access limit FIFO.
 */
@Entity
@DiscriminatorValue("FIFO")
public class FifoLocation extends Location implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(FifoLocation.class);
    
    /**
     * Default Fifo Location
     */
    public FifoLocation() {
    	super();
    }

    /**
     * Fifo Location with id
     * 
     * @param id the given id
     */
    public FifoLocation(String id) {
    	super(id);
    }
    
    /**
     * Fifo Location with id and user
     * 
     * @param id the given id
     * @param user the given user
     */
    public FifoLocation(String id, String user) {
    	super(id, user);
    }
    
    /**
     * Fifo Location with id, user and timestamp
     * 
     * @param id the given id
     * @param user the given user
     * @param timestamp the given timestamp
     */
    public FifoLocation(String id, String user, Timestamp timestamp) {
    	super(id, user, timestamp);
    }

	@Override
	public boolean addHandlingUnit(HandlingUnit handlingUnit) {
		LOG.trace("--> addHandlingUnit()");

		handlingUnit.setLocation(this);

		HandlingUnit max = handlingUnits.stream()
				.max(Comparator.comparing(HandlingUnit::getLocaPos))
				.orElse(null);

		if (max == null) {
			handlingUnit.setLocaPos(1);
		}
		else {
			handlingUnit.setLocaPos(max.getLocaPos() + 1);
		}
		
		LOG.trace("<-- addHandlingUnit()");
		
		return handlingUnits.add(handlingUnit);
	}
	
	@Override
	public boolean removeHandlingUnit(HandlingUnit handlingUnit) {
		LOG.trace("--> removeHandlingUnit()");
		
		boolean b = handlingUnits.remove( handlingUnit );
	    
		if ( b ) {
			handlingUnit.setLocation(null);
			handlingUnit.setLocaPos(null);
			
			handlingUnits.forEach(h -> h.setLocaPos(h.getLocaPos() - 1));
		}

		LOG.trace("<-- removeHandlingUnit()");

		return b;
	}
	
	@Override
	public List<HandlingUnit> getAvailablePicks() {
		LOG.trace("--> getAvailablePicks()");

		List<HandlingUnit> ret = new ArrayList<>();
		
		if (! handlingUnits.isEmpty()) {
			ret = handlingUnits.stream().filter(hu -> hu.getLocaPos() == 1)
					.collect(Collectors.toList());
		}
		
		LOG.trace("<-- getAvailablePicks()");

		return ret;
	}

	@Override
	protected String toString(List<HandlingUnit> list) {
		StringBuilder builder = new StringBuilder();

		builder.append("FIFO=[");
		
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
