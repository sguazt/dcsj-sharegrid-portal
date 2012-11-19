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

package it.unipmn.di.dcs.sharegrid.web.faces.servlet;

import it.unipmn.di.dcs.sharegrid.web.faces.view.AbstractApplicationBean;
import it.unipmn.di.dcs.sharegrid.web.faces.view.AbstractFragmentBean;
import it.unipmn.di.dcs.sharegrid.web.faces.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.faces.view.AbstractRequestBean;
import it.unipmn.di.dcs.sharegrid.web.faces.view.AbstractSessionBean;
import it.unipmn.di.dcs.sharegrid.web.faces.view.ViewHandlerImpl;

import java.beans.Beans;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.faces.context.FacesContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


/**
 * <p><strong>LifecycleListener</strong> implements the lifecycle startup
 * and shutdown calls (<code>init()</code> and <code>destroy()</code>) for
 * subclasses of {@link AbstractApplicationBean}, {@link AbstractSessionBean},
 * {@link AbstractRequestBean}, {@link AbstractPageBean}, and
 * {@link AbstractFragmentBean}.</p>
 * 
 * <p>It must be registered with the servlet container as a listener,
 * through an entry in either the <code>/WEB-INF/web.xml</code> resource
 * or a tag library descriptor included in the web application.</p>
 *
 * This is inspired to the Sun RAVE APPBASE project.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class LifecycleListener
    implements ServletContextAttributeListener,
               ServletContextListener,
               HttpSessionActivationListener,
               HttpSessionAttributeListener,
               HttpSessionListener,
               ServletRequestAttributeListener,
               ServletRequestListener
{
	/**
	 * <p>Create a new lifecycle listener.</p>
	 */
	public LifecycleListener()
	{      
		// empty
	}

	//@{ ServletContextListener implementation /////////////////////////////

	/**
	 * <p>Respond to a context created event.  No special processing
	 * is required.</p>
	 *
	 * @param event Event to be processed
	 */
	public void contextInitialized(ServletContextEvent event)
	{
		// No processing is required
	}

	/**
	 * Respond to a context destroyed event.  Causes any application
	 * scope attribute that implements {@link AbstractApplicationBean}
	 * to be removed, triggering an <code>attributeRemoved()</code> event.
	 *
	 * @param event Event to be processed
	 */
	public void contextDestroyed(ServletContextEvent event)
	{
		// Remove any AbstractApplicationBean attributes, which will
		// trigger an attributeRemoved event
		ServletContext ctx = event.getServletContext();
		List<String> list = new ArrayList<String>();
		Enumeration names = ctx.getAttributeNames();
		while (names.hasMoreElements())
		{
			String name = (String) names.nextElement();
			list.add(name);
		}
		for (String key : list)
		{
			if (Beans.isDesignTime())
			{
				//[141508] play it safe. keep existing behavior at designtime.
				ctx.removeAttribute(key);
			}
			else
			{
				//[141508] fixed behavior. only remove if instanceof.
				Object value = ctx.getAttribute(key);
				if (value instanceof AbstractApplicationBean)
				{
					ctx.removeAttribute(key);
				}
			}
		}
	}

	//@} ServletContextListener implementation /////////////////////////////

	//@{ ServletContextAttributeListener implementation ////////////////////

	/**
	 * <p>Respond to an application scope attribute being added.  If the
	 * value is an {@link AbstractApplicationBean}, call its
	 * <code>init()</code> method.</p>
	 *
	 * @param event Event to be processed
	 */
	public void attributeAdded(ServletContextAttributeEvent event)
	{
		// If the new value is an AbstractApplicationBean, notify it
		Object value = event.getValue();
		if ((value != null) && (value instanceof AbstractApplicationBean))
		{
			((AbstractApplicationBean) value).init();
		}
	}


	/**
	 * <p>Respond to an application scope attribute being replaced.
	 * If the old value was an {@link AbstractApplicationBean}, call
	 * its <code>destroy()</code> method.  If the new value is an
	 * {@link AbstractApplicationBean}, call its <code>init()</code>
	 * method.</p>
	 *
	 * @param event Event to be processed
	 */
	public void attributeReplaced(ServletContextAttributeEvent event)
	{
		// If the old value is an AbstractApplicationBean, notify it
		Object value = event.getValue();
		if ((value != null) && (value instanceof AbstractApplicationBean))
		{
			((AbstractApplicationBean) value).destroy();
		}

		// If the new value is an AbstractApplicationBean, notify it
		value = event.getServletContext().getAttribute(event.getName());
		if ((value != null) && (value instanceof AbstractApplicationBean))
		{
			((AbstractApplicationBean) value).init();
		}
	}

	/**
	 * <p>Respond to an application scope attribute being removed.
	 * If the old value was an {@link AbstractApplicationBean}, call
	 * its <code>destroy()</code> method.</p>
	 *
	 * @param event Event to be processed
	 */
	public void attributeRemoved(ServletContextAttributeEvent event)
	{
		// If the old value is an AbstractApplicationBean, notify it
		Object value = event.getValue();
		if ((value != null) && (value instanceof AbstractApplicationBean))
		{
			((AbstractApplicationBean) value).destroy();
		}
	}

	//@} ServletContextAttributeListener implementation ////////////////////

	//@{ HttpSessionListener implementation ////////////////////////////////

	/**
	 * <p>Respond to a session created event.  No special processing
	 * is required.</p>
	 *
	 * @param event Event to be processed
	 */
	public void sessionCreated(HttpSessionEvent event)
	{
		// No processing is required
	}

	/**
	 * Respond to a session destroyed event.  Causes any session
	 * scope attribute that implements {@link AbstractSessionBean}
	 * to be removed, triggering an <code>attributeRemoved()</code> event.
	 *
	 * @param event Event to be processed
	 */
	public void sessionDestroyed(HttpSessionEvent event)
	{
		// Remove any AbstractSessionBean attributes, which will
		// trigger an attributeRemoved event
		try
		{
			HttpSession session = event.getSession();
			List<String> list = new ArrayList<String>();
			Enumeration names = session.getAttributeNames();
			while (names.hasMoreElements())
			{
				String name = (String) names.nextElement();
				list.add(name);
			}
			for (String key : list)
			{
				if (Beans.isDesignTime())
				{
					//[141508] play it safe. keep existing behavior at designtime.
					session.removeAttribute(key);
				}
				else
				{
					//[141508] fixed behavior. only remove if instanceof.
					Object value = session.getAttribute(key);
					if (value instanceof AbstractSessionBean)
					{
						session.removeAttribute(key);
					}
				}
			}
		}
		catch (IllegalStateException e)
		{
			// [6365605] The session was already invalidated, most likely
			// because we are running on a Servlet 2.3 container.  That means
			// the attributes should have already been removed, so there is
			// nothing more for us to do here, so swallow the exception.
		}
	}

	//@} HttpSessionListener implementation ////////////////////////////////

	//@{ HttpSessionActivationListener implementation //////////////////////

	/**
	 * <p>Respond to a "session will passivate" event.  Notify all session
	 * scope attributes that are {@link AbstractSessionBean}s.</p>
	 *
	 * @param event Event to be processed
	 */
	public void sessionWillPassivate(HttpSessionEvent event)
	{
		// Notify any AbstractSessionBean attributes
		Enumeration names = event.getSession().getAttributeNames();
		while (names.hasMoreElements())
		{
			String name = (String) names.nextElement();
			Object value = event.getSession().getAttribute(name);
			if ((value != null) && (value instanceof AbstractSessionBean))
			{
				((AbstractSessionBean) value).passivate();
			}
		}
	}


	/**
	 * <p>Respond to a "session did activate" event.  Notify all session
	 * scope attributes that are {@link AbstractSessionBean}s.</p>
	 *
	 * @param event Event to be processed
	 */
	public void sessionDidActivate(HttpSessionEvent event)
	{
		// Notify any AbstractSessionBean attributes
		Enumeration names = event.getSession().getAttributeNames();
		while (names.hasMoreElements())
		{
			String name = (String) names.nextElement();
			Object value = event.getSession().getAttribute(name);
			if ((value != null) && (value instanceof AbstractSessionBean))
			{
				((AbstractSessionBean) value).activate();
			}
		}
	}

	//@} HttpSessionActivationListener implementation //////////////////////

	//@{ HttpSessionAttributeListener implementation ///////////////////////

	/**
	 * <p>Respond to a session scope attribute being added.  If the
	 * value is an {@link AbstractSessionBean}, call its
	 * <code>init()</code> method.</p>
	 *
	 * @param event Event to be processed
	 */
	public void attributeAdded(HttpSessionBindingEvent event)
	{
		// If the new value is an AbstractSessionBean, notify it
		Object value = event.getValue();
		if ((value != null) && (value instanceof AbstractSessionBean))
		{
			((AbstractSessionBean) value).init();
		}
	}


	/**
	 * <p>Respond to a session scope attribute being replaced.
	 * Provided that the old and new values are not the same object,
	 * if the old value was an {@link AbstractSessionBean}, call
	 * its <code>destroy()</code> method; if the new value is an
	 * {@link AbstractSessionBean}, call its <code>init()</code>
	 * method. No-op if the old and new values are the same object.</p>
	 *
	 * @param event Event to be processed
	 */
	public void attributeReplaced(HttpSessionBindingEvent event)
	{
		//125789: we might be replacing the session attribute with itself
		//in order to assure propagation in a clustered environment.
		//in such a case, do not call init and destroy on the object
		Object oldValue = event.getValue();;
		Object newValue = event.getSession().getAttribute(event.getName());
		if (oldValue == newValue)
		{
			return;
		}

		// If the old value is an AbstractSessionBean, notify it
		Object value = oldValue;
		if ((value != null) && (value instanceof AbstractSessionBean))
		{
			((AbstractSessionBean) value).destroy();
		}

		// If the new value is an AbstractSessionBean, notify it
		value = newValue;
		if ((value != null) && (value instanceof AbstractSessionBean))
		{
			((AbstractSessionBean) value).init();
		}
	}

	/**
	 * <p>Respond to a session scope attribute being removed.
	 * If the old value was an {@link AbstractSessionBean}, call
	 * its <code>destroy()</code> method.</p>
	 *
	 * @param event Event to be processed
	 */
	public void attributeRemoved(HttpSessionBindingEvent event)
	{
		// If the old value is an AbstractSessionBean, notify it
		Object value = event.getValue();
		if ((value != null) && (value instanceof AbstractSessionBean))
		{
			((AbstractSessionBean) value).destroy();
		}
	}

	//@} HttpSessionAttributeListener implementation ///////////////////////

	//@{ ServletRequestListener implementation /////////////////////////////

	/**
	 * <p>Respond to a request created event.  No special processing
	 * is required.</p>
	 *
	 * @param event Event to be processed
	 */
	public void requestInitialized(ServletRequestEvent event)
	{
		// No processing is required
	}


	/**
	 * Respond to a request destroyed event.
	 *
	 * Causes any request scope attribute that implements
	 * {@link AbstractRequestBean}, {@link AbstractPageBean}, or
	 * {@link AbstractFragmentBean} to be removed, triggering an
	 * {@code attributeRemoved()} event.
	 *
	 * @param event Event to be processed
	 */
	public void requestDestroyed(ServletRequestEvent event)
	{
		// Remove any AbstractRequestBean, AbstractPageBean,
		// or AbstractFragmentBean attributes, which will trigger an
		// attributeRemoved event
		ServletRequest req = event.getServletRequest();
		List<String> list = new ArrayList<String>();
		Enumeration names = req.getAttributeNames();
		while (names.hasMoreElements())
		{
			String name = (String) names.nextElement();
			list.add(name);
		}
		for (String key : list)
		{
			if (Beans.isDesignTime())
			{
				//[141508] play it safe. keep existing behavior at designtime.
				req.removeAttribute(key);
			}
			else
			{
				//[141508] fixed behavior. only remove if instanceof.
				Object value = req.getAttribute(key);
				if (
					(value instanceof AbstractRequestBean)
					|| (value instanceof AbstractPageBean)
					|| (value instanceof AbstractFragmentBean)
				) {
					req.removeAttribute(key);
				}
			}
		}
	}

	//@} ServletRequestListener implementation /////////////////////////////

	//@{ ServletRequestAttributeListener implementation ////////////////////

	/**
	 * <p>Respond to a request scope attribute being added.  If the
	 * value is an {@link AbstractPageBean}, {@link AbstractRequestBean},
	 * or {@link AbstractFragmentBean}, call its <code>init()</code> method.
	 * </p>
	 *
	 * @param event Event to be processed
	 */
	public void attributeAdded(ServletRequestAttributeEvent event)
	{
		Object value = event.getValue();
		if (value != null)
		{
			if (value instanceof AbstractFragmentBean)
			{
				this.fireInit((AbstractFragmentBean) value);
			}
			else if (value instanceof AbstractPageBean)
			{
				this.fireInit((AbstractPageBean) value);
			}
			else if (value instanceof AbstractRequestBean)
			{
				this.fireInit((AbstractRequestBean) value);
			}
		}
	}


	/**
	 * <p>Respond to a request scope attribute being replaced.
	 * If the old value was an {@link AbstractPageBean},
	 * {@link AbstractRequestBean} or {@link AbstractFragmentBean},
	 * call its <code>destroy()</code> method.  If the new value is an
	 * {@link AbstractPageBean}, {@link AbstractRequestBean} or
	 * {@link AbstractFragmentBean}, call its <code>init()</code> method.</p>
	 *
	 * @param event Event to be processed
	 */
	public void attributeReplaced(ServletRequestAttributeEvent event)
	{
		Object value = event.getValue();
		if (value != null)
		{
			if (value instanceof AbstractFragmentBean)
			{
				this.fireDestroy((AbstractFragmentBean) value);
			}
			else if (value instanceof AbstractPageBean)
			{
				this.fireDestroy((AbstractPageBean) value);
			}
			else if (value instanceof AbstractRequestBean)
			{
				this.fireDestroy((AbstractRequestBean) value);
			}
		}

		value = event.getServletRequest().getAttribute(event.getName());
		if (value != null)
		{
			if (value instanceof AbstractFragmentBean)
			{
				this.fireInit((AbstractFragmentBean) value);
			}
			else if (value instanceof AbstractPageBean)
			{
				this.fireInit((AbstractPageBean) value);
			}
			else if (value instanceof AbstractRequestBean)
			{
				this.fireInit((AbstractRequestBean) value);
			}
		}
	}

	/**
	 * <p>Respond to a request scope attribute being removed.
	 * If the old value was an {@link AbstractPageBean},
	 * {@link AbstractRequestBean} or {@link AbstractFragmentBean},
	 * call its <code>destroy()</code> method.</p>
	 *
	 * @param event Event to be processed
	 */
	public void attributeRemoved(ServletRequestAttributeEvent event)
	{
		Object value = event.getValue();
		if (value != null)
		{
			if (value instanceof AbstractFragmentBean)
			{
				this.fireDestroy((AbstractFragmentBean) value);
			}
			else if (value instanceof AbstractPageBean)
			{
				this.fireDestroy((AbstractPageBean) value);
			}
			else if (value instanceof AbstractRequestBean)
			{
				this.fireDestroy((AbstractRequestBean) value);
			}
		}
	}

	//@} ServletRequestAttributeListener implementation ////////////////////

	//@{ Private Methods ///////////////////////////////////////////////////////

	/**
	 * <p>Fire a destroy event on an AbstractFragmentBean.</p>
	 *
	 * @param bean {@link AbstractFragmentBean} to fire event on
	 */
	private void fireDestroy(AbstractFragmentBean bean)
	{
		try
		{
			bean.destroy();
		}
		catch (Exception e)
		{
			log(e.getMessage(), e);
			ViewHandlerImpl.cache(FacesContext.getCurrentInstance(), e);
		}
	}

	/**
	 * <p>Fire a destroy event on an AbstractPageBean.</p>
	 *
	 * @param bean {@link AbstractPageBean} to fire event on
	 */
	private void fireDestroy(AbstractPageBean bean)
	{
		try
		{
			bean.destroy();
		}
		catch (Exception e)
		{
			log(e.getMessage(), e);
			ViewHandlerImpl.cache(FacesContext.getCurrentInstance(), e);
		}
	}

	/**
	 * <p>Fire a destroy event on an AbstractRequestBean.</p>
	 *
	 * @param bean {@link AbstractRequestBean} to fire event on
	 */
	private void fireDestroy(AbstractRequestBean bean)
	{
		try
		{
			bean.destroy();
		}
		catch (Exception e)
		{
			log(e.getMessage(), e);
			ViewHandlerImpl.cache(FacesContext.getCurrentInstance(), e);
		}
	}

	/**
	 * <p>Fire an init event on an AbstractFragmentBean.</p>
	 *
	 * @param bean {@link AbstractFragmentBean} to fire event on
	 */
	private void fireInit(AbstractFragmentBean bean)
	{
		try
		{
			bean.init();
		}
		catch (Exception e)
		{
			log(e.getMessage(), e);
			ViewHandlerImpl.cache(FacesContext.getCurrentInstance(), e);
		}
	}

	/**
	 * <p>Fire an init event on an AbstractPageBean.</p>
	 *
	 * @param bean {@link AbstractPageBean} to fire event on
	 */
	private void fireInit(AbstractPageBean bean)
	{
		try
		{
			ViewHandlerImpl.record(FacesContext.getCurrentInstance(), bean);
			bean.init();
		}
		catch (Exception e)
		{
			log(e.getMessage(), e);
			ViewHandlerImpl.cache(FacesContext.getCurrentInstance(), e);
		}
	}

	/**
	 * <p>Fire an init event on an AbstractRequestBean.</p>
	 *
	 * @param bean {@link AbstractRequestBean} to fire event on
	 */
	private void fireInit(AbstractRequestBean bean)
	{
		try
		{
			bean.init();
		}
		catch (Exception e)
		{
			log(e.getMessage(), e);
			ViewHandlerImpl.cache(FacesContext.getCurrentInstance(), e);
		}
	}

	/**
	 * <p>Log the specified message via <code>FacesContext</code> if it is
	 * not null, or directly to the container otherwise.</p>
	 *
	 * @param message Message to be logged
	 */
	private void log(String message)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null)
		{
			context.getExternalContext().log(message);
		}
		else
		{
			System.out.println(message);
		}
	}

	/**
	 * <p>Log the specified message and exception via <code>FacesContext</code>
	 * if it is not null, or directly to the container otherwise.</p>
	 *
	 * @param message Message to be logged
	 * @param throwable Exception to be logged
	 */
	private void log(String message, Throwable throwable)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null)
		{
			context.getExternalContext().log(message);
		}
		else
		{
			System.out.println(message);
		}
	}

	//@} Private Methods ///////////////////////////////////////////////////////
}
