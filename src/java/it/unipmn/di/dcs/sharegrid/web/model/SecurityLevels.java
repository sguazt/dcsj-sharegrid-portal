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

/**
 * User security levels enumeration.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public enum SecurityLevels
{
	AnonymousUser	(0),
	StandardUser	(1000),
	PowerUser	(2000),
	BetaTesterUser	(3000),
	DeveloperUser	(5000),
	AdminUser	(9000),
	Unknown		(-1);

	private int intValue;

	/** A constructor. */
	SecurityLevels(int value)
	{
		this.intValue = value;
	} 

	public int intValue()
	{
		return this.intValue;
	}

	public static SecurityLevels fromInt(int value)
	{
		if ( value >= AnonymousUser.intValue() && value < StandardUser.intValue() )
		{
			return AnonymousUser;
		}
		else if ( value >= StandardUser.intValue() && value < PowerUser.intValue() )
		{
			return StandardUser;
		}
		else if ( value >= PowerUser.intValue() && value < PowerUser.intValue() )
		{
			return PowerUser;
		}
		else if ( value >= BetaTesterUser.intValue() && value < DeveloperUser.intValue() )
		{
			return BetaTesterUser;
		}
		else if ( value >= DeveloperUser.intValue() && value < AdminUser.intValue() )
		{
			return DeveloperUser;
		}
		else if ( value >= AdminUser.intValue() )
		{
			return AdminUser;
		}
		return Unknown;
	}
}
