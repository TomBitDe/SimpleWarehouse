package com.home.simplewarehouse.jnditree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
@Startup
@LocalBean
@Path("/JndiTree")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
/**
 * Fetch the JNDI tree information.
 */
public class JndiTree {
	private static final Logger LOG = LogManager.getLogger(JndiTree.class);

	private InitialContext ctx;

	/**
	 * Fetch all JNDI tree context as a text string (little bit formatted)
	 *
	 * @return the JNDI tree context
	 */
	@GET
	@Path("/treeAsText")
	@Produces(MediaType.TEXT_PLAIN)
	public String getJndiTreeAsText() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("JNDI-Context-Listing\n");

		return fetchTreeAsText(buffer).toString();
	}

	/**
	 * Fetch the JNDI tree context entries that contain the given keyword as a text string (little bit formatted)
	 *
	 * @param key the keyword
	 *
	 * @return the related JNDI tree context entries
	 */
	@GET
	@Path("/treeAsText/{key}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getEntryAsText(@PathParam("key") String key) {

		BufferedReader bufReader = new BufferedReader(new StringReader(fetchTreeAsText(new StringBuffer()).toString()));
		StringBuffer retBuffer = new StringBuffer();

		retBuffer.append("JNDI-Context-Listing for KEY=" + key + "\n\n");

		try {
			String line=null;

			while( (line=bufReader.readLine()) != null )
			{
				if (line.contains(key)) {
					retBuffer.append(line + '\n');
				}
			}
		}
		catch (IOException ioex) {
			LOG.fatal(ioex.getMessage());
		}

		return retBuffer.toString();
	}

	/**
	 * Fetch the JNDI tree in the given buffer
	 *
	 * @param buffer the buffer to use
	 *
	 * @return the buffer containing the tree context
	 */
	private StringBuffer fetchTreeAsText(StringBuffer buffer) {
		try {
			ctx = new InitialContext();

			writeJndiContextAsStringBuffer(buffer, ctx, "");
		}
		catch (NamingException nex) {
			LOG.fatal(nex.getMessage());
		}

		return buffer;
	}

	/**
	 * Write the JNDI context into a StringBuffer
	 *
	 * @param buffer a buffer to use
	 * @param ctx    the context ( InitialContext() ) to work on
	 * @param name   prefix for the printout
	 */
	private void writeJndiContextAsStringBuffer(StringBuffer buffer, Context ctx, String name) {
		buffer.append("\n");

		try {
			NamingEnumeration<Binding> en = ctx.listBindings("");

			while (en != null && en.hasMoreElements()) {
				Binding binding = en.next();

				String name2 = name + ((name.length() > 0) ? "/" : "") + binding.getName();
				buffer.append(name2 + ": " + binding.getClassName() + "\n");

				if (binding.getObject() instanceof Context) {
					writeJndiContextAsStringBuffer(buffer, (Context) binding.getObject(), name2);
				}
			}
		}
		catch (NamingException ex) {
			LOG.fatal(ex.getLocalizedMessage());
		}
	}
}
