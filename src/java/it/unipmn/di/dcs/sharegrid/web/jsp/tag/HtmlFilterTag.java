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

package it.unipmn.di.dcs.sharegrid.web.jsp.tag;

import it.unipmn.di.dcs.common.io.FastStringWriter;
import it.unipmn.di.dcs.common.util.Strings;

import java.io.IOException;
//import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Tag that replaces special HTML characters (like less than and greater than
 * signs) with their HTML character entities.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class HtmlFilterTag extends SimpleTagSupport
{
	private static final Map<Character,String> SpecialCharsMap = new HashMap<Character,String>();

	static
	{
		SpecialCharsMap.put('&', "&amp;"); // or &#38;
		SpecialCharsMap.put('\'', "&apos;"); // or &#39;
		SpecialCharsMap.put('>', "&gt;"); // or &#62;
		SpecialCharsMap.put('<', "&lt;"); // or &#60;
		SpecialCharsMap.put('"', "&quot;"); // or &#34;
		SpecialCharsMap.put('\n', "<br/>"); // or &#34;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		// Buffer tag body's output
		FastStringWriter swr = null;
		swr = new FastStringWriter();
		this.getJspBody().invoke(swr);

		// Filter out any special HTML characters
		// (e.g., "<" becomes "&lt;")
		String output = this.filter(swr.toString());

		// Send output to the client
		JspWriter out = getJspContext().getOut();
		out.print(output);
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

			if (SpecialCharsMap.containsKey(c))
			{
				filtered.append(SpecialCharsMap.get(c));
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

				if (SpecialCharsMap.containsKey(c))
				{
					return true;
				}
			}
		}

		return false;
	}
}
