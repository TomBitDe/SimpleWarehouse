package com.home.simplewarehouse.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// For @Named CDI is needed. Otherwise use @ManagedBean(name = "environBean", eager = true)
@Named
@SessionScoped
public class EnvironBean implements Serializable {
	private static final long serialVersionUID = 1L;	
	private static final Logger LOG = LogManager.getLogger(EnvironBean.class);

	@Inject
    private FacesContext facesContext;

    private String specTitle;
    private String specVersion;
    private String implTitle;
    private String implVersion;
    
    public EnvironBean() {
        Package info = FacesContext.class.getPackage();

        specTitle = info.getSpecificationTitle();
        specVersion = info.getSpecificationVersion();
        implTitle = info.getImplementationTitle();
        implVersion = info.getImplementationVersion();
    }

    public String getSpecTitle() {
        return specTitle;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public String getImplTitle() {
        return implTitle;
    }

    public String getImplVersion() {
        return implVersion;
    }

    public String getFacesContextValue() {
    	String ret = facesContext.toString().substring(0, facesContext.toString().indexOf('@'));
    	LOG.info("FacesContext=[{}] changed to ret=[{}]", facesContext, ret);
    	
        return ret;
    }
}