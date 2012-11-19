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

package it.unipmn.di.dcs.sharegrid.web.portal.view.test;

import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Page-Bean for testing the {@c inputRadio} tag.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class TestInputRadioBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( TestInputRadioBean.class.getName() );

	private boolean radio1Checked;
	public void setRadio1Checked(Boolean value)
	{
		this.radio1Checked = value;
	}
	public Boolean getRadio1Checked()
	{
		return this.radio1Checked;
	}
	public void setIsRadio1Checked(Boolean value)
	{
		this.radio1Checked = value;
	}
	public Boolean isRadio1Checked()
	{
		return this.radio1Checked;
	}

	private boolean radio2Checked;
	public void setIsRadio2Checked(Boolean value)
	{
		this.radio2Checked = value;
	}
	public Boolean isRadio2Checked()
	{
		return this.radio2Checked;
	}

	private boolean radio3Checked;
	public void setIsRadio3Checked(Boolean value)
	{
		this.radio3Checked = value;
	}
	public Boolean isRadio3Checked()
	{
		return this.radio3Checked;
	}

	public String doSubmitAction()
	{
		boolean allOk;
		FacesMessage msg = null;

		msg = new FacesMessage( "RADIO1 (" + this.isRadio1Checked() + ") -RADIO1 (" + this.isRadio2Checked() + ") -  RADIO3 (" + this.isRadio3Checked() + ")");
		this.getFacesContext().addMessage( null, msg );

		allOk = true;

		return allOk ? "submit-ok" : "submit-ko";
	}
}
