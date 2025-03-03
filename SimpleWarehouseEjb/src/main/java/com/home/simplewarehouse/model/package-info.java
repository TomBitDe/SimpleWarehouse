/**
 * Entity model for the Simple Warehouse application.<br>
 *
 * <pre>{@code
 * 
 *                                                  +--------------+ 
 *                                                  | Zone         |
 *                                                  |              |
 *                                                  |  zoneId      |
 *                                                  |  locations   |
 *                                                  |              |
 *                                                  +--------------+
 *                                                          ^
 *                                                         0:N  
 *                                                          |
 *                                                          |
 *                                                          |
 *                                                         0:M
 *                                                          v
 *      +--------------+                         +--------------------------+
 *      | HandlingUnit |                         | Location                 |
 *      |              |                         |                          |
 *      |  id          |                         |  locationId              |<------------+-------------------+--------------------+
 *      |  location    | <-- 0:1 ------- 0:N --> |  handlingUnits           |             |                   |                    |
 *      |  locaPos     |                         |  locationStatus          |             |                   |                    |
 *      |  ...         |                         |  dimension               |             |                   |                    |
 *      |  base        |<--0:1-+                 |  ...                     |             |                   |                    |
 *      |  contains    |       |                 |                          |             |                   |                    |
 *      |  ...         |       |                 +--------------------------+             |                   |                    |
 *      +--------------+       |                              |                           |                   |                    |
 *             ^               |                              |                          1:1                 1:1                  1:1
 *             |               |                       +-------------+                    |                   |                    |
 *            0:N              |                       |             |                    v                   v                    v
 *             +---------------+             +--------------+  +--------------+  +----------------+  +-----------------+  +-----------------+
 *                                           | FifoLocation |  | LifoLocation |  | LocationStatus |  | Dimension       |  | Position        |
 *                                           |              |  |              |  |                |  |                 |  |                 |
 *                                           |  ...         |  |  ...         |  |  locationId    |  |  locationId     |  |  locationId     |
 *                                           +--------------+  +--------------+  |  errorStatus   |  |  capacity       |  |                 |
 *                                                                               |  ...           |  |  ...            |  |  ...            |
 *                                                                               +----------------+  +-----------------+  +-----------------+
 *                                                                                                                                 |                
 *                                                                                                                                 |                
 *                                                                                                            +--------------------+-------------------+         
 *                                                                                                            |                    |                   |         
 *                                                                                                   +-----------------+  +-----------------+  +-----------------+ 
 *                                                                                                   | LogicalPosition |  | RelativPosition |  | AbsolutPosition |
 *                                                                                                   |                 |  |                 |  |                 |
 *                                                                                                   |  positionId     |  |  xCoord         |  |  xCoord         |
 *                                                                                                   |                 |  |  yCoord         |  |  yCoord         |
 *                                                                                                   |                 |  |  zCoord         |  |  zCoord         |
 *                                                                                                   +-----------------+  +-----------------+  +-----------------+
 *
 * }</pre>
 */
package com.home.simplewarehouse.model;