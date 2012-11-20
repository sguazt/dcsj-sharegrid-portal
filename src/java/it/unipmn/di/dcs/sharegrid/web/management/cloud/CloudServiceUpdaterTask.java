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

package it.unipmn.di.dcs.sharegrid.web.management.cloud;

import it.unipmn.di.dcs.common.format.SizeFormat;
import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;

import it.unipmn.di.dcs.sharegrid.web.management.IDAOFactory;
import it.unipmn.di.dcs.sharegrid.web.management.JdbcDAOFactory;
import it.unipmn.di.dcs.sharegrid.web.model.Conf;
import it.unipmn.di.dcs.sharegrid.web.model.IMailSender;
import it.unipmn.di.dcs.sharegrid.web.model.NotificationSenderFactory;
import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;
import it.unipmn.di.dcs.sharegrid.web.model.cloud.UserCloudService;
import it.unipmn.di.dcs.sharegrid.web.model.cloud.UserCloudServiceFacade;
//import it.unipmn.di.dcs.sharegrid.web.service.ICloudServiceService;
//import it.unipmn.di.dcs.sharegrid.web.service.ServiceException;
//import it.unipmn.di.dcs.sharegrid.web.service.ServiceFactory;

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
 * Class responsible of dynamically updating informations of scheduled services.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
//public class CloudServiceUpdaterTask extends TimerTask
public class CloudServiceUpdaterTask implements Runnable
{
	private static transient final Logger Log = Logger.getLogger( CloudServiceUpdaterTask.class.getName() );

	private int minutes = 5;
	private CloudServiceUpdaterState curState;
	private ICloudServiceUpdaterStateDAO updaterStateDAO;
	private IUserCloudServiceCheckStateDAO svcCheckStateDAO;

	/** A constructor. */
	public CloudServiceUpdaterTask(int minutes)
	{
		this.minutes = minutes;
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
			this.updaterStateDAO = daoFactory.getCloudServiceUpdaterStateDAO();
			this.svcCheckStateDAO = daoFactory.getUserCloudServiceCheckStateDAO();
		}
		catch (Exception e)
		{
			CloudServiceUpdaterTask.Log.log( Level.SEVERE, "Unable to start the updater.", e );
			Thread.currentThread().interrupt();
			return;
		}

