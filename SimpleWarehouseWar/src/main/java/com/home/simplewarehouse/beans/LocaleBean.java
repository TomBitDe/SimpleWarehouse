package com.home.simplewarehouse.beans;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named
@SessionScoped
public class LocaleBean implements Serializable {
    private static final long serialVersionUID = -2786895465419133453L;
	private static final Logger LOG = LogManager.getLogger(LocaleBean.class);

    private Locale locale;
    
    private static Map<String,Object> countries;
	static {
		countries = new LinkedHashMap<>();
		countries.put("English", Locale.ENGLISH);
		countries.put("German", Locale.GERMAN);
	}

    @PostConstruct
    public void init() {
        locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

    public void setLanguage(String language) {
        locale = new Locale(language);
        
        LOG.info("RootView = {}", FacesContext.getCurrentInstance().getViewRoot().getId());

        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

	public Map<String, Object> getCountries() {
		return countries;
	}

	// value change event listener
	public void localeChanged(ValueChangeEvent e) {
		if (e.getNewValue() != null) {
			String newLocaleValue = e.getNewValue().toString();

			for (Map.Entry<String, Object> entry : countries.entrySet()) {

				if (entry.getValue().toString().equals(newLocaleValue)) {
					FacesContext.getCurrentInstance().getViewRoot().setLocale((Locale) entry.getValue());
				}
			}
		}
	}}