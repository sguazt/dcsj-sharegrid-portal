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

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIInputFileComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

/**
 * Tag for uploading files.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class InputFileTag extends AbstractInputTag
{ 
	private static final String COMPONENT_TYPE = FacesConstants.InputFileComponentType;
	private static final String RENDERER_TYPE = FacesConstants.HtmlInputFileRendererType;

	@Override
	public String getComponentType()
	{
		return COMPONENT_TYPE;
	}  

	@Override
	public String getRendererType()
	{
		return RENDERER_TYPE;
	} 

	@Override
	public void release()
	{
		super.release();

		this.accept = null;
	}

	@Override
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

		UIInputFileComponent inputFile = null;

		try
		{
			inputFile = (UIInputFileComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Unexpected component " + component.toString() + ". Expected: '" + UIInputFileComponent.class.getName() + "'. Perhaps you're missing a tag?");
		}

		if (this.accept != null)
		{
			inputFile.setValueExpression("accept", this.accept);
		}
	}

	/** PROPERTY: accept.*/
	private ValueExpression accept;
	public ValueExpression getAccept()
	{
		return this.accept;
	}
	public void setAccept(ValueExpression value)
	{
		this.accept = value;
	}
}
