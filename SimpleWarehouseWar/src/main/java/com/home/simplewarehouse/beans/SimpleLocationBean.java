package com.home.simplewarehouse.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.views.SimpleLocation;

@Named
@RequestScoped
public class SimpleLocationBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(SimpleLocationBean.class);

    @EJB
	LocationService locationService;
	
    private List<SimpleLocation> items;

	public void setItems(List<SimpleLocation> items) {
		this.items = items;
	}

	public List<SimpleLocation> getItems() {
		items = new ArrayList<>();
		
		List<Location> locations = locationService.getAll();
		
		for (Location location : locations) {
			items.add(new SimpleLocation(location.getLocationId(), false));
		}
		
		LOG.debug("Found [{}] locations", items.size());
		
		return items;
	}
	
    public void deleteSelected() {
        // Process the selected rows
        for (SimpleLocation item : items) {
            if (item.isSelected()) {
                // This row has to be processed
            	locationService.delete(item.getLocationId());
            }
        }
    }
}
