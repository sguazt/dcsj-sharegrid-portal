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

import javax.faces.component.UIInput;

import javax.faces.context.FacesContext;

/**
 * Base class for input components.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractUIInputComponent extends UIInput implements IUIComponent
{
	private Object[] values;

	/** A constructor. */
	protected AbstractUIInputComponent()
	{
		super();
	}

	/** A constructor. */
	protected AbstractUIInputComponent(String id)
	{
		this();

		this.setId(id);
	}

	@Override
	public void restoreState(FacesContext context, Object state)
	{
		this.values = (Object[]) state;

		super.restoreState(context, this.values[0]);

		this.accesskey = (String) this.values[1];
		this.dir = (String) this.values[2];
		this.disabled = (Boolean) this.values[3];
		this.lang = (String) this.values[4];
		this.onblur = (String) this.values[5];
		this.onchange = (String) this.values[6];
		this.onclick = (String) this.values[7];
		this.ondblclick = (String) this.values[8];
		this.onfocus = (String) this.values[9];
		this.onkeydown = (String) this.values[10];
		this.onkeypress = (String) this.values[11];
		this.onkeyup = (String) this.values[12];
		this.onmousedown = (String) this.values[13];
		this.onmousemove = (String) this.values[14];
		this.onmouseout = (String) this.values[15];
		this.onmouseover = (String) this.values[16];
		this.onmouseup = (String) this.values[17];
		this.readonly = (Boolean) this.values[18];
		this.style = (String) this.values[19];
		this.styleClass = (String) this.values[20];
		this.tabindex = (Integer) this.values[21];
		this.title = (String) this.values[22];
	}

	@Override
	public Object saveState(FacesContext context)
	{
		if (this.values == null)
		{
			this.values = new Object[23];
		}

		this.values[0] = super.saveState(context);

		this.values[1] = this.accesskey;
		this.values[2] = this.dir;
		this.values[3] = this.disabled;
		this.values[4] = this.lang;
		this.values[5] = this.onblur;
		this.values[6] = this.onchange;
		this.values[7] = this.onclick;
		this.values[8] = this.ondblclick;
		this.values[9] = this.onfocus;
		this.values[10] = this.onkeydown;
		this.values[11] = this.onkeypress;
		this.values[12] = this.onkeyup;
		this.values[13] = this.onmousedown;
		this.values[14] = this.onmousemove;
		this.values[15] = this.onmouseout;
		this.values[16] = this.onmouseover;
		this.values[17] = this.onmouseup;
		this.values[18] = this.readonly;
		this.values[19] = this.style;
		this.values[20] = this.styleClass;
		this.values[21] = this.tabindex;
		this.values[22] = this.title;

		return this.values;
	}

	/**
	 * This attribute assigns an access key to an element.
	 *
	 * An access key is a single character from the document character set.
	 */
	private String accesskey;
	public String getAccesskey()
	{
		if (null != this.accesskey)
		{
			return this.accesskey;
		}

		ValueExpression ve = this.getValueExpression("accesskey");
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
	public void setAccesskey(String accesskey)
	{
		this.accesskey = accesskey;
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
	 * When set for a form control, this boolean attribute disables the control
	 * for user input.
	 *
	 * Defaults to {@code false}.
	 */
	private Boolean disabled;
	public boolean isDisabled()
	{
		if (null != this.disabled)
		{
			return this.disabled;
		}

		ValueExpression ve = this.getValueExpression("disabled");
		if (ve != null)
		{
			return (Boolean) ve.getValue(this.getFacesContext().getELContext());
		}

		return false;
	}
	public void setDisabled(Boolean disabled)
	{
		this.disabled = disabled;
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
	 * Event occurring when an element loses focus either by the pointing device
	 * or by tabbing navigation.
	 */
	private String onblur;
	public String getOnblur()
	{
		if (null != this.onblur)
		{
			return this.onblur;
		}
		ValueExpression ve = getValueExpression("onblur");
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
	public void setOnblur(String onblur)
	{
		this.onblur = onblur;
	}


	private String onchange;
	/**
	 * Event occurring when a control loses the input focus and its value has been
	 * modified since gaining focus
	 */
	public String getOnchange()
	{
		if (null != this.onchange)
		{
			return this.onchange;
		}
		ValueExpression ve = getValueExpression("onchange");
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
	public void setOnchange(String onchange)
	{
		this.onchange = onchange;
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
	 * Event occurring when this element receives focus either by the pointing
	 * device or by tabbing navigation.
	 */
	private String onfocus;
	public String getOnfocus()
	{
		if (null != this.onfocus)
		{
			return this.onfocus;
		}
		ValueExpression ve = getValueExpression("onfocus");
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
	public void setOnfocus(String onfocus)
	{
		this.onfocus = onfocus;
	}

	/**
	 * Flag indicating that this component will prohibit changes by
	 * the user.
	 *
	 * The element may receive focus unless it has also been disabled. A value
	 * of false causes no attribute to be rendered, while a value of true
	 * causes the attribute to be rendered as readonly="readonly".
	 *
	 * Defaults to {@code false}.
	 */
	private Boolean readonly;
	public boolean isReadonly()
	{
		if (null != this.readonly)
		{
			return this.readonly;
		}
		ValueExpression ve = getValueExpression("readonly");
		if (ve != null)
		{
			return (Boolean) ve.getValue(this.getFacesContext().getELContext());
		}

		return false;
	}
	public void setReadonly(boolean readonly)
	{
		this.readonly = readonly;
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
