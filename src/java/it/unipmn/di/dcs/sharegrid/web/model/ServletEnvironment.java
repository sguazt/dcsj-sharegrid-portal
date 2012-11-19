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

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.InitialContext;

/**
 * Environment for servlet applications.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ServletEnvironment implements IEnvironment
{
	private String envUri;
	private Context envCtx;

	/** A constructor. */
	public ServletEnvironment(String uri)
	{
		this.envUri = uri;
	}

	/** Returns the naming context for this environment. */
	private Context getContext() throws NamingException
	{
		if ( this.envCtx == null )
		{
			Context ctx = new InitialContext();
			if ( ctx != null )
			{
				this.envCtx = (Context) ctx.lookup( this.envUri );
			}
		}

		return this.envCtx;
	}

	//@{ IEnvironment implementation

	public Object lookup(String uri) throws Exception
	{
		return this.getContext().lookup( uri );
	}

	public boolean checkEnv()
	{
		try
		{
			this.getContext();

			return true;
		}
		catch (NamingException me)
		{
			return false;
		}
	}

	//@} IEnvironment implementation
}
