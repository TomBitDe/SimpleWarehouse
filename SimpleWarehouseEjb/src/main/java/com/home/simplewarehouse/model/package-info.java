/**
 * Entity model for the Simple Warehouse application.<br>
 *
 * <pre>{@code
 * 
 *      +--------------+                         +--------------------------+
 *      | HandlingUnit |                         | Location                 |
 *      |              |                         |                          |
 *      |  id          |                         |  locationId              |<--------------+---------------------+
 *      |  location    | <-- 0:1 ------- 0:N --> |  handlingUnits           |               |                     |
 *      |  locaPos     |                         |  locationStatus          |               |                     |
 *      |  ...         |                         |  dimension               |               |                     |    
 *      +--------------+                         |  ...                     |               |                     |
 *                                               |                          |               |                     |
 *                                               +--------------------------+               |                     |
 *                                                            |                             |                     |
 *                                                            |                            1:1                   1:1
 *                                                     +-------------+                      |                     |
 *                                                     |             |                      v                     v
 *                                            +--------------+  +--------------+    +----------------+    +-----------------+
 *                                            | FifoLocation |  | LifoLocation |    | LocationStatus |    | Dimension       |
 *                                            |              |  |              |    |                |    |                 |
 *                                            |  ...         |  |  ...         |    |  locationId    |    |  locationId     |
 *                                            +--------------+  +--------------+    |  errorStatus   |    |  capacity       |
 *                                                                                  |  ...           |    |  ...            |
 *                                                                                  +----------------+    +-----------------+
 *                                                                  
 * }</pre>
 */
package com.home.simplewarehouse.model;