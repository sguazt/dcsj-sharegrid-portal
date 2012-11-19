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

import it.unipmn.di.dcs.common.annotation.FIXME;
import it.unipmn.di.dcs.common.util.Pair;
import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.SecurityLevels;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewConstants;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Bean class for application scoped bean objects.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ApplicationBean extends AbstractApplicationBean
{
//	static
//	{
//		String mgroot = null;
//
//		ExternalContext extCtx = FacesContext.getCurrentInstance().getExternalContext();
//
//		mgroot = extCtx.getInitParameter("it.unipmn.di.dcs.sharegrid.MGROOT");
//		System.setProperty( "MGROOT", mgroot );
//	}

	/** The list of supported languages. */
	private List<Pair<String,String>> langs;
	public List<Pair<String,String>> getSupportedLanguages()
	{
		if ( this.langs == null || this.langs.isEmpty() )
		{
			FacesContext facesContext = FacesContext.getCurrentInstance();
			Application app = facesContext.getApplication();

			//List<Pair<String,String>> langs = new ArrayList<Pair<String,String>>();
			this.langs = new ArrayList<Pair<String,String>>();
			java.util.Iterator<java.util.Locale> langIt = app.getSupportedLocales();
			while (langIt.hasNext())
			{
				Locale locale = langIt.next();
				this.langs.add(
					new Pair<String,String>(
						locale.getLanguage(),
						locale.getDisplayName()
					)
				);
			}
		}

		return this.langs;
	}

	/** The current locale. */
	private Locale defLocale;
	public Pair<String,String> getDefaultLanguage()
	{
		if ( this.defLocale == null )
		{
			FacesContext facesContext = FacesContext.getCurrentInstance();
			Application app = facesContext.getApplication();

			//Locale defLocale = app.getDefaultLocale();
			this.defLocale = app.getDefaultLocale();
		}

		return new Pair<String,String>(
			this.defLocale.getLanguage(),
			this.defLocale.getDisplayName()
		);
	}

//	/** The section navigator map. */
//	@FIXME("Navigation sections are hard-coded. They should be built at deploy time (e.g. a script that creates a Java class with an auto-generated Map")
//	private Map<String,String> navSectMap;
//	protected Map<String,String> getPageNavSectionMap()
//	{
//		if ( this.navSectMap == null )
//		{
//			this.navSectMap = new HashMap<String,String>();
//			this.navSectMap.put( "/admin", "admin" );
//			this.navSectMap.put( "/grid", "grid" );
//			this.navSectMap.put( "/", "user" ); // fall-back case
//		}
//
//		return this.navSectMap;
//	}
//	public String getPageNavSection(String pagePath)
//	{
//		int maxPrefixLen = 0;
//		String maxPrefix = null;
//
//		if ( pagePath != null )
//		{
//			for (String path : this.getPageNavSectionMap().keySet())
//			{
//				if (
//					pagePath.startsWith( path )
//					&& path.length() > maxPrefixLen
//				) {
//					maxPrefixLen = path.length();
//					maxPrefix = path;
//				}
//			}
//		}
//
//		if ( Strings.IsNullOrEmpty( maxPrefix ) )
//		{
//			// fall-back prefix
//			maxPrefix = "/";
//		}
//
//		return this.getPageNavSectionMap().get(maxPrefix);
//	}
//	public String getCurrentPageNavSection()
//	{
//		return this.getPageNavSection(
//			(String) this.getRequestMap().get( ViewConstants.PageIdAttribute )
//		);
//	}

/*
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
			secLevel = 0;
		}

		if ( path.startsWith("/admin") )
		{
			return secLevel >= SecurityLevels.AdminUser.intValue();
		}
		else if ( path.startsWith("/grid") )
		{
			return secLevel >= SecurityLevels.StandardUser.intValue();
		}
		else if ( path.startsWith("/user") )
		{
			return secLevel >= SecurityLevels.StandardUser.intValue();
		}

		// fall-back case (include: /, /err)
		return secLevel >= SecurityLevels.AnonymousUser.intValue();
	}
*/
}
