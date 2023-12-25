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
	
	public List<SimpleLocation> getSimpleLocations() {
		List<SimpleLocation> ret = new ArrayList<>();
		
		List<Location> locations = locationService.getAll();
		
		for (Location location : locations) {
            ret.add(new SimpleLocation(location.getLocationId()));
		}
		
		LOG.debug("Found [{}] locations", ret.size());
		
		return ret;
	}
}
