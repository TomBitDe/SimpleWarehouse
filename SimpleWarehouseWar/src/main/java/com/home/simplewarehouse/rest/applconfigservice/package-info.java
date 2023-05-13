/**
 * REST service for application configuration handling.<br>
 * <p>
 * Show, create, update and delete application configuration data.<br>
 * Refresh the cache data.<br>
 * <p>
 * Test with your browsers integrated RESTClient functionality. Firefox has a RESTClient add-on.<br>
 * <br>
 * - options (OPTIONS http://localhost:8080/war/resources/ApplConfigRestService)<br>
 * - ping (GET http://localhost:8080/war/resources/ApplConfigRestService/Ping)<br>
 * - delete (DELETE http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa)<br>
 * - create (PUT http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa/Test1)<br>
 * - update (POST http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa/TestX)<br>
 * - content (GET http://localhost:8080/war/resources/ApplConfigRestService/Content)<br>
 * - content (GET http://localhost:8080/war/resources/ApplConfigRestService/Content/3/5)<br>
 * - entry (GET http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa)<br>
 * - exists (GET http://localhost:8080/war/resources/ApplConfigRestService/Exists/aaa)<br>
 * - count (GET http://localhost:8080/war/resources/ApplConfigRestService/Count)<br>
 * - refresh (GET http://localhost:8080/war/resources/ApplConfigRestService/Refresh)<br>
 */
package com.home.simplewarehouse.rest.applconfigservice;