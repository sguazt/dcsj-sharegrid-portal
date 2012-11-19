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

package it.unipmn.di.dcs.sharegrid.web.faces.renderer;

//import it.unipmn.di.dcs.sharegrid.web.faces.component.AbstractUIConditionalComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.component.UIIfComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesUtil;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

/**
 * Renderer for the 'if' tag.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class IfRenderer extends AbstractRenderer
{
	/** A constructor. */
	public IfRenderer()
	{
		super();
	}

/*
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
		super.encodeBegin(context, component);
	}
*/

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException
	{
		FacesUtil.AssertValidParams( context, component, UIIfComponent.class );

		if ( !component.isRendered() )
		{
			return;
		}

		UIIfComponent condComp = (UIIfComponent) component;

		// Render children only if the specified condition holds.

		if ( condComp.getTest() )
		{
			super.encodeChildren(context, component);
		}
	}

/*
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
		super.encodeEnd(context, component);
	}
*/

	@Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException
	{
		return null;
	}

	@Override
	public boolean getRendersChildren()
	{
		return true;
	}
}
