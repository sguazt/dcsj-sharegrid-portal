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
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * Arbitrary grouping "renderer" that simply renders its children
 * recursively in the <code>encodeEnd()</code> method.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class HtmlPanelGroupRenderer extends AbstractHtmlRenderer
{
	@Override
	public void doEncodingBegin(FacesContext context, UIComponent component) throws IOException
	{
		// Render a span around this group if necessary
		String style = (String) component.getAttributes().get("style");
		String styleClass = (String) component.getAttributes().get("styleClass");
		ResponseWriter writer = context.getResponseWriter();

		if ( this.divOrSpan(component) )
		{
			if (("block".equals(component.getAttributes().get("layout"))))
			{
				writer.startElement("div", component);
			}
			else
			{
				writer.startElement("span", component);
			}
			this.writeIdAttributeIfNecessary(context, writer, component);
			if (styleClass != null)
			{
				writer.writeAttribute("class", styleClass, "styleClass");
			}
			if (style != null)
			{
				writer.writeAttribute("style", style, "style");
			}
		}
	}

	@Override
	public void doEncodingChildren(FacesContext context, UIComponent component) throws IOException
	{
		// Render our children recursively
		Iterator<UIComponent> kids = getChildren(component);
		while (kids.hasNext())
		{
			this.encodeRecursive(context, kids.next());
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
		// Close our span element if necessary
		ResponseWriter writer = context.getResponseWriter();
		if (this.divOrSpan(component))
		{
			if ("block".equals(component.getAttributes().get("layout")))
			{
				writer.endElement("div");
			}
			else
			{
				writer.endElement("span");
			}
		}
	}

	@Override
	public boolean getRendersChildren()
	{
		return true;
	}

	/**
	 * @param component <code>UIComponent</code> for this group
	 *
	 * @return <code>true</code> if we need to render a div or span element
	 *  around this group.
	 */
	private boolean divOrSpan(UIComponent component)
	{
		return	this.shouldWriteIdAttribute(component)
			|| (component.getAttributes().get("style") != null)
			|| (component.getAttributes().get("styleClass") != null);
	}
}
