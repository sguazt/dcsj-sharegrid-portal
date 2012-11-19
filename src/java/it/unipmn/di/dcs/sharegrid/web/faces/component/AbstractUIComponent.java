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

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

/**
 * This abstract class provides base functionality for components that
 * that do not need any "special" component features.
 *
 * This class is suitable for non-visual components or for components
 * that simply aggregate other components together.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractUIComponent extends UIComponentBase implements IUIComponent
{
	/** A constructor. */
	protected AbstractUIComponent()
	{
		super();
	}

	/**
	 * This method will find the request child {@code UIComponent}
	 * by the given {@code id}.
	 *
	 * @param context The <code>FacesContext</code>.
	 * @param id The <code>UIComponent</code> id to find.
	 *
	 * @return The requested <code>UIComponent</code>.
	 */
	public UIComponent getChild(FacesContext context, String id)
	{
		if ((id == null) || (id.trim().equals("")))
		{
			return null;
		}

		UIComponent child = null;

		child = UIComponentUtil.FindChild( this, id, id );

		if (child != null)
		{
			return child;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T getPropertyValue(T field, String attrName, T defaultValue)
	{
		if (field != null)
		{
			return field;
		}

		ValueExpression ve = null;

		ve = this.getValueExpression( attrName );

		return	( ve != null )
			? (T) ve.getValue( ( this.getFacesContext() != null ? this.getFacesContext().getELContext() : null ) )
			: defaultValue;
	}
}
