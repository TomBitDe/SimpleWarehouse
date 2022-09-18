/**
 * Simple cache using a singleton.<br>
 * <p>
 * Cache data is read only for the application.<br>
 * Cache content is loaded from the properties file 'globals.properties' or database table 'APPL_CONFIG'<br>
 * Cache content is refreshed by a timer<br>
 * New cache content can be provided at runtime by editing the properties file<br>
 * REST interface for db data management<br>
 */
package com.home.simplewarehouse.singleton.simplecache;