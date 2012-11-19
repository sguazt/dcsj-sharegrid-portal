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

import it.unipmn.di.dcs.sharegrid.web.model.SecurityLevels;
import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;
import it.unipmn.di.dcs.sharegrid.web.portal.PortalController;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
//import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Implements a filter for security accesses.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class AccessControlFilter implements Filter
{
	private static final String REDIRECT_PAGE_PARAM = "redirectPage";
	private static final String SESSION_USERID_PARAM = "userIdSessionAttr";

	private boolean isRedirectPageAbsolute;
	private String redirectPage;
	private String userIdAttr;
	private ServletContext context;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		String redirectParam = null;
		String userIdAttrParam = null;

		redirectParam = filterConfig.getInitParameter( REDIRECT_PAGE_PARAM );
		if ( redirectParam == null )
		{
			throw new ServletException( "Init parameter '" + REDIRECT_PAGE_PARAM + "' not specified." );
		}

		URI redirectURI = null;
		try
		{
			// Note: the trick url.toURI().toURL() is needed for
			// encoding special characters in the original URL.
			// Furthermore the conversion URL -> URI let us to
			// know if the URL is absolute or relative.

			redirectURI = new URI( redirectParam );
			this.isRedirectPageAbsolute = redirectURI.isAbsolute();
		}
		catch (Exception e)
		{
			throw new ServletException( "Bad URL for the init parameter '" + REDIRECT_PAGE_PARAM + "'.", e );
		}

		this.redirectPage = redirectURI.toString();

		userIdAttrParam = filterConfig.getInitParameter( SESSION_USERID_PARAM );
		if ( userIdAttrParam == null )
		{
			throw new ServletException( "Init parameter '" + SESSION_USERID_PARAM + "' not specified." );
		}
		this.userIdAttr = userIdAttrParam;

		this.context = filterConfig.getServletContext();
	}

	@Override
	public void destroy()
	{
		// empty
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		String path = null;

		//path = request.getContextPath() + request.getServletPath() + request.getPathInfo();
		path = request.getPathInfo();
		boolean forbidden = true;

                HttpSession session = request.getSession(false);

		if ( session != null && request.getRequestedSessionId() != null && session.getAttribute( this.userIdAttr ) != null )
		{
			User user = null;
			try
			{
				user = UserFacade.instance().getUser(
					(Integer) session.getAttribute( this.userIdAttr )
				);
			}
			catch (Exception e)
			{
				throw new ServletException("Unable to retrieve session user '" + session.getAttribute( this.userIdAttr ) + "'");
			}

			if ( PortalController.Instance().getAccessManager().isPathAccessibleByUser(path, user) )
			{
				forbidden = false;
			}
		}
		else if ( PortalController.Instance().getAccessManager().isPathAccessibleByAnonymous(path) )
		{
			forbidden = false;
		}

		if ( forbidden )
		{
			response.sendRedirect( request.getContextPath() + request.getServletPath() + this.redirectPage );
			//response.sendRedirect(request.getContextPath() + request.getServletPath() + "/err/403.jspx");
//			if ( this.isRedirectPageAbsolute )
//			{
//				response.sendRedirect( this.redirectPage );
//			}
//			else
//			{
//System.err.println("SecurityFilter::doFilter>> FORWARDING TO: " + this.redirectPage);//XXX
//				RequestDispatcher dispatcher = null;
//				//dispatcher = context.getRequestDispatcher(request.getContextPath() + this.redirectPage);
//				dispatcher = this.context.getRequestDispatcher( request.getContextPath() + request.getServletPath() + this.redirectPage );
//				dispatcher.forward(servletRequest, servletResponse);
//			}
		}
		else
		{
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}
}
