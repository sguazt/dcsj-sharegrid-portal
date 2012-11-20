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

import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.cloud.UserCloudService;

import java.util.List;

/**
 * Interface for service level classes for the executing/management of user
 * cloud-jobs.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface ICloudServiceService
{
	/**
	 * Returns the submittable service objects.
	 */
	List<ICloudService> getAvailableServices() throws ServiceException;

	/**
	 * Returns the physical machine objects where it is possibile to execute
	 * a service.
	 */
	List<IPhysicalMachine> getAvailableMachines() throws ServiceException;

	/**
	 * Submits, for the given user, the given service on the given machine
	 * (if possible).
	 */
	UserCloudService submitService(User user, ICloudService service, IPhysicalMachine machine) throws ServiceException;

	/**
	 * Returns the service objects submitted by the given user.
	 */
	List<UserCloudService> getUserServices(User user) throws ServiceException;

	/** Returns the user service identified by the given id. */
	UserCloudService getUserService(int id) throws ServiceException;

	/** Cancel the schedulation/execution of the given service. */
	void cancelUserService(UserCloudService service) throws ServiceException;

	/**
	 * Cancel the schedulation/execution of the given service and remove it
	 * from the database.
	 */
	void removeUserService(UserCloudService service) throws ServiceException;
}
