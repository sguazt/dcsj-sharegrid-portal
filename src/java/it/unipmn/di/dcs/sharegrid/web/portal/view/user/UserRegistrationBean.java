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

package it.unipmn.di.dcs.sharegrid.web.portal.view.user;

import it.unipmn.di.dcs.common.util.Pair;
import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.model.Conf;
import it.unipmn.di.dcs.sharegrid.web.model.IMailSender;
import it.unipmn.di.dcs.sharegrid.web.model.NotificationSenderFactory;
import it.unipmn.di.dcs.sharegrid.web.model.RegistrationInfo;
import it.unipmn.di.dcs.sharegrid.web.model.User;
//import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.SessionBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.MessageConstants;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;
import it.unipmn.di.dcs.sharegrid.web.service.IAuthenticationService;
import it.unipmn.di.dcs.sharegrid.web.service.ServiceFactory;

import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.faces.component.UIComponent;
//import javax.faces.component.EditableValueHolder;
import javax.faces.context.FacesContext;

/**
 * Page bean for the user registration.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UserRegistrationBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( UserRegistrationBean.class.getName() );

	private RegistrationInfo regInfo = new RegistrationInfo();

	///@{ Lifecycle ////////////////////////////////////////////////////////

	@Override
	public void init()
	{
		super.init();

		SessionBean session = this.getSessionBean();

		if ( session != null && session.isUserLoggedIn() )
		{
			try
			{
				ViewUtil.RedirectPage( "index.html" );
			}
			catch (Exception e)
			{
				// Ignore
			}
		}
	}

	@Override
	public void prerender()
	{
		super.prerender();

		FacesContext context = this.getFacesContext();

		if ( Strings.IsNullOrEmpty( this.getLanguage() ) )
		{
			Locale locale = null;

			if (context != null && context.getViewRoot() != null)
			{
				locale = context.getViewRoot().getLocale();
				if (locale == null)
				{
					locale = Locale.getDefault();
				}
			}
			else
			{
				locale = Locale.getDefault();
			}
			this.setLanguage( locale.getLanguage() );
		}
	}

	///@} Lifecycle ////////////////////////////////////////////////////////

	///@{ Properties ///////////////////////////////////////////////////////

	/** PROPERTY: nickname. */
	public String getNickname() { return this.regInfo.getNickname(); }
	public void setNickname(String value) { this.regInfo.setNickname( value ); }

	/** PROPERTY: email. */
	public String getEmail() { return this.regInfo.getEmail(); }
	public void setEmail(String value) { this.regInfo.setEmail( value ); }

	/** PROPERTY: name. */
	public String getName() { return this.regInfo.getName(); }
	public void setName(String value) { this.regInfo.setName( value ); }

	/** PROPERTY: password. */
	public String getPassword() { return this.regInfo.getPassword(); }
	public void setPassword(String value) { this.regInfo.setPassword( value ); }

	/** PROPERTY: password2. */
	public String getPassword2() { return this.regInfo.getPassword2(); }
	public void setPassword2(String value) { this.regInfo.setPassword2( value ); }

	/** PROPERTY: language. */
	public String getLanguage() { return this.regInfo.getLanguage(); }
	public void setLanguage(String value) { this.regInfo.setLanguage( value ); }

	/** PROPERTY: prettyLanguage. */
	public void setPrettyLanguage(String value) { this.regInfo.setPrettyLanguage(value); }
	public String getPrettyLanguage() { return this.regInfo.getPrettyLanguage(); }

	/** PROPERTY: timeZoneOffset. */
	public void setTimeZoneOffset(int value) { this.regInfo.setTimeZoneOffset(value); }
	public int getTimeZoneOffset() { return this.regInfo.getTimeZoneOffset(); }

	/** PROPERTY: registered. */
	public boolean registered = false;
	public void setRegistered(boolean value) { this.registered = value; }
	public boolean getRegistered() { return this.registered; }

	/** PROPERTY: nicknameMinLen. */
	public int getNicknameMinLen() { return 1; }

	/** PROPERTY: nicknameMaxLen. */
	public int getNicknameMaxLen() { return 50; }

	/** PROPERTY: passwordMinLen. */
	public int getPasswordMinLen() { return 8; }

	/** PROPERTY: passwordMaxLen. */
	public int getPasswordMaxLen() { return 100; }

	/** PROPERTY: nameMinLen. */
	public int getNameMinLen() { return 0; }

	/** PROPERTY: nameMaxLen. */
	public int getNameMaxLen() { return 50; }

	/** PROPERTY: emailMinLen. */
	public int getEmailMinLen() { return 3; }

	/** PROPERTY: emailMaxLen. */
	public int getEmailMaxLen() { return 255; }

	///@} Properties ///////////////////////////////////////////////////////

	///@{ Actions //////////////////////////////////////////////////////////

	/**
	 * Action for registering a user.
	 */
	public String registrationAction()
	{
		Pair<Integer,String> activationInfo = null;
		URL activationUrl= null;

                try
                {
			if ( UserRegistrationBean.Log.isLoggable(Level.FINE) )
			{
				UserRegistrationBean.Log.fine( "Going to register user '" + this.getNickname() + "'." );
			}

			//User user = null;
			//user = UserFacade.instance().registerUser( this.regInfo );
			//if ( user == null )
			activationInfo = ServiceFactory.instance().authenticationService().register( this.regInfo );
			if ( activationInfo == null )
                        {
				if ( UserRegistrationBean.Log.isLoggable(Level.FINE) )
				{
					UserRegistrationBean.Log.fine( "Failed to register the user" );
				}

                                ViewUtil.AddErrorMessage(
					MessageConstants.USER_REGISTRATION_KO,
					this.getNickname()
				);

                        	return "registration-ko";
                        }

			IMailSender mailer = null;
			mailer = NotificationSenderFactory.instance().getMailSender();
			if ( mailer != null )
			{
				String baseUrl = null;
				baseUrl = ViewUtil.GetServletUrl( 
					this.getExternalContext()
				);
				activationUrl = new URL(
					baseUrl
					+ "/activateuser.jspx"
					+ "?uid=" + URLEncoder.encode( activationInfo.getFirst().toString(), "UTF-8" )
					+ "&aid=" + URLEncoder.encode( activationInfo.getSecond(), "UTF-8" )
				);

				SimpleDateFormat dateFormat = new SimpleDateFormat( Conf.instance().getFormatDateTime() );

				mailer.send(
					Conf.instance().getSmtpRegistrationSender(),
					new String[] { this.regInfo.getEmail() },
					Conf.instance().getSmtpRegistrationSubject(),
					MessageFormat.format(
						Conf.instance().getSmtpRegistrationBody(),
						activationUrl.toString(),
						dateFormat.format( new Date() )
					)
				);
			}
                }
                catch (Exception e)
                {
			UserRegistrationBean.Log.severe( "Error while register a user (" + activationInfo + "): " + e.toString() );

			if ( activationInfo == null )
			{
				ViewUtil.AddExceptionMessage(e);
				return "registration-ko";
			}
			else
			{
				if ( activationUrl != null )
				{
					ViewUtil.AddWarnMessage(
						MessageConstants.USER_REGISTRATION_NOTIFICATION_KO,
						e,
						activationUrl.toString()
					);
				}
			}
                }

		this.setRegistered( true );

		ViewUtil.AddInfoMessage(
			MessageConstants.USER_REGISTRATION_OK,
			this.getNickname(),
			this.getEmail()
		);

		return "registration-ok";
	}

	///@} Actions //////////////////////////////////////////////////////////

//	public void emailValidator(FacesContext context, UIComponent comp, Object value)
//	{
//		String email = (String) value;
//		if (email.indexOf("@") == -1)
//		{
//			String compId = comp.getClientId(context);
//			ViewUtil.AddErrorMessage( context, compId, INVALID_EMAIL_MSGID );
//			((EditableValueHolder) comp).setValid(false);
//		}
//	}

}
