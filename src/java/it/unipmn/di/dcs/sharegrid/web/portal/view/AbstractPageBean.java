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

package it.unipmn.di.dcs.sharegrid.web.portal.view;

import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewConstants;

import java.util.Locale;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletResponse;

/**
 * Base class for page beans.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractPageBean extends it.unipmn.di.dcs.sharegrid.web.faces.view.AbstractPageBean
{
	@Override
	public void init()
	{
		super.init();

		// Set the user language as the default language for this page
		SessionBean session = this.getSessionBean();
		if (session != null && session.getUser() != null)
		{
			UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
			viewRoot.setLocale(
				new Locale( session.getUser().getLanguage() )
			);
		}

/*
		String pagePath = this.getPageId();

		// Check for permission to see current view
		ApplicationBean app = null;
		app = this.getApplicationBean();
		if (
			app == null
			|| (
				session != null
				&& session.getUser() != null
				&& !app.isPathAccessibleByUser( pagePath, session.getUser() )
			)
			|| !app.isPathAccessibleByAnonymous(pagePath)
		) {
//			// Prepare the response object
//			HttpServletResponse response = null;
//			response = (HttpServletResponse) this.getFacesContext().getExternalContext().getResponse();
//			try
//			{
//				response.sendError( HttpServletResponse.SC_FORBIDDEN );
//			}
//			catch(Exception e)
//			{
//				// ignore
//			}
//
//			// Complete the response
//			this.getFacesContext().responseComplete();
//			//this.getFacesContext().renderResponse();

			throw new ForbiddenAccessException();
		}
*/
	}

/*
	protected String getPageId()
	{
		// Variant #1
		//String pagePath = FacesContext.getCurrentInstance().getExternalContext().getRequestPathInfo();

		// Variant #2
		String pageId = null;
		pageId = (String) this.getRequestMap().get( ViewConstants.PageIdAttribute );

		return !Strings.IsNullOrEmpty( pageId ) ? pageId : "/";
	}
*/

	/** Returns the session bean object. */
        protected SessionBean getSessionBean()
        {
                return (SessionBean) this.getBean( ViewConstants.SessionBeanName );
        }

	/** Returns the application bean object. */
        protected ApplicationBean getApplicationBean()
        {
                return (ApplicationBean) this.getBean( ViewConstants.ApplicationBeanName );
        }
}
