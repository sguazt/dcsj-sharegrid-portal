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

package it.unipmn.di.dcs.sharegrid.web.faces.servlet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;

/**
 * Enables messages to be rendered on different pages from which they were set.
 *
 * To produce this behaviour, this class acts as a {@code PhaseListener}.
 * This is performed by moving the FacesMessage objects:
 * <ol>
 * <li> After each phase where messages may be added, this moves the messages
 * from the page-scoped FacesContext to the session-scoped session map.
 * <li> Before messages are rendered, this moves the messages from the
 * session-scoped session map back to the page-scoped FacesContext.
 * </ol>
 * Only messages that are not associated with a particular component are ever
 * moved. These are the only messages that can be rendered on a page that is
 * different from where they originated.
 *
 * To enable this behaviour, add a <code>lifecycle</code> block to your
 * faces-config.xml file. That block should contain a single
 * <code>phase-listener</code> * block containing the fully-qualified classname
 * of this file.
 *
 * <pre>
 * &lt;faces-config&gt;
 * &lt;lifecycle&gt;
 * &lt;phase-listener&gt;MultiPageMessagesPhaseListener&lt;/phase-listener&gt;
 * &lt;/lifecycle&gt;
 * &lt;/faces-config&gt;
 * </pre>
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class MultiPageMessagesPhaseListener implements PhaseListener
{

	/** a name to save messages in the session under */
	private static final String SESSION_TOKEN = MultiPageMessagesPhaseListener.class.getName();

	//@{ PhaseListener implementation //////////////////////////////////////

	public PhaseId getPhaseId()
	{
		return PhaseId.ANY_PHASE;
	}

	public void beforePhase(PhaseEvent event)
	{
		if( event.getPhaseId() == PhaseId.RENDER_RESPONSE )
		{
			FacesContext facesContext = event.getFacesContext();
			this.restoreMessages(facesContext);
		}
	}

	public void afterPhase(PhaseEvent event)
	{
		if(
			event.getPhaseId() == PhaseId.APPLY_REQUEST_VALUES
			|| event.getPhaseId() == PhaseId.PROCESS_VALIDATIONS
			|| event.getPhaseId() == PhaseId.INVOKE_APPLICATION
		) {
			FacesContext facesContext = event.getFacesContext();
			this.saveMessages(facesContext);
		}
	}

	//@} PhaseListener implementation //////////////////////////////////////

	/**
	 * Remove the messages that are not associated with any particular
	 * component from the faces context and store them to the user's
	 * session.
	 *
	 * @return the number of removed messages.
	 */
	@SuppressWarnings("unchecked")
	private int saveMessages(FacesContext facesContext)
	{
		// remove messages from the context
		List<FacesMessage> messages = new ArrayList<FacesMessage>();
		for ( Iterator i = facesContext.getMessages(null); i.hasNext(); )
		{
			messages.add( (FacesMessage) i.next() );
			i.remove();
		}

		// store them in the session
		if( messages.size() == 0 )
		{
			return 0;
		}

		Map<String,Object> sessionMap = null;
		sessionMap = facesContext.getExternalContext().getSessionMap();
		if ( sessionMap == null )
		{
			return 0;
		}

		// Check for already stored messages

		List<FacesMessage> existingMessages = null;
		existingMessages = (List<FacesMessage>) sessionMap.get(SESSION_TOKEN);
		if( existingMessages != null )
		{
			// append new messages to already stored messages.
			existingMessages.addAll(messages);
		}
		else
		{
			// no message already stored.
			sessionMap.put(SESSION_TOKEN, messages);
		}

		return messages.size();
	}

	/**
	 * Remove the messages that are not associated with any particular
	 * component from the user's session and add them to the faces context.
	 *
	 * @return the number of removed messages.
	 */
	@SuppressWarnings("unchecked")
	private int restoreMessages(FacesContext facesContext)
	{
		// remove messages from the session

		Map<String,Object> sessionMap = null;
		sessionMap = facesContext.getExternalContext().getSessionMap();
		if ( sessionMap == null )
		{
			return 0;
		}

		List<FacesMessage> messages = null;
		messages = (List<FacesMessage>) sessionMap.remove(SESSION_TOKEN);

		// store them in the context

		if (messages == null)
		{
			return 0;
		}

		int restoredCount = messages.size();
		for( Iterator i = messages.iterator(); i.hasNext(); )
		{
			facesContext.addMessage( null, (FacesMessage) i.next() );
		}

		return restoredCount;
	}
}
