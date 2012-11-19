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

import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import java.io.IOException;

import javax.el.ValueExpression;
//import javax.faces.*;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
//import javax.faces.context.*;
//import javax.faces.convert.*;
//import javax.faces.el.*;
//import javax.faces.event.*;
//import javax.faces.validator.*;
import javax.faces.webapp.UIComponentELTag;
import javax.servlet.jsp.JspException;

/**
 * Tag similar to the JSF's 'h:panelGroup' but in addition
 * adds the column span attribute.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class PanelGroupTag extends UIComponentELTag
{
    // PROPERTY: layout
    private ValueExpression layout;
    public void setLayout(ValueExpression layout) {
        this.layout = layout;
    }

    // PROPERTY: style
    private ValueExpression style;
    public void setStyle(ValueExpression style) {
        this.style = style;
    }

    // PROPERTY: styleClass
    private ValueExpression styleClass;
    public void setStyleClass(ValueExpression styleClass) {
        this.styleClass = styleClass;
    }

    // PROPERTY: colSpan
    private ValueExpression colSpan;
    public void setColSpan(ValueExpression colSpan) {
        this.colSpan = colSpan;
    }

    @Override
    public String getRendererType() {
        return FacesConstants.HtmlPanelGroupRendererType;
    }

    @Override
    public String getComponentType() {
        return FacesConstants.PanelGroupComponentType;
    }

    @Override
    protected void setProperties(UIComponent component)
    {
        super.setProperties(component);
        UIPanel panel = null;
        try {
            panel = (UIPanel) component;
        } catch (ClassCastException cce) {
            throw new IllegalStateException("Component " + component.toString() + " not expected type.  Expected: javax.faces.component.UIPanel.  Perhaps you're missing a tag?");
        }

        if (this.layout != null) {
            panel.setValueExpression("layout", this.layout);
        }
        if (this.style != null) {
            panel.setValueExpression("style", this.style);
        }
        if (this.styleClass != null) {
            panel.setValueExpression("styleClass", this.styleClass);
        }
        if (this.colSpan != null) {
            panel.setValueExpression("colSpan", this.colSpan);
        }
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            return super.doStartTag();
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            throw new JspException(root);
        }
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            return super.doEndTag();
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            throw new JspException(root);
        }
    }

    @Override
    public void release() {
        super.release();

        // component properties

        // rendered attributes
        this.layout = null;
        this.style = null;
        this.styleClass = null;
    }

    public String getDebugString() {
        return "id: " + this.getId() + " class: " + this.getClass().getName();
    }

}
