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

package it.unipmn.di.dcs.sharegrid.web.faces.view;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Application exception class that wraps the one or more runtime
 * exceptions that were intercepted and cached during the execution of
 * a particular request's lifecycle.  Call the <code>getExceptions()</code>
 * method to retrieve the cached exception instances.</p>
 *
 * This is inspired to the Sun RAVE APPBASE project.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ApplicationException extends RuntimeException
{
	/**
	 * <p><code>List</code> of cached exceptions associated with this
	 * exception.</p>
	 */
	private List list = null;

	/**
	 * <p>Construct a new exception with no additional information.</p>
	 */
	public ApplicationException()
	{
		this(null, null, null);
	}

	/**
	 * <p>Construct a new exception with the specified detail message.</p>
	 *
	 * @param message Detail message for this exception
	 */
	public ApplicationException(String message)
	{
		this(message, null, null);
	}

	/**
	 * <p>Construct a new exception with the specified detail message and
	 * root cause.</p>
	 *
	 * @param message Detail message for this exception
	 * @param cause Root cause for this exception
	 */
	public ApplicationException(String message, Throwable cause)
	{
		this(message, cause, null);
	}

	/**
	 * <p>Construct a new exception with the specified root cause.</p>
	 *
	 * @param cause Root cause for this exception
	 */
	public ApplicationException(Throwable cause)
	{
		this(cause.getMessage(), cause, null);
	}

	/**
	 * <p>Construct a new exception with the specified root cause and
	 * list of cached exceptions.</p>
	 *
	 * @param cause Root cause for this exception
	 * @param list <code>List</code> of cached exceptions
	 */
	public ApplicationException(Throwable cause, List list)
	{
		this(cause.getMessage(), cause, list);
	}

	/**
	 * <p>Construct a new exception with the specified detail message,
	 * root cause, and list of cached exceptions.</p>
	 *
	 * @param message Detail message for this exception
	 * @param cause Root cause for this exception
	 * @param list <code>List</code> of cached exceptions
	 */
	public ApplicationException(String message, Throwable cause, List list)
	{
		super(message, cause);
		this.list = list;
	}

	/**
	 * <p>Return a <code>List</code> of the cached exceptions associated with
	 * this exception.  If no such exceptions were associated, return
	 * <code>null</code> instead.</p>
	 */
	public List getExceptions()
	{
		return this.list;
	}
}
