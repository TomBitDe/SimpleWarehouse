package com.home.simplewarehouse.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.location.LocationService;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.views.SimpleLocationWithHandlingUnits;

@Named
@RequestScoped
public class SimpleLocationWithHandlingUnitsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(SimpleLocationWithHandlingUnitsBean.class);

	@EJB
	LocationService locationService;
	
	public List<SimpleLocationWithHandlingUnits> getSimpleLocationWithHandlingUnits() {
		List<SimpleLocationWithHandlingUnits> ret = new ArrayList<>();
		
		List<Location> locations = locationService.getAll();
		
		for (Location location : locations) {
			if (!location.getHandlingUnits().isEmpty()) {
				Set<HandlingUnit> handlingUnits = location.getHandlingUnits();

				String hus = "";
				for (HandlingUnit hu : handlingUnits) {
					hus = hus.concat(hu.getId() + ", ");
				}
				hus = hus.substring(0, hus.lastIndexOf(','));
				
				ret.add(new SimpleLocationWithHandlingUnits(location.getLocationId(), hus));
			}
		}
		
		LOG.debug("Found [{}] locations", ret.size());
		
		return ret;
	}
}
