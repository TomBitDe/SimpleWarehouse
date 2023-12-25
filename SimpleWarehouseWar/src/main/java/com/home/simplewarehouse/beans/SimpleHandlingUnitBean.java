package com.home.simplewarehouse.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.handlingunit.HandlingUnitService;
import com.home.simplewarehouse.model.HandlingUnit;
import com.home.simplewarehouse.views.SimpleHandlingUnit;

@Named
@RequestScoped
public class SimpleHandlingUnitBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(SimpleHandlingUnitBean.class);

	@EJB
	HandlingUnitService handlingUnitService;
	
	public List<SimpleHandlingUnit> getSimpleHandlingUnits() {
		List<SimpleHandlingUnit> ret = new ArrayList<>();
		
		List<HandlingUnit> handlingUnits = handlingUnitService.getAll();
		
		for (HandlingUnit handlingUnit : handlingUnits) {
            ret.add(new SimpleHandlingUnit(handlingUnit.getId()));
		}
		
		LOG.debug("Found [{}] handlingUnits", ret.size());
		
		return ret;
	}
}
