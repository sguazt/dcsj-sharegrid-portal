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

package it.unipmn.di.dcs.sharegrid.web.management;

import it.unipmn.di.dcs.sharegrid.web.management.naming.ManagementContextFactory;
import it.unipmn.di.dcs.sharegrid.web.management.naming.ManagementContextFactoryBuilder;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.spi.NamingManager;
import javax.naming.StringRefAddr;

/**
 * Represents the environment for classes under the {@code management} package.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public final class ManagementEnv
{
	private boolean initialized = false;
	private InitialContext initCtx = null;
	private ManagementContextFactoryBuilder ctxFactoryBuild = null;

	/** A constructor. */
	protected ManagementEnv()
	{
		// empty
	}

	/** Returns the sole instance. */
	public static ManagementEnv instance()
	{
		return ManagementEnvHolder.instance;
	}

	/** Initialize the environment. */
	public void init() throws ManagementException
	{
		if ( this.initialized )
		{
			return;
		}

		this.initJNDI();
	}

	/** Initialized the JNDI. */
	protected void initJNDI() throws ManagementException
	{
		try
		{
			this.ctxFactoryBuild = new ManagementContextFactoryBuilder();
			NamingManager.setInitialContextFactoryBuilder( this.ctxFactoryBuild );
			NamingManager.setObjectFactoryBuilder( this.ctxFactoryBuild );

			// Initial environment with various properties
			Hashtable<String,String> env = new Hashtable<String,String>();
			env.put( Context.INITIAL_CONTEXT_FACTORY, ManagementContextFactory.class.getName() );
			//env.put(Context.PROVIDER_URL, "");
			//env.put(Context.OBJECT_FACTORIES, "foo.bar.ObjFactory");
			env.put( Context.URL_PKG_PREFIXES, ManagementContextFactory.class.getPackage().getName() );

			this.initCtx = new InitialContext(env);
			Context javaCompCtx = (Context) this.getOrCreateSubcontext("java:comp", initCtx);
			if ( javaCompCtx == null )
			{
				throw new ManagementException("JNDI problem. Cannot get java:comp context from InitialContext.");
			}
			Context envCtx = (Context) this.getOrCreateSubcontext("env", javaCompCtx);
			if ( envCtx == null )
			{
				throw new ManagementException("JNDI problem. Cannot get env context from java:comp context.");
			}
			Context jdbcCtx = (Context) this.getOrCreateSubcontext("jdbc", envCtx);
			if ( jdbcCtx == null )
			{
				throw new ManagementException("JNDI problem. Cannot get jdbc context from java:comp/env context.");
			}

			// Create the DataSource

			//Properties properties = new Properties();
			//properties.put( "driverClassName", "com.mysql.jdbc.Driver" );
			//properties.put( "url", "jdbc:mysql://localhost:3306/DB" );
			//properties.put( "username", "username" );
			//properties.put( "password", "********" );
			//
			//DataSource dataSource = BasicDataSourceFactory.createDataSource( properties );
			//initContext.bind( "java:comp/env/jdbc/db", dataSource );

			//Reference ref = new Reference( "javax.sql.DataSource", "org.apache.commons.dbcp.BasicDataSourceFactory", null ); 
			//ref.add(new StringRefAddr("driverClassName", "com.mysql.jdbc.Driver"));
			//ref.add(new StringRefAddr("url", "jdbc:mysql://localhost:3306/sharegrid"));
			//ref.add(new StringRefAddr("username", "root"));
			//ref.add(new StringRefAddr("password", ""));
			//initCtx.rebind( "java:comp/env/jdbc/mysql", ref );
			java.util.Properties properties = new java.util.Properties();
			properties.put( "driverClassName", "com.mysql.jdbc.Driver" );
			properties.put( "url", "jdbc:mysql://localhost:3306/sharegrid" );
			properties.put( "username", "root" );
			properties.put( "password", "" );
			javax.sql.DataSource dataSource = org.apache.commons.dbcp.BasicDataSourceFactory.createDataSource( properties );
			initCtx.rebind( "java:comp/env/jdbc/mysql", dataSource );

		}
		catch (Exception ex)
		{
			throw new ManagementException("JNDI problem.", ex);
		}
	}

	/** Returns the given (sub-)context (eventually, creating it). */
	protected Context getOrCreateSubcontext(String contextName, Context context) throws NamingException
	{
		try {
			return (Context) context.lookup( contextName );
		} catch ( Exception exp ) {
			return context.createSubcontext( contextName );
		}
	}

	/** Holds the singleton instance. */
	private static final class ManagementEnvHolder
	{
		private final static ManagementEnv instance = new ManagementEnv();
	}
}
