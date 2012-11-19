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

package it.unipmn.di.dcs.sharegrid.web.faces.component;

import java.util.Iterator;

import javax.el.ValueExpression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * Utility class for UI components.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UIComponentUtil
{

	/** A constructor. */
	private UIComponentUtil()
	{
		// empty
	}

	/**
	 * Return a child with the specified component id from the specified
	 * component.
	 *
	 * This method will NOT create a new <code>UIComponent</code>.
	 *
	 * @param parent <code>UIComponent</code> to be searched
	 * @param id Component id (or facet name) to search for
	 *
	 * @return The child <code>UIComponent</code> if it exists, null
	 * otherwise.
	 */
	public static UIComponent GetChild(UIComponent parent, String id)
	{
		return FindChild(parent, id, id);
	}

	/**
	 * Return a child with the specified component id (or facetName) from
	 * the specified component.
	 *
	 * If not found, return <code>null</code>.
	 * <code>facetName</code> or <code>id</code> may be null to avoid
	 * searching the facet Map or the <code>parent</code>'s children.
	 *
	 * This method will NOT create a new <code>UIComponent</code>.
	 *
	 * @param parent <code>UIComponent</code> to be searched
	 * @param id id to search for
	 * @param facetName Facet name to search for
	 *
	 * @return The child <code>UIComponent</code> if it exists, null otherwise.
	 */
	public static UIComponent FindChild(UIComponent parent, String id, String facetName)
	{
		// sanity check
		if (parent == null)
		{
			return null;
		}

		// First search for facet

		UIComponent child = null;

		if (facetName != null)
		{
			child = parent.getFacets().get(facetName);
			if (child != null)
			{
				return child;
			}
		}

		// Then, search for component by id

		if (id != null)
		{
			Iterator<UIComponent> it = parent.getChildren().iterator();
			while (it.hasNext())
			{
				child = it.next();
				if (id.equals(child.getId()))
				{
					return (child);
				}
			}
		}

		// Not found, return null

		return null;
	}

	/**
	 * Returns true if this expression looks like an EL expression.
	 */
	public static boolean IsValueReference(String value)
	{
		if (value == null)
		{
			return false;
		}
		// FIXME: Consider adding logic to look for "matching" {}'s
		int start = value.indexOf("#{");
		if ( start == -1 )
		{
			return false;
		}

		int end = value.indexOf('}', start);
		if ( end == -1 )
		{
			return false;
		}
		return true;
	}

	/**
	 * Return the value of the given attribute for the given component and in
	 * the given context.
	 *
	 * If the attribute has not been set, return the given default value.
	 * If the given context is {@code null}, try to get the value from the
	 * current context.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T GetAttributeValue(FacesContext context, UIComponent component, String attrName, T defaultValue)
	{
		ValueExpression ve = component.getValueExpression(attrName);

		if (context == null)
		{
			context = FacesContext.getCurrentInstance();
		}

		return	( ve != null )
			? (T) ve.getValue( (context != null ? context.getELContext() : null) )
			: defaultValue;
	}

	/**
	 * Return the value of the given attribute for the given component and in
	 * the current context.
	 *
	 * If the attribute has not been set, return the given default value.
	 */
	public static <T> T GetAttributeValue(UIComponent component, String attrName, T defaultValue)
	{
		return UIComponentUtil.GetAttributeValue(
			FacesContext.getCurrentInstance(),
			component,
			attrName,
			defaultValue
		);
	}

	/**
	 * Return the current value of the given attribute if it is not
	 * {@code null}, otherwise return value of the given attribute for the given
	 * component and in the given context.
	 *
	 * If the attribute has not been set, return the given default value.
	 */
	public static <T> T GetAttributeValueIfNull(FacesContext context, UIComponent component, T currentValue, String attrName, T defaultValue)
	{
		if (currentValue != null)
		{
			return currentValue;
		}

		return UIComponentUtil.GetAttributeValue(
			context,
			component,
			attrName,
			defaultValue
		);
	}

	@Deprecated
	public static <T> T GetValue(FacesContext context, UIComponent comp, String attrName, T defaultValue)
	{
		return GetAttributeValue(context, comp, attrName, defaultValue);
	}

	@Deprecated
	public static <T> T GetValue(UIComponent comp, String attrName, T defaultValue)
	{
		return GetAttributeValue(comp, attrName, defaultValue);
	}

	/**
	 * Return a default value for properties with a {@code boolean} primitive
	 * type.
	 */
	public static boolean GetBooleanDefaultPropertyValue()
	{
		return false;
	}

	/**
	 * Return a default value for properties with a {@code byte} primitive
	 * type.
	 */
	public static byte GetByteDefaultPropertyValue()
	{
		return Byte.MIN_VALUE;
	}

	/**
	 * Return a default value for properties with a {@code char} primitive
	 * type.
	 */
	public static char GetCharDefaultPropertyValue()
	{
		return Character.MIN_VALUE;
	}

	/**
	 * Return a default value for properties with a {@code double} primitive
	 * type.
	 */
	public static double GetDoubleDefaultPropertyValue()
	{
		return Double.MIN_VALUE;
	}

	/**
	 * Return a default value for properties with a {@code float} primitive
	 * type.
	 */
	public static float GetFloatDefaultPropertyValue()
	{
		return Float.MIN_VALUE;
	}

	/**
	 * Return a default value for properties with a {@code int} primitive
	 * type.
	 */
	public static int GetIntDefaultPropertyValue()
	{
		return Integer.MIN_VALUE;
	}

	/**
	 * Return a default value for properties with a {@code long} primitive
	 * type.
	 */
	public static long GetLongDefaultPropertyValue()
	{
		return Long.MIN_VALUE;
	}

	/**
	 * Return a default value for properties with a {@code short} primitive
	 * type.
	 */
	public static short GetShortDefaultPropertyValue()
	{
		return Short.MIN_VALUE;
	}
}
