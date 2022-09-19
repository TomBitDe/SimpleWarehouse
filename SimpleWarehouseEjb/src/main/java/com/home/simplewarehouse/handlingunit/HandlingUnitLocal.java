package com.home.simplewarehouse.handlingunit;

import java.util.List;

import com.home.simplewarehouse.handlingunit.model.HandlingUnit;

public interface HandlingUnitLocal {
	public void create(HandlingUnit handlingUnit);
	public HandlingUnit delete(String id);
	public HandlingUnit delete(HandlingUnit handlingUnit);
	public HandlingUnit getById(String id);
	public List<HandlingUnit> getAll();
}
