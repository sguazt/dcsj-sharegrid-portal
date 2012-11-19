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

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIPanelGridComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.component.UIPanelGroupComponent;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * <b>HtmlPanelGridRenderer</b> is a class that renders
 * {@code UIPanelGridComponent} component as a "Grid".
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class HtmlPanelGridRenderer extends AbstractHtmlTableRenderer
{
	@Override
	public void doEncodingBegin(FacesContext context, UIComponent component) throws IOException
	{
		// Render the beginning of this panel
		ResponseWriter writer = context.getResponseWriter();
		this.renderTableStart(context, component, writer, UIPanelGridComponent.PASSTHROUGH_ATTRIBUTES);

		// render the caption facet (if present)
		this.renderCaption(context, component, writer);

		// Render the header facet (if any)
		this.renderHeader(context, component, writer);

		// Render the footer facet (if any)
		this.renderFooter(context, component, writer);
	}

	@Override
	public void doEncodingChildren(FacesContext context, UIComponent component) throws IOException
	{
		// Set up the variables we will need
		ResponseWriter writer = context.getResponseWriter();
		AbstractHtmlTableRenderer.TableMetaInfo info = getMetaInfo(component);
		int columnCount = info.columns.size();
		boolean open = false;
		int i = 0;

		// Render our children, starting a new row as needed
		this.renderTableBodyStart(component, writer);
		for ( Iterator<UIComponent> kids = this.getChildren(component); kids.hasNext(); )
		{
			UIComponent child = kids.next();
			if (!child.isRendered())
			{
				continue;
			}
			if ((i % columnCount) == 0)
			{
				if (open)
				{
					this.renderRowEnd(component, writer);
					open = false;
				}
				this.renderRowStart(component, writer);
				open = true;
				info.columnStyleCounter = 0;
			}
			int colSpan;
			colSpan = this.renderRow(context, component, child, writer);
			//i++;
			i += colSpan;
		}
		if (open)
		{
			this.renderRowEnd(component, writer);
		}
		this.renderTableBodyEnd(component, writer);
	}

	@Override
	public void doEncodingEnd(FacesContext context, UIComponent component) throws IOException
	{
		// Render the ending of this panel
		this.renderTableEnd(component, context.getResponseWriter());

		this.clearMetaInfo(component);
	}

	@Override
	public boolean getRendersChildren()
	{
		return true;
	}

	@Override
	protected int renderRow(FacesContext context, UIComponent table, UIComponent child, ResponseWriter writer) throws IOException
	{
		int colSpan = 1;
		AbstractHtmlTableRenderer.TableMetaInfo info = this.getMetaInfo(table);
		writer.startElement("td", table);
		if ( info.columnClasses.length > 0 )
		{
			writer.writeAttribute("class", info.getCurrentColumnClass(), "columns");
		}
		if ( child instanceof UIPanelGroupComponent )
		{
			UIPanelGroupComponent group = (UIPanelGroupComponent) child;
			colSpan = group.getColSpan();
			if ( colSpan > 1 )
			{
				if ( colSpan > info.columns.size() )
				{
					colSpan = info.columns.size();
				}

				writer.writeAttribute("colspan", Integer.toString( colSpan ), "colSpan");
			}
		}
		this.encodeRecursive(context, child);
		writer.endElement("td");
		writer.writeText("\n", table, null);
		return colSpan;
	}

	@Override
	protected void renderHeader(FacesContext context, UIComponent table, ResponseWriter writer) throws IOException
	{
		AbstractHtmlTableRenderer.TableMetaInfo info = this.getMetaInfo(table);
		UIComponent header = this.getFacet(table, "header");
		String headerClass = (String) table.getAttributes().get("headerClass");
		if (header != null)
		{
			writer.startElement("thead", table);
			writer.writeText("\n", table, null);
			writer.startElement("tr", header);
			writer.startElement("th", header);
			if (headerClass != null)
			{
				writer.writeAttribute("class", headerClass, "headerClass");
			}
			writer.writeAttribute("colspan", String.valueOf(info.columns.size()), null);
			writer.writeAttribute("scope", "colgroup", null);
			this.encodeRecursive(context, header);
			writer.endElement("th");
			writer.endElement("tr");
			writer.writeText("\n", table, null);
			writer.endElement("thead");
			writer.writeText("\n", table, null);
		}
	}

	@Override
	protected void renderFooter(FacesContext context, UIComponent table, ResponseWriter writer) throws IOException
	{
		AbstractHtmlTableRenderer.TableMetaInfo info = this.getMetaInfo(table);
		UIComponent footer = this.getFacet(table, "footer");
		String footerClass = (String) table.getAttributes().get("footerClass");
		if (footer != null)
		{
			writer.startElement("tfoot", table);
			writer.writeText("\n", table, null);
			writer.startElement("tr", footer);
			writer.startElement("td", footer);
			if (footerClass != null)
			{
				writer.writeAttribute("class", footerClass, "footerClass");
			}
			writer.writeAttribute("colspan", String.valueOf(info.columns.size()), null);
			this.encodeRecursive(context, footer);
			writer.endElement("td");
			writer.endElement("tr");
			writer.writeText("\n", table, null);
			writer.endElement("tfoot");
			writer.writeText("\n", table, null);
		}
	}
}
