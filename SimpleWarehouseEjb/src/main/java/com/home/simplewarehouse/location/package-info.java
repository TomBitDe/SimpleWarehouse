/**
 * Location related classes.
 * <p>
 * <h3>Concepts</h3>
 * <br>
 * <h3>Location dimension</h3>
 * <br>
 * <h3>Location status</h3>
 * <br>
 * <h3>Storage location access FIFO, LIFO, RANDOM</h3>
 * A storage location can contain zero, one or more handling units. If a location contains
 * more than one handling units then access to the handling units can be limited. For that
 * reason different kinds of locations exist. In the following examples hu1, hu2, hu3, ... 
 * are different handling units.
 * <br>
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
 *<p>
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
 * <p>
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