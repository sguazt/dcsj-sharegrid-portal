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

/**
 * <p>Interface describing a service used to map from a JavaServer Faces
 * <em>view identifier</em> (typically the context relative path to a JSP
 * page) to the managed bean name of the corresponding page bean.</p>
 *
 * This is inspired to the Sun RAVE APPBASE project.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface PageBeanMapper
{
	/**
	 * <p>Return the managed bean name for the page bean (must extend
	 * {@link AbstractPageBean}) that corresponds to the specified
	 * view identifier (typically the context-relative path to a
	 * JSP page).</p>
	 *
	 * @param viewId View identifier to be mapped
	 */
	public String mapViewId(String viewId);
}
