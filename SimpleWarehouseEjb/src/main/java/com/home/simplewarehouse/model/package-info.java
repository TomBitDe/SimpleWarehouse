/**
 * Entity model for the Simple Warehouse application.<br>
 *
 * <pre>{@code
 * 
 *      +--------------+                         +----------------+
 *      | HandlingUnit |                         | Location       |
 *      |              |                         |                |
 *      |  id          |                         |  locationId    |
 *      |  location    | <-- 0:1 ------- 0:N --> |  handlingUnits |
 *      |  ...         |                         |  ...           |
 *      +--------------+                         +----------------+
 *      
 * }</pre>
 */
package com.home.simplewarehouse.model;