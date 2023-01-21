/**
 * Location related classes.
 * 
 * <h3>Concepts</h3>
 * The location covers dimension, status and access control. Details are in the following.
 * 
 * <h3>Location dimension</h3>
 * Any attributes that define the dimension limitations for a location like capacity, height, weight.
 * 
 * <h4>Capacity</h4>
 * A capacity of 0 means that there is no limitation concerning the number of handling units that can
 * be dropped on a location.<br>
 * If the capacity is greater than 0 then this is the maximum capacity for the location.
 * 
 * <h4>Weight</h4>
 * A weight of 0 means that there is no limitation concerning the total weight of handling units that can
 * be dropped on a location.<br>
 * If the weight is greater than 0 then this is the maximum weight of all handling units dropped on a location.<br>
 * Rule is: <strong>total weight less then maximum weight</strong>.
 * 
 * <h4>Height</h4>
 * A height of {@link com.home.simplewarehouse.model.HeightCategory HeightCategory.NOT_RELEVANT} means that height limitations do not exist.
 * 
 * <h4>Length</h4>
 * A length of {@link com.home.simplewarehouse.model.LengthCategory LengthCategory.NOT_RELEVANT} means that length limitations do not exist.
 * 
 * <h4>Width</h4>
 * A width of {@link com.home.simplewarehouse.model.WidthCategory WidthCategory.NOT_RELEVANT} means that width limitations do not exist.
 * 
 * <h3>Location status</h3>
 * Any attributes that define the device access limitations for a location like error, LTOS, lock. 
 * 
 * <h4>Error status</h4>
 * This is a status automatically set/reset.
 * The following status values exist {@link com.home.simplewarehouse.model.ErrorStatus ErrorStatus} 
 * 
 * <h4>LTOS status</h4>
 * LTOS means Long Time Out of Service. This status is manually set/reset. It never happens automatically.
 * The following status values exist {@link com.home.simplewarehouse.model.LtosStatus LtosStatus} 
 * 
 * <h4>Lock status</h4>
 * The lock status is manually set/reset.
 * The following status values exist {@link com.home.simplewarehouse.model.LockStatus LockStatus} 
 * 
 * <h3>Storage location access FIFO, LIFO, RANDOM</h3>
 * A storage location can contain zero, one or more handling units. If a location contains
 * more than one handling unit then access to the handling units can be limited. For that
 * reason different kinds of locations exist. In the following examples hu1, hu2, hu3, ... 
 * are different handling units.

 * <h4>FIFO</h4>
 * <br>
 * <pre>{@code
 *                                             
 *                        IN       FIFO sequence      OUT
                       
 *   First in (hu1)       hu1                            
 *                        -------------------------------
 *                     
 *                        hu2                         hu1                       
 *                        -------------------------------
 *                     
 *                        hu3                    hu2  hu1                  
 *                        -------------------------------
 *                     
 *                                          hu3  hu2  hu1                   
 *                        -------------------------------
 *                     
 *                                               hu3  hu2   hu1   First out (hu1)                         
 *                        -------------------------------
 * }</pre>
 * <h4>LIFO</h4>
 * <br>
 * <pre>{@code
 *                          
 *                        IN/OUT      LIFO sequence
 *
 *                        hu1                            |
 *                        -------------------------------+
 * 
 *                        hu2  hu1                       |
 *                        -------------------------------+
 *      
 *  Last in (hu3)         hu3  hu2  hu1                  |
 *                        -------------------------------+
 *      
 *  First out (hu3) hu3   hu2  hu1                       |
 *                        -------------------------------+
 *      
 *                  hu2   hu1                            |
 *                        -------------------------------+
 *                                                       
 *                  hu1                                  |
 *                        -------------------------------+
 * }</pre>
 * <h4>RANDOM</h4>
 * <br>
 * <pre>{@code
 *                        RANDOM no sequence
 *                       
 *                        +--Floor area-----------------+
 *                        |                             |
 *       No limits        |  hu1                        |
 *       for access       |         hu2    hu4          |
 *                        |                             |
 *                        |      hu3             hu5    |
 *                        +-----------------------------+
 *                        
 * }</pre>
 *<br>
 */
package com.home.simplewarehouse.location;