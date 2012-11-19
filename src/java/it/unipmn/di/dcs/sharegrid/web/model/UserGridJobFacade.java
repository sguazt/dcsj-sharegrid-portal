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

import it.unipmn.di.dcs.grid.core.format.ExportFormatType;
import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJobHandle;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ease the user grid jobs management.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public final class UserGridJobFacade
{
	private static Logger Log = Logger.getLogger( UserGridJobFacade.class.getName() );
	private IUserGridJobDAO gridJobDao;

	private UserGridJobFacade()
	{
		try
		{
			this.setUserGridJobDAO( Conf.instance().getDAOFactory().getUserGridJobDAO() );
		}
		catch (Exception e)
		{
			UserGridJobFacade.Log.severe( "Unable to retrieve UserGridJobDAO from configuration: " + e.toString() );

			this.setUserGridJobDAO( null );
		}
	}

	private UserGridJobFacade(IUserGridJobDAO gridJobDao)
	{
		this.setUserGridJobDAO( gridJobDao );
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static UserGridJobFacade instance()
	{
		return InstanceHolder.instance;
	}

	/**
	 * Set a new state for the given user grid job.
	 */
	public UserGridJob createUserGridJob(User user, IJobHandle jhnd, IJob job) throws ModelException
	{
		// Creates a user grid job object
		UserGridJob userJob = UserGridJobFacade.CreateUserGridJob( user, jhnd, job );
		if ( userJob == null )
		{
			throw new ModelException("Unexpected error while creating the user grid job: <" + user + "," + jhnd + ">");
		}

		// Adds the user object to the data-store
		int jobId;
		try
		{
			jobId = this.getUserGridJobDAO().insert( userJob );
		}
		catch (Exception e)
		{
			throw new ModelException("Unable to add the user grid job: " + userJob, e);
		}
		userJob.setId( jobId );

		return userJob;
	}

	/**
	 * Set a new state for the given user grid job.
	 */
	public void setUserGridJob(UserGridJob job) throws ModelException
	{
		try
		{
			this.getUserGridJobDAO().save( job );
		}
		catch (Exception e)
		{
			throw new ModelException("Error while saving user grid job '" + job + "'.", e);
		}
	}

	public UserGridJob getUserGridJob(int jobId) throws ModelException
	{
		try
		{
			return this.getUserGridJobDAO().load( jobId );
		}
		catch (Exception e)
		{
			throw new ModelException("Error while loading user grid job '" + jobId + "'.", e);
		}
	}

	public boolean existsUserGridJob(int jobId) throws ModelException
	{
		try
		{
			return this.getUserGridJobDAO().exists( jobId );
		}
		catch (Exception e)
		{
			throw new ModelException("Error while checking for existence of user grid job '" + jobId + "'.", e);
		}
	}

	public void removeUserGridJob(int jobId) throws ModelException
	{
		try
		{
			this.getUserGridJobDAO().remove( jobId );
		}
		catch (Exception e)
		{
			throw new ModelException("Error while removing user grid job '" + jobId + "'.", e);
		}
	}

	public List<UserGridJob> userGridJobs(User user) throws ModelException
	{
		try
		{
			return this.getUserGridJobDAO().listFromUser( user.getId() );
		}
		catch (Exception e)
		{
			throw new ModelException("Error while retrieving user grid jobs from user.", e);
		}
	}

	public List<UserGridJob> userGridJobs() throws ModelException
	{
		try
		{
			return this.getUserGridJobDAO().list();
		}
		catch (Exception e)
		{
			throw new ModelException("Error while retrieving user grid jobs.", e);
		}
	}

	public List<UserGridJob> userGridJobsFromStatus(User user, ExecutionStatus[] status) throws ModelException
	{
		try
		{
			return this.getUserGridJobDAO().listFromUserAndStatus(user.getId(), status);
		}
		catch (Exception e)
		{
			throw new ModelException("Error while retrieving user grid jobs from user and status.", e);
		}
	}

	public List<UserGridJob> userGridJobsFromStatus(ExecutionStatus[] status) throws ModelException
	{
		try
		{
			return this.getUserGridJobDAO().listFromStatus(status);
		}
		catch (Exception e)
		{
			throw new ModelException("Error while retrieving user grid jobs from status.", e);
		}
	}

	public List<UserGridJob> pendingUserGridJobs() throws ModelException
	{
		try
		{
			return this.getUserGridJobDAO().listPendings();
		}
		catch (Exception e)
		{
			throw new ModelException("Error while retrieving pending user grid jobs.", e);
		}
	}

	public List<UserGridJob> pendingUserGridJobsByMinAge(long minAge) throws ModelException
	{
		// preconditions
		assert( minAge > 0 );

		if ( minAge <= 0 )
		{
			throw new ModelException("Age must be a positive value.");
		}

		try
		{
			return this.getUserGridJobDAO().listPendingsByMinAge(minAge);
		}
		catch (Exception e)
		{
			throw new ModelException("Error while retrieving pending user grid jobs (min age '" + minAge + "').", e);
		}
	}

//	protected List<UserGridJob> pendingUserGridJobsByAgeRange(int minAge, int maxAge) throws ModelException
//	{
//		// preconditions
//		assert( minAge > 0 || maxAge > 0 );
//
//		try
//		{
//			if ( maxAge <= 0 )
//			{
//				return this.getUserGridJobDAO().listPendingsByMinAge(minAge);
//			}
//			if ( minAge <= 0 )
//			{
//				return this.getUserGridJobDAO().listPendingsByMaxAge(maxAge);
//			}
//			return this.getUserGridJobDAO().listPendingsByAgeRange(minAge, maxAge);
//		}
//		catch (Exception e)
//		{
//			throw new ModelException("Error while retrieving pending user grid jobs (min age '" + minAge + "', max age '" + maxAge + "').", e);
//		}
//	}

	/**
	 * Returns the store for the user grid jobs.
	 */
	protected IUserGridJobDAO getUserGridJobDAO()
	{
		return this.gridJobDao;
	}

	/**
	 * Sets the store for the user grid jobs.
	 */
	protected void setUserGridJobDAO(IUserGridJobDAO value)
	{
		this.gridJobDao = value;
	}

	/** Creates a {@code UserGridJob} object from the given parameters. */
	protected static UserGridJob CreateUserGridJob(User user, IJobHandle jhnd, IJob job)
	{
		UserGridJob userJob = new UserGridJob();

		userJob.setUserId( user.getId() );
		userJob.setName( jhnd.getName() );
		userJob.setSubmissionDate( new Date() );
		userJob.setStoppingDate( null );
		userJob.setMiddlewareId( null ); //FIXME: where to get this value?
		userJob.setSchedulerJobId( jhnd.getId() );
		userJob.setStatus( ExecutionStatus.UNKNOWN );
		userJob.setRawJob( job );

		return userJob;
	}

	/**
	 * Private class for holding the singleton instance.
	 */
	private static class InstanceHolder
	{
		private final static UserGridJobFacade instance = new UserGridJobFacade();
	}
}
