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

package it.unipmn.di.dcs.sharegrid.web.management.grid;

import it.unipmn.di.dcs.common.format.SizeFormat;
import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;

import it.unipmn.di.dcs.sharegrid.web.management.IDAOFactory;
import it.unipmn.di.dcs.sharegrid.web.management.JdbcDAOFactory;
import it.unipmn.di.dcs.sharegrid.web.model.Conf;
import it.unipmn.di.dcs.sharegrid.web.model.IMailSender;
import it.unipmn.di.dcs.sharegrid.web.model.NotificationSenderFactory;
import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;
import it.unipmn.di.dcs.sharegrid.web.model.UserGridJob;
import it.unipmn.di.dcs.sharegrid.web.service.IGridJobExecutionService;
import it.unipmn.di.dcs.sharegrid.web.service.ServiceException;
import it.unipmn.di.dcs.sharegrid.web.service.ServiceFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.Timer;
//import java.util.TimerTask;

/**
 * Class responsible of dynamically updating informations of scheduled jobs.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
//public class GridJobUpdaterTask extends TimerTask
public class GridJobUpdaterTask implements Runnable
{
	private static transient final Logger Log = Logger.getLogger( GridJobUpdaterTask.class.getName() );

	private int minutes = 5;
	private long ageThreshold = 30*24*60*60; // 1 month (in secs)
	private long ageWarn = 30*24*60*60 - 86400 * 10; // 1 month - 10 days (in secs)
	private long timeBeforeNextNotify = 24*60*60; // 1 day (in secs)
	private GridJobUpdaterState curState;
	private IGridJobUpdaterStateDAO updaterStateDAO;
	private IUserGridJobCheckStateDAO jobCheckStateDAO;

	/** A constructor. */
	public GridJobUpdaterTask(int minutes, long ageThreshold, long ageWarn)
	{
		this.minutes = minutes;
		this.ageThreshold = ageThreshold;
		this.ageWarn = ageWarn;
	}

	//@{ Runnable implementation ///////////////////////////////////////////

	public void run()
	{
		boolean isInterrupted = false;
		Date dtNow = null;

		try
		{
			IDAOFactory daoFactory = null;
			daoFactory = new JdbcDAOFactory(
				Conf.instance().getDaoJdbcDriver(),
				Conf.instance().getDaoJdbcUrl(),
				Conf.instance().getDaoJdbcUser(),
				Conf.instance().getDaoJdbcPassword()
			);
			this.updaterStateDAO = daoFactory.getGridJobUpdaterStateDAO();
			this.jobCheckStateDAO = daoFactory.getUserGridJobCheckStateDAO();
		}
		catch (Exception e)
		{
			GridJobUpdaterTask.Log.log( Level.SEVERE, "Unable to start the updater.", e );
			Thread.currentThread().interrupt();
			return;
		}

		while ( !isInterrupted )
		{
			try
			{
				GridJobUpdaterTask.Log.info( "Sleeping..." );

				Thread.sleep( 1000 * 60 * this.minutes );

				GridJobUpdaterTask.Log.info( "Retrieving previous state ..." );

				dtNow = new Date();

				List<GridJobUpdaterState> states = this.updaterStateDAO.list();
				if ( states.size() > 0 )
				{
					this.curState = states.get(0);
				}
				else
				{
					this.curState = new GridJobUpdaterState();
				}
				states = null;

				Date checkDate = null;

				GridJobUpdaterTask.Log.info( "Running the job lifetime check..." );

				checkDate = this.doJobLifetimeCheck();

				GridJobUpdaterTask.Log.info( "Saving new state ..." );

				this.curState.setLastLifetimeCheckDate(
					( checkDate == null || checkDate.compareTo(dtNow) < 0 )
					? dtNow
					: checkDate
				);
				Integer stateId = this.updaterStateDAO.save( this.curState );
				this.curState.setId( stateId );

				GridJobUpdaterTask.Log.info( "Running the job status updater..." );

				this.doJobStatusUpdate();

				GridJobUpdaterTask.Log.info( "Saving new state ..." );

				this.curState.setLastStatusCheckDate( dtNow );
				stateId = this.updaterStateDAO.save( this.curState );
				this.curState.setId( stateId );
			}
			catch (InterruptedException ie)
			{
				GridJobUpdaterTask.Log.info( "Intercetted interruption request. Deferring..." );
				isInterrupted = true;
			}
			catch (Exception e)
			{
				GridJobUpdaterTask.Log.log( Level.SEVERE, "Error while executing the Grid Job Updater.", e );
				isInterrupted = true;
			}
		}

		if ( isInterrupted )
		{
			Thread.currentThread().interrupt();
		}
	}

	//@} Runnable implementation ///////////////////////////////////////////

	/**
	 * Looks for old jobs and jobs which are becoming old, and
	 * notifies users who submittied them.
	 *
	 * User notification is cumulative: for each user sends a
	 * notification containing the list of old/quasi-old jobs.
	 */
	public Date doJobLifetimeCheck()
	{
		GridJobUpdaterTask.Log.info( "Starting Job Lifetime Check..." );
		Date dtNow = new Date();
		long dtNowSecs = Math.round( dtNow.getTime() / 1.0e+3 );
		Date lifetimeCheckDate = null;

		try
		{
			IGridJobExecutionService jobSvc = null;
			List<UserGridJob> jobs = null;
			List<UserGridJobCheckState> jobCheckStates = null;
			Map<Integer,StringBuilder[]> userJobMap = null;

			GridJobUpdaterTask.Log.info( "Retrieving pending jobs from middleware..." );

			// Gets the service class for job execution
			jobSvc = ServiceFactory.instance().gridJobExecutionService();
			// Retrieves not yet closed jobs
			jobs = jobSvc.getPendingUserJobsByMinAge( this.ageWarn );
			if ( jobs != null && !jobs.isEmpty() )
			{
				// Create a map for holding the association:
				//   <user-id, list-of-job-message>
				userJobMap = new HashMap<Integer,StringBuilder[]>();
				jobCheckStates = new ArrayList<UserGridJobCheckState>();

				GridJobUpdaterTask.Log.info( "Preparing notification messages..." );

				String jobDetailUrlMainPart = Conf.instance().getWebGridJobDetailUrl().toString();
				SimpleDateFormat dateFormat = new SimpleDateFormat( Conf.instance().getFormatDateTime() );

				// Iterates through the open jobs and prepares the
				// notification messages.
				for (UserGridJob job : jobs)
				{
					long jobAge = dtNowSecs - Math.round( job.getSubmissionDate().getTime() / 1.0e+3 );

					UserGridJobCheckState jobCheckState = null;
					jobCheckState = this.jobCheckStateDAO.loadByJobId( job.getId() );

					// Checks if the current job really is to be notified
					if ( jobAge < this.ageThreshold )
					{
						if (  jobCheckState != null )
						{
							GridJobUpdaterTask.Log.info( "Skipping job '" + job.getName() + "' (#" + job.getId() + "): lifetime warning notification already sent..." );
							continue;
						}
					}
					else if (
						jobCheckState != null
						&& jobCheckState.getLastLifetimeCheckDate() != null
						&& (dtNowSecs - Math.round( jobCheckState.getLastLifetimeCheckDate().getTime() / 1.0e+3 ) ) > this.timeBeforeNextNotify
					) {
						GridJobUpdaterTask.Log.info( "Skipping job '" + job.getName() + "' (#" + job.getId() + "): lifetime exceeded but too early to notify..." );
						continue;
					}
//					else if ( job.isRunning() )
//					{
//						GridJobUpdaterTask.Log.info( "Skipping job '" + job.getName() + "' (#" + job.getId() + "): lifetime exceeded but the job is still running..." );
//						continue;
//					}

//					if (
//						job.getSubmissionDate() != null
//						&& jobCheckState.getLastLifetimeCheckDate() != null
//						&& job.getSubmissionDate().compareTo( jobCheckState.getLastLifetimeCheckDate() ) < 0
//					) {
//						GridJobUpdaterTask.Log.info( "Skipping job '" + job.getName() + "' (#" + job.getId() + ")..." );
//						continue;
//					}

					// Prepares notification message for current job.

					GridJobUpdaterTask.Log.info( "Preparing notification message for job '" + job.getName() + "' (#" + job.getId() + ")..." );

					String msg = null;
					int index;

					try
					{
						if ( ! userJobMap.containsKey( job.getUserId() ) )
						{
							userJobMap.put( job.getUserId(), new StringBuilder[2] );
							userJobMap.get( job.getUserId() )[0] = new StringBuilder(); // message for old jobs
							userJobMap.get( job.getUserId() )[1] = new StringBuilder(); // message for quasi-old jobs
						}

						URL jobDetailUrl = null;
						jobDetailUrl = new URL(
							jobDetailUrlMainPart
							+ "?id=" + URLEncoder.encode( Integer.toString( job.getId() ), "UTF-8" )
						);
						//userJobMap.get( user.getId() ).append( "[URL: " + jobDetailUrl.toString() + "]\n\n" );

						String customMsg = null;
						if ( jobAge > this.ageThreshold )
						{
							jobSvc.abortUserJob( job );

							index = 0;
							BigDecimal excess = new BigDecimal( jobAge - this.ageThreshold ); // needed because long is 64bit while double is 32bit
							customMsg = "Aborted: max lifetime (" + SizeFormat.FormatClockTimeDiff( this.ageThreshold, false ) + ") exceeded by " + SizeFormat.FormatClockTimeDiff( excess.doubleValue(), false ) + " (approximately)."; //NO I18N
						}
						else
						{
							index = 1;
							BigDecimal tta = new BigDecimal( this.ageThreshold - jobAge ); // needed because long is 64bit while double is 32bit
							customMsg = "Time to Abort: " + SizeFormat.FormatClockTimeDiff( tta.doubleValue(), false ) + " (approximately)."; //NO I18N
						}

						//msg = 	"<Name: " + job.getName() + ", Submitted: " + job.getSubmissionDate() + ", Status: " + job.getStatus() + ">\n" 
						msg =	"<ID: " + job.getId() + ", Name: " + job.getName() + ", Submitted: " + dateFormat.format(job.getSubmissionDate()) + ", Status: " + job.getStatus() + ", Scheduler ID: " + job.getSchedulerJobId() + ">\n" 
							+ ( customMsg != null ? ("!! " + customMsg + " !!\n") : "" )
							+ "[URL: " + jobDetailUrl.toString() + "]\n\n";
					}
					catch (Exception e)
					{
						GridJobUpdaterTask.Log.log(Level.SEVERE, "Error while analyzing job '" + job + "'.", e);
						GridJobUpdaterTask.Log.info( "Skip this job and continue." );
						userJobMap.remove( job.getUserId() );
						continue;
					}

					// Finalize the preparation of the jobs notification
					if ( !Strings.IsNullOrEmpty(msg) )
					{
						userJobMap.get( job.getUserId() )[index].append( msg );

						// Insert an updated job checking state into the list
						// of job state to be later updated.
						if ( jobCheckState == null )
						{
							jobCheckState = new UserGridJobCheckState();
							jobCheckState.setJobId( job.getId() );
						}
						jobCheckState.setLastLifetimeCheckDate( dtNow );
						jobCheckStates.add( jobCheckState );

						// Updates the lifetime check date to be returned.
						if ( lifetimeCheckDate == null || lifetimeCheckDate.compareTo( job.getSubmissionDate() ) < 0 )
						{
							lifetimeCheckDate = job.getSubmissionDate();
						}
					}
				}

				// Notifies to users (if any)
				if ( ! userJobMap.isEmpty() )
				{
					List<User> users = null;

					GridJobUpdaterTask.Log.info( "Notifying to users..." );

					users = UserFacade.instance().users( userJobMap.keySet().toArray(new Integer[0]) );

					String dtNowStr = null;
					//SimpleDateFormat dateFormat = new SimpleDateFormat( Conf.instance().getFormatDateTime() );
					dtNowStr = dateFormat.format( new Date() );
					dateFormat = null;

					// Notifies only job with a "final" status
					for (User user : users)
					{
						GridJobUpdaterTask.Log.info( "Notifying to user '" + user.getName() + "' (#" + user.getId() + ") [" + user.getEmail() + "]..." );

						// Notify to the user
						IMailSender mailer = null;
						mailer = NotificationSenderFactory.instance().getMailSender();
						if ( mailer != null )
						{
							mailer.send(
								Conf.instance().getSmtpGridJobLifeTimeUpdateSender(),
								new String[] { user.getEmail() },
								Conf.instance().getSmtpGridJobLifeTimeUpdateSubject(),
								MessageFormat.format(
									Conf.instance().getSmtpGridJobLifeTimeUpdateBody(),
									(
										userJobMap.get( user.getId() )[0].length() > 0
										? userJobMap.get( user.getId() )[0].toString()
										: "<No Jobs>"
									), // "aborted" jobs
									(
										userJobMap.get( user.getId() )[1].length() > 0
										? userJobMap.get( user.getId() )[1].toString()
										: "<No Jobs>"
									), // "candidate to be aborted" jobs
									dtNowStr
								)
							);
						}
					}

					// Update the user grid job checking states in the persistent mem.
					for (UserGridJobCheckState jobCheckState : jobCheckStates)
					{
						this.jobCheckStateDAO.save( jobCheckState );
					}
				}
			}
			else
			{
				GridJobUpdaterTask.Log.info( "No job found..." );
			}
		}
		catch (Exception e)
		{
			//throw new ManagementException(se);
			GridJobUpdaterTask.Log.log( Level.SEVERE, "Error while checking the lifetime of User Grid Jobs", e );
		}

		GridJobUpdaterTask.Log.info( "Stopping Job Lifetime Check..." );

		return lifetimeCheckDate;
	}

	/**
	 * Looks for pending jobs, update theme (when possible) and
	 * notifies users who submittied them.
	 *
	 * User notification is cumulative: for each user sends a
	 * notification containing the list of updated jobs.
	 */
	public Date doJobStatusUpdate()
	{
		GridJobUpdaterTask.Log.info( "Starting Job Status Check..." );

		Date statusCheckDate = null;

		try
		{
			IGridJobExecutionService jobSvc = null;
			List<UserGridJob> updatedJobs = null;
			Map<Integer,StringBuilder> userJobMap = null;

			GridJobUpdaterTask.Log.info( "Updating jobs from middleware..." );

			// Gets the service class for job execution
			jobSvc = ServiceFactory.instance().gridJobExecutionService();
			// Retrieves job updates from the middleware and saves
			// related jobs
			updatedJobs = jobSvc.updateUserJobsFromMiddleware();
			if ( updatedJobs != null && !updatedJobs.isEmpty() )
			{
				// Create a map for holding the association:
				//   <user-id, list-of-job-message>
				userJobMap = new HashMap<Integer,StringBuilder>();

				GridJobUpdaterTask.Log.info( "Purging jobs and Preparing notification messages..." );

				String jobDetailUrlMainPart = Conf.instance().getWebGridJobDetailUrl().toString();
				SimpleDateFormat dateFormat = new SimpleDateFormat( Conf.instance().getFormatDateTime() );

				// Iterates through the updated jobs and prepares the
				// notification messages.
				for (UserGridJob job : updatedJobs)
				{
//					if (
//						job.getSubmissionDate() != null
//						&& this.curState.getLastStatusCheckDate() != null
//						&& job.getSubmissionDate().compareTo( this.curState.getLastStatusCheckDate() ) < 0
//					) {
//						GridJobUpdaterTask.Log.info( "Skipping job '" + job.getName() + "' (#" + job.getId() + ")..." );
//						continue;
//					}

					// Makes sure to notifies only jobs with a
					// "final" status
					if (
						job.getStatus() == ExecutionStatus.FAILED
						|| job.getStatus() == ExecutionStatus.ABORTED
						|| job.getStatus() == ExecutionStatus.FINISHED
						|| job.getStatus() == ExecutionStatus.CANCELLED
					) {
						GridJobUpdaterTask.Log.info( "Purging job '" + job.getName() + "' (#" + job.getId() + ") from the scheduler queue..." );

						jobSvc.purgeUserJob(job);

						GridJobUpdaterTask.Log.info( "Preparing notification message for job '" + job.getName() + "' (#" + job.getId() + ")..." );

						if ( ! userJobMap.containsKey( job.getUserId() ) )
						{
							userJobMap.put( job.getUserId(), new StringBuilder() );
						}

						URL jobDetailUrl = null;
						jobDetailUrl = new URL(
							jobDetailUrlMainPart
							+ "?id=" + URLEncoder.encode( Integer.toString( job.getId() ), "UTF-8" )
						);
						//userJobMap.get( user.getId() ).append( "[URL: " + jobDetailUrl.toString() + "]\n\n" );

						userJobMap.get( job.getUserId() ).append(
							//"<"Name: " + job.getName() + ", Submitted: " + job.getSubmissionDate() + ", Status: " + job.getStatus() + ">\n" 
							"<ID: " + job.getId() + ", Name: " + job.getName() + ", Submitted: " + dateFormat.format(job.getSubmissionDate()) + ", Status: " + job.getStatus() + ", Scheduler ID: " + job.getSchedulerJobId() + ">\n" 
							+ "[URL: " + jobDetailUrl.toString() + "]\n\n"
						);
					}
				}

				// Notifies to users (if any)
				if ( ! userJobMap.isEmpty() )
				{
					List<User> users = null;

					GridJobUpdaterTask.Log.info( "Notifying to users..." );

					users = UserFacade.instance().users( userJobMap.keySet().toArray(new Integer[0]) );

					String dtNowStr = null;
					//SimpleDateFormat dateFormat = new SimpleDateFormat( Conf.instance().getFormatDateTime() );
					dtNowStr = dateFormat.format( new Date() );
					dateFormat = null;

					String userStorageUrlStr = Conf.instance().getWebUserStorageUrl().toString();

					// Notifies only job with a "final" status
					for (User user : users)
					{
						GridJobUpdaterTask.Log.info( "Notifying to user '" + user.getName() + "' (#" + user.getId() + ") [" + user.getEmail() + "]..." );

						// Notify to the user
						IMailSender mailer = null;
						mailer = NotificationSenderFactory.instance().getMailSender();
						if ( mailer != null )
						{
//							URL jobDetailUrl = null;
//							jobDetailUrl = new URL(
//								Conf.instance().getWebGridJobDetailUrl().toString()
//								+ "?id=" + URLEncoder.encode( Integer.toString( user.getId() ), "UTF-8" )
//							);
//
//							userJobMap.get( user.getId() ).append( "[URL: " + jobDetailUrl.toString() + "]\n\n" );

							mailer.send(
								Conf.instance().getSmtpGridJobStatusUpdateSender(),
								new String[] { user.getEmail() },
								Conf.instance().getSmtpGridJobStatusUpdateSubject(),
								MessageFormat.format(
									Conf.instance().getSmtpGridJobStatusUpdateBody(),
									userJobMap.get( user.getId() ).toString(),
									userStorageUrlStr,
									dtNowStr
								)
							);
						}
					}
				}
			}
			else
			{
				GridJobUpdaterTask.Log.info( "No job found..." );
			}
		}
		catch (Exception e)
		{
			//throw new ManagementException(se);
			GridJobUpdaterTask.Log.log( Level.SEVERE, "Error while updating User Grid Jobs", e );
		}

		GridJobUpdaterTask.Log.info( "Stopping Job Status Check..." );

		return statusCheckDate;
	}

	/** Class application entry-point. */
	public static void main(String[] args)
	{
		GridJobUpdaterTask.Log.setLevel( Level.FINE );

		int minutes = 5;
		long ageThreshold = 30*24*60*60; // 1 month (in secs)
		long ageWarn = 30*24*60*60 - 86400 * 10; // 1 month - 10 days (in secs)

		if ( args.length > 0 )
		{
			minutes = Integer.parseInt( args[0] );
		}
		if ( args.length > 1 )
		{
			ageThreshold = Integer.parseInt( args[1] );
		}
		if ( args.length > 2 )
		{
			ageWarn = Integer.parseInt( args[2] );
		}

		GridJobUpdaterTask.Log.info( "Preparing the env..." );

//		try
//		{
//			ManagementEnv.instance().init();
//		}
//		catch (ManagementException me)
//		{
//			GridJobUpdaterTask.Log.log( Level.SEVERE, "Could not initialize the environment.", me );
//			System.exit(1);
//		}

		GridJobUpdaterTask.Log.info( "Starting the scheduler..." );

//		Timer scheduler = new Timer( "DCS_ShareGrid_JobUpdater");

		GridJobUpdaterTask.Log.info( "Scheduling..." );

//		scheduler.schedule(
//			new GridJobUpdaterTask(),
//			new Date( System.currentTimeMillis() + 5 )
//		);

//		GridJobUpdaterTask.Log.info( "Scheduled..." );

		Runnable updater = null;
		Thread runner = null;

		updater = new GridJobUpdaterTask( minutes, ageThreshold, ageWarn );
		runner = new Thread( updater );
		//runner.setDaemon( true );

		if ( runner != null && !runner.isAlive() )
		{
			runner.start();
		}

		try
		{
			if ( runner.isAlive() )
			{
				Thread.currentThread().join();
			}
		}
		catch (Exception e)
		{
			GridJobUpdaterTask.Log.log( Level.SEVERE, "Error while waiting the updater task", e );
			System.exit(2);
		}

		GridJobUpdaterTask.Log.info( "Stopping the scheduler..." );
	}
}
