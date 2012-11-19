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
 * <p><strong>AbstractFragmentBean</strong> is the abstract base class for every
 * page bean associated with a JSP page fragment containing JavaServer Faces
 * components. It extends {@link AbstractFacesBean}, so it inherits all of the
 * default integration behavior found there.</p>
 *
 * <p>In addition to event handler methods that you create while building
 * your application, the runtime environment will also call the following
 * <em>lifecycle</em> related methods at appropriate points during the execution
 * of your application:</p>
 * <ul>
 * <li><strong>init()</strong> - Called whenever you navigate to a page
 * containing this page fragment, either directly (via a URL) or indirectly
 * via page navigation from a different page.  You can override this
 * method to acquire any resources that will always be needed by this
 * page fragment.</li>
 * <li><strong>destroy()</strong> - Called unconditionally if
 * <code>init()</code> was called, after completion of rendering by
 * whichever page was actually rendered.  Override this method to release
 * any resources allocated in the <code>init()</code> method,
 * or in an event handler.</li>
 * </ul>
 *
 * This is inspired to the Sun RAVE APPBASE project.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractFragmentBean extends AbstractFacesBean
{
	/**
	 * <p>Construct a new instance of this bean.</p>
	 */
	public AbstractFragmentBean()
	{
		// empty
	}

	/**
	 * <p>Callback method that is called whenever a page containing
	 * this page fragment is navigated to, either directly via a URL,
	 * or indirectly via page navigation.  Override this method to acquire
	 * resources that will be needed for event handlers and lifecycle methods.
	 * </p>
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	public void init()
	{
		// empty
	}


	/**
	 * <p>Callback method that is called after rendering is completed for
	 * this request, if <code>init()</code> was called.  Override this
	 * method to release resources acquired in the <code>init()</code>
	 * method (or acquired during execution of an event handler).</p>
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	public void destroy()
	{
		// empty
	}
}
