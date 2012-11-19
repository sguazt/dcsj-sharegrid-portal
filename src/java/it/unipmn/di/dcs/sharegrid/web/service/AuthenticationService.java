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

import it.unipmn.di.dcs.common.conversion.Convert;
import it.unipmn.di.dcs.common.util.Pair;

import it.unipmn.di.dcs.sharegrid.web.model.AccountStatus;
import it.unipmn.di.dcs.sharegrid.web.model.Conf;
import it.unipmn.di.dcs.sharegrid.web.model.LoginInfo;
import it.unipmn.di.dcs.sharegrid.web.model.RegistrationInfo;
import it.unipmn.di.dcs.sharegrid.web.model.SecurityLevels;
//import it.unipmn.di.dcs.sharegrid.web.model.IMailSender;
//import it.unipmn.di.dcs.sharegrid.web.model.NotificationSenderFactory;
import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;

import java.io.File;
//import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A concrete implementation for authentication services.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class AuthenticationService implements IAuthenticationService
{
	private static final transient Logger Log = Logger.getLogger( AuthenticationService.class.getName() );

	protected AuthenticationService()
	{
		// empty
	}

	public static AuthenticationService instance()
	{
		return AuthenticationServiceHolder.instance;
	}

	//@{ IAuthenticationService implementation /////////////////////////////

	public boolean login(LoginInfo loginInfo)
	{
		return	this.loginLoad(loginInfo) != null
			? true
			: false;
	}

	public User loginLoad(LoginInfo loginInfo)
	{
		User user = null;

		try
		{
//			// Checks for user existance
//			if ( !UserFacade.instance().isUserRegistered( loginInfo ) )
//			{
//				AuthenticationService.Log.fine( "Unable to log ing: invalid login." );
//				return false;
//			}

			// Gets the user info
			user = UserFacade.instance().getUser( loginInfo );

			// Checks the password
			//if ( !loginInfo.getPassword().equals( user.getPassword() ) )
			if ( ! user.checkPassword( loginInfo.getPassword() ) )
			{
				AuthenticationService.Log.fine( "Unable to log ing: invalid password." );
				return null;
			}

			if ( user.getAccountStatus() == AccountStatus.FullyRegistered )
			{
				// Updates the last login date
				user.setLastLoginDate( new Date() );

				UserFacade.instance().setUser(user);
			}
			else
			{
				// only fully-registered user can log-in
				user = null;
			}
		}
		catch (Exception e)
		{
			if ( user == null )
			{
				AuthenticationService.Log.fine( "Unable to login: invalid login." );
			}
			else
			{
				AuthenticationService.Log.log( Level.SEVERE, "Unable to login.", e );
			}
		}

		return user;
	}

	public boolean logout(LoginInfo loginInfo)
	{
		return true;
	}

	public Pair<Integer,String> register(RegistrationInfo regInfo) throws ServiceException
	{
		User user = null;
		String key = null;

		if ( regInfo == null )
		{
			throw new ServiceException( "Informations about the registration not specified.");
		}

		try
		{
			// Register the user
			user = UserFacade.instance().registerUser( regInfo );

			key = this.generateActivationKey( user );

//			if ( user != null )
//			{
//				URL url = null;
//				url = new URL( Conf.instance().getWebUserActivationUrl() + "?aid=" + token + "&uid=" + user.getId() );
//
//				// Send a mail notification
//				IMailSender mailer = null;
//				mailer = NotificationSenderFactory.Instance().getMailSender();
//				mailer.send(
//					Conf.instance().getSmtpRegistrationSender(),
//					new String[] { user.getEmail() },
//					Conf.instance().getSmtpRegistrationSubject(),
//					MessageFormat.format(
//						Conf.instance().getSmtpRegistrationBody(),
//						url.toString()
//					)
//				);
//			}
		}
		catch (Exception e)
		{
			if ( user != null )
			{
				try
				{
					UserFacade.instance().unregisterUser(user);
				}
				catch (Exception ee)
				{
					// ignore
				}
			}

			throw new ServiceException( "Unable to register the user '" + regInfo.getNickname() + "'.", e);
		}

		return new Pair<Integer,String>( user.getId(), key );
	}

	public boolean activate(int userId, String key) throws ServiceException
	{
		try
		{
			return this.activate(
				UserFacade.instance().getUser( userId ),
				key
			);
		}
		catch (Exception e)
		{
			throw new ServiceException(e);
		}
	}

	public boolean activate(User user, String key) throws ServiceException
	{
		if ( user == null || key == null )
		{
			throw new ServiceException( "Informations about the activation not specified.");
		}

		if ( user.getAccountStatus() == AccountStatus.FullyRegistered )
		{
			// User registration has been already activated.
			return true;
		}

		boolean activated = false;

		try
		{
			if ( this.verifyActivationKey(user, key) )
			{
				// Creates user paths
				boolean pathsCreated = false;

				pathsCreated = UserFacade.instance().createUserPaths( user );
				if ( pathsCreated )
				{
					// Activate the user
					user.setAccountStatus( AccountStatus.FullyRegistered );
					user.setSecurityLevel( SecurityLevels.StandardUser );
					UserFacade.instance().setUser( user );

					activated = true;
				}
			}
		}
		catch (Exception e)
		{
			throw new ServiceException( "Unable to register the user '" + user.getNickname() + "'.", e);
		}

		return activated;
	}

	public void changePassword(User user, String oldPassword, String newPassword) throws ServiceException
	{
		if ( user.checkPassword( oldPassword ) )
		{
			user.setPlainPassword( newPassword );

			try
			{
				UserFacade.instance().setUser( user );
			}
			catch (Exception e)
			{
				throw new ServiceException("Error while changing the password for user '" + user.getId() + "'", e);
			}
		}
	}

	//@} IAuthenticationService implementation /////////////////////////////

	protected String generateActivationKey(User user) throws Exception
	{
		byte[] digest = null;

		String plainToken;

		plainToken =	Convert.BytesToBase64( Integer.toString( user.getId() ).getBytes() )
			+	"#"
			+	Convert.BytesToBase64( user.getNickname().getBytes() )
			+	"#"
			+	Convert.BytesToBase64( new SimpleDateFormat("yyyy-MM-dd HH:mm").format( user.getRegistrationDate() ).getBytes() );
//			+	"#"
//			+	new SimpleDateFormat("yyyy-MM-dd HH:mm").format( new Date( user.getRegistrationDate().getTime() + 10*24*60*60*1000 ) );

		MessageDigest digestAlg = MessageDigest.getInstance("MD5");

		digest = digestAlg.digest( plainToken.getBytes() );

		return Convert.BytesToBase64( digest );
	}

	protected boolean verifyActivationKey(User user, String key) throws Exception
	{
		String newKey = this.generateActivationKey(user);

		return newKey.equals( key );
	}

	private static final class AuthenticationServiceHolder
	{
		private final static AuthenticationService instance = new AuthenticationService();
	}
}
