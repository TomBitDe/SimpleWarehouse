package com.home.simplewarehouse.jsfutils;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class to show messages via JSF.
 */
public class FacesMessageProxy implements Serializable {
	private static final long serialVersionUID = 1L;	
	private static final Logger LOG = LogManager.getLogger(FacesMessageProxy.class);

    private FacesMessageProxy() {
    	super();
    }
    
    /**
     * Shows an I18N message using the given parameters
     * <p>
     * Example: FacesMessageProxy.showI18N(FacesContext.getCurrentInstance(),
	 *				localeBean.getText("warning"), localeBean.getText("no_selection"));
     * </p>
     * @param currentContext the current FacesContext
     * @param summaryI18N the summary text
     * @param detailI18N the details text
     */
    public static void showI18N(FacesContext currentContext, String summaryI18N, String detailI18N) {
		LOG.warn(detailI18N);
		currentContext.addMessage(null
				, new FacesMessage(FacesMessage.SEVERITY_WARN, summaryI18N, detailI18N));
    }
}
