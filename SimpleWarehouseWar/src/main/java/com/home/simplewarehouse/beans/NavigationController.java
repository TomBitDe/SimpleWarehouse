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
    * Move to page 1
    * 
    * @return the identifier
    */
   public String moveToPage1() {      
      return "page1";    
   }  
   
   /**
    * Move to page 2
    * 
    * @return the identifier
    */
   public String moveToPage2() {       
      return "page2";    
   }  
   
   /**
    * Move to home page
    * 
    * @return the identifier
    */
   public String moveToHomePage() {      
      return "home";    
   }  
   
   /**
    * Process page 1
    * 
    * @return the identifier
    */
   public String processPage1() {       
      return "page";    
   }  
   
   /**
    * Process page 2
    * 
    * @return the identifier
    */
   public String processPage2() {       
      return "page";    
   } 
   
   /**
    * Shows the current page
    * 
    * @return the page identifier
    */
   public String showPage() {       
      if(pageId == null) {          
         return "home";       
      }       
      
      if(pageId.equals("1")) {          
         return "page1";       
      }
      else if(pageId.equals("2")) {          
         return "page2";       
      }
      else {          
         return "home";       
      }    
   }  
   
   /**
    * Gets the page id
    * 
    * @return the page id
    */
   public String getPageId() {       
      return pageId;    
   }  
   
   /**
    * Sets the page id
    * 
    * @param pageId the page id
    */
   public void setPageId(String pageId) {       
      this.pageId = pageId;   
   } 
}