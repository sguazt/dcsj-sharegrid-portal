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

import it.unipmn.di.dcs.sharegrid.web.faces.component.UISelectItemsComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentELTag;

/**
 * Tag for specifying multiple selected items in a select tag.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class SelectItemsTag extends UIComponentELTag
{
	private ValueExpression value;
	private String var;
	private ValueExpression itemLabel;
	private ValueExpression itemValue;

	@Override
	public String getRendererType()
	{
		return null;
	}	

	@Override
	public String getComponentType()
	{
		return FacesConstants.SelectItemsComponentType;
	}

	@Override
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		
		UISelectItemsComponent select = null;
		try
		{
			select = (UISelectItemsComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Component " + component.toString() + " not expected type.  Expected: " + UISelectItemsComponent.class.getName() + ". Perhaps you're missing a tag?", cce);
		}

		select.setVar( this.var );
//		if (this.var != null)
//		{
//			if (!this.var.isLiteralText())
//			{
//				select.setValueExpression("var", this.var);
//			}
//			else
//			{
//				select.setVar( this.var.getExpressionString() );
//			}
//		}
		if (this.itemLabel != null)
		{
			if (!this.itemLabel.isLiteralText())
			{
				select.setValueExpression("itemLabel", this.itemLabel);
			}
			else
			{
				select.setItemLabel( this.itemLabel.getExpressionString() );
			}
		}
		if (this.itemValue != null)
		{
			if (!this.itemValue.isLiteralText())
			{
				select.setValueExpression("itemValue", this.itemValue);
			}
			else
			{
				select.setItemValue( this.itemValue.getExpressionString() );
			}
		}
		if (this.value != null)
		{
			if (!this.value.isLiteralText())
			{
				select.setValueExpression("value", this.value);
			}
			else
			{
				select.setValue( this.value.getExpressionString() );
			}
		}
	}

	@Override
	public void release()
	{
		super.release();

		this.value = null;
		this.var = null;
		this.itemLabel = null;
		this.itemValue = null;
	}

	public void setValue(ValueExpression value)
	{
		this.value = value;
	}

	public ValueExpression getValue()
	{
		return this.value;
	}

	public String getVar()
	{
		return this.var;
	}

	public void setVar(String value)
	{
		this.var = value;
	}

	public ValueExpression getItemLabel()
	{
		return this.itemLabel;
	}

	public void setItemLabel(ValueExpression value)
	{
		this.itemLabel = value;
	}

	public ValueExpression getItemValue()
	{
		return this.itemValue;
	}

	public void setItemValue(ValueExpression value)
	{
		this.itemValue = value;
	}
}
