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

import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 * The Accordion {@code UIComponent} can be used to create a panel bar menu.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UIOutputAccordionComponent extends AbstractUIOutputComponent
{
	public static final String COMPONENT_FAMILY = FacesConstants.OutputAccordionComponentFamily;
	public static final String COMPONENT_TYPE = FacesConstants.OutputAccordionComponentType;
	public static final String RENDERER_TYPE = FacesConstants.HtmlOutputAccordionRendererType;

	private Object[] values;

	/** A constructor. */
	public UIOutputAccordionComponent()
	{
		super();

		this.setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily()
	{
		return COMPONENT_FAMILY;
	}

	@Override
	public void restoreState(FacesContext context, Object state)
	{
		this.values = (Object[]) state;

		super.restoreState(context, values[0]);

		this.contentClass = (String) values[1];
		this.headerClass = (String) values[2];
		this.panelClass = (String) values[3];
		this.panelHeight = (Integer) values[4];
	}

	@Override
	public Object[] saveState(FacesContext context)
	{
		if (this.values == null)
		{
			this.values = new Object[5];
		}

		this.values[0] = super.saveState(context);

		this.values[1] = this.contentClass;
		this.values[2] = this.headerClass;
		this.values[3] = this.panelClass;
		this.values[4] = this.panelHeight;

		return this.values;
	}

	private String contentClass = null;
	public String getContentClass()
	{ 
		if (null != this.contentClass)
		{
			return this.contentClass;
		}

		ValueExpression ve = this.getValueExpression("contentClass");
		if (ve != null)
		{
			return (String) ve.getValue(this.getFacesContext().getELContext());
		}

		return null;
	}
	public void setContentClass(String value)
	{
		this.contentClass = value;
	}

	private String headerClass = null;
	public String getHeaderClass()
	{ 
		if (null != this.headerClass)
		{
			return this.headerClass;
		}

		ValueExpression ve = this.getValueExpression("headerClass");
		if (ve != null)
		{
			return (String) ve.getValue(this.getFacesContext().getELContext());
		}

		return null;
	}
	public void setHeaderClass(String value)
	{
		this.headerClass = value;
	}

	private String panelClass = null;
	public String getPanelClass()
	{ 
		if (null != this.panelClass)
		{
			return this.panelClass;
		}

		ValueExpression ve = this.getValueExpression("panelClass");
		if (ve != null)
		{
			return (String) ve.getValue(this.getFacesContext().getELContext());
		}

		return null;
	}
	public void setPanelClass(String value)
	{
		this.panelClass = value;
	}

	private Integer panelHeight;
	public int getPanelHeight()
	{ 
		if (null != this.panelHeight)
		{
			return this.panelHeight;
		}

		ValueExpression ve = this.getValueExpression("panelHeight");
		if (ve != null)
		{
			return (Integer) ve.getValue(this.getFacesContext().getELContext());
		}

		return -1; // means 100%
	}
	public void setPanelHeight(int value)
	{
		this.panelHeight = value;
	}
}
