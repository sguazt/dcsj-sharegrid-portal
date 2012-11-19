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
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class TestRichDataScrollBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( TestRichDataScrollBean.class.getName() );

	@Override
	public void init()
	{
		this.items = new ArrayList<Pair<String,String>>();
		this.items.add( new Pair<String,String>("Item1.Name", "Item1.Value") );
		this.items.add( new Pair<String,String>("Item2.Name", "Item2.Value") );
		this.items.add( new Pair<String,String>("Item3.Name", "Item3.Value") );
		this.items.add( new Pair<String,String>("Item4.Name", "Item4.Value") );
		this.items.add( new Pair<String,String>("Item5.Name", "Item5.Value") );
		this.items.add( new Pair<String,String>("Item6.Name", "Item6.Value") );
		this.items.add( new Pair<String,String>("Item7.Name", "Item7.Value") );
		this.items.add( new Pair<String,String>("Item8.Name", "Item8.Value") );
		this.items.add( new Pair<String,String>("Item9.Name", "Item9.Value") );
		this.items.add( new Pair<String,String>("Item10.Name", "Item10.Value") );
		this.items.add( new Pair<String,String>("Item11.Name", "Item11.Value") );
	}

	private List<Pair<String,String>> items;
	public List<Pair<String,String>> getItems()
	{
		Log.info( "TestIfBean::getTrueValue>> returning the list of all items" );

		return this.items;
	}
}
