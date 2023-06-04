package com.home.simplewarehouse.utils.configurator.base;

import java.lang.management.ManagementFactory;
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
import javax.ejb.Startup;
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
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.patterns.singleton.simplecache.CacheDataProvider;
import com.home.simplewarehouse.patterns.singleton.simplecache.ValueSourceEntry;
import com.home.simplewarehouse.utils.configurator.jmxrestview.ConfiguratorMXBean;

/**
 * The Configurator class.
 */
@Singleton
@Startup
@LocalBean
@Path("configuration")
@javax.ws.rs.Produces({MediaType.TEXT_PLAIN})
public class Configurator implements ConfiguratorMXBean {
	private static final Logger LOG = LogManager.getLogger(Configurator.class);
	
	private static final String SOURCE_DEFAULT = "Default";
	private static final String SOURCE_CONFIGURATOR = "Configurator";

	/**
	 * The current configuration
	 */
	private Map<String, ValueSourceEntry> configuration;

	/**
	 * Configurations from other providers like DbTable or Properties
	 */
	@Inject
	private Instance<CacheDataProvider> configurationProvider;

	private Set<String> unconfiguredFields = new HashSet<>();

	private ObjectName objectName;
	private MBeanServer platformMBeanServer;

	@Resource
	SessionContext sc;
	
	/**
	 * Create this Configurator
	 */
	public Configurator() {
		super();
	}
	
	/**
	 * Give some general configurable defaults here
	 * 
	 * @param origin the configuration Map to fill with this defaults
	 */
	private static void setDefaults(Map<String, ValueSourceEntry> origin) {
		origin.put("com.home.simplewarehouse.timed.TimerJpaSessionsBean1.secondsConfig", new ValueSourceEntry("*/5", SOURCE_DEFAULT));
		origin.put("com.home.simplewarehouse.timed.TimerJpaSessionsBean2.secondsConfig", new ValueSourceEntry("*/8", SOURCE_DEFAULT));
	}

	/**
	 * Fetch the Configuration items, merge with a custom configuration and register in JMX
	 */
	@PostConstruct
	public void fetchConfigurationAndRegisterInJMX() {
		this.configuration = new HashMap<>();
		
		setDefaults(this.configuration);

		mergeWithCustomConfiguration();

		registerInJMX();
	}

	/**
	 * At startup time put all the configurations from providers to the initial
	 * (defaults) configuration
	 */
	private void mergeWithCustomConfiguration() {
		// Loop over all providers
		for (CacheDataProvider provider : configurationProvider) {
			// Load data from this provider
			Map<String, ValueSourceEntry> customConfiguration = provider.loadCacheData();
			// Put all to the current configuration
			this.configuration.putAll(customConfiguration);
		}
	}

	/**
	 * Sync configuration with custom configurations from other sources<br>
	 * <br>
	 * Entries in configuration are always master. Other sources are synced with the
	 * configurator. SOURCE_DEFAULT and SOURCE_CONFIGURATOR always remain here.
	 * Configurations from other sources are only put to synced if not already in the
	 * configuration.
	 * 
	 * @return the synced configuration
	 */
	private Map<String, ValueSourceEntry> syncWithCustomConfiguration() {
		Map<String, ValueSourceEntry> synced = new HashMap<>();
		
		LOG.info("+++ CONNFIGURATION SYNC START ++++++++++++++++++++++++++++++++++++");
		LOG.info("--- STEP ONE -----------------------------------------------------");
		final Set<String> configurationKeys = configuration.keySet();
		for (String configurationKey : configurationKeys) {
			final String source = configuration.get(configurationKey).getSource();
			
			if (source.equals(SOURCE_DEFAULT) || source.equals(SOURCE_CONFIGURATOR)) {
				LOG.info("Put for key=[{}] source=[{}]", configurationKey, source);
				synced.put(configurationKey, configuration.get(configurationKey));
			}
			else {
				LOG.info("No put for key=[{}] source=[{}]", configurationKey, source);
			}
		}
		LOG.info("--- AFTER STEP ONE -----------------------------------------------");
		synced.forEach((k,v) -> LOG.info("Key=[{}] Value=[{}]", k, v));
		
		LOG.info("--- STEP TWO -----------------------------------------------------");
		for (CacheDataProvider provider : configurationProvider) {
			Map<String, ValueSourceEntry> provided = provider.loadCacheData();

			final Set<String> providedKeys = provided.keySet();
						
			for (String providedKey : providedKeys) {
				if (synced.containsKey(providedKey)) {
					LOG.info("NO REPLACE for key=[{}] {}", providedKey, provided.get(providedKey));
					LOG.info("SYNCED ALREADY has key=[{}] {}", providedKey, synced.get(providedKey));
				}
				else {
					LOG.info("Put for key=[{}] {}", providedKey, provided.get(providedKey));
					synced.put(providedKey, provided.get(providedKey));						
				}
			}
		}
		LOG.info("--- AFTER STEP TWO -----------------------------------------------");
		synced.forEach((k,v) -> LOG.info("Key=[{}] Value=[{}]", k, v));
		LOG.info("+++ CONNFIGURATION SYNC END ++++++++++++++++++++++++++++++++++++++");
		
		return synced;
	}

