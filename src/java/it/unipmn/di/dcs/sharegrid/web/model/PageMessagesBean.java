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

import it.unipmn.di.dcs.common.annotation.Experimental;
import it.unipmn.di.dcs.common.util.Strings;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

/**
 * Bean class for messages issued inside a page bean.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@Experimental
public class PageMessagesBean extends AbstractRequestBean
{
	/** A constructor. */
	public PageMessagesBean()
	{
		this.msgTitle = null;
		// See if there are messages queued for the page
		Severity severityLevel = this.getFacesContext().getMaximumSeverity();

		if ( severityLevel != null )
		{
			this.log("Severity Level Trapped: level = '" + severityLevel.toString() + "'");

			if (severityLevel.compareTo(FacesMessage.SEVERITY_ERROR) == 0)
			{
				this.msgTitle = "MESSAGE_ERROR"; //FIXME: use MessagesUtil.GetString(...)
				this.msgIcon = "resources/theme/current/img/medium/warning.gif";
			}
			else if (severityLevel.compareTo(FacesMessage.SEVERITY_INFO) == 0)
			{
				this.msgTitle = null;
				this.msgIcon = "resources/theme/current/img/medium/information.gif";
			}
			else if (severityLevel.compareTo(FacesMessage.SEVERITY_WARN) == 0)
			{
				this.msgTitle = null;
				this.msgIcon = "resources/theme/current/img/medium/warning.gif";
			}
			else if (severityLevel.compareTo(FacesMessage.SEVERITY_FATAL) == 0)
			{
				this.msgTitle = "FATAL_ERROR"; //FIXME: use MessagesUtil.GetString(...)
				this.msgIcon = "resources/theme/current/img/medium/stop.gif";
			}
		}
		else
		{
			this.log("Severity Level Trapped: level = 'null'");
		}
	}

	/** PROPERTY: renderMessage. */
	public boolean getRenderMessage()
	{
		return ! Strings.IsNullOrEmpty( this.getMessageTitle() );
	}

	/** PROPERTY: messageTitle. */
	private String msgTitle;
	public String getMessageTitle()
	{
		return this.msgTitle;
	}

	/** PROPERTY: messageIcon. */
	private String msgIcon;
	public String getMessageIcon()
	{
		return this.msgIcon;
	}
}
