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

import it.unipmn.di.dcs.common.util.IClonable;
import it.unipmn.di.dcs.common.util.Strings;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Represents a user account.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class User implements Serializable, IClonable<User>
{
	private static final long serialVersionUID = 2L;

	private transient boolean changed; // Tells if some property of this class has changed.
	private int id;
	private String name;
	private String nickname;
	private String email;
	private String password;
	private String language;
	private int tzoffset;
	private SecurityLevels secLevel;
	private int effSecLevel;
	private AccountStatus accStatus;
	private Date regDate;
	private Date lastLoginDate;

	private transient String prettyLang;
	private transient IPasswordObfuscator passObf = null;

/*
	public User(int id, String nickname)
	{
		this.id = id;
		this.nickname = nickname;
		this.name = nickname;
		this.secLevel = 9999;
	}
*/

	/** A constructor. */
	public User()
	{
		this.passObf = Conf.instance().getUserPasswordObfuscator();
		this.setEffectiveSecurityLevel( SecurityLevels.AnonymousUser.intValue() );
		this.accStatus = AccountStatus.Unregistered;
	}

	public boolean isChanged()
	{
		return this.changed;
	}

	public void setChanged(boolean value)
	{
		this.changed = value;
	}

	public void setId(int value)
	{
		this.id = value;
	}

	public int getId()
	{
		return this.id;
	}

	public void setName(String value)
	{
		this.name = value;
	}

	public String getName()
	{
		return this.name;
	}

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

	public void setPlainPassword(String value)
	{
		try
		{
			this.password = this.passObf.obfuscate(value);
		}
		catch (Exception e)
		{
			// don't set any password
		}
	}

	public void setPassword(String value)
	{
		this.password = value;
	}

	public String getPassword()
	{
		return this.password;
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

	public void setAccountStatus(AccountStatus value)
	{
		this.accStatus = value;
	}

	public AccountStatus getAccountStatus()
	{
		return this.accStatus;
	}

	public void setEffectiveSecurityLevel(int value)
	{
		this.secLevel = SecurityLevels.fromInt( value );
		this.effSecLevel = value;
	}

	public int getEffectiveSecurityLevel()
	{
		return this.effSecLevel;
	}

	public void setSecurityLevel(SecurityLevels value)
	{
		this.secLevel = value;
		this.effSecLevel = value.intValue();
	}

	public SecurityLevels getSecurityLevel()
	{
		return this.secLevel;
	}

	public void setRegistrationDate(Date value)
	{
		this.regDate = value;
	}

	public Date getRegistrationDate()
	{
		return this.regDate;
	}

	public void setLastLoginDate(Date value)
	{
		this.lastLoginDate = value;
	}

	public Date getLastLoginDate()
	{
		return this.lastLoginDate;
	}

	/**
	 * Returns {@code true} if the given password match with the current
	 * `one; {@code otherwise}.
	 */
	public boolean checkPassword(String password)
	{
		try
		{
			return this.password.equals(
				this.passObf.obfuscate( password )
			);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	@Override
	public String toString()
	{
		return  "<ID: " + this.getId()
			+ ",Nickname: " + this.getNickname()
			+ ",Name: " + this.getName()
			+ ",Language: " + this.getLanguage()
			+ ",SecLevel: " + this.getSecurityLevel()
			+ ",AccountStatus: " + this.getAccountStatus()
			+ ">";
	}

	//@{ IClonable implementation //////////////////////////////////////////

	public User clone()
	{
		User u = new User();

		u.setId( this.getId() );
		u.setName( this.getName() );
		u.setNickname( this.getNickname() );
		u.setEmail( this.getEmail() );
		u.setPassword( this.getPassword() );
		u.setLanguage( this.getLanguage() );
		u.setTimeZoneOffset( this.getTimeZoneOffset() );
		u.setSecurityLevel( this.getSecurityLevel() );
		u.setAccountStatus( this.getAccountStatus() );
		u.setRegistrationDate( this.getRegistrationDate() );
		u.setLastLoginDate( this.getLastLoginDate() );

		return u;
	}

	//@} IClonable implementation //////////////////////////////////////////
}
