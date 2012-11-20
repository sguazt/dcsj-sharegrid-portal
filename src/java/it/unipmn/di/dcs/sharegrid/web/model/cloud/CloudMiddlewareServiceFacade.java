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

import it.unipmn.di.dcs.cloud.core.middleware.model.ICloudService;
import it.unipmn.di.dcs.cloud.core.middleware.model.IPhysicalMachine;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceHandle;
import it.unipmn.di.dcs.cloud.core.middleware.service.IInformationService;
import it.unipmn.di.dcs.cloud.core.middleware.service.IServiceSchedulerService;

import it.unipmn.di.dcs.sharegrid.web.model.ModelException;

import java.util.List;

/**
 * Facade class for Cloud middleware services.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class CloudMiddlewareServiceFacade
{
	/** A constructor. */
	private CloudMiddlewareServiceFacade()
	{
		// empty
	}

	/** Returns the sole instance. */
	public static CloudMiddlewareServiceFacade Instance()
	{
		return InstanceHolder.Instance;
	}

	/** Returns the available cloud services. */
	public List<ICloudService> getAvailableServices() throws ModelException
	{
		IInformationService isSvc = CloudMiddlewareFactory.instance().getMiddlewareManager().getInformationService();

		try
		{
			return isSvc.getServices();
		}
		catch (Exception e)
		{
			throw new ModelException("Unable to get the list of available cloud services.", e);
		}
	}

	/** Returns the available physical machines. */
	public List<IPhysicalMachine> getAvailableMachines() throws ModelException
	{
		IInformationService isSvc = CloudMiddlewareFactory.instance().getMiddlewareManager().getInformationService();

		try
		{
			return isSvc.getPhysicalMachines();
		}
		catch (Exception e)
		{
			throw new ModelException("Unable to get the list of available physical machines.", e);
		}
	}

	/** Starts the given service on the given physical machine. */
	public IServiceHandle startService(ICloudService service, IPhysicalMachine machine) throws ModelException
	{
		UserCloudService userSvc = null;

		try
		{
			IServiceSchedulerService schedSvc = CloudMiddlewareFactory.instance().getMiddlewareManager().getServiceSchedulerService();

			return schedSvc.submitService( service, machine );
		}
		catch (Exception e)
		{
			throw new ModelException("Error while submitting the service '" + service + "' on machine '" + machine + "'.", e);
		}
	}

	/** Stops the given service. */
	public boolean stopService(IServiceHandle shnd) throws ModelException
	{
		try
		{
			IServiceSchedulerService schedSvc = CloudMiddlewareFactory.instance().getMiddlewareManager().getServiceSchedulerService();

			return schedSvc.stopService( shnd );
		}
		catch (Exception e)
		{
			throw new ModelException("Error while stopping the service from service handle '" + shnd + "'.", e);
		}
	}

	/** Get the execution status of the given service. */
	public ExecutionStatus getServiceStatus(IServiceHandle shnd) throws ModelException
	{
		try
		{
			IServiceSchedulerService schedSvc = CloudMiddlewareFactory.instance().getMiddlewareManager().getServiceSchedulerService();

			return schedSvc.getServiceStatus( shnd );
		}
		catch (Exception e)
		{
			throw new ModelException("Error while requesting the service execution status from service handle '" + shnd + "'.", e);
		}
	}

	/** Returns the handle of the given submitted service id. */
	public IServiceHandle getServiceHandle(int shndId) throws ModelException
	{
		try
		{
			IServiceSchedulerService schedSvc = CloudMiddlewareFactory.instance().getMiddlewareManager().getServiceSchedulerService();

			return schedSvc.getServiceHandle( shndId );
		}
		catch (Exception e)
		{
			throw new ModelException("Error while requesting the service hanlde from service id '" + shndId + "'.", e);
		}
	}

	/**
	 * Private class user for holding the singleton instance in a
	 * thread-safe manner.
	 */
	private static class InstanceHolder
	{
		public static final CloudMiddlewareServiceFacade Instance = new CloudMiddlewareServiceFacade();
	}
}
