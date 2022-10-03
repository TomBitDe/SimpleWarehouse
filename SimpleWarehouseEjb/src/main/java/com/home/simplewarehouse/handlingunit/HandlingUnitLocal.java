package com.home.simplewarehouse.handlingunit;

import java.util.List;

import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.model.HandlingUnitNotOnLocationException;
import com.home.simplewarehouse.model.Location;
import com.home.simplewarehouse.model.LocationIsEmptyException;

public interface HandlingUnitLocal {
	public void create(HandlingUnit handlingUnit);
	public void delete(HandlingUnit handlingUnit);
	public HandlingUnit getById(String id);
	public void pickFrom(Location location, HandlingUnit handlingUnit) throws LocationIsEmptyException, HandlingUnitNotOnLocationException;
	public void dropTo(Location location, HandlingUnit handlingUnit);
	public List<HandlingUnit> getAll();
}
