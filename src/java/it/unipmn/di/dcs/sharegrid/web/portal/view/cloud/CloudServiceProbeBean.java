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

package it.unipmn.di.dcs.sharegrid.web.portal.view.cloud;

import it.unipmn.di.dcs.common.util.Strings;
import it.unipmn.di.dcs.common.net.IInetServiceStats;

import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceHandle;
import it.unipmn.di.dcs.cloud.monitor.IServiceProberStats;

import it.unipmn.di.dcs.sharegrid.web.model.cloud.CloudMiddlewareServiceFacade;
import it.unipmn.di.dcs.sharegrid.web.model.cloud.UserCloudService;
import it.unipmn.di.dcs.sharegrid.web.model.cloud.UserCloudServiceFacade;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.MessageConstants;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

/**
 * Page bean showing monitoring statistics of a given service.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class CloudServiceProbeBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( CloudServiceProbeBean.class.getName() );

	private static final String SVCID_PARAM = "id";

	/**
	 * Flag indicating an error is happened during the normal
	 * page processing.
	 */
	private boolean inError = false;

	//@{ Lifecycle /////////////////////////////////////////////////////////

	@Override
	public void init()
	{
		super.init();

		Map<String,String> paramsMap = this.getRequestParameterMap();
		if ( paramsMap.containsKey( SVCID_PARAM ) ) {
			String sid = paramsMap.get( SVCID_PARAM ).trim();

			if ( !Strings.IsNullOrEmpty( sid ) )
			{
				try
				{
					// Decode the service-id parameter
					int serviceId;
					serviceId = Integer.parseInt( sid );

					// Retrieve the service for checking against the owner
					UserCloudService service = null;

					service = UserCloudServiceFacade.instance().loadUserService( serviceId );

					// Makes sure current user can view this service
					if ( service != null )
					{
						if ( service.getUserId().intValue() == this.getSessionBean().getUser().getId() )
						{
							this.setService( service );

							if ( service.isRunning() )
							{
								this.setServiceHandle(
									 CloudMiddlewareServiceFacade.Instance().getServiceHandle(
										service.getSchedulerServiceId()
									)
								);
								IServiceProberStats stats = null;
								stats = UserCloudServiceFacade.instance().probeUserService( service );

								if ( stats != null )
								{
									this.setStats( stats );
								}
								else
								{
									this.inError = true;
									ViewUtil.AddErrorMessage( MessageConstants.SERVICE_PROBING_KO, paramsMap.get(SVCID_PARAM) );
								}
							}
							else
							{
								ViewUtil.AddWarnMessage( MessageConstants.SERVICE_NOT_RUNNING, paramsMap.get(SVCID_PARAM) );
							}
						}
						else
						{
							this.inError = true;
							ViewUtil.AddErrorMessage( MessageConstants.AUTHORIZATION_KO, "Service ''" + paramsMap.get(SVCID_PARAM) + "'" );
						}
					}
					else
					{
						this.inError = true;
						ViewUtil.AddErrorMessage( MessageConstants.EXIST_KO, "Service ''" + paramsMap.get(SVCID_PARAM) + "'" );
					}
				}
				catch (Exception e)
				{
					this.inError = true;
					CloudServiceProbeBean.Log.log( Level.SEVERE, "Unable to probe service '" + sid + "'", e );
					ViewUtil.AddExceptionMessage( e );
				}
			}
		}
		else
		{
			this.inError = true;
			ViewUtil.AddErrorMessage( MessageConstants.MISSING_PARAM_KO, "id" );
		}
	}

	//@} Lifecycle /////////////////////////////////////////////////////////

	//@{ Properties ////////////////////////////////////////////////////////

	/** PROPERTY: service. */
	private UserCloudService service;
	public UserCloudService getService() { return this.service; }
	protected void setService(UserCloudService value) { this.service = value; }

	/** PROPERTY: serviceHandle. */
	private IServiceHandle serviceHandle;
	public IServiceHandle getServiceHandle() { return this.serviceHandle; }
	protected void setServiceHandle(IServiceHandle value) { this.serviceHandle = value; }

	/** PROPERTY: stats. */
	private IServiceProberStats stats;
	public IServiceProberStats getStats() { return this.stats; }
	protected void setStats(IServiceProberStats value) { this.stats = value; }

	/** PROPERTY: serviceStats. */
	public List<IInetServiceStats> getServiceStats()
	{
		if ( this.inError )
		{
			return new ArrayList<IInetServiceStats>();
		}
		ArrayList<IInetServiceStats> al = new ArrayList<IInetServiceStats>();
		al.addAll(this.stats.getServiceStats());
		return al;
	}

	//@} Properties ////////////////////////////////////////////////////////
}
