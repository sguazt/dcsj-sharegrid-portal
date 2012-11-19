/*
 * Copyright (C) 2008  Distributed Computing System (DCS) Group, Computer
 * Science Department - University of Piemonte Orientale, Alessandria (Italy).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.unipmn.di.dcs.sharegrid.web.management.naming;

import java.util.Hashtable;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.InitialContextFactory;
//import org.apache.naming.SelectorContext;
//import org.apache.naming.NamingContext;
//import org.apache.naming.ContextBindings;

/**
 * Context factory for the "java:" namespace.
 * <p>
 * <b>Important note</b> : This factory MUST be associated with the "java" URL
 * prefix, which can be done by either :
 * <ul>
 * <li>Adding a 
 * java.naming.factory.url.pkgs=org.apache.catalina.util.naming property
 * to the JNDI properties file</li>
 * <li>Setting an environment variable named Context.URL_PKG_PREFIXES with 
 * its value including the org.apache.catalina.util.naming package name. 
 * More detail about this can be found in the JNDI documentation : 
 * {@link javax.naming.spi.NamingManager#getURLContext(java.lang.String, java.util.Hashtable)}.</li>
 * </ul>
 * 
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */

public class ManagementContextFactory implements ObjectFactory, InitialContextFactory
{
	public static final String MAIN = "initialContext";

	/**
	 * Initial context.
	 */
	protected static Context initialContext = null;

	/**
	 * Crete a new Context's instance.
	 */
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws NamingException
	{
		if ((ManagementContextBindings.isThreadBound()) || (ManagementContextBindings.isClassLoaderBound()))
		{
			return new ManagementSelectorContext(environment);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Get a new (writable) initial context.
	 */
	public Context getInitialContext(Hashtable environment) throws NamingException
	{
		if (ManagementContextBindings.isThreadBound() || (ManagementContextBindings.isClassLoaderBound()))
		{
			// Redirect the request to the bound initial context
			return new ManagementSelectorContext(environment, true);
		}
		else
		{
			// If the thread is not bound, return a shared writable context
			if (initialContext == null)
			{
				initialContext = new ManagementNamingContext(environment, MAIN);
			}
			return initialContext;
		}
	}
}
