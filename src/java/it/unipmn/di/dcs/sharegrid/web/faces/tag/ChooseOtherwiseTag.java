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
 * Tag for specifying the 'otherwise' part of 'choose-when-otherwise' conditions
 * at runtime.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ChooseOtherwiseTag extends UIComponentELTag
{
	/** A constructor. */
	public ChooseOtherwiseTag()
	{
		super();
	}

	@Override
	public String getComponentType()
	{
		return FacesConstants.ChooseOtherwiseComponentType;
	}

	@Override
	public String getRendererType()
	{
		return FacesConstants.ChooseOtherwiseRendererType;
	}

        @Override
        public int getDoStartValue() throws JspException
        {
		return EVAL_BODY_INCLUDE;
	}
}
