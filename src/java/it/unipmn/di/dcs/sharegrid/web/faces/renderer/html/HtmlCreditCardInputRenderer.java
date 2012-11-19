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

import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesUtil;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

//import org.jdom.Attribute;
//import org.jdom.Element;
//import org.jdom.output.Format;
//import org.jdom.output.XMLOutputter;

/**
 * Renderer for credit card number input components.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class HtmlCreditCardInputRenderer extends AbstractHtmlRenderer
{
	/** A constructor. */
	public HtmlCreditCardInputRenderer()
	{
		super();
	}

	@Override
	public void doDecoding(FacesContext context, UIComponent component)
	{
		if (component instanceof UIInput)
		{
			UIInput input = (UIInput) component;
			String clientId = input.getClientId(context);
			Map requestMap = context.getExternalContext().getRequestParameterMap();
			String newValue = (String) requestMap.get(clientId);
			if (newValue != null)
			{
				input.setSubmittedValue(newValue);
			}
		}
	}

	@Override
	public void doEncodingBegin(FacesContext context, UIComponent component) throws IOException
	{
		// empty
	}

	@Override
	public void doEncodingEnd(FacesContext context, UIComponent component) throws IOException
	{
		this.doEncodingEndDefault(context, component);
	}

	protected void doEncodingEndDefault(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement("input", component);
		writer.writeAttribute("type", "text", "text");
		String id = (String) component.getClientId(context);
		writer.writeAttribute("id", id, "id");
		writer.writeAttribute("name", id, "id");
		Integer size = (Integer) component.getAttributes().get("size");
		if (size != null)
		{
			writer.writeAttribute("size", size, "size");
		}
		Object currentValue = this.getValue(component);
		writer.writeAttribute("value", this.formatValue(currentValue), "value");
		writer.endElement("input");
	}

//	protected void doEncodingEndJDOM(FacesContext ctx, UIComponent component) throws IOException
//	{
//		ResponseWriter writer = ctx.getResponseWriter();
//		Element input = new Element("input");
//		input.setAttribute(new Attribute("type", "text"));
//		String id = (String) component.getClientId(ctx);
//		input.setAttribute(new Attribute("id", id));
//		input.setAttribute(new Attribute("name", id));
//		String size = (String) component.getAttributes().get("size");
//		if (null != size)
//		{
//			input.setAttribute(new Attribute("size", size));
//		}
//		Object currentValue = getValue(component);
//		input.setAttribute(new Attribute("value", formatValue(currentValue)));
//		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
//		out.output(input, writer);
//	}

	protected Object getValue(UIComponent component)
	{
		Object value = null;
		if (component instanceof UIInput)
		{
			value = ((UIInput) component).getSubmittedValue();
		}
		// if its not a UIInput or the submitted value
		// was null then get the value (it should
		// always be a UIInput)
		if (null == value && component instanceof ValueHolder)
		{
			value = ((ValueHolder) component).getValue();
		}

		return value;
	}

	private String formatValue(Object currentValue)
	{
		// this should be a bit more sophisiticated
		// in essence what should happen here is any
		// conversion that needs to take place.
		return currentValue.toString();
	}

	@Deprecated
	private void assertValidInput(FacesContext context, UIComponent component)
	{
		if (context == null)
		{
			throw new NullPointerException( "context should not be null");
		}
		else if (component == null)
		{
			throw new NullPointerException("component should not be null");
		}
	}
}
