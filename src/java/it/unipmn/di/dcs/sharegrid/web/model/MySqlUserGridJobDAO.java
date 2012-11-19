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

import javax.sql.DataSource;

/**
 * MySQL specialized DAO class for UserGridJob objects.
 *
 * This class specializes the generic class <code>JdbcUserGridJobDAO</code> for
 * providing MySQL-optimized versions of its methods.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class MySqlUserGridJobDAO extends JdbcUserGridJobDAO
{
	/** A constructor. */
	public MySqlUserGridJobDAO(DataSource ds) throws Exception
	{
		super( ds );
	}

	/** A constructor. */
	public MySqlUserGridJobDAO(String jdbcClass, String jdbcDsn, String user, String password) throws Exception
	{
		super( jdbcClass, jdbcDsn, user, password );
	}
}
