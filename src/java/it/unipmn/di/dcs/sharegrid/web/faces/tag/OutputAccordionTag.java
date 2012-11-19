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

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIOutputAccordionComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

/**
 * Accordion output tag handler.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class OutputAccordionTag extends AbstractOutputTag
{
	@Override
	public String getComponentType()
	{
		return FacesConstants.OutputAccordionComponentType;
	}

	@Override
	public String getRendererType()
	{
		return FacesConstants.HtmlOutputAccordionRendererType;
	}

	@Override
	public void release()
	{
		super.release();
//		this.name = null;
		this.panelHeight = null;
//		this.styleClass = null;
		this.panelClass = null;
		this.headerClass = null;
		this.contentClass = null;
	}

    @Override
    protected void setProperties(UIComponent component)
    {
		super.setProperties(component);

		UIOutputAccordionComponent outputAccordion = null;

		try
		{
			outputAccordion = (UIOutputAccordionComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Unexpected component " + component.toString() + ". Expected: '" + UIOutputAccordionComponent.class.getName() + "'. Perhaps you're missing a tag?");
		}

//		if (this.name != null)
//		{
//			outputAccordion.setValueExpression("name", this.name);
//		}
		if (this.panelHeight != null)
		{
			outputAccordion.setValueExpression("panelHeight", this.panelHeight);
		}
//		if (this.styleClass != null)
//		{
//			outputAccordion.setValueExpression("styleClass", this.styleClass);
//		}
		if (this.panelClass != null)
		{
			outputAccordion.setValueExpression("panelClass", this.panelClass);
		}
		if (this.headerClass != null)
		{
			outputAccordion.setValueExpression("headerClass", this.headerClass);
		}
		if (this.contentClass != null)
		{
			outputAccordion.setValueExpression("contentClass", this.contentClass);
		}
	}
	
//	/** PROPERTY: name. */
//	private ValueExpression name;
//	public ValueExpression getName()
//	{
//		return this.name;
//	}
//	public void setName(ValueExpression value)
//	{
//		this.name = value;
//	}

	/** PROPERTY: panelHeight. */
	private ValueExpression panelHeight;
	public ValueExpression getPanelHeight() {
		return this.panelHeight;
	}
	public void setPanelHeight(ValueExpression value)
	{
		this.panelHeight = value;
	}

//	/** PROPERTY: styleClass. */
//	private ValueExpression styleClass;
//	public ValueExpression getStyleClass()
//	{
//		return this.styleClass;
//	}
//	public void setStyleClass(ValueExpression value)
//	{
//		this.styleClass = value;
//	}

	/** PROPERTY: panelClass. */
	private ValueExpression panelClass;
	public ValueExpression getPanelClass()
	{
		return this.panelClass;
	}
	public void setPanelClass(ValueExpression value)
	{
		this.panelClass = value;
	}

	/** PROPERTY: headerClass. */
	private ValueExpression headerClass;
	public ValueExpression getHeaderClass()
	{
		return this.headerClass;
	}
	public void setHeaderClass(ValueExpression value)
	{
		this.headerClass = value;
	}

	/** PROPERTY: contentClass. */
	private ValueExpression contentClass;
	public ValueExpression getContentClass()
	{
		return this.contentClass;
	}
	public void setContentClass(ValueExpression value)
	{
		this.contentClass = value;
	}
}
