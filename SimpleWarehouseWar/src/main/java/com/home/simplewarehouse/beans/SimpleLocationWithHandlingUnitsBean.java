package com.home.simplewarehouse.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.handlingunit.HandlingUnitNotOnLocationException;
import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.handlingunit.LocationIsEmptyException;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.utils.FacesMessageProxy;
import com.home.simplewarehouse.views.SimpleLocationWithHandlingUnits;

/**
 * Provides a simplified representation on Locations containing HandlingUnits and implements basic actions.
 */
@Named
@RequestScoped
public class SimpleLocationWithHandlingUnitsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(SimpleLocationWithHandlingUnitsBean.class);

	/**
	 * The location service
	 */
	@EJB
	private LocationService locationService;
	/**
	 * The handling unit service
	 */
	@EJB
	private HandlingUnitService handlingUnitService;
    /**
     * The locale bean
     */
    @Inject
    private LocaleBean localeBean;
	/**
	 * The location items containing handling units
	 */
    private List<SimpleLocationWithHandlingUnits> items;

	/**
	 * Default constructor not mandatory
	 */
    public SimpleLocationWithHandlingUnitsBean() {
    	super();
    }

    /**
     * Sets the items
     * 
     * @param items the items
     */
	public void setItems(List<SimpleLocationWithHandlingUnits> items) {
		this.items = items;
	}

	/**
	 * Gets the items to show
	 * 
	 * @return the items
	 */
	public List<SimpleLocationWithHandlingUnits> getItems() {
		items = new ArrayList<>();
		
		List<Location> locations = locationService.getAll();
		
		for (Location location : locations) {
			if (!location.getHandlingUnits().isEmpty()) {
				Set<HandlingUnit> handlingUnits = location.getHandlingUnits();

				String hus = "";
				for (HandlingUnit hu : handlingUnits) {
					hus = hus.concat(hu.getId() + ", ");
				}
				hus = hus.substring(0, hus.lastIndexOf(','));
				
				items.add(new SimpleLocationWithHandlingUnits(location.getLocationId(), hus, false));
			}
		}
		
		LOG.debug("Found [{}] locations", items.size());
		
		return items;
	}
	
	/**
	 * Empties the selected locations form all handlingunits
	 */
    public void emptySelected() {
		if (items.isEmpty()) {
			FacesMessageProxy.showI18N(FacesContext.getCurrentInstance(),
					localeBean.getText("warning"), localeBean.getText("no_items"));
			
			return;
		}

		int cnt = 0;

        // Process the selected rows
        for (SimpleLocationWithHandlingUnits item : items) {
            if (item.isSelected()) {
                // This row has to be processed
            	Set<HandlingUnit> handlingUnits = locationService.getById(item.getLocationId()).getHandlingUnits();
            	
				// Prepare handlingunit pick from item
            	for (HandlingUnit hu : handlingUnits) {
            	    try {
					    handlingUnitService.pickFrom(item.getLocationId(), hu.getId());
					    LOG.info("Handlingunit {} picked from item {}", hu.getId(), item.getLocationId());
				    }
            	    catch (LocationIsEmptyException | HandlingUnitNotOnLocationException e) {
					    LOG.error("Handlingunit pick {} failed; reason {}", hu.getId(), e.getMessage());
            	    }
            	}
            	
            	if (locationService.getById(item.getLocationId()).getHandlingUnits().isEmpty()) {
            	    LOG.info("Emptied item {}", item.getLocationId());
            	}
            	else {
            	    LOG.error("Item {} NOT empty!", item.getLocationId());
            	}
            	
            	++cnt;       	
                }
            }   
        
		    if (cnt == 0) {
				FacesMessageProxy.showI18N(FacesContext.getCurrentInstance(),
						localeBean.getText("warning"), localeBean.getText("no_selection"));
		    }
    }
}
