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

package it.unipmn.di.dcs.sharegrid.web.model.cloud;

import it.unipmn.di.dcs.cloud.core.middleware.IMiddlewareFactory;
import it.unipmn.di.dcs.cloud.core.middleware.IMiddlewareManager;
//import it.unipmn.di.dcs.cloud.middleware.ourgrid.OurGridSchedulerEnv;
//import it.unipmn.di.dcs.cloud.middleware.ourgrid.OurGridMiddlewareManager;

import it.unipmn.di.dcs.sharegrid.web.model.Conf;

/**
 * A concrete factory class for creating cloud middleware manager objects.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class CloudMiddlewareFactory implements IMiddlewareFactory
{
	private IMiddlewareManager manager;

	private CloudMiddlewareFactory()
	{
		// empty
	}

	public static CloudMiddlewareFactory instance()
	{
		return InstanceHolder.Instance;
	}

	public IMiddlewareManager getMiddlewareManager()
	{
		if ( this.manager == null )
		{
			String middlewareCls = null;

			middlewareCls = Conf.instance().getCloudMiddlewareClass(
				Conf.instance().getCloudMiddlewareId()
			);

			try
			{
				this.manager = (IMiddlewareManager) Class.forName( middlewareCls ).newInstance();
			}
			catch (Exception e)
			{
				this.manager = null;
			}
		}

		return this.manager;
	}

	private static final class InstanceHolder
	{
		public final static CloudMiddlewareFactory Instance = new CloudMiddlewareFactory();
	}
}
