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

package it.unipmn.di.dcs.sharegrid.web.faces.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Validator for the credit card number input component.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class CreditCardValidator implements Validator
{
	/** A constructor. */
	public CreditCardValidator()
	{
		// empty
	}

	//@{ Validator implementation

	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException
	{
		if (null != value)
		{
			if (!(value instanceof String))
			{
				throw new IllegalArgumentException("The value must be a String");
			}

			String ccNum = (String) value;

			// do some asserts that value matches a regex.
			Pattern withDashesPattern = Pattern.compile("\\d\\d\\d\\d-\\d\\d\\d\\d-\\d\\d\\d\\d-\\d\\d\\d\\d");
			Pattern noDashesPattern = Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d");
			Matcher withDashesMatcher = withDashesPattern.matcher(ccNum);
			Matcher noDashesMatcher = noDashesPattern.matcher(ccNum);

			if (!withDashesMatcher.matches() && !noDashesMatcher.matches())
			{
				throw new ValidatorException(
					new FacesMessage(
						"The Credit Card Number must "
						+ "have 16 digits with or "
						+ "without dashes every "
						+ "four digits"
					)
				);
			}
		}
	}

	//@} Validator implementation
}
