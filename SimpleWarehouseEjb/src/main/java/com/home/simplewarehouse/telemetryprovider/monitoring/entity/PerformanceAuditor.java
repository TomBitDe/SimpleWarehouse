package com.home.simplewarehouse.telemetryprovider.monitoring.entity;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.InvocationContext;

public class PerformanceAuditor {
	@Inject
	MonitoringRessource monitoring;

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
