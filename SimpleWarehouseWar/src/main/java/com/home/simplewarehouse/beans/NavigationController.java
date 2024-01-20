package com.home.simplewarehouse.beans;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.annotation.ManagedProperty;
import javax.inject.Inject;
import javax.inject.Named;  

/**
 * JSF navigation controller (example).
 */
@Named
@RequestScoped
public class NavigationController implements Serializable {  
   private static final long serialVersionUID = 1L;
   
   /**
    * The current page id for navigation
    */
   @Inject
   @ManagedProperty("#{param.pageId}") 
   private String pageId;  
   
	/**
	 * Default constructor not mandatory
	 */
   public NavigationController() {
	   super();
   }
   
   /**
    * Move to page
    * 
    * @return the identifier
    */
   public String moveToPage(String page) {      
       return page + "?faces-redirect=true";    
   }  
}