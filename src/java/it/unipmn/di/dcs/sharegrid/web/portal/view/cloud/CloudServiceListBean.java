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

import it.unipmn.di.dcs.sharegrid.web.model.cloud.UserCloudService;
import it.unipmn.di.dcs.sharegrid.web.model.cloud.UserCloudServiceFacade;
//import it.unipmn.di.dcs.sharegrid.web.model.cloud.CloudMiddlewareServiceFacade;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.MessageConstants;
//import it.unipmn.di.dcs.sharegrid.web.service.ICloudServiceService;
//import it.unipmn.di.dcs.sharegrid.web.service.ServiceFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.FacesException;

/**
 * Page bean for listing user cloud services.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class CloudServiceListBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( CloudServiceListBean.class.getName() );

	private static final String SVCID_PARAM = "sid";

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

		// Security check: makes sure the requested service really belongs
		// to current user.
		if ( this.getRequestParameterMap().containsKey( SVCID_PARAM ) )
		{
			String sid = this.getRequestParameterMap().get( SVCID_PARAM ).trim();

			if ( !Strings.IsNullOrEmpty( sid ) )
			{
				try
				{
					int serviceId = Integer.parseInt( sid );

					UserCloudService service = null;
					service = UserCloudServiceFacade.instance().loadUserService( serviceId );
					if ( service != null )
					{
						if ( service.getUserId().intValue() != this.getSessionBean().getUser().getId() )
						{
							throw new FacesException("Unauthorized service request.");
						}
					}
				}
				catch (Exception e)
				{
					CloudServiceListBean.Log.log( Level.SEVERE, "Request for service '" + sid + "' by user '" + this.getSessionBean().getUser().getId() + "' not satisfiable.", e );
					ViewUtil.AddExceptionMessage( e );

					this.inError = true;
				}
			}
		}
	}

	//@} Lifecycle /////////////////////////////////////////////////////////

	//@{ Properties ////////////////////////////////////////////////////////

	/** PROPERTY: userList. */
	private List<UserCloudService> services;
	public List<UserCloudService> getServices()
	{
		if ( this.services == null )
		{
			try
			{
				//ICloudServiceService serviceSvc = null;

				//serviceSvc = ServiceFactory.instance().cloudServiceService();
				this.services = UserCloudServiceFacade.instance().userServices( this.getSessionBean().getUser() );
				Collections.sort(
					this.services,
					new Comparator<UserCloudService>() {
						public int compare(UserCloudService o1, UserCloudService o2)
						{
							if (
								o1.getStoppingDate() != null
								&& o2.getStoppingDate() != null
							) {
								int cmp = o1.getStoppingDate().compareTo( o2.getStoppingDate() );
								if ( cmp != 0 )
								{
									return cmp;
								}
							}
							if (
								o1.getSubmissionDate() != null
								&& o2.getSubmissionDate() != null
							) {
								int cmp = o1.getSubmissionDate().compareTo( o2.getSubmissionDate() );
								if ( cmp != 0 )
								{
									return cmp;
								}
							}
							return o1.getName().compareTo( o2.getName() );
						}
					}
				);
			}
			catch (Exception e)
			{
				CloudServiceListBean.Log.log( Level.SEVERE, "Unable to retrieve services.", e );
				ViewUtil.AddExceptionMessage(e);
			}
		}

		return this.services;
	}

	//@} Properties ////////////////////////////////////////////////////////

	//@{ Actions ///////////////////////////////////////////////////////////

//	/** Action for viewing a service. */
//	public String doViewAction()
//	{
//		return "view-ok";
//	}

	/** Action for stop a service. */
	public String doStopAction()
	{
		if ( this.inError )
		{
			return "cancel-ko";
		}

		boolean allOk = false;

		if ( this.getRequestParameterMap().containsKey( SVCID_PARAM ) )
		{
			String sid = this.getRequestParameterMap().get( SVCID_PARAM ).trim();

			if ( !Strings.IsNullOrEmpty( sid ) )
			{
				try
				{
                                        // Decode the service-id parameter

					int serviceId;
					serviceId = Integer.parseInt( sid );

					// Retrieve the service

					//ICloudServiceService serviceSvc = null;
					UserCloudService service = null;
					//serviceSvc = ServiceFactory.instance().cloudServiceService();
					service = UserCloudServiceFacade.instance().loadUserService( serviceId );

					// Remove it

					UserCloudServiceFacade.instance().stopUserService( service );

//					// Updates the displayed list
//Not needed since the list holds the reference to the service object
//
//					if ( this.services != null )
//					{
//						int pos = this.services.indexOf( service );
//						if ( pos != -1 )
//						{
//							this.services.set( pos, service );
//						}
//					}
					this.services = null;

					allOk = true;
				}
				catch (Exception e)
				{
					CloudServiceListBean.Log.log( Level.SEVERE, "Error while stopping service '" + sid + "'.", e );
					ViewUtil.AddErrorMessage( MessageConstants.STOP_KO, "service" );
					ViewUtil.AddExceptionMessage( e );
				}
			}
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.STOP_KO, "service" );
			ViewUtil.AddWarnMessage( MessageConstants.MISSING_PARAM_KO, SVCID_PARAM );
		}

		return allOk ? "cancel-ok" : "cancel-ko";
	}

	/** Action for deleting a service. */
	public String doDeleteAction()
	{
		if ( this.inError )
		{
			return "delete-ko";
		}

		boolean allOk = false;

		if ( this.getRequestParameterMap().containsKey( SVCID_PARAM ) )
		{
			String sid = this.getRequestParameterMap().get( SVCID_PARAM ).trim();

			if ( !Strings.IsNullOrEmpty( sid ) )
			{
				try
				{
                                        // Decode the service-id parameter

					int serviceId;
					serviceId = Integer.parseInt( sid );

					// Retrieve the service

					//ICloudServiceService serviceSvc = null;
					UserCloudService service = null;
					//serviceSvc = ServiceFactory.instance().cloudServiceService();
					service = UserCloudServiceFacade.instance().loadUserService( serviceId );

					// Remove it

					UserCloudServiceFacade.instance().removeUserService( service );

					// Removes from the displayed list

// Don't work
//					if ( this.services != null )
//					{
//						this.services.remove( service.getId() );
//					}
					this.services = null;

					allOk = true;
				}
				catch (Exception e)
				{
					CloudServiceListBean.Log.log( Level.SEVERE, "Error while deleting service '" + sid + "'.", e );
					ViewUtil.AddErrorMessage( MessageConstants.DELETE_KO, "service" );
					ViewUtil.AddExceptionMessage( e );
				}
			}
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.DELETE_KO, "service" );
			ViewUtil.AddWarnMessage( MessageConstants.MISSING_PARAM_KO, SVCID_PARAM );
		}

		return allOk ? "delete-ok" : "delete-ko";
	}

	//@} Actions ///////////////////////////////////////////////////////////
}
