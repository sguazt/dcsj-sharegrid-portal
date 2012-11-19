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

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Page-Bean for testing the {@code rich:dropDownMenu} tag.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class TestRichDropDownMenuBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( TestRichDropDownMenuBean.class.getName() );
	private static final String MYCHOICE_KEY = "mychoice";

	public TestRichDropDownMenuBean()
	{
//		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
//		if (ctx.getSessionMap().containsKey(MYCHOICE_KEY))
//		{
//			this.setMyChoice((String) ctx.getSessionMap().get(MYCHOICE_KEY));
//		}
//		else
//		{
//			this.setMyChoice("it");
//		}
	}

	/** PROPERTY: myChoice. */
//	private String myChoice = null;
//	public void setMyChoice(String value) { this.myChoice = value; }
	public String getMyChoice()
	{
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		if (ctx.getSessionMap().containsKey(MYCHOICE_KEY))
		{
			Log.info("TestRichDropDownMenuBean::getMyChoice>> Found previously stored choice: " + (String) ctx.getSessionMap().get(MYCHOICE_KEY));
			return (String) ctx.getSessionMap().get(MYCHOICE_KEY);
		}

		Log.info("TestRichDropDownMenuBean::getMyChoice>> No previously stored choice found.");
		return "it";
	}

	/** PROPERTY: pairList. */
	private List<Pair<String,String>> pairList = null;
	protected void setPairList(List<Pair<String,String>> value) { this.pairList = value; }
	public List<Pair<String,String>> getPairList()
	{
		if (this.pairList == null)
		{
			this.pairList = new ArrayList<Pair<String,String>>();
			this.pairList.add( new Pair<String,String>( "de", "Deutsch") );
			this.pairList.add( new Pair<String,String>( "en", "English" ) );
			this.pairList.add( new Pair<String,String>( "es", "Espaniol") );
			this.pairList.add( new Pair<String,String>( "fr", "Francaise" ) );
			this.pairList.add( new Pair<String,String>( "it", "Italiano") );

			// Retrieve the current choice
			String curChoice = null;
			if (this.getCurrentChoice() == null || this.getCurrentChoice().length() == 0)
			{
				// Actually, in the first case
				curChoice = this.getMyChoice();
			}
			else
			{
				curChoice = this.getCurrentChoice();
			}

			// Remove current choice from the choice list
			for (int i = 0; i < this.pairList.size(); i++)
			{
				if ( this.pairList.get(i).getFirst().equals( curChoice ) )
				{
					this.pairList.remove(i);
					break;
				}
			}
		}

		Log.info( "TestRichDropDownMenuBean::getPairList>> returning list of pairs" );

		return this.pairList;
	}
//	@SuppressWarnings("unchecked")
//	public Pair<String,String>[] getPairArray()
//	{
//		if (this.pairList == null)
//		{
//			this.pairList = new ArrayList<Pair<String,String>>();
//			this.pairList.add( new Pair<String,String>( "de", "Deutsch") );
//			this.pairList.add( new Pair<String,String>( "en", "English" ) );
//			this.pairList.add( new Pair<String,String>( "es", "Espaniol") );
//			this.pairList.add( new Pair<String,String>( "fr", "Francaise" ) );
//			this.pairList.add( new Pair<String,String>( "it", "Italiano") );
//		}
//
//		Log.info( "TestRichDropDownMenuBean::getPairArray>> returning array of pairs" );
//
//		return (Pair<String,String>[]) this.pairList.toArray();
//	}

	/** PROPERTY: curChoice. */
	private String curChoice;
	public String getCurrentChoice() { return this.curChoice; }
	public void setCurrentChoice(String value) { this.curChoice = value; }

	public String choiceChangeAction()
	{
		Log.info("TestRichDropDownMenuBean::choiceChangeAction>> Changing menu item: " + this.getCurrentChoice());

		Log.info("TestRichDropDownMenuBean::choiceChangeAction>> Old choice: " + this.getMyChoice());

		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		ctx.getSessionMap().put(MYCHOICE_KEY, this.getCurrentChoice());

		Log.info("TestRichDropDownMenuBean::choiceChangeAction>> New choice: " + this.getCurrentChoice());

		this.pairList = null;

		return "ok";
	}

//	public String doChooseAction()
//	{
//		Log.info("TestRichDropDownMenuBean::doChooseAction>> Old choice: " + this.getMyChoice() + " - New choice '" + this.getCurrentChoice());
//
//		this.setMyChoice(this.getCurrentChoice());
//
//		return "ok";
//	}

//	private static List<Integer> IntegerList = null;
//	static
//	{
//			IntegerList = new ArrayList<Integer>();
//			IntegerList.add( new Integer(1) );
//			IntegerList.add( new Integer(2) );
//			IntegerList.add( new Integer(3) );
//			IntegerList.add( new Integer(4) );
//	}
//	public List<Integer> getIntegerList()
//	{
//			Log.info( "TestForEachBean::getIntegerList>> returning list of integers" );
//
//			return IntegerList;
//	}
}
