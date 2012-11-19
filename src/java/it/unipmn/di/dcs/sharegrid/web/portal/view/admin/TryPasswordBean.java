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

import it.unipmn.di.dcs.sharegrid.web.model.Conf;
import it.unipmn.di.dcs.sharegrid.web.model.IPasswordObfuscator;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Page bean for viewing a password.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class TryPasswordBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( TryPasswordBean.class.getName() );

	//@{ Properties ////////////////////////////////////////////////////////

	private String plainPassword;
	public String getPlainPassword() { return this.plainPassword; }
	public void setPlainPassword(String value) { this.plainPassword = value; }

	private String obfPassword;
	public String getObfuscatedPassword() { return this.obfPassword; }
	public void setObfuscatedPassword(String value) { this.obfPassword = value; }

	//@} Properties ////////////////////////////////////////////////////////

	//@{ Actions ///////////////////////////////////////////////////////////

	/** Action for obfuscating the password. */
	public String doObfuscateAction()
	{
		try
		{
			IPasswordObfuscator passObf = null;
			passObf = Conf.instance().getUserPasswordObfuscator();
			this.setObfuscatedPassword(
				passObf.obfuscate( this.getPlainPassword() )
			);
		}
		catch (Exception e)
		{
			TryPasswordBean.Log.log( Level.SEVERE, "Error while obfuscating the password '" + this.getPlainPassword() + "'.", e );
			ViewUtil.AddExceptionMessage( e );

			return "obfuscate-ko";
		}

		return "obfuscate-ok";
	}

	//@} Actions ///////////////////////////////////////////////////////////
}
