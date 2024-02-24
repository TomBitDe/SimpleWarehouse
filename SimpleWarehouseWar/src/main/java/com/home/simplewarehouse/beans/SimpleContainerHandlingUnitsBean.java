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

import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.utils.FacesMessageProxy;
import com.home.simplewarehouse.views.SimpleContainerHandlingUnits;

/**
 * Provides a simplified representation of Locations containing HandlingUnits and implements basic actions.
 */
@Named
@RequestScoped
public class SimpleContainerHandlingUnitsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(SimpleContainerHandlingUnitsBean.class);

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
	 * The handlingunit items containing handling units
	 */
    private List<SimpleContainerHandlingUnits> items;
    /**
     * The origin handlingunits for further actions
     */
    private List<String> origins;
    /**
     * The destination handlingunits for further actions
     */
    private List<String> destinations;
    /**
     * The selected origin
     */
    private String selectedOrigin = "";
    /**
     * The selected destination
     */
    private String selectedDestination = "";

	/**
	 * Default constructor not mandatory
	 */
    public SimpleContainerHandlingUnitsBean() {
    	super();
    }

    /**
     * Sets the items
     * 
     * @param items the items
     */
	public void setItems(List<SimpleContainerHandlingUnits> items) {
		this.items = items;
	}

	/**
	 * Gets the items to show
	 * 
	 * @return the items
	 */
	public List<SimpleContainerHandlingUnits> getItems() {
		items = new ArrayList<>();
		
		List<HandlingUnit> handlingUnits = handlingUnitService.getAll();
		
		for (HandlingUnit handlingUnit : handlingUnits) {
			Set<HandlingUnit> containing = handlingUnit.getContains();

			if (!containing.isEmpty()) {
				String hus = "";
				
				for (HandlingUnit hu : containing) {
					hus = hus.concat(hu.getId() + ", ");
				}
				hus = hus.substring(0, hus.lastIndexOf(','));
				
				items.add(new SimpleContainerHandlingUnits(handlingUnit.getId()
						, hus, false));
			}
		}
		
		LOG.debug("Found [{}] base handlingunits", items.size());
		
		return items;
	}
	
	/**
	 * Sets the origin handlingunits
	 * 
	 * @param origins the origins
	 */
	public void setOrigins(List<String> origins) {
		this.origins = origins;
	}

	/**
	 * Gets the origin handlingunits
	 * 
	 * @return the origins
	 */
	public List<String> getOrigins() {
		origins = new ArrayList<>();
 		
		List<HandlingUnit> handlingUnists = handlingUnitService.getAll();
		
		for (HandlingUnit handlingUnit : handlingUnists) {
			origins.add(handlingUnit.getId());
		}
		
		LOG.debug("Set [{}] origins", origins.size());
		
		return origins;
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
	 * Gets the destination handlingunits
	 * 
	 * @return the origins
	 */
	public List<String> getDestinations() {
		destinations = new ArrayList<>();
 		
		List<HandlingUnit> handlingUnists = handlingUnitService.getAll();
		
		for (HandlingUnit handlingUnit : handlingUnists) {
			destinations.add(handlingUnit.getId());
		}
		
		LOG.debug("Set [{}] destinations", destinations.size());
		
		return destinations;
	}

	/**
	 * Gets the selected origin
	 * 
	 * @return the origin
	 */
	public String getSelectedOrigin() {
		return selectedOrigin;
	}

	/**
	 * Sets the selected origin
	 * 
	 * @param selectedOrigin the origin
	 */
	public void setSelectedOrigin(String selectedOrigin) {
		this.selectedOrigin = selectedOrigin;
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
	 * Frees the selected base handlingunit form all composition handlingunits
	 */
    public void freeSelected() {
		if (items.isEmpty()) {
			FacesMessageProxy.showI18N(FacesContext.getCurrentInstance(),
					localeBean.getText("warning"), localeBean.getText("no_items"));
			
			return;
		}

		int cnt = 0;

        // Process the selected rows
        for (SimpleContainerHandlingUnits item : items) {
            if (item.isSelected()) {
                // This row has to be processed
            	handlingUnitService.free(item.getBaseId());
            	
				LOG.info("Handlingunit {} freed", item.getBaseId());
				++cnt;
			}
		}

		if (cnt == 0) {
			FacesMessageProxy.showI18N(FacesContext.getCurrentInstance(),
					localeBean.getText("warning"), localeBean.getText("no_selection"));
		}
    }

	/**
	 * Assigns the handlingunit to a base handlingunit
	 */
    public void assign() {
    	if (selectionValid()) {
    		handlingUnitService.assign(selectedOrigin, selectedDestination);
    	}
    }

	/**
	 * Move the handlingunit to another destination handlingunit
	 */
    public void move() {
    	if (selectionValid()) {
    		handlingUnitService.move(selectedOrigin, selectedDestination);
    	}
    }

	/**
	 * Removes the handlingunit from another handlingunit
	 */
    public void remove() {
    	if (selectionValid()) {
    		handlingUnitService.remove(selectedOrigin, selectedDestination);
    	}
    }
    
    /**
     * Checks if the selections are valid for further actions
     * 
     * @return true if valid else false
     */
    private boolean selectionValid() {
    	if (selectedOrigin == null || selectedOrigin.isEmpty()) {
			LOG.warn("Invalid origin [{}] selection!", selectedOrigin);
			
			FacesMessageProxy.showI18N(FacesContext.getCurrentInstance(),
					localeBean.getText("warning"), localeBean.getText("no_origin_selected"));

			return false;
    	}
    	
    	if (selectedDestination == null || selectedDestination.isEmpty()) {
			LOG.warn("Invalid destination [{}] selection!", selectedDestination);
			
			FacesMessageProxy.showI18N(FacesContext.getCurrentInstance(),
					localeBean.getText("warning"), localeBean.getText("no_destination_selected"));

			return false;
    	}
    	
    	if (selectedOrigin.equals(selectedDestination)) {
			LOG.warn("Invalid equal origin {} destination {} selection!", selectedOrigin, selectedDestination);
			
			FacesMessageProxy.showI18N(FacesContext.getCurrentInstance(),
					localeBean.getText("warning"), localeBean.getText("origin_equals_destination"));

			return false;
    	}
    	
    	return true;
    }
}
