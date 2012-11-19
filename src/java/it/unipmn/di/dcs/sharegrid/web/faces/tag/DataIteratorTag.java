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

import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.webapp.UIComponentELTag;

import javax.servlet.jsp.PageContext;

/**
 * The tag handler class for a {@code UIData} component associated with a
 * {@code IteratorRenderer}.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class DataIteratorTag extends UIComponentELTag
{
	private ValueExpression first;
	private ValueExpression rows;
	private ValueExpression styleClass;
	private ValueExpression value;
	private ValueExpression var;
//	private ValueExpression scope;

	public DataIteratorTag()
	{
		super();

System.err.println("DataIteratorTag::Constructor>>");//XXX
		this.init();
	}

	public void setFirst(ValueExpression value)
	{
		this.first = value;
	}

	public ValueExpression getFirst()
	{
		return this.first;
	}

	public void setRows(ValueExpression value)
	{
		this.rows = value;
	}

	public ValueExpression getRows()
	{
		return this.rows;
	}

/*
	public void setScope(ValueExpression value)
	{
		this.scope = value;
	}

	public void setScope(String value)
	{
		if ( "page".equalsIgnoreCase( value ) )
		{
			this.scope = PageContext.PAGE_SCOPE;
		}
		else if ( "request".equalsIgnoreCase( value ) )
		{
			this.scope = PageContext.REQUEST_SCOPE;
		}
		else if ( "session".equalsIgnoreCase( value ) )
		{
			this.scope = PageContext.SESSION_SCOPE;
		}
		else if ( "application".equalsIgnoreCase( value ))
		{
			this.scope = PageContext.APPLICATION_SCOPE;
		}
		else
		{
			this.scope = PageContext.PAGE_SCOPE;
		}
	}

	public ValueExpression getScope()
	{
		return this.scope;
	}
*/

	public void setStyleClass(ValueExpression value)
	{
		this.styleClass = value;
	}

	public ValueExpression getStyleClass()
	{
		return this.styleClass;
	}

	public void setValue(ValueExpression value)
	{
		this.value = value;
	}

	public ValueExpression getValue()
	{
		return this.value;
	}

	public void setVar(ValueExpression value)
	{
		this.var = value;
	}

	public ValueExpression getVar()
	{
		return this.var;
	}

	@Override
	public String getComponentType()
	{
		return FacesConstants.DataIteratorComponentType;
	}

	@Override
	public String getRendererType()
	{
		return FacesConstants.DataIteratorRendererType;
	}

	@Override
	public void release()
	{
		super.release();

		this.init();
	}

	@Override
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

                UIData iterator = null;
                try
                {
                        iterator = (UIData) component;
                }
                catch (ClassCastException cce)
                {
                        throw new IllegalStateException("Component " + component.toString() + " not expected type. Expected: " + UIData.class.getName() + ". Perhaps you're missing a tag?", cce);
                }

		if (this.first != null)
		{
			if (!this.first.isLiteralText())
			{
				iterator.setValueExpression("first", this.first);
			}
			else
			{
				iterator.setFirst(new Integer(this.first.getExpressionString()));
			}
		}
		if (this.rows != null)
		{
			if (!this.rows.isLiteralText())
			{
				iterator.setValueExpression("rows", this.rows);
			}
			else
			{
				iterator.setRows(new Integer(this.rows.getExpressionString()));
			}
		}
		if (this.styleClass != null)
		{
			if (!this.styleClass.isLiteralText())
			{
				iterator.setValueExpression("styleClass", this.styleClass);
			}
			else
			{
				iterator.getAttributes().put( "styleClass", this.getStyleClass().getExpressionString() );
			}
		}
		if (this.value != null)
		{
			if (!this.value.isLiteralText())
			{
				iterator.setValueExpression("value", this.value);
			}
			else
			{
				iterator.setValue(this.value.getExpressionString());
			}
		}
		if (this.var != null)
		{
			if (!this.var.isLiteralText())
			{
				iterator.setValueExpression("var", this.var);
			}
			else
			{
				iterator.setVar(this.var.getExpressionString());
			}
		}
	}

	protected void init()
	{
		this.first = null;
		this.rows = null;
		this.styleClass = null;
//		this.scope = PageContext.PAGE_SCOPE;
		this.value = null;
		this.var = null;
	}
}
