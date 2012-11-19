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
 * User account status enumeration.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public enum AccountStatus
{
	/** Unknown account status. */
	Unknown			(-1),
	/** Unregistered account status. */
	Unregistered		(0),
	/** Partially registered account status (waiting for activation). */
	PartiallyRegistered	(1),
	/** Registered and activated account status. */
	FullyRegistered		(2);

	/** Holds the account status value. */
	private int intValue;

	/** A constructor. */
	AccountStatus(int value)
	{
		this.intValue = value;
	}

	/** Return the integer value for this account status. */
	public int intValue()
	{
		return this.intValue;
	}

	/** Return the account status from an integer value. */
	public static AccountStatus fromInt(int value)
	{
		if ( value == Unregistered.intValue() )
		{
			return Unregistered;
		}
		else if ( value == PartiallyRegistered.intValue() )
		{
			return PartiallyRegistered;
		}
		else if ( value == FullyRegistered.intValue() )
		{
			return FullyRegistered;
		}
		else
		{
			return Unknown;
		}
	}
}
