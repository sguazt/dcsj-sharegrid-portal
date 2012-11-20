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

package it.unipmn.di.dcs.sharegrid.web.service;

import it.unipmn.di.dcs.cloud.core.middleware.model.ICloudService;
import it.unipmn.di.dcs.cloud.core.middleware.model.IPhysicalMachine;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceHandle;
import it.unipmn.di.dcs.cloud.core.middleware.service.IInformationService;
import it.unipmn.di.dcs.cloud.core.middleware.service.IServiceSchedulerService;

import it.unipmn.di.dcs.sharegrid.web.model.cloud.CloudMiddlewareFactory;
import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.cloud.UserCloudService;

import java.util.List;

/**
 * Interface for service level classes for the executing/management of user
 * cloud-jobs.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class CloudServiceService implements ICloudServiceService
{
	private CloudServiceService()
	{
		// empty
	}

	public static CloudServiceService instance()
	{
		return CloudServiceService.InstanceHolder.Instance;
	}

	//@{ ICloudServiceService implementation ///////////////////////////////

	public List<ICloudService> getAvailableServices() throws ServiceException
	{
		IInformationService isSvc = CloudMiddlewareFactory.instance().getMiddlewareManager().getInformationService();

		try
		{
			return isSvc.getServices();
		}
		catch (Exception e)
		{
			throw new ServiceException("Unable to get the list of available cloud services.", e);
		}
	}

	public List<IPhysicalMachine> getAvailableMachines() throws ServiceException
	{
		IInformationService isSvc = CloudMiddlewareFactory.instance().getMiddlewareManager().getInformationService();

		try
		{
			return isSvc.getPhysicalMachines();
		}
		catch (Exception e)
		{
			throw new ServiceException("Unable to get the list of available physical machines.", e);
		}
	}

	public UserCloudService submitService(User user, ICloudService service, IPhysicalMachine machine) throws ServiceException
	{
		IServiceSchedulerService schedSvc = CloudMiddlewareFactory.instance().getMiddlewareManager().getServiceSchedulerService();

		boolean submitted = false;;
		IServiceHandle shnd = null;

		try
		{
			shnd = schedSvc.submitService( service, machine );

			submitted = true;
		}
		catch (Exception e)
		{
			throw new ServiceException("Error while submitting the service '" + service + "' on machine '" + machine + "'.", e);
		}

		if ( !submitted )
		{
			throw new ServiceException("Unable to submit the service '" + service + "' on machine '" + machine + "'.");
		}

		return new UserCloudService(); //TODO: create on DB
	}

	public List<UserCloudService> getUserServices(User user) throws ServiceException
	{
		//TODO
		throw new ServiceException("Not Implemented");
	}

	public UserCloudService getUserService(int svcId) throws ServiceException
	{
		//TODO
		throw new ServiceException("Not Implemented");
	}

	public void cancelUserService(UserCloudService service) throws ServiceException
	{
		//TODO
		throw new ServiceException("Not Implemented");
	}

	public void removeUserService(UserCloudService service) throws ServiceException
	{
		//TODO
		throw new ServiceException("Not Implemented");
	}

	//@} ICloudServiceService implementation ///////////////////////////////

	private static class InstanceHolder
	{
		public static final CloudServiceService Instance = new CloudServiceService();
	}
}
