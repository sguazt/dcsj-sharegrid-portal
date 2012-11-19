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

import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * Bean class handling user sessions.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class SessionBean extends AbstractSessionBean implements Serializable
{
	private static final transient Logger Log = Logger.getLogger( SessionBean.class.getName() );

//	private String loggedUserName;
//	private int loggedUserId;

	///@{ Lifecycle methods ///////////////////////////////////////////////

	@Override
	public void init()
	{
		super.init();

		this.loggedIn = false;
		this.invalid = false;
	}

	@Override
	public void destroy()
	{
		super.destroy();

		try
		{
			this.invalidate();
		}
		catch (Exception e)
		{
			SessionBean.Log.log( Level.SEVERE, "Error while invalidating the session.", e );
			ViewUtil.AddExceptionMessage( e );
		}
	}

	///@} Lifecycle methods ///////////////////////////////////////////////

	///@{ Properties /////////////////////////////////////////////////////

	/** PROPERTY: userLoggedIn. */
	private boolean loggedIn;
	public boolean isUserLoggedIn() { return this.loggedIn; }
	public void setUserLoggedIn(boolean value) { this.loggedIn = value; }

	/** PROPERTY: userNotLoggedIn. */
	public boolean isUserNotLoggedIn() { return ! this.loggedIn; }

//	// PROPERY: loggedUserName
//	public String getLoggedUserName() { return this.loggedUserName; }
//	public void setLoggedUserName(String value) { this.loggedUserName = value; }
//
//	// PROPERTY: loggedUserId
//	public int getLoggedUserId() { return this.loggedUserId; }
//	public void setLoggedUserId(int value) { this.loggedUserId = value; }
//
	/** PROPERTY: user. */
	private User user;
	public User getUser() { return this.user; }
	public void setUser(User value)
	{
		this.user = value;

		try
		{
			Map<String,Object> sessionMap = null;
			sessionMap = this.getSessionMap();
			if ( sessionMap != null )
			{
				if ( value != null )
				{
					sessionMap.put( "USERID", value.getId() );
				}
				else
				{
					sessionMap.remove( "USERID" );
				}
			}
		}
		catch (Exception e)
		{
			//TODO
		}
	}
	@Deprecated
	public User getUserLogin() { return this.getUser(); }
	@Deprecated
	public void setUserLogin(User value) { this.setUser( value ); }

	/** PROPERTY: invalid. */
	private boolean invalid = false;
	public boolean isInvalid() { return this.invalid; }
	public void setInvalid(boolean value) { this.invalid = value; }

	///@} Properties /////////////////////////////////////////////////////

	public void reset()
	{
		if ( this.isInvalid() )
		{
			return;
		}

		// Saves user (if updated)
		//if ( this.getUser().isChanged() )
		if ( this.getUser() != null && this.getUser().isChanged() )
		{
			try
			{
				UserFacade.instance().setUser( this.getUser() );
			}
			catch (Exception e)
			{
				SessionBean.Log.log( Level.SEVERE, "Error while updating the user: " + this.getUserLogin().getNickname(), e );
				e.printStackTrace();
				ViewUtil.AddExceptionMessage( e );
			}
		}

		// Invalidates private state
		this.setUserLoggedIn( false );
		this.setUser( null );
		this.setInvalid( false );
	}

	/**
	 * Invalidates current session.
	 */
	public void invalidate() throws Exception
	{
		if ( this.isInvalid() )
		{
			return;
		}

		this.reset();
//		// Saves user (if updated)
//		//if ( this.getUser().isChanged() )
//		if ( this.getUser() != null && this.getUser().isChanged() )
//		{
//			try
//			{
//				UserFacade.instance().setUser( this.getUser() );
//			}
//			catch (Exception e)
//			{
//				SessionBean.Log.log( Level.SEVERE, "Error while updating the user: " + this.getUserLogin().getNickname(), e );
//				e.printStackTrace();
//				ViewUtil.AddExceptionMessage( e );
//			}
//		}
//
//		// Invalidates private state
//		this.setUserLoggedIn( false );
//		this.setUser( null );
		this.setInvalid( true );

		// Invalidates HttpSession session (if present)
		HttpSession session = null;
		session = ViewUtil.GetHttpSession();
		if ( session != null )
		{
			session.invalidate();
		}

		// Invalidates Faces session (if present)
		Map<String,Object> sessionMap = this.getSessionMap();
		if ( sessionMap != null )
		{
			sessionMap.clear();
		}
	}
}
