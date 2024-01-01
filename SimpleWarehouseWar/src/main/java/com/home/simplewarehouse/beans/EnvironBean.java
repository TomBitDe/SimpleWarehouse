package com.home.simplewarehouse.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Bean class providing JSF environment data. 
 */
// For @Named CDI is needed. Otherwise use @ManagedBean(name = "environBean", eager = true)
@Named
@SessionScoped
public class EnvironBean implements Serializable {
	private static final long serialVersionUID = 1L;	
	private static final Logger LOG = LogManager.getLogger(EnvironBean.class);

	/**
	 * The used faces context
	 */
	@Inject
    private FacesContext facesContext;

	/**
	 * The specification title
	 */
    private String specTitle;
    /**
     * The specification version
     */
    private String specVersion;
    /**
     * The implementation title
     */
    private String implTitle;
    /**
     * The implementation version
     */
    private String implVersion;
    
	/**
	 * Default constructor is mandatory
	 */
    public EnvironBean() {
        Package info = FacesContext.class.getPackage();

        specTitle = info.getSpecificationTitle();
        specVersion = info.getSpecificationVersion();
        implTitle = info.getImplementationTitle();
        implVersion = info.getImplementationVersion();
    }

    /**
     * Gets the specification title
     * 
     * @return the specification title
     */
    public String getSpecTitle() {
        return specTitle;
    }

    /**
     * Gets the specification version
     * 
     * @return the specification version
     */
    public String getSpecVersion() {
        return specVersion;
    }

    /**
     * Gets the implementation title
     * 
     * @return the implementation title
     */
    public String getImplTitle() {
        return implTitle;
    }

    /**
     * Gets the implementation version
     * 
     * @return the implementation version
     */
    public String getImplVersion() {
        return implVersion;
    }

    /**
     * Gets the faces context value without &commat; suffix
     * 
     * @return the faces context value
     */
    public String getFacesContextValue() {
    	String ret = facesContext.toString().substring(0, facesContext.toString().indexOf('@'));
    	LOG.info("FacesContext=[{}] changed to ret=[{}]", facesContext, ret);
    	
        return ret;
    }
}