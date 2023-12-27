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

@Named
@RequestScoped
public class SimpleHandlingUnitBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(SimpleHandlingUnitBean.class);

	@EJB
	HandlingUnitService handlingUnitService;
	
	@EJB
	LocationService locationService;
	
    private List<SimpleHandlingUnit> items;
    
    private List<String> destinations;
    
    private String selectedDestination = "";

	public void setItems(List<SimpleHandlingUnit> items) {
		this.items = items;
	}

	public List<SimpleHandlingUnit> getItems() {
		items = new ArrayList<>();
		
		List<HandlingUnit> handlingUnits = handlingUnitService.getAll();
		
		for (HandlingUnit handlingUnit : handlingUnits) {
			items.add(new SimpleHandlingUnit(handlingUnit.getId(), false));
		}
		
		LOG.debug("Found [{}] handlingUnits", items.size());
		
		return items;
	}
	
	public void setDestinations(List<String> destinations) {
		this.destinations = destinations;
	}

	public List<String> getDestinations() {
		destinations = new ArrayList<>();
 		
		List<Location> locations = locationService.getAll();
		
		for (Location location : locations) {
			destinations.add(location.getLocationId());
		}
		
		LOG.debug("Set [{}] destinations", destinations.size());
		
		return destinations;
	}
	
    public String getSelectedDestination() {
		return selectedDestination;
	}

	public void setSelectedDestination(String selectedDestination) {
		this.selectedDestination = selectedDestination;
	}

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
