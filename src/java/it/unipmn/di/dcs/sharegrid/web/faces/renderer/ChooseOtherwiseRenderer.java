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

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIChooseComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.component.UIChooseOtherwiseComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.component.UIChooseWhenComponent;

import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesUtil;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

/**
 * Renderer for the 'otherwise' tag of the 'choose' tag.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ChooseOtherwiseRenderer extends AbstractRenderer
{
	/** A constructor. */
	public ChooseOtherwiseRenderer()
	{
		super();
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException
	{
		// Make sure this component is a 'otherwise' component
		FacesUtil.AssertValidParams( context, component, UIChooseOtherwiseComponent.class );

		// Make sure the parent of this component is a 'choose' component
		FacesUtil.AssertValidParams( context, component.getParent(), UIChooseComponent.class );

		// If this component is not rendered skip all the logic
		if ( !component.isRendered() )
		{
			return;
		}

		UIChooseComponent chooseComp = (UIChooseComponent) component.getParent();

		// Render children only a true condition hasn't been already
		// evaluated (assure mutual exclusion).
//		if ( chooseComp.isFoundTrue() )
//		{
//			return;
//		}

		// Render children only if the specified condition holds.

		boolean allFalse = true;
		for (UIComponent child : chooseComp.getChildren())
		{
			if ( child instanceof UIChooseWhenComponent )
			{
				UIChooseWhenComponent whenComp = (UIChooseWhenComponent) child;

				if ( whenComp.getTest() )
				{
					allFalse = false;
					break;
				}
			}
		}
		if ( allFalse )
		{
			super.encodeChildren(context, component);
//			chooseComp.setFoundTrue( true );
		}
	}

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
