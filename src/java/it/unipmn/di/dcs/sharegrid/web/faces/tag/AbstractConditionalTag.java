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

import it.unipmn.di.dcs.sharegrid.web.faces.component.AbstractUIConditionalComponent;

import javax.el.ValueExpression;

import javax.faces.component.UIComponent;
//import javax.faces.webapp.UIComponentELTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * <p>Abstract class that facilitates implementation of conditional actions 
 * where the boolean result is exposed as a JSP scoped variable. The 
 * boolean result may then be used as the test condition in a &lt;c:when&gt;
 * action.</p>
 *
 * <p>This base class provides support for:</p>
 * 
 * <ul>
 *  <li> Conditional processing of the action's body based on the returned value
 *       of the abstract method <tt>condition()</tt>.</li>
 *  <li> Storing the result of <tt>condition()</tt> as a <tt>Boolean</tt> object
 *       into a JSP scoped variable identified by attributes <tt>var</tt> and
 *       <tt>scope</tt>.
 * </ul>
 * 
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractConditionalTag extends AbstractTag
{
	/**
	 * Name of the exported scoped variable storing the result of the
	 * condition.
	 */
	private String var; // The bound variable

	private ValueExpression test; // The conditional expression

	private Boolean evalTest; // the saved result of condition()

	/** Scope of the 'var' attribute. */
	private int scope; // The scope of the bound variable

	/**
	* Base constructor to initialize local state.  As with <tt>UIComponentELTag</tt>,
	* subclasses should not implement constructors with arguments, and
	* no-argument constructors implemented by subclasses must call the 
	* superclass constructor.
	*/
	protected AbstractConditionalTag()
	{
		super();

		this.init();
	}

        @Override
        protected void setProperties(UIComponent component)
        {
		super.setProperties(component);

		AbstractUIConditionalComponent condComp = null;
		try
		{
			condComp = (AbstractUIConditionalComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Component " + component.toString() + " not expected type.  Expected: " + AbstractUIConditionalComponent.class.getName() + ". Perhaps you're missing a tag?", cce);
		}

		condComp.setVar( this.var );
		if (this.test != null)
		{
			if (!this.test.isLiteralText())
			{
				condComp.setValueExpression("test", this.test);
			}
			else
			{
				//condition.setTest( this.test.getExpressionString() );
				condComp.setTest( (Boolean) this.test.getValue( this.getELContext() ) );
			}
		}
		condComp.setScope( this.scope );
	}

	@Override
	public void release()
	{
		super.release();

		this.init();
	}

	public String getVar()
	{
		return this.var;
	}

	public void setVar(String value)
	{
		this.var = value;
	}

	public ValueExpression getTest()
	{
		return this.test;
	}

	public void setTest(ValueExpression value)
	{
		this.test = value;
		this.evalTest = null;
	}

	public int getScope()
	{
		return this.scope;
	}

	public void setScope(int value)
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

//	protected boolean getEvalTest()
//	{
//		return this.evalTest;
//	}
//
//	protected void setEvalTest(boolean value)
//	{
//		this.evalTest = value;
//	}

	/**
	 * @return a boolean representing the condition that a particular subclass
	 *   uses to drive its conditional logic.
	 */
	public boolean evalTest() throws JspException
	{
		if ( this.evalTest != null )
		{
			return this.evalTest;
		}

		try
		{
			Object res = null;
			res = this.getTest().getValue( this.getELContext() );
			if (res == null)
			{
				throw new JspException("Error while evaluting the 'if' condition");
			}
			else
			{
				this.evalTest = (Boolean) res;
				return this.evalTest;
			}
		}
		catch (JspException ex)
		{
			throw new JspException("Error while evaluating the tag boolean condition.", ex);
		}
	}

//	/**
//	 * Includes its body if <tt>condition()</tt> evaluates to true.
//	 */
//	@Override
//	public int getDoStartValue() throws JspException
//	{
//		// execute our condition() method once per invocation
//		this.evalTest = this.evalTest();
//
//		// expose variables if appropriate
//		this.exposeVariables();
//
//		// handle conditional behavior
//		if (this.evalTest)
//		{
//			return EVAL_BODY_INCLUDE;
//		}
//		else
//		{
//			return SKIP_BODY;
//		}
//	}

	// expose attributes if we have a non-null 'var'
	protected void exposeVariables() throws JspException
	{
		if (this.var != null)
		{
			this.pageContext.setAttribute(var, new Boolean(this.evalTest()), scope);
		}
	}

	// initializes internal state
	private void init()
	{
		this.evalTest = null;
		this.var = null;
		this.test = null;
		this.scope = PageContext.PAGE_SCOPE;
	}
}
