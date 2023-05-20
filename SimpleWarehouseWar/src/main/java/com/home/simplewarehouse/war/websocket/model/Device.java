package com.home.simplewarehouse.war.websocket.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the entity that is manipulated via the Web Socket test sample.
 */
public class Device {
	private static final Logger LOG = LogManager.getLogger(Device.class);

    private int id;
    private String name;
    private String status;
    private String type;
    private String description;

    public Device() {
    	super();

    	LOG.debug("--> Device");
    	LOG.debug("<-- Device");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
    	LOG.info("setId=[{}]", id);
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}