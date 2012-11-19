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

package it.unipmn.di.dcs.sharegrid.web.faces.tag;

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIOutputAccordionPanelComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class OutputAccordionPanelTag extends AbstractOutputTag
{
	@Override
	public String getComponentType()
	{
		return FacesConstants.OutputAccordionPanelComponentType;
	}

	@Override
	public String getRendererType()
	{
		return FacesConstants.HtmlOutputAccordionPanelRendererType;
	}

	@Override
	public void release()
	{
		super.release();
		this.heading = null;
	}

    @Override
    protected void setProperties(UIComponent component)
    {
		super.setProperties(component);

		UIOutputAccordionPanelComponent outputAccordionPanel = null;

		try
		{
			outputAccordionPanel = (UIOutputAccordionPanelComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Unexpected component " + component.toString() + ". Expected: '" + UIOutputAccordionPanelComponent.class.getName() + "'. Perhaps you're missing a tag?");
		}

		if (this.heading != null)
		{
			outputAccordionPanel.setValueExpression("heading", this.heading);
		}
	}
	
	/** PROPERTY: heading. */
	private ValueExpression heading;
	public ValueExpression getHeading()
	{
		return this.heading;
	}
	public void setHeading(ValueExpression value)
	{
		this.heading = value;
	}
}
