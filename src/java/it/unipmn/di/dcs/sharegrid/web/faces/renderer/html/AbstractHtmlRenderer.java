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

package it.unipmn.di.dcs.sharegrid.web.faces.renderer.html;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.Set;

import javax.faces.application.ViewHandler;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.component.ValueHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

import it.unipmn.di.dcs.sharegrid.web.faces.renderer.AbstractRenderer;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesUtil;

/**
 * <B>AbstractHtmlRenderer</B> is a base class for implementing renderers
 * for Html RenderKit.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@SuppressWarnings("unchecked")
public abstract class AbstractHtmlRenderer extends AbstractRenderer
{
	// Here are the standard HTML attributes
	protected static String[] PassthruAttributes = {
		"accept",
		"accesskey",
		"alt",
		"bgcolor",
		"border",
		"cellpadding",
		"cellspacing",
		"charset",
		"cols",
		"coords",
		"dir",
		"enctype",
		"frame",
		"height",
		"hreflang",
		"lang",
		"longdesc",
		"maxlength",
		"onblur",
		"onchange",
		"onclick",
		"ondblclick",
		"onfocus",
		"onkeydown",
		"onkeypress",
		"onkeyup",
		"onload",
		"onmousedown",
		"onmousemove",
		"onmouseout",
		"onmouseover",
		"onmouseup",
		"onreset",
		"onselect",
		"onsubmit",
		"onunload",
		"rel",
		"rev",
		"rows",
		"rules",
		"shape",
		"size",
		"style",
		"summary",
		"tabindex",
		"target",
		"title",
		"usemap",
		"width"
	};

	protected static final String RENDERED_SCRIPTS_KEY = AbstractHtmlRenderer.class.getName() + ".RENDERED_SCRIPTS";

	protected static final String RENDERED_STYLES_KEY = AbstractHtmlRenderer.class.getName() + ".RENDERED_STYLE";

	private static final Pattern AbsUrlRegEx = Pattern.compile("^\\w+://[^/]+/");

	/** A constructor. */
	protected AbstractHtmlRenderer()
	{
		super();
	}

	@Override
	public void decode(FacesContext context, UIComponent component)
	{
		FacesUtil.AssertValidParams( context, component );

		if (context == null || component == null)
		{
			throw new NullPointerException();
		}

		if ( !(component instanceof UIInput) )
		{
			// decode needs to be invoked only for components that
			// are instances or subclasses of UIInput.
			return;
		}
//		else
//		{
//			uiInput = (UIInput) component;
//		}

		// If the component is disabled, do not change the value of the
		// component, since its state cannot be changed.
		if (FacesUtil.ComponentIsDisabledOrReadonly(component))
		{
			return;
		}

		String clientId = component.getClientId(context);
		Map<java.lang.String,java.lang.String> requestMap = null;
		requestMap = context.getExternalContext().getRequestParameterMap();

		// Don't overwrite the value unless you have to!
		if (requestMap.containsKey(clientId))
		{
			String newValue = (String) requestMap.get(clientId);
			this.setSubmittedValue(component, newValue);
		}

		this.doDecoding(context, component);
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component)  throws IOException
	{
		FacesUtil.AssertValidParams( context, component );

		if (!component.isRendered())
		{
			return;
		}

		//super.encodeBegin( context, component );

		this.doEncodingBegin(context, component);

		this.doEncodingResources(context, component);
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
		FacesUtil.AssertValidParams( context, component );

		String currentValue = null;
		//        ResponseWriter writer = null;
		//        String styleClass = null;

		if (context == null || component == null)
		{
			throw new NullPointerException();
		}

		// suppress rendering if "rendered" property on the component is
		// false.
		if (!component.isRendered())
		{
			return;
		}

		//writer = context.getResponseWriter();

		//currentValue = this.getCurrentValue(context, component);
		//this.getEndTextToRender(context, component, currentValue);

		this.doEncodingEnd(context, component);
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException
	{
		FacesUtil.AssertValidParams( context, component );

		String currentValue = null;
		//        ResponseWriter writer = null;
		//        String styleClass = null;

		if (context == null || component == null)
		{
			throw new NullPointerException();
		}

		// suppress rendering if "rendered" property on the component is
		// false.
		if (!component.isRendered())
		{
			return;
		}

		this.doEncodingChildren(context, component);
	}

	@Override
	public String convertClientId(FacesContext context, String clientId)
	{
		return clientId;
	}

	/**
	 * Hook for adding custom decoding behaviour.
	 */
	protected void doDecoding(FacesContext context, UIComponent component)
	{
//		super.decode( context, component );
	}

	/**
	 * Hook for adding custom behaviour at the beginning of encoding.
	 */
	protected void doEncodingBegin(FacesContext context, UIComponent component) throws IOException
	{
//		super.encodeBegin( context, component );
	}

	/**
	 * Hook for adding custom behaviour at the end of encoding.
	 */
	protected void doEncodingEnd(FacesContext context, UIComponent component) throws IOException
	{
//		super.encodeEnd( context, component );
	}

	/**
	 * Hook for adding custom encoding behaviour for children.
	 */
	protected void doEncodingChildren(FacesContext context, UIComponent component) throws IOException
	{
//		super.encodeChildren( context, component );
	}

	/**
	 * Hook for adding custom resource encoding behaviour.
	 */
	protected void doEncodingResources(FacesContext context, UIComponent component) throws IOException
	{
		// empty
	}

//	protected void renderScript(String scriptPath, ResponseWriter writer, UIComponent component, FacesContext context) throws IOException
//	{
//		Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
//
//		// Check to see if resource already rendered
//		if (requestMap.containsKey(RENDERED_SCRIPTS_KEY) && ((Map<String,Boolean>) requestMap.get(RENDERED_SCRIPTS_KEY)).containsKey(scriptPath))
//		{
//			// script already rendered
//			return;
//		}
//
//		String contextRoot = null;
//		contextRoot = context.getExternalContext().getRequestContextPath();
//
//		// Render markup tag for the javascript file
//		writer.startElement("script", component);
//		writer.writeAttribute("type", "text/javascript", null);
//		writer.writeAttribute("src", contextRoot + "/" + scriptPath, null);
//		writer.endElement("script");
//		writer.write("\n");
//
//		// put flag in requestMap to indicate already rendered
//		if (!requestMap.containsKey(RENDERED_SCRIPTS_KEY))
//		{
//			requestMap.put(RENDERED_SCRIPTS_KEY, new HashMap<String,Boolean>());
//		}
//		((Map<String,Boolean>) requestMap.get(RENDERED_SCRIPTS_KEY)).put(scriptPath, Boolean.TRUE);
//	}
//
//	protected void renderCss(String cssPath, ResponseWriter writer, UIComponent component, FacesContext context) throws IOException
//	{
//		Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
//
//		// Check to see if resource already rendered
//		if (requestMap.containsKey(RENDERED_STYLE_KEY) && ((Map<String,Boolean>) requestMap.get(RENDERED_STYLE_KEY)).containsKey(cssPath))
//		{
//			// css already rendered
//			return;
//		}
//
//		String contextRoot = null;
//		contextRoot = context.getExternalContext().getRequestContextPath();
//
//		// Render markup tag for the javascript file
//		writer.startElement("link", component);
//		writer.writeAttribute("type", "text/css", null);
//		writer.writeAttribute("rel", "stylesheet", null);
//		writer.writeAttribute("href", contextRoot + "/" + cssPath, null);
//		writer.endElement("link");
//		writer.write("\n");
//
//		// put flag in requestMap to indicate already rendered
//		if (!requestMap.containsKey(RENDERED_STYLE_KEY))
//		{
//			requestMap.put(RENDERED_STYLE_KEY, new HashMap<String,Boolean>());
//		}
//		((Map<String,Boolean>) requestMap.get(RENDERED_STYLE_KEY)).put(cssPath, Boolean.TRUE);
//	}

	/**
	 * Writes a style sheet resource at-most-once within a single
	 * RenderResponse phase.
	 *
	 * @param context        the Faces context
	 * @param resourcePath the style sheet resource path
	 * @param type          the script type (e.g. 'text/css')
	 *
	 * @throws IOException if an error occurs during rendering
	 */
	protected void renderStyleResource(FacesContext context, String resourcePath, String type) throws IOException
	{
		Set<String> styleResources = this.getRenderedStyleResources(context);

		if (!styleResources.contains(resourcePath))
		{
			ViewHandler handler = context.getApplication().getViewHandler();
			if (this.isRelativeUrl(resourcePath))
			{
				//resourcePath = "/resources/theme/current/dcsjsf/" + resourcePath;//FIXME: hard-coded
				resourcePath = "/resources/theme/current/css/" + resourcePath;//FIXME: hard-coded
			}
			String resourceURL = handler.getResourceURL(context, resourcePath);
			ResponseWriter out = context.getResponseWriter();
			out.startElement("style", null);
			out.writeAttribute("type", type, null);
			out.writeText("@import url(" + resourceURL + ");", null);
			out.endElement("style");

			styleResources.add(resourcePath);
		}
	}

	/**
	 * Writes an inline style at-most-once within a single
	 * RenderResponse phase.
	 * 
	 * @param context      the Faces context
	 * @param inlineStyle  the inline style classes
	 * @param type          the script type (e.g. 'text/css')
	 * 
	 * @throws IOException  if an error occurs during rendering
	 */
	protected void renderStyleInline(FacesContext context, String inlineStyle, String type) throws IOException
	{
		Set<String> styleResources = this.getRenderedStyleResources(context);

		String styleDigest = this.getDigest(inlineStyle);

		if (!styleResources.contains(styleDigest))
		{
			ResponseWriter out = context.getResponseWriter();
			out.startElement("style", null);
			out.writeAttribute("type", type, null);
			out.writeText(inlineStyle, null);
			out.endElement("style");

			styleResources.add(styleDigest);
		}
	}

	/**
	 * Writes a script library resource at-most-once within a single
	 * RenderResponse phase.
	 *
	 * @param context        the Faces context
	 * @param resourcePath the script library resource path
	 * @param type          the script type (e.g. 'text/javascript')
	 *
	 * @throws IOException if an error occurs during rendering
	 */
	protected void renderScriptResource(FacesContext context, String resourcePath, String type) throws IOException
	{
		Set<String> scriptResources = this.getRenderedScriptResources(context);

		if (!scriptResources.contains(resourcePath))
		{
			if (this.isRelativeUrl(resourcePath))
			{
				resourcePath = "/resources/scripts/" + resourcePath;//FIXME: hard-coded
			}
			ViewHandler handler = context.getApplication().getViewHandler();
			String resourceURL = handler.getResourceURL(context, resourcePath);
			ResponseWriter out = context.getResponseWriter();
			out.startElement("script", null);
			out.writeAttribute("type", type, null);
			out.writeAttribute("src", resourceURL, null);
			out.endElement("script");

			scriptResources.add(resourcePath);
		}
	}

	/**
	 * Writes an inline script at-most-once within a single
	 * RenderResponse phase.
	 * 
	 * @param context       the Faces context
	 * @param inlineScript  the inline script code
	 * @param type          the script type (e.g. 'text/javascript')
	 * 
	 * @throws IOException  if an error occurs during rendering
	 */
	protected void renderScriptInline(FacesContext context, String inlineScript, String type) throws IOException
	{
		Set<String> scriptResources = this.getRenderedScriptResources(context);

		String scriptDigest = this.getDigest(inlineScript);

		if (!scriptResources.contains(scriptDigest))
		{
			ResponseWriter out = context.getResponseWriter();
			out.startElement("script", null);
			out.writeAttribute("type", type, null);
			out.writeText(inlineScript, null);
			out.endElement("script");

			scriptResources.add(scriptDigest);
		}
	}

	// Implements at-most-once semantics for each script resource on
	// the currently rendering page
	private Set<String> getRenderedScriptResources(FacesContext context)
	{
		ExternalContext external = context.getExternalContext();
		Map<String,Object> requestScope = external.getRequestMap();
		Set<String> written = (Set<String>)requestScope.get(RENDERED_SCRIPTS_KEY);

		if (written == null)
		{
			written = new HashSet<String>();
			requestScope.put(RENDERED_SCRIPTS_KEY, written);
		}

		return written;
	}

	// Implements at-most-once semantics for each style resource on
	// the currently rendering page
	private Set<String> getRenderedStyleResources(FacesContext context)
	{
		ExternalContext external = context.getExternalContext();
		Map<String,Object> requestScope = external.getRequestMap();
		Set<String> written = (Set<String>)requestScope.get(RENDERED_STYLES_KEY);

		if (written == null)
		{
			written = new HashSet<String>();
			requestScope.put(RENDERED_STYLES_KEY, written);
		}

		return written;
	}

	private boolean isRelativeUrl(String url)
	{
		if (url.charAt(0) != '/' && !AbsUrlRegEx.matcher(url).matches())
		{
			return true;
		}
		return false;
	}

	private String getDigest(String s)
	{
		MessageDigest md = null;
		String digest = null;

		try
		{
			md = MessageDigest.getInstance("SHA");
			md.update(s.getBytes());
			digest = new String(md.digest());
			md = null;
		}
		catch (Exception e)
		{
			digest = "DUMMY_" + Integer.toString(s.hashCode());
		}

		return digest;
	}

	/**
	 * Gets value to be rendered and formats it if required. Sets to empty
	 * string if value is null.
	 */
	protected String getCurrentValue(FacesContext context, UIComponent component)
	{
		if (component instanceof UIInput)
		{
			Object submittedValue = ((UIInput) component).getSubmittedValue();
			if (submittedValue != null)
			{
				return (String) submittedValue;
			}
		}

		String currentValue = null;
		Object currentObj = getValue(component);
		if (currentObj != null)
		{
			currentValue = getFormattedValue(context, component, currentObj);
		}
		return currentValue;
	}


	protected Object getValue(UIComponent component)
	{
		// Make sure this method isn't being called except
		// from subclasses that override getValue()!
		throw new UnsupportedOperationException();
	}


	/**
	 * Renderers override this method to write appropriate HTML content into
	 * the buffer.
	 */
	protected void getEndTextToRender(FacesContext context, UIComponent component, String currentValue) throws IOException
	{
		return;
	}


	/**
	 * Renderers override this method to store the previous value
	 * of the associated component.
	 */
	protected void setSubmittedValue(UIComponent component, Object value)
	{
		return;
	}


	/**
	 * Renderers override this method in case output value needs to be
	 * formatted
	 */
	protected String getFormattedValue(FacesContext context, UIComponent component, Object currentValue) throws ConverterException
	{
		String result = null;
		// formatting is supported only for components that support
		// converting value attributes.
		if (!(component instanceof ValueHolder))
		{
			if (currentValue != null)
			{
				result = currentValue.toString();
			}
			return result;
		}

		Converter converter = null;

		// If there is a converter attribute, use it to to ask application
		// instance for a converter with this identifer.

		if (component instanceof ValueHolder)
		{
			converter = ((ValueHolder) component).getConverter();
		}

		// if value is null and no converter attribute is specified, then
		// return a zero length String.
		if (converter == null && currentValue == null)
		{
			return "";
		}

		if (converter == null)
		{
			// Do not look for "by-type" converters for Strings
			if (currentValue instanceof String)
			{
			return (String) currentValue;
			}

			// if converter attribute set, try to acquire a converter
			// using its class type.

			Class converterType = currentValue.getClass();
			converter = FacesUtil.GetConverterForClass(converterType, context);

			// if there is no default converter available for this identifier,
			// assume the model type to be String.
			if (converter == null && currentValue != null)
			{
				result = currentValue.toString();
				return result;
			}
		}

		if (converter != null)
		{
			result = converter.getAsString(context, component, currentValue);

			return result;
		}
		else
		{
			// throw converter exception if no converter can be
			// identified
			//            Object[] params = {
			//                 currentValue,
			//                 "null Converter"
			//            };

			throw new ConverterException("No converter can be identified");
		}
	}

	protected Iterator getMessageIterator(FacesContext context, String forComponent, UIComponent component)
	{
		Iterator messageIter = null;
		// Attempt to use the "for" attribute to locate
		// messages.  Three possible scenarios here:
		// 1. valid "for" attribute - messages returned
		//    for valid component identified by "for" expression.
		// 2. zero length "for" expression - global errors
		//    not associated with any component returned
		// 3. no "for" expression - all messages returned.
		if (null != forComponent) {
			if (forComponent.length() == 0)
			{
				messageIter = context.getMessages(null);
			}
			else
			{
				UIComponent result = getForComponent(context, forComponent, component);
				if (result == null)
				{
					messageIter = Collections.EMPTY_LIST.iterator();
				}
				else
				{
					messageIter = context.getMessages(result.getClientId(context));
				}
			}
		}
		else
		{
			messageIter = context.getMessages();
		}
		return messageIter;
	}

	/**
	 * Locates the component identified by <code>forComponent</code>
	 *
	 * @param forComponent - the component to search for
	 * @param component    - the starting point in which to begin the search
	 * @return the component with the the <code>id</code> that matches
	 *         <code>forComponent</code> otheriwse null if no match is found.
	 */
	protected UIComponent getForComponent(FacesContext context, String forComponent, UIComponent component)
	{
		if (null == forComponent || forComponent.length() == 0)
		{
			return null;
		}

		UIComponent result = null;
		UIComponent currentParent = component;
		try {
			// Check the naming container of the current
			// component for component identified by
			// 'forComponent'
			while (currentParent != null)
			{
				// If the current component is a NamingContainer,
				// see if it contains what we're looking for.
				result = currentParent.findComponent(forComponent);
				if (result != null)
				{
					break;
				}
				// if not, start checking further up in the view
				currentParent = currentParent.getParent();
			}

			// no hit from above, scan for a NamingContainer
			// that contains the component we're looking for from the root.
			if (result == null)
			{
				result = findUIComponentBelow(context.getViewRoot(), forComponent);
			}
		}
		catch (Throwable t)
		{
			throw new RuntimeException("Unable to find 'for' component '" + forComponent + "'");
		}
		// log a message if we were unable to find the specified

		return result;
	}

	/**
	 * <p>Recursively searches for {@link NamingContainer}s from the
	 * given start point looking for the component with the <code>id</code>
	 * specified by <code>forComponent</code>.
	 *
	 * @param startPoint   - the starting point in which to begin the search
	 * @param forComponent - the component to search for
	 * @return the component with the the <code>id</code> that matches
	 *         <code>forComponent</code> otheriwse null if no match is found.
	 */
	private UIComponent findUIComponentBelow(UIComponent startPoint, String forComponent)
	{
		UIComponent retComp = null;
		List children = startPoint.getChildren();
		for (int i = 0, size = children.size(); i < size; i++)
		{
			UIComponent comp = (UIComponent) children.get(i);

			if (comp instanceof NamingContainer)
			{
				retComp = comp.findComponent(forComponent);
			}

			if (retComp == null)
			{
				if (comp.getChildCount() > 0)
				{
					retComp = findUIComponentBelow(comp, forComponent);
				}
			}

			if (retComp != null)
			{
				break;
			}
		}
		return retComp;
	}


	protected UIComponent findAncestorByClass(UIComponent child, Class clazz)
	{
		UIComponent parent = null;

		for ( parent = child.getParent(); parent != null; parent = parent.getParent() )
		{
			if ( parent.getClass().getName().equals( clazz.getName() ) )
			{
				break;
			}
		}

		return parent;
	}

	/**
	 * <p>Render nested child components by invoking the encode methods
	 * on those components, but only when the <code>rendered</code>
	 * property is <code>true</code>.</p>
	 */
	protected void encodeRecursive(FacesContext context, UIComponent component) throws IOException
	{
		// suppress rendering if "rendered" property on the component is
		// false.
		if (!component.isRendered())
		{
			return;
		}

		// Render this component and its children recursively
		component.encodeBegin(context);
		if (component.getRendersChildren())
		{
			component.encodeChildren(context);
		}
		else
		{
			Iterator<UIComponent> kids = getChildren(component);
			while (kids.hasNext())
			{
				UIComponent kid = (UIComponent) kids.next();
				encodeRecursive(context, kid);
			}
		}
		component.encodeEnd(context);
	}


	/**
	 * <p>Return an Iterator over the children of the specified
	 * component, selecting only those that have a
	 * <code>rendered</code> property of <code>true</code>.</p>
	 *
	 * @param component <code>UIComponent</code> for which to extract children
	 */
	protected Iterator<UIComponent> getChildren(UIComponent component)
	{
		int childCount = component.getChildCount();
		if (childCount > 0)
		{
			return new RenderedChildIterator(component.getChildren().iterator());
		}
		else
		{
			return Collections.EMPTY_LIST.iterator();
		}
	}


	/**
	 * <p>Return the specified facet from the specified component, but
	 * <strong>only</strong> if its <code>rendered</code> property is
	 * set to <code>true</code>.
	 *
	 * @param component Component from which to return a facet
	 * @param name      Name of the desired facet
	 */
	protected UIComponent getFacet(UIComponent component, String name)
	{
		UIComponent facet = component.getFacet(name);
		if ((facet != null) && !facet.isRendered())
		{
			facet = null;
		}
		return (facet);
	}


	/**
	 * @return true if this renderer should render an id attribute.
	 */
	protected boolean shouldWriteIdAttribute(UIComponent component)
	{
		String id;
		return (null != (id = component.getId()) && !id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX));
	}


	protected String writeIdAttributeIfNecessary(FacesContext context, ResponseWriter writer, UIComponent component)
	{
		String id = null;

		if (this.shouldWriteIdAttribute(component))
		{
			id = component.getClientId(context);

			try
			{
				writer.writeAttribute("id", id, "id");
			}
			catch (IOException e)
			{
				// empty
				// TODO
//				if (logger.isLoggable(Level.WARNING))
//				{
//					String message = MessageUtils.getExceptionMessageString(
//						MessageUtils.CANT_WRITE_ID_ATTRIBUTE_ERROR_MESSAGE_ID,
//						e.getMessage()
//					);
//					logger.warning(message);
//				}
			}
		}

		return id;
	}

	protected Param[] getParamList(FacesContext context, UIComponent command)
	{
		ArrayList parameterList = new ArrayList();

		Iterator kids = command.getChildren().iterator();
		while (kids.hasNext())
		{
			UIComponent kid = (UIComponent) kids.next();

			if (kid instanceof UIParameter)
			{
				UIParameter uiParam = (UIParameter) kid;
				Object value = uiParam.getValue();
				Param param = new Param(
					uiParam.getName(),
					(value == null) ? null : value.toString()
				);
				parameterList.add(param);
			}
		}

		return (Param[]) parameterList.toArray(new Param[parameterList.size()]);
	}

	//inner class to store parameter name and value pairs
	protected class Param
	{
		public Param(String name, String value)
		{
			set(name, value);
		}


		private String name;
		private String value;


		public void set(String name, String value)
		{
			this.name = name;
			this.value = value;
		}


		public String getName()
		{
			return name;
		}


		public String getValue()
		{
			return value;
		}
	}

	/**
	* <p>This <code>Iterator</code> is used to Iterator over
	* children components that are set to be rendered.</p>
	*/
	private static class RenderedChildIterator implements Iterator<UIComponent>
	{
		Iterator<UIComponent> childIterator;
		boolean hasNext;
		UIComponent child;

		private RenderedChildIterator(Iterator<UIComponent> childIterator)
		{
			this.childIterator = childIterator;
			this.update();
		}

		//@{ Iterator implementation

		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		public boolean hasNext()
		{
			return hasNext;
		}

		public UIComponent next()
		{
			if (!hasNext)
			{
				throw new NoSuchElementException();
			}
			UIComponent temp = this.child;
			this.update();
			return temp;
		}

		//@} Iterator implementation

		/**
		* <p>Moves the internal pointer to the next renderable
		* component skipping any that are not to be rendered.</p>
		*/
		private void update()
		{
			while (childIterator.hasNext())
			{
				UIComponent comp = childIterator.next();
				if (comp.isRendered())
				{
					child = comp;
					hasNext = true;
					return;
				}
			}

			hasNext = false;
			child = null;
		}
	}
}
