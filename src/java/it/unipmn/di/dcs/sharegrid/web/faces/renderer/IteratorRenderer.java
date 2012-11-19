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

package it.unipmn.di.dcs.sharegrid.web.faces.renderer;

import it.unipmn.di.dcs.sharegrid.web.faces.renderer.util.RendererUtil;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesUtil;

import java.io.IOException;
import java.util.Iterator;

//import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * {@code Renderer} that supports generating markup for the per-row data
 * associated with a {@code UIData} component.
 *
 * You can easily specialize the behavior of the {@code Renderer} by subclassing
 * and overriding the <code>tableBegin()</code>, <code>rowBegin()</code>,
 * <code>rowBody()</code>, <code>rowEnd()</code>, and <code>tableEnd()</code>
 * methods.  The default implementation renders an HTML table with
 * headers and footers.</p>
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class IteratorRenderer extends AbstractRenderer
{
	/**
	 * <p>Render the beginning of the table for our associated data.</p>
	 *
	 * @param context   <code>FacesContext</code> for the current request
	 * @param component <code>UIComponent</code> being rendered
	 *
	 * @throws IOException if an input/output error occurs
	 */
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
System.err.println("IteratorRenderer::encodeBegin>> Entering");//XXX
		// Make sure this component is a 'otherwise' component
		FacesUtil.AssertValidParams( context, component, UIData.class );

		if ( !component.isRendered() )
		{
			return;
		}

		super.encodeBegin(context, component);

		ResponseWriter writer = context.getResponseWriter();
		UIData data = (UIData) component;

		// Render the beginning of this list
		data.setRowIndex(-1);

		this.iterationBegin(context, data, writer);
System.err.println("IteratorRenderer::encodeBegin>> Exiting");//XXX
	}

	/**
	 * <p>Render the body rows of the table for our associated data.</p>
	 *
	 * @param context   <code>FacesContext</code> for the current request
	 * @param component <code>UIComponent</code> being rendered
	 *
	 * @throws IOException if an input/output error occurs
	 */
	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException
	{
System.err.println("IteratorRenderer::encodeChildren>> Entering");//XXX
		// super.encodeChildren(context, component); //FIXME
		ResponseWriter writer = context.getResponseWriter();
		UIData data = (UIData) component;

		int processed = 0;
		int rowIndex = data.getFirst() - 1;
		int rows = data.getRows();

System.err.println("IteratorRenderer::encodeChildren>> rows: " + rows);//XXX
		// Iterate over the specified rows of data
		while (true)
		{
System.err.println("IteratorRenderer::encodeChildren>> rowIndex: " + rowIndex);//XXX
			// Have we displayed the requested number of rows?
			if ((rows > 0) && (++processed > rows))
			{
				break;
			}

			// Select the next row (if there is one)
			data.setRowIndex(++rowIndex);
System.err.println("IteratorRenderer::encodeChildren>> row available: " + data.isRowAvailable());//XXX
			if (!data.isRowAvailable())
			{
				break;
			}

			// Render the beginning, body, and ending of this row
System.err.println("IterationRenderer::encodeChildren>> renderer item begin");//XXX
			this.iterationItemBegin(context, data, writer);
System.err.println("IterationRenderer::encodeChildren>> renderer item body");//XXX
			this.iterationItemBody(context, data, writer);
System.err.println("IterationRenderer::encodeChildren>> renderer item end");//XXX
			this.iterationItemEnd(context, data, writer);
		}
System.err.println("IteratorRenderer::encodeChildren>> Exiting");//XXX
	}

	/**
	 * <p>Render the ending of the table for our associated data.</p>
	 *
	 * @param context   <code>FacesContext</code> for the current request
	 * @param component <code>UIComponent</code> being rendered
	 *
	 * @throws IOException if an input/output error occurs
	 */
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
System.err.println("IteratorRenderer::encodeEnd>> Entering");//XXX
		super.encodeEnd(context, component);

		ResponseWriter writer = context.getResponseWriter();
		UIData data = (UIData) component;

		// Render the ending of this table
		data.setRowIndex(-1);

		this.iterationEnd(context, data, writer);
System.err.println("IteratorRenderer::encodeEnd>> Exiting");//XXX
	}

	/**
	 * <p>Return <code>true</code> to indicate that we do indeed wish to be
	 * responsible for rendering the children of the associated component.</p>
	 */
	@Override
	public boolean getRendersChildren()
	{
		return true;
	}

	/**
	 * <p>Return the number of children are registered with the specified
	 * {@code UIData}.
	 *
	 * @param data {@code UIData} component for which to count
	 */
	protected static int GetIterationItemCount(UIData data)
	{
		int n = 0;
		Iterator kids = data.getChildren().iterator();
		while (kids.hasNext())
		{
//			if (kids.next() instanceof UIColumn)
//			{
				n++;
//			}
		}
		return n;
	}

