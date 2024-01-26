package com.home.simplewarehouse.beans;

import java.io.PrintWriter;
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

@Named
public class JndiContextBean {
	private static final Logger LOG = LogManager.getLogger(JndiContextBean.class);
	
	private String servletOutput;

    @PostConstruct
    public void init() {
    	StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        try {
	        Context ctx = new InitialContext();;

	        printJndiContextAsHtmlList( writer, ctx, "" );

	        ctx.close();
	        
	        writer.flush();
	        
	        servletOutput = out.toString();
        }
		catch (NamingException ex) {
			LOG.error(ex.getLocalizedMessage());
			servletOutput = ex.getLocalizedMessage();
		}
    }

	public String getServletOutput() {
		return servletOutput;
	}

	public void setServletOutput(String servletOutput) {
		this.servletOutput = servletOutput;
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
