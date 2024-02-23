package com.home.simplewarehouse.utils.telemetryprovider.monitoring.boundary;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity.Diagnostics;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity.ExceptionStatistics;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity.Invocation;

/**
 * The Monitoring Resource 
 */
@Singleton
@Startup
@LocalBean
@Path("monitoring")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class MonitoringResource implements MonitoringResourceMXBean {

    private MBeanServer platformMBeanServer;
    private ObjectName objectName = null;

    private ConcurrentHashMap<String, Invocation> methods = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> diagnostics = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<String> exceptions = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<String, AtomicInteger> exceptionStatistics = new ConcurrentHashMap<>();
    private AtomicLong exceptionCount;

    @Resource
    private SessionContext sc;
    
    /**
     * Create this Monitoring Resource
     */
    public MonitoringResource() {
    	super();
    }

    @Override
	@GET
    @Path("slowestMethods/{max}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Invocation> getSlowestMethods(@QueryParam("max") @DefaultValue("50") int maxResult) {
    	List<Invocation> list = new ArrayList<>(methods.values());

    	Collections.sort(list);
    	Collections.reverse(list);
    	if (list.size() > maxResult) {
    		return list.subList(0, maxResult);
    	}
    	else {
    		return list;
    	}
    }

    @GET
    @Path("exceptionCount")
    @Produces(MediaType.TEXT_PLAIN)
    @Override
	public String getNumberOfExceptions() {
		return String.valueOf(exceptionCount.get());
	}

	@Override
	public List<Invocation> getSlowestMethods() {
		return getSlowestMethods(50);
	}

	@Override
	public Map<String, String> getDiagnostics() {
		return diagnostics;
	}

	@Override
	@GET
	@Path("diagnostics")
	@Produces(MediaType.TEXT_PLAIN)
	public String getDiagnosticsAsString() {
		return getDiagnostics().toString();
	}

	@GET
	@Path("exceptions")
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public String getExceptions() {
		StringBuilder message = new StringBuilder();

		for (int i = exceptions.size()-1; i >= 0; i--) {
			message.append(exceptions.get(i));
			message.append("\n");
		}

		return message.toString();
	}

	@Override
	@GET
	@Path("exceptionStatistics")
	@Produces(MediaType.TEXT_PLAIN)
	public String getExceptionStatisticsAsString() {
		StringBuilder message = new StringBuilder();

		Set<Entry<String, AtomicInteger>> entrySet = exceptionStatistics.entrySet();
		for (Entry<String, AtomicInteger> entry : entrySet) {
			message.append(entry.getKey());
			message.append("--->");
			message.append(entry.getValue());
			message.append("\n");
		}

		return message.toString();
	}
	
	@Override
	@GET
	@Path("exceptionList")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public List<ExceptionStatistics> getExceptionStatisticsAsList() {
    	List<ExceptionStatistics> list = new ArrayList<>();

		Set<Entry<String, AtomicInteger>> entrySet = exceptionStatistics.entrySet();
		for (Entry<String, AtomicInteger> entry : entrySet) {
			ExceptionStatistics statisticsEntry = new ExceptionStatistics(entry.getKey(),
					entry.getValue().get());
			
			list.add(statisticsEntry);
		}
		
    	return list;
	}

	@Override
	public Map<String, Integer> getExceptionStatistics() {
		Map<String, Integer> statistics = new HashMap<>();

		for (Entry<String, AtomicInteger> entry : exceptionStatistics.entrySet()) {
			statistics.put(entry.getKey(), entry.getValue().intValue());
		}

		return statistics;
	}

	@Override
	@GET
	@Path("diagnostics/{key}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getDiagnosticsForKey(@PathParam("key") String key) {
		return getDiagnostics().get(key);
	}

	/**
	 * Add an invocation
	 * 
	 * @param invocation the invocation
	 */
	public void add(Invocation invocation) {
		String methodName = invocation.getMethodName();

		if (methods.containsKey(methodName)) {
			Invocation existing = methods.get(methodName);
			if (existing.isSlowerThan(invocation)) {
				return;
			}
		}

		methods.put(methodName, invocation);
	}

	/**
	 * Adds an Invocation to this Monitor Resource
	 * 
	 * @param methodName the method name
	 * @param performance the methods performance
	 */
	public void add(String methodName, long performance) {
		Invocation invocation = new Invocation(methodName, performance);

		this.add(invocation);
	}

	/**
	 * Process an exception
	 * 
	 * @param methodName the method that throws the exception
	 * @param e the exception
	 */
	public void exceptionOccurred(String methodName, Exception e) {
		exceptionCount.incrementAndGet();
		final String exception = e.toString();
		exceptions.add(exception + "-->" + methodName);

		if (exceptionStatistics.containsKey(methodName)) {
			exceptionStatistics.get(methodName).incrementAndGet();
		}
		else {
			exceptionStatistics.put(methodName, new AtomicInteger(1));
		}
	}

    /**
     * Register the Monitoring Resource in JMX
     */
    @PostConstruct
    public void registerInJMX() {
        this.exceptionCount = new AtomicLong();
        try {
            objectName = new ObjectName("SimpleWarehouseMonitoring:type=" + this.getClass().getName());
            platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            platformMBeanServer.registerMBean(sc.getBusinessObject(MonitoringResource.class), objectName);
        }
        catch (IllegalStateException | InstanceAlreadyExistsException | MBeanRegistrationException |
        		MalformedObjectNameException | NotCompliantMBeanException e) {
            throw new IllegalStateException("Problem during registration of Monitoring into JMX:" + e);
        }
    }

    /**
     * Listen on a new Diagnostics and do the needed
     * 
     * @param diagnostics the Diagnostics 
     */
    public void onNewDiagnostics(@Observes Diagnostics diagnostics) {
        Map<String, String> map = diagnostics.asMap();

        if (map != null) {
            this.diagnostics.putAll(map);
        }
    }

    @Override
    @DELETE
	@Path("clear")
    public void clear() {
        methods.clear();
        exceptionCount.set(0);
        exceptions.clear();
        exceptionStatistics.clear();
        diagnostics.clear();
    }

    /**
     * Unregister the Monitoring Resource from JMX
     */
    @PreDestroy
    public void unregisterFromJMX() {
        try {
            platformMBeanServer.unregisterMBean(this.objectName);
        }
        catch (InstanceNotFoundException | MBeanRegistrationException e) {
            throw new IllegalStateException("Problem during unregistration of Monitoring into JMX:" + e);
        }
    }
}
