package com.home.simplewarehouse.beans;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Bean class provides the Jndi context to display by JSF.
 */
@Named
public class JndiContextBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(JndiContextBean.class);
	
	private String context;
	
	/**
	 * Default constructor is not mandatory
	 */
	public JndiContextBean() {
		super();
	}

	/**
	 * Initialize the jndi content
	 */
    @PostConstruct
    public void init() {
    	StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        try {
	        Context ctx = new InitialContext();;

	        printJndiContextAsHtmlList( writer, ctx, "" );

	        ctx.close();
	        
	        writer.flush();
	        
	        context = out.toString();
        }
		catch (NamingException ex) {
			LOG.error(ex.getLocalizedMessage());
			context = ex.getLocalizedMessage();
		}
    }

    /**
     * Gets the jndi context
     * 
     * @return the context
     */
	public String getContext() {
		return context;
	}

	/**
	 * Sets the jndi context
	 * 
	 * @param val the value to set
	 */
	public void setContext(String val) {
		this.context = val;
	}

	/**
	 * Do print the JNDI context as HTML for the browser
	 *
	 * @param writer a writer to use
	 * @param ctx    the context ( InitialContext() ) to work on
	 * @param name   prefix for the printout
	 */
	public void printJndiContextAsHtmlList(PrintWriter writer, Context ctx, String name) {
		writer.println("<ul>");

		try {
			NamingEnumeration<Binding> en = ctx.listBindings("");

			while (en != null && en.hasMoreElements()) {
				Binding binding = en.next();

				String name2 = name + ((name.length() > 0) ? "/" : "") + binding.getName();
				writer.println("<li><u>" + name2 + "</u>: " + binding.getClassName() + "</li>");

				if (binding.getObject() instanceof Context) {
					printJndiContextAsHtmlList(writer, (Context) binding.getObject(), name2);
				}
			}
		} 
		catch (NamingException ex) {
			LOG.error(ex.getLocalizedMessage());
			writer.println(ex.getLocalizedMessage());
		}

		writer.println("</ul>");
	}
}
