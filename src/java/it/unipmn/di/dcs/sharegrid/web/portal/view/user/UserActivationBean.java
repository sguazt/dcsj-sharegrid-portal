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

import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.MessageConstants;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;
import it.unipmn.di.dcs.sharegrid.web.service.IAuthenticationService;
import it.unipmn.di.dcs.sharegrid.web.service.ServiceFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;

/**
 * Page bean for handling user activation.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UserActivationBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( UserActivationBean.class.getName() );

	///@{ Lifecycle ////////////////////////////////////////////////////////

	@Override
	public void init()
	{
		super.init();

		if ( this.isActivated() )
		{
			return;
		}

//		ServletRequest req = (ServletRequest) this.getExternalContext().getRequest();
//		if ( req == null )
//		{
//			//TODO: throws an error
//		}
//
//		String activationKey = req.getParameter( "aid" );
//		String userId = req.getParameter( "uid" );

		boolean activated = false;

		String activationKey = this.getAid();
		String userId = this.getUid();

		if (
			!Strings.IsNullOrEmpty( activationKey )
			&& !Strings.IsNullOrEmpty( userId )
		) {
			try
			{
				if ( UserActivationBean.Log.isLoggable(Level.FINE) )
				{
					UserActivationBean.Log.fine( "Going to activate user '" + userId + "'." );
				}

				//User user = null;
				//user = UserFacade.instance().registerUser( this.regInfo );
				//if ( user == null )
				activated = ServiceFactory.instance().authenticationService().activate( Integer.parseInt( userId ), activationKey );
			}
			catch (Exception e)
			{
				this.setActivated( false );

				UserActivationBean.Log.severe( "Error while activating user '" + userId + "': " + e.toString() );

				ViewUtil.AddExceptionMessage(e);

				return;
			}
		}

		this.setActivated( activated );

		if ( activated )
		{
			ViewUtil.AddInfoMessage( MessageConstants.USER_ACTIVATION_OK );
		}
		else
		{
			ViewUtil.AddWarnMessage(
				MessageConstants.USER_ACTIVATION_KO,
				userId,
				activationKey
			);
		}
	}

	///@} Lifecycle ////////////////////////////////////////////////////////

	///@{ Properties ///////////////////////////////////////////////////////

	/** PROPERTY: activated. */
	public boolean activated = false;
	public void setActivated(boolean value) { this.activated = value; }
	public boolean isActivated() { return this.activated; }

	public String uid;
	public void setUid(String value) { this.uid = value; }
	public String getUid() { return this.uid; }

	public String aid;
	public void setAid(String value) { this.aid = value; }
	public String getAid() { return this.aid; }

	///@} Properties ///////////////////////////////////////////////////////
}
