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

package it.unipmn.di.dcs.sharegrid.web.management;

import it.unipmn.di.dcs.sharegrid.web.management.grid.IGridJobUpdaterStateDAO;
import it.unipmn.di.dcs.sharegrid.web.management.grid.IUserGridJobCheckStateDAO;
import it.unipmn.di.dcs.sharegrid.web.management.grid.JdbcGridJobUpdaterStateDAO;
import it.unipmn.di.dcs.sharegrid.web.management.grid.JdbcUserGridJobCheckStateDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * DAO Factory class for JDBC database connections.
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

	public JdbcDAOFactory(DataSource ds)
	{
		this.ds = ds;
	}

	public JdbcDAOFactory(String jdbcClass, String jdbcDsn, String user, String password)
	{
		this.jdbcClass = jdbcClass;
		this.jdbcDsn = jdbcDsn;
		this.user = user;
		this.password = password;
	}

	//@{ IDAOFactory implementation ////////////////////////////////////////

	public IGridJobUpdaterStateDAO getGridJobUpdaterStateDAO() throws ManagementException
	{
		return  (this.ds != null)
			? new JdbcGridJobUpdaterStateDAO(this.ds)
			: new JdbcGridJobUpdaterStateDAO(this.jdbcClass, this.jdbcDsn, this.user, this.password);
	}

	public IUserGridJobCheckStateDAO getUserGridJobCheckStateDAO() throws ManagementException
	{
		return  (this.ds != null)
			? new JdbcUserGridJobCheckStateDAO(this.ds)
			: new JdbcUserGridJobCheckStateDAO(this.jdbcClass, this.jdbcDsn, this.user, this.password);
	}

	//@} IDAOFactory implementation ////////////////////////////////////////
}
