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

import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;

import it.unipmn.di.dcs.sharegrid.web.model.IBaseDAO;

import java.util.List;

/**
 * Interface for submitted VMs data store.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IUserCloudServiceDAO extends IBaseDAO<UserCloudService,Integer>
{
	/**
	 * Returns the list of all jobs submitted by the given user.
	 */
	List<UserCloudService> listFromUser(int userId) throws Exception;

	/**
	 * Returns the list of all jobs having one of the given status.
	 */
	List<UserCloudService> listFromStatus(final ExecutionStatus[] status) throws Exception;

	/**
	 * Returns the list of all jobs submitted by the given user restricted
	 * to the given status.
	 */
	List<UserCloudService> listFromUserAndStatus(int userId, final ExecutionStatus[] status) throws Exception;

	/**
	 * Returns the list of jobs with a pending execution status (READY,
	 * RUNNING, UNSTARTED or UNKNOWN) or with an empty Stopping date.
	 */
	List<UserCloudService> listPendings() throws Exception;
}
