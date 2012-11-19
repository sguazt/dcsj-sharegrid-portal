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

package it.unipmn.di.dcs.sharegrid.web.faces.view;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>Default implementation of {@link PageBeanMapper} that corresponds to
 * the mapping performed by the IDE at design time.  This implementation
 * uses the following rules:</p>
 * <ul>
 * <li>Strip the leading '/' character (if any).</li>
 * <li>String the trailing extension (if any).</li>
 * <li>Replace any '/' characters with '$' characters</li>
 * <li>If the resulting string matches the name of one of the implicit
 * variables recognized by JSF EL expressions, prefix it with '_'.
 * </ul>
 *
 * This is inspired to the Sun RAVE APPBASE project.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class PageBeanMapperImpl implements PageBeanMapper
{
	/**
	 * <p>The set of reserved identifiers recognized by the JavaServer Faces
	 * expression language machinery.</p>
	 */
	private static Set<String> reserved = new HashSet<String>();

	static {
		reserved.add("applicationScope"); //NOI18N
		reserved.add("cookies");          //NOI18N
		reserved.add("facesContext");     //NOI18N
		reserved.add("header");           //NOI18N
		reserved.add("headerValues");     //NOI18N
		reserved.add("initParam");        //NOI18N
		reserved.add("param");            //NOI18N
		reserved.add("paramValues");      //NOI18N
		reserved.add("requestScope");     //NOI18N
		reserved.add("sessionScope");     //NOI18N
		reserved.add("view");             //NOI18N
	};

	//@{ PageBeanMapper implementation

	/**
	 * <p>Map the specified view identifier (which will be the context relative
	 * path of a JSP page) to the managed bean name of the corresponding
	 * page bean (which must extend {@link AbstractPageBean}).</p>
	 *
	 * @param viewId View identifier of the JSP page to be mapped
	 */
	public String mapViewId(String viewId)
	{
		if ((viewId == null) || (viewId.length() < 1))
		{
			return viewId;
		}

		// Strip any leading '/' character
		if (viewId.charAt(0) == '/')
		{ //NOI18N
			viewId = viewId.substring(1);
		}

		// Strip any trailing extension
		int slash = viewId.lastIndexOf('/'); //NOI18N
		int period = viewId.lastIndexOf('.'); //NOI18N
		if (period >= 0)
		{
			if ((slash < 0) || (period > slash))
			{
				viewId = viewId.substring(0, period);
			}
		}

		// Replace remaining '/' characters with '$'
		viewId = viewId.replace('/', '$'); //NOI18N

		// Prefix with '_' if necessary, and return
		if (reserved.contains(viewId))
		{
			return "_" + viewId;
		}
		else
		{
			return viewId;
		}
	}

	//@} PageBeanMapper implementation
}
