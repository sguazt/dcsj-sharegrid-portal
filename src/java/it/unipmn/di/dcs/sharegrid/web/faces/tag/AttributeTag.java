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

import it.unipmn.di.dcs.common.annotation.Experimental;
import it.unipmn.di.dcs.common.annotation.FIXME;

import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentELTag;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

//import com.sun.faces.util.MessageUtils;


/**
 * <p>Tag implementation that adds an attribute with a specified name
 * and String value to the component whose tag it is nested inside,
 * if the component does not already contain an attribute with the
 * same name.  This tag creates no output to the page currently
 * being created.</p>
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@Experimental
@FIXME("The evaluation of Body Content does'n work!")
public class AttributeTag extends BodyTagSupport
{
	private static final transient Logger Log = Logger.getLogger( AttributeTag.class.getName() );

    /**
     * <p>The name of the attribute to be created, if not already present.
     */
    private ValueExpression name = null;


    /**
     * <p>Set the attribute name.</p>
     *
     * @param name The new attribute name
     */
    public void setName(ValueExpression name) {

        this.name = name;

    }


    /**
     * <p>The value to be associated with this attribute, if it is created.</p>
     */
    private ValueExpression value = null;



    /**
     * <p>Set the attribute value.</p>
     *
     * @param value The new attribute value
     */
    public void setValue(ValueExpression value) {

        this.value = value;

    }


    // -------------------------------------------------------- Methods from Tag


    /**
     * <p>Register the specified attribute name and value with the
     * {@link UIComponent} instance associated with our most immediately
     * surrounding {@link UIComponentClassicTagBase} instance, if this
     * {@link UIComponent} does not already have a value for the
     * specified attribute name.</p>
     *
     * @exception JspException if a JSP error occurs
     */
    public int doStartTag() throws JspException {
        return (EVAL_BODY_INCLUDE);

    }

    public int doEndTag() throws JspException {

        // Locate our parent UIComponentTagBase
        UIComponentClassicTagBase tag =
            UIComponentELTag.getParentUIComponentClassicTagBase(pageContext);
        if (tag == null) {
//        	String message = MessageUtils.getExceptionMessageString
 //       	(MessageUtils.NOT_NESTED_IN_UICOMPONENT_TAG_ERROR_MESSAGE_ID);
        	throw new JspException("com.sun.faces.NOT_NESTED_IN_UICOMPONENT_TAG_ERRO");
        }
        
        // Add this attribute if it is not already defined
        UIComponent component = tag.getComponentInstance();
        if (component == null) {
//        	String message = MessageUtils.getExceptionMessageString
//        	(MessageUtils.NO_COMPONENT_ASSOCIATED_WITH_UICOMPONENT_TAG_MESSAGE_ID);
        	throw new JspException("com.sun.faces.NO_COMPONENT_ASSOCIATED_WITH_UICOMPONENT_TAG");
        }

        FacesContext context = FacesContext.getCurrentInstance();
        ELContext elContext = context.getELContext();

        String nameVal = null;
        Object valueVal = null;
	boolean isLiteral = false;
	int result = EVAL_PAGE;

        if (name != null) {
            nameVal = (String) name.getValue(elContext);
        }

        if (value != null) {
	    if (isLiteral = value.isLiteralText()) {
		valueVal = value.getValue(elContext);
	    }
        }
	else
	{
		BodyContent bodyContent = null;
		String content = null;
		String trimContent = null;

		if (
			(bodyContent = this.getBodyContent()) == null
			|| (content = bodyContent.getString()) == null
			||  (trimContent = content.trim()).length() == 0
			|| (
				trimContent.startsWith("<!--")
				&& trimContent.endsWith("-->")
			)
		) {
			return result;
		}

		valueVal = trimContent;;
		bodyContent.clearBody();
		isLiteral = true;
	}
	
        if (component.getAttributes().get(nameVal) == null) {
	    if (isLiteral) {
		component.getAttributes().put(nameVal, valueVal);
	    }
	    else {
		component.setValueExpression(nameVal, value);
	    }
        }
	this.release();
	return result;
    }


    /**
     * <p>Release references to any acquired resources.
     */
    public void release() {

        this.name = null;
        this.value = null;

    } // END release

}
