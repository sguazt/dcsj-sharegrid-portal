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

package it.unipmn.di.dcs.sharegrid.web.faces.util;

import javax.el.ValueExpression;

import javax.faces.application.Application;
import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.ValueHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.FacesException;
import javax.faces.validator.Validator;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class for our JSF framework.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public final class FacesUtil
{
	// component family constants for UIForm and WebUIForm
	protected static final String WEB_UIFORM = "com.sun.rave.web.ui.Form";
	protected static final String UIFORM = "javax.faces.form";
	protected static final String WEB_UIJSFFORM = "com.sun.webui.jsf.Form";
	protected static final String INVOCATION_PATH = "it.unipmn.di.dcs.sharegrid.web.faces.INVOCATION_PATH";

	public static void AssertValidParams(FacesContext context, UIComponent component)
	{
		// preconditions
		assert( context != null && component != null );

		if (context == null)
		{
			throw new NullPointerException("Invalid Parameter - FacesContext instance must not be null");
		}
		if (component == null)
		{
			throw new NullPointerException("Invalid Parameter - UIComponent instance must not be null");
		}
	}

	public static void AssertValidParams(FacesContext context, UIComponent component, Class validComponentType)
	{
		// preconditions
		assert( validComponentType != null );

		FacesUtil.AssertValidParams(context, component);

		if ( !validComponentType.isInstance(component) )
		{
			throw new IllegalArgumentException("Invalid Parameter - UIComponent class should be [" + validComponentType + "] but it is an instance of [" + component.getClass() + "]");
		}
		if (
			( component instanceof UIInput )
			|| ( component instanceof UICommand )
		)
		{
			if ( FacesUtil.FindForm(component) == null )
			{
				throw new NullPointerException("Missing Form - the UIComponent of type [" + component.getClass() + "] requires a containing form.");
			}
		}
	}

	/**
	 * <p/>
	 * Given a UIComponent instance, recursively examine the heirarchy of
	 * parent NamingContainers until a Form is found. </p>
	 *
	 * @param component the UIComponent instance
	 * @return form as the UIComponent instance
	*/
	public static UIComponent FindForm(UIComponent component)
	{
		UIComponent parent = component.getParent();
		while (parent != null && !(parent instanceof UIForm))
		{
			parent = FindNamingContainer(parent);
		}
		UIComponent form = null;
		// check family 
		if (
			parent != null
			&& (parent.getFamily().equalsIgnoreCase(WEB_UIFORM)
			|| parent.getFamily().equalsIgnoreCase(UIFORM)
			|| parent.getFamily().equalsIgnoreCase(WEB_UIJSFFORM))
		) {
			form = (UIComponent) parent;
		}

		return form;
	}

	/**
	 * <p/>
	 * Given a UIComponent instance, recursively examine the heirarchy of
	 * parent UIComponents until the first NamingContainer is found. </p>
	 *
	 * @param component
	 * @return the nearest parent NamingContainer or null if none exist.
	 */
    public static UIComponent FindNamingContainer(UIComponent component) {
        UIComponent parent = component.getParent();
        while (parent != null) {
            if (parent instanceof NamingContainer) {
                break;
            }
            parent = parent.getParent();
        }
        return parent;
    }

	public static boolean ComponentIsDisabledOrReadonly(UIComponent component)
	{
		Object disabledOrReadonly = null;
		boolean result = false;
		if (null != (disabledOrReadonly = component.getAttributes().get("disabled")))
		{
			if (disabledOrReadonly instanceof String)
			{
				result = ((String) disabledOrReadonly).equalsIgnoreCase("true");
			}
			else
			{
				result = disabledOrReadonly.equals(Boolean.TRUE);
			}
		}
		if ((result == false) && null != (disabledOrReadonly = component.getAttributes().get("readonly")))
		{
			if (disabledOrReadonly instanceof String)
			{
				result = ((String) disabledOrReadonly).equalsIgnoreCase("true");
			}
			else
			{
				result = disabledOrReadonly.equals(Boolean.TRUE);
			}
		}

		return result;
	}

	public static Converter GetConverterForClass(Class converterClass, FacesContext facesContext)
	{
		if (converterClass == null)
		{
			return null;
		}
		try
		{
			Application application = facesContext.getApplication();
			return (application.createConverter(converterClass));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static Converter GetConverter(FacesContext context, UIComponent component)
	{
		if (!(component instanceof ValueHolder))
		{
			return null;
		}

		ValueHolder holder = (ValueHolder) component;

		Converter converter = holder.getConverter();
		if (converter != null)
		{
			return converter;
		}

		ValueExpression expr = component.getValueExpression("value");
		if (expr == null)
		{
			return null;
		}

		Class targetType = expr.getType(context.getELContext());
		if (targetType == null)
		{
			return null;
		}

		// Version 1.0 of the reference implementation will not apply a converter
		// if the target type is String or Object, but that is a bug.

		Application app = context.getApplication();
		return app.createConverter(targetType);
	}
 
	public static Object GetConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException
	{
		if (submittedValue instanceof String)
		{
			Converter converter = FacesUtil.GetConverter(context, component);
			if (converter != null)
			{
				return converter.getAsObject(context, component, (String) submittedValue);
			}
		}
		return submittedValue;
	}

	@SuppressWarnings("unchecked")
	public static <T> T GetValue(UIComponent comp, String attrName, T defaultValue)
	{
		ValueExpression ve = comp.getValueExpression(attrName);

		return  ( ve != null )
			? (T) ve.getValue( FacesContext.getCurrentInstance().getELContext() )
			: defaultValue;
	}

	public static String GetFacesMapping(FacesContext context)
	{
		if (context == null)
		{
			throw new NullPointerException("Context not specified.");
		}

		// Check for a previously stored mapping   
		ExternalContext extContext = context.getExternalContext();
		String mapping = (String) extContext.getRequestMap().get(INVOCATION_PATH);

		if (mapping == null)
		{
			Object request = extContext.getRequest();
			String contextPath = null;
			String servletPath = null;
			String pathInfo = null;

			// first check for javax.servlet.forward.servlet_path
			// and javax.servlet.forward.path_info for non-null
			// values.  if either is non-null, use this
			// information to generate determine the mapping.

			if (request instanceof HttpServletRequest)
			{
				contextPath = extContext.getRequestContextPath();
				servletPath = extContext.getRequestServletPath();
				pathInfo = extContext.getRequestPathInfo();
			}

			mapping = FacesUtil.GetMappingForRequest(contextPath, servletPath, pathInfo);
		}

		// if the FacesServlet is mapped to /* throw an 
		// Exception in order to prevent an endless 
		// RequestDispatcher loop
		if ("/*".equals(mapping))
		{
			throw new FacesException("The FacesServlet was configured incorrectly");
		}

		if (mapping != null)
		{
			extContext.getRequestMap().put(INVOCATION_PATH, mapping);
		}

		return mapping;
	}

	/**
	 * <p>Return the appropriate {@link javax.faces.webapp.FacesServlet} mapping
	 * based on the servlet path of the current request.</p>
	 *
	 * @param contextPath the context path for the servlet of the request
	 * @param servletPath the servlet path of the request
	 * @param pathInfo    the path info of the request
	 *
 	 * @return the appropriate mapping based on the current request
	 *
	 * @see HttpServletRequest#getServletPath()
	 */
	private static String GetMappingForRequest(String contextPath, String servletPath, String pathInfo)
	{
		if (contextPath == null || servletPath == null)
		{
			return null;
		}

		// If the path returned by HttpServletRequest.getServletPath()
		// returns a zero-length String, then the FacesServlet has
		// been mapped to '/*'. 
		if (servletPath.length() == 0)
		{
			return contextPath + "/*";
		}

		// presence of path info means we were invoked
		// using a prefix path mapping
		if (pathInfo != null)
		{
			return contextPath + servletPath;
		}
		else if (servletPath.indexOf('.') < 0)
		{
			// if pathInfo is null and no '.' is present, assume the
			// FacesServlet was invoked using prefix path but without
			// any pathInfo - i.e. GET /contextroot/faces or
			// GET /contextroot/faces/
			return contextPath + servletPath;
		}
		else
		{
			// Servlet invoked using extension mapping
			return contextPath + servletPath.substring(servletPath.lastIndexOf('.'));
		}
	}

	/**
	 * <p>Returns true if the provided <code>url-mapping</code> is
	 * a prefix path mapping (starts with <code>/</code>).</p>
	 *
	 * @param mapping a <code>url-pattern</code>
	 * @return true if the mapping starts with <code>/</code>
	 */
	public static boolean IsPrefixMapped(String mapping)
	{
		return (mapping.charAt(0) == '/');
	}

}
