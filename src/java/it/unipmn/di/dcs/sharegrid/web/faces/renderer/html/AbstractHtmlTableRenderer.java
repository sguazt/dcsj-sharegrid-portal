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

import it.unipmn.di.dcs.sharegrid.web.faces.renderer.util.RendererUtil;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import javax.faces.component.UIColumn;
import javax.faces.component.UIData;

/**
 * Base class for concrete Grid and Table renderers.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractHtmlTableRenderer extends AbstractHtmlRenderer
{
	private static final Pattern CommaPattern = Pattern.compile(",");

	/**
	 * Called to render the opening/closing <code>thead</code> elements
	 * and any content nested between.
	 * @param context the <code>FacesContext</code> for the current request
	 * @param table the table that's being rendered
	 * @param writer the current writer
	 * @throws IOException if content cannot be written
	 */
	protected abstract void renderHeader(FacesContext context, UIComponent table, ResponseWriter writer) throws IOException;

	/**
	 * Called to render the opening/closing <code>tfoot</code> elements
	 * and any content nested between.
	 * @param context the <code>FacesContext</code> for the current request
	 * @param table the table that's being rendered
	 * @param writer the current writer
	 * @throws IOException if content cannot be written
	 */
	protected abstract void renderFooter(FacesContext context, UIComponent table, ResponseWriter writer) throws IOException;

	/**
	 * Call to render the content that should be included between opening
	 * and closing <code>tr</code> elements.
	 * @param context the <code>FacesContext</code> for the current request
	 * @param table the table that's being rendered
	 * @param row the current row (if any - an implmenetation may not need this)
	 * @param writer the current writer
	 * @return the number of columns (i.e. the column span) the
	 * <code>row</code> component spans in the current table row.
	 * @throws IOException if content cannot be written
	 */
	protected abstract int renderRow(FacesContext context, UIComponent table, UIComponent row, ResponseWriter writer) throws IOException;

	/**
	 * Renders the start of a table and applies the value of
	 * <code>styleClass</code> if available and renders any
	 * pass through attributes that may be specified.
	 * @param context the <code>FacesContext</code> for the current request
	 * @param table the table that's being rendered
	 * @param writer the current writer
	 * @param passThroughAttributes pass-through attributes that the component
	 *  supports
	 * @throws IOException if content cannot be written
	 */
	protected void renderTableStart(FacesContext context, UIComponent table, ResponseWriter writer, String[] passThroughAttributes) throws IOException
	{
		writer.startElement("table", table);
		writeIdAttributeIfNecessary(context, writer, table);
		String styleClass = (String) table.getAttributes().get("styleClass");
		if (styleClass != null)
		{
			writer.writeAttribute("class", styleClass, "styleClass");
		}
		RendererUtil.RenderPassThruAttributes(writer, table, passThroughAttributes);
		writer.writeText("\n", table, null);
	}


	/**
	 * Renders the closing <code>table</code> element.
	 * @param table the table that's being rendered
	 * @param writer the current writer
	 * @throws IOException if content cannot be written
	 */
	protected void renderTableEnd(UIComponent table, ResponseWriter writer) throws IOException
	{
		writer.endElement("table");
		writer.writeText("\n", table, null);
	}


	/**
	 * Renders the caption of the table applying the values of
	 * <code>captionClass</code> as the class and <code>captionStyle</code>
	 * as the style if either are present.
	 * @param context the <code>FacesContext</code> for the current request
	 * @param table the table that's being rendered
	 * @param writer the current writer
	 * @throws IOException if content cannot be written
	 */
	protected void renderCaption(FacesContext context, UIComponent table, ResponseWriter writer) throws IOException
	{
		UIComponent caption = getFacet(table, "caption");
		if (caption != null)
		{
			String captionClass = (String) table.getAttributes().get("captionClass");
			String captionStyle = (String)
			table.getAttributes().get("captionStyle");
			writer.startElement("caption", table);
			if (captionClass != null)
			{
				writer.writeAttribute("class", captionClass, "captionClass");
			}
			if (captionStyle != null)
			{
				writer.writeAttribute("style", captionStyle, "captionStyle");
			}
			encodeRecursive(context, caption);
			writer.endElement("caption");
		}
	}

	/**
	 * Renders the starting <code>tbody</code> element.
	 * @param table the table that's being rendered
	 * @param writer the current writer
	 * @throws IOException if content cannot be written
	 */
	protected void renderTableBodyStart(UIComponent table, ResponseWriter writer) throws IOException
	{
		writer.startElement("tbody", table);
		writer.writeText("\n", table, null);
	}

	/**
	 * Renders the closing <code>tbody</code> element.
	 * @param table the table that's being rendered
	 * @param writer the current writer
	 * @throws IOException if content cannot be written
	 */
	protected void renderTableBodyEnd(UIComponent table, ResponseWriter writer) throws IOException
	{
		writer.endElement("tbody");
		writer.writeText("\n", table, null);
	}

	/**
	 * Renders the starting <code>tr</code> element applying any values
	 * from the <code>rowClasses</code> attribute.
	 * @param table the table that's being rendered
	 * @param writer the current writer
	 * @throws IOException if content cannot be written
	 */
	protected void renderRowStart(UIComponent table, ResponseWriter writer) throws IOException
	{
		AbstractHtmlTableRenderer.TableMetaInfo info = this.getMetaInfo(table);
		writer.startElement("tr", table);
		if (info.rowClasses.length > 0)
		{
			writer.writeAttribute("class", info.getCurrentRowClass(), "rowClasses");
		}
		writer.writeText("\n", table, null);
	}

	/**
	 * Renders the closing <code>rt</code> element.
	 * @param table the table that's being rendered
	 * @param writer the current writer
	 * @throws IOException if content cannot be written
	 */
	protected void renderRowEnd(UIComponent table, ResponseWriter writer) throws IOException
	{
		writer.endElement("tr");
		writer.writeText("\n", table, null);
	}

	/**
	 * Returns a <code>TableMetaInfo</code> object containing details such
	 * as row and column classes, columns, and a mechanism for scrolling through
	 * the row/column classes.
	 * @param table the table that's being rendered
	 * @return the <code>TableMetaInfo</code> for provided table
	 */
	protected AbstractHtmlTableRenderer.TableMetaInfo getMetaInfo(UIComponent table)
	{
		AbstractHtmlTableRenderer.TableMetaInfo info = (AbstractHtmlTableRenderer.TableMetaInfo) table.getAttributes().get(AbstractHtmlTableRenderer.TableMetaInfo.KEY);
		if (info == null)
		{
			info = new AbstractHtmlTableRenderer.TableMetaInfo(table);
			table.getAttributes().put(AbstractHtmlTableRenderer.TableMetaInfo.KEY, info);
		}
		return info;
	}


	/**
	 * Removes the cached TableMetaInfo from the specified component.
	 * @param table the table from which the TableMetaInfo will be removed
	 */
	protected void clearMetaInfo(UIComponent table)
	{
		table.getAttributes().remove(AbstractHtmlTableRenderer.TableMetaInfo.KEY);
	}

	protected static class TableMetaInfo
	{
		private static final UIColumn PLACE_HOLDER_COLUMN = new UIColumn();
		private static final String[] EMPTY_STRING_ARRAY = new String[0];
		public static final String KEY = TableMetaInfo.class.getName();

		public final String[] rowClasses;
		public final String[] columnClasses;
		public final List<UIColumn> columns;
		public final boolean hasHeaderFacets;
		public final boolean hasFooterFacets;
		public int columnStyleCounter;
		public int rowStyleCounter;

		public TableMetaInfo(UIComponent table)
		{
			rowClasses = getRowClasses(table);
			columnClasses = getColumnClasses(table);
			columns = getColumns(table);
			hasHeaderFacets = hasFacet("header", columns);
			hasFooterFacets = hasFacet("footer", columns);
		}

		/**
		 * Obtain the column class based on the current counter.  Calling this
		 * method automatically moves the pointer to the next style.  If the
		 * counter is larger than the number of total classes, the counter will
		 * be reset.
		 * @return the current style
		 */
		public String getCurrentColumnClass()
		{
			String style = columnClasses[columnStyleCounter++];
			if (columnStyleCounter >= columnClasses.length)
			{
				columnStyleCounter = 0;
			}
			return style;
		}

		/**
		 * Obtain the row class based on the current counter.  Calling this
		 * method automatically moves the pointer to the next style.  If the
		 * counter is larger than the number of total classes, the counter will
		 * be reset.
		 * @return the current style
		 */
		public String getCurrentRowClass()
		{
			String style = rowClasses[rowStyleCounter++];
			if (rowStyleCounter >= rowClasses.length)
			{
				rowStyleCounter = 0;
			}
			return style;
		}

		/**
		 * <p>Return an array of stylesheet classes to be applied to each column in
		 * the table in the order specified. Every column may or may not have a
		 * stylesheet.</p>
		 *
		 * @param table {@link javax.faces.component.UIComponent} component being rendered
		 *
		 * @return an array of column classes
		 */
		private static String[] getColumnClasses(UIComponent table)
		{
			String values = (String) table.getAttributes().get("columnClasses");
			if (values == null)
			{
				return EMPTY_STRING_ARRAY;
			}
			return AbstractHtmlTableRenderer.CommaPattern.split(values.trim(), 0);
		}

		/**
		 * <p>Return an Iterator over the <code>UIColumn</code> children of the
		 * specified <code>UIData</code> that have a <code>rendered</code> property
		 * of <code>true</code>.</p>
		 *
		 * @param table the table from which to extract children
		 *
		 * @return the List of all UIColumn children
		 */
		private static List<UIColumn> getColumns(UIComponent table)
		{
			if (table instanceof UIData)
			{
				int childCount = table.getChildCount();
				if (childCount > 0)
				{
					List<UIColumn> results = new ArrayList<UIColumn>(childCount);
					for (UIComponent kid : table.getChildren())
					{
						if ((kid instanceof UIColumn) && kid.isRendered())
						{
							results.add((UIColumn) kid);
						}
					}
					return results;
				}
				else
				{
					return Collections.emptyList();
				}
			}
			else
			{
				int count;
				Object value = table.getAttributes().get("columns");
				if ((value != null) && (value instanceof Integer))
				{
					count = ((Integer) value);
				}
				else
				{
					count = 2;
				}
				if (count < 1)
				{
					count = 1;
				}
				List<UIColumn> result = new ArrayList<UIColumn>(count);
				for (int i = 0; i < count; i++)
				{
					result.add(PLACE_HOLDER_COLUMN);
				}
				return result;
			}
		}

		/**
		 * <p>Return the number of child <code>UIColumn</code> components nested in
		 * the specified <code>UIData</code> that have a facet with the specified
		 * name.</p>
		 *
		 * @param name    Name of the facet being analyzed
		 * @param columns the columns to search
		 *
		 * @return the number of columns associated with the specified Facet name
		 */
		private static boolean hasFacet(String name, List<UIColumn> columns)
		{
			if (!columns.isEmpty())
			{
				for (UIColumn column : columns)
				{
					if (column.getFacetCount() > 0)
					{
						if (column.getFacets().containsKey(name))
						{
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * <p>Return an array of stylesheet classes to be applied to each row in the
		 * table, in the order specified.  Every row may or may not have a
		 * stylesheet.</p>
		 *
		 * @param table {@link javax.faces.component.UIComponent} component being rendered
		 *
		 * @return an array of row classes
		 */
		private static String[] getRowClasses(UIComponent table)
		{
			String values = (String) table.getAttributes().get("rowClasses");
			if (values == null)
			{
				return (EMPTY_STRING_ARRAY);
			}
			return AbstractHtmlTableRenderer.CommaPattern.split(values.trim(), 0);
		}
	}
}
