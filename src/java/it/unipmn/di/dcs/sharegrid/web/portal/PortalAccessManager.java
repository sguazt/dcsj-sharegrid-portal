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

import it.unipmn.di.dcs.sharegrid.web.model.SecurityLevels;
import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manager for controlling accesses to the portal.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class PortalAccessManager implements IPortalAccessManager
{
	private NavigableMap<String,SiteSection> siteMap;
	private Map<SecurityLevels,List<SiteSection>> secLevelMap;

	/** A constructor. */
	public PortalAccessManager()
	{
		this.init();
	}

	//@{ IPortalAccessManager //////////////////////////////////////////////

        public boolean isPathAccessibleByAnonymous(String path)
        {
                return this.isPathAccessibleByUser(path, null);
        }

        public boolean isPathAccessibleByUser(String path, User user)
        {
                int secLevel;

                if ( user != null )
                {
                        secLevel = user.getSecurityLevel().intValue();
                }
                else
                {
                        secLevel = SecurityLevels.AnonymousUser.intValue();
                }

		//Pattern PATH_EXTRACTOR_PATTERN = Pattern.compile("(/.+)/[^/]*$");
		Pattern PATH_EXTRACTOR_PATTERN = Pattern.compile("(/.+)(?:/[^/]*)?$");

		Matcher matcher = PATH_EXTRACTOR_PATTERN.matcher(path);
		if ( matcher.matches() )
		{
			path = matcher.group(1);
		}
		else
		{
			path = "/";
		}
		if ( this.siteMap.containsKey(path) )
		{
			return secLevel >= this.siteMap.get(path).getSecurityLevel().intValue();
		}
		else
		{
			String bestPath = null;
			bestPath = this.siteMap.floorKey(path);

			if ( bestPath == null || !path.startsWith(bestPath) )
			{
				// Fall-back case
				bestPath = "/";
			}
			return secLevel >= this.siteMap.get(bestPath).getSecurityLevel().intValue();
		}
	}
 
	public List<SiteSection> getAnonymouslyAccessibleSections()
	{
		return this.secLevelMap.get( SecurityLevels.AnonymousUser );
	}

	public List<SiteSection> getAccessibleSections(User user)
	{
		if ( user == null )
		{
			return this.secLevelMap.get( SecurityLevels.AnonymousUser );
		}

		return this.secLevelMap.get( user.getSecurityLevel() );
	}

	//@} IPortalAccessManager //////////////////////////////////////////////

	/** Performs initializations. */
	private void init()
	{
		this.secLevelMap = new HashMap<SecurityLevels,List<SiteSection>>();
		for (SecurityLevels level : SecurityLevels.values())
		{
			this.secLevelMap.put( level, new ArrayList<SiteSection>() );
		}

		this.siteMap = new TreeMap<String,SiteSection>();
		for (SiteSection section : PortalController.Instance().getSiteMapManager().getSiteSections())
		{
			this.siteMap.put(
				section.getPath(),
				section
			);
			this.secLevelMap.get( section.getSecurityLevel() ).add( section );
		}
	}
}
