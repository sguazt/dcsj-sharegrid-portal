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

import it.unipmn.di.dcs.common.util.Strings;

import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Represents a subscriber information.
 *
 * This information is entered by the user who want to register to the portal.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class RegistrationInfo extends LoginInfo implements Serializable
{
	private static final long serialVersionUID = 2L;

	/** The name of the subscriber. */
	private String name;
	/** The password verification of the subscriber. */
	private String password2;
	/** The language of the subscriber. */
	private String language;
	/** The formatted version of the language of the subscriber. */
	private transient String prettyLang;
	/** The timezone offset of the subscriber. */
	private int tzoffset;

	public void setName(String value)
	{
		this.name = value;
	}

	public String getName()
	{
		return this.name;
	}

	public void setPassword2(String value)
	{
		this.password2 = value;
	}

	public String getPassword2()
	{
		return this.password2;
	}

	public String getLanguage()
	{
		return this.language;
	}

	public void setLanguage(String value)
	{
		this.language = value;

                if ( !Strings.IsNullOrEmpty(this.language) )
                {
                        this.setPrettyLanguage( new Locale(this.language).getDisplayName() );
                }
	}

	public String getPrettyLanguage()
	{
		return this.prettyLang;
	}

	public void setPrettyLanguage(String value)
	{
		this.prettyLang = value;
	}

	public int getTimeZoneOffset()
	{
		return this.tzoffset;
	}

	public void setTimeZoneOffset(int value)
	{
		this.tzoffset = value;
	}

	public TimeZone getTimeZone()
	{
		return TimeZone.getTimeZone( "GMT" + ( this.tzoffset > 0 ? "+" : "" ) + Integer.toString(this.tzoffset) );
	}

	@Override
	public String toString()
	{
		return	"<Nickname: " + this.getNickname()
			+ ",Name: " + this.getName()
			+ ",Language: " + this.getLanguage()
			+ ",TZ: GMT+" + this.getTimeZoneOffset()
			+ ">";
	}
}
