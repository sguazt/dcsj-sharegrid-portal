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
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
//import javax.faces.el.ValueBinding;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class for JSF messages.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public final class MessageUtil
{
	/** A constructor. */
	private MessageUtil()
	{
		// empty
	}

	/**
	 * @see #GetMessage(String, Object...)
	 *
	 * @param messageId The message id.
	 * @param severity Set a custom severity.
	 * @param params Additional message parameters.
	 */
	public static FacesMessage GetMessage(String messageId, FacesMessage.Severity severity, Object... params)
	{
		FacesMessage message = GetMessage(messageId, params);
		message.setSeverity(severity);
		return message;
	}

	/**
	 * @see #GetMessage(Locale, String, Object...)
	 *
	 * @param locale The locale for this message.
	 * @param messageId The message id.
	 * @param severity Set a custom severity.
	 * @param params Additional message parameters.
	 */
	public static FacesMessage GetMessage(Locale locale, String messageId, FacesMessage.Severity severity, Object... params)
	{
		FacesMessage message = GetMessage(locale, messageId, params);
		message.setSeverity(severity);
		return message;
	}

	/**
	 * @see #GetMessage(FacesContext, String, Object...)
	 *
	 * @param context The context for this message.
	 * @param messageId The message id.
	 * @param severity Set a custom severity.
	 * @param params Additional message parameters.
	 */
	public static FacesMessage GetMessage(FacesContext context, String messageId, FacesMessage.Severity severity, Object... params)
	{
		FacesMessage message = GetMessage(context, messageId, params);
		message.setSeverity(severity);
		return message;
	}

	/**
	 * <p>This version of GetMessage() is used for localizing implementation
	 * specific messages.</p>
	 *
	 * @param messageId - the key of the message in the resource bundle
	 * @param params    - substittion parameters
	 *
	 * @return a localized <code>FacesMessage</code> with the severity
	 *  of FacesMessage.SEVERITY_ERROR
	 */
	public static FacesMessage GetMessage(String messageId, Object... params)
	{
		Locale locale = null;
		FacesContext context = FacesContext.getCurrentInstance();
		// context.getViewRoot() may not have been initialized at this point.
		if (context != null && context.getViewRoot() != null)
		{
			locale = context.getViewRoot().getLocale();
			if (locale == null)
			{
				locale = Locale.getDefault();
			}
		}
		else
		{
			locale = Locale.getDefault();
		}

		return GetMessage(locale, messageId, params);
	}

	/**
	 * <p>Creates and returns a FacesMessage for the specified Locale.</p>
	 *
	 * @param locale    - the target <code>Locale</code>
	 * @param messageId - the key of the message in the resource bundle
	 * @param params    - substittion parameters
	 *
	 * @return a localized <code>FacesMessage</code> with the severity
	 *  of FacesMessage.SEVERITY_ERROR
	 */
	public static FacesMessage GetMessage(Locale locale, String messageId, Object... params)
	{
		String summary = null;
		String detail = null;       
		ResourceBundle bundle;
		String bundleName;

		// see if we have a user-provided bundle
		if (null != (bundleName = getApplication().getMessageBundle()))
		{
			if (null != (bundle = ResourceBundle.getBundle(bundleName, locale, getCurrentLoader(bundleName))))
			{
				// see if we have a hit
				try
				{
					summary = bundle.getString(messageId);
					detail = bundle.getString(messageId + "_detail");
				}
				catch (MissingResourceException e)
				{
					// ignore
				}
			}
		}

		// we couldn't find a summary in the user-provided bundle
		if (null == summary)
		{
			// see if we have a summary in the app provided bundle
			bundle = ResourceBundle.getBundle(FacesMessage.FACES_MESSAGES, locale, getCurrentLoader(bundleName));
			if (null == bundle)
			{
				throw new NullPointerException();
			}
			// see if we have a hit
			try
			{
				summary = bundle.getString(messageId);
				// we couldn't find a summary anywhere!  Return null
				if (null == summary)
				{
					return null;
				}
				detail = bundle.getString(messageId + "_detail");
			}
			catch (MissingResourceException e)
			{
				// ignore
			}
		}
		// At this point, we have a summary and a bundle.     
		FacesMessage ret = new BindingFacesMessage(locale, summary, detail, params);
		ret.setSeverity(FacesMessage.SEVERITY_ERROR);
		return (ret);
	}

	/**
	 * <p>Creates and returns a FacesMessage for the specified Locale.</p>
	 *
	 * @param context   - the <code>FacesContext</code> for the current request
	 * @param messageId - the key of the message in the resource bundle
	 * @param params    - substittion parameters
	 *
	 * @return a localized <code>FacesMessage</code> with the severity
	 *  of FacesMessage.SEVERITY_ERROR
	 */
	public static FacesMessage GetMessage(FacesContext context, String messageId, Object... params)
	{
		if (context == null || messageId == null )
		{
			throw new NullPointerException(" context " + context + " messageId " + messageId);
		}
		Locale locale;
		// viewRoot may not have been initialized at this point.
		if (context.getViewRoot() != null)
		{
			locale = context.getViewRoot().getLocale();
		}
		else
		{
			locale = Locale.getDefault();
		}

		if (null == locale)
		{
			throw new NullPointerException(" locale is null ");
		}

		FacesMessage message = GetMessage(locale, messageId, params);
		if (message != null)
		{
			return message;
		}
		locale = Locale.getDefault();
		return (GetMessage(locale, messageId, params));
	}  

	/**
	 * <p>Returns the <code>label</code> property from the specified
	 * component.</p>
	 *
	 * @param context   - the <code>FacesContext</code> for the current request
	 * @param component - the component of interest
	 *
	 * @return the label, if any, of the component
	 */
	public static Object getLabel(FacesContext context, UIComponent component)
	{
		Object o = component.getAttributes().get("label");
		if (o == null || (o instanceof String && ((String) o).length() == 0))
		{
			o = component.getValueExpression("label");
		}
		// Use the "clientId" if there was no label specified.
		if (o == null)
		{
			o = component.getClientId(context);
		}
		return o;
	}

	/** Returns the {@code Application} object. */
	protected static Application getApplication()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null)
		{
			return (FacesContext.getCurrentInstance().getApplication());
		}
		ApplicationFactory afactory = (ApplicationFactory)
		FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		return (afactory.getApplication());
	}

	/** Returns the {@code ClassLoader} object. */
	protected static ClassLoader getCurrentLoader(Object fallbackClass)
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null)
		{
			loader = fallbackClass.getClass().getClassLoader();
		}
		return loader;
	}

	/**
	 * This class overrides FacesMessage to provide the evaluation
	 * of binding expressions in addition to Strings.
	 * It is often the case, that a binding expression may reference
	 * a localized property value that would be used as a 
	 * substitution parameter in the message.  For example:
	 *  <code>#{bundle.userLabel}</code>
	 * "bundle" may not be available until the page is rendered.
	 * The "late" binding evaluation in <code>getSummary</code> and 
	 * <code>getDetail</code> allow the expression to be evaluated
	 * when that property is available.
	 *
	 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
	 */
	static class BindingFacesMessage extends FacesMessage
	{
		private Locale locale;
		private Object[] parameters;
		private Object[] resolvedParameters;

		BindingFacesMessage(Locale locale, String messageFormat, String detailMessageFormat, Object[] parameters)
		{
			// parameters: array of parameters, both Strings and ValueBindings

			super(messageFormat, detailMessageFormat);

			this.locale = locale;
			this.parameters = parameters;
			if (parameters != null)
			{
				resolvedParameters = new Object[parameters.length];
			}
		}

		public String getSummary()
		{
			String pattern = super.getSummary();
			resolveBindings();
			return getFormattedString(pattern, resolvedParameters);
		}

		public String getDetail()
		{
			String pattern = super.getDetail();
			resolveBindings();
			return getFormattedString(pattern, resolvedParameters);
		}

		private void resolveBindings()
		{
			FacesContext context = null;
			if (parameters != null)
			{
				for (int i = 0; i < parameters.length; i++)
				{
					Object o = parameters[i];
					//if (o instanceof ValueBinding)
					//{
					//	if (context == null)
					//	{
					//		context = FacesContext.getCurrentInstance();
					//	}
					//	o = ((ValueBinding) o).getValue(context);
					//}
					if (o instanceof ValueExpression)
					{
						if (context == null)
						{
							context = FacesContext.getCurrentInstance();
						}
						o = ((ValueExpression) o).getValue(context.getELContext());
					}
					// to avoid 'null' appearing in message
					if (o == null)
					{
						o = "";
					}
					resolvedParameters[i] = o;
				}
			}
		}

		private String getFormattedString(String msgtext, Object[] params)
		{
			String localizedStr = null;

			if (params == null || msgtext == null )
			{
				return msgtext;
			}
			StringBuffer b = new StringBuffer(100);
			MessageFormat mf = new MessageFormat(msgtext);
			if (locale != null)
			{
				mf.setLocale(locale);
				b.append(mf.format(params));
				localizedStr = b.toString();
			}
			return localizedStr;
		}
	}
}
