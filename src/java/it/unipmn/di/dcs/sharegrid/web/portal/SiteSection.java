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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract section of the site.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class SiteSection
{
	/** The ID of this section. */
	private String id;
	/** The path of this section (without servlet and request contexts). */
	private String path;
	/**
	 * The ID of the navigation section.
	 *
	 * Might be different from the one of this section, since a section
	 * might appear in a bigger container section.
	 */
	private String navSectionId;
	/** The minimum security level needed to access to this section. */
	private SecurityLevels secLevel;
//	private List<SectionItem> items;

	/** A constructor. */
	public SiteSection(String id, String path, String navSectionId, SecurityLevels secLevel)
	{
		this.id = id;
		this.path = path;
		this.navSectionId = navSectionId;
		this.secLevel = secLevel;
//		this.items = new ArrayList<SectionItem>();
		//this.navSect = navSect;
	}

	public String getId() { return this.id; }

	public String getPath() { return this.path; }

	public String getNavigationSectionId() { return this.navSectionId; }

	public SecurityLevels getSecurityLevel() { return this.secLevel; }

//	public void addItem(String name, String path, String decr)
//	{
//		this.items.add( new SectionItem(name, path, decr) );
//	}
}
