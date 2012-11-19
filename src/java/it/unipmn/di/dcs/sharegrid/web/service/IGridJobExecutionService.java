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

import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
import it.unipmn.di.dcs.grid.core.middleware.sched.IBotJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJobHandle;

import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.UserGridJob;

import java.util.List;

/**
 * Interface for service level classes for the executing/management of user
 * jobs.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IGridJobExecutionService
{
	/**
	 * Abort the given job from the Grid (if necessary) but keep it in  the
	 * job database.
	 */
	UserGridJob abortUserJob(UserGridJob job) throws ServiceException;

	/**
	 * Remove the given job from the Grid (if necessary) but keep it in  the
	 * job database.
	 */
	UserGridJob cancelUserJob(UserGridJob job) throws ServiceException;

	/**
	 * Tells if the given job can be exported.
	 *
	 * @return <code>true</code> if the given job can be exported.
	 * <code>false</code> otherwise.
	 */
	boolean canExportJob(IJob job);

	/**
	 * Tells if the given job can be executed.
	 *
	 * @return <code>true</code> if the given job can be executed.
	 * <code>false</code> otherwise.
	 */
	boolean canExecuteJob(IJob job);

	/**
	 * Returns a string representing a unique file name at job level.
	 */
	String createJobUniqueFileName(String fileNamePrefix) throws ServiceException;

	/**
	 * Returns a string representing a unique file name at task level.
	 */
	String createTaskUniqueFileName(String fileNamePrefix) throws ServiceException;

	/**
	 * Executes the given job and returns a job representation for
	 * submitted jobs.
	 */
	UserGridJob executeJob(User user, IJob job) throws ServiceException;

	/**
	 * Executes the given BoT job and returns a job representation for
	 * submitted jobs.
	 */
	UserGridJob executeJob(User user, IBotJob job) throws ServiceException;

	/**
	 * Returns the job objects still running (or to be run) whose age is
	 * equal to or greater than the given age.
	 */
	List<UserGridJob> getPendingUserJobsByMinAge(long minAge) throws ServiceException;

	/**
	 * Retrieve the execution status of the given job from the middleware.
	 */
	ExecutionStatus getStatusFromMiddleware(UserGridJob job) throws ServiceException;

	/**
	 * Returns the job object related to the given job identifier.
	 */
	UserGridJob getUserJob(int userJobId) throws ServiceException;

	/**
	 * Returns the job objects submitted by the given user.
	 */
	List<UserGridJob> getUserJobs(User user) throws ServiceException;

	/**
	 * Returns {@code true} if the given job exists in the job store and in
	 * the queue of the middleware scheduler (if its status is not "final").
	 */
	boolean isValidUserJob(UserGridJob job) throws ServiceException;

	/**
	 * Remove the given job from the Grid scheduler queue.
	 * The job must have a final execution status (e.g. ABORTED, CANCELLED,
	 * FAILED, FINISHED).
	 */
	void purgeUserJob(UserGridJob job) throws ServiceException;

	/**
	 * Remove the given job from the Grid (if necessary) and from the job
	 * database.
	 */
	UserGridJob removeUserJob(UserGridJob job) throws ServiceException;

	/**
	 * Save the given job in the storage.
	 */
	void saveUserJob(UserGridJob job) throws ServiceException;

	/**
	 * Retrieve job informations from the middleware and update the jobs in
	 * the storage as necessary.
	 *
	 * @return The list of updated jobs.
	 */
	List<UserGridJob> updateUserJobsFromMiddleware() throws ServiceException;
}
