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

package it.unipmn.di.dcs.sharegrid.web.portal;

import it.unipmn.di.dcs.sharegrid.web.model.User;

import java.util.List;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IPortalAccessManager
{
	/**
	 * Returns {@code true} if the given path is accessible by an anonymous
	 * user.
	 *
	 * @param path The path against which to check for access permissions.
	 *
	 * @return {@code true} if the anonymous user can access to the given
	 * path; {@code false} otherwise.
	 *
	 */
	boolean isPathAccessibleByAnonymous(String path);

	/**
	 * Returns {@code true} if the given path is accessible by the given
	 * user.
	 *
	 * @param path The path against which to check for access permissions.
	 * @param user The user that want to access to this path.
	 * If {@code null}, is interpreted as an anonymous user.
	 *
	 * @return {@code true} if the user can access to the given path;
	 * {@code false} otherwise.
	 */
	boolean isPathAccessibleByUser(String path, User user);

	/**
	 * Returns the list of site sections accessible by the given user.
	 *
	 * @return A {@code List} of {@code SiteSection} accessible by the
	 * given user.
	 */
	List<SiteSection> getAnonymouslyAccessibleSections();

	/**
	 * Returns the list of site sections accessible by the given user.
	 *
	 * @param user The user for which returning all accessible sections.
	 * If {@code null}, is interpreted as an anonymous user.
	 * @return A {@code List} of {@code SiteSection} accessible by the
	 * given user.
	 */
	List<SiteSection> getAccessibleSections(User user);
}
