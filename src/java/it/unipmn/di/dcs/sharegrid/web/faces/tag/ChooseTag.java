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

//import it.unipmn.di.dcs.sharegrid.web.faces.component.UIChooseComponent;

//import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import javax.servlet.jsp.JspException;

/**
 * Tag for specifying the 'choose-when-otherwise' conditions
 * at runtime.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ChooseTag extends UIComponentELTag
{
//	private Boolean foundTrue = Boolean.FALSE;

	/** A constructor. */
	public ChooseTag()
	{
		super();
	}

	@Override
	public String getComponentType()
	{
		return FacesConstants.ChooseComponentType;
	}

	@Override
	public String getRendererType()
	{
		return FacesConstants.ChooseRendererType;
	}

        @Override
        public int getDoStartValue() throws JspException
        {
		return EVAL_BODY_INCLUDE;
	}

/*
        @Override
        protected void setProperties(UIComponent component)
        {
                super.setProperties(component);

                UIChooseComponent chooseComp = null;
                try
                {
                        chooseComp = (UIChooseComponent) component;
                }
                catch (ClassCastException cce)
                {
                        throw new IllegalStateException("Component " + component.toString() + " not expected type.  Expected: " + UIChooseComponent.class.getName() + ". Perhaps you're missing a tag?", cce);
                }

                chooseComp.setFoundTrue( this.foundTrue );
	}

	@Override
	public void release()
	{
		super.release();

		this.foundTrue = Boolean.FALSE;
	}
*/
}
