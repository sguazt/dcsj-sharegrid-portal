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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Page-Bean for testing the {@c outputFile} tag.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class TestOutputFileBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( TestOutputFileBean.class.getName() );

	public byte[] getPdf()
	{
		Log.info( "[TestOutputFileBean::getPdf>> returning PDF." );
		return Util.GetPdfBytes();
	}

	public byte[] getPng()
	{
		Log.info( "[TestOutputFileBean::getPng>> returning PNG." );
		return Util.GetPngBytes();
	}
}
