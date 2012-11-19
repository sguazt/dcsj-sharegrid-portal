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

import it.unipmn.di.dcs.common.util.Strings;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * Class for accessing to the model configuration.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class Conf
{
	private IDAOFactory daoFactory;
	private IEnvironment env;
	private Properties props;

	/**
	 * A constructor.
	 */
	private Conf()
	{
		// empty
	}

	/**
	 * Returns the singleton instance.
	 */
	public static Conf instance()
	{
		return InstanceHolder.instance;
	}

	//@{ DAO configuration /////////////////////////////////////////////////

	/** Returns the instance of the factory for DAO objects. */
	public IDAOFactory getDAOFactory() throws ModelException
	{
		if ( this.daoFactory == null )
		{
			JdbcDAOFactory daoFact = null;
			DataSource ds = null;

			try
			{
				String jdbcType = null;
				jdbcType = this.getDaoJdbcType();

				if ( Strings.IsNullOrEmpty(jdbcType) )
				{
					throw new ModelException( "Configuration Error. JDBC type not specified." );
				}

				ds = (DataSource) this.getEnv().lookup("jdbc/" + jdbcType );
			}
			catch (ModelException me)
			{
				throw me;
			}
			catch (Exception e)
			{
				ds = null;
			}

			try
			{
				if ( ds != null )
				{
					daoFact = new JdbcDAOFactory(ds);
				}
				else
				{
					daoFact = new JdbcDAOFactory(
						this.getDaoJdbcDriver(),
						this.getDaoJdbcUrl(),
						this.getDaoJdbcUser(),
						this.getDaoJdbcPassword()
					);
				}
			}
			catch (Exception e)
			{
				throw new ModelException("Caught exception while creating a new DAO Factory instance.", e);
			}

			this.daoFactory = daoFact;
		}

		return this.daoFactory;
	}

	/** Returns the DAO JDBC type. */
	public String getDaoJdbcType()
	{
		try
		{
			return this.getProperties().getProperty( "dao.jdbc.type" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "";
	}

	/** Returns the DAO JDBC driver. */
	public String getDaoJdbcDriver()
	{
		try
		{
			return this.getProperties().getProperty( "dao.jdbc.driver" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "";
	}

	/** Returns the DAO JDBC URL. */
	public String getDaoJdbcUrl()
	{
		try
		{
			return this.getProperties().getProperty( "dao.jdbc.url" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "";
	}

	/** Returns the DAO JDBC user. */
	public String getDaoJdbcUser()
	{
		try
		{
			return this.getProperties().getProperty( "dao.jdbc.user" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "";
	}

	/** Returns the DAO JDBC user password. */
	public String getDaoJdbcPassword()
	{
		try
		{
			return this.getProperties().getProperty( "dao.jdbc.password" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "";
	}

	//@} DAO configuration /////////////////////////////////////////////////

	//@{ Localization Format configuration /////////////////////////////////

	/**
	 * Returns the date format.
	 * 
	 * For the syntax of the format @see java.text.SimpleDateFormat.
	 */
	public String getFormatDate()
	{
		try
		{
			return this.getProperties().getProperty( "format.date" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "yyyy-MM-dd";
	}

	/**
	 * Returns the time format.
	 * 
	 * For the syntax of the format @see java.text.SimpleDateFormat.
	 */
	public String getFormatTime()
	{
		try
		{
			return this.getProperties().getProperty( "format.time" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "HH:mm:ss";
	}

	/**
	 * Returns the date-time format.
	 * 
	 * For the syntax of the format @see java.text.SimpleDateFormat.
	 */
	public String getFormatDateTime()
	{
		try
		{
			return this.getProperties().getProperty( "format.datetime" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return this.getFormatDate() + " " + this.getFormatDateTime();
	}

	//@} Localization Format configuration /////////////////////////////////

	//@{ User configuration ////////////////////////////////////////////////

	/** Returns the user base path. */
	public String getUserBasePath()
	{
		try
		{
			return this.getProperties().getProperty( "user.basepath" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return this.getDefaultPath();
	}

	/** Returns the type of the user password obfuscator. */
	public IPasswordObfuscator getUserPasswordObfuscator()
	{
		String type = null;

		try
		{
			type = this.getProperties().getProperty( "user.password.obfuscator" );
		}
		catch (Exception e)
		{
			type = "passthru";
		}

		return new PasswordObfuscatorFactory().getObfuscator( type );
	}

	//@} User configuration ////////////////////////////////////////////////

	//@{ Grid Middleware configuration /////////////////////////////////////

	/** Returns the ID of the GRID middleware used. */
	public String getGridMiddlewareId()
	{
		try
		{
			return this.getProperties().getProperty( "grid.middleware.id" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "";
	}

	/**
	 * Returns the value of the given property {@code propName} for the
	 * given GRID middleware ID.
	 */
	public String getGridMiddlewareCustomProperty(String middlewareId, String propName)
	{
		try
		{
			return this.getProperties().getProperty( "grid.middleware." + middlewareId + "." + propName );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "";
	}

	//FIXME: this should be deprecated in favor of getGridMiddlewareCustomProperty
	public String getMiddlewareOurGridMgName()
	{
		try
		{
			return this.getProperties().getProperty( "grid.middleware.ourgrid.mgname" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "";
	}

	//FIXME: this should be deprecated in favor of getGridMiddlewareCustomProperty
	public String getMiddlewareOurGridMgPort()
	{
		try
		{
			return this.getProperties().getProperty( "grid.middleware.ourgrid.mgport" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "";
	}

	//FIXME: this should be deprecated in favor of getGridMiddlewareCustomProperty
	public String getMiddlewareOurGridMgRoot()
	{
		try
		{
			return this.getProperties().getProperty( "grid.middleware.ourgrid.mgroot" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "";
	}

	//@} Grid Middleware configuration /////////////////////////////////////

	//@{ Site configuration ////////////////////////////////////////////////

	/** Returns the base URL for the site. */
	public URL getWebBaseUrl() throws Exception
	{
		return new URL( this.getProperties().getProperty( "web.baseurl" ) );
	}

	/** Returns the default upload URL for the site. */
	public URL getWebUploadUrl() throws Exception
	{
		//return new URL( this.getProperties().getProperty( "web.upload.url" ) );
		return new URL(
			this.getWebBaseUrl().toString()
			+ this.getProperties().getProperty( "web.upload.url" )
		);
	}

	/** Returns the default upload path for the site. */
	public String getWebUploadPath()
	{
		try
		{
			return this.getProperties().getProperty( "web.upload.path" );
		}
		catch (Exception e)
		{
			// Ignore
		}

		return this.getDefaultPath();
	}

	/** Returns the URL for the user registration page. */
	public URL getWebUserRegistrationUrl() throws Exception
	{
		return new URL(
			this.getWebBaseUrl().toString()
			+ this.getProperties().getProperty( "web.user.registration.url" )
		);
	}

	/** Returns the URL for the user activation page. */
	public URL getWebUserActivationUrl() throws Exception
	{
		return new URL(
			this.getWebBaseUrl().toString()
			+ this.getProperties().getProperty( "web.user.activation.url" )
		);
	}

	/** Returns the URL for the user storage page. */
	public URL getWebUserStorageUrl() throws Exception
	{
		return new URL(
			this.getWebBaseUrl().toString()
			+ this.getProperties().getProperty( "web.user.storage.url" )
		);
	}

	/** Returns the URL for the GRID job detail page. */
	public URL getWebGridJobDetailUrl() throws Exception
	{
		return new URL(
			this.getWebBaseUrl().toString()
			+ this.getProperties().getProperty( "web.grid.jobdetail.url" )
		);
	}

	//@} Site configuration ////////////////////////////////////////////////

	//@{ SMTP configuration ////////////////////////////////////////////////

	/** Returns the SMTP host. */
	public String getSmtpHost()
	{
		try
		{
			return this.getProperties().getProperty( "mail.smtp.host" );
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}

	/** Returns the SMTP port. */
	public int getSmtpPort()
	{
		try
		{
			return Integer.parseInt( this.getProperties().getProperty( "mail.smtp.port" ) );
		}
		catch (Exception e)
		{
			// ignore
		}

		return 25;
	}

	/** Returns the SMTP user. */
	public String getSmtpUser()
	{
		try
		{
			return this.getProperties().getProperty( "mail.smtp.user" );
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}

	/** Returns the SMTP user password. */
	public String getSmtpPassword()
	{
		try
		{
			return this.getProperties().getProperty( "mail.smtp.password" );
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}

	/** Tells if TLS is used for the SMTP. */
	public boolean isSmtpTLSUsed()
	{
		try
		{
			return Strings.GetBoolean( this.getProperties().getProperty( "mail.smtp.usetls" ) );
		}
		catch (Exception e)
		{
			// ignore
		}

		return false;
	}

	/**
	 * Returns the message sender for the notification of a login
	 * registration.
	 */
	public String getSmtpRegistrationSender()
	{
		try
		{
			return this.getProperties().getProperty( "mail.smtp.registration.from" );
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}

	/**
	 * Returns the message subject for the notification of a login
	 * registration.
	 */
	public String getSmtpRegistrationSubject()
	{
		try
		{
			return this.getProperties().getProperty( "mail.smtp.registration.subject" );
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}

	/**
	 * Returns the message body for the notification of a login
	 * registration.
	 */
	public String getSmtpRegistrationBody()
	{
		try
		{
			return this.getProperties().getProperty( "mail.smtp.registration.body" );
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}

	/**
	 * Returns the message sender for the notification of a GRID job
	 * status update.
	 */
	public String getSmtpGridJobStatusUpdateSender()
	{
		try
		{
			return this.getProperties().getProperty( "mail.smtp.gridjobstatusupdate.from" );
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}

	/**
	 * Returns the message subject for the notification of a GRID job
	 * status update.
	 */
	public String getSmtpGridJobStatusUpdateSubject()
	{
		try
		{
			return this.getProperties().getProperty( "mail.smtp.gridjobstatusupdate.subject" );
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}

	/**
	 * Returns the message body for the notification of a GRID job status
	 * update.
	 */
	public String getSmtpGridJobStatusUpdateBody()
	{
		try
		{
			return this.getProperties().getProperty( "mail.smtp.gridjobstatusupdate.body" );
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}

	/**
	 * Returns the message sender for the notification of a GRID job
	 * lifetime update.
	 */
	public String getSmtpGridJobLifeTimeUpdateSender()
	{
		try
		{
			return this.getProperties().getProperty( "mail.smtp.gridjoblifetimeupdate.from" );
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}

	/**
	 * Returns the message subject for the notification of a GRID job
	 * lifetime update.
	 */
	public String getSmtpGridJobLifeTimeUpdateSubject()
	{
		try
		{
			return this.getProperties().getProperty( "mail.smtp.gridjoblifetimeupdate.subject" );
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}

	/**
	 * Returns the message body for the notification of a GRID job lifetime
	 * update.
	 */
	public String getSmtpGridJobLifeTimeUpdateBody()
	{
		try
		{
			return this.getProperties().getProperty( "mail.smtp.gridjoblifetimeupdate.body" );
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}

	//@} SMTP configuration ////////////////////////////////////////////////

	/** Returns the application environment. */
	protected IEnvironment getEnv()
	{
		if ( this.env == null )
		{
			//FIXME: eventually parameterize the type of env
			//       through env EnvironmentFactory
			//FIXME: parameterize the environment URI
			boolean envLoaded = false;
			try
			{
				this.env = new ServletEnvironment("java:/comp/env");
				envLoaded = this.env.checkEnv();
			}
			catch (Exception e)
			{
				envLoaded = false;
			}

			if ( !envLoaded )
			{
				// Servlet environment not available
				this.env = new StandardEnvironment("java:/comp/env");
				if ( ! this.env.checkEnv() )
				{
					this.env = null;
				}
			}
		}

		return this.env;
	}

	/** Returns all configuration properties. */
	protected Properties getProperties() throws Exception
	{
		if ( this.props == null )
		{
			this.props = new Properties();

			String confFileName = null;

			//FIXME: hard-coded string
			confFileName = (String) this.getEnv().lookup("it.unipmn.di.dcs.sharegrid.web.ConfigurationFile");

			if ( !Strings.IsNullOrEmpty( confFileName ) )
			{
				this.props.load( Thread.currentThread().getContextClassLoader().getResourceAsStream( confFileName ) );
			}
		}

		return this.props;
	}

	/** Returns the path usable as default path. */ 
	protected String getDefaultPath()
	{
		try
		{
			return System.getProperty("java.io.tmpdir");
		}
		catch (Exception e)
		{
			// Ignore
		}

		return "";
	}

	/**
i	 * Class for holding the singleton <code>conf</code> instance.
	 *
	 * This class is loaded on the first execution of
	 * <code>Conf.instance()</code> or the first access to
	 * <code>InstanceHolder.instance</code>, not before.
	 */
	private static class InstanceHolder
	{
		private final static Conf instance = new Conf();
	}
}
