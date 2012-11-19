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

import java.util.Date;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Page-Bean for testing the {@code inputDate} tag.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class TestInputDateBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( TestInputDateBean.class.getName() );

	/** PROPERTY: date. */
	private Date date;
	public Date getDate()
	{
		Log.info( "[TestInputDateBean::getDate>> returning previously stored date: " + this.date );
		return this.date;
	}
	public void setDate(Date value)
	{
		Log.info( "[TestInputDateBean::setDate>> setting new date: " + value );
		this.date = value;
	}
}
