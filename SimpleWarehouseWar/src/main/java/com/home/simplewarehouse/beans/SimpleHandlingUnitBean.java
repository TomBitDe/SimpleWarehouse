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
import com.home.simplewarehouse.location.LocationService;
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
	
    public void deleteSelected() {
        // Process the selected rows
        for (SimpleHandlingUnit item : items) {
            if (item.isSelected()) {
                // This row has to be processed
            	handlingUnitService.delete(item.getId());
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
					}
            	    catch (LocationIsEmptyException | HandlingUnitNotOnLocationException e) {
						LOG.error("HandlingUnit pick failed; reason {}", e.getMessage());
					}
            	}
            }
        }
    }

}
