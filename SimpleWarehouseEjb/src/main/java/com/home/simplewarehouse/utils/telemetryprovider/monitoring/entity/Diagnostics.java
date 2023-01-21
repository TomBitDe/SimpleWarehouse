package com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * The Diagnostics class.
 */
public class Diagnostics {

    private Map<String,String> parameters = null;

    /**
     * Private only Constructor
     * 
     * @param name the parameters name
     * @param value the value
     */
    private Diagnostics(String name,Object value){
        this.parameters = new HashMap<>();
        this.parameters.put(name, String.valueOf(value));
    }
    
    /**
     * Create a new Diagnostics parameter
     * 
     * @param name the parameters name
     * @param value the value
     * 
     * @return the Diagnostics
     */
    public static Diagnostics with(String name,Object value){
        return new Diagnostics(name, value);
    }
    
    /**
     * Add a Diagnostics parameter
     * 
     * @param name the parameters name 
     * @param value the value
     * 
     * @return the Diagnostics
     */
    public Diagnostics and(String name,Object value){
        this.parameters.put(name, String.valueOf(value));
        return this;
    }
    
    /**
     * Deliver the Diagnostics as Map
     * 
     * @return the Map
     */
    public Map<String,String> asMap(){
        return this.parameters;
    }
}
