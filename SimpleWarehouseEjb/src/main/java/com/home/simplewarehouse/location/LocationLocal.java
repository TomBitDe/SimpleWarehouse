package com.home.simplewarehouse.location;

import java.util.List;

import com.home.simplewarehouse.location.model.Location;

public interface LocationLocal {
	public void create(Location location);
	public Location delete(String id);
	public Location delete(Location location);
	public Location getById(String id);
	public List<Location> getAll();
}
