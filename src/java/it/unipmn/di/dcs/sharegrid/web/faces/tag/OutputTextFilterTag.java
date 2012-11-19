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

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIOutputTextFilterComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

/**
 * Tag that replaces special HTML characters (like less than and greater than
 * signs) with their HTML character entities.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class OutputTextFilterTag extends AbstractOutputTag
{
    private static final String COMPONENT_TYPE = FacesConstants.OutputTextFilterComponentType;
    private static final String RENDERER_TYPE = FacesConstants.HtmlOutputTextFilterRendererType;

	@Override
	public String getComponentType()
	{
		return COMPONENT_TYPE;
	}

	@Override
	public String getRendererType()
	{
		return RENDERER_TYPE;
	}

	@Override
	public void release()
	{
		super.release();

		this.layout = null;
		this.rules = null;
	}

	@Override
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

		UIOutputTextFilterComponent filterComp = null;

		try
		{
			filterComp = (UIOutputTextFilterComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Unexpected component " + component.toString() + ". Expected: '" + UIOutputTextFilterComponent.class.getName() + "'. Perhaps you're missing a tag?");
		}

		if (this.layout != null)
		{
			filterComp.setValueExpression("layout", this.layout);
		}
		if (this.rules != null)
		{
			filterComp.setValueExpression("rules", this.rules);
		}
	}

	/** PROPERTY: layout. */
	private ValueExpression layout;
	public ValueExpression getLayout()
	{
		return this.layout;
	}
	public void setLayout(ValueExpression value)
	{
		this.layout = value;
	}

	/** PROPERTY: rules. */
	private ValueExpression rules;
	public ValueExpression getRules()
	{
		return this.rules;
	}
	public void setRules(ValueExpression value)
	{
		this.rules = value;
	}
}
