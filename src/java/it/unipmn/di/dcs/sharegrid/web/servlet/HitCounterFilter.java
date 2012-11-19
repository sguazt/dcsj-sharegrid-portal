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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Implements a filter for counting accesses.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public final class HitCounterFilter implements Filter
{
	private static final String COUNTER_ATTRIBUTE = "it.unipmn.di.dcs.sharegrid.web.HitCounter";

	private FilterConfig filterConfig = null;

	//@{ Filter implementation

	public void init(FilterConfig filterConfig) throws ServletException
	{
		this.filterConfig.getServletContext().removeAttribute( COUNTER_ATTRIBUTE );
		this.filterConfig = filterConfig;
	}

	public void destroy()
	{
		this.filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		if (this.filterConfig == null)
		{
			return;
		}

		Integer counter = (Integer) this.filterConfig.getServletContext().getAttribute( COUNTER_ATTRIBUTE );
		if ( counter == null )
		{
			counter = new Integer(0);
		}
		counter = new Integer( counter.intValue() + 1 );
		this.filterConfig.getServletContext().setAttribute(
			 COUNTER_ATTRIBUTE,
			counter
		);

		chain.doFilter(request, response);
	}

	//@{ Filter implementation
}
