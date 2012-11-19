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

import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

/**
 * Tag that replaces special HTML characters (like less than and greater than
 * signs) with their HTML character entities.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UIOutputTextFilterComponent extends AbstractUIOutputComponent
{
	public static final String COMPONENT_FAMILY = FacesConstants.OutputTextFilterComponentFamily;
	public static final String COMPONENT_TYPE = FacesConstants.OutputTextFilterComponentType;
	public static final String RENDERER_TYPE = FacesConstants.HtmlOutputTextFilterRendererType;

	private Object[] values;

	/** A constructor. */
	public UIOutputTextFilterComponent()
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

		super.restoreState(context, this.values[0]);

		this.layout = (String) this.values[1];
		this.rules = (String) this.values[2];
	}

	@Override
	public Object saveState(FacesContext context)
	{
		if (this.values == null)
		{
			this.values = new Object[3];
		}

		this.values[0] = super.saveState(context);

		this.values[1] = this.layout;
		this.values[2] = this.rules;

		return this.values;
	}

	private String layout;
	public String getLayout()
	{
		if (null != this.layout)
		{
			return this.layout;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"layout",
			"none"
		);
	}
	public void setLayout(String value)
	{
		this.layout = value;
	}

	private String rules;
	public String getRules()
	{
		if (null != this.rules)
		{
			return this.rules;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"rules",
			null
		);
	}
	public void setRules(String value)
	{
		this.rules = value;
	}
}
