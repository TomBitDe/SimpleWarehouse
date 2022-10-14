package com.home.simplewarehouse.war.showjnditree;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Show the JNDI content.
 */
public class JndiContextServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;
   
   private static final Logger LOG = Logger.getLogger(JndiContextServlet.class.getName());

   /**
	* Mandatory default constructor
	*/
   public JndiContextServlet() {
	   super();
   }
	
   @Override
   protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
   {
      PrintWriter writer = resp.getWriter();
      try {
         writer.println( "<html><head><title>JNDI-Context-Listing</title></head>" );
         writer.println( "<body><h3>JNDI-Context-Listing</h3>" );

         Context ctx = new InitialContext();

         printJndiContextAsHtmlList( writer, ctx, "" );
         ctx.close();

         writer.println( "</body>" );
         writer.println( "</html>" );
         writer.flush();
      }
      catch( Exception ex ) {
         throw new ServletException( ex );
      }
      finally {
         writer.close();
      }
   }

   /**
    * Do print the JNDI context as HTML for the browser
    *
    * @param writer a writer to use
    * @param ctx the context ( InitialContext() ) to work on
    * @param name prefix for the printout
    */
   public void printJndiContextAsHtmlList( PrintWriter writer, Context ctx, String name )
   {
      writer.println( "<ul>" );

      try {
         NamingEnumeration<Binding> en = ctx.listBindings( "" );

         while( en != null && en.hasMoreElements() ) {
            Binding binding = en.next();

            String name2 = name + (( name.length() > 0 ) ? "/" : "") + binding.getName();
            writer.println( "<li><u>" + name2 + "</u>: " + binding.getClassName() + "</li>" );

            if( binding.getObject() instanceof Context ) {
               printJndiContextAsHtmlList( writer, (Context) binding.getObject(), name2 );
            }
         }
      }
      catch( NamingException ex ) {
    	  LOG.severe(ex.getLocalizedMessage());
      }

      writer.println( "</ul>" );
   }
}
