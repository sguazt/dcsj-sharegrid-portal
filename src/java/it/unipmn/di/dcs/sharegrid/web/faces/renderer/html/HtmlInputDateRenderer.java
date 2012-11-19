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

import java.io.IOException;

import java.util.Map;
import java.util.TimeZone;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;

/**
 * Renders the UIInput component as a date field.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class HtmlInputDateRenderer extends AbstractHtmlRenderer
{
	/**
	 * The title attribute.
	 */
	protected static String TITLE_ATTR = "title";

	/**
	 * The onchange attribute.
	 */
	protected static String ONCHANGE_ATTR = "onchange";

	public HtmlInputDateRenderer()
	{
		super();
	}

	@Override
	public void doDecoding(FacesContext context, UIComponent  component)
	{
		super.doDecoding(context, component);

		UIInput input = (UIInput)component;
		String clientId = input.getClientId(context);

		ExternalContext external = context.getExternalContext();
		Map<String,String> requestParams = external.getRequestParameterMap();
		String submittedValue = (String)requestParams.get(clientId);

		// store the submitted value from the request on the input component
		input.setSubmittedValue(submittedValue);
	}

	@Override
	public void doEncodingEnd(FacesContext context, UIComponent component) throws IOException
	{
		String valueString = getValueAsString(context, component);
		String clientId = component.getClientId(context);

		Map<String,Object> attrs = component.getAttributes();

		String title = (String) attrs.get(TITLE_ATTR);
		String onchange = (String) attrs.get(ONCHANGE_ATTR);

		ResponseWriter rw = context.getResponseWriter();
		rw.startElement("div", component);
		if (title != null)
		{
			rw.writeAttribute("title", title, TITLE_ATTR);
		}

		// <input id="[clientId]" name="[clientId]"
		//        value="[converted-value]" onchange="[onchange]" />
		rw.startElement("input", component);
		rw.writeAttribute("id", clientId, null);
		rw.writeAttribute("name", clientId, null);
		if (valueString != null)
		{
			rw.writeAttribute("value", valueString, null);
		}
		if (onchange != null)
		{
			rw.writeAttribute("onchange", onchange, ONCHANGE_ATTR);
		}
		rw.endElement("input");

		// <img class="dcsjsf-inputDate-overlay"
		//      src="<context-root>/<resource-basepath>/inputDateOverlay.gif" >
		ViewHandler handler = context.getApplication().getViewHandler();
		String overlayURL = null;
		overlayURL = handler.getResourceURL(
				context, 
				"/resources/theme/current/dcsjsf/inputdate/img/calendar.gif" //FIXME: hard-coded path
		);
		rw.startElement("img", null);
		rw.writeAttribute("class", "dcsjsf-inputDate-overlay", null);
		rw.writeAttribute("src", overlayURL, null);
		rw.endElement("img");

		rw.endElement("div");
		rw.flush();

		super.doEncodingEnd(context, component);
	}

	@Override
	public Object getConvertedValue(FacesContext context, UIComponent  component, Object submittedValue) throws ConverterException
	{
		UIInput input = (UIInput)component;
		Converter converter = this.getConverter(context, input);
		String valueString = (String)submittedValue;
		return converter.getAsObject(context, component, valueString);
	}

	@Override
	public boolean getRendersChildren()
	{
		return true;
	}

	/**
	 * Returns the Converter to use when converting the newly submitted
	 * request parameter value to a strongly-typed object.
	 *
	 * @param context  the Faces context
	 * @param input    the Faces input component
	 *
	 * @return  the Faces Converter to be used
	 */
	protected final Converter getConverter(FacesContext context, UIInput input)
	{
		Converter converter = input.getConverter();
		if (converter == null)
		{
			// default the converter
			DateTimeConverter datetime = new DateTimeConverter();
			datetime.setLocale(context.getViewRoot().getLocale());
			datetime.setTimeZone(TimeZone.getDefault());
			converter = datetime;
		}
		return converter;
	}

	@Override
	protected void doEncodingResources(FacesContext context, UIComponent  component) throws IOException
	{
		this.renderStyleResource(
				context,
				"/resources/theme/current/dcsjsf/inputdate/css/screen.css", //FIXME: hard-coded path
				"text/css"
		);
	}

	/**
	 * Returns the submitted value, if present, otherwise returns 
	 * the value attribute converted to string.
	 *
	 * @param context    the Faces context
	 * @param component  the Faces component
	 * 
	 * @return  the value string for the specified component
	 *
	 * @throws IOException  if an I/O exception occurs during rendering
	 */
	protected String getValueAsString(FacesContext context, UIComponent  component) throws IOException
	{
		// lookup the submitted value
		UIInput input = (UIInput)component;
		String valueString = (String)input.getSubmittedValue();

		// on initial render (or after a successful postback)
		// the submitted value will be null
		if (valueString == null)
		{
			// lookup the strongly-typed value for this input
			Object value = input.getValue();
			if (value != null)
			{
				// if present, convert the strongly-typed value
				// to a string for rendering
				Converter converter = this.getConverter(context, input);
				valueString = converter.getAsString(context, component, value);
			}
		}

		return valueString;
	}
}
