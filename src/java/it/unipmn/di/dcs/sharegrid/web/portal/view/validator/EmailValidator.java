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

package it.unipmn.di.dcs.sharegrid.web.portal.view.validator;

import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Email validator class.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class EmailValidator implements Validator
{
	private final static String INVALID_EMAIL_MSGID = EmailValidator.class.getName() + ".INVALID_EMAIL";
	//public final static String INVALID_EMAIL_MSGID = "it.unipmn.di.dcs.sharegrid.web.portal.view.validator.EmailValidator.INVALID_EMAIL";

	public void validate(FacesContext context, UIComponent comp, Object value)
	{
		String email = (String) value;
		if (email.indexOf("@") == -1)
		{
			String compId = comp.getClientId(context);
			ViewUtil.AddErrorMessage( context, compId, INVALID_EMAIL_MSGID );
			((EditableValueHolder) comp).setValid(false);
		}
	}
}
