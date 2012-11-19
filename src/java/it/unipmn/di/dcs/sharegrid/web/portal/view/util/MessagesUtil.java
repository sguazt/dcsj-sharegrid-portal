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

package it.unipmn.di.dcs.sharegrid.web.portal.view.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * Utility class for view messages.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class MessagesUtil
{
	public static FacesMessage GetMessage(String bundleName, String resourceId)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		String appBundle = app.getMessageBundle();
		Locale locale = MessagesUtil.GetLocale(context);
		ClassLoader loader = MessagesUtil.GetClassLoader();
		String summary = MessagesUtil.GetString(
					appBundle,
					bundleName,
					resourceId,
					locale,
					loader
		);
		if (summary == null)
		{
			summary = "???" + resourceId + "???";
		}
		String detail = MessagesUtil.GetString(
					appBundle,
					bundleName,
					resourceId + "_detail",
					locale,
					loader
		);
		return new FacesMessage(summary, detail);
	}

	public static FacesMessage GetMessage(String bundleName, String resourceId, Object... params)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		String appBundle = app.getMessageBundle();
		Locale locale = MessagesUtil.GetLocale(context);
		ClassLoader loader = MessagesUtil.GetClassLoader();
		String summary = MessagesUtil.GetString(
					appBundle,
					bundleName,
					resourceId,
					locale,
					loader,
					params
		);
		if (summary == null)
		{
			summary = "???" + resourceId + "???";
		}
		String detail = MessagesUtil.GetString(
					appBundle,
					bundleName,
					resourceId + "_detail",
					locale,
					loader,
					params
		);
		return new FacesMessage(summary, detail);
	}

	public static String GetString(String bundle, String resourceId)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		String appBundle = app.getMessageBundle();
		Locale locale = MessagesUtil.GetLocale(context);
		ClassLoader loader = MessagesUtil.GetClassLoader();
		return MessagesUtil.GetString(
				appBundle,
				bundle,
				resourceId,
				locale,
				loader
		);
	}  

	public static String GetString(String bundle, String resourceId, Object... params)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		String appBundle = app.getMessageBundle();
		Locale locale = MessagesUtil.GetLocale(context);
		ClassLoader loader = MessagesUtil.GetClassLoader();
		return MessagesUtil.GetString(
				appBundle,
				bundle,
				resourceId,
				locale,
				loader,
				params
		);
	}  

	public static String GetString(String bundle1, String bundle2, String resourceId, Locale locale, ClassLoader loader, Object... params)
	{
		String resource = null;
		ResourceBundle bundle;

		if (bundle1 != null)
		{
			try
			{
				bundle = ResourceBundle.getBundle(bundle1, locale, loader);
			}
			catch (Exception e)
			{
				bundle = null;
			}
			if (bundle != null)
			{
				try
				{
					resource = bundle.getString(resourceId);
				}
				catch (MissingResourceException ex)
				{
					// empty
				}
			}
		}

		if (resource == null)
		{
			try
			{
				bundle = ResourceBundle.getBundle(bundle2, locale, loader);
			}
			catch (Exception e)
			{
				bundle = null;
			}

			if (bundle != null)
			{
				try
				{
					resource = bundle.getString(resourceId);
				}
				catch (MissingResourceException ex)
				{
					// empty
				}
			}
		}

		if (resource == null)
		{
			return null; // no match
		}
		if (params == null)
		{
			return resource;
		}

		MessageFormat formatter = new MessageFormat(resource, locale);      
		return formatter.format(params);
	}   

	public static Locale GetLocale(FacesContext context)
	{
		Locale locale = null;
		UIViewRoot viewRoot = context.getViewRoot();
		if (viewRoot != null)
		{
			locale = viewRoot.getLocale();
		}
		if (locale == null)
		{
			locale = Locale.getDefault();
		}
		return locale;
	}

	public static ClassLoader GetClassLoader()
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null)
		{
			loader = ClassLoader.getSystemClassLoader();
		}
		return loader;
	}
}
