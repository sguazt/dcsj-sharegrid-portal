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

import it.unipmn.di.dcs.sharegrid.web.faces.component.AbstractUIInputComponent;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.MethodExpressionValueChangeListener;
import javax.faces.validator.MethodExpressionValidator;
//import javax.faces.webapp.UIComponentELTag;

/**
 * Base class for visual input tags.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractInputTag extends AbstractTag
{ 
	/** A constructor. */
	protected AbstractInputTag()
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

	/** PROPERTY: immediate. */
	private ValueExpression immediate;
	public void setImmediate(ValueExpression immediate)
	{
		this.immediate = immediate;
	}

	/** PROPERTY: required. */
	private ValueExpression required;
	public void setRequired(ValueExpression required)
	{
		this.required = required;
	}

	/** PROPERTY: requiredMessage. */
	private ValueExpression requiredMessage;
	public void setRequiredMessage(ValueExpression requiredMessage)
	{
		this.requiredMessage = requiredMessage;
	}

	/** PROPERTY: validator. */
	private MethodExpression validator;
	public void setValidator(MethodExpression validator)
	{
		this.validator = validator;
	}

	/** PROPERTY: validatorMessage. */
	private ValueExpression validatorMessage;
	public void setValidatorMessage(ValueExpression validatorMessage)
	{
		this.validatorMessage = validatorMessage;
	}

	/** PROPERTY: valueChangeListener. */
	private MethodExpression valueChangeListener;
	public void setValueChangeListener(MethodExpression valueChangeListener)
	{
		this.valueChangeListener = valueChangeListener;
	}

	/** PROPERTY: accesskey. */
	private ValueExpression accesskey;
	public void setAccesskey(ValueExpression accesskey)
	{
		this.accesskey = accesskey;
	}

	/** PROPERTY: disabled. */
	private ValueExpression disabled;
	public void setDisabled(ValueExpression disabled)
	{
		this.disabled = disabled;
	}

	/** PROPERTY: onblur. */
	private ValueExpression onblur;
	public void setOnblur(ValueExpression onblur)
	{
		this.onblur = onblur;
	}

	/** PROPERTY: onchange. */
	private ValueExpression onchange;
	public void setOnchange(ValueExpression onchange)
	{
		this.onchange = onchange;
	}

	/** PROPERTY: onfocus. */
	private ValueExpression onfocus;
	public void setOnfocus(ValueExpression onfocus)
	{
		this.onfocus = onfocus;
	}

	/** PROPERTY: onselect. */
	private ValueExpression onselect;
	public void setOnselect(ValueExpression onselect)
	{
		this.onselect = onselect;
	}

	/** PROPERTY: readonly. */
	private ValueExpression readonly;
	public void setReadonly(ValueExpression readonly)
	{
		this.readonly = readonly;
	}

	/** PROPERTY: size. */
	private ValueExpression size;
	public void setSize(ValueExpression size)
	{
		this.size = size;
	}

     	@Override
	public void release()
	{
		super.release();

		// component properties
		this.converter = null;
		this.converterMessage = null;
		this.immediate = null;
		this.required = null;
		this.requiredMessage = null;
		this.validator = null;
		this.validatorMessage = null;
		this.value = null;
		this.valueChangeListener = null;

		// rendered attributes
		this.accesskey = null;
		this.disabled = null;
		this.dir = null;
		this.lang = null;
		this.onblur = null;
		this.onchange = null;
		this.onclick = null;
		this.ondblclick = null;
		this.onfocus = null;
		this.onkeydown = null;
		this.onkeypress = null;
		this.onkeyup = null;
		//this.label = null;
		this.onmousedown = null;
		this.onmousemove = null;
		this.onmouseout = null;
		this.onmouseover = null;
		this.onmouseup = null;
		this.onselect = null;
		this.readonly = null;
		this.size = null;
		this.style = null;
		this.styleClass = null;
		this.tabindex = null;
		this.title = null;
	}

	@Override
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

		AbstractUIInputComponent input = null;

		try
		{
			input = (AbstractUIInputComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Component " + component.toString() + " not expected type.  Expected: " + AbstractUIInputComponent.class.getName() + ". Perhaps you're missing a tag?", cce);
		}

		if (converter != null)
		{
			if (!converter.isLiteralText())
			{
				input.setValueExpression("converter", converter);
			}
			else
			{
				Converter conv = FacesContext.getCurrentInstance().getApplication().createConverter(converter.getExpressionString());
				input.setConverter(conv);
			}
		}
		if (converterMessage != null)
		{
			input.setValueExpression("converterMessage", converterMessage);
		}
		if (value != null)
		{
			input.setValueExpression("value", value);
		}
		if (dir != null)
		{
			input.setValueExpression("dir", dir);
		}
		if (lang != null)
		{
			input.setValueExpression("lang", lang);
		}
		if (onclick != null)
		{
			input.setValueExpression("onclick", onclick);
		}
		if (ondblclick != null)
		{
			input.setValueExpression("ondblclick", ondblclick);
		}
		if (onkeydown != null)
		{
			input.setValueExpression("onkeydown", onkeydown);
		}
		if (onkeypress != null)
		{
			input.setValueExpression("onkeypress", onkeypress);
		}
		if (onkeyup != null)
		{
			input.setValueExpression("onkeyup", onkeyup);
		}
		if (onmousedown != null)
		{
			input.setValueExpression("onmousedown", onmousedown);
		}
		if (onmousemove != null)
		{
			input.setValueExpression("onmousemove", onmousemove);
		}
		if (onmouseout != null)
		{
			input.setValueExpression("onmouseout", onmouseout);
		}
		if (onmouseover != null)
		{
			input.setValueExpression("onmouseover", onmouseover);
		}
		if (onmouseup != null)
		{
			input.setValueExpression("onmouseup", onmouseup);
		}
		if (style != null)
		{
			input.setValueExpression("style", style);
		}
		if (styleClass != null)
		{
			input.setValueExpression("styleClass", styleClass);
		}
		if (tabindex != null)
		{
			input.setValueExpression("tabindex", tabindex);
		}
		if (title != null)
		{
			input.setValueExpression("title", title);
		}
		if (immediate != null)
		{
			input.setValueExpression("immediate", immediate);
		}
		if (required != null)
		{
			input.setValueExpression("required", required);
		}
		if (requiredMessage != null)
		{
			input.setValueExpression("requiredMessage", requiredMessage);
		}
		if (validator != null)
		{
			input.addValidator(new MethodExpressionValidator(validator));
		}
		if (validatorMessage != null)
		{
			input.setValueExpression("validatorMessage", validatorMessage);
		}
		if (valueChangeListener != null)
		{
			input.addValueChangeListener(new MethodExpressionValueChangeListener(valueChangeListener));
		}
		if (accesskey != null)
		{
			input.setValueExpression("accesskey", accesskey);
		}
		if (disabled != null)
		{
			input.setValueExpression("disabled", disabled);
		}
		//TODO:
		//if (maxlength != null)
		//{
		//	input.setValueExpression("maxlength", maxlength);
		//}
		if (onblur != null)
		{
			input.setValueExpression("onblur", onblur);
		}
		if (onchange != null)
		{
			input.setValueExpression("onchange", onchange);
		}
		if (onfocus != null)
		{
			input.setValueExpression("onfocus", onfocus);
		}
		if (onselect != null)
		{
			input.setValueExpression("onselect", onselect);
		}
		if (readonly != null)
		{
			input.setValueExpression("readonly", readonly);
		}
		if (size != null)
		{
			input.setValueExpression("size", size);
		}
	}

	public String getDebugString()
	{
		return "id: " + this.getId() + " class: " + this.getClass().getName();
	}
}
