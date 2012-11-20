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
//import it.unipmn.di.dcs.cloud.core.middleware.service.IServiceSchedulerService;
import it.unipmn.di.dcs.cloud.monitor.IServiceProberStats;
import it.unipmn.di.dcs.cloud.monitor.ServiceProber;

import it.unipmn.di.dcs.sharegrid.web.model.Conf;
import it.unipmn.di.dcs.sharegrid.web.model.ModelException;
import it.unipmn.di.dcs.sharegrid.web.model.User;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Facade class for ser cloud services.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public final class UserCloudServiceFacade
{
	/** The class logger. */
	private static final transient Logger Log = Logger.getLogger( UserCloudServiceFacade.class.getName() );

	/** The user service DAO. */
	private IUserCloudServiceDAO cloudSvcDao;

	/** A constructor. */
	private UserCloudServiceFacade()
	{
		try
		{
			this.setUserCloudServiceDAO( Conf.instance().getDAOFactory().getUserCloudServiceDAO() );
		}
		catch (Exception e)
		{
			UserCloudServiceFacade.Log.severe( "Unable to retrieve UserCloudServiceDAO from configuration: " + e.toString() );

			this.setUserCloudServiceDAO( null );
		}
	}

	/** A constructor. */
	protected UserCloudServiceFacade(IUserCloudServiceDAO cloudSvcDao)
	{
		this.setUserCloudServiceDAO( cloudSvcDao );
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static UserCloudServiceFacade instance()
	{
		return InstanceHolder.instance;
	}

	/**
	 * Excecutes the given service on the given machine (if possible) for
	 * the given user.
	 */
	public UserCloudService startUserService(User user, ICloudService service, IPhysicalMachine machine, String name) throws ModelException
	{
		UserCloudService userSvc = null;

		try
		{
			IServiceHandle shnd = null;

			shnd = CloudMiddlewareServiceFacade.Instance().startService( service, machine );

			userSvc = this.createUserService( user, shnd, name );

			userSvc.setStatus(
				CloudMiddlewareServiceFacade.Instance().getServiceStatus( shnd )
			);

			this.saveUserService( userSvc );
		}
		catch (ModelException me)
		{
			throw me;
		}
		catch (Exception e)
		{
			throw new ModelException("Error while starting the service '" + service + "' on machine '" + machine + "'.", e);
		}

		return userSvc;
	}

	/**
	 * Cancel the execution of the given user service.
	 *
	 * @return {@code true} if the service is really canceled; {@code false}
	 * if not (e.g. if the service was already stopped).
	 */
	public boolean stopUserService(UserCloudService service) throws ModelException
	{
		boolean stopped = false;

		try
		{
			IServiceHandle shnd = null;

			shnd = CloudMiddlewareServiceFacade.Instance().getServiceHandle( service.getSchedulerServiceId() );

			//boolean needUpdate = false;
			//ExecutionStatus status = null;
			//status = service.getStatus();
			//if (
			//	status != ExecutionStatus.READY
			//	&& status != ExecutionStatus.RUNNING
			//	&& status != ExecutionStatus.UNSTARTED
			//	&& status != ExecutionStatus.UNKNOWN
			//) {
			//	// Retrieves status from scheduler
			//	// (in case of a status update)
			//
			//	status = CloudMiddlewareServiceFacade.Instance().getServiceStatus( shnd );
			//	if (
			//		status == ExecutionStatus.READY
			//		|| status == ExecutionStatus.RUNNING
			//		|| status == ExecutionStatus.UNSTARTED
			//		|| status == ExecutionStatus.UNKNOWN
			//	) {
			//		stopped = CloudMiddlewareServiceFacade.Instance().stopService( shnd );
			//	}
			//	else
			//	{
			//		stopped = true;
			//	}
			//
			//	needUpdate = stopped;
			//}
			//else
			//{
			//	stopped = true;
			//}

			stopped = CloudMiddlewareServiceFacade.Instance().stopService( shnd );

			if ( stopped )
			{
				service.setStatus( ExecutionStatus.CANCELLED );
				service.setStoppingDate( new Date() );

				this.saveUserService( service );
			}
		}
		catch (Exception e)
		{
			throw new ModelException("Error while stopping the service '" + service + "'.", e);
		}

		return stopped;
	}

	/**
	 * Save the given user service inside the data store.
	 */
	public void saveUserService(UserCloudService svc) throws ModelException
	{
		try
		{
			this.getUserCloudServiceDAO().save( svc );
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Load from the data store the user service associated to the given id.
	 */
	public UserCloudService loadUserService(int svcId) throws ModelException
	{
		try
		{
			return this.getUserCloudServiceDAO().load( svcId );
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Returns {@code true} if a user service for the given id does exist.
	 */
	public boolean existsUserService(int svcId) throws ModelException
	{
		try
		{
			return this.getUserCloudServiceDAO().exists( svcId );
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Remove from the data store the user service associated to the
	 * given id.
	 */
	public void removeUserService(UserCloudService service) throws ModelException
	{
		try
		{
			// Stop the service
			try
			{
				this.stopUserService( service );
			}
			catch (Exception e)
			{
				// ignore errors
			}

			// Remove the service from the DB
			this.getUserCloudServiceDAO().remove( service.getId() );
		}
		catch (ModelException me)
		{
			throw me;
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Remove from the data store the user service associated to the
	 * given id.
	 */
	public void removeUserService(int svcId) throws ModelException
	{
		try
		{
			// Stop the service
			try
			{
				this.stopUserService( this.loadUserService(svcId) );
			}
			catch (Exception e)
			{
				// ignore
			}

			// Remove the service from the DB
			this.getUserCloudServiceDAO().remove( svcId );
		}
		catch (ModelException me)
		{
			throw me;
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Probe the given service and return the associated statistics.
	 */
	public IServiceProberStats probeUserService(UserCloudService svc) throws ModelException
	{
		IServiceProberStats stats = null;

		try
		{
			IServiceHandle shnd = null;

			shnd = CloudMiddlewareServiceFacade.Instance().getServiceHandle( svc.getSchedulerServiceId() );
			if ( shnd == null )
			{
				throw new ModelException("Service '" + svc.getId() + "' not found on the scheduler. Maybe  not running?");
			}

			ServiceProber prober = null;

			prober = new ServiceProber(
				CloudMiddlewareFactory.instance().getMiddlewareManager()
			);

			stats = prober.probe( shnd );

			shnd = null;
		}
		catch (ModelException me)
		{
			throw me;
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}

		return stats;
	}

	/**
	 * Returns the list of the services submitted by the given user.
	 */
	public List<UserCloudService> userServices(User user) throws ModelException
	{
		try
		{
			return this.getUserCloudServiceDAO().listFromUser( user.getId() );
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Returns the list of the services submitted by all of the users.
	 */
	public List<UserCloudService> userServices() throws ModelException
	{
		try
		{
			return this.getUserCloudServiceDAO().list();
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Returns the list of the services submitted by the given user whose
	 * execution status match the one given as parameter.
	 */
	public List<UserCloudService> userServicesFromStatus(User user, ExecutionStatus[] status) throws ModelException
	{
		try
		{
			return this.getUserCloudServiceDAO().listFromUserAndStatus(user.getId(), status);
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Returns the list of the services submitted by all of the users whose
	 * execution status match the one given as parameter.
	 */
	public List<UserCloudService> userServicesFromStatus(ExecutionStatus[] status) throws ModelException
	{
		try
		{
			return this.getUserCloudServiceDAO().listFromStatus(status);
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	public List<UserCloudService> pendingUserCloudServices() throws ModelException
	{
		try
		{
			return this.getUserCloudServiceDAO().listPendings();
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	public List<UserCloudService> updateUserServicesFromMiddleware() throws ModelException
	{
		List<UserCloudService> updatedSvcs = new ArrayList<UserCloudService>();

		try
		{
			List<UserCloudService> services = null;
			services = UserCloudServiceFacade.instance().pendingUserCloudServices();
			for (UserCloudService service : services)
			{
//TODO:
//				if (
//					! this.isValidUserJob( service )
//					|| ! this.existsUserServiceOnMiddleware( service )
//				) {
//					continue;
//				}

				IServiceHandle shnd = null;
				ExecutionStatus status = null;

				shnd = CloudMiddlewareServiceFacade.Instance().getServiceHandle( service.getSchedulerServiceId() );

				status = CloudMiddlewareServiceFacade.Instance().getServiceStatus( shnd );

				if ( status != service.getStatus() )
				{
					// Update the execution status
					service.setStatus( status );
					// Add the Stopping date (if needed)
					if (
						status == ExecutionStatus.FAILED
						|| status == ExecutionStatus.ABORTED
//						|| status == ExecutionStatus.FINISHED
						|| status == ExecutionStatus.STOPPED
						|| status == ExecutionStatus.CANCELLED
					) {
						service.setStoppingDate( new Date() );
					}
					this.saveUserService( service );
					updatedSvcs.add( service );
				}
			}
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}

		return updatedSvcs;
	}

	/**
	 * Returns the store for the user services.
	 */
	protected IUserCloudServiceDAO getUserCloudServiceDAO()
	{
		return this.cloudSvcDao;
	}

	/**
	 * Sets the store for the user services.
	 */
	protected void setUserCloudServiceDAO(IUserCloudServiceDAO value)
	{
		this.cloudSvcDao = value;
	}

	/**
	 * Creates a new user service for the given service handle.
	 */
	protected UserCloudService createUserService(User user, IServiceHandle shnd, String name) throws ModelException
	{
		// Creates a user service object
		UserCloudService userSvc = UserCloudServiceFacade.CreateUserService( user, shnd, name );
		if ( userSvc == null )
		{
			throw new ModelException("Unexpected error while creating the user service: <" + user + "," + shnd + ">");
		}

		// Adds the user object to the data-store
		int svcId;
		try
		{
			svcId = this.getUserCloudServiceDAO().insert( userSvc );
		}
		catch (Exception e)
		{
			throw new ModelException("Unable to add the user service: " + userSvc, e);
		}
		userSvc.setId( svcId );

		return userSvc;
	}

	/**
	 * Creates a user service object from the given user and service handle.
	 */
	protected static UserCloudService CreateUserService(User user, IServiceHandle shnd, String name)
	{
		UserCloudService userSvc = new UserCloudService();

		userSvc.setUserId( user.getId() );
		userSvc.setName( name );
		userSvc.setSubmissionDate( new Date() );
		userSvc.setStoppingDate( null );
		userSvc.setMiddlewareId( null ); //FIXME: where to get this value?
		userSvc.setSchedulerServiceId( shnd.getId() );
		userSvc.setStatus( ExecutionStatus.UNKNOWN );

		return userSvc;
	}

	/**
	 * Private class user for holding the singleton instance in a
	 * thread-safe manner.
	 */
	private static final class InstanceHolder
	{
		/** The singleton instance of the parent class. */
		private final static UserCloudServiceFacade instance = new UserCloudServiceFacade();
	}
}
