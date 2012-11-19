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

import it.unipmn.di.dcs.common.annotation.Experimental;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseEvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Phase Listener for AJAX requests.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@Experimental("This class is intended for experimenting with AJAX request")
public class AjaxPhaseListener implements PhaseListener
{
	//@{ PhaseListener implementation

	public void afterPhase(PhaseEvent event)
	{
System.err.println("AjaxListener::afterPhase>> Entering");//XXX
        FacesContext ctx = event.getFacesContext();
        Map<String,String> requestParams = ctx.getExternalContext().getRequestParameterMap();
        boolean isAjax = requestParams.containsKey("ajax");
System.err.println("AjaxListener::afterPhase>> view root id: " + ctx.getViewRoot().getViewId());//XXX
System.err.println("AjaxListener::afterPhase>> Is Ajax? " + isAjax);//XXX

		if (isAjax)
		{
			this.handleAjaxRequest(event);
		}
System.err.println("AjaxListener::afterPhase>> Exiting");//XXX
	}

	public void beforePhase(PhaseEvent arg0)
	{
System.err.println("AjaxListener::beforePhase>> Entering");//XXX
		// empty: view state hasn't been restored yet
System.err.println("AjaxListener::beforePhase>> Exiting");//XXX
	}

	public PhaseId getPhaseId()
	{
		return PhaseId.RESTORE_VIEW;
	}

	//@} PhaseListener implementation

	private void handleAjaxRequest(PhaseEvent event)
	{
		FacesContext ctx = event.getFacesContext();
        Map<String,String> requestParams = ctx.getExternalContext().getRequestParameterMap();
		HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
		//HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
		//String name = request.getParameter("param");
		try
		{
System.err.println("AjaxListener::handleAjaxRequest>> target name: " + requestParams.get("ajax_target"));//XXX
System.err.println("AjaxListener::handleAjaxRequest>> target value: " + requestParams.get(requestParams.get("ajax_target")));//XXX
			response.getWriter().write("IT WORKS");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		ctx.responseComplete();
	}

}
