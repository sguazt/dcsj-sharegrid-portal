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

import it.unipmn.di.dcs.sharegrid.web.faces.el.IndexedValueExpression;

import java.io.Serializable;

import java.lang.reflect.Array;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import javax.faces.context.FacesContext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Simple <em>for-each</em> tag supporting EL expressions.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ForEachTag extends TagSupport
{
	private int currentBegin;
	private int currentIndex;
	private int currentEnd;
	private int currentStep;
	private int currentCount;
	private boolean isFirst;
	private boolean isLast;

	private ValueExpression items;
	private Object itemsValue;

	private ValueExpression beginVE;
	private ValueExpression endVE;
	private ValueExpression stepVE;

	private Integer begin;
	private Integer end;
	private Integer step;

	private String var;
	private String varStatus;

	// Saved values on the VariableMapper
	private ValueExpression previousDeferredVar;
	private ValueExpression previousDeferredVarStatus;

	// Map for properties referred off from 'varStatus' and their replacements
	private Map<String, Object> propertyReplacementMap;

	public void setItems(ValueExpression items)
	{
		if ( items.isLiteralText() )
		{
			throw new IllegalArgumentException("\"items\" must be a simple JSF expression");
		}
		this.items = items;
	}

	public void setBegin(ValueExpression begin)
	{
		this.beginVE = begin;
	}

	public void setEnd(ValueExpression end)
	{
		this.endVE = end;
	}

	public void setStep(ValueExpression step)
	{
		this.stepVE = step;
	}

	public void setVar(String var)
	{
		this.var = var;
	}

	public void setVarStatus(String varStatus)
	{
		this.varStatus = varStatus;
	}

	@Override
	public int doStartTag() throws JspException
	{
		this.validateAttributes();

		FacesContext context = FacesContext.getCurrentInstance();
		this.currentBegin = (this.begin == null) ? 0 : this.begin.intValue();
		this.isFirst = true;
		int length;

		if (this.items != null)
		{
			// AdamWiner: for reasons I cannot yet explain, using the JSP's
			// ELContext is giving me big problems trying to grab Lists
			// from inside of managed beans.  Switching this one call
			// to the JSF ELContext seems to resolve that.  We certainly
			// have to use the JSPs ELResolver for calling through
			// to the VariableMapper
			Object items = this.items.getValue(context.getELContext());//pageContext.getELContext());

			//pu: If items is specified and resolves to null, it is treated as an
			//  empty collection, i.e., no iteration is performed.
			if (items == null)
			{
				return SKIP_BODY;
			}

			this.itemsValue = items;
			// =-=AEW <c:forEach> supports arbitrary collections;  but
			// JSF only supports List in its EL.
			if (items instanceof List)
			{
				length = ((List) items).size();
			}
			else if (items.getClass().isArray())
			{
				length = Array.getLength(items);
			}
			else
			{
				throw new JspException( "MUST_POINT_TO_LIST_OR_ARRAY");
			}
			if (length == 0)
			{
				return SKIP_BODY;
			}
			//pu: If valid 'items' was specified, and so was 'begin', get out if size
			//  of collection were to be less than the begin. A mimic of c:forEach.
			if (length < this.currentBegin)
			{
				return SKIP_BODY;
			}

			this.currentEnd = (this.end == null) ? length - 1 : this.end.intValue();
			//pu: If 'end' were specified, but is beyond the size of collection, limit
			//  the iteration to where the collection ends. A mimic of c:forEach and
			//  fix for bug 4029853.
			if (length < this.currentEnd)
			{
				this.currentEnd = length - 1;
			}
		}
		else
		{
			this.currentEnd = (this.end == null) ? 0 : this.end.intValue();
		}
		this.currentIndex = this.currentBegin;
		this.currentCount = 1;
		this.currentStep = (this.step == null) ? 1 : this.step.intValue();
		//pu: Now check the valid relation between 'begin','end' and validity of 'step'
		this.validateRangeAndStep();

		// If we can bail, do it now
		if (this.currentEnd < this.currentIndex)
		return SKIP_BODY;

		this.isLast = this.currentIndex == this.currentEnd;

		// Save off the previous deferred variables
		VariableMapper vm = 
		pageContext.getELContext().getVariableMapper();
		if (this.var != null)
		this.previousDeferredVar = vm.resolveVariable(this.var);

		if (null != this.varStatus)
		{
			this.previousDeferredVarStatus = vm.resolveVariable(this.varStatus);
			this.propertyReplacementMap = new HashMap<String, Object>(9, 1);
			this.propertyReplacementMap.put("begin", Integer.valueOf(this.currentBegin));
			this.propertyReplacementMap.put("end", Integer.valueOf(this.currentEnd));
			this.propertyReplacementMap.put("step", Integer.valueOf(this.currentStep));
			this.propertyReplacementMap.put("count", Integer.valueOf(this.currentCount));
			this.propertyReplacementMap.put("index", Integer.valueOf(this.currentIndex));
			//this.propertyReplacementMap.put("current", this.varReplacement);
			this.propertyReplacementMap.put("first", (this.isFirst) ? Boolean.TRUE : Boolean.FALSE);
			this.propertyReplacementMap.put("last", (this.isLast) ? Boolean.TRUE : Boolean.FALSE);
		}

		// Update the variables
		this.updateVars();

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doAfterBody()
	{
		this.currentIndex += this.currentStep;
		this.currentCount += 1;

		//pu: if there is no varStatus set, no point in keeping loop status
		//  variables updated.
		if (null != this.varStatus)
		{
			if (this.isFirst)
			{
				this.propertyReplacementMap.put("first", Boolean.FALSE);
				this.isFirst = false;
			}

			this.isLast = (this.currentIndex == this.currentEnd);
			if (this.isLast)
			{
				this.propertyReplacementMap.put("last", this.isLast);
			}
			this.propertyReplacementMap.put("count", Integer.valueOf(this.currentCount));
			this.propertyReplacementMap.put("index", Integer.valueOf(this.currentIndex));
			//this.propertyReplacementMap.put("current", this.varReplacement);
		}

		// If we're at the end, bail
		if (this.currentEnd < this.currentIndex)
		{
			// Restore EL state
			VariableMapper vm = 
			pageContext.getELContext().getVariableMapper();
			if (this.var != null)
			{
				vm.setVariable(this.var, this.previousDeferredVar);
			}
			if (this.varStatus != null)
			{
				vm.setVariable(this.varStatus, this.previousDeferredVarStatus);
			}

			return SKIP_BODY;
		}

		// Otherwise, update the variables and go again
		this.updateVars();

		return EVAL_BODY_AGAIN;
	}

	/**
	 * Release state.
	 */
	@Override
	public void release()
	{
		super.release();
		this.begin = null;
		this.end = null;
		this.step = null;
		this.items = null;
		this.itemsValue = null;
		this.var = null;
		this.varStatus = null;
		this.propertyReplacementMap = null;
		this.previousDeferredVar = null;
		this.previousDeferredVarStatus = null;
	}

	// Push new values into the VariableMapper and the pageContext
	private void updateVars()
	{
		VariableMapper vm = pageContext.getELContext().getVariableMapper();
		if (this.var != null)
		{
			// Catch programmer error where _var has been set but
			// this.items has not
			if (this.items != null)
			{
				ValueExpression iterated = new IndexedValueExpression(this.items, this.currentIndex);
				vm.setVariable(this.var, iterated);
			}

			// Ditto (though, technically, one check for
			// _items is sufficient, because if _items evaluated
			// to null, we'd skip the whole loop)
			Object items = this.itemsValue;
			if (items != null)
			{
				Object item;
				if (items instanceof List)
				{
					item = ((List) items).get(this.currentIndex);
				}
				else
				{
					item = Array.get(items, this.currentIndex);
				}

				pageContext.setAttribute(this.var, item);
			}
		}

		if (this.varStatus != null)
		{
			pageContext.setAttribute(this.varStatus, this.propertyReplacementMap);
			ValueExpression constant = new Constants(new HashMap<String,Object>(this.propertyReplacementMap));
			vm.setVariable(this.varStatus, constant);
		}
	}

	private Integer evaluateInteger(FacesContext context, ValueExpression ve)
	{
		if (ve == null)
		{
			return null;
		}

		Object val = ve.getValue(context.getELContext());
		if (val instanceof Integer)
		{
			return (Integer) val;
		}
		else if (val instanceof Number)
		{
			return Integer.valueOf(((Number) val).intValue());
		}

		return null;
	}

	private void validateAttributes() throws JspTagException
	{
		// Evaluate these three ValueExpressions into integers
		// For why we use FacesContext instead of PageContext, see
		// above (the evaluation of _items)
		FacesContext context = FacesContext.getCurrentInstance();
		this.end = this.evaluateInteger(context, this.endVE);
		this.begin = this.evaluateInteger(context, this.beginVE);
		this.step = this.evaluateInteger(context, this.stepVE);

		if (null == this.items)
		{
			if (null == this.begin || null == this.end)
			{
				throw new JspTagException("'begin' and 'end' should be specified if 'items' is not specified");
			}
		}
		//pu: This is our own check - c:forEach behavior un-defined & unpredictable.
		if ((this.var != null) && this.var.equals(this.varStatus))
		{
			throw new JspTagException("'var' and 'varStatus' should not have the same value");
		}
	}

	private void validateRangeAndStep() throws JspTagException
	{
		if (this.currentBegin < 0)
		{
			throw new JspTagException("'begin' < 0");
		}
		if (this.currentStep < 1)
		{
			throw new JspTagException("'step' < 1");
		}
	}

	// Basic ValueExpression that always returns a constant object
	static private class Constants extends ValueExpression implements Serializable
	{
		private Object o;

		public Constants(Object o)
		{
			this.o = o;
		}

		public Object getValue(ELContext context)
		{
			return this.o;
		}

		public void setValue(ELContext context, Object value)
		{
			throw new PropertyNotWritableException();
		}

		public boolean isReadOnly(ELContext context)
		{
			return true;
		}

		public Class getType(ELContext context)
		{
			return this.o.getClass();
		}

		public Class getExpectedType()
		{
			return this.o.getClass();
		}

		public String getExpressionString()
		{
			return null;
		}

		public boolean equals(Object obj)
		{
			return obj == this;
		}

		public int hashCode()
		{
			return this.o.hashCode();
		}

		public boolean isLiteralText()
		{
			return true;
		}
	}
}
