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

import it.unipmn.di.dcs.cloud.core.middleware.IMiddlewareEnv;
import it.unipmn.di.dcs.cloud.middleware.minicloud.MiddlewareManager;
import it.unipmn.di.dcs.cloud.middleware.minicloud.MiddlewareEnv;

import it.unipmn.di.dcs.sharegrid.web.model.Conf;

import java.util.Properties;

/**
 * Wrapper class representing the bridge between the MiniCloud class framework
 * and this class framework.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class MiniCloudMiddlewareManagerWrapper extends MiddlewareManager
{
	/** A constructor. */
	public MiniCloudMiddlewareManagerWrapper()
	{
		super();

		Properties props = new Properties();

		String middlewareId = Conf.instance().getCloudMiddlewareId();
		String isId = Conf.instance().getCloudMiddlewareCustomProperty(
			middlewareId,
			"is.id"
		);
		String isCls = Conf.instance().getCloudMiddlewareCustomProperty(
			middlewareId,
			"is." + isId + ".class"
		);
		props.setProperty(
			IMiddlewareEnv.IS_HOST_PROP,
			Conf.instance().getCloudMiddlewareCustomProperty(
				middlewareId,
				"is." + isId + ".host"
			)
		);
		props.setProperty(
			IMiddlewareEnv.IS_PORT_PROP,
			Conf.instance().getCloudMiddlewareCustomProperty(
				middlewareId,
				"is." + isId + ".port"
			)
		);
		String svcSchedId = Conf.instance().getCloudMiddlewareCustomProperty(
			middlewareId,
			"svcsched.id"
		);
		String svcSchedCls = Conf.instance().getCloudMiddlewareCustomProperty(
			middlewareId,
			"svcsched." + svcSchedId + ".class"
		);
		props.setProperty(
			IMiddlewareEnv.SVCSCHED_HOST_PROP,
			Conf.instance().getCloudMiddlewareCustomProperty(
				middlewareId,
				"svcsched." + svcSchedId + ".host"
			)
		);
		props.setProperty(
			IMiddlewareEnv.SVCSCHED_PORT_PROP,
			Conf.instance().getCloudMiddlewareCustomProperty(
				middlewareId,
				"svcsched." + svcSchedId + ".port"
			)
		);

		this.setEnv( new MiddlewareEnv(props) );
	}
}
