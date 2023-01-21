package com.home.simplewarehouse.utils.telemetryprovider.monitoring;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.InvocationContext;

import com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary.MonitoringResource;

/**
 * The Performance Auditor
 */
public class PerformanceAuditor {

    @Inject
    MonitoringResource monitoring;
    
    /**
     * Default constructor
     */
    public PerformanceAuditor() {
    	super();
    }

    /**
     * Measures the performance of an invocation
     * 
     * @param context the invocation context
     * 
     * @return the Object
     * 
     * @throws Exception on invocation exception
     */
    @AroundTimeout
    @AroundInvoke
    public Object measurePerformance(InvocationContext context) throws Exception {
        String methodName = context.getMethod().toString();
        long start = System.currentTimeMillis();

        try {
            return context.proceed();
        }
        catch (Exception e) {
            monitoring.exceptionOccurred(methodName, e);
            throw e;
        }
        finally {
            long duration = System.currentTimeMillis() - start;
            monitoring.add(methodName, duration);
        }
    }
}