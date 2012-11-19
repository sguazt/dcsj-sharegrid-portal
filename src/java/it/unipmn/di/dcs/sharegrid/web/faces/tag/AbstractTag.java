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

import java.text.MessageFormat;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.ActionSource2;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIGraphic;
import javax.faces.component.UIParameter;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.event.MethodExpressionActionListener;
import javax.faces.event.MethodExpressionValueChangeListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.MethodExpressionValidator;
import javax.faces.webapp.UIComponentELTag;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractTag extends UIComponentELTag
{
	protected static final String NO_ACTION_SOURCE_ERROR = "Component {0} is no ActionSource.";
	protected static final String NO_ACTION_SOURCE2_ERROR = "Component {0} is no ActionSource2.";
	protected static final String NO_VALUE_HOLDER_ERROR = "Component {0} is no ValueHolder, cannot set value.";
	protected static final String NO_EDITABLE_VALUE_HOLDER_ERROR = "Component {0} is no EditableValueHolder.";

	protected ExpressionFactory getExpressionFactory()
	{
		return this.getFacesContext().getApplication().getExpressionFactory();
	}

	protected void setProperty(UIComponent component, String propName, ValueExpression valueExpression)
	{
		if (valueExpression != null)
		{
			if (valueExpression.isLiteralText())
			{
				component.getAttributes().put(propName,valueExpression.getValue(getELContext()));
			}
			else
			{
				component.setValueExpression(propName, valueExpression);
			}
		}
	}

	protected void setProperty(UIComponent component, Class<?> type, String propName, String value)
	{
		if (value != null)
		{
			ValueExpression valueExpression = getExpressionFactory().createValueExpression(getELContext(), value, type);
			setProperty(component, propName, valueExpression);
		}
	}

	protected void setIntegerProperty(UIComponent component, String propName,
			ValueExpression value) {
		setProperty(component, propName, value);
	}

	protected void setLongProperty(UIComponent component, String propName,
			ValueExpression value) {
		setProperty(component, propName, value);
	}

	protected void setFloatProperty(UIComponent component, String propName,
			ValueExpression value) {
		setProperty(component, propName, value);
	}

	protected void setDoubleProperty(UIComponent component, String propName,
			ValueExpression value) {
		setProperty(component, propName, value);
	}

	protected void setStringProperty(UIComponent component, String propName,
			ValueExpression value) {
		setProperty(component, propName, value);
	}

	protected void setBooleanProperty(UIComponent component, String propName,
			ValueExpression value) {
		setProperty(component, propName, value);
	}

	protected void setIntegerProperty(UIComponent component, String propName,
			String value) {
		setProperty(component, Integer.class, propName, value);
	}

	protected void setLongProperty(UIComponent component, String propName,
			String value) {
		setProperty(component, Long.class, propName, value);
	}

	protected void setFloatProperty(UIComponent component, String propName,
			String value) {
		setProperty(component, Float.class, propName, value);
	}

	protected void setDoubleProperty(UIComponent component, String propName,
			String value) {
		setProperty(component, Double.class, propName, value);
	}

	protected void setStringProperty(UIComponent component, String propName,
			String value) {
		setProperty(component, String.class, propName, value);
	}

	protected void setBooleanProperty(UIComponent component, String propName,
			String value) {
		setProperty(component, Boolean.class, propName, value);
	}
	protected void setValueProperty(UIComponent component, String value) {
		if (value != null) {
			ValueExpression expression = getExpressionFactory()
					.createValueExpression(getELContext(), value, Object.class);

			setValueProperty(component, expression);
		}
	}
	protected void setValueProperty(UIComponent component, ValueExpression expression) {
		if (expression != null) {
			String value = expression.getExpressionString();
			if (!expression.isLiteralText()) {
				component.setValueExpression("value", expression);
			} else if (component instanceof UICommand) {
				((UICommand) component).setValue(value);
			} else if (component instanceof UIParameter) {
				((UIParameter) component).setValue(value);
			} else if (component instanceof UISelectBoolean) {
				((UISelectBoolean) component).setValue(Boolean.valueOf(value));
			} else if (component instanceof UIGraphic) {
				((UIGraphic) component).setValue(value);
			}
			// Since many input components are ValueHolders the special
			// components must come first, ValueHolder is the last resort.
			else if (component instanceof ValueHolder) {
				((ValueHolder) component).setValue(value);
			} else {
				component.getAttributes().put("value", value);
			}
		}
	}

	public boolean isValueReference(String s) {
		return !getExpressionFactory().createValueExpression(s, Object.class).isLiteralText();
	}

        protected void setActionListenerProperty(UIComponent component, MethodExpression actionListener) {
                if (actionListener != null) {
                        if (component instanceof ActionSource2) {
                                ActionSource2 actionSource2 = (ActionSource2) component;
                                actionSource2.addActionListener(new MethodExpressionActionListener(actionListener));
                        } else {
                                throw new IllegalArgumentException(FormatMessage(NO_ACTION_SOURCE2_ERROR, component.getClientId(getFacesContext())));
                        }
                }
        }

        protected void setActionProperty(UIComponent component, MethodExpression action) {
                if (action != null) {
                        if (component instanceof ActionSource2) {
                                ActionSource2 actionSource2 = (ActionSource2) component;
                                actionSource2.setActionExpression(action);
                        } else {
                                throw new IllegalArgumentException(FormatMessage(NO_ACTION_SOURCE2_ERROR, component.getClientId(getFacesContext())));
                        }
                }
        }

        protected void setConverterProperty(UIComponent component, ValueExpression converter) {
        if (converter != null) {
                        if (component instanceof ValueHolder) {
                                ValueHolder output = (ValueHolder) component;
                            if (!converter.isLiteralText()) {
                                component.setValueExpression("converter", converter);
                            } else {
                                Converter conv = FacesContext.getCurrentInstance().getApplication().createConverter(converter.getExpressionString());
                                output.setConverter(conv);
                            }
                        } else {
                                 throw new IllegalArgumentException(FormatMessage(NO_VALUE_HOLDER_ERROR, component.getClass().getName()));
                        }
        }
        }

        protected void setValidatorProperty(UIComponent component, MethodExpression validator) {

                if (validator != null) {
                        if (component instanceof EditableValueHolder) {
                                EditableValueHolder input = (EditableValueHolder) component;
                                input.addValidator(new MethodExpressionValidator(validator));
                        } else {
                    throw new IllegalArgumentException(FormatMessage(NO_EDITABLE_VALUE_HOLDER_ERROR, component.getId()));
                        }
                }
        }

        protected void setValueChangeListenerProperty(UIComponent component, MethodExpression valueChangeListener) {
                if (valueChangeListener != null) {
                        if (component instanceof EditableValueHolder) {
                                EditableValueHolder input = (EditableValueHolder) component;
                                input.addValueChangeListener(new MethodExpressionValueChangeListener(valueChangeListener));
                        } else {
                    throw new IllegalArgumentException(FormatMessage(NO_EDITABLE_VALUE_HOLDER_ERROR, component.getId()));
                        }
                }
        }

	protected void setActionProperty(UIComponent component, String action) {
		if (action != null) {
			MethodExpression expression =
				getExpressionFactory().createMethodExpression(getELContext(), action, String.class, new Class[] {});
			setActionProperty(component, expression);
		}
	}

	protected void setActionListenerProperty(UIComponent component, String actionListener){
		if (actionListener != null) {
			MethodExpression expression =
				getExpressionFactory().createMethodExpression(getELContext(), actionListener, String.class, new Class[] {ActionEvent.class});
			setActionListenerProperty(component, expression);
		}
	}

	protected void setValueChangedListenerProperty(UIComponent component, String valueChangedListener) {
		if (valueChangedListener != null) {
			MethodExpression expression =
				getExpressionFactory().createMethodExpression(getELContext(), valueChangedListener, String.class, new Class[] {ValueChangeEvent.class});
			setValueChangeListenerProperty(component, expression);
		}
	}

	protected String FormatMessage(String msg, Object...params)
	{
		return MessageFormat.format(msg, params);
	}
}
