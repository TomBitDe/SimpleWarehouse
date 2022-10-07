package com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity;

import java.util.HashMap;
import java.util.Map;

public class Diagnostics {

    private Map<String,String> parameters = null;

    private Diagnostics(String name,Object value){
        this.parameters = new HashMap<>();
        this.parameters.put(name, String.valueOf(value));
    }
    public static Diagnostics with(String name,Object value){
        return new Diagnostics(name, value);
    }
    public Diagnostics and(String name,Object value){
        this.parameters.put(name, String.valueOf(value));
        return this;
    }
    public Map<String,String> asMap(){
        return this.parameters;
    }
}
