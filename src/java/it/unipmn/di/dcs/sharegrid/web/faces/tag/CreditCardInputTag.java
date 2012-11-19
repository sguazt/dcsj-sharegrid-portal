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

import it.unipmn.di.dcs.sharegrid.web.faces.component.UICreditCardInputComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

/**
 * Tag for input credit card number.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class CreditCardInputTag extends AbstractInputTag
{
	/** A constructor. */
	public CreditCardInputTag()
	{
		super();
	}

	@Override
	public String getComponentType()
	{
		return FacesConstants.CreditCardComponentType;
	}

	@Override
	public String getRendererType()
	{
		return FacesConstants.HtmlCreditCardRendererType;
	}

	@Override
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

		UICreditCardInputComponent cc = null;

		try
		{
			cc = (UICreditCardInputComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Unexpected component " + component.toString() + ". Expected: '" + UICreditCardInputComponent.class.getName() + "'. Perhaps you're missing a tag?");
		}
	} 
} 
