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

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * HTML renderer for the Accordion component.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class HtmlOutputAccordionRenderer extends AbstractHtmlRenderer
{
	@Override
	public boolean getRendersChildren()
	{
		return false;
	}

	@Override
    protected void doEncodingBegin(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();

		// write the script for loading the Rico JS file
		this.renderScriptResource(
			context,
			"prototype.js",
			"text/javascript"
		);
		this.renderScriptResource(
			context,
			"rico.js",
			"text/javascript"
		);

		// Start the encoding of the enclosing DIV
		// The id of the DIV, specified in a JSP page
		// with a tag attribute, is significant: it's used
		// by encodeEnd() to wire the DIV to a Rico component
		Map<String,Object> attrs = null;
		attrs = component.getAttributes();

		String styleClass = (String) attrs.get("styleClass");

		//writer.write("<div id='" + (String) attrs.get("name") + "'");
		writer.write("<div id='" + component.getClientId(context) + "'");

		if (!Strings.IsNullOrEmpty(styleClass))
		{
			writer.write(" class='" + styleClass + "' ");
		}

		writer.write(">");
	}

	@Override
	protected void doEncodingEnd(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();

		// Finish the encoding of the enclosing DIV
		writer.write("</div>");

		// Write the JS that creates the Rico Accordian component
		Map<String,Object> attrs = null;
		attrs = component.getAttributes();

		//String div = (String) attrs.get("name");
		String divId = component.getClientId(context);

		this.renderScriptInline(
			context, 
			"new Rico.Accordion( $('" + divId + "'), "
			+ "{"
			+ " panelHeight: " + attrs.get("panelHeight")
			+ "}"
			+ ");",
			"text/javascript"
		);
	}
}
