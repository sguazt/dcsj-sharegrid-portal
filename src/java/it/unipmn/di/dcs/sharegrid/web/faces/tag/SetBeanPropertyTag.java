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

import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import javax.faces.component.UIComponent;

import javax.servlet.jsp.JspException;

/**
 * Tag for setting bean properties in a JSF page.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@Experimental("Don't use it")
public class SetBeanPropertyTag extends AbstractTag
{
	protected ExpressionFactory getExpressionFactory()
	{
		return this.getFacesContext().getApplication().getExpressionFactory();
	}

	private ValueExpression bean;
	public void setBean(ValueExpression value) { this.bean = value; }

	private ValueExpression name;
	public void setName(ValueExpression value) { this.name = value; }

	private ValueExpression value;
	public void setValue(ValueExpression value) { this.value = value; }

	@Override
	public String getComponentType()
	{
		return FacesConstants.SetBeanProperty_ComponentType;
	}

	@Override
	public String getRendererType()
	{
		return null;
	}

	@Override
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

		this.setStringProperty(component, "bean", this.bean);
		this.setStringProperty(component, "name", this.name);
		this.setStringProperty(component, "value", this.value);
	}

	@Override
	public void release()
	{
		super.release();

		this.bean = null;
		this.name = null;
		this.value = null;
	}

	@Override
	public int doStartTag() throws JspException
	{
		ELContext elContext = this.getELContext();

		ExpressionFactory expressionFactory = null;
		ValueExpression valueExpression = null;

		expressionFactory = this.getExpressionFactory();
		valueExpression = expressionFactory.createValueExpression(
				elContext,
				"#{" + bean + "." + name + "}",
				Object.class
		);
		valueExpression.setValue(elContext, value);

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException
	{
		return EVAL_PAGE;
	}
}
