/**
 * Entity model for the Simple Warehouse application.<br>
 *
 * <pre>{@code
 * 
 *      +--------------+                         +-----------------+
 *      | HandlingUnit |                         | Location        |
 *      |              |                         |                 |
 *      |  id          |                         |  locationId     |<-----+
 *      |  location    | <-- 0:1 ------- 0:N --> |  handlingUnits  |      |
 *      |  ...         |                         |  locationStatus |      |
 *      +--------------+                         |  ...            |      |      
 *                                               +-----------------+      |
 *                                                                        |
 *                                                                        |
 *                                                                       1:1
 *                                                                        |
 *                                                                        v
 *                                                                  +----------------+
 *                                                                  | LocationStatus |
 *                                                                  |                |
 *                                                                  |  locationId    |
 *                                                                  |  errorStatus   |
 *                                                                  |  ...           |
 *                                                                  +----------------+
 *                                                                  
 * }</pre>
 */
package com.home.simplewarehouse.model;