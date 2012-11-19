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

package it.unipmn.di.dcs.sharegrid.web.portal.view;

import it.unipmn.di.dcs.common.util.Pair;
import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;

import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;

//import javax.el.ELContext;
//import javax.el.ExpressionFactory;
//import javax.el.MethodExpression;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
//import javax.faces.event.MethodExpressionActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.richfaces.component.html.HtmlDropDownMenu;
//import org.richfaces.component.html.HtmlMenuItem;   

/**
 * Bean class for header page fragment.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class HeaderBean extends AbstractFragmentBean
{
	private static final transient Logger Log = Logger.getLogger( HeaderBean.class.getName() );

	///@{ Properties ///////////////////////////////////////////////////////

//	/**
//	 * PROPERTY: supportedLanguageMenu.
//	 */
//	private HtmlDropDownMenu langsMenu = new HtmlDropDownMenu();
//	public HtmlDropDownMenu getSupportedLanguagesMenu()
//	{
//		if ( this.langsMenu == null || this.langsMenu.getChildren().size() == 0 )
//		{
//			this.langsMenu = this.createLanguageMenu();
//		}
//		return this.langsMenu;
//	}
//	public void setSupportedLanguagesMenu(HtmlDropDownMenu value)
//	{
//		this.langsMenu = value;
//		this.langsMenu = this.createLanguageMenu();
//	}

	/**
	 * PROPERTY: languages.
	 *
	 * A wrapper to the <code>supportedLanguages</code> property of
	 * <code>ApplicationBean</code> that returns all supported languages
	 * but the user's one.
	 */
	public List<Pair<String,String>> languages;
	public List<Pair<String,String>> getLanguages()
	{
//System.err.println("getLanguages>> LANGS: " + this.languages);//XXX
		if ( this.languages == null )
		{
			//List<Pair<String,String>> langs = null;
			//this.languages = this.getApplicationBean().getSupportedLanguages();
			////java.util.Collections.copy( this.languages, this.getApplicationBean().getSupportedLanguages() );
			this.languages = new ArrayList<Pair<String,String>>();
			for (Pair<String,String> lang : this.getApplicationBean().getSupportedLanguages())
			{
//System.err.println("getLanguages>> GET LANG: " + lang.getFirst() + " - " + lang.getSecond());//XXX
				this.languages.add( new Pair<String,String>( lang.getFirst(), lang.getSecond() ) );
			}

			String curLang = null;
//System.err.println("getLanguages>> CUR LANG: " + this.getCurrentLanguage());//XXX
			if ( Strings.IsNullOrEmpty(this.getCurrentLanguage()) )
			{
				if ( this.getSessionBean() != null && !this.getSessionBean().isInvalid() )
				{
					curLang = this.getSessionBean().getUser().getLanguage();
//System.err.println("getLanguages>> CUR LANG FROM SESSION: " + curLang);//XXX
				}
				else
				{
					curLang = this.getApplicationBean().getDefaultLanguage().getFirst();
//System.err.println("getLanguages>> CUR LANG FROM FIRST: " + curLang);//XXX
				}
			}
			else
			{
				curLang = this.getCurrentLanguage();
//System.err.println("getLanguages>> CUR LANG FROM CURRENT: " + curLang);//XXX
			}

			for (int i = 0; i < this.languages.size(); i++)
			{
				if ( this.languages.get(i).getFirst().equals( curLang ) )
				{
					this.languages.remove(i);
					break;
				}
			}
		}

		return this.languages;
	}
	protected void setLanguages(List<Pair<String,String>> value) { this.languages = value; }

	/**
	 * PROPERTY: currentLanguage.
	 */
	private String curLanguage;
	public String getCurrentLanguage() { return this.curLanguage; }
	public void setCurrentLanguage(String value) { this.curLanguage = value; }

	///@} Properties ///////////////////////////////////////////////////////

	///@{ Actions //////////////////////////////////////////////////////////

