/**
 * Entity model for the Simple Warehouse application.<br>
 *
 * <pre>{@code
 * 
 *      +--------------+                         +--------------------------+
 *      | HandlingUnit |                         | Location                 |
 *      |              |                         |                          |
 *      |  id          |                         |  locationId              |<-----+---------------------+
 *      |  location    | <-- 0:1 ------- 0:N --> |  handlingUnits           |      |                     |
 *      |  ...         |                         |  locationStatus          |      |                     |
 *      +--------------+                         |  locationCharacteristics |      |                     |    
 *                                               |  ...                     |      |                     |
 *                                               |                          |      |                     |
 *                                               +--------------------------+      |                     |
 *                                                                                1:1                   1:1
 *                                                                                 |                     |
 *                                                                                 v                     v
 *                                                                           +----------------+    +-------------------------+
 *                                                                           | LocationStatus |    | LocationCharacteristics |
 *                                                                           |                |    |                         |
 *                                                                           |  locationId    |    |  locationId             |
 *                                                                           |  errorStatus   |    |  capacity               |
 *                                                                           |  ...           |    |  ...                    |
 *                                                                           +----------------+    +-------------------------+
 *                                                                  
 * }</pre>
 */
package com.home.simplewarehouse.model;