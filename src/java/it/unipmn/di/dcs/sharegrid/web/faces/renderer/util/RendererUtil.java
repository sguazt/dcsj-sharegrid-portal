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

package it.unipmn.di.dcs.sharegrid.web.faces.renderer.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * Utility class for renderers.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class RendererUtil
{
	/**
	 * ResponseWriter Content Types and Encoding.
	 */
	public static final String HTML_CONTENT_TYPE = "text/html";
	public static final String XHTML_CONTENT_TYPE = "application/xhtml+xml";
	public static final String APPLICATION_XML_CONTENT_TYPE = "application/xml";
	public static final String TEXT_XML_CONTENT_TYPE = "text/xml";
	public static final String ALL_MEDIA = "*/*";
	public static final String CHAR_ENCODING = "ISO-8859-1";

	/**
	 * <p>The prefix to append to certain attributes when renderking
	 * <code>XHTML Transitional</code> content.
	 */
	private static final String XHTML_ATTR_PREFIX = "xml:";

	/**
	 * <p><code>Boolean</code> attributes to be rendered 
	 * using <code>XHMTL</code> semantics.
	 */
	private static final String[] BOOLEAN_ATTRIBUTES = {
		"checked",
		"disabled",
		"ismap",
		"readonly",
		"selected"
	};

	/**
	 * <p>An array of attributes that must be prefixed by
	 * {@link #XHTML_ATTR_PREFIX} when rendering 
	 * <code>XHTML Transitional</code> content.
	 */
	private static final String[] XHTML_PREFIX_ATTRIBUTES = {
		"lang"
	};

	/**
	 * The character that is used to delimit content types
	 * in an accept String.</p>
	 */
	private final static String CONTENT_TYPE_DELIMITER = ",";

	/**
	 * The character that is used to delimit the type and 
	 * subtype portions of a content type in an accept String.
	 * Example: text/html </p>
	 */
	private final static String CONTENT_TYPE_SUBTYPE_DELIMITER = "/";

	/** A constructor. */
	private RendererUtil()
	{
		// empty
	}

	/**
	 * <p>Render any "passthru" attributes, where we simply just output the
	 * raw name and value of the attribute.  This method is aware of the
	 * set of HTML4 attributes that fall into this bucket.  Examples are
	 * all the javascript attributes, alt, rows, cols, etc. </p>
	 * 
	 * @param writer writer the {@link javax.faces.context.ResponseWriter} to be used when writing
	 *  the attributes
	 * @param component the component
	 * @param attributes an array off attributes to be processed
	 * @throws IOException if an error occurs writing the attributes
	 */
	public static void RenderPassThruAttributes(ResponseWriter writer, UIComponent component, String[] attributes) throws IOException
	{
		assert (null != writer);
		assert (null != component);

		Map<String, Object> attrMap = component.getAttributes();

		boolean isXhtml = RendererUtil.XHTML_CONTENT_TYPE.equals(writer.getContentType());
		for (String attrName : attributes)
		{
			Object value = attrMap.get(attrName);
			if (value != null && RendererUtil.ShouldRenderAttribute(value))
			{
				writer.writeAttribute(RendererUtil.PrefixAttribute(attrName, isXhtml), value, attrName);
			}
		}
	}

	/** Adds a proper prefix to the given attribute. */
	public static String PrefixAttribute(final String attrName, final ResponseWriter writer)
	{
		return RendererUtil.PrefixAttribute(
			attrName,
			RendererUtil.XHTML_CONTENT_TYPE.equals( writer.getContentType() )
		);
	}

	/** Adds a proper prefix to the given attribute. */
	public static String PrefixAttribute(final String attrName, boolean isXhtml)
	{
		if (isXhtml)
		{
			if (Arrays.binarySearch(XHTML_PREFIX_ATTRIBUTES, attrName) > -1)
			{
				return XHTML_ATTR_PREFIX + attrName;
			}
			else
			{
				return attrName;
			}
		}
		else
		{
			return attrName;
		}
	}

	/**
	 * Renders the attributes from {@link #BOOLEAN_ATTRIBUTES} 
	 * using <code>XHMTL</code> semantics (i.e., disabled="disabled").
	 * 
	 * @param writer writer the {@link ResponseWriter} to be used when
	 * writing the attributes.
	 * @param component the component
	 * @throws IOException if an error occurs writing the attributes
	 */
	public static void RenderXHTMLStyleBooleanAttributes(ResponseWriter writer, UIComponent component) throws IOException
	{
		assert (writer != null);
		assert (component != null);

		Map attrMap = component.getAttributes();
		for (String attrName : BOOLEAN_ATTRIBUTES)
		{
			Object val = attrMap.get(attrName);
			if (val == null)
			{
				continue;
			}

			if (Boolean.valueOf(val.toString()))
			{
				writer.writeAttribute(attrName, true, attrName);
			}
		}
	}

	/**
	 * @param contentType the content type in question
	 * @return <code>true</code> if the content type is a known XML-based
	 *  content type, otherwise, <code>false</code>
	 */
	public static boolean IsXml(String contentType)
	{
		return	XHTML_CONTENT_TYPE.equals(contentType)
			|| APPLICATION_XML_CONTENT_TYPE.equals(contentType)
			|| TEXT_XML_CONTENT_TYPE.equals(contentType);
	}


	/**
	 * <p>For each attribute in <code>setAttributes</code>, perform a binary
	 * search against the array of <code>knownAttributes</code>  If a match is found
	 * and the value is not <code>null</code>, render the attribute.
	 * @param writer the current writer
	 * @param component the component whose attributes we're rendering
	 * @param knownAttributes an array of pass-through attributes supported by
	 *  this component
	 * @param setAttributes a <code>List</code> of attributes that have been set
	 *  on the provided component
	 * @throws IOException if an error occurs during the write
	 */
	private static void RenderPassThruAttributesOptimized(ResponseWriter writer, UIComponent component, String[] knownAttributes, List<String> setAttributes) throws IOException
	{
		String[] attributes = setAttributes.toArray(new String[setAttributes.size()]);
		Arrays.sort(attributes);
		boolean isXhtml = XHTML_CONTENT_TYPE.equals(writer.getContentType());
		Map<String, Object> attrMap = component.getAttributes();
		for (String name : attributes)
		{
			if (Arrays.binarySearch(knownAttributes, name) >= 0)
			{
				Object value = attrMap.get(name);
				if (value != null && RendererUtil.ShouldRenderAttribute(value))
				{
					writer.writeAttribute(RendererUtil.PrefixAttribute(name, isXhtml), value, name);
				}
			}
		}
	}

	/**
	 * <p>Determines if an attribute should be rendered based on the
	 * specified #attributeVal.</p>
	 * 
	 * @param attributeVal the attribute value
	 * @return <code>true</code> if and only if #attributeVal is
	 *  an instance of a wrapper for a primitive type and its value is
	 *  equal to the default value for that type as given in the specification.
	 */
	private static boolean ShouldRenderAttribute(Object attributeVal)
	{
		if (attributeVal instanceof String)
		{
			return true;
		}
		else if (attributeVal instanceof Boolean && Boolean.FALSE.equals(attributeVal))
		{
			return false;
		}
		else if (attributeVal instanceof Integer && (Integer) attributeVal == Integer.MIN_VALUE)
		{
			return false;
		}
		else if (attributeVal instanceof Double && (Double) attributeVal == Double.MIN_VALUE)
		{
			return false;
		}
		else if (attributeVal instanceof Character && (Character) attributeVal == Character.MIN_VALUE)
		{
			return false;
		}
		else if (attributeVal instanceof Float && (Float) attributeVal == Float.MIN_VALUE)
		{
			return false;
		}
		else if (attributeVal instanceof Short && (Short) attributeVal == Short.MIN_VALUE)
		{
			return false;
		}
		else if (attributeVal instanceof Byte && (Byte) attributeVal == Byte.MIN_VALUE)
		{
			return false;
		}
		else if (attributeVal instanceof Long && (Long) attributeVal == Long.MIN_VALUE)
		{
			return false;
		}
		return true;
	}

	/** Render the children of the given component. */
	public static void RenderChildren(FacesContext facesContext, UIComponent component) throws IOException
	{
		if (component.getChildCount() > 0)
		{
			for (UIComponent child : component.getChildren())
			//for (Iterator it = component.getChildren().iterator(); it.hasNext();)
			{
				//UIComponent child = (UIComponent) it.next();
				RenderChild(facesContext, child);
			}
		}
	}

	/** Render the given child of the a component. */
	public static void RenderChild(FacesContext facesContext, UIComponent child) throws IOException
	{
		if ( !child.isRendered() )
		{
			return;
		}

		child.encodeBegin(facesContext);

		if (child.getRendersChildren())
		{
			child.encodeChildren(facesContext);
		}
		else
		{
			RenderChildren(facesContext, child);
		}
		child.encodeEnd(facesContext);
	}

	/** Recursively render the given component and its children. */
	public static void EncodeRecursive(FacesContext context, UIComponent component) throws IOException
	{
		component.encodeBegin(context);

		if (component.getRendersChildren())
		{
			component.encodeChildren(context);
		}
		else
		{
			for (UIComponent child : component.getChildren())
			{
				EncodeRecursive(context, child);
			}
		}

		component.encodeEnd(context);
	}

	/**
	 * Render a component.
	 * @param component The component to render
	 * @param context The FacesContext of the request
	 *
	 */
	public static void RenderComponent(UIComponent component, FacesContext context) throws IOException
	{
		// This is to workaround a JSF bug with tables where client ids are
		// cached, especially for facets. By setting the id, we ensure ids are
		// recreated for each row of the table.
		String id = component.getId();
		if (id != null)
		{
			component.setId(id);
		}
		// Calling encodeBegin, encodeChildren, and encodeEnd directly is no
		// longer necessary here.
		component.encodeAll(context);
	}
}
