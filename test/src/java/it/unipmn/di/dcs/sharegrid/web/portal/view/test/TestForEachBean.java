/*
 * Copyright (C) 2008  Distributed Computing System (DCS) Group, Computer
 * Science Department - University of Piemonte Orientale, Alessandria (Italy).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.unipmn.di.dcs.sharegrid.web.portal.view.test;

import it.unipmn.di.dcs.common.util.Pair;

import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Page-Bean for testing the {@code forEach} tag.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class TestForEachBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( TestForEachBean.class.getName() );

	private static String[] StringArray = new String[] { "Blues", "Folk", "Hard-Rock", "Jazz", "Rock", "Salsa" };
	public String[] getStringArray()
	{
		Log.info( "TestForEachBean::getStringArray>> returning array of strings" );

		return TestForEachBean.StringArray;
	}

	private static List<Integer> IntegerList = null;
	static
	{
		IntegerList = new ArrayList<Integer>();
		IntegerList.add( new Integer(1) );
		IntegerList.add( new Integer(2) );
		IntegerList.add( new Integer(3) );
		IntegerList.add( new Integer(4) );
	}
	public List<Integer> getIntegerList()
	{
		Log.info( "TestForEachBean::getIntegerList>> returning list of integers" );

		return TestForEachBean.IntegerList;
	}

	private static List<Pair<String,String>> PairList = null;
	static
	{
		PairList = new ArrayList<Pair<String,String>>();
		PairList.add( new Pair<String,String>( "de", "Deutsch") );
		PairList.add( new Pair<String,String>( "en", "English" ) );
		PairList.add( new Pair<String,String>( "es", "Espaniol") );
		PairList.add( new Pair<String,String>( "fr", "Francaise" ) );
		PairList.add( new Pair<String,String>( "it", "Italiano") );
	}
	public List<Pair<String,String>> getPairList()
	{
		Log.info( "TestForEachBean::getPairList>> returning list of pairs" );

		return TestForEachBean.PairList;
	}
}
