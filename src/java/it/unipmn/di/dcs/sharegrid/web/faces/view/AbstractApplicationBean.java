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

import java.util.Locale;

/**
 * <p><strong>AbstractApplicationBean</strong> is the abstract base class for
 * data bean(s) that are stored in application scope attributes.  It extends
 * {@link AbstractFacesBean}, so it inherits all of the default behavior
 * found there.  In addition, the following lifecycle methods are called
 * automatically when the corresponding events occur:</p>
 * <ul>
 * <li><code>init()</code> - Called when this bean is initially added as an
 *     application scope attribute (typically as the result of
 *     evaluating a value binding or method binding expression).</li>
 * <li><code>destroy()</code> - Called when the bean is removed from the
 *     application attributes (typically as a result of the application
 *     being shut down by the servlet container).</li>
 * </ul>
 *
 * This is inspired to the Sun RAVE APPBASE project.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractApplicationBean extends AbstractFacesBean
{
	/**
	 * <p>Create a new application scope bean.</p>
	 */
	public AbstractApplicationBean()
	{
		// empty
	}

	/**
	 * <p>Mapping from the String version of the <code>Locale</code> for
	 * this response to the corresponding character encoding.  For each
	 * row, the first String is the value returned by the toString() method
	 * for the java.util.Locale for the current view, and the second
	 * String is the name of the character encoding to be used.</p>
	 *
	 * <p>Only locales that use an encoding other than the default (UTF-8)
	 * need to be listed here.</p>
	 */
	protected String encoding[][] = {
		{ "zh_CN", "GB2312" }, // NOI18N
	};

	/**
	 * <p>Return an appropriate character encoding based on the
	 * <code>Locale</code> defined for the current JavaServer Faces
	 * view.  If no more suitable encoding can be found, return
	 * "UTF-8" as a general purpose default.</p>
	 *
	 * <p>This method makes a convenient value binding target for the
	 * <code>value</code> property of a <em>Set Encoding</em> component.
	 * Applications that wish to specialize this behavior can override
	 * this method in their own application bean class.</p>
	 */
	public String getLocaleCharacterEncoding()
	{
		// Return the appropriate character encoding for this locale (if any)
		Locale locale = getFacesContext().getViewRoot().getLocale();
		if (locale == null)
		{
			locale = Locale.getDefault();
		}
		String match = locale.toString();
		for (int i = 0; i < encoding.length; i++)
		{
			if (match.equals(encoding[i][0]))
			{
				return encoding[i][1];
			}
		}

		// Return the default encoding value
		return "UTF-8"; // NOI18N
	}

	/**
	 * <p>This method is called when this bean is initially added to
	 * application scope.  Typically, this occurs as a result of evaluating
	 * a value binding or method binding expression, which utilizes the
	 * managed bean facility to instantiate this bean and store it into
	 * application scope.</p>
	 *
	 * <p>You may customize this method to initialize and cache application wide
	 * data values (such as the lists of valid options for dropdown list
	 * components), or to allocate resources that are required for the
	 * lifetime of the application.</p>
	 */
	public void init()
	{
		// empty
	}

	/**
	 * <p>This method is called when this bean is removed from
	 * application scope.  Typically, this occurs as a result of
	 * the application being shut down by its owning container.</p>
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
