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
 * The Accordion Panel {@code UIComponent} can be used to create a single panel
 * for the Accordion component.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UIOutputAccordionPanelComponent extends AbstractUIOutputComponent
{
	public static final String COMPONENT_FAMILY = FacesConstants.OutputAccordionPanelComponentFamily;
	public static final String COMPONENT_TYPE = FacesConstants.OutputAccordionPanelComponentType;
	public static final String RENDERER_TYPE = FacesConstants.HtmlOutputAccordionPanelRendererType;

	private Object[] values;

	/** A constructor. */
	public UIOutputAccordionPanelComponent()
	{
		super();

		this.setRendererType( UIOutputAccordionPanelComponent.RENDERER_TYPE );
	}

	@Override
	public String getFamily()
	{
		return UIOutputAccordionPanelComponent.COMPONENT_FAMILY;
	}

	@Override
	public void restoreState(FacesContext context, Object state)
	{
		this.values = (Object[]) state;

		super.restoreState(context, this.values[0]);

		this.heading = (String) this.values[1];
	}

	@Override
	public Object saveState(FacesContext context)
	{
		if (this.values == null)
		{
			this.values = new Object[2];
		}

		this.values[0] = super.saveState(context);

		this.values[1] = this.heading;

		return this.values;
	}

	private String heading = null;
	private boolean headingSet = false;
	public String getHeading()
	{ 
		if (null != this.heading)
		{
			return this.heading;
		}

		ValueExpression ve = this.getValueExpression("heading");
		if (ve != null)
		{
			return (String) ve.getValue(this.getFacesContext().getELContext());
		}

		return null;
	}
	public void setHeading(String value)
	{
		this.heading = value;
	}
}
