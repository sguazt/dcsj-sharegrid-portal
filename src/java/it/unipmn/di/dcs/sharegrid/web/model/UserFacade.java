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

//import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
//mport it.unipmn.di.dcs.grid.core.middleware.sched.IJobHandle;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ease the user management.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public final class UserFacade
{
	private static Logger Log = Logger.getLogger( UserFacade.class.getName() );
	private IUserDAO userDao;

	private UserFacade()
	{
		try
		{
			this.setUserDAO( Conf.instance().getDAOFactory().getUserDAO() );
		}
		catch (Exception e)
		{
			UserFacade.Log.severe( "Unable to retrieve UserDAO from configuration: " + e.toString() );

			this.setUserDAO( null );
		}
	}

	private UserFacade(IUserDAO userDao)
	{
		this.setUserDAO( userDao );
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static UserFacade instance()
	{
		return UserFacadeHolder.instance;
	}

	/**
	 * Registers a new user.
	 *
	 * @return The new registered user.
	 */
	public User registerUser(RegistrationInfo info) throws ModelException
	{
		// preconditions
		assert( info != null );
		if ( info == null )
		{
			throw new ModelException("Registration info not specified.");
		}

		// Checks for an already existing user
		try
		{
			if ( this.getUserDAO().exists( info ) )
			{
				throw new ModelException("User already existent.");
			}
		}
		catch (ModelException me)
		{
			throw me;
		}
		catch (Exception e)
		{
			throw new ModelException("Unable to check for user existence for user " + info, e);
		}

		// Creates a user object
		User user = UserFacade.CreateUser( info );
		if ( user == null )
		{
			throw new ModelException("Unexpected error while creating the user: " + info);
		}

		// Adds the user object to the data-store
		int uid;
		try
		{
			uid = this.getUserDAO().insert( user );
		}
		catch (Exception e)
		{
			throw new ModelException("Unable to add the user: " + user, e);
		}
		user.setId( uid );

		return user;
	}

	/**
	 * Unregisters the given user.
	 */
	public void unregisterUser(User user) throws Exception
	{
		if ( !this.getUserDAO().exists( user.getId() ) )
		{
			throw new ModelException( "User does not exist." );
		}

		this.getUserDAO().remove( user.getId() );
	}

	/**
	 * Unregisters the user with the given user-id.
	 */
	public void unregisterUser(int userId) throws ModelException
	{
		try
		{
			if ( !this.getUserDAO().exists( userId ) )
			{
				throw new ModelException( "User does not exist." );
			}

			this.getUserDAO().remove( userId );
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
	 * Returns the user associated to the given user-id.
	 */
	public User getUser(int userId) throws ModelException
	{
		try
		{
			return this.getUserDAO().load( userId );
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Returns the user associated to the given user login informations.
	 */
	public User getUser(LoginInfo login) throws ModelException
	{
		try
		{
			return this.getUserDAO().load( login );
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Set a new state for the given user.
	 */
	public void setUser(User user) throws ModelException
	{
		try
		{
			this.getUserDAO().save( user );
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Returns <code>true</code> if the user associated to the given user-id
	 * is already registered; <code>false</code> otherwise.
	 */
	public boolean isUserRegistered(LoginInfo login) throws ModelException
	{
		try
		{
			return this.getUserDAO().exists( login );
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Returns <code>true</code> if the given user is already registered;
	 * <code>false</code> otherwise.
	 */
	@Deprecated
	public boolean validateUser(LoginInfo login) throws ModelException
	{
		try
		{
			if ( this.getUserDAO().exists( login ) )
			{
				return true;
			}
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}

		return false;
	}

	/**
	 * Returns the list of all users.
	 */
	public List<User> users() throws ModelException
	{
		try
		{
			return this.getUserDAO().list();
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	/**
	 * Returns the list of the users associated to the given identifiers.
	 */
	public List<User> users(Integer[] ids) throws ModelException
	{
		try
		{
			return this.getUserDAO().listFromId(ids);
		}
		catch (Exception e)
		{
			throw new ModelException(e);
		}
	}

	public boolean createUserPaths(User user)
	{
		return ( this.getUserBasePath(user, true ) != null );
	}

	public String getUserBasePath(User user, boolean create)
	{
		String path = null;

		path =	Conf.instance().getUserBasePath()
		+	File.separator
		+	"user."
		+	user.getId();

		if ( create )
		{
			File f = new File( path );
/*
			if ( !f.exists() && !f.mkdirs() )
			{
				path = null;
			}
*/
			if ( !f.exists() )
			{
				if ( f.mkdirs() )
				{
					boolean writable;
					writable = f.setWritable( true, false );
				}
				else
				{
					path = null;
				}
			}
		}

		return path;
	}

	public String getUserTmpPath(User user, boolean create)
	{
		String path = null;

		path = this.getUserBasePath(user, create);
		if ( path != null )
		{
			path =	path
			+	File.separator
			+	"tmp";

			if ( create )
			{
				File f = new File( path );
//				if ( !f.exists() && !f.mkdirs() )
//				{
//					path = null;
//				}
				if ( !f.exists() )
				{
					if ( f.mkdirs() )
					{
						boolean writable;
						writable = f.setWritable( true, false );
					}
					else
					{
						path = null;
					}
				}
			}
		}

		return path;
	}

	public String getUserGridBaseLocalPath(User user, String jobName, boolean create)
	{
		String path = null;

		path = this.getUserBasePath(user, create);
		if ( path != null )
		{
			path =	path
			+	File.separator
			+	jobName;

			if ( create )
			{
				File f = new File( path );
//				if ( !f.exists() && !f.mkdirs() )
//				{
//					path = null;
//				}
				if ( !f.exists() )
				{
					if ( f.mkdirs() )
					{
						boolean writable;
						writable = f.setWritable( true, false );
					}
					else
					{
						path = null;
					}
				}
			}
		}

		return path;
	}

	public String getUserGridStageInLocalPath(User user, String jobName, boolean create)
	{
		String path = null;

		path =	this.getUserGridBaseLocalPath(user, jobName, create);
		if ( path != null )
		{
			path =	path
			+	File.separator
			+	"in";

			if ( create )
			{
				File f = new File( path );
//				if ( !f.exists() && !f.mkdirs() )
//				{
//					path = null;
//				}
				if ( !f.exists() )
				{
					if ( f.mkdirs() )
					{
						boolean writable;
						writable = f.setWritable( true, false );
					}
					else
					{
						path = null;
					}
				}
			}
		}

		return path;
	}

	public String getUserGridStageOutLocalPath(User user, String jobName, boolean create)
	{
		String path = null;

		path =	this.getUserGridBaseLocalPath(user, jobName, create);
		if ( path != null )
		{
			path =	path
			+	File.separator
			+	"out";

			if ( create )
			{
				File f = new File( path );
//				if ( !f.exists() && !f.mkdirs() )
//				{
//					path = null;
//				}
				if ( !f.exists() )
				{
					if ( f.mkdirs() )
					{
						boolean writable;
						writable = f.setWritable( true, false );
					}
					else
					{
						path = null;
					}
				}
			}
		}

		return path;
	}

	/**
	 * Sets the store for the users.
	 */
	protected void setUserDAO(IUserDAO value)
	{
		this.userDao = value;
	}

	/**
	 * Returns the store for the users.
	 */
	protected IUserDAO getUserDAO()
	{
		return this.userDao;
	}

	protected static User CreateUser(RegistrationInfo info)
	{
		User user = new User();

		user.setNickname( info.getNickname() );
		user.setName( info.getName() );
		user.setEmail( info.getEmail() );
		user.setPlainPassword( info.getPassword() );
		user.setSecurityLevel( SecurityLevels.StandardUser );
		user.setAccountStatus( AccountStatus.PartiallyRegistered );
		user.setLanguage( info.getLanguage() );
		user.setTimeZoneOffset( info.getTimeZoneOffset() );

		return user;
	}

	private static class UserFacadeHolder
	{
		private final static UserFacade instance = new UserFacade();
	}
}
