package com.home.simplewarehouse.utils.configurator.base;

import java.lang.management.ManagementFactory;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.utils.configurator.jmxrestview.ConfiguratorMXBean;
import com.home.simplewarehouse.utils.configurator.pluggable.ConfigurationProvider;

@LocalBean
@Singleton
@Path("configuration")
@javax.ws.rs.Produces({MediaType.TEXT_PLAIN})
public class Configurator implements ConfiguratorMXBean {
	private static final Logger LOG = LogManager.getLogger(Configurator.class);

	private Map<String, String> configuration;

	@Inject
	private Instance<ConfigurationProvider> configurationProvider;

	private Set<String> unconfiguredFields = new HashSet<>();

	private ObjectName objectName;
	private MBeanServer platformMBeanServer;

	@Resource
	SessionContext sc;

	@PostConstruct
	public void fetchConfiguration() {
		this.configuration = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;

		    {
		    	// Default values are here
			    put("com.home.gftest.latestarter.ControllerSessionTimer.controllerSessionRuns", "*/8");
			    put("pingControllerRuns", "*/5");
		    }
		};

		// Override defaults with explicitly configured entries
		this.configuration.put("com.home.gftest.latestarter.ControllerSessionTimer.controllerSessionRuns", "*/3");

		mergeWithCustomConfiguration();

		registerInJMX();
	}

	private void mergeWithCustomConfiguration() {
		for (ConfigurationProvider provider : configurationProvider) {
			Map<String, String> customConfiguration = provider.getConfiguration();
			this.configuration.putAll(customConfiguration);
		}
	}

	@Override
	public Map<String, String> getConfigurationMap() {
		return this.configuration;
	}

	@GET
    @PermitAll
	public String getConfiguration() {
		return this.configuration.toString();
	}

	@GET
    @PermitAll
	@Path("{key}")
	public String getEntry(@PathParam("key") String key) {

		return configuration.get(key);
	}

	@PUT
    @PermitAll
	@Path("{key}")
	@Consumes({MediaType.TEXT_PLAIN})
	public Response addEntry(@PathParam("key") String key, String value, @Context UriInfo uriInfo) {
		Response response = null;

		if (this.configuration.containsKey(key)) {
			response = Response.noContent().build();
		}
		else {
			URI uri = uriInfo.getAbsolutePathBuilder().build(key);
			response = Response.created(uri).build();
		}

		return response;
	}

	@DELETE
    @PermitAll
	@Path("{key}")
	public Response deleteEntry(@PathParam("key") String key) {
		this.configuration.remove(key);

		return Response.noContent().build();
	}

	void registerInJMX() {
	    try {
	        objectName = new ObjectName("Configurator:type=" + this.getClass().getName());
	        platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
	        Configurator thiz = sc.getBusinessObject(Configurator.class);
	        platformMBeanServer.registerMBean(thiz, objectName);
	    }
	    catch (Exception ex) {
	    	throw new IllegalStateException("Problem during registration of Monitoring into JMX:" + ex);
	    }
	}

	@PreDestroy
	public void unregisterFromJMX() {
		try {
			platformMBeanServer.unregisterMBean(this.objectName);
		}
		catch (Exception ex) {
			throw new IllegalStateException("Problem during unregistration of Monitoring into JMX:" + ex);
		}
	}

	private String obtainConfigurableName(InjectionPoint ip) {
 		AnnotatedField<?> field = (AnnotatedField<?>) ip.getAnnotated();
		Configurable configurable = field.getAnnotation(Configurable.class);

 		if (configurable != null) {
			return configurable.value();
		}
	 	else {
			String clazzName = ip.getMember().getDeclaringClass().getName();
			String memberName = ip.getMember().getName();
			String fqn = clazzName + "." + memberName;

			return fqn;
		}
	}

	@javax.enterprise.inject.Produces
	public String getString(InjectionPoint ip) throws NotConfiguredException {
		String fieldName = obtainConfigurableName(ip);

		return getValueForKey(fieldName);
	}

	@javax.enterprise.inject.Produces
	public int getInteger(InjectionPoint ip) throws NotConfiguredException {
		String stringValue = getString(ip);

		if (stringValue == null) {
			throw new NotConfiguredException();
		}
		return Integer.parseInt(stringValue);
	}

	private String getValueForKey(String fieldName) throws NotConfiguredException {
		String valueForFieldName = configuration.get(fieldName);

		if (valueForFieldName == null) {
			this.unconfiguredFields.add(fieldName);
			LOG.fatal("No configuration in Configurator for fieldName [{}]", fieldName);
			throw new NotConfiguredException("No configuration in Configurator for fieldName [" + fieldName + ']');
		}

		return valueForFieldName;
	}
}