		while ( !isInterrupted )
		{
			try
			{
				CloudServiceUpdaterTask.Log.info( "Sleeping..." );

				Thread.sleep( 1000 * 60 * this.minutes );

				CloudServiceUpdaterTask.Log.info( "Retrieving previous state ..." );

				dtNow = new Date();

				List<CloudServiceUpdaterState> states = this.updaterStateDAO.list();
				if ( states.size() > 0 )
				{
					this.curState = states.get(0);
				}
				else
				{
					this.curState = new CloudServiceUpdaterState();
				}
				states = null;

				Date checkDate = null;
				Integer stateId = null;

//				CloudServiceUpdaterTask.Log.info( "Running the service lifetime check..." );
//
//				checkDate = this.doServiceLifetimeCheck();

//				CloudServiceUpdaterTask.Log.info( "Saving new state ..." );
//
//				this.curState.setLastLifetimeCheckDate(
//					( checkDate == null || checkDate.compareTo(dtNow) < 0 )
//					? dtNow
//					: checkDate
//				);
//				Integer stateId = this.updaterStateDAO.save( this.curState );
//				this.curState.setId( stateId );

				CloudServiceUpdaterTask.Log.info( "Running the service status updater..." );

				this.doServiceStatusUpdate();

				CloudServiceUpdaterTask.Log.info( "Saving new state ..." );

				this.curState.setLastStatusCheckDate( dtNow );
				stateId = this.updaterStateDAO.save( this.curState );
				this.curState.setId( stateId );
			}
			catch (InterruptedException ie)
			{
				CloudServiceUpdaterTask.Log.info( "Intercetted interruption request. Deferring..." );
				isInterrupted = true;
			}
			catch (Exception e)
			{
				CloudServiceUpdaterTask.Log.log( Level.SEVERE, "Error while executing the Grid Service Updater.", e );
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
	 * Looks for pending services, update them (when possible) and
	 * notifies users who submittied them.
	 *
	 * User notification is cumulative: for each user sends a
	 * notification containing the list of updated services.
	 */
	public Date doServiceStatusUpdate()
	{
		CloudServiceUpdaterTask.Log.info( "Starting Service Status Check..." );

		Date statusCheckDate = null;

		try
		{
			//ICloudServiceService svcSvc = null;
			List<UserCloudService> updatedSvcs = null;
			Map<Integer,StringBuilder> userSvcMap = null;

			CloudServiceUpdaterTask.Log.info( "Updating services from middleware..." );

			//// Gets the service class for service execution
			//svcSvc = ServiceFactory.instance().cloudServiceService();
			// Retrieves service updates from the middleware and saves
			// related services
			updatedSvcs = UserCloudServiceFacade.instance().updateUserServicesFromMiddleware();
			if ( updatedSvcs != null && !updatedSvcs.isEmpty() )
			{
				// Create a map for holding the association:
				//   <user-id, list-of-svc-message>
				userSvcMap = new HashMap<Integer,StringBuilder>();

				CloudServiceUpdaterTask.Log.info( "Preparing notification messages..." );

				String svcDetailUrlMainPart = Conf.instance().getWebCloudServiceDetailUrl().toString();

				// Iterates through the updated services and prepares the
				// notification messages.
				for (UserCloudService svc : updatedSvcs)
				{
//					if (
//						svc.getSubmissionDate() != null
//						&& this.curState.getLastStatusCheckDate() != null
//						&& svc.getSubmissionDate().compareTo( this.curState.getLastStatusCheckDate() ) < 0
//					) {
//						CloudServiceUpdaterTask.Log.info( "Skipping service '" + svc.getName() + "' (#" + svc.getId() + ")..." );
//						continue;
//					}

					// Makes sure to notifies only services with a
					// "final" status
					if (
						svc.getStatus() == ExecutionStatus.FAILED
						|| svc.getStatus() == ExecutionStatus.ABORTED
						|| svc.getStatus() == ExecutionStatus.FINISHED
						|| svc.getStatus() == ExecutionStatus.CANCELLED
					) {
						CloudServiceUpdaterTask.Log.info( "Preparing notification message for service '" + svc.getName() + "' (#" + svc.getId() + ")..." );

						if ( ! userSvcMap.containsKey( svc.getUserId() ) )
						{
							userSvcMap.put( svc.getUserId(), new StringBuilder() );
						}

						URL svcDetailUrl = null;
						svcDetailUrl = new URL(
							svcDetailUrlMainPart
							+ "?id=" + URLEncoder.encode( Integer.toString( svc.getId() ), "UTF-8" )
						);
						//userSvcMap.get( user.getId() ).append( "[URL: " + svcDetailUrl.toString() + "]\n\n" );

						userSvcMap.get( svc.getUserId() ).append(
							"<Name: " + svc.getName() + ", Submitted: " + svc.getSubmissionDate() + ", Status: " + svc.getStatus() + ">\n" 
							+ "[URL: " + svcDetailUrl.toString() + "]\n\n"
						);
					}
				}

				// Notifies to users (if any)
				if ( ! userSvcMap.isEmpty() )
				{
					List<User> users = null;

					CloudServiceUpdaterTask.Log.info( "Notifying to users..." );

					users = UserFacade.instance().users( userSvcMap.keySet().toArray(new Integer[0]) );

					String dtNowStr = null;
					SimpleDateFormat dateFormat = new SimpleDateFormat( Conf.instance().getFormatDateTime() );
					dtNowStr = dateFormat.format( new Date() );
					dateFormat = null;

					String userStorageUrlStr = Conf.instance().getWebUserStorageUrl().toString();

					// Notifies only service with a "final" status
					for (User user : users)
					{
						CloudServiceUpdaterTask.Log.info( "Notifying to user '" + user.getName() + "' (#" + user.getId() + ") [" + user.getEmail() + "]..." );

						// Notify to the user
						IMailSender mailer = null;
						mailer = NotificationSenderFactory.instance().getMailSender();
						if ( mailer != null )
						{
//							URL svcDetailUrl = null;
//							svcDetailUrl = new URL(
//								Conf.instance().getWebCloudServiceDetailUrl().toString()
//								+ "?id=" + URLEncoder.encode( Integer.toString( user.getId() ), "UTF-8" )
//							);
//
//							userSvcMap.get( user.getId() ).append( "[URL: " + svcDetailUrl.toString() + "]\n\n" );

							mailer.send(
								Conf.instance().getSmtpCloudServiceStatusUpdateSender(),
								new String[] { user.getEmail() },
								Conf.instance().getSmtpCloudServiceStatusUpdateSubject(),
								MessageFormat.format(
									Conf.instance().getSmtpCloudServiceStatusUpdateBody(),
									userSvcMap.get( user.getId() ).toString(),
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
				CloudServiceUpdaterTask.Log.info( "No service found..." );
			}
		}
		catch (Exception e)
		{
			//throw new ManagementException(se);
			CloudServiceUpdaterTask.Log.log( Level.SEVERE, "Error while updating User Grid Services", e );
		}

		CloudServiceUpdaterTask.Log.info( "Stopping Service Status Check..." );

		return statusCheckDate;
	}

	/** Class application entry-point. */
	public static void main(String[] args)
	{
		CloudServiceUpdaterTask.Log.setLevel( Level.FINE );

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

		CloudServiceUpdaterTask.Log.info( "Preparing the env..." );

//		try
//		{
//			ManagementEnv.instance().init();
//		}
//		catch (ManagementException me)
//		{
//			CloudServiceUpdaterTask.Log.log( Level.SEVERE, "Could not initialize the environment.", me );
//			System.exit(1);
//		}

		CloudServiceUpdaterTask.Log.info( "Starting the scheduler..." );

//		Timer scheduler = new Timer( "DCS_ShareGrid_SvcUpdater");

		CloudServiceUpdaterTask.Log.info( "Scheduling..." );

//		scheduler.schedule(
//			new CloudServiceUpdaterTask(),
//			new Date( System.currentTimeMillis() + 5 )
//		);

//		CloudServiceUpdaterTask.Log.info( "Scheduled..." );

		Runnable updater = null;
		Thread runner = null;

		updater = new CloudServiceUpdaterTask( minutes );
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
			CloudServiceUpdaterTask.Log.log( Level.SEVERE, "Error while waiting the updater task", e );
			System.exit(2);
		}

		CloudServiceUpdaterTask.Log.info( "Stopping the scheduler..." );
	}
}
