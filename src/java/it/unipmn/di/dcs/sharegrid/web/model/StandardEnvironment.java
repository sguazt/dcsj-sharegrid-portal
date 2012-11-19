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

package it.unipmn.di.dcs.sharegrid.web.model;

import it.unipmn.di.dcs.common.annotation.FIXME;

import it.unipmn.di.dcs.sharegrid.web.management.naming.ManagementContextFactory;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Standard environment.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@FIXME("This class contains hard-coded stuff. Before deploy check for changes!!!!")
public class StandardEnvironment implements IEnvironment
{
	private static Map<String,Object> Env;

	private String envUri;
	private Context envCtx;

	//FIXME: THIS IS A DIRTY HAAAACKK!!!!!
	// This hash file might be auto-generated in some way
	static
	{
		Env = new HashMap<String,Object>();
		Env.put( "it.unipmn.di.dcs.sharegrid.web.ConfigurationFile", "META-INF/conf.properties" );
	}

	public StandardEnvironment(String uri)
	{
		this.envUri = uri;
	}

	private Context getContext() throws NamingException
	{
		if ( this.envCtx == null )
		{
			Hashtable<String,String> env = new Hashtable<String,String>();
			env.put(
				Context.INITIAL_CONTEXT_FACTORY,
				ManagementContextFactory.class.getName()
			);
			//env.put(Context.PROVIDER_URL, "");
			//env.put(Context.OBJECT_FACTORIES, "foo.bar.ObjFactory");
//System.err.println( "INITIAL_CONTEXT_FACTORY: " + Context.INITIAL_CONTEXT_FACTORY );//XXX
//System.err.println( "URL_PKG_PREFIXES: " + Context.URL_PKG_PREFIXES );//XXX
			env.put(
				Context.URL_PKG_PREFIXES,
				ManagementContextFactory.class.getName()
			);

//System.err.println("ENV URI: " + envUri);//XXX
			Context ctx = new InitialContext(env);
			if ( ctx != null )
			{
//System.err.println("CONTEXT NOT NULL");//XXX
				String[] parts = this.envUri.split("/");
				Context[] ctxs = new Context[parts.length + 1];
				ctxs[0] = ctx;
				int i = 1;
				for (String envPart : parts)
				{
//System.err.println("ENV PART: " + envPart);//XXX
					ctxs[i] = (Context) this.getOrCreateSubcontext( envPart, ctxs[i-1] );
					i++;
				}
				this.envCtx = (Context) this.getOrCreateSubcontext( this.envUri, ctx );
//System.err.println("ENV CONTEXT: " + this.envCtx);//XXX

				//Properties properties = new Properties();
				//properties.put( "driverClassName", "com.mysql.jdbc.Driver" );
				//properties.put( "url", "jdbc:mysql://localhost:3306/sharegrid" );
				//properties.put( "username", "root" );
				//properties.put( "password", "" );
				//javax.sql.DataSource dataSource = org.apache.commons.dbcp.BasicDataSourceFactory.createDataSource( properties );
				//this.envCtx.rebind( "jdbc/mysql", dataSource );
			}
		}

		return this.envCtx;
	}

	protected Context getOrCreateSubcontext(String contextName, Context context) throws NamingException
	{
		try {
			return (Context) context.lookup( contextName );
		} catch ( Exception exp ) {
			return context.createSubcontext( contextName );
		}
	}

	//@{ IEnvironment implementation

	public Object lookup(String uri) throws Exception
	{
		try
		{
			if ( Env.containsKey( uri ) )
			{
				return Env.get( uri );
			}

			return this.getContext().lookup( uri );
		}
		catch (NamingException ne)
		{
			// Ignore
		}

		return null;
	}

	public boolean checkEnv()
	{
		return true;
	}

	//@} IEnvironment implementation
}
