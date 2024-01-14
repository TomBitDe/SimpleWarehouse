package com.home.simplewarehouse.beans;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Bean class for JSF locale handling.
 * Locale switching can be done by browser settings.
 */
@Named
@RequestScoped
public class LocaleBean implements Serializable {
    private static final long serialVersionUID = -2786895465419133453L;
	private static final Logger LOG = LogManager.getLogger(LocaleBean.class);

	/**
	 * The used locale
	 */
    private Locale locale;
    /**
     * The browsers locale
     */
    private Locale browserLocale;
    
	/**
	 * Default constructor not mandatory
	 */
    public LocaleBean() {
    	super();
    }
    
	/**
	 * Initialize locale and browserLocale
	 */
    @PostConstruct
    public void init() {
        this.locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        this.browserLocale = request.getLocale();
        
        LOG.info("Browser locale={} Application locale={}", this.browserLocale, this.locale);
    }

    /**
     * Gets the locale
     * 
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }
    
    /**
     * Sets the locale
     * 
     * @param locale the locale
     */
    public void setLocale(Locale locale) {
    	this.locale = locale;
    }

    /**
     * Gets the browsers locale
     * 
     * @return the browsers locale
     */
    public Locale getBrowserLocale() {
		return browserLocale;
	}
	
	/**
	 * Gets a text for the given key taking the current locale into account
	 * 
	 * @param key the key to reference the text
	 * 
	 * @return the text
	 */
    public String getText(String key) {
        FacesContext context = FacesContext.getCurrentInstance();
        
        // See <var>text</var> in faces-config.xml; same value required
        ResourceBundle text = context.getApplication().evaluateExpressionGet(context, "#{text}", ResourceBundle.class);
        
        return text.getString(key);
    }
}