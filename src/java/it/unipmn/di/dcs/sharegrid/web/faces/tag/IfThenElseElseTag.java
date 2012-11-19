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

import javax.faces.webapp.UIComponentELTag;

import javax.servlet.jsp.JspException;

/**
 * Tag for specifying the 'else' part of 'if-then-else' conditions at runtime.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class IfThenElseElseTag extends UIComponentELTag
{
	/** A constructor. */
	public IfThenElseElseTag()
	{
		super();
	}

	@Override
	public String getComponentType()
	{
		return FacesConstants.IfThenElseElseComponentType;
	}

	@Override
	public String getRendererType()
	{
		return FacesConstants.IfThenElseElseRendererType;
	}

        /**
 	 * Includes its body if <tt>condition()</tt> evaluates to true.
	 */
        @Override
        public int getDoStartValue() throws JspException
        {
		boolean test;

		// Logic moved inside the component
		return EVAL_BODY_INCLUDE;
//		// evaluate the test once per invocation
//		test = ((IfThenElseTag) this.getParent()).evalTest();
//
//		// handle conditional behavior
//		if ( !test )
//		{
//			return EVAL_BODY_INCLUDE;
//		}
//		else
//		{
//			return SKIP_BODY;
//		}
	}
}
