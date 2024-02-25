package com.home.simplewarehouse.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.jsfutils.FacesMessageProxy;
import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.views.SimpleLocation;

/**
 * Provides a simplified representation on Location and implements basic actions.
 */
@Named
@RequestScoped
public class SimpleLocationBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(SimpleLocationBean.class);
	
	private static final String START_ID = "A";
	private static final String END_ID = "Z";
	
	private static final List<String> GENERATED_IDS = new ArrayList<>(Arrays.asList(START_ID, "B", "C", "D", "E", "F", "G", "H"
			, "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", END_ID));

	/**
	 * The location service
	 */
    @EJB
	private LocationService locationService;
    /**
     * The locale bean
     */
    @Inject
    private LocaleBean localeBean;
    /**
     * The location items
     */
    private List<SimpleLocation> items;
    
	/**
	 * Default constructor not mandatory
	 */
    public SimpleLocationBean() {
    	super();
    }

    /**
     * Sets the items
     * 
     * @param items the items
     */
	public void setItems(List<SimpleLocation> items) {
		this.items = items;
	}

	/**
	 * Gets the items to show
	 * 
	 * @return the items
	 */
	public List<SimpleLocation> getItems() {
		items = new ArrayList<>();
		
		List<Location> locations = locationService.getAll();
		
		for (Location location : locations) {
			items.add(new SimpleLocation(location.getLocationId(), false));
		}
		
		LOG.debug("Found [{}] locations", items.size());
		
		return items;
	}
	
	/**
	 * Adds a location with DEFAULT values
	 */
	public void addDefault() {
		List<Location> locations = locationService.getAll();
		List<String> existing = new ArrayList<>();
		
		for (Location location : locations) {
			existing.add(location.getLocationId());
		}
		
		List<String> ids = validIds(GENERATED_IDS, existing);
		
		if (! ids.isEmpty()) {
			locationService.createOrUpdate(new Location(ids.get(0)));
		}
		else {
			LOG.info("No ID available to add a DEFAULT location");
		}
	}
	
	/**
	 * Provides valid ids sorted out from base compared with existing
	 * 
	 * @param base the complete amount of foreseen ids 
	 * @param existing all the already existing / used ids
	 * 
	 * @return the ids that can be used for the next action
	 */
	private List<String> validIds(final List<String> base, List<String> existing) {
		List<String> ret = new ArrayList<>();
		
		for (String elem : base) {
			if (! existing.contains(elem)) {
				ret.add(elem);
			}
		}
		
		return ret;
	}
	
	/**
	 * Deletes the selected item
	 */
    public void deleteSelected() {
		if (items.isEmpty()) {
			FacesMessageProxy.showI18N(FacesContext.getCurrentInstance(),
					localeBean.getText("warning"), localeBean.getText("no_items"));
			
			return;
		}
		
		int cnt = 0;

		// Process the selected rows
		for (SimpleLocation item : items) {
			if (item.isSelected()) {
				// This row has to be processed
				locationService.delete(item.getLocationId());
				LOG.info("Deleted item {}", item.getLocationId());
					
				++cnt;
			}
		}

		if (cnt == 0) {
			FacesMessageProxy.showI18N(FacesContext.getCurrentInstance(),
					localeBean.getText("warning"), localeBean.getText("no_selection"));
		}
    }
}