//	/**
//	 * <p>Return the number of child com.sun.javaee.blueprints.components.ui of type <code>UIColumn</code>
//	 * are registered with the specified <code>UIData</code> component
//	 * and have a facet named <code>footer</code>.</p>
//	 *
//	 * @param data <code>UIData</code> component for which to count
//	 */
//	protected int getIterationItemFooterCount(UIData data)
//	{
//		int n = 0;
//		Iterator kids = data.getChildren().iterator();
//		while (kids.hasNext())
//		{
//			UIComponent kid = (UIComponent) kids.next();
//			if ((kid instanceof UIColumn) && (kid.getFacet("footer") != null))
//			{
//				n++;
//			}
//		}
//		return (n);
//	}


//	/**
//	 * <p>Return the number of child com.sun.javaee.blueprints.components.ui of type <code>UIColumn</code>
//	 * are registered with the specified <code>UIData</code> component
//	 * and have a facet named <code>header</code>.</p>
//	 *
//	 * @param data <code>UIData</code> component for which to count
//	 */
//	protected int getIterationItemHeaderCount(UIData data)
//	{
//		int n = 0;
//		Iterator kids = data.getChildren().iterator();
//		while (kids.hasNext()) {
//			UIComponent kid = (UIComponent) kids.next();
//			if ((kid instanceof UIColumn) && (kid.getFacet("header") != null)) {
//				n++;
//			}
//		}
//		return (n);
//	}


	/**
	 * <p>Render the markup for the beginning of the current body row.  The
	 * default implementation renders <code>&lt;tr&gt;</code>.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param data    <code>UIData</code> being rendered
	 * @param writer  <code>ResponseWriter</code> to render to
	 *
	 * @exception IOException if an input/output error occurs
	 */
	protected void iterationItemBegin(FacesContext context, UIData data, ResponseWriter writer) throws IOException
	{
//		writer.startElement("tr", data);
		writer.writeText("\n", null);
	}


	/**
	 * <p>Render the markup for the content of the current body row.  The
	 * default implementation renders the descendant com.sun.javaee.blueprints.components.ui of each
	 * child <code>UIColumn</code>, surrounded by <code>&lt;td&gt;</code>
	 * and <code>&lt;/td&gt;</code>.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param data    <code>UIData</code> being rendered
	 * @param writer  <code>ResponseWriter</code> to render to
	 *
	 * @exception IOException if an input/output error occurs
	 */
	protected void iterationItemBody(FacesContext context, UIData data, ResponseWriter writer) throws IOException
	{
System.err.println("IteratorRenderer::iterationItemBody>> Entering");//XXX
		// Iterate over the UIColumn children of this UIData component
//		for (UIComponent column : data.getChildren())
//		{
//			// Only process UIColumn children
//			if (!(column instanceof UIColumn))
//			{
//				continue;
//			}
//
//			// Create the markup for this column
//			writer.startElement("td", column);
//			for (UIComponent child : column.getChildren())
//			{
//				RendererUtil.EncodeRecursive(context, child);
//			}
//			writer.endElement("td");
//			writer.writeText("\n", null);
//		}
		for (UIComponent child : data.getChildren())
		{
System.err.println("IteratorRenderer::iterationItemBody>> Renderering child: " + child.getClientId(context));//XXX
			RendererUtil.EncodeRecursive(context, child);
			writer.writeText("\n", null);
		}
System.err.println("IteratorRenderer::iterationItemBody>> Exiting");//XXX
	}

	/**
	 * <p>Render the markup for the ending of the current body row.  The
	 * default implementation renders <code>&lt;/tr&gt;</code>.</p>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param data    <code>UIData</code> being rendered
	 * @param writer  <code>ResponseWriter</code> to render to
	 *
	 * @exception IOException if an input/output error occurs
	 */
	protected void iterationItemEnd(FacesContext context, UIData data, ResponseWriter writer) throws IOException
	{
//		writer.endElement("tr");
		writer.writeText("\n", null);
	}

	/**
	 * <p>Render the markup for the beginnning of an entire table.  The
	 * default implementation renders:</p>
	 * <ul>
	 * <li>A <code>&lt;table&gt;</code> element.</li>
	 * <li>If the <code>UIData</code> component has a facet named
	 * <code>header</code>, render it in a table row with a
	 * <code>colspan</code> set to span all the columns in the table.</li>
	 * <li>If any of the child <code>UIColumn</code> com.sun.javaee.blueprints.components.ui has a facet
	 * named <code>header</code>, render them in a table row with a
	 * each header in a <code>&lt;th&gt;</code> element.</li>
	 * </ul>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param data    <code>UIData</code> being rendered
	 * @param writer  <code>ResponseWriter</code> to render to
	 *
	 * @exception IOException if an input/output error occurs
	 */
	protected void iterationBegin(FacesContext context, UIData data, ResponseWriter writer) throws IOException
	{
		// Render the outermost table element
//		writer.startElement("table", data);
//		String styleClass = (String) data.getAttributes().get("styleClass");
//		if (styleClass != null)
//		{
//			writer.writeAttribute("class", styleClass, "styleClass");
//		}
//		else
//		{
//			writer.writeAttribute("border", "0", null);
//			writer.writeAttribute("cellspacing", "5", null);
//		}
//		writer.writeText("\n", null);

		// Render the table and column headers (if any)
//		UIComponent header = data.getFacet("header");
//		int n = getIterationItemHeaderCount(data);
//		if ((header != null) || (n > 0))
//		{
//			writer.startElement("thead", header);
//		}
//		if (header != null)
//		{
//			writer.startElement("tr", header);
//			writer.startElement("th", header);
//			writer.writeAttribute("colspan", "" + getIterationItemCount(data), null);
//			writer.writeText("\n", null);
//			encodeRecursive(context, header);
//			writer.writeText("\n", null);
//			writer.endElement("th");
//			writer.endElement("tr");
//			writer.writeText("\n", null);
//		}
//		if (n > 0)
//		{
//			writer.startElement("tr", data);
//			writer.writeText("\n", null);
//			Iterator columns = data.getChildren().iterator();
//			while (columns.hasNext())
//			{
//				UIComponent column = (UIComponent) columns.next();
//				if (!(column instanceof UIColumn))
//				{
//					continue;
//				}
//				writer.startElement("th", column);
//				UIComponent facet = column.getFacet("header");
//				if (facet != null)
//				{
//					encodeRecursive(context, facet);
//				}
//				writer.endElement("th");
//				writer.writeText("\n", null);
//			}
//			writer.endElement("tr");
//			writer.writeText("\n", null);
//		}
//		if ((header != null) || (n > 0))
//		{
//			writer.endElement("thead");
//			writer.writeText("\n", null);
//		}

		// Render the beginning of the table body
//		writer.startElement("tbody", data);
		writer.writeText("\n", null);
	}

	/**
	 * <p>Render the markup for the ending of an entire table.  The
	 * default implementation renders:</p>
	 * <ul>
	 * <li>If any of the child <code>UIColumn</code> com.sun.javaee.blueprints.components.ui has a facet
	 * named <code>footer</code>, render them in a table row with a
	 * each footer in a <code>&lt;th&gt;</code> element.</li>
	 * <li>If the <code>UIData</code> component has a facet named
	 * <code>footer</code>, render it in a table row with a
	 * <code>colspan</code> set to span all the columns in the table.</li>
	 * <li>A <code>&lt;/table&gt;</code> element.</li>
	 * </ul>
	 *
	 * @param context <code>FacesContext</code> for the current request
	 * @param data    <code>UIData</code> being rendered
	 * @param writer  <code>ResponseWriter</code> to render to
	 *
	 * @exception IOException if an input/output error occurs
	 */
	protected void iterationEnd(FacesContext context, UIData data, ResponseWriter writer) throws IOException
	{
		// Render the end of the table body
//		writer.endElement("tbody");
		writer.writeText("\n", null);

//		// Render the table and column footers (if any)
//		UIComponent footer = data.getFacet("footer");
//		int n = getIterationItemFooterCount(data);
//		if ((footer != null) || (n > 0))
//		{
//			writer.startElement("tfoot", footer);
//		}
//		if (n > 0)
//		{
//			writer.startElement("tr", data);
//			writer.writeText("\n", null);
//			Iterator columns = data.getChildren().iterator();
//			while (columns.hasNext())
//			{
//				UIComponent column = (UIComponent) columns.next();
//				if (!(column instanceof UIColumn))
//				{
//					continue;
//				}
//				writer.startElement("th", column);
//				UIComponent facet = column.getFacet("footer");
//				if (facet != null)
//				{
//					encodeRecursive(context, facet);
//				}
//				writer.endElement("th");
//				writer.writeText("\n", null);
//			}
//			writer.endElement("tr");
//			writer.writeText("\n", null);
//		}
//		if (footer != null)
//		{
//			writer.startElement("tr", footer);
//			writer.startElement("th", footer);
//			writer.writeAttribute("colspan", "" + getIterationItemCount(data), null);
//			writer.writeText("\n", null);
//			encodeRecursive(context, footer);
//			writer.writeText("\n", null);
//			writer.endElement("th");
//			writer.endElement("tr");
//			writer.writeText("\n", null);
//		}
//		if ((footer != null) || (n > 0))
//		{
//			writer.endElement("tfoot");
//			writer.writeText("\n", null);
//		}
//
//		// Render the ending of the outermost table element
//		writer.endElement("table");
//		writer.writeText("\n", null);
	}
}
