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

import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.model.SecurityLevels;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for describing the structure of the portal.
 *
 * THe structure is fixed and hard-coded.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class FixedPortalSiteMapManager implements IPortalSiteMapManager
{
	private static final String ADMIN_SECTION_ID = "/admin";
	private static final String ERROR_SECTION_ID = "/error";
	private static final String GRID_SECTION_ID = "/grid";
	private static final String HELP_SECTION_ID = "/help";
	private static final String ROOT_SECTION_ID = "/";
	private static final String USER_SECTION_ID = "/user";

	private static final String ADMIN_SECTION_PATH = "/admin";
	private static final String ERROR_SECTION_PATH = "/err";
	private static final String GRID_SECTION_PATH = "/grid";
	private static final String HELP_SECTION_PATH = "/help";
	private static final String ROOT_SECTION_PATH = "/";
	private static final String USER_SECTION_PATH = "/user";

	private Map<String,SiteSection> siteMap;

	/** A constructor. */
	public FixedPortalSiteMapManager()
	{
		this.init();
	}

	//@{ IPortalSiteMapManager implementation //////////////////////////////

	@SuppressWarnings("unchecked")
	public List<SiteSection> getSiteSections()
	{
		return new ArrayList( this.siteMap.values() );
	}

	public SiteSection getSiteSectionById(String id)
	{
		return this.siteMap.get( id );
	}

	public SiteSection getSiteSectionByPath(String path)
	{
		return this.siteMap.get( path );
	}

	public SiteSection getLongestPrefixSiteSectionByPath(String path)
	{
		String maxPrefix = null;

		if ( !Strings.IsNullOrEmpty( path ) )
		{
			if ( this.siteMap.containsKey(path) )
			{
				maxPrefix = path;
			}
			else
			{
				int maxPrefixLen = 0;

				for (String sectPath : this.siteMap.keySet())
				{
					if (
						path.startsWith( sectPath )
						&& sectPath.length() > maxPrefixLen
					) {
						maxPrefixLen = sectPath.length();
						maxPrefix = sectPath;
					}
				}
			}
		}

		if ( Strings.IsNullOrEmpty( maxPrefix ) )
		{
			// fall-back prefix
			maxPrefix = ROOT_SECTION_PATH;
		}

		return this.siteMap.get(maxPrefix);
	}

	public SiteSection getAdminSection()
	{
		return this.siteMap.get( ADMIN_SECTION_PATH );
	}

	public SiteSection getErrorSection()
	{
		return this.siteMap.get( ERROR_SECTION_PATH );
	}

	public SiteSection getGridSection()
	{
		return this.siteMap.get( GRID_SECTION_PATH );
	}

	public SiteSection getHelpSection()
	{
		return this.siteMap.get( HELP_SECTION_PATH );
	}

	public SiteSection getRootSection()
	{
		return this.siteMap.get( ROOT_SECTION_PATH );
	}

	public SiteSection getUserSection()
	{
		return this.siteMap.get( USER_SECTION_PATH );
	}

	//@} IPortalSiteMapManager implementation //////////////////////////////

	/** Performs initializations. */
	private void init()
	{
		SiteSection section = null;

		this.siteMap = new HashMap<String,SiteSection>();

		// Admin section
		section = new SiteSection(
				ADMIN_SECTION_ID,
				ADMIN_SECTION_PATH,
				ADMIN_SECTION_ID,
				SecurityLevels.AdminUser
		);
		this.siteMap.put( ADMIN_SECTION_PATH, section );
		// Grid section
		section = new SiteSection(
				GRID_SECTION_ID,
				GRID_SECTION_PATH,
				GRID_SECTION_ID,
				SecurityLevels.StandardUser
		);
		this.siteMap.put( GRID_SECTION_PATH, section );
		// Root section
		section = new SiteSection(
				ROOT_SECTION_ID,
				ROOT_SECTION_PATH,
				USER_SECTION_ID,
				SecurityLevels.AnonymousUser
		);
		this.siteMap.put( ROOT_SECTION_PATH, section );
		// User section
		section = new SiteSection(
				USER_SECTION_ID,
				USER_SECTION_PATH,
				USER_SECTION_ID,
				SecurityLevels.StandardUser
		);
		this.siteMap.put( USER_SECTION_PATH, section );
	}
}
