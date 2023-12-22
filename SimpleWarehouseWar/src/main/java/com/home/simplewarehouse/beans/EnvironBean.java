package com.home.simplewarehouse.beans;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

// For @Named CDI is needed. Otherwise use @ManagedBean(name = "environBean", eager = true)
@Named
@RequestScoped
public class EnvironBean {

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
        return facesContext.toString();
    }
}