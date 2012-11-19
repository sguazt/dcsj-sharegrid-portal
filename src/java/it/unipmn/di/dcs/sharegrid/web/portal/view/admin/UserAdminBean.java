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

package it.unipmn.di.dcs.sharegrid.web.portal.view.admin;

import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

/**
 * Page bean for administering the registered users.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UserAdminBean extends AbstractPageBean
{
	private static transient Logger Log = Logger.getLogger( UserAdminBean.class.getName() );

	private List<User> users;

	/** PROPERTY: userList. */
	public List<User> getUsers()
	{
		if ( this.users == null )
		{
			try
			{
				this.users = UserFacade.instance().users();
			}
			catch (Exception e)
			{
				UserAdminBean.Log.severe( "Unable to retrieve users: " + e.getMessage() );
				ViewUtil.AddExceptionMessage(e);
			}
		}

		return this.users;
	}
}
