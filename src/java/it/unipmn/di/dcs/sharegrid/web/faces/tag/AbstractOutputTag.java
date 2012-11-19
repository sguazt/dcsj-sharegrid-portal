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

package it.unipmn.di.dcs.sharegrid.web.faces.tag;

import it.unipmn.di.dcs.sharegrid.web.faces.component.AbstractUIOutputComponent;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
//import javax.faces.webapp.UIComponentELTag;

/**
 * Base class for output visual tags.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractOutputTag extends AbstractTag
{ 
	/** A constructor. */
	protected AbstractOutputTag()
	{
		super();
	}

	/** PROPERTY: converter. */
	private ValueExpression converter;
	public void setConverter(ValueExpression converter)
	{
		this.converter = converter;
	}

	/** PROPERTY: converterMessage. */
	private ValueExpression converterMessage;
	public void setConverterMessage(ValueExpression converterMessage)
	{
		this.converterMessage = converterMessage;
	}

	/** PROPERTY: value. */
	private ValueExpression value;
	public void setValue(ValueExpression value)
	{
		this.value = value;
	}

	/** PROPERTY: dir. */
	private ValueExpression dir;
	public void setDir(ValueExpression dir)
	{
		this.dir = dir;
	}

	/** PROPERTY: lang. */
	private ValueExpression lang;
	public void setLang(ValueExpression lang)
	{
		this.lang = lang;
	}

	/** PROPERTY: onclick. */
	private ValueExpression onclick;
	public void setOnclick(ValueExpression onclick)
	{
		this.onclick = onclick;
	}

	/** PROPERTY: ondblclick. */
	private ValueExpression ondblclick;
	public void setOndblclick(ValueExpression ondblclick)
	{
		this.ondblclick = ondblclick;
	}

	/** PROPERTY: onkeydown. */
	private ValueExpression onkeydown;
	public void setOnkeydown(ValueExpression onkeydown)
	{
		this.onkeydown = onkeydown;
	}

	/** PROPERTY: onkeypress. */
	private ValueExpression onkeypress;
	public void setOnkeypress(ValueExpression onkeypress)
	{
		this.onkeypress = onkeypress;
	}

	/** PROPERTY: onkeyup. */
	private ValueExpression onkeyup;
	public void setOnkeyup(ValueExpression onkeyup)
	{
		this.onkeyup = onkeyup;
	}

	/** PROPERTY: onmousedown. */
	private ValueExpression onmousedown;
	public void setOnmousedown(ValueExpression onmousedown)
	{
		this.onmousedown = onmousedown;
	}

	/** PROPERTY: onmousemove. */
	private ValueExpression onmousemove;
	public void setOnmousemove(ValueExpression onmousemove)
	{
		this.onmousemove = onmousemove;
	}

	/** PROPERTY: onmouseout. */
	private ValueExpression onmouseout;
	public void setOnmouseout(ValueExpression onmouseout)
	{
		this.onmouseout = onmouseout;
	}

	/** PROPERTY: onmouseover. */
	private ValueExpression onmouseover;
	public void setOnmouseover(ValueExpression onmouseover)
	{
		this.onmouseover = onmouseover;
	}

	/** PROPERTY: onmouseup. */
	private ValueExpression onmouseup;
	public void setOnmouseup(ValueExpression onmouseup)
	{
		this.onmouseup = onmouseup;
	}

	/** PROPERTY: style. */
	private ValueExpression style;
	public void setStyle(ValueExpression style)
	{
		this.style = style;
	}

	/** PROPERTY: styleClass. */
	private ValueExpression styleClass;
	public void setStyleClass(ValueExpression styleClass)
	{
		this.styleClass = styleClass;
	}

	/** PROPERTY: tabindex. */
	private ValueExpression tabindex;
	public void setTabindex(ValueExpression tabindex)
	{
		this.tabindex = tabindex;
	}

	/** PROPERTY: title. */
	private ValueExpression title;
	public void setTitle(ValueExpression title)
	{
		this.title = title;
	}

     	@Override
	public void release()
	{
		super.release();

		// component properties
		this.converter = null;
		this.converterMessage = null;
		this.value = null;

		// rendered attributes
		this.dir = null;
		this.lang = null;
		this.onclick = null;
		this.ondblclick = null;
		this.onkeydown = null;
		this.onkeypress = null;
		this.onkeyup = null;
		this.onmousedown = null;
		this.onmousemove = null;
		this.onmouseout = null;
		this.onmouseover = null;
		this.onmouseup = null;
		this.style = null;
		this.styleClass = null;
		this.tabindex = null;
		this.title = null;
	}

	@Override
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

		AbstractUIOutputComponent output = null;
		try
		{
			output = (AbstractUIOutputComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Component " + component.toString() + " not expected type.  Expected: " + AbstractUIOutputComponent.class.getName() + ". Perhaps you're missing a tag?", cce);
		}

                if (converter != null)
		{
			if (!converter.isLiteralText())
			{
				output.setValueExpression("converter", converter);
			}
			else
			{
				Converter conv = FacesContext.getCurrentInstance().getApplication().createConverter(converter.getExpressionString());
				output.setConverter(conv);
			}
                }
                if (converterMessage != null)
		{
                	output.setValueExpression("converterMessage", converterMessage);
                }
		if (value != null)
		{
			output.setValueExpression("value", value);
		}
		if (dir != null)
		{
			output.setValueExpression("dir", dir);
		}
		if (lang != null)
		{
			output.setValueExpression("lang", lang);
		}
		if (onclick != null)
		{
			output.setValueExpression("onclick", onclick);
		}
		if (ondblclick != null)
		{
			output.setValueExpression("ondblclick", ondblclick);
		}
		if (onkeydown != null)
		{
			output.setValueExpression("onkeydown", onkeydown);
		}
		if (onkeypress != null)
		{
			output.setValueExpression("onkeypress", onkeypress);
		}
		if (onkeyup != null)
		{
			output.setValueExpression("onkeyup", onkeyup);
		}
		if (onmousedown != null)
		{
			output.setValueExpression("onmousedown", onmousedown);
		}
		if (onmousemove != null)
		{
			output.setValueExpression("onmousemove", onmousemove);
		}
		if (onmouseout != null)
		{
			output.setValueExpression("onmouseout", onmouseout);
		}
		if (onmouseover != null)
		{
			output.setValueExpression("onmouseover", onmouseover);
		}
		if (onmouseup != null)
		{
			output.setValueExpression("onmouseup", onmouseup);
		}
		if (style != null)
		{
			output.setValueExpression("style", style);
		}
		if (styleClass != null)
		{
			output.setValueExpression("styleClass", styleClass);
		}
		if (tabindex != null)
		{
			output.setValueExpression("tabindex", tabindex);
		}
		if (title != null)
		{
			output.setValueExpression("title", title);
		}
	}

	public String getDebugString()
	{
		return "id: " + this.getId() + " class: " + this.getClass().getName();
	}
}
