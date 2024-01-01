package com.home.simplewarehouse.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.handlingunit.HandlingUnitNotOnLocationException;
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.handlingunit.LocationIsEmptyException;
import com.home.simplewarehouse.location.CapacityExceededException;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.location.OverheightException;
import com.home.simplewarehouse.location.OverlengthException;
import com.home.simplewarehouse.location.OverwidthException;
import com.home.simplewarehouse.location.WeightExceededException;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.views.SimpleHandlingUnit;

/**
 * Provides a simplified representation on HandlingUnit and implements basic actions.
 */
@Named
@RequestScoped
public class SimpleHandlingUnitBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(SimpleHandlingUnitBean.class);

	private static final String START_ID = "1";
	private static final String END_ID = "50";
	
	/**
	 * The handling unit service
	 */
	@EJB
	private HandlingUnitService handlingUnitService;
	/**
	 * The location service
	 */
	@EJB
	private LocationService locationService;
	/**
	 * The handlingunit items
	 */
    private List<SimpleHandlingUnit> items;
    /**
     * The current destinations
     */
    private List<String> destinations;
    /**
     * The selected destinations
     */
    private String selectedDestination = "";
    
	/**
	 * Default constructor not mandatory
	 */
    public SimpleHandlingUnitBean() {
    	super();
    }

    /**
     * Sets the HandlingUnists
     * 
     * @param items the handlingunit items
     */
	public void setItems(List<SimpleHandlingUnit> items) {
		this.items = items;
	}

	/**
	 * Gets the handlingunit items
	 * 
	 * @return the items
	 */
	public List<SimpleHandlingUnit> getItems() {
		items = new ArrayList<>();
		
		List<HandlingUnit> handlingUnits = handlingUnitService.getAll();
		
		for (HandlingUnit handlingUnit : handlingUnits) {
			items.add(new SimpleHandlingUnit(handlingUnit.getId(), false));
		}
		
		LOG.debug("Found [{}] handlingUnits", items.size());
		
		return items;
	}
	
	/**
	 * Sets the destinations
	 * 
	 * @param destinations the destinations
	 */
	public void setDestinations(List<String> destinations) {
		this.destinations = destinations;
	}

	/**
	 * Gets the destionations
	 * 
	 * @return the destinations
	 */
	public List<String> getDestinations() {
		destinations = new ArrayList<>();
 		
		List<Location> locations = locationService.getAll();
		
		for (Location location : locations) {
			destinations.add(location.getLocationId());
		}
		
		LOG.debug("Set [{}] destinations", destinations.size());
		
		return destinations;
	}
	
	/**
	 * Gets the selected destination
	 * 
	 * @return the destination
	 */
    public String getSelectedDestination() {
		return selectedDestination;
	}

    /**
     * Sets the selected destination
     * 
     * @param selectedDestination the selected destination
     */
	public void setSelectedDestination(String selectedDestination) {
		this.selectedDestination = selectedDestination;
	}

	/**
	 * Deletes the selected item
	 */
	public void deleteSelected() {
        // Process the selected rows
        for (SimpleHandlingUnit item : items) {
            if (item.isSelected()) {
                // This row has to be processed
            	handlingUnitService.delete(item.getId());
            	LOG.info("Deleted item {}", item.getId());
            }
        }
    }
	
	/**
	 * Adds an Item with DEFAULT values
	 */
	public void addDefault() {
		List<HandlingUnit> hus = handlingUnitService.getAll();
		List<String> existing = new ArrayList<>();
		
		for (HandlingUnit hu : hus) {
			existing.add(hu.getId());
		}
		
		for (int idx = Integer.parseInt(START_ID); idx <= Integer.valueOf(END_ID); idx++) {
			if (! existing.contains(String.valueOf(idx))) {
				handlingUnitService.createOrUpdate(new HandlingUnit(String.valueOf(idx)));
				break;
			}
		}
	}
    
	/**
	 * Picks the selected item
	 */
    public void pickSelected() {
        // Process the selected rows
        for (SimpleHandlingUnit item : items) {
            if (item.isSelected()) {
                // This row has to be processed
            	List<Location> locations = locationService.getAllContaining(handlingUnitService.getById(item.getId()));
            	for (Location location : locations) {
            	    try {
						handlingUnitService.pickFrom(location, handlingUnitService.getById(item.getId()));
						LOG.info("Item {} picked from {}", item.getId(), location.getLocationId());
					}
            	    catch (LocationIsEmptyException | HandlingUnitNotOnLocationException e) {
						LOG.error("Item pick {} failed; reason {}", item.getId(), e.getMessage());
					}
            	}
            }
        }
    }
    
    /**
     * Drops the selected item
     */
    public void dropSelected() {
		if (selectedDestination != null && !selectedDestination.isEmpty()) {
			// Process the selected rows
			for (SimpleHandlingUnit item : items) {
				if (item.isSelected()) {
					// This row has to be processed
            	    try {
						handlingUnitService.dropTo(selectedDestination, item.getId());
						LOG.info("Item {} dropped on {}", item.getId(), selectedDestination);
					}
            	    catch (CapacityExceededException | WeightExceededException | OverheightException | OverlengthException | OverwidthException e) {
						LOG.error("Item {} drop on {} failed; reason {}", item.getId(), selectedDestination, e.getMessage());
					}
				}
			}
		}
    }
}
