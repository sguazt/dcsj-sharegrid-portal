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
package it.unipmn.di.dcs.sharegrid.web.servlet;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
//import javax.faces.el.ValueBinding;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Toy servlet for experiencing with AJAX requests.
 * It accepts GET requests and looks for parameter "ajaxtarget", representing
 * the name of the parameter containing a meaningful value.
 * If this parameter is found, the servlet looks for a request parameter with
 * name equals to the "ajaxtarget" parameter value.
 * As response, the servlet sends an XML:
 * <target>
 *   <name>The value of "ajaxtarget" parameter</name>
 *   <value>The value of parameter whose name if the value of the "ajaxtarget" parameter</value>
 * </target>
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class AjaxServlet extends HttpServlet
{   
	private ServletContext context;
	private Lifecycle lifecycle;
	private ServletConfig servletConfig;
	private FacesContextFactory facesContextFactory;

	@Override
	public void init(ServletConfig config) throws ServletException
	{
System.err.println("AjaxServlet::init>> Entering");//XXX
		this.servletConfig = config;
		this.context = config.getServletContext();

		// Perform initialization such that on a request (see doGet), we can find the FacesContext
		// for the JSF servlet. This is precisely what the FacesServlet does too.
		// Acquire our FacesContextFactory instance
		try
		{
			this.facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		}
		catch (FacesException e)
		{
			Throwable rootCause = e.getCause();

			if (rootCause == null)
			{
				throw e;
			}
			else
			{
				throw new ServletException(e.getMessage(), rootCause);
			}
		}

		// Acquire our Lifecycle instance
		try
		{
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		String lifecycleId = servletConfig.getServletContext().getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);

			if (lifecycleId == null)
			{
				lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
			}

			lifecycle = lifecycleFactory.getLifecycle(lifecycleId);
		}
		catch (FacesException e)
		{
			Throwable rootCause = e.getCause();

			if (rootCause == null)
			{
				throw e;
			}
			else
			{
				throw new ServletException(e.getMessage(), rootCause);
			}
		}
System.err.println("AjaxServlet::init>> Exiting");//XXX
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
System.err.println("AjaxServlet::doGet>> Entering");//XXX

		// Acquire the FacesContext instance for this request
		FacesContext context = null;

		context = facesContextFactory.getFacesContext(
					servletConfig.getServletContext(),
					request,
					response,
					lifecycle
		);

		StringBuilder sb = new StringBuilder();
		boolean contentAdded = false;

		String targetId = request.getParameter("ajaxtarget");
		if (targetId != null)
		{
			String targetValue = request.getParameter(targetId);

			if (targetValue != null)
			{
				sb.append("<?xml version=\"1.0\"?>");
				sb.append("<target>");
				sb.append("<name>");
				sb.append(targetId);
				sb.append("</name>");
				sb.append("<value>");
				sb.append(targetValue);
				sb.append("</value>");
				sb.append("</target>");

				contentAdded = true;
			}
		}

		if (contentAdded)
		{
System.err.println("AjaxServlet::doGet>> writing: " + sb.toString());//XXX
			response.setContentType("text/xml");
			response.setHeader("Cache-Control", "no-cache");
			response.getWriter().write(sb.toString());
		}
		else
		{
			//nothing to show
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
System.err.println("AjaxServlet::doGet>> Exiting");//XXX
	}

	@Override
	public void destroy()
	{
System.err.println("AjaxServlet::destroy>> Entering");//XXX
		super.destroy();
		facesContextFactory = null;
		lifecycle = null;
		servletConfig = null;
		context = null;
System.err.println("AjaxServlet::destroy>> Exiting");//XXX
	}
}
