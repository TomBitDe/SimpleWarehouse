package com.home.simplewarehouse.handlingunit;

import java.util.List;

import com.home.simplewarehouse.model.HandlingUnit;

public interface HandlingUnitLocal {
	public void create(HandlingUnit handlingUnit);
	public void delete(HandlingUnit handlingUnit);
	public HandlingUnit getById(String id);
	public List<HandlingUnit> getAll();
}
