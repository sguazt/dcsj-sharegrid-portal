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

import java.io.Serializable;

/**
 * <p><strong>AbstractSessionBean</strong> is the abstract base class for
 * data bean(s) that are stored in session scope attributes.  It extends
 * {@link AbstractFacesBean}, so it inherits all of the default behavior
 * found there.  In addition, the following lifecycle methods are called
 * automatically when the corresponding events occur:</p>
 * <ul>
 * <li><code>init()</code> - Called when this bean is initially added
 *     as a session attribute (typically as the result of evaluating a
 *     value binding or method binding expression).</li>
 * <li><code>passivate()</code> - Called when the servlet container is about
 *     to serialize and remove this session from its current container.</li>
 * <li><code>activate()</code> - Called when the servlet container has
 *     finished deserializing this session and making it available in a
 *     (potentially different) container.</li>
 * <li><code>destroy()</code> - Called when the bean is removed from the
 *     session attributes (typically as a result of the session timing out
 *     or being terminated by the application).</li>
 * </ul>
 *
 * This is inspired to the Sun RAVE APPBASE project.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractSessionBean extends AbstractFacesBean implements Serializable
{
	/**
	 * <p>Create a new session scope bean.</p>
	 */
	public AbstractSessionBean()
	{      
		// empty
	}

	/**
	 * <p>This method is called when this bean is initially added to
	 * session scope.  Typically, this occurs as a result of evaluating
	 * a value binding or method binding expression, which utilizes the
	 * managed bean facility to instantiate this bean and store it into
	 * session scope.</p>
	 *
	 * <p>You may customize this method to initialize and cache data values
	 * or resources that are required for the lifetime of a particular
	 * user session.</p>
	 */
	public void init()
	{
		// empty
	}

	/**
	 * <p>This method is called when the session containing it is about to be
	 * passivated.  Typically, this occurs in a distributed servlet container
	 * when the session is about to be transferred to a different
	 * container instance, after which the <code>activate()</code> method
	 * will be called to indicate that the transfer is complete.</p>
	 *
	 * <p>You may customize this method to release references to session data
	 * or resources that can not be serialized with the session itself.</p>
	 */
	public void passivate()
	{
		// empty
	}

	/**
	 * <p>This method is called when the session containing it was
	 * reactivated.</p>
	 * 
	 * <p>You may customize this method to reacquire references to session
	 * data or resources that could not be serialized with the
	 * session itself.</p>
	 */
	public void activate()
	{
		// empty
	}

	/**
	 * <p>This method is called when this bean is removed from
	 * session scope.  Typically, this occurs as a result of
	 * the session timing out or being terminated by the application.</p>
	 *
	 * <p>You may customize this method to clean up resources allocated
	 * during the execution of the <code>init()</code> method, or
	 * at any later time during the lifetime of the application.</p>
	 */
	public void destroy()
	{
		// empty
	}
}
