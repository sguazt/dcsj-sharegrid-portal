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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter for preventing direct access to JSP page (it redirects to the cor
 * responding JSF page).
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class FacesRedirectFilter implements Filter
{
	private final static String EXTENSION = "faces";

	//@{ Filter implementation

	public void init(FilterConfig filterConfig)
	{
		// empty
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException
	{
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		String uri = request.getRequestURI();
		if (uri.endsWith(".jsp"))
		{
			int length = uri.length();
			String newAddress =
			uri.substring(0, length-3) + EXTENSION;
			response.sendRedirect(newAddress);
		}
		else
		{
			// Address ended in "/"
			response.sendRedirect( "index." + FacesRedirectFilter.EXTENSION );
		}
	}

	public void destroy()
	{
		// empty
	}

	//@} Filter implementation
}
