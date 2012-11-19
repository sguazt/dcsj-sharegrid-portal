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
 * HTML renderer for the Accordion Panel component.
 *
 * For each panel, write out three DIVS that look like this:
 * <div>
 *   <div>
 *   Item's label goes here
 *   </div>
 *   <div>
 *   Included content (represented by the item's
 *   label) goes here
 *   </div>
 * </div>
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class HtmlOutputAccordionPanelRenderer extends AbstractHtmlRenderer
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

        // heading and styleClass attributes come from the component
        Map<String, Object> attrs = null;
		attrs = component.getAttributes();
        String heading = (String) attrs.get("heading");
        String styleClass = (String) attrs.get("styleClass");

        // headerClass and contentClass attributes
        // come from the component's parent
        Map<String, Object> parentAttrs = null;
		parentAttrs = component.getParent().getAttributes();
        String headerClass = (String) parentAttrs.get("headerClass");
        String contentClass = (String) parentAttrs.get("contentClass");

        // Write out the opening tag for the enclosing div...
        writer.write("<div");
        if (!Strings.IsNullOrEmpty(styleClass))
		{
           writer.write(" class='" + styleClass + "'");
		}
        writer.write(">");

        // Write out the header div
        // (both opening and closing DIVs)...
        writer.write("<div");
        if (!Strings.IsNullOrEmpty(headerClass))
		{
           writer.write(" class='" + headerClass + "'");
		}
        writer.write(">");
        writer.write(heading);
        writer.write("</div>");
 
        // Write out the opening tag for the content div...
        writer.write("<div");
        if (!Strings.IsNullOrEmpty(contentClass))
		{
           writer.write(" class='" + contentClass + "'");
		}
        writer.write(">");
	}

	@Override
	protected void doEncodingEnd(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();

        // Finish enclosing DIVs started in encodeBegin()
        writer.write("</div>"); // content DIV
        writer.write("</div>"); // enclosing DIV
	}
}
