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

package it.unipmn.di.dcs.sharegrid.web.faces.view;

import it.unipmn.di.dcs.sharegrid.web.faces.servlet.LifecycleListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.el.ELResolver;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
//import javax.faces.el.ValueBinding;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import javax.servlet.ServletContext;

/**
 * <p>ViewHandler implementation that allows events to be triggered upon the
 * occurrence of specific ViewHandler method calls.  This implementation also
 * posts relevant lifecycle events to initialized page beans, so it also
 * implements <code>PhaseListener</code>.</p>
 *
 * This is inspired to the Sun RAVE APPBASE project.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ViewHandlerImpl extends ViewHandler implements PhaseListener
{
	/**
	 * <p>Request attribute key under which a <code>List</code> of any
	 * <code>Exception</code>s thrown by a page bean event handler,
	 * and then logged and swallowed, will be cached.  Application
	 * logic can check for such exceptions (perhaps during the
	 * <code>destroy()</code> method), to invoke application specific
	 * error processing.</p>
	 */
	public static final String CACHED_EXCEPTIONS = "it.unipmn.di.dcs.sharegrid.web.faces.CACHED_EXCEPTIONS";

	/**
	 * <p>The <code>UIViewRoot</code> attribute under which we store
	 * <code>Boolean.TRUE</code> when <code>createView()</code> is
	 * called.  This can be used by the <code>isPostBack()</code>
	 * method to determine whether this view was restored (no such
	 * attribute present) and a postback is happening, or whether
	 * this view was created (no postback is happening).</p>
	 */
	public static final String CREATED_VIEW = "it.unipmn.di.dcs.sharegrid.web.faces.CREATED_VIEW"; //NOI18N

	/**
	 * <p>Request attribute key under which a <code>List</code> of the
	 * {@link AbstractPageBean}s that have been created for the current
	 * request are stored.  Typically, there will be either one or two
	 * page beans on this list, depending on whether page navigation has
	 * taken place or not, but will be more if/when static or dynamic
	 * includes are used.</p>
	 */
	public static final String PAGE_BEANS_CREATED = "it.unipmn.di.dcs.sharegrid.web.faces.PAGE_BEANS_CREATED"; //NOI18N

	/**
	 * <p>The ViewHandler instance to which we delegate operations.</p>
	 */
	private ViewHandler handler = null;

	/**
	 * <p>The cached <code>Lifecycle</code> instance for this application.</p>
	 */
	private Lifecycle lifecycle = null;

	/**
	 * <p>The {@link PageBeanMapper} used to identify the page bean that
	 * corresponds to a view identifier.  This instance is lazily instantiated,
	 * so use <code>pageBeanMapper()</code> to acquire a reference.</p>
	 */
	private PageBeanMapper mapper = null;

	/**
	 * <p>Flag indicating whether we have been registered as a phase
	 * listener with the application <code>Lifecycle</code> instance
	 * yet.  This registration needs to be performed lazily, rather
	 * than in our constructor, in case the <code>Lifecycle</code>
	 * instance is replaced by a customized version (as will occur when
	 * using the JSF-Portlet Bridge).</p>
	 */
	private boolean registered = false;

	/**
	 * <p>Construct a new {@link ViewHandlerImpl} that delegates to the
	 * specified <code>ViewHandler</code> instance.</p>
	 *
	 * @param handler The ViewHandler instance to which we will delegate
	 */
	public ViewHandlerImpl(ViewHandler handler)
	{
		this.handler = handler;
	}

	/**
	 * <p>Return an appropriate <code>Locale</code> to use for this
	 * and subsequent requests for the current client.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 *
	 * @exception NullPointerException if <code>context</code>
	 *  is <code>null</code>
	 */
	@Override
	public Locale calculateLocale(FacesContext context)
	{
		return this.handler.calculateLocale(context);
	}

	/**
	 * <p>Return an appropriate <code>RenderKit</code> identifier
	 * for this and subsequent requests from the current
	 * client.
	 *
	 * @param context <code>FacesContext</code> for the current request
	 *
	 * @exception NullPointerException if <code>context</code>
	 *  is <code>null</code>
	 */
	@Override
	public String calculateRenderKitId(FacesContext context)
	{
		return this.handler.calculateRenderKitId(context);
	}

	/**
	 * <p>Create and return a new <code>UIViewRoot</code> instance
	 * initialized with information from this <code>FacesContext</code>
	 * for the specified <code>viewId</code>.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param viewId View identifier of the view to be created
	 *
	 * @exception NullPointerException if <code>context</code>
	 *  or <code>viewId</code> is <code>null</code>
	 */
	@Override
	public UIViewRoot createView(FacesContext context, String viewId)
	{
		this.register();
		UIViewRoot viewRoot = this.handler.createView(context, viewId);
		viewRoot.getAttributes().put(CREATED_VIEW, Boolean.TRUE);
		return viewRoot;
	}

	/**
	 * <p>Return a URL suitable for rendering that selects the
	 * specified view identifier.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param viewId View identifier of the desired view
	 *
	 * @exception NullPointerException if <code>context</code>
	 *  or <code>viewId</code> is <code>null</code>
	 */
	@Override
	public String getActionURL(FacesContext context, String viewId)
	{
		return this.handler.getActionURL(context, viewId);
	}


	/**
	 * <p>Return a URL suitable for rendering that selects the
	 * specified resource.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param path Context-relative resource path to reference
	 *
	 * @exception NullPointerException if <code>context</code>
	 *  or <code>path</code> is <code>null</code>
	 */
	@Override
	public String getResourceURL(FacesContext context, String path)
	{
		return this.handler.getResourceURL(context, path);
	}


	/**
	 * <p>Perform the necessary actions to render the specified view
	 * as part of the current response.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param viewRoot View to be rendered
	 *
	 * @exception NullPointerException if <code>context</code>
	 *  or <code>viewRoot</code> is <code>null</code>
	 */
	@Override
	public void renderView(FacesContext context, UIViewRoot viewRoot) throws IOException, FacesException
	{
		// Set up our page bean, if this has not yet been done
		this.register();
		int count = this.recordedCount(context);
		AbstractPageBean pageBean = this.pageBean(context);
		if (pageBean != null)
		{
			// If our page bean was just now created, that means we were
			// called from Render Response phase.  Therefore, we'll
			// fake a "before Render Response" event for symmetry with the
			// fact that an "after Render Response" event is going to get
			// fired later on
			if (this.recordedCount(context) > count)
			{
				try
				{
					pageBean.beforePhase(
						new PhaseEvent(
							context,
							PhaseId.RENDER_RESPONSE,
							this.lifecycle()
						)
					);
				}
				catch (RuntimeException e)
				{
					context.getExternalContext().log(e.getMessage(), e);
					ViewHandlerImpl.cache(context, e);
				}
			}

			// Fire the prerender() callback event
			this.prerender(context, pageBean);
		}

		// If we have cached any exceptions already, call cleanup()
		// (which will also cause an ApplicationException wrapping them
		// to be thrown).
		if (this.cached(context) != null)
		{
			this.cleanup(context);
			return;
		}

		// Render the specified view, calling cleanup() if any exception
		// is thrown (which will also cause an ApplicationException
		// wrapping it to be thrown).
		try
		{
			if (!context.getResponseComplete())
			{
				this.handler.renderView(context, viewRoot);
			}
		}
		catch (RuntimeException e)
		{
			context.getExternalContext().log(e.getMessage(),e);
			ViewHandlerImpl.cache(context, e);
			this.cleanup(context);
		}
	}

	/**
	 * <p>Perform necessary actions to restore the specified view
	 * and return a corresponding <code>UIViewRoot</code>.  If there
	 * is no view information to be restored, return <code>null</code>.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param viewId View identifier of the view to be restored
	 *
	 * @exception NullPointerException if <code>context</code>
	 *  or <code>viewId</code> is <code>null</code>
	 */
	@Override
	public UIViewRoot restoreView(FacesContext context, String viewId)
	{
		this.register();
		UIViewRoot viewRoot = this.handler.restoreView(context, viewId);

		/* mbohm (6451472): when the view root is
		 * restored in the RESTORE_VIEW phase, its attributes from the 
		 * previous request are preserved. This will include the CREATED_VIEW
		 * view root attribute. That is causing isPostBack always to think
		 * that the view root was just created (so isPostBack always returns
		 * false). So be sure to clean out the CREATED_VIEW view root attribute
		 * here.
		 */
		if (viewRoot != null)
		{
			viewRoot.getAttributes().remove(CREATED_VIEW);
		}
		return viewRoot;
	}

	/**
	 * <p>Take appropriate action to save the current state information.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 *
	 * @exception IOException if an input/output error occurs
	 * @exception NullPointerException if <code>context</code>
	 *  is <code>null</code>
	 */
	@Override
	public void writeState(FacesContext context) throws IOException
	{
		this.handler.writeState(context);
	}

	//@{ PhaseListener implementation //////////////////////////////////////

	/**
	 * <p>Return <code>PhaseId.ANY_PHASE</code> because we are interested
	 * in all phase events.</p>
	 */
	public PhaseId getPhaseId()
	{
		return PhaseId.ANY_PHASE;
	}


	/**
	 * <p>Process the specified <em>before phase</em> event.</p>
	 *
	 * @param event <code>PhaseEvent</code> to be processed
	 */
	@SuppressWarnings("unchecked")
	public void beforePhase(PhaseEvent event)
	{
		PhaseId phaseId = event.getPhaseId();
		FacesContext context = event.getFacesContext();

		// Ripple this event through to all the page beans that have been
		// initialized for this request and call beforePhase()
		List<AbstractPageBean> pageBeans = (List<AbstractPageBean>) context.getExternalContext().getRequestMap().get(PAGE_BEANS_CREATED);
		if (pageBeans != null)
		{
			for (AbstractPageBean pageBean : pageBeans)
			{
				try
				{
					pageBean.beforePhase(event);
				}
				catch (RuntimeException e)
				{
					context.getExternalContext().log(e.getMessage(), e);
					ViewHandlerImpl.cache(context, e);
				}
			}
		}

		// Broadcast application level events as required
		if (PhaseId.APPLY_REQUEST_VALUES.equals(phaseId))
		{
			// This is the page that will be processing the form submit,
			// so tell the page bean by calling preprocess() on it
			this.preprocess(context);
		}

	}


	/**
	 * <p>Process the specified <em>after phase</em> event.</p>
	 *
	 * @param event <code>PhaseEvent</code> to be processed
	 */
	@SuppressWarnings("unchecked")
	public void afterPhase(PhaseEvent event)
	{
		PhaseId phaseId = event.getPhaseId();
		FacesContext context = event.getFacesContext();

		// Ripple this event through to all the page beans that have been
		// initialized for this request and call afterPhase()
		List<AbstractPageBean> pageBeans = (List<AbstractPageBean>) context.getExternalContext().getRequestMap().get(PAGE_BEANS_CREATED);
		if (pageBeans != null)
		{
			for (AbstractPageBean pageBean : pageBeans)
			{
				try
				{
					pageBean.afterPhase(event);
				}
				catch (RuntimeException e)
				{
					context.getExternalContext().log(e.getMessage(), e);
					ViewHandlerImpl.cache(context, e);
				}
			}
		}

		// In a portlet environment, the "action" and "render"
		// parts of the lifecycle appear as two different requests.
		// Therefore, clean up the page that processed the current
		// form submit (if any)
		if (!(context.getExternalContext().getContext() instanceof ServletContext))
		{
			if (
				PhaseId.INVOKE_APPLICATION.equals(phaseId)
				|| context.getRenderResponse()
				|| context.getResponseComplete()
			) {
				this.cleanup(context);
				return;
			}
		}

		// Broadcast application level events as required
		if (
			PhaseId.RENDER_RESPONSE.equals(phaseId)
			|| context.getResponseComplete()
		) {
			// Unconditionally clean up after rendering is completed
			this.cleanup(context);
		}
	}

	//@} PhaseListener implementation //////////////////////////////////////

	/**
	 * <p>Cache the specified exception in a request scope attribute
	 * that application logic can use to invoke error processing.
	 * All such cached exceptions will be available in the <code>List</code>
	 * used to maintain the cache.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param exception Exception to be cached
	 */
	@SuppressWarnings("unchecked")
	public static void cache(FacesContext context, Exception exception)
	{
		// Is there an active FacesContext?  There will not be if a lifecycle
		// event was fired on a non-Faces request
		if (context == null)
		{
			return;
		}

		// Add this exception to the list of exceptions for this request
		Map<String,Object> map = context.getExternalContext().getRequestMap();
		List<Exception> exceptions = (List<Exception>) map.get(CACHED_EXCEPTIONS);
		if (exceptions == null)
		{
			exceptions = new LinkedList<Exception>();
			map.put(CACHED_EXCEPTIONS, exceptions);
		}
		exceptions.add(exception);
	}
    
	/**
	 * <p>Record the specified {@link AbstractPageBean} on the list of
	 * page beans that have been , and therefore need to have their
	 * beforePhase() and afterPhase() methods called at appropriate times.</p>
	 *
	 * @param context <code>FacesContext</code> for this request
	 * @param bean Page bean to be added to the list
	*/
	@SuppressWarnings("unchecked")
	public static void record(FacesContext context, AbstractPageBean bean)
	{
		if (context == null)
		{
			return;
		}

		Map<String,Object> map = context.getExternalContext().getRequestMap();
		List<AbstractPageBean> pageBeans = (List<AbstractPageBean>) map.get(PAGE_BEANS_CREATED);
		if (pageBeans == null)
		{
			pageBeans = new LinkedList<AbstractPageBean>();
			map.put(PAGE_BEANS_CREATED, pageBeans);
		}
		pageBeans.add(bean);
	}


	/**
	 * <p>Return a <code>List</code> of cached exceptions associated with
	 * this request, if any.  If there were no such exceptions, return
	 * <code>null</code>.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 */
	@SuppressWarnings("unchecked")
	private List<Exception> cached(FacesContext context)
	{
		Map<String,Object> map = context.getExternalContext().getRequestMap();
		return (List<Exception>) map.get(CACHED_EXCEPTIONS);
	}


	/**
	 * <p>Cause any application model request scope beans (instances of
	 * {@link AbstractFragmentBean}, {@link AbstractPageBean}, and
	 * {@link AbstractRequestBean}) to be removed from request scope.
	 * A side effect of this will be to cause {@link LifecycleListener}
	 * to fire <code>destroy()</code> methods on them.</p>
	 *
	 * <p>Then, if we have cached any exceptions associated with this request,
	 * throw an {@link ApplicationException} that wraps the list.  If this occurs,
	 * the first cached exception will be considered the root cause.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 */
	@SuppressWarnings("unchecked")
	private void cleanup(FacesContext context)
	{
		// Acquire a list of request scope attribute keys to be processed
		List<String> list = new ArrayList<String>();
		Map<String,Object> map = context.getExternalContext().getRequestMap();
		map.remove(PAGE_BEANS_CREATED);
		for (Map.Entry<String,Object> entry : map.entrySet())
		{
			Object value = entry.getValue();
			if (value != null)
			{
				if (
					(value instanceof AbstractFragmentBean)
					|| (value instanceof AbstractPageBean)
					|| (value instanceof AbstractRequestBean)
				) {
					list.add(entry.getKey());
				}
			}
		}

		// Cause the selected attributes to be removed from request scope,
		// which will trigger calls to their destroy() methods
		for (String key : list)
		{
			map.remove(key);
		}

		// If we cached any exceptions, wrap them in an ApplicationException
		// and throw it
		List<Exception> exceptions = this.cached(context);
		if ((exceptions != null) && (exceptions.size() > 0))
		{
			// After throwing this ApplicationException
			// and forwarding to a resource specified in the <error-page>
			// element of web.xml, we will arrive here again and throw another
			// ApplicationException during the attempt to present that resource
			// (and thus fail to present it) if the CACHED_EXCEPTIONS entry
			// still exists. So remove the CACHED_EXCEPTIONS entry before 
			// throwing this ApplicationException.
			map.remove(CACHED_EXCEPTIONS);

			throw new ApplicationException(
				(Exception) exceptions.get(0),
				exceptions
			);
		}
	}

	/**
	 * <p>Return the <code>Lifecycle</code> instance for this application,
	 * caching it the first time it is retrieved.</p>
	 */
	private Lifecycle lifecycle()
	{
		if (lifecycle == null)
		{
			String lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE; // FIXME - override?
			LifecycleFactory factory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			lifecycle = factory.getLifecycle(lifecycleId);
		}
		return lifecycle;
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

	/**
	 * <p>Return the {@link AbstractPageBean} instance related to the
	 * current request (if any).  Otherwise, return <code>null</code>.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 */
	private AbstractPageBean pageBean(FacesContext context)
	{
		// Identify the current view (if any)
		UIViewRoot view = context.getViewRoot();
		if (view == null)
		{
			return null;
		}

		// Map the view identifier to the corresponding page bean name
		String viewId = view.getViewId();
		if (view == null)
		{
			return null;
		}

		// Return the relevant page bean (if any)
		return this.pageBean(context, viewId);
	}


	/**
	 * Return the {@link AbstractPageBean} instance related to the
	 * current request (if any).  Otherwise, return <code>null</code>.
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param viewId View identifier used to select the page bean
	 */
	private AbstractPageBean pageBean(FacesContext context, String viewId)
	{
		// Identify the managed bean name of the corresponding page bean
		String viewName = this.pageBeanMapper().mapViewId(viewId);
		if (viewName == null)
		{
			return null;
		}

		// Retrieve or create a corresponding page bean instance
//		ValueBinding vb = context.getApplication().createValueBinding("#{" + viewName + "}"); //NOI18N
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if ( facesCtx == null )
		{
			return null;
		}
		ELResolver elr = facesCtx.getELContext().getELResolver();
		AbstractPageBean pageBean = null;
		try
		{
//			pageBean = (AbstractPageBean) vb.getValue(context);
			pageBean = (AbstractPageBean) elr.getValue( facesCtx.getELContext(), null, "#{" + viewName + "}" );
		}
		catch (ClassCastException e)
		{
			// System.out.println("  WARNING: Bean for " + viewId + " is not a page bean");
		}
		return pageBean;
	}

	/**
	 * <p>Return the {@link PageBeanMapper} we will use to map view identifiers
	 * to managed bean names of the corresponding page beans, instantiating a
	 * new instance if necessary.  <strong>FIXME</strong> - make the actual
	 * implementation class to be used configurable.</p>
	 */
	private PageBeanMapper pageBeanMapper()
	{
		if (mapper == null)
		{
			mapper = new PageBeanMapperImpl();
		}
		return mapper;
	}

	/**
	* <p>Call the <code>preprocess()</code> method on the page bean
	* associated with this request (if any).</p>
	*
	* @param context <code>FacesContext</code> for the current request
	*/
	private void preprocess(FacesContext context)
	{
		this.preprocess(context, pageBean(context));
	}

	/**
	 * <p>Call the <code>preprocess()</code> method on the page bean
	 * that is associated with the specified view identifier (if any).</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param viewId View identifier of the selected view
	 */
	private void preprocess(FacesContext context, String viewId)
	{
		this.preprocess(context, pageBean(context, viewId));
	}

	/**
	 * <p>Call the <code>preprocess()</code> method on the specified
	 * page bean associated with this request.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param pageBean {@link AbstractPageBean} for the current view
	 */
	private void preprocess(FacesContext context, AbstractPageBean pageBean)
	{
		if (pageBean == null)
		{
			return;
		}

		// CR 6255669 - Log and swallow any thrown RuntimeException
		try
		{
			pageBean.preprocess();
		}
		catch (RuntimeException e)
		{
			context.getExternalContext().log(e.getMessage(), e);
			ViewHandlerImpl.cache(context, e);
		}
	}

	/**
	 * <p>Call the <code>prerender()</code> method on the page bean
	 * associated with this request (if any).</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 */
	private void prerender(FacesContext context)
	{
		this.prerender(context, pageBean(context));
	}

	/**
	 * <p>Call the <code>prerender()</code> method on the specified
	 * page bean associated with this request.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param pageBean {@link AbstractPageBean} for the current view
	 */
	private void prerender(FacesContext context, AbstractPageBean pageBean)
	{
		if (pageBean == null)
		{
			return;
		}

		// CR 6255669 - Log and swallow any thrown RuntimeException
		try
		{
			if (!context.getResponseComplete())
			{
				pageBean.prerender();
			}
		}
		catch (RuntimeException e)
		{
			context.getExternalContext().log(e.getMessage(), e);
			ViewHandlerImpl.cache(context, e);
		}
	}

	/**
	 * <p>Return a <code>List</code> of cached page beans that have
	 * been created, and therefore need to have their beforePhase()
	 * and afterPhase() methods called at appropriate times.  If there were
	 * no such beans, return <code>null</code>.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 */
	@SuppressWarnings("unchecked")
	private List<AbstractPageBean> recorded(FacesContext context)
	{
		if (context != null)
		{
			Map<String,Object> map = context.getExternalContext().getRequestMap();
			return (List<AbstractPageBean>) map.get(PAGE_BEANS_CREATED);
		}
		return null;
	}

	/**
	 * <p>Return the number of page beans that have been created for
	 * this request.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 */
	@SuppressWarnings("unchecked")
	private int recordedCount(FacesContext context)
	{
		if (context != null)
		{
			Map<String,Object> map = context.getExternalContext().getRequestMap();
			List<AbstractPageBean> list = (List<AbstractPageBean>) map.get(PAGE_BEANS_CREATED);
			if (list != null)
			{
				return list.size();
			}
			else
			{
				return 0;
			}
		}
		else
		{
			return 0;
		}
	}

	/**
	 * <p>Register this instance as a <code>PhaseListener</code> with the
	 * <code>Lifecycle</code> instance for this web application, if we
	 * have not already done so.</p>
	 */
	private void register()
	{
		if (registered)
		{
			return;
		}
		this.lifecycle().addPhaseListener(this);
		registered = true;
	}
}
