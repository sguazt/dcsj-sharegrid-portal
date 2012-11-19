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

import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.common.io.FastStringWriter;

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIOutputPopupPanelComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.renderer.util.RendererUtil;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesUtil;

import java.io.Writer;
import java.io.IOException;
import java.util.Map;

import javax.el.ValueExpression;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Renderer for output popup panel components.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class HtmlOutputPopupPanelRenderer extends AbstractHtmlJsRenderer
{
	@Override
	public boolean getRendersChildren()
	{
		return true;
	}

	@Override
	protected void doEncodingBegin(FacesContext context, UIComponent component) throws IOException
	{
		super.doEncodingBegin( context, component );

		UIOutputPopupPanelComponent popup = null;
		popup = (UIOutputPopupPanelComponent) component;

		ResponseWriter writer = context.getResponseWriter();

		// Output dependent styles
		this.renderStyleResource(
			context,
			"/resources/scripts/ext/resources/css/ext-all.css",
			"text/css"
		);

		//FIXME: Ext overrides previously defined CSS rules. So for restoring them  we need to reinclude the common CSS resource
		this.renderStyleResource(
			context,
			"screen.css",
			"text/css"
		);

		// Output dependent scripts
		this.renderScriptResource(
			context,
			"ext/adapter/ext/ext-base.js",
			"text/javascript"
		);
		this.renderScriptResource(
			context,
			"ext/ext-all.js",
			"text/javascript"
		);
		// Set up Ext configuration
		this.renderScriptInline(
			context,
			"Ext.BLANK_IMAGE_URL = 'js/ext/resources/images/default/s.gif';",
			"text/javascript"
		);
		this.renderScriptResource(
			context,
			"dcs/widget/outputpopuppanel.js",
			"text/javascript"
		);

		// This id will be used as a temporary place holder to position the
		// component in page.
		String id;

		id = context.getViewRoot().createUniqueId();

		// Render beginning of the enclosing tag
		writer.startElement("div", component);
		writer.writeAttribute("id", id, null);

		// Render beginning of the enclosing script tag
		writer.startElement("script", component);
		writer.writeAttribute("type", "text/javascript", null);
		//// [if defer]
		//writer.write("._addOnLoad(function() {");
		//// [fi]

		// Render the beginning of the JavaScript script
		writer.write( "new Dcs.Widget.OutputPopupPanel(" );
//		writer.write(popup.getShowOnId());
//		writer.write("',");
	}

	@Override
	protected void doEncodingEnd(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();

		// Note: Trailing \n char causes grief with CSS float.        

		// Render the end of the JavaScript script
		writer.write(");");

		// Render the end of the enclosing script tag.
		//// [if defer]
		//writer.write("});");
		//// [fi]
		writer.endElement("script");

		// Render the end of enclosing tag.
		writer.endElement("div");

		super.doEncodingEnd( context, component );
	}

	@Override
	protected void doEncodingChildren(FacesContext context, UIComponent component) throws IOException
	{
//		super.doEncodingChildren( context, component );

		ResponseWriter writer = context.getResponseWriter();

		try
		{
//			// Encode childrens
//			for (UIComponent child : component.getChildren())
//			{
//				if ( child.isRendered() )
//				{
//					writer.write(
//						WidgetUtilities.renderComponent(
//							context,
//							child
//						).toString()
//					);
//				}
//			}

			JSONObject json = null;

			json = new JSONObject();

			// Get common properties
			this.setCommonProperties(context, component, json);

			// Get additional options
			this.setProperties(context, component, json);

			writer.write( json.toString() );
		}
		catch (JSONException je)
		{
			throw new IOException("Error while getting JSON properties.", je);
			//je.printStackTrace();
		}
	}

/*
	private boolean isWidgetChild(FacesContext context, UIComponent component)
	{
		// Get component parent.
		UIComponent parent = component.getParent();
		if (parent == null)
		{
			return false;
		}
		// Get family and renderer type.
		String family = parent.getFamily();
		String type = parent.getRendererType();
		if (family == null || type == null)
		{
			return false;
		}
		// Get renderer.
		Renderer renderer = context.getRenderKit().getRenderer(family, type);
		return (renderer instanceof AbstractHtmlJsRenderer);
	}
*/

	protected void setCommonProperties(FacesContext context, UIComponent component, JSONObject json) throws IOException, JSONException
	{
		json.put(
			"id", component.getClientId(context)
//		).put(
//			"widgetType", getWidgetType(context, component)
		);
	}

	protected void setProperties(FacesContext context, UIComponent component, JSONObject json) throws IOException, JSONException
	{
		if (!(component instanceof UIOutputPopupPanelComponent))
		{
			throw new IllegalArgumentException("HtmlOutputPopupPanelRenderer can only render UIOutputPopupPanelComponent components.");
		}

		UIOutputPopupPanelComponent popup = null;

		popup = (UIOutputPopupPanelComponent) component;

		//JSONObject json = new JSONObject();
//		Map<String,Object> attrsMap = popup.getAttributes();
//		for (String attrName : attrsMap.keySet())
//		{
//			Object value = attrsMap.get(attrName);
//
//			if ( value != null )
//			{
//				json.put( attrName, attrsMap.get(attrName) );
//			}
//		}
		json.put("autoScroll", popup.isAutoScroll());
		if ( !Strings.IsNullOrEmpty(popup.getBodyClass()) )
		{
			json.put("bodyClass", popup.getBodyClass());
		}
		if ( !Strings.IsNullOrEmpty(popup.getBodyContentId()) )
		{
			json.put("bodyContentId", popup.getBodyContentId());
		}
		json.put("border", popup.isBorder());
		json.put("closable", popup.isClosable());
		if ( !Strings.IsNullOrEmpty(popup.getCloseAction()) )
		{
				json.put("closableAction", popup.getCloseAction());
		}
		if ( !Strings.IsNullOrEmpty(popup.getHeaderClass()) )
		{
			json.put("headerClass", popup.getHeaderClass());
		}
		if ( !Strings.IsNullOrEmpty(popup.getHeaderTitle()) )
		{
			json.put("headerTitle", popup.getHeaderTitle());
		}
		if (popup.getHeight() >= 0)
		{
			json.put("height", popup.getHeight());
		}
		json.put("maximizable", popup.isMaximizable());
		json.put("minimizable", popup.isMinimizable());
		json.put("modal", popup.isModal());
		json.put("resizable", popup.isResizable());
		if ( popup.getShowOn() != null )
		{
			json.put("showOn", popup.getShowOn());
		}
		if ( popup.getShowOnId() != null )
		{
			UIComponent showOnComp = null;
			String showOnId = null;

			// Try to find an existing component with the given id.
			showOnComp = popup.findComponent(popup.getShowOnId());

			// If found, take the right client-side id, otherwise take
			// the originally given id. 
			if (showOnComp != null)
			{
				showOnId = showOnComp.getClientId(context);
			}
			else
			{
				showOnId = popup.getShowOnId();
			}
			json.put("showOnId", showOnId);
		}
		if (popup.getWidth() >= 0)
		{
			json.put("width", popup.getWidth());
		}

//		JSONArray jArray = new JSONArray();
		StringBuilder contentsb = null;
		contentsb = new StringBuilder();

		// Popup children components.
		for (UIComponent child : component.getChildren())
		{
			if ( child.isRendered() )
			{
				JSONObject jobj = null;
				jobj = WidgetUtilities.renderComponent(context, child);

				if ( jobj != null && jobj.has("_content_") )
				{
//					jArray.put(jobj.get("_content_"));
					contentsb.append(jobj.get("_content_").toString());
				}
			}
		}
//		json.put("contentBody", jArray.join(""));
		json.put("bodyContent", contentsb);

		//return json;
	}

//@} FIXME
private static class WidgetUtilities
{
	/**
	 * Helper method to capture rendered component properties for client-side
	 * rendering.
	 * 
	 * If the component is a widget, the rendered output should be a string 
	 * beginning with { and ending with }. In this case, the string shall be 
	 * used to create a JSONObject contain properties such as widgetName, 
	 * module, etc. Otherwise, the rendered HTML string is assigned to the 
	 * JSONObject via a "html" property. In this case, an escape property is
	 * also set to false -- HTML should not be escaped.
	 *
	 * In either case, it is important to use a JavaScript object to distinguish
	 * between widgets and static strings so HTML escaping can be
	 * applied properly.
	 * 
	 * @param context FacesContext for the current request.
	 * @param component UIComponent to be rendered.
	 *
	 * @returns JSONObject containing component properties or markup.
	 */
	public static JSONObject renderComponent(FacesContext context, UIComponent component) throws IOException, JSONException
	{
		JSONObject json = null;
		if (component == null || !component.isRendered())
		{
			return json;
		}

		/// To intercept all response writing we clone the
		/// current response writer and attach to it a string
		/// writer.

		// Save current reponse writer.
		ResponseWriter oldWriter = null;
		oldWriter = context.getResponseWriter();

		// Initialize a string writer for buffere rendered output 
		Writer strWriter = null;
		strWriter = new FastStringWriter();

		// Initialize new writer.
		ResponseWriter newWriter = null;
		if (null != oldWriter)
		{
			newWriter = oldWriter.cloneWithWriter(strWriter);
		}
		else
		{
			ExternalContext extContext = context.getExternalContext();
			newWriter = context.getRenderKit().createResponseWriter(
					strWriter,
					null,
					extContext.getRequestCharacterEncoding()
			);
		}

		// Set new writer in context.
		context.setResponseWriter(newWriter);

		// Render component
		RendererUtil.RenderComponent(component, context);

		// Restore old writer
		context.setResponseWriter(oldWriter);

		// Get buffered output.
		String s = strWriter.toString();

		try
		{
			// String beginning with { and ending with }.
			json = new JSONObject(s);
		}
		catch (JSONException e)
		{
			json = new JSONObject();
			json.put("_content_", s);
		}

		return json;
	}
}
//@} FIXME
}
