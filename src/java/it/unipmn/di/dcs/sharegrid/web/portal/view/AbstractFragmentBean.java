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

import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewConstants;

import java.util.Locale;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * Base class for page fragment beans.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractFragmentBean extends it.unipmn.di.dcs.sharegrid.web.faces.view.AbstractFragmentBean
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
	}

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
