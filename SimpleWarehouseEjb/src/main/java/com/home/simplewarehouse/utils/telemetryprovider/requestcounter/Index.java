package com.home.simplewarehouse.utils.telemetryprovider.requestcounter;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity.Diagnostics;

import org.apache.logging.log4j.Logger;

/**
 * The Index as Model.
 */
@Model
public class Index {
	private static AtomicLong requestCounter = new AtomicLong(0);

	@Inject
	Event<Diagnostics> diagnostics;

	@Inject
	GoodMorning gm;
	
	/**
	 * Create this Index
	 */
	public Index() {
		super();
	}

	/**
	 * Handle a new request
	 */
	@PostConstruct
	public void onNewRequest() {
		final Diagnostics requests = Diagnostics.with("request", requestCounter.incrementAndGet());
		diagnostics.fire(requests);

		try {
            Cache<String, Logger> cache = CacheBuilder.newBuilder().build();
            cache.put("", null);
        }
		catch (Exception e) {
            e.printStackTrace();
        }
	}

	/**
	 * Just for test call say
	 * 
	 * @return <code>null</code> in any case
	 */
	public Object ok() {
		gm.say();
		return null;
	}

	/**
	 * Just for test call tooEarly
	 * 
	 * @return <code>null</code> in any case
	 */
	public Object tooEarly() {
		gm.tooEarly();
		return null;
	}
}
