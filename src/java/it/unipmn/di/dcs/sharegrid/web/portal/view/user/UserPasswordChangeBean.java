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
import it.unipmn.di.dcs.sharegrid.web.service.ServiceFactory;
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
public class UserPasswordChangeBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( UserPasswordChangeBean.class.getName() );

	//@{ Properties ////////////////////////////////////////////////////////

	private String oldPassword;
	public String getOldPassword() { return this.oldPassword; }
	public void setOldPassword(String value) { this.oldPassword = value; }

	private String newPassword;
	public String getNewPassword() { return this.newPassword; }
	public void setNewPassword(String value) { this.newPassword = value; }

	private String newPasswordCheck;
	public String getNewPasswordCheck() { return this.newPasswordCheck; }
	public void setNewPasswordCheck(String value) { this.newPasswordCheck = value; }

	/** PROPERTY: passwordMinLen. */
	public int getPasswordMinLen() { return 1; }

	/** PROPERTY: passwordMaxLen. */
	public int getPasswordMaxLen() { return 50; }

	//@} Properties ////////////////////////////////////////////////////////

	//@{ Actions ///////////////////////////////////////////////////////////

	/** Action for saving the user password. */
	public String doSaveAction()
	{
		if ( ! this.getNewPassword().equals( this.getNewPasswordCheck() ) )
		{
			ViewUtil.AddInfoMessage( MessageConstants.USER_PASSWORD_SAVE_KO, this.getSessionBean().getUser().getNickname() );
			return "save-ko";
		}

		try
		{
			ServiceFactory.instance().authenticationService().changePassword(
				this.getSessionBean().getUser(),
				this.getOldPassword(),
				this.getNewPassword()
			);
		}
		catch (Exception e)
		{
			UserPasswordChangeBean.Log.log( Level.SEVERE, "Error while changing the password for user '" + this.getSessionBean().getUser().getId() + "'.", e );
			ViewUtil.AddExceptionMessage( e );
			ViewUtil.AddInfoMessage( MessageConstants.USER_PASSWORD_SAVE_KO, this.getSessionBean().getUser().getNickname() );

			return "save-ko";
		}

		ViewUtil.AddInfoMessage( MessageConstants.USER_PASSWORD_SAVE_OK, this.getSessionBean().getUser().getNickname() );

		return "save-ok";
	}

	//@} Actions ///////////////////////////////////////////////////////////
}
