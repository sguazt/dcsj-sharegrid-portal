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

import java.io.IOException;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

/**
 * The <em>then</em> part of the <em>if-then-else</em> JSP tag.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class IfThenElseThenTag extends SimpleTagSupport
{
	@Override
	public void doTag() throws JspException, IOException
	{
		// Get parent tag (if tag)
		IfThenElseTag ifTag = (IfThenElseTag) this.getParent();
		if (ifTag != null)
		{
			// Decide whether to output body of then
			if ( ifTag.getEvalTest() )
			{
				this.getJspBody().invoke(null);
			}
		}
		else
		{
			throw new JspTagException( "Error: 'then' must be inside a 'ifThenElse' tag." );
		}
	}
}
