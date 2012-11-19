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

import it.unipmn.di.dcs.common.io.FileUtil;
import it.unipmn.di.dcs.common.util.collection.Collections;
import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.grid.core.middleware.IMiddlewareManager;
import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
import it.unipmn.di.dcs.grid.core.middleware.sched.IBotJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJobHandle;

import it.unipmn.di.dcs.sharegrid.web.model.MiddlewareFactory;
import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;
import it.unipmn.di.dcs.sharegrid.web.model.UserGridJob;
import it.unipmn.di.dcs.sharegrid.web.model.UserGridJobFacade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service level class for grid job execution/management.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class GridJobExecutionService implements IGridJobExecutionService
{
	private IMiddlewareManager middleware;

	/** A constructor. */
	protected GridJobExecutionService()
	{
		// empty
	}

	/**
	 * Returns the singleton instance.
	 */
	public static GridJobExecutionService instance()
	{
		return GridJobExecutionService.InstanceHolder.instance;
	}

	//@{ IGridJobExecutionService implementation

	public UserGridJob abortUserJob(UserGridJob job) throws ServiceException
	{
		// preconditions
		assert( job != null );
		if ( job == null )
		{
			throw new ServiceException( "Error while aborting job (Job not specified)." );
		}

		if ( !this.getMiddleware().getJobScheduler().isRunning() )
		{
			throw new ServiceException( "Scheduler is not running." );
		}

		boolean needUpdate = false;

		// Makes sure this is a valid job
		if ( this.isValidUserJob( job ) )
		{
			try
			{
				// Remove job from the grid (if needed)

				if ( this.existsUserJobOnMiddleware( job ) )
				{
					ExecutionStatus status = this.getStatusFromMiddleware(job);

					if (
						status == ExecutionStatus.READY
						|| status == ExecutionStatus.RUNNING
						|| status == ExecutionStatus.UNSTARTED
						|| status == ExecutionStatus.UNKNOWN
					) {
						IJobHandle jhnd = null;

						jhnd = this.getMiddleware().getJobScheduler().getJobHandle(
							job.getSchedulerJobId()
						);
						this.getMiddleware().getJobScheduler().abortJob( jhnd );

						job.setStatus( ExecutionStatus.ABORTED );
						job.setStoppingDate( new Date() );

						needUpdate = true;
					}
				}
			}
			catch (Exception e)
			{
				throw new ServiceException( "Error while canceling job '" + job.getId() + "'", e );
			}

			// Makes sure the job have a valid stopping date
			if ( !needUpdate && job.getStoppingDate() == null )
			{
				job.setStoppingDate( new Date() );

				needUpdate = true;
			}
		}
		else
		{
			//throw new ServiceException( "Error while canceling job '" + job.getId() + "' (Not a valid job)." );

			// Sets the job status to aborted.

			job.setStatus( ExecutionStatus.ABORTED );
			job.setStoppingDate( new Date() );
			needUpdate = true;
		}

		// Update job in the job store

		if ( needUpdate )
		{
			try
			{
				UserGridJobFacade.instance().setUserGridJob( job );
			}
			catch (Exception e)
			{
				throw new ServiceException( "Error while saving the canceled job '" + job.getId() + "'", e );
			}
		}

		// Remove job from scheduler queue
		try
		{
			this.purgeUserJob(job);
		}
		catch (Exception e)
		{
			throw new ServiceException( "Error while purging job '" + job.getId() + "'", e );
		}

		return job;
	}

	public UserGridJob cancelUserJob(UserGridJob job) throws ServiceException
	{
		// preconditions
		if ( job == null )
		{
			throw new ServiceException( "Error while canceling job (Job not specified)." );
		}

		if ( !this.getMiddleware().getJobScheduler().isRunning() )
		{
			throw new ServiceException( "Scheduler is not running." );
		}

		boolean needUpdate = false;

		// Makes sure this is a valid job
		if ( this.isValidUserJob( job ) )
		{
			try
			{
				// Remove job from the grid (if needed)

				if ( this.existsUserJobOnMiddleware( job ) )
				{
					ExecutionStatus status = this.getStatusFromMiddleware(job);

					if (
						status == ExecutionStatus.READY
						|| status == ExecutionStatus.RUNNING
						|| status == ExecutionStatus.UNSTARTED
						|| status == ExecutionStatus.UNKNOWN
					) {
						IJobHandle jhnd = null;

						jhnd = this.getMiddleware().getJobScheduler().getJobHandle(
							job.getSchedulerJobId()
						);
						this.getMiddleware().getJobScheduler().cancelJob( jhnd );

						job.setStatus( ExecutionStatus.CANCELLED );
						job.setStoppingDate( new Date() );

						needUpdate = true;
					}
				}
			}
			catch (Exception e)
			{
				throw new ServiceException( "Error while canceling job '" + job.getId() + "'", e );
			}

			// Makes sure the job have a valid stopping date
			if ( !needUpdate && job.getStoppingDate() == null )
			{
				job.setStoppingDate( new Date() );

				needUpdate = true;
			}
		}
		else
		{
			//throw new ServiceException( "Error while canceling job '" + job.getId() + "' (Not a valid job)." );

			// Set the job status to canceled.

			job.setStatus( ExecutionStatus.CANCELLED );
			job.setStoppingDate( new Date() );
			needUpdate = true;
		}

		// Update job in the job store

		if ( needUpdate )
		{
			try
			{
				UserGridJobFacade.instance().setUserGridJob( job );
			}
			catch (Exception e)
			{
				throw new ServiceException( "Error while saving the canceled job '" + job.getId() + "'", e );
			}
		}

		// Remove job from scheduler queue
		try
		{
			this.purgeUserJob(job);
		}
		catch (Exception e)
		{
			throw new ServiceException( "Error while purging job '" + job.getId() + "'", e );
		}

		return job;
	}

	public boolean canExportJob(IJob job)
	{
//                return (
//				!Strings.IsNullOrEmpty( job.getSceneName() )
//				&& !Collections.IsNullOrEmpty( job.getRemoteCommands() )
//				&& !Strings.IsNullOrEmpty( job.getSceneFile() )
//				&& !Collections.IsNullOrEmpty( job.getOutputFiles() )
//			)
//			? true
//			: false;
		return true; //FIXME
	}

	public boolean canExecuteJob(IJob job)
	{
		return	this.canExportJob(job)
			&& this.getMiddleware().getJobScheduler().isRunning();
	}

	public UserGridJob executeJob(User user, IJob job) throws ServiceException
	{
		UserGridJob userJob = null;

		try
		{
			if ( !this.canExecuteJob(job) )
			{
				throw new ServiceException( "Scheduler is not running." );
			}

			IJobHandle jhnd = null;
			jhnd = this.getMiddleware().getJobScheduler().submitJob( job );

			userJob = UserGridJobFacade.instance().createUserGridJob( user, jhnd, job );

		}
		catch (Exception e)
		{
			throw new ServiceException( "Error while executing current job", e );
		}

		return userJob;
	}

	public String createJobUniqueFileName(String fileNamePrefix) throws ServiceException
	{
		try
		{
			return this.getMiddleware().getJobScheduler().createJobUniqueFileName( fileNamePrefix );
		}
		catch (Exception e)
		{
			throw new ServiceException( "Error while creating Job Unique File Name", e );
		}
	}

	public String createTaskUniqueFileName(String fileNamePrefix) throws ServiceException
	{
		try
		{
			return this.getMiddleware().getJobScheduler().createTaskUniqueFileName( fileNamePrefix );
		}
		catch (Exception e)
		{
			throw new ServiceException( "Error while creating Task Unique File Name: ", e );
		}
	}

	public UserGridJob executeJob(User user, IBotJob job) throws ServiceException
	{
		UserGridJob userJob = null;

		try
		{
			if ( !this.canExecuteJob(job) )
			{
				throw new ServiceException( "Scheduler is not running." );
			}

			IJobHandle jhnd = null;

			jhnd = this.getMiddleware().getJobScheduler().submitJob( job );

			userJob = UserGridJobFacade.instance().createUserGridJob( user, jhnd, job );

			userJob.setStatus( this.getMiddleware().getJobScheduler().getJobStatus( jhnd ) );

			if (
				userJob.isAborted()
				|| userJob.isCancelled()
				|| userJob.isFailed()
				|| userJob.isFinished()
			) {
				userJob.setStoppingDate( new Date() );
			}
			UserGridJobFacade.instance().setUserGridJob( userJob );
		}
		catch (Exception e)
		{
			throw new ServiceException( "Error while executing current job: ", e );
		}

		return userJob;
	}

	public List<UserGridJob> getPendingUserJobsByMinAge(long minAge) throws ServiceException
	{
		try
		{
			return UserGridJobFacade.instance().pendingUserGridJobsByMinAge( minAge );
		}
		catch (Exception e)
		{
			throw new ServiceException(e);
		}
	}

	public ExecutionStatus getStatusFromMiddleware(UserGridJob job) throws ServiceException
	{
		if ( !this.getMiddleware().getJobScheduler().isRunning() )
		{
			throw new ServiceException( "Scheduler is not running." );
		}

		try
		{
			IJobHandle jhnd = this.getMiddleware().getJobScheduler().getJobHandle( job.getSchedulerJobId() );

			return this.getMiddleware().getJobScheduler().getJobStatus( jhnd );
		}
		catch (Exception e)
		{
			throw new ServiceException(e);
		}
	}

	public UserGridJob getUserJob(int userJobId) throws ServiceException
	{
		try
		{
			return UserGridJobFacade.instance().getUserGridJob( userJobId );
		}
		catch (Exception e)
		{
			throw new ServiceException(e);
		}
	}

	public List<UserGridJob> getUserJobs(User user) throws ServiceException
	{
		try
		{
			return UserGridJobFacade.instance().userGridJobs( user );
		}
		catch (Exception e)
		{
			throw new ServiceException(e);
		}
	}

	public boolean isValidUserJob(UserGridJob job) throws ServiceException
	{
		boolean isValid = false;

		try
		{
			// Check for existence on the middleware
			isValid = this.existsUserJobOnMiddleware( job );
			if ( isValid )
			{
				// Job found on middleware.
				// Now check for existence in the database.

				// Note: it is an error to return TRUE if the
				// given job exists only in the middleware
				// scheduler (it might be a job submitted
				// outside the Web portal).
				isValid = UserGridJobFacade.instance().existsUserGridJob( job.getId() );
			}
			else
			{
				// Job not found in the middleware scheduler.
				// It might be an already finalized job.
				// So check for existence inside the database.
				if ( UserGridJobFacade.instance().existsUserGridJob( job.getId() ) )
				{
					// Check for a "final" status.
					ExecutionStatus status = null;

					status = UserGridJobFacade.instance().getUserGridJob( job.getId() ).getStatus();

					if (
						status == ExecutionStatus.FAILED
						|| status == ExecutionStatus.ABORTED
						|| status == ExecutionStatus.FINISHED
						|| status == ExecutionStatus.CANCELLED
					) {
						isValid = true;
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new ServiceException(e);
		}

		return isValid;
	}

	public void purgeUserJob(UserGridJob job) throws ServiceException
	{
		// preconditions
		if ( job == null )
		{
			throw new ServiceException( "Error while purging job (Job not specified)." );
		}

		try
		{
			// Remove job from the grid (if needed)

			if ( this.existsUserJobOnMiddleware( job ) )
			{
				ExecutionStatus status = this.getStatusFromMiddleware(job);

				if (
					status == ExecutionStatus.FAILED
					|| status == ExecutionStatus.ABORTED
					|| status == ExecutionStatus.FINISHED
					|| status == ExecutionStatus.CANCELLED
				) {
					IJobHandle jhnd = null;

					jhnd = this.getMiddleware().getJobScheduler().getJobHandle(
						job.getSchedulerJobId()
					);
					this.getMiddleware().getJobScheduler().purgeJob( jhnd );
				}
			}
		}
		catch (Exception e)
		{
			throw new ServiceException( "Error while purging job '" + job.getId() + "'", e );
		}
	}

	public UserGridJob removeUserJob(UserGridJob job) throws ServiceException
	{
		// preconditions
		if ( job == null )
		{
			throw new ServiceException( "Error while removing job (Job not specified)." );
		}

//		// Makes sure this is a valid job
//		if ( this.isValidUserJob( job ) )
//		{
			try
			{
				// Remove job from the grid (if needed)

				if ( this.existsUserJobOnMiddleware( job ) )
				{
					ExecutionStatus status = this.getStatusFromMiddleware(job);

					if (
						status == ExecutionStatus.READY
						|| status == ExecutionStatus.RUNNING
						|| status == ExecutionStatus.UNSTARTED
						|| status == ExecutionStatus.UNKNOWN
					) {
						this.cancelUserJob( job );
					}

					// Remove job from scheduler queue
					this.purgeUserJob(job);
				}

				// Remove job from the job store

				UserGridJobFacade.instance().removeUserGridJob( job.getId() );

				// Remove job from the user storage are

				User user = null;
				String jobStoragePath = null;

				user = UserFacade.instance().getUser( job.getUserId() );
				jobStoragePath = UserFacade.instance().getUserGridBaseLocalPath( user, job.getName(), false );
				FileUtil.TryDelete( jobStoragePath, true );
			}
			catch (Exception e)
			{
				throw new ServiceException( "Error while removing job '" + job.getId() + "'", e );
			}
//		}
//		else
//		{
//			throw new ServiceException( "Error while removing job '" + job.getId() + "' (Not a valid job)." );
//		}

		return job;
	}

	public void saveUserJob(UserGridJob job) throws ServiceException
	{
		try
		{
			UserGridJobFacade.instance().setUserGridJob( job );
		}
		catch (Exception e)
		{
			throw new ServiceException(e);
		}
	}

	public List<UserGridJob> updateUserJobsFromMiddleware() throws ServiceException
	{
		if ( !this.getMiddleware().getJobScheduler().isRunning() )
		{
			throw new ServiceException( "Scheduler is not running." );
		}

		List<UserGridJob> updatedJobs = null;
		updatedJobs = new ArrayList<UserGridJob>();

		try
		{
			List<UserGridJob> jobs = null;
			jobs = UserGridJobFacade.instance().pendingUserGridJobs();
			for (UserGridJob job : jobs)
			{
				ExecutionStatus status = null;

				if (
					! this.isValidUserJob( job )
					|| ! this.existsUserJobOnMiddleware( job )
				) {
					continue;
				}

				status = this.getStatusFromMiddleware( job );
				if ( status != job.getStatus() )
				{
					// Update the execution status
					job.setStatus( status );
					// Add the Stopping date (if needed)
					if (
						status == ExecutionStatus.FAILED
						|| status == ExecutionStatus.ABORTED
						|| status == ExecutionStatus.FINISHED
						|| status == ExecutionStatus.CANCELLED
					) {
						job.setStoppingDate( new Date() );
					}

					UserGridJobFacade.instance().setUserGridJob( job );

					updatedJobs.add( job );
				}
			}
		}
		catch (Exception e)
		{
			throw new ServiceException(e);
		}

		return updatedJobs;
	}

	//@} IGridJobExecutionService implementation

	/**
	 * Returns the current middleware on which this service relies.
	 */
	protected IMiddlewareManager getMiddleware()
	{
		if ( this.middleware == null )
		{
			this.middleware = MiddlewareFactory.instance().getMiddlewareManager();
		}

		return this.middleware;
	}

	protected boolean existsUserJobOnMiddleware(UserGridJob job) throws Exception
	{
		if ( !this.getMiddleware().getJobScheduler().isRunning() )
		{
			throw new ServiceException( "Scheduler is not running." );
		}

		return this.getMiddleware().getJobScheduler().existsJob( job.getSchedulerJobId() );
	}

	/**
	 * Private class for holding the singleton instance.
	 */
	private static final class InstanceHolder
	{
		private final static GridJobExecutionService instance = new GridJobExecutionService();
	}
}
