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

import java.io.IOException;

import javax.el.ValueExpression;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.JspException;

/**
 * A simple <em>if-then-else</em> JSP tag.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class IfThenElseTag extends SimpleTagSupport
{
	private ValueExpression test;

	/** Set the <em>if</em> condition. */
	public void setTest(ValueExpression value)
	{
		this.test = value;
	}

	/** Returns the <em>if</em> condition. */
	public ValueExpression getTest()
	{
		return this.test;
	}

	/** Returns the evaluated <em>if</em> condition. */
	public boolean getEvalTest() throws JspException
	{
		try
		{
			Object res = null;
			res = this.getTest().getValue( this.getJspContext().getELContext() );
			if (res == null)
			{
				throw new JspException("Error while evaluting the 'ifThenElse' condition");
			}
			else
			{
				return (Boolean) res;
			}
		}
		catch (JspException ex)
		{
			throw new JspException(ex.toString(), ex);
		}
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		this.getJspBody().invoke(null);
	}
}
