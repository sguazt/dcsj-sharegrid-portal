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

import it.unipmn.di.dcs.common.io.FastStringWriter;
import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIOutputTextFilterComponent;

import java.io.IOException;
//import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * Tag that replaces special HTML characters (like less than and greater than
 * signs) with their HTML character entities.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class HtmlOutputTextFilterRenderer extends AbstractHtmlRenderer
{
	private static final Map<Character,String> DefaultRulesMap = new HashMap<Character,String>();

	static
	{
		DefaultRulesMap.put('&', "&amp;"); // or &#38;
		DefaultRulesMap.put('\'', "&apos;"); // or &#39;
		DefaultRulesMap.put('>', "&gt;"); // or &#62;
		DefaultRulesMap.put('<', "&lt;"); // or &#60;
		DefaultRulesMap.put('"', "&quot;"); // or &#34;
		DefaultRulesMap.put('\n', "<br/>"); //FIXME: assume XHTML but in HTML we have <br>
	}

	/** The copy of the original response writer. */
	private ResponseWriter oldrwr;

	/** The string writer where the children rendering is redirected to. */
	private FastStringWriter swr;

	/** The customized rules map. */
	private Map<Character,String> rulesMap;

	/** The enclosing tag determined by the 'layout' attribute. */
	private String enclosingTag;

	@Override
	public boolean getRendersChildren()
	{
		return false;
	}

	@Override
	protected void doEncodingBegin(FacesContext context, UIComponent component) throws IOException
	{
		super.doEncodingBegin(context, component);

		UIOutputTextFilterComponent filterComp = null;
		filterComp = (UIOutputTextFilterComponent) component;

		// Check for inline filtering rules
		boolean useDefaultMap = true;
		if (!Strings.IsNullOrEmpty(filterComp.getRules()))
		{
			String[] rules = null;
			rules = filterComp.getRules().split(",");

			if (rules.length > 0)
			{
				this.rulesMap = new HashMap<Character,String>();
				useDefaultMap = false;

				for (String rule : rules)
				{
					String[] fromTo = null;
					fromTo = rule.split(":");
					this.rulesMap.put(fromTo[0].charAt(0), fromTo[1]);
				}
			}
		}
		if (useDefaultMap)
		{
			this.rulesMap = DefaultRulesMap;
		}

		// Save original response writer ...
		this.oldrwr = context.getResponseWriter();

		// Write enclosing tag (if needed)
		if (!Strings.IsNullOrEmpty(filterComp.getLayout()))
		{
			String enclosingTag = null;

			if ("inline".equals(filterComp.getLayout()))
			{
				this.enclosingTag = "span";
			}
			else if ("block".equals(filterComp.getLayout()))
			{
				this.enclosingTag = "div";
			}
			else
			{
				// layout is "none" or whatever but "inline" and "block"
				// ==> leave enclosingTag to null
			}

			if (!Strings.IsNullOrEmpty(this.enclosingTag))
			{
				// Render the start enclosing tag
				this.oldrwr.startElement(this.enclosingTag, component);

				// Render some attributes
				if (!Strings.IsNullOrEmpty(filterComp.getStyleClass()))
				{
					this.oldrwr.writeAttribute("class", filterComp.getStyleClass(), "styleClass");
				}

				if (!Strings.IsNullOrEmpty(filterComp.getStyle()))
				{
					this.oldrwr.writeAttribute("style", filterComp.getStyle(), "style");
				}
			}
		}

		// ... and redirect subsequent rendering to a custom writer

		// Buffer rendering into a string
		this.swr = new FastStringWriter();

		// Attach the string to a custom reponse writer
		ResponseWriter newrwr = null;
		if (oldrwr != null)
		{
			newrwr = this.oldrwr.cloneWithWriter(this.swr);
		}
		else
		{
			ExternalContext extContext = null;
			extContext = context.getExternalContext();
			newrwr = context.getRenderKit().createResponseWriter(
				this.swr,
				null,
				extContext.getRequestCharacterEncoding()
			);
		}

		// Set the custom writer as the current response writer
		context.setResponseWriter(newrwr);

		// Write the value attribute (if present) as text in the response
		// It will be filtered later along with possible children.
		if (filterComp.getValue() != null)
		{
			newrwr.write((String) filterComp.getValue());
		}
	}

	@Override
	protected void doEncodingEnd(FacesContext context, UIComponent component) throws IOException
	{
		// Retrieve the custom writer
		ResponseWriter newrwr = null;
		newrwr = context.getResponseWriter();

		// Make sure all output is flushed
		newrwr.flush();
		newrwr.close();
		newrwr = null;

		// Filter out any special HTML characters
		// (e.g., "<" becomes "&lt;")
		String output = this.filter(this.swr.toString());

		this.swr.close();
		this.swr = null;

		// Restore old writer
		context.setResponseWriter(this.oldrwr);

		// Writer filtered text to original response writer
		this.oldrwr.write(output);

		// Render the end of the enclosing tag (if needed)
		if (!Strings.IsNullOrEmpty(this.enclosingTag))
		{
			this.oldrwr.endElement(enclosingTag);
		}

		this.oldrwr = null;

		super.doEncodingEnd(context, component);
	}

	/**
	 * Replaces characters that have special HTML meanings
	 * with their corresponding HTML character entities.
	 *
	 * Given a string, this method replaces all occurrences of
	 * '&lt;' with '&amp;lt;', all occurrences of '&gt;' with
	 * '&amp;gt;', and (to handle cases that occur inside attribute
	 * values), all occurrences of double quotes "&quot;" with
	 * '&amp;quot;', all occurrences of single quotes "&apos;" with
	 * '&amp;apos;' and all occurrences of '&amp;' with '&amp;amp;'.
	 * Without such filtering, an arbitrary string
	 * could not safely be inserted in a Web page.
	 */
	protected String filter(String input)
	{
		if (!this.hasSpecialChars(input))
		{
			return input;
		}

		StringBuilder filtered = null;
		filtered = new StringBuilder(input.length());

		for(int i=0; i < input.length(); i++)
		{
			char c = input.charAt(i);

			if (this.rulesMap != null && this.rulesMap.containsKey(c))
			{
				filtered.append(this.rulesMap.get(c));
			}
			else
			{
				filtered.append(c);
			}
		}
		return filtered.toString();
	}

	/** Check for special characters inside the given input string. */
	protected boolean hasSpecialChars(String input)
	{
		if (!Strings.IsNullOrEmpty(input))
		{
			for(int i=0; i<input.length(); i++)
			{
				char c = input.charAt(i);

				if (this.rulesMap.containsKey(c))
				{
					return true;
				}
			}
		}

		return false;
	}
}