	@Override
	public Map<String, ValueSourceEntry> getConfigurationMap() {
		this.configuration = syncWithCustomConfiguration();
		return this.configuration;
	}

	/**
	 * Gets the Configuration
	 * 
	 * @return the Configuration as String
	 */
	@GET
    @PermitAll
	@Override
	public String getConfiguration() {
		this.configuration = syncWithCustomConfiguration();
		return this.configuration.toString();
	}

	/**
	 * Gets the Configuation entry for the given key
	 * 
	 * @param key the key value
	 * 
	 * @return the entry as String
	 */
	@GET
    @PermitAll
	@Path("{key}")
	@Override
	public String getEntry(@PathParam("key") String key) {
		String ret = null;

		if (this.configuration.get(key) != null) {
			ret = this.configuration.get(key).getValue();
		}

		return ret;
	}

	/**
	 * Gets the Configuation entry for the given key
	 * 
	 * @param key the key value
	 * @param defaultValue the default value if nothing found for the key
	 * 
	 * @return the entry as String
	 */
	@GET
    @PermitAll
	@Path("{key}/{defaultValue}")
	@Override
	public String getEntry(@PathParam("key") String key, @PathParam("defaultValue") String defaultValue) {
		String ret = null;
		
		if (this.configuration.get(key) != null) {
			ret = this.configuration.get(key).getValue();
		}
		
		if (ret == null) {
			ret = defaultValue;
		}
		
		return ret;
	}

	/**
	 * Put this entry to the Configuration (add or replace)
	 * 
	 * @param key the key value for this entry
	 * @param value the entries value
	 */
	@PUT
    @PermitAll
	@Path("{key}/{value}")
	@Consumes({MediaType.TEXT_PLAIN})
	@Override
	public void putEntry(@PathParam("key") String key, @PathParam("key") String value) {
		this.configuration.put(key, new ValueSourceEntry(value, SOURCE_CONFIGURATOR));
	}

	/**
	 * Delete the configuration entry for the given key
	 * 
	 * @param key the key value
	 */
	@DELETE
    @PermitAll
	@Path("{key}")
	@Override
	public void deleteEntry(String key) {
		this.configuration.remove(key);
	}
	
	/**
	 * Register this Configurator in JMX
	 */
	private void registerInJMX() {
	    try {
	        objectName = new ObjectName("SimpleWarehouseConfigurator:type=" + this.getClass().getName());
	        platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
	        Configurator thiz = sc.getBusinessObject(Configurator.class);
	        platformMBeanServer.registerMBean(thiz, objectName);
	    }
	    catch (Exception ex) {
	    	throw new IllegalStateException("Problem during registration of Monitoring into JMX:" + ex);
	    }
	}

	/**
	 * Unregister this Configurator from JMX
	 */
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

			return clazzName + "." + memberName;
		}
	}

	/**
	 * Gets a configuration entry value as String
	 * 
	 * @param ip the injection point
	 * 
	 * @return the value
	 * 
	 * @throws NotConfiguredException in case there is no configuration entry
	 */
	@javax.enterprise.inject.Produces
	public String getString(InjectionPoint ip) throws NotConfiguredException {
		String fieldName = obtainConfigurableName(ip);

		return getValueForKey(fieldName);
	}

	/**
	 * Gets a configuration entry value as Integer
	 * 
	 * @param ip the injection point
	 * 
	 * @return the value
	 * 
	 * @throws NotConfiguredException in case there is no configuration entry
	 */
	@javax.enterprise.inject.Produces
	public int getInteger(InjectionPoint ip) throws NotConfiguredException {
		String stringValue = getString(ip);

		if (stringValue == null) {
			throw new NotConfiguredException();
		}
		return Integer.parseInt(stringValue);
	}

	private String getValueForKey(String fieldName) throws NotConfiguredException {
		String valueForFieldName = null;
		
		if (this.configuration.get(fieldName) != null) {
			valueForFieldName = this.configuration.get(fieldName).getValue();
		}
		
		if (valueForFieldName == null) {
			this.unconfiguredFields.add(fieldName);
			LOG.fatal("No configuration in Configurator for fieldName [{}]", fieldName);
			throw new NotConfiguredException("No configuration in Configurator for fieldName [" + fieldName + ']');
		}

		return valueForFieldName;
	}
}
