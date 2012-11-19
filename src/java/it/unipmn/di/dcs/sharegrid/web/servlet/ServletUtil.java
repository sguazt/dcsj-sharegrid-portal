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

package it.unipmn.di.dcs.sharegrid.web.servlet;

import java.util.Locale;

/**
 * Utility class for servlet.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public final class ServletUtil
{
	/** Parse a string looking for size information. */
	public static long ParseSizeLong(String s) throws Exception
	{
		// precondition
		assert( s != null );

		if (s != null)
		{
			String number = s.toLowerCase(Locale.ENGLISH);
			long factor = 1;
			if ( number.endsWith("g") )
			{
				// Gigabytes
				factor = ServletConstants.OneGigaByte;
				number = number.substring(0, number.length() - 1);
			}
			else if ( number.endsWith("m") )
			{
				// Megabytes
				factor = ServletConstants.OneMegaByte;
				number = number.substring(0, number.length() - 1);
			}
			else if ( number.endsWith("k") )
			{
				// Kilobytes
				factor = ServletConstants.OneMegaByte;
				number = number.substring(0, number.length() - 1);
			}
			try
			{
				// bytes
				return Long.parseLong(number.trim()) * factor;
			}
			catch (NumberFormatException nbe)
			{
				//MultipartRequestWrapper.Log.warning("Given size for " + MultipartRequestWrapper.class.getName() + " " + s + " couldn't be parsed to a number (" + nbe + "). Fallback to default value.");
			}
		}

		throw new Exception( "Unable to retrieve size information from string: '" + s + "'" );
	}

	/** Parse a string looking for size information. */
	public static int ParseSizeInt(String s) throws Exception
	{
		// precondition
		assert( s != null );

		long size;

		size = ServletUtil.ParseSizeLong(s);
		if ( size > Integer.MAX_VALUE )
		{
			throw new Exception( "Size information in string: '" + s + "' exceeds the maximum allowed." );
		}

		return (int) size;
	}
}
