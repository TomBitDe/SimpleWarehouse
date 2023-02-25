package com.home.simplewarehouse.utils.telemetryprovider.requestcounter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is only a sample how to integrate the PerformanceAuditor.
 */
public class InnerClasses {
	private static final Logger LOG = LogManager.getLogger(InnerClasses.class);
	
	/**
	 * Cuonstructor
	 */
	public InnerClasses() {
		super();
    	LOG.trace("--> InnerClasses()");
    	LOG.trace("<-- InnerClasses()");
	}
	
	/**
	 * The inner class
	 */
	class Inner1 {
	    private int x = 5;
	}

	/**
	 * Just to call Inner
	 */
	public void do1() {
    	LOG.trace("--> do1()");
 
    	int x = 1 + new Inner1().x;
	    
	    LOG.info("x is {}", x);

		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					new Inner1();
				}
				catch (Exception e) {
					LOG.fatal(e.getMessage());
				}
			}
		};
		
		r.run();
		
	   	LOG.trace("<-- do1()");
	}
}
