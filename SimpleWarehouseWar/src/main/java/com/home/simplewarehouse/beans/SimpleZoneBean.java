package com.home.simplewarehouse.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.jsfutils.FacesMessageProxy;
import com.home.simplewarehouse.model.Zone;
import com.home.simplewarehouse.views.SimpleZone;
import com.home.simplewarehouse.zone.ZoneService;

/**
 * Provides a simplified representation on Zone and implements basic actions.
 */
@Named
@RequestScoped
public class SimpleZoneBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(SimpleZoneBean.class);
	
	private static final String START_ID = "A";
	private static final String END_ID = "Z";
	
	private static final List<String> GENERATED_IDS = new ArrayList<>(Arrays.asList(START_ID, "B", "C", "D", "E", "F", "G", "H"
			, "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", END_ID));

	/**
	 * The zone service
	 */
    @EJB
	private ZoneService zoneService;
    /**
     * The locale bean
     */
    @Inject
    private LocaleBean localeBean;
    /**
     * The zone items
     */
    private Set<SimpleZone> items;
    
	/**
	 * Default constructor not mandatory
	 */
    public SimpleZoneBean() {
    	super();
    }

    /**
     * Sets the items
     * 
     * @param items the items
     */
	public void setItems(Set<SimpleZone> items) {
		this.items = items;
	}

	/**
	 * Gets the items to show
	 * 
	 * @return the items
	 */
	public Set<SimpleZone> getItems() {
		items = new HashSet<>();
		
		Set<Zone> zones = zoneService.getAll();
		
		for (Zone zone : zones) {
			items.add(new SimpleZone(zone.getId(), false));
		}
		
		LOG.debug("Found [{}] zones", items.size());
		
		return items;
	}
	
	/**
	 * Adds a zone with DEFAULT values
	 */
	public void addDefault() {
		Set<Zone> zones = zoneService.getAll();
		Set<String> existing = new HashSet<>();
		
		for (Zone zone : zones) {
			existing.add(zone.getId());
		}
		
		List<String> ids = validIds(GENERATED_IDS, existing);
		
		if (! ids.isEmpty()) {
			zoneService.createOrUpdate(new Zone(ids.get(0)));
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
	private List<String> validIds(final List<String> base, Set<String> existing) {
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
		for (SimpleZone item : items) {
			if (item.isSelected()) {
				// This row has to be processed
				zoneService.delete(item.getId());
				LOG.info("Deleted item {}", item.getId());
					
				++cnt;
			}
		}

		if (cnt == 0) {
			FacesMessageProxy.showI18N(FacesContext.getCurrentInstance(),
					localeBean.getText("warning"), localeBean.getText("no_selection"));
		}
    }
}
