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

package it.unipmn.di.dcs.sharegrid.web.faces.component;

import javax.el.ValueExpression;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

/**
 * Base class for output components.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractUIOutputComponent extends UIOutput implements IUIOutputComponent
{
	private Object[] values;

	/** A constructor. */
	protected AbstractUIOutputComponent()
	{
		super();
	}

	/** A constructor. */
	protected AbstractUIOutputComponent(String id)
	{
		this();
		this.setId(id);
	}

	@Override
	public void restoreState(FacesContext context, Object state)
	{
		this.values = (Object[]) state;

		super.restoreState(context, values[0]);

		this.dir = (String) values[1];
		this.lang = (String) values[2];
		this.onclick = (String) values[3];
		this.ondblclick = (String) values[4];
		this.onkeydown = (String) values[5];
		this.onkeypress = (String) values[6];
		this.onkeyup = (String) values[7];
		this.onmousedown = (String) values[8];
		this.onmousemove = (String) values[9];
		this.onmouseout = (String) values[10];
		this.onmouseover = (String) values[11];
		this.onmouseup = (String) values[12];
		this.style = (String) values[13];
		this.styleClass = (String) values[14];
		this.tabindex = (Integer) values[15];
		this.title = (String) values[16];
	}

	@Override
	public Object saveState(FacesContext context)
	{
		if (this.values == null)
		{
			this.values = new Object[17];
		}

		this.values[0] = super.saveState(context);

		this.values[1] = this.dir;
		this.values[2] = this.lang;
		this.values[3] = this.onclick;
		this.values[4] = this.ondblclick;
		this.values[5] = this.onkeydown;
		this.values[6] = this.onkeypress;
		this.values[7] = this.onkeyup;
		this.values[8] = this.onmousedown;
		this.values[9] = this.onmousemove;
		this.values[10] = this.onmouseout;
		this.values[11] = this.onmouseover;
		this.values[12] = this.onmouseup;
		this.values[13] = this.style;
		this.values[14] = this.styleClass;
		this.values[15] = this.tabindex;
		this.values[16] = this.title;

		return this.values;
	}

	/**
	 * Specifies the base direction of directionally neutral text (i.e., text
	 * that doesn't have inherent directionality as defined in UNICODE) in an
	 * element's content and attribute values.
	 *
	 * It also specifies the directionality of tables.
	 * Possible values:
	 * <ul>
	 * <li>{@code LTR}: Left-to-right text or table.</li>
	 * <li>{@code RTL}: Right-to-left text or table.</li>
	 * </ul>
	 */
	private String dir;
	public String getDir()
	{
		if (null != this.dir)
		{
			return this.dir;
		}

		ValueExpression ve = getValueExpression("dir");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setDir(String dir)
	{
		this.dir = dir;
	}

	/**
	 * Specifies the base language of an element's attribute values and text
	 * content.
	 *
	 * The default value of this attribute is unknown.
	 */
	private String lang;
	public String getLang()
	{
		if (null != this.lang)
		{
			return this.lang;
		}

		ValueExpression ve = getValueExpression("lang");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setLang(String lang)
	{
		this.lang = lang;
	}

	/**
	 * Event occurring when the pointing device button is clicked over this
	 * element.
	 */
	private String onclick;
	public String getOnclick()
	{
		if (null != this.onclick)
		{
			return this.onclick;
		}

		ValueExpression ve = getValueExpression("onclick");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setOnclick(String onclick)
	{
		this.onclick = onclick;
	}


	/**
	 * Event occurring when the pointing device button is double clicked over
	 * this element.
	 */
	private String ondblclick;
	public String getOndblclick()
	{
		if (null != this.ondblclick)
		{
			return this.ondblclick;
		}

		ValueExpression ve = getValueExpression("ondblclick");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setOndblclick(String ondblclick)
	{
		this.ondblclick = ondblclick;
	}

	/**
	 * Event occurring when a key is pressed down over this element.
	 */
	private String onkeydown;
	public String getOnkeydown()
	{
		if (null != this.onkeydown)
		{
			return this.onkeydown;
		}
		ValueExpression ve = getValueExpression("onkeydown");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setOnkeydown(String onkeydown)
	{
		this.onkeydown = onkeydown;
	}

	/**
	 * Event occurring when a key is pressed and released over this element.
	 */
	private String onkeypress;
	public String getOnkeypress()
	{
		if (null != this.onkeypress)
		{
			return this.onkeypress;
		}

		ValueExpression ve = getValueExpression("onkeypress");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setOnkeypress(String onkeypress)
	{
		this.onkeypress = onkeypress;
	}

	/**
	 * Event occurring when a key is released over this element.
	 */
	private String onkeyup;
	public String getOnkeyup()
	{
		if (null != this.onkeyup)
		{
			return this.onkeyup;
		}

		ValueExpression ve = getValueExpression("onkeyup");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setOnkeyup(String onkeyup)
	{
		this.onkeyup = onkeyup;
	}

	/**
	 * Events occurring when the pointing device button is pressed down over
	 * this element.
	 */
	private String onmousedown;
	public String getOnmousedown()
	{
		if (null != this.onmousedown)
		{
			return this.onmousedown;
		}

		ValueExpression ve = getValueExpression("onmousedown");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setOnmousedown(String onmousedown)
	{
		this.onmousedown = onmousedown;
	}

	/**
	 * Event occurring when the pointing device button is moved within this
	 * element.
	 */
	private String onmousemove;
	public String getOnmousemove()
	{
		if (null != this.onmousemove)
		{
			return this.onmousemove;
		}
		ValueExpression ve = getValueExpression("onmousemove");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setOnmousemove(String onmousemove)
	{
		this.onmousemove = onmousemove;
	}

	/**
	 * Event occurring when the pointing device button is moved away from this
	 * element.
	 */
	private String onmouseout;
	public String getOnmouseout()
	{
		if (null != this.onmouseout)
		{
			return this.onmouseout;
		}
		ValueExpression ve = getValueExpression("onmouseout");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setOnmouseout(String onmouseout)
	{
		this.onmouseout = onmouseout;
	}

	/**
	 * Event occurring when the pointing device button is moved onto this
	 * element.
	 */
	private String onmouseover;
	public String getOnmouseover()
	{
		if (null != this.onmouseover)
		{
			return this.onmouseover;
		}

		ValueExpression ve = getValueExpression("onmouseover");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setOnmouseover(String onmouseover)
	{
		this.onmouseover = onmouseover;
	}

	/**
	 * Event occurring when the pointing device button is released over this
	 * element.
	 */
	private String onmouseup;
	public String getOnmouseup()
	{
		if (null != this.onmouseup)
		{
			return this.onmouseup;
		}
		ValueExpression ve = getValueExpression("onmouseup");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setOnmouseup(String onmouseup)
	{
		this.onmouseup = onmouseup;
	}

	/**
	 * CSS style(s) to be applied when this component is rendered.
	 */
	private String style;
	public String getStyle()
	{
		if (null != this.style)
		{
			return this.style;
		}
		ValueExpression ve = getValueExpression("style");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setStyle(String style)
	{
		this.style = style;
	}

	/**
	 * Space-separated list of CSS style class(es) to be applied when this
	 * element is rendered.
	 *
	 * This value must be passed through as the "class" attribute on generated
	 * markup.
	 */
	private String styleClass;
	public String getStyleClass()
	{
		if (null != this.styleClass)
		{
			return this.styleClass;
		}
		ValueExpression ve = getValueExpression("styleClass");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setStyleClass(String styleClass)
	{
		this.styleClass = styleClass;
	}

	/**
	 * Position of this element in the tabbing order for the current document.
	 *
	 * This value must be an integer between 0 and 32767.
	 * Those elements that do not support the tabindex attribute or support it
	 * and assign it a value of "0" are navigated next. These elements are
	 * navigated in the order they appear in the character stream.
	 *
	 * Defaults to {@code 0}.
	 */
	private Integer tabindex;
	public int getTabindex()
	{
		if (null != this.tabindex)
		{
			return this.tabindex;
		}
		ValueExpression ve = getValueExpression("tabindex");
		if (ve != null)
		{
			return (Integer) ve.getValue(this.getFacesContext().getELContext());
		}

		return 0;
	}
	public void setTabindex(int value)
	{
		this.tabindex = value;
	}

	/**
	 * Advisory title information about markup elements generated
	 * for this component.
	 */
	private String title;
	public String getTitle()
	{
		if (null != this.title)
		{
			return this.title;
		}
		ValueExpression ve = getValueExpression("title");
		if (ve != null)
		{
			Object result = null;

			result = ve.getValue(this.getFacesContext().getELContext());

			return (result != null)
				? (String) result
				: null;
		}

		return null;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
}
