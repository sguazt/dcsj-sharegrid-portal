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

import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;

import java.util.List;

/**
 * Interface for grid jobs data store.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IUserGridJobDAO extends IBaseDAO<UserGridJob,Integer>
{
	/**
	 * Returns the list of all jobs submitted by the given user.
	 */
	List<UserGridJob> listFromUser(int userId) throws Exception;

	/**
	 * Returns the list of all jobs having one of the given status.
	 */
	List<UserGridJob> listFromStatus(final ExecutionStatus[] status) throws Exception;

	/**
	 * Returns the list of all jobs submitted by the given user restricted
	 * to the given status.
	 */
	List<UserGridJob> listFromUserAndStatus(int userId, final ExecutionStatus[] status) throws Exception;

	/**
	 * Returns the list of jobs with a pending execution status (READY,
	 * RUNNING, UNSTARTED or UNKNOWN) or with an empty Stopping date.
	 */
	List<UserGridJob> listPendings() throws Exception;

	/**
	 * Returns the list of jobs with a pending execution status (READY,
	 * RUNNING, UNSTARTED or UNKNOWN) or with an empty Stopping date,
	 * whose age is equal to or greater than the given age.
	 */
	List<UserGridJob> listPendingsByMinAge(long minAge) throws Exception;
}
