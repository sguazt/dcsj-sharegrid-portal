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

import it.unipmn.di.dcs.common.annotation.Experimental;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
//import java.util.regex.Pattern;

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
 * A servlet filter responsible for checking about expired sessions.
 *
 * To enable this filter, the <em>web.xml</em> has to be modified in the
 * following way:
 * <ul>
 * <li>Provide an initial parameter <em>redirectPage</em> representing the URL where
 * redirecting in case of an expired session. E.g.:
 * <pre>
 *   &lt;filter&gt;
 *     &lt;filter-name&gt;SessionExpireFilter&lt;/filter-name&gt;
 *     &lt;filter-class&gt;com.cj.sessexpire.SessionExpireFilter&lt;/filter-class&gt;
 *     &lt;init-param&gt;
 *       &lt;param-name&gt;redirect&lt;/param-name&gt;
 *       &lt;param-value&gt;/your/page/for/expired/sessions&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 *   &lt;/filter&gt;
 * </pre>
 * </li>
 * <li>Provide a mapping for this filter in <em>web.xml</em>. E.g.:
 * <pre>
 *   &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;SessionExpiredFilter&lt;/filter-name&gt;
 *     &lt;url-pattern&gt;*.jsp&lt;/url-pattern&gt;
 *   &lt;/filter-mapping&gt;
 * </pre>
 * </li>
 *
 * In this example, filter will be on for the each .jsp file. When some .jsp
 * page (covered by SessionExpireFilter mapping) is requested, this filter may
 * redirect (or forward) request to the specified URL.
 * If target URL is absolute (starts with http) than request will be redirected.
 * Otherwise filter assumes a local resource and forwards request.
 *
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class SessionExpiredFilter implements Filter
{
	private static final String REDIRECTPAGE_PARAM = "redirectPage";

	private String redirectPage = null;
	private boolean isRedirectPageAbsolute = false;
	private FilterConfig config = null;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		String redirect = filterConfig.getInitParameter( REDIRECTPAGE_PARAM );

		if ( redirect == null )
		{
			throw new ServletException( "Init parameter '" + REDIRECTPAGE_PARAM + "' not specified." );
		}

		URI redirectURI = null;
		try
		{
//			// Note: the trick url.toURI().toURL() is needed for
//			// encoding special characters in the original URL.
//			// Furthermore the conversion URL -> URI let us to
//			// know if the URL is absolute or relative.

			redirectURI = new URI( redirect );
			this.isRedirectPageAbsolute = redirectURI.isAbsolute();
		}
		catch (Exception e)
		{
			throw new ServletException( "Bad URL for the init parameter '" + REDIRECTPAGE_PARAM + "'.", e );
		}

		this.redirectPage = redirectURI.toString();
		this.config = filterConfig;
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
		HttpSession session = request.getSession(false);
		if (
			session == null
			|| request.getRequestedSessionId() == null
			|| this.redirectPage == null
		) {
			filterChain.doFilter(servletRequest, servletResponse);
		}
		else
		{
			String sessionId = session.getId();
			if ( sessionId.equals( request.getRequestedSessionId() ) )
			{
				filterChain.doFilter(servletRequest, servletResponse);
			}
			else
			{
				HttpServletResponse response = (HttpServletResponse) servletResponse;
				response.sendRedirect( request.getContextPath() + request.getServletPath() + this.redirectPage );
//FIXME: check if forward works: in SecurityFilter the use of forward raise the exception:
//     Servlet.service() for servlet Faces Servlet threw exception. java.lang.RuntimeException: Cannot find FacesContext 
//
//				HttpServletResponse response = (HttpServletResponse) servletResponse;
//				ServletContext context = this.config.getServletContext();
//				if ( this.isRedirectPageAbsolute )
//				{
//					response.sendRedirect( this.redirectPage );
//				}
//				else
//				{
//					RequestDispatcher dispatcher = null;
//					//dispatcher = context.getRequestDispatcher(request.getContextPath() + this.redirectPage);
//					dispatcher = context.getRequestDispatcher( this.redirectPage );
//					dispatcher.forward(servletRequest, servletResponse);
//				}
			}
		}
	}
}
