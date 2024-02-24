package com.home.simplewarehouse.utils;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FacesMessageProxy implements Serializable {
	private static final long serialVersionUID = 1L;	
	private static final Logger LOG = LogManager.getLogger(FacesMessageProxy.class);

    private FacesMessageProxy() {
    	super();
    }
    
    public static void showI18N(FacesContext currentContext, String summaryI18N, String detailI18N) {
		LOG.warn(detailI18N);
		currentContext.addMessage(null
				, new FacesMessage(FacesMessage.SEVERITY_WARN, summaryI18N, detailI18N));
    }
}
