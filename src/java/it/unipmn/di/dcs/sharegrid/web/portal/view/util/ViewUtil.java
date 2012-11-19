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

import it.unipmn.di.dcs.sharegrid.web.portal.view.SessionBean;
import it.unipmn.di.dcs.sharegrid.web.faces.util.MessageUtil;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Utilty class for page views.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ViewUtil
{
	/**
	 * Wraps (if necessarily) a generic exception with a new one of type
	 * <code>FacesException</code>.
	 * From a JSF page it is more convenient to throw a 
	 * <code>FacesException</code>.
	 */
	public static void WrapException(Exception e) throws FacesException
	{
		throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
	}

	public static void AddExceptionMessage(Exception e)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(
				"[FATAL ERROR] "
				+ e
				+ ( e.getCause() != null ? (" (Cause: " + e.getCause() + ")") : "" )
		);
		message.setSeverity( FacesMessage.SEVERITY_FATAL );
		context.addMessage( null, message );
	}

	public static void AddInfoMessage(FacesContext context, ResourceBundle bundle, String compId, String messageId, Object... params)
	{
		FacesMessage message = MessageUtil.GetMessage(
			context,
			messageId,
			FacesMessage.SEVERITY_INFO,
			params
		);

//		FacesMessage message = new FacesMessage(
//			bundle.getString(messageId)
//		);
//		message.setSeverity( FacesMessage.SEVERITY_INFO );
		context.addMessage( compId, message );
	}

	public static void AddWarnMessage(FacesContext context, ResourceBundle bundle, String compId, String messageId, Object... params)
	{
		FacesMessage message = MessageUtil.GetMessage(
			context,
			messageId,
			FacesMessage.SEVERITY_WARN,
			params
		);

		context.addMessage( compId, message );
	}

	public static void AddErrorMessage(FacesContext context, ResourceBundle bundle, String compId, String messageId, Object... params)
	{
		FacesMessage message = MessageUtil.GetMessage(
			context,
			messageId,
			FacesMessage.SEVERITY_ERROR,
			params
		);

		context.addMessage( compId, message );
	}

	public static void AddInfoMessage(FacesContext context, String compId, String messageId, Object... params)
	{
		ResourceBundle bundle = ResourceBundle.getBundle(
			context.getApplication().getMessageBundle()
		);

		ViewUtil.AddInfoMessage( context, bundle, compId, messageId, params );
	}

	public static void AddWarnMessage(FacesContext context, String compId, String messageId, Object... params)
	{
		ResourceBundle bundle = ResourceBundle.getBundle(
			context.getApplication().getMessageBundle()
		);

		ViewUtil.AddWarnMessage( context, bundle, compId, messageId, params );
	}

	public static void AddErrorMessage(FacesContext context, String compId, String messageId, Object... params)
	{
		ResourceBundle bundle = ResourceBundle.getBundle(
			context.getApplication().getMessageBundle()
		);

		ViewUtil.AddErrorMessage( context, bundle, compId, messageId, params );
	}

	public static void AddInfoMessage(FacesContext context, String messageId, Object... params)
	{
		ViewUtil.AddInfoMessage( context, null, messageId, params );
	}

	public static void AddWarnMessage(FacesContext context, String messageId, Object... params)
	{
		ViewUtil.AddWarnMessage( context, null, messageId, params );
	}

	public static void AddErrorMessage(FacesContext context, String messageId, Object... params)
	{
		ViewUtil.AddErrorMessage( context, null, messageId, params );
	}

	public static void AddInfoMessage(String messageId, Object... params)
	{
		ViewUtil.AddInfoMessage( FacesContext.getCurrentInstance(), null, messageId, params );
	}

	public static void AddWarnMessage(String messageId, Object... params)
	{
		ViewUtil.AddWarnMessage( FacesContext.getCurrentInstance(), null, messageId, params );
	}

	public static void AddErrorMessage(String messageId, Object... params)
	{
		ViewUtil.AddErrorMessage( FacesContext.getCurrentInstance(), null, messageId, params );
	}

	public static void RedirectPage(String address) throws Exception
	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if ( facesCtx == null )
		{
			// this thread no longer has a FacesContext instance.
			throw new Exception("Cannot redirect: current thread no longer has a FacesContext instance.");
		}

		ExternalContext extCtx = facesCtx.getExternalContext();
		if ( extCtx == null )
		{
			// this thread no longer has a ExternalContext instance.
			throw new Exception("Cannot redirect: current thread no longer has a ExternalContext instance.");
		}

		extCtx.redirect(address);
	}

	/**
 	 * Returns the current session of <code>null</code> if it doesn't exist.
 	 */
	public static HttpSession GetHttpSession() throws Exception
	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if ( facesCtx == null )
		{
			// this thread no longer has a FacesContext instance.
			return null;
		}

		ExternalContext extCtx = facesCtx.getExternalContext();
		if ( extCtx == null )
		{
			// this thread no longer has a ExternalContext instance.
			return null;
		}

		HttpSession session = (HttpSession) extCtx.getSession(false);

		return session;
	}

	@SuppressWarnings("unchecked")
	public static <T> T GetHttpSessionAttribute(String attrName) throws Exception
	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if ( facesCtx == null )
		{
			// this thread no longer has a FacesContext instance.
			return null;
		}

		ExternalContext extCtx = facesCtx.getExternalContext();
		if ( extCtx == null )
		{
			// this thread no longer has a ExternalContext instance.
			return null;
		}

		T attr = null;
		try
		{
			attr = (T) extCtx.getSessionMap().get(attrName);
		}
		catch (ClassCastException cce)
		{
			// ignore
		}

		return attr;
	}

	public static SessionBean GetSessionBean()
	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if ( facesCtx == null )
		{
			// this thread no longer has a FacesContext instance.
			return null;
		}

		// deprecated
		//return (SessionBean) facesCtx.getApplication().getVariableResolver().resolveVariable( facesCtx, "sessionBean" );
		return (SessionBean) facesCtx.getELContext().getELResolver().getValue( facesCtx.getELContext(), null, "sessionBean" );
		// alternative
		//return (SessionBean) facesCtx.getApplication().getExpressionFactory().createValueExpression( facesCxt.getELContext(), "#{sessionBean}", SessionBean.class ).getValue( context.getELContext() );
	}

	public static String GetServletUrl(ExternalContext extCtx)
	{
		String url = null;

		ServletRequest req = (ServletRequest) extCtx.getRequest();
		//ServletContext svlCtx = (ServletContext) extCtx.getContext();

		try
		{
			if ( req instanceof HttpServletRequest )
			{
				HttpServletRequest httpReq = (HttpServletRequest) req;

				URL reqUrl = new URL( httpReq.getRequestURL().toString() );
				url = new URL(
					httpReq.getScheme()
					//url.getProtocol()
					+ "://"
					+ reqUrl.getAuthority()
//					+ svlCtx.getContextPath()
					+ extCtx.getRequestContextPath()
					+ extCtx.getRequestServletPath()
				).toString();
			}
			else
			{
				url = new URL(
					req.getScheme()
					+ "://"
					+ req.getLocalName()
					+ ":" + req.getLocalPort()
//					+ svlCtx.getContextPath()
					+ extCtx.getRequestContextPath()
					+ extCtx.getRequestServletPath()
				).toString();
			}
		}
		catch (Exception e)
		{
			// Ignore
		}

		return url;
	}

	protected static void SetNoCacheResponse(HttpServletResponse response)
	{
		response.setHeader("Cache-Control","no-cache"); 
		response.setHeader("Cache-Control","no-store");
		response.setHeader("Cache-Control","must-revalidate");
		response.setDateHeader("Expires", 0);
		response.setHeader("Pragma","no-cache");
	}

/*
	@Deprecated
	public static <T> T GetSessionBean(String valueExprStr) throws Exception
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application app = facesContext.getApplication();
		return (T) app.createValueBinding("#{" + valueExprStr + "}").getValue(facesContext);
	}
*/
/*
	public static void GetDbConnection()
	{
		java.sql.Connection conn = java.sql.DriverManager.getConnection(getDbURL(), getDbUserName(),  ̄getDbPassword());

		Context ctx = new InitialContext();
		if (ctx != null)
		{
			Context envContext = (Context) ctx.lookup("java:/comp/env");
			if (envContext != null)
			{
				javax.sql.DataSource ds = (javax.sql.DataSource) envContext.lookup("jdbc/mysql");
				if (ds != null)
				{
					conn = ds.getConnection();
				}
			}
		}
	}
*/
}
