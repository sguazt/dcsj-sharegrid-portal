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

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

/**
 * <p>ActionListener implementation that cooperates with the exception caching
 * strategy we implement in {@link ViewHandlerImpl}.</p>
 *
 * This is inspired to the Sun RAVE APPBASE project.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ActionListenerImpl implements ActionListener
{
	/**
	 * <p>The original <code>ActionListener</code> to whom we delegate.</p>
	 */
	private ActionListener handler = null;

	/**
	 * <p>Create a new <code>ActionListener</code> instance that wraps the
	 * specified one.</p>
	 *
	 * @param handler Original <code>ActionListener</code> provided by JSF
	 */
	public ActionListenerImpl(ActionListener handler)
	{
		this.handler = handler;
	}

	//@{ ActionListener implementation

	public void processAction(ActionEvent event)
	{
		try
		{
			handler.processAction(event);
		}
		catch (RuntimeException e)
		{
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().log(e.getMessage(), e);
			ViewHandlerImpl.cache(context, e);
		}
	}

	//@} ActionListener implementation
}
