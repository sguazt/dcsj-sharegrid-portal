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

/**
 * <p><strong>AbstractRequestBean</strong> is the abstract base class for
 * data bean(s) that are stored in request scope attributes.
 *
 * It extends {@link AbstractFacesBean}, so it inherits all of the default behavior
 * found there.  In addition, the following lifecycle methods are called
 * automatically when the corresponding events occur:</p>
 * <ul>
 * <li><code>init()</code> - Called when this bean is initially added as a
 * request scope attribute (typically as the result of
 * evaluating a value binding or method binding expression).</li>
 * <li><code>destroy()</code> - Called when the bean is removed from the
 * request attributes (typically as a result of the application
 * being shut down by the servlet container).</li>
 * </ul>
 *
 * This is inspired to the Sun RAVE APPBASE project.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractRequestBean extends AbstractFacesBean
{
	/**
	 * <p>Create a new request scope bean.</p>
	 */
	public AbstractRequestBean()
	{      
		// empty
	}


	/**
	 * <p>This method is called when this bean is initially added to
	 * request scope.  Typically, this occurs as a result of evaluating
	 * a value binding or method binding expression, which utilizes the
	 * managed bean facility to instantiate this bean and store it into
	 * request scope.</p>
	 *
	 * <p>You may customize this method to allocate resources that are required
	 * for the lifetime of the current request.</p>
	 */
	public void init()
	{
		// empty
	}


	/**
	 * <p>This method is called when this bean is removed from
	 * request scope.  This occurs automatically when the corresponding
	 * HTTP response has been completed and sent to the client.</p>
	 *
	 * <p>You may customize this method to clean up resources allocated
	 * during the execution of the <code>init()</code> method, or
	 * at any later time during the lifetime of the request.</p>
	 */
	public void destroy()
	{
		// empty
	}
}
