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

package it.unipmn.di.dcs.sharegrid.web.faces.renderer.html;

import it.unipmn.di.dcs.common.annotation.Experimental;
import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIInputRadioComponent;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * Renderer for input radio component.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@Experimental("Need more testing")
public class HtmlInputRadioRenderer extends AbstractHtmlRenderer
{
	/** A constructor. */
	public HtmlInputRadioRenderer()
	{
		super();
	}

	@Override
	public void doDecoding(FacesContext context, UIComponent component)
	{
		super.doDecoding( context, component );

		// If there is a request parameter that matches the
		// group name property, this component is one of the possible
		// selections. We need to match the value of the parameter to the
		// the component's value to see if this is the selected component.
		//
		UIInputRadioComponent radioComp = null;
		radioComp = (UIInputRadioComponent) component;
		String groupName = radioComp.getGroupName();
		boolean inGroup = groupName != null;

		// If groupName is null use the clientId.
		//
		if (groupName == null)
		{
			groupName = component.getClientId(context);
		}

		if ( radioComp.isChecked() )
		{
			radioComp.setSubmittedValue( radioComp.getValue() );
		}
	}

	@Override
	protected void doEncodingBegin(FacesContext context, UIComponent component) throws IOException
	{
		super.doEncodingBegin( context, component );

		ResponseWriter writer = context.getResponseWriter();

		String clientId = component.getClientId(context);

		writer.startElement("input", component);
		writer.writeAttribute("type", "radio", null);
		writer.writeAttribute("id", clientId, "id");

		UIInputRadioComponent radioComp = null;

		try
		{
			radioComp = (UIInputRadioComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IOException( "Not a valid radio component.", cce );
		}

		String name = null;
		name = radioComp.getGroupName();
		if ( Strings.IsNullOrEmpty(name) )
		{
			name = clientId;
		}
		writer.writeAttribute("name", name, "groupName");

		if ( radioComp.isChecked() )
		{
			writer.writeAttribute("checked", Boolean.TRUE, "checked");
		}

		writer.endElement("input");
		writer.flush();
	}
}
