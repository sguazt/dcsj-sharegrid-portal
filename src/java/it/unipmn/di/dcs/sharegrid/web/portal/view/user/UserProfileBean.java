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

package it.unipmn.di.dcs.sharegrid.web.portal.view.user;

import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.MessageConstants;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Page bean for viewing and editing the current session user profile.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UserProfileBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( UserProfileBean.class.getName() );

	//@{ Lifecycle /////////////////////////////////////////////////////////

	@Override
	public void init()
	{
		super.init();

		// Make a temporary copy of current session User and work on it.
		this.setUser( this.getSessionBean().getUser() );
	}

	//@} Lifecycle /////////////////////////////////////////////////////////

	//@{ Properties ////////////////////////////////////////////////////////

	/**
	 * PROPERTY: user.
	 *
	 * A working copy of current (i.e. session) user object.
	 */
	private User user;
	public User getUser() { return this.user; }
	public void setUser(User value) { this.user = value; }

	/** PROPERTY: nicknameMinLen. */
	public int getNicknameMinLen() { return 1; }

	/** PROPERTY: nicknameMaxLen. */
	public int getNicknameMaxLen() { return 50; }

	/** PROPERTY: nameMinLen. */
	public int getNameMinLen() { return 0; }

	/** PROPERTY: nameMaxLen. */
	public int getNameMaxLen() { return 50; }

	/** PROPERTY: emailMinLen. */
	public int getEmailMinLen() { return 3; }

	/** PROPERTY: emailMaxLen. */
	public int getEmailMaxLen() { return 255; }

	//@} Properties ////////////////////////////////////////////////////////

	//@{ Actions ///////////////////////////////////////////////////////////

	/** Action for saving the user profle. */
	public String doSaveAction()
	{
		try
		{
			// Saves the user on the database
			UserFacade.instance().setUser( this.getUser() );

			// Update the session object
			// Note: user is not marked as 'changed' since we've
			// just saved it in the line above.
			this.getSessionBean().setUser( this.getUser() );
		}
		catch (Exception e)
		{
			UserProfileBean.Log.log( Level.SEVERE, "Error while updating the profile for user '" + this.getSessionBean().getUser().getId() + "'.", e );
			ViewUtil.AddExceptionMessage( e );
			ViewUtil.AddInfoMessage( MessageConstants.USER_SAVE_KO, this.getSessionBean().getUser().getNickname() );

			return "save-ko";
		}

		ViewUtil.AddInfoMessage( MessageConstants.USER_SAVE_OK, this.getSessionBean().getUser().getNickname() );

		return "save-ok";
	}

	//@} Actions ///////////////////////////////////////////////////////////
}
