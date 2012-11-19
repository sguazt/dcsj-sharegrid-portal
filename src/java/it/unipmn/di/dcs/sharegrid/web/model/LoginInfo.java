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

import java.io.Serializable;

/**
 * Represents a user login information.
 *
 * This information is entered by the user who want to sign in to the portal.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class LoginInfo implements Serializable
{
	private static final long serialVersionUID = 2L;

	private String nickname;
	private String email;
	private String password;
	//private transient IPasswordObfuscator passObf = null;

	//public LoginInfo()
	//{
	//	this.passObf = Conf.instance().getUserPasswordObfuscator();
	//}

	public void setNickname(String value)
	{
		this.nickname = value;
	}

	public String getNickname()
	{
		return this.nickname;
	}

	public void setEmail(String value)
	{
		this.email = value;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setPassword(String value)
	{
		this.password = value;
//		try
//		{
//			this.password = this.passObf.obfuscate(value);
//		}
//		catch (Exception e)
//		{
//			// don't set any password
//		}
	}

	public String getPassword()
	{
		return this.password;
	}

//	/**
//	 * Returns {@code true} if the given password match with the current
//	 * `one; {@code otherwise}.
//	 */
//	public boolean checkPassword(String password)
//	{
//		return this.password.equals( password );
//	}
}
