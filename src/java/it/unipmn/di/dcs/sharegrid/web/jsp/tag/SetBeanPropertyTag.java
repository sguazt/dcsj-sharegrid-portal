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

package it.unipmn.di.dcs.sharegrid.web.jsp.tag;
 
import it.unipmn.di.dcs.common.annotation.Experimental;
import it.unipmn.di.dcs.common.annotation.FIXME;

import java.io.IOException;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
 
/**
 * Tag for setting bean properties in a JSP page.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@Experimental("Don't use it")
public class SetBeanPropertyTag extends SimpleTagSupport
{
	private String bean;
	private String name;
	private Object value;

//	public PageContext getPageContext()
//	{
//		return this.pageContext;
//	}
//	public void setPageContext(PageContext value)
//	{
//		this.pageContext = value;
//	}
//	@Override
//	public int doEndTag() throws JspException
//	{
//		return EVAL_PAGE;
//	}
	@Override
	@FIXME("This method would be OK. The problem is that Apache TOMCAT 6 doesn't implement ExpressionFactory#newInstance(). See https://issues.apache.org/bugzilla/show_bug.cgi?id=45345")
	public void doTag() throws JspException, SkipPageException, IOException
	{
//FIXME: this code is OK. The problem is that Apache TOMCAT doesn't implement
// method ExpressionFactory#newInstance()!!!!
//		ELContext elContext = this.getJspContext().getELContext();
//
//		ExpressionFactory expressionFactory = null;
//		ValueExpression valueExpression = null;
//
//		expressionFactory = ExpressionFactory.newInstance();
//		valueExpression = expressionFactory.createValueExpression(
//				elContext,
//				"${" + bean + "." + name + "}",
//				Object.class
//		);
//		valueExpression.setValue(elContext, value);
	}

	public String getName()
	{
		return this.name;
	}
	public void setName(String value)
	{
		this.name = value;
	}

	public Object getValue()
	{
		return this.value;
	}
	public void setValue(Object value)
	{
		this.value = value;
	}

	public String getBean()
	{
		return this.bean;
	}
	public void setBean(String value)
	{
		this.bean = value;
	}
}
