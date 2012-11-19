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

import javax.faces.context.FacesContext;
import javax.servlet.jsp.PageContext;

/**
 * Base class for condition components.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractUIConditionalComponent extends AbstractUIComponent
{
	private Object[] values;
 
	/** A constructor. */
	protected AbstractUIConditionalComponent()
	{
		super();
	}

	private String var;
	public void setVar(String value)
	{
		this.var = value;
	}

	public String getVar()
	{
		if ( this.var != null )
		{
			return this.var;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"test",
			null
		);
	}

	private Boolean test;
	public boolean getTest()
	{
		if (null != this.test)
		{
			return this.test;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"test",
			false
		);
	}
	public void setTest(boolean value)
	{
		this.test = value;
	}

	private Integer scope;
	public int getScope()
	{
		if (null != this.scope)
		{
			return this.scope;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"scope",
			PageContext.PAGE_SCOPE
		);
	}
	public void setScope(int value)
	{
		this.scope = value;
	}

	@Override
	public Object saveState(FacesContext context)
	{
		if (this.values == null)
		{
			this.values = new Object[4];
		}

		this.values[0] = super.saveState(context);

		this.values[1] = this.var;
		this.values[2] = this.test;
		this.values[3] = this.scope;

		return this.values;
	}

	@Override
	public void restoreState(FacesContext context, Object state)
	{
		this.values = (Object[]) state;

		super.restoreState(context, this.values[0]);

		this.var = (String) this.values[1];
		this.test = (Boolean) this.values[2];
		this.scope = (Integer) this.values[3];
	}
}
