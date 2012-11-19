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

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.JspException;

/**
 * A simple <em>for-each</em> JSP tag.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ForEachTag extends SimpleTagSupport
{
	private Object items;
	private String var;

	/** Set the items to be iterated. */
	public void setItems(Object value)
	{
		this.items = value;
/*
		if (value instanceof Object[])
		{
			this.items = (Object[]) value;
		}
		else if (value instanceof boolean[])
		{
			this.items = (boolean[]) value;
		}
		else if (value instanceof byte[])
		{
			this.items = (byte[]) value;
		}
		else if (value instanceof char[])
		{
			this.items = (char[]) value;
		}
		else if (value instanceof short[])
		{
			this.items = (short[]) value;
		}
		else if (value instanceof int[])
		{
			this.items = (int[]) value;
		}
		else if (value instanceof long[])
		{
			this.items = (long[]) value;
		}
		else if (value instanceof float[])
		{
			this.items = (float[]) value;
		}
		else if (value instanceof double[])
		{
			this.items = (double[]) value;
		}
		else if (value instanceof Collection)
		{
			this.items = (Collection) value;
		}
		else if (value instanceof Iterator)
		{
			this.items = (Iterator) value;
		}
		else if (value instanceof Enumeration)
		{
			this.items = (Enumeration) value;
		}
		else if (value instanceof Map)
		{
			this.items = (Map) value;
		}
		//else if (value instanceof ResultSet)
		//{
		//	items = (ResultSet) value;
		//}
		else if (value instanceof String)
		{
			this.items = (String) value;
		}
		else
		{
			this.items = value;
		}
*/
	}

//	public void setItems(Object[] items)
//	{
//		this.items = items;
//	}

	/** Set the binding variable. */
	public void setVar(String value)
	{
		this.var = value;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		if (this.items instanceof Object[])
		{
			Object[] array = (Object[]) this.items;

			for(int i=0; i < array.length; i++)
			{
				this.getJspContext().setAttribute( this.var, array[i] );
				this.getJspBody().invoke(null);
			}
		}
		else if (this.items instanceof boolean[])
		{
			boolean[] array = (boolean[]) this.items;

			for(int i=0; i < array.length; i++)
			{
				this.getJspContext().setAttribute( this.var, array[i] );
				this.getJspBody().invoke(null);
			}
		}
		else if (this.items instanceof byte[])
		{
			byte[] array = (byte[]) this.items;

			for(int i=0; i < array.length; i++)
			{
				this.getJspContext().setAttribute( this.var, array[i] );
				this.getJspBody().invoke(null);
			}
		}
		else if (this.items instanceof char[])
		{
			char[] array = (char[]) this.items;

			for(int i=0; i < array.length; i++)
			{
				this.getJspContext().setAttribute( this.var, array[i] );
				this.getJspBody().invoke(null);
			}
		}
		else if (this.items instanceof short[])
		{
			short[] array = (short[]) this.items;

			for(int i=0; i < array.length; i++)
			{
				this.getJspContext().setAttribute( this.var, array[i] );
				this.getJspBody().invoke(null);
			}
		}
		else if (this.items instanceof int[])
		{
			int[] array = (int[]) this.items;

			for(int i=0; i < array.length; i++)
			{
				this.getJspContext().setAttribute( this.var, array[i] );
				this.getJspBody().invoke(null);
			}
		}
		else if (this.items instanceof long[])
		{
			long[] array = (long[]) this.items;

			for(int i=0; i < array.length; i++)
			{
				this.getJspContext().setAttribute( this.var, array[i] );
				this.getJspBody().invoke(null);
			}
		}
		else if (this.items instanceof float[])
		{
			float[] array = (float[]) this.items;

			for(int i=0; i < array.length; i++)
			{
				this.getJspContext().setAttribute( this.var, array[i] );
				this.getJspBody().invoke(null);
			}
		}
		else if (this.items instanceof double[])
		{
			double[] array = (double[]) this.items;

			for(int i=0; i < array.length; i++)
			{
				this.getJspContext().setAttribute( this.var, array[i] );
				this.getJspBody().invoke(null);
			}
		}
		else if (this.items instanceof Collection)
		{
			Collection col = (Collection) this.items;

			for (Object item : col)
			{
				this.getJspContext().setAttribute( this.var, item );
				this.getJspBody().invoke(null);
			}
		}
		else if (this.items instanceof Iterator)
		{
			Iterator it = (Iterator) this.items;

			while ( it.hasNext() )
			{
				this.getJspContext().setAttribute( this.var, it.next() );
				this.getJspBody().invoke(null);
			}
		}
		else if (this.items instanceof Enumeration)
		{
			Enumeration enu = (Enumeration) this.items;

			while ( enu.hasMoreElements() )
			{
				this.getJspContext().setAttribute( this.var, enu.nextElement() );
				this.getJspBody().invoke(null);
			}
		}
		else if (this.items instanceof Map)
		{
			Map map = (Map) this.items;

			for (Object item : map.values())
			{
				this.getJspContext().setAttribute( this.var, item );
				this.getJspBody().invoke(null);
			}
		}
		//else if (this.items instanceof ResultSet)
		//{
		//	items = (ResultSet) value;
		//}
		else if (this.items instanceof String)
		{
			this.getJspContext().setAttribute( this.var, (String) this.items );
			this.getJspBody().invoke(null);
		}
		else
		{
			this.getJspContext().setAttribute( this.var, this.items );
			this.getJspBody().invoke(null);
		}
	}
}