//	public String loginAction()
//	{
//		return "login";
//	}

	/**
	 * Action for logging-out the current logged user.
	 */
	public String logoutAction()
	{
		try
		{
			// Make sure the session really exists before invalidating
			if ( this.getSessionBean() != null )
			{
				this.getSessionBean().invalidate();
			}
		}
		catch (Exception e)
		{
			HeaderBean.Log.log( Level.SEVERE, "Logout failure", e );

			ViewUtil.AddExceptionMessage(e);

			return "logout-ko";
		}

		return "logout-ok";
	}

//	public String helpAction()
//	{
//		return "help";
//	}

	/**
	 * Action for changing the user language.
	 */
	public String languageChangeAction()
	{
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		viewRoot.setLocale(new Locale( this.getCurrentLanguage() ));
		this.setLanguages( null );
		this.getSessionBean().getUser().setLanguage( this.getCurrentLanguage() );
		//this.getSessionBean().getUser().setChanged( true );
		try
		{
			UserFacade.instance().setUser( this.getSessionBean().getUser() );
		}
		catch (Exception e)
		{
			HeaderBean.Log.log( Level.SEVERE, "Error while changing the user language.", e );

			return "lang-change-ko";
		}

		return "lang-change-ok";
	}

	///@} Actions //////////////////////////////////////////////////////////

	///@{ Action Listeners //////////////////////////////////////////////////////////

//	public void languageChangeHandler(ActionEvent event)
//	{
//		FacesContext ctx = FacesContext.getCurrentInstance();
//
//		String id = event.getComponent().getId();
//		String containerId = event.getComponent().getContainerClientId( ctx );
//		String clientId = event.getComponent().getClientId( ctx );
//
//		this.setCurrentLanguage( id );
//	}

	///@} Action Listeners //////////////////////////////////////////////////////////

//	protected HtmlDropDownMenu createLanguageMenu()
//	{
//		this.langsMenu = new HtmlDropDownMenu();
//
//		List<Pair<String,String>> langs = null;
//		langs = this.getApplicationBean().getSupportedLanguages();
//
//		String curLang = null;
//		if ( Strings.IsNullOrEmpty(this.getCurrentLanguage()) )
//		{
//			curLang = this.getSessionBean().getUserLogin().getLanguage();
//		}
//		else
//		{
//			curLang = this.getCurrentLanguage();
//		}
//
//		for (Pair<String,String> lang : langs)
//		{
//			if (
//				!lang.getFirst().equals( curLang ) )
//			{
//				HtmlMenuItem item = new HtmlMenuItem();
//				item.setId( lang.getFirst() );
//				item.setValue( lang.getSecond() );
//				item.setIcon( "/resources/theme/current/img/flags/" + lang.getFirst() + ".png");
//				//MethodExpression methodExpression = FacesContext.getCurrentInstance().getApplication().getExpressionFactory().createMethodExpression(FacesContext.getCurrentInstance().getELContext(), "#{headerBean.languageChangeAction}", null, new Class<?>[0]);
//
//				ExpressionFactory expFactory = FacesContext.getCurrentInstance().getApplication().getExpressionFactory();
//				ELContext elContext = FacesContext.getCurrentInstance().getELContext();
//				MethodExpression me = expFactory.createMethodExpression( elContext, "#{headerBean.languageChangeAction}", String.class, new Class[]{});
//				item.setActionExpression(me);
//				item.addActionListener(
//					new javax.faces.event.MethodExpressionActionListener(
//						expFactory.createMethodExpression(
//							elContext,
//							"#{headerBean.languageChangeHandler}",
//							null,
//							new Class[]{ ActionEvent.class }
//						)
//					)
//				);
//				//item.setReRender("header-view:userform:langsMenu");
//				//item.setAjaxSingle( true );
//				item.setStyle( "color: #000 !important;" );
//
//				this.langsMenu.getChildren().add(item);
//			}
//		}
//		this.langsMenu.setRendered(true);
//		return this.langsMenu;
//	}
}
