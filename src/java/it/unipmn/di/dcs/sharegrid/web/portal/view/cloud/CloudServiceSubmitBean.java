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

import it.unipmn.di.dcs.cloud.core.middleware.model.ICloudService;
import it.unipmn.di.dcs.cloud.core.middleware.model.IPhysicalMachine;

import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;
import it.unipmn.di.dcs.sharegrid.web.model.cloud.CloudMiddlewareServiceFacade;
import it.unipmn.di.dcs.sharegrid.web.model.cloud.UserCloudServiceFacade;
//import it.unipmn.di.dcs.sharegrid.web.service.ServiceFactory;
//import it.unipmn.di.dcs.sharegrid.web.model.UserCloudService;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.MessageConstants;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bean class for CLOUD services submission.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class CloudServiceSubmitBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( CloudServiceSubmitBean.class.getName() );

	private boolean inError = false;

	//@{ Lifecycle /////////////////////////////////////////////////////////

	@Override
	public void init()
	{
		super.init();

		try
		{
			this.setAvailableServices(
				CloudMiddlewareServiceFacade.Instance().getAvailableServices()
			);
			this.setAvailableMachines(
				CloudMiddlewareServiceFacade.Instance().getAvailableMachines()
			);
		}
		catch (Exception e)
		{
			this.inError = true;
			ViewUtil.AddExceptionMessage( e );
			CloudServiceSubmitBean.Log.log( Level.SEVERE, "Error while retrieving informations for submitting a service.", e );
		}
	}

	//@} Lifecycle /////////////////////////////////////////////////////////

	//@{ Properties ////////////////////////////////////////////////////////

	/** PROPERTY: availableMachines. */
	private Map<Integer,IPhysicalMachine> availableMachines = new HashMap<Integer,IPhysicalMachine>();
	public Collection<IPhysicalMachine> getAvailableMachines()
	{
		List<IPhysicalMachine> phyMachs = null;
		phyMachs = new ArrayList<IPhysicalMachine>();
		phyMachs.addAll( this.availableMachines.values() );
		Collections.sort(
			phyMachs,
			new Comparator<IPhysicalMachine>() {
				public int compare(IPhysicalMachine o1, IPhysicalMachine o2)
				{
					return o1.getName().compareTo( o2.getName() );
				}
			}
		);
		return phyMachs;
	}
	protected void setAvailableMachines(Collection<IPhysicalMachine> value)
	{
		if ( this.availableMachines == null )
		{
			this.availableMachines = new HashMap<Integer,IPhysicalMachine>();
		}
		else
		{
			this.availableMachines.clear();
		}
		for (IPhysicalMachine m : value)
		{
			this.availableMachines.put( m.getId(), m );
		}
	}
	protected IPhysicalMachine getMachine(int id) { return this.availableMachines.get( id ); }

	/** PROPERTY: availableServices. */
	private Map<Integer,ICloudService> availableServices = new HashMap<Integer,ICloudService>();
	public Collection<ICloudService> getAvailableServices()
	{
		List<ICloudService> services = null;
		services = new ArrayList<ICloudService>();
		services.addAll( this.availableServices.values() );
		Collections.sort(
			services,
			new Comparator<ICloudService>() {
				public int compare(ICloudService o1, ICloudService o2)
				{
					return o1.getName().compareTo( o2.getName() );
				}
			}
		);
		return services;
	}
	protected void setAvailableServices(Collection<ICloudService> value)
	{
		if ( this.availableServices == null )
		{
			this.availableServices = new HashMap<Integer,ICloudService>();
		}
		else
		{
			this.availableServices.clear();
		}
		for (ICloudService svc : value)
		{
			this.availableServices.put( svc.getId(), svc );
		}
	}
	protected ICloudService getService(int id) { return this.availableServices.get( id ); }

	/**
	 * PROPERTY: serviceName.
	 *
	 * The service name.
	 */
	private String serviceName;
	public String getServiceName() { return this.serviceName; }
	public void setServiceName(String value) { this.serviceName = value; }

	/** PROPERTY: chosenMachine. */
	private String chosenMachine;
	public String getChosenMachine() { return this.chosenMachine; }
	public void setChosenMachine(String value) { this.chosenMachine = value; }

	/** PROPERTY: chosenService. */
	private String chosenService;
	public String getChosenService() { return this.chosenService; }
	public void setChosenService(String value) { this.chosenService = value; }

	//@} Properties ////////////////////////////////////////////////////////

	//@{ Actions ///////////////////////////////////////////////////////////

	/** Start the service chosen by the user. */
	public String submitServiceAction()
	{
		if ( this.inError )
		{
			return "svc-submit-ko";
		}

		if ( Strings.IsNullOrEmpty( this.getServiceName() ) )
		{
			this.setServiceName(
				CloudServiceSubmitBean.CreateServiceName()
			);
		}

		try
		{
			UserCloudServiceFacade.instance().startUserService(
				this.getSessionBean().getUser(),
				this.getService( Integer.parseInt( this.getChosenService() ) ),
				this.getMachine( Integer.parseInt( this.getChosenMachine() ) ),
				this.getServiceName()
			);
		}
		catch (Exception e)
		{
			ViewUtil.AddExceptionMessage( e );
			CloudServiceSubmitBean.Log.log( Level.SEVERE, "Error while submitting a service.", e );

			return "svc-submit-ko";
		}

		return "svc-submit-ok";
	}

	//@} Actions ///////////////////////////////////////////////////////////

	private static String CreateServiceName()
	{
		SimpleDateFormat dtFormatter = new SimpleDateFormat( "yyyyMMdd_HHmm" );

		return "sgweb_" + dtFormatter.format( new Date() );
	}
}
