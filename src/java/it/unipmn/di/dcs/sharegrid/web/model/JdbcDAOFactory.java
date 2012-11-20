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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * DAO Factory for JDBC database connections.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class JdbcDAOFactory implements IDAOFactory
{
	private DataSource ds = null;
	private String jdbcClass = null;
	private String jdbcDsn = null;
	private String user = null;
	private String password = null;

	/** A constructor. */
	public JdbcDAOFactory(DataSource ds)
	{
		this.ds = ds;
	}

	/** A constructor. */
	public JdbcDAOFactory(String jdbcClass, String jdbcDsn, String user, String password)
	{
		this.jdbcClass = jdbcClass;
		this.jdbcDsn = jdbcDsn;
		this.user = user;
		this.password = password;
	}

	//@{ IDAOFactory implementation

	public IUserDAO getUserDAO() throws Exception
	{
		if ( this.isMySqlDatabase() )
		{
			return	(this.ds != null)
				? new MySqlUserDAO(this.ds)
				: new MySqlUserDAO(this.jdbcClass, this.jdbcDsn, this.user, this.password);
		}

		// fall-back to generic case
		return	(this.ds != null)
			? new JdbcUserDAO(this.ds)
			: new JdbcUserDAO(this.jdbcClass, this.jdbcDsn, this.user, this.password);
	}

	public IUserCloudServiceDAO getUserCloudServiceDAO() throws Exception
	{
		if ( this.isMySqlDatabase() )
		{
			return  (this.ds != null)
				? new MySqlUserCloudServiceDAO(this.ds)
				: new MySqlUserCloudServiceDAO(this.jdbcClass, this.jdbcDsn, this.user, this.password);
		}

		// fall-back to generic case
		return  (this.ds != null)
			? new JdbcUserCloudServiceDAO(this.ds)
			: new JdbcUserCloudServiceDAO(this.jdbcClass, this.jdbcDsn, this.user, this.password);
	}

	public IUserGridJobDAO getUserGridJobDAO() throws Exception
	{
		if ( this.isMySqlDatabase() )
		{
//			if ( this.ds != null )
//			{
//				return	new MySqlUserGridJobDAO(this.ds);
//			}
//			else
//			{
//				return new MySqlUserGridJobDAO(this.jdbcClass, this.jdbcDsn, this.user, this.password);
//			}
			return	(this.ds != null)
				? new MySqlUserGridJobDAO(this.ds)
				: new MySqlUserGridJobDAO(this.jdbcClass, this.jdbcDsn, this.user, this.password);
		}

//		if	(this.ds != null)
//		{
//			return new JdbcUserGridJobDAO(this.ds);
//		}
//		else
//		{
//			return new JdbcUserGridJobDAO(this.jdbcClass, this.jdbcDsn, this.user, this.password);
//		}
		// fall-back to generic case
		return	(this.ds != null)
			? new JdbcUserGridJobDAO(this.ds)
			: new JdbcUserGridJobDAO(this.jdbcClass, this.jdbcDsn, this.user, this.password);
	}

	//@} IDAOFactory implementation

	/** Returns {@code true} if the underlying DB is a MySQL database. */
	protected boolean isMySqlDatabase()
	{
		try
		{
			Connection con = this.getConnection();

			String prodName = null;
			prodName = con.getMetaData().getDatabaseProductName();

			return "mysql".equalsIgnoreCase( prodName );
		}
		catch (Exception se)
		{
			// Ignore
		}

		return false;
	}

	/** Returns the database connection object. */
	protected Connection getConnection() throws Exception
	{
		if ( this.ds != null )
		{
			return this.ds.getConnection();
		}

		Class.forName( this.jdbcClass );
		return DriverManager.getConnection( this.jdbcDsn, this.user, this.password );
	}
}
