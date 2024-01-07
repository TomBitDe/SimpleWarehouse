package com.home.simplewarehouse.beans;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Bean class for JSF locale handling. 
 */
@Named
@SessionScoped
public class LocaleBean implements Serializable {
    private static final long serialVersionUID = -2786895465419133453L;
	private static final Logger LOG = LogManager.getLogger(LocaleBean.class);

	/**
	 * The used locale
	 */
    private Locale locale;
    
	/**
	 * Default constructor not mandatory
	 */
    public LocaleBean() {
    	super();
    }
    
    /**
     * The supported locale definition
     */
    private static Map<String,Object> countries;
	static {
		countries = new LinkedHashMap<>();
		countries.put("English", Locale.ENGLISH);
		countries.put("German", Locale.GERMAN);
	}

	/**
	 * Initialize locale
	 */
    @PostConstruct
    public void init() {
        locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
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
     * Gets the language
     * 
     * @return the language
     */
    public String getLanguage() {
        return locale.getLanguage();
    }

    /**
     * Sets the language
     * 
     * @param language the language to use
     */
    public void setLanguage(String language) {
        locale = new Locale(language);
        
        LOG.info("RootView = {}", FacesContext.getCurrentInstance().getViewRoot().getId());

        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

    /**
     * Gets the countries
     * 
     * @return the countries
     */
	public Map<String, Object> getCountries() {
		return countries;
	}

	/**
	 * Locale changed event listener
	 * 
	 * @param e the event
	 */
	public void localeChanged(ValueChangeEvent e) {
		if (e.getNewValue() != null) {
			String newLocaleValue = e.getNewValue().toString();

			for (Map.Entry<String, Object> entry : countries.entrySet()) {

				if (entry.getValue().toString().equals(newLocaleValue)) {
					FacesContext.getCurrentInstance().getViewRoot().setLocale((Locale) entry.getValue());
				}
			}
		}
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