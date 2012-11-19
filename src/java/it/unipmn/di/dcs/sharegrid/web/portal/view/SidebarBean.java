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

import it.unipmn.di.dcs.sharegrid.web.portal.PortalController;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewConstants;

import java.util.logging.Logger;

/**
 * Page bean for page sidebar.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class SidebarBean extends AbstractFragmentBean
{
	private static final transient Logger Log = Logger.getLogger( SidebarBean.class.getName() );

//	private String incView = "";
//	public String getIncludingView() { return this.incView; }
//	public void setIncludingView(String value) { this.incView = value; }

//	public String getNavigatorSectionIdAttr()
//	{
//		//return "it.unipmn.di.dcs.sharegrid.web.portal.NavigatorSectionId";
//		return ViewConstants.NavigatorSectionIdAttribute;
//	}

//	public String getPageIdAttribute()
//	{
//		return ViewConstants.PageIdAttribute;
//	}

	public String getNavigatorSectionId()
	{
		//return this.getApplicationBean().getPageNavSection( (String) this.getRequestMap().get( ViewConstants.PageIdAttribute ) );
		String path = null;
		path = (String) this.getRequestMap().get( ViewConstants.PageIdAttribute );
		return PortalController.Instance().getSiteMapManager().getLongestPrefixSiteSectionByPath( path ).getNavigationSectionId();
	}

	public String getAdminSiteSectionNavId()
	{
		return PortalController.Instance().getSiteMapManager().getAdminSection().getNavigationSectionId();
	}

	public String getGridSiteSectionNavId()
	{
		return PortalController.Instance().getSiteMapManager().getGridSection().getNavigationSectionId();
	}

	public String getUserSiteSectionNavId()
	{
		return PortalController.Instance().getSiteMapManager().getUserSection().getNavigationSectionId();
	}

	/**
	 * Return {@code true} if the ADMIN section is accessible by the current
	 * user.
	 */
	public boolean isAdminSiteSectionAccessible()
	{
		return this.isSiteSectionAccessible(
			PortalController.Instance().getSiteMapManager().getAdminSection().getPath()
		);
	}

	/**
	 * Return {@code true} if the GRID section is accessible by the current
	 * user.
	 */
	public boolean isGridSiteSectionAccessible()
	{
		return this.isSiteSectionAccessible(
			PortalController.Instance().getSiteMapManager().getGridSection().getPath()
		);
	}

	/**
	 * Return {@code true} if the USER section is accessible by the current
	 * user.
	 */
	public boolean isUserSiteSectionAccessible()
	{
		return this.isSiteSectionAccessible(
			PortalController.Instance().getSiteMapManager().getUserSection().getPath()
		);
	}

	/**
	 * Return {@code true} if the given path is accessible by the current
	 * user.
	 */
	protected boolean isSiteSectionAccessible(String path)
	{
		return PortalController.Instance().getAccessManager().isPathAccessibleByUser(
			path,
			this.getSessionBean() != null
				? this.getSessionBean().getUser()
				: null
		);
	}
}
