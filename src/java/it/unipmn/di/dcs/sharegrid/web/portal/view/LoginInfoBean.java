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

package it.unipmn.di.dcs.sharegrid.web.portal.view;

import it.unipmn.di.dcs.sharegrid.web.service.IAuthenticationService;
import it.unipmn.di.dcs.sharegrid.web.service.ServiceFactory;
import it.unipmn.di.dcs.sharegrid.web.model.LoginInfo;
import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.MessageConstants;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

//import java.util.ResourceBundle;

/**
 * Bean class for user login.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class LoginInfoBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( LoginInfoBean.class.getName() );

	private LoginInfo loginInfo = new LoginInfo();
//	private boolean loggedIn = false;

//	/** PROPERTY: loginInfo. */
//	private LoginInfo getLoginInfo() { return this.loginInfo; }

	/** PROPERTY: nickname. */
	public String getNickname() { return this.loginInfo.getNickname(); }
	public void setNickname(String value) { this.loginInfo.setNickname( value ); }

	/** PROPERTY: password. */
	public String getPassword() { return this.loginInfo.getPassword(); }
	public void setPassword(String value) { this.loginInfo.setPassword( value ); }

//	/** PROPERTY: loggedIn. */
//	public boolean isLoggedIn() { return this.loggedIn; }

	/** PROPERTY: nicknameMinLen. */
	public int getNicknameMinLen() { return 1; }

	/** PROPERTY: nicknameMaxLen. */
	public int getNicknameMaxLen() { return 50; }

	/** PROPERTY: passwordMinLen. */
	public int getPasswordMinLen() { return 8; }

	/** PROPERTY: passwordMaxLen. */
	public int getPasswordMaxLen() { return 100; }

	public String loginAction()
	{
		User user = null;

		try
		{
			user = ServiceFactory.instance().authenticationService().loginLoad( this.loginInfo );
			//if ( !ServiceFactory.instance().authenticationService().login( this.loginInfo ) )
			if ( user == null )
			{
				if ( LoginInfoBean.Log.isLoggable(Level.FINE) )
				{
					LoginInfoBean.Log.fine( "Unable to log in: invalid login info." );
				}
				ViewUtil.AddErrorMessage( MessageConstants.INVALID_LOGIN );
				return "login-ko";
			}
		}
		catch (Exception e)
		{
			LoginInfoBean.Log.log( Level.SEVERE, "Unable to login.", e );

			ViewUtil.AddExceptionMessage(e);
			return "login-ko";
		}

		// Updates session
		//SessionBean session = ViewUtil.GetSessionBean();
		SessionBean session = this.getSessionBean();
		if ( session == null )
		{
			LoginInfoBean.Log.warning( "Unable to log in: invalid session object." );
			ViewUtil.AddErrorMessage( MessageConstants.INVALID_SESSION );
			return "login-ko";
		}
		else
		{
			try
			{
				// Makes sure to invalidate current session
				session.reset();
			}
			catch (Exception e)
			{
				// ignore

				LoginInfoBean.Log.log( Level.WARNING, "Exception caught while invalidating session for login (ignored).", e );
			}
		}

		session.setUserLoggedIn( true );
		session.setUser( user );
		//session.setInvalid( false );

		// Update the language
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		viewRoot.setLocale(
			new Locale( user.getLanguage() )
		);

		return "login-ok";
	}

	public String logoutAction()
	{
		try
		{
			if ( !ServiceFactory.instance().authenticationService().logout( this.loginInfo ) )
			{
				LoginInfoBean.Log.fine( "Unable to log out: invalid login info." );
				ViewUtil.AddErrorMessage( MessageConstants.INVALID_LOGIN );
				return "logout-ko";
			}

			// Invalidates the session
			SessionBean session = this.getSessionBean();
			if ( session != null )
			{
				session.invalidate();
			}
		}
		catch (Exception e)
		{
			LoginInfoBean.Log.log( Level.SEVERE, "Error while logging out.", e );
			ViewUtil.AddExceptionMessage(e);
			return "logout-ko";
		}

//		this.loggedIn = false;

		return "logout-ok";
	}
}
