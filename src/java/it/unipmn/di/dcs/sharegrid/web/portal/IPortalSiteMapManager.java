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

import it.unipmn.di.dcs.common.annotation.FIXME;

import java.util.List;

/**
 * Interface for describing the structure of the portal.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IPortalSiteMapManager
{
	/** Returns the {@code List} of all site sections. */
	List<SiteSection> getSiteSections();

	/** Returns the site section associated to the given section ID. */
	SiteSection getSiteSectionById(String id);

	/** Returns the site section associated to the given section path. */
	SiteSection getSiteSectionByPath(String path);

	/**
	 * Returns the longest matching site section prefix associated to the
	 * given section path.
	 */
	SiteSection getLongestPrefixSiteSectionByPath(String path);

	/** Returns the administration site section. */
	@FIXME("Used only for accessing from a JSF page. Probably removed in the future")
	SiteSection getAdminSection();

	/** Returns the error site section. */
	@FIXME("Used only for accessing from a JSF page. Probably removed in the future")
	SiteSection getErrorSection();

	/** Returns the grid site section. */
	@FIXME("Used only for accessing from a JSF page. Probably removed in the future")
	SiteSection getGridSection();

	/** Returns the help site section. */
	@FIXME("Used only for accessing from a JSF page. Probably removed in the future")
	SiteSection getHelpSection();

	/** Returns the root site section. */
	@FIXME("Used only for accessing from a JSF page. Probably removed in the future")
	SiteSection getRootSection();

	/** Returns the user site section. */
	@FIXME("Used only for accessing from a JSF page. Probably removed in the future")
	SiteSection getUserSection();
}
