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

import it.unipmn.di.dcs.common.annotation.Experimental;

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIInputRadioComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

/**
 * Tag for uploading files.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@Experimental("Need more testing")
public class InputRadioTag extends AbstractInputTag
{ 
	/** A constructor. */
	public InputRadioTag()
	{
		super();
	}

	// PROPERTY: groupName
	private ValueExpression groupName;
	public ValueExpression getGroupName()
	{
		return this.groupName;
	}
	public void setGroupName(ValueExpression value)
	{
		this.groupName = value;
	}

	// PROPERTY: checked
	private ValueExpression checked;
	public ValueExpression getChecked()
	{
		return this.checked;
	}
	public void setChecked(ValueExpression value)
	{
		this.checked = value;
	}

	@Override
	public String getComponentType()
	{
		return FacesConstants.InputRadioComponentType;
	}  

	@Override
	public String getRendererType()
	{
		return FacesConstants.HtmlInputRadioRendererType;
	} 

	@Override
	public void release()
	{
		super.release();
		this.checked = null;
		this.groupName = null;
	}

	@Override
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

		UIInputRadioComponent inputRadio = null;

		try
		{
			inputRadio = (UIInputRadioComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Unexpected component " + component.toString() + ". Expected: '" + UIInputRadioComponent.class.getName() + "'. Perhaps you're missing a tag?");
		}

		if (this.checked != null)
		{
			if (!this.checked.isLiteralText())
			{
				inputRadio.setValueExpression("checked", this.checked);
			}
			else
			{
				inputRadio.setIsChecked( Boolean.valueOf( this.checked.getExpressionString() ) );
			}
		}
		if (this.groupName != null)
		{
			if (!this.groupName.isLiteralText())
			{
				inputRadio.setValueExpression("groupName", this.groupName);
			}
			else
			{
				inputRadio.setGroupName( this.groupName.getExpressionString() );
			}
		}
	}
}
