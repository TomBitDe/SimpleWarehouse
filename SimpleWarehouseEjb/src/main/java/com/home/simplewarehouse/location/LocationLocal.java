package com.home.simplewarehouse.location;

import java.util.List;

import com.home.simplewarehouse.model.Location;

public interface LocationLocal {
	public void create(Location location);
	public void delete(Location location);
	public Location getById(String id);
	public List<Location> getAll();
}
