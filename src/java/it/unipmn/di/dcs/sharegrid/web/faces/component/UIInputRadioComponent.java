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

import it.unipmn.di.dcs.common.annotation.Experimental;
import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

/**
 * <p>A component that represents a radio button.</p>
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@Experimental("Need more testing")
public class UIInputRadioComponent extends AbstractUIInputComponent
{
	public static final String COMPONENT_FAMILY = FacesConstants.InputRadioComponentFamily;
	public static final String COMPONENT_TYPE = FacesConstants.InputRadioComponentType;
	public static final String RENDERER_TYPE = FacesConstants.HtmlInputRadioRendererType;

	private Object[] values;

	/** A constructor. */
	public UIInputRadioComponent()
	{
		super();

		//this.setMultiple(false);
		this.setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily()
	{
		return COMPONENT_FAMILY;
	}

	@Override
	public void restoreState(FacesContext context,Object state)
	{
		this.values = (Object[]) state;

		super.restoreState(context, this.values[0]);

		this.groupName = (String) this.values[1];
		this.checked = (Boolean) this.values[2];
	}

	@Override
	public Object saveState(FacesContext context)
	{
		if (this.values == null)
		{
			this.values = new Object[3];
		}

		this.values[0] = super.saveState(context);

		this.values[1] = this.groupName;
		this.values[2] = this.checked;

		return this.values;
	}

	/**
	 * Return the value of the <code>selectedValue</code> property
	 * of the selected radio button in the group of radio buttons
	 * identified by the <code>name</code> parameter.
	 * A <code>RadioButton</code> is one of a group of radio buttons
	 * if more than on radio button has the same value for the
	 * <code>name</code> property.<br/>
	 * When one of the radio buttons among that group is selected,
	 * the value of its <code>selectedValue</code> property
	 * is maintained in a request attribute identified by the value
	 * of its <code>name</code> property.
	 *
	 * @param groupName the value a RadioButton name property.
	 */
	public static Object GetGroupSelected(String groupName)
	{
		Map reqMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

		if (groupName != null)
		{
			return reqMap.get(groupName);
		}
		else
		{
			return null;
		}
	}

//	@Override
//	public void validate(FacesContext context)
//	{
//		super.validate(context);
//
//		if ( !(this.isValid() && this.isChecked()) )
//		{
//			return;
//		}
//
//		String groupName = this.getGroupName();
//		if ( Strings.IsNullOrEmpty(groupName) )
//		{
//			return;
//		}
//
//		// Update the request parameter that holds the value of the
//		// selectedValue property of the selected radio button.
//		this.addToRequestMap(context, groupName);
//	}
//
//	protected void addToRequestMap(FacesContext context, String groupName)
//	{
//		Map requestMap = context.getExternalContext().getRequestMap();
//		requestMap.put(groupName, getValue());
//	}

	private Boolean checked;
	public boolean isChecked()
	{
		if (null != this.checked)
		{
			return this.checked;
		}

		ValueExpression ve = this.getValueExpression("checked");
		if ( ve != null )
		{
			return (Boolean) ve.getValue(this.getFacesContext().getELContext());
		}

		return false;
	}
	public void setIsChecked(boolean value)
	{
		this.checked = value;
	}

	private String groupName;
	public String getGroupName()
	{
		if (null != this.groupName)
		{
			return this.groupName;
		}
		ValueExpression ve = getValueExpression("groupName");
		if (ve != null)
		{
			return (String) ve.getValue(this.getFacesContext().getELContext());
		}

		return null;
	}
	public void setGroupName(String value)
	{
		this.groupName = value;
	}
}
