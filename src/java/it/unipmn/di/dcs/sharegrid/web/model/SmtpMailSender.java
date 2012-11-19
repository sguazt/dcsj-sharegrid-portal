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

package it.unipmn.di.dcs.sharegrid.web.model;

//import com.sun.mail.smtp.SMTPTransport; // located in mail.jar

import it.unipmn.di.dcs.common.util.Strings;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

/**
 * SMTP email sender.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class SmtpMailSender implements IMailSender
{
	private static final String Mailer = "ShareGrid SMTP mailer";
	private static final String Protocol = "smtp";
	private String host;
	private int port = 25;
	private String user;
	private String password;
	private boolean useTLS = false;
	private boolean debugEnabled = false;

	/** A constructor. */
	public SmtpMailSender(String host)
	{
		this.host = host;
	}

	/** A constructor. */
	public SmtpMailSender(String host, int port)
	{
		this.host = host;
		this.port = port;
	}

	/** A constructor. */
	public SmtpMailSender(String host, String user, String password)
	{
		this.host = host;
		this.user = user;
		this.password = password;
	}

	/** A constructor. */
	public SmtpMailSender(String host, int port, String user, String password)
	{
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	/** Set the <em>use-tls</em> flag. */
	public void setUseTLSFlag(boolean value)
	{
		this.useTLS = value;
	}

	/** Tells if debugging messages are enabled. */
	protected boolean isDebugEnabled()
	{
		return this.debugEnabled;
	}

	/** Controls the enabling of debugging messages. */
	protected void setDebugEnabled(boolean value)
	{
		this.debugEnabled = value;
	}

	//@{ IMailSender implementation

	public void send(String from, String[] to, String subject, String body)
	{
		this.send( from, to, subject, body, null, null );
	}

	public void send(String from, String[] to, String subject, String body, String[] cc, String[] bcc)
	{
		// preconditions
		assert(
			!Strings.IsNullOrEmpty(from)
			&& to != null && to.length > 0 && !Strings.IsNullOrEmpty(to[0]) // at least one recipient TO
		);

		if ( Strings.IsNullOrEmpty(subject) )
		{
			subject = "(No Subject)";
		}

		try
		{
			//Get system properties
			Properties props = System.getProperties();

			//Specify the desired SMTP server
			//props.put( "mail.smtp.host", this.host + ":" + Integer.toString(this.port) );
			props.put( "mail.transport.protocol", SmtpMailSender.Protocol );
			props.put( "mail.smtp.host", this.host );
			props.put( "mail.smtp.port", this.port );
			if ( !Strings.IsNullOrEmpty(this.user) )
			{
				props.put( "mail.smtp.auth", "true" );
			}
			else
			{
				props.put( "mail.smtp.auth", "false" );
			}
			if ( this.useTLS )
			{
				props.put( "mail.smtp.starttls.enable", "true" );
			}
			else
			{
				props.put( "mail.smtp.starttls.enable", "false" );
			}
			if ( this.isDebugEnabled() )
			{
				props.put("mail.smtp.debug", "true");
			}

			// create a new Session object
			Session session = null;
			if ( Strings.IsNullOrEmpty(this.user) )
			{
				session = Session.getInstance(props,null);
			}
			else
			{
				session = Session.getInstance(
					props,
					new Authenticator() {
						PasswordAuthentication passAuth = new PasswordAuthentication(SmtpMailSender.this.user,SmtpMailSender.this.password);
						protected PasswordAuthentication getPasswordAuthentication()
						{
							return this.passAuth;
						}
					}
				);
			}
			if ( this.isDebugEnabled() )
			{
				session.setDebug(true);
			}

			// create a new MimeMessage object (using the Session created above)
			Message message = new MimeMessage(session);
			message.setHeader("X-Mailer", SmtpMailSender.Mailer);
			message.setFrom(new InternetAddress(from));
			for (int i = 0; i < to.length; i++)
			{
				message.addRecipient( Message.RecipientType.TO, new InternetAddress(to[i]) );
			}
			if ( cc != null )
			{
				for (int i = 0; i < cc.length; i++)
				{
					message.addRecipient( Message.RecipientType.CC, new InternetAddress(cc[i]) );
				}
			}
			if ( bcc != null )
			{
				for (int i = 0; i < bcc.length; i++)
				{
					message.addRecipient( Message.RecipientType.BCC, new InternetAddress(bcc[i]) );
				}
			}
			//message.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress(to) });
			message.setSubject(subject);
			message.setContent(body, "text/plain");
			message.setSentDate(new Date());

			// send the thing off
			//
			// The simple way to send a message is this:
			//   Transport.send(message);
			// If we're going to use some SMTP-specific features,
			// we need to manage the Transport object explicitly:
			//   SMTPTransport t = (SMTPTransport)session.getTransport( SmtpMailSender.Protocol );
			//   try
			//   {
			//   if (auth)
			//   {
			//	     t.connect(mailhost, user, password);
			//   }
			//   else
			//   {
			//	     t.connect();
			//	     t.sendMessage(msg, msg.getAllRecipients());
			//   }
			//   finally
			//   {
			//	     t.close();
			//   }
			Transport.send(message);

//System.err.println("Thank you.  Your message to " + to + " was successfully sent."); //XXX
		}
		catch (Exception e)
		{
			System.out.println("Unable to send message: " + e.toString());
			e.printStackTrace();
		}
	}

	//@} IMailSender implementation
}
