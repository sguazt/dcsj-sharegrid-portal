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

import it.unipmn.di.dcs.common.annotation.TODO;

import it.unipmn.di.dcs.grid.core.middleware.IMiddlewareFactory;
import it.unipmn.di.dcs.grid.core.middleware.IMiddlewareManager;
import it.unipmn.di.dcs.grid.middleware.ourgrid.OurGridSchedulerEnv;
import it.unipmn.di.dcs.grid.middleware.ourgrid.OurGridMiddlewareManager;

/**
 * A concrete factory class for creating middleware manager objects.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@TODO("Get the type of middleware from a configuration file.")
public class MiddlewareFactory implements IMiddlewareFactory
{
	/** The underlying Grid middleware manager. */
	private IMiddlewareManager manager;

	/** A constructor. */
	protected MiddlewareFactory()
	{
		// empty
	}

//	public static synchronized MiddlewareFactory GetInstance()
//	{
//		if ( MiddlewareFactory.instance == null )
//			MiddlewareFactory.instance = new MiddlewareFactory();
//		}
//
//		return MiddlewareFactory.instance;
//	}

	/** Returns the sole instance. */
	public static MiddlewareFactory instance()
	{
		return InstanceHolder.instance;
	}

	/** Returns the underlying Grid middleware manager. */
	public synchronized IMiddlewareManager getMiddlewareManager()
	{
		//FIXME: Actually OurGrid is the only managed middleware
		//	 In the future we can have several middleware.
		//	 So this code should be replaced with a more general
		//	 one (e.g. one that read from a XML file)

		if ( this.manager == null )
		{
			String mgroot = null;
			mgroot = Conf.instance().getMiddlewareOurGridMgRoot();
			System.setProperty( "MGROOT", mgroot );

			OurGridSchedulerEnv env = OurGridSchedulerEnv.GetInstance();
			env.initialize();
			env.setSchedulerName( Conf.instance().getMiddlewareOurGridMgName() );
			env.setSchedulerPort( Integer.parseInt( Conf.instance().getMiddlewareOurGridMgPort() ) );

			this.manager = new OurGridMiddlewareManager(env);
		}

		return this.manager;
	}

	/** Holds the sole instance. */
	private static final class InstanceHolder
	{
		private final static MiddlewareFactory instance = new MiddlewareFactory();
	}
}
