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

package it.unipmn.di.dcs.sharegrid.web.management.grid;

import it.unipmn.di.dcs.sharegrid.web.management.ManagementException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

/**
 * A UserGridJob check state data store with persistence implemented via JDBC.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class JdbcUserGridJobCheckStateDAO implements IUserGridJobCheckStateDAO
{
	// SQL statements
	private static final String SQL_INS = "INSERT INTO sharegrid_usergridjobcheck (jobid,statuscheckdate,lifetimecheckdate) VALUES (?,?,?)";
	private static final String SQL_UPD = "UPDATE sharegrid_usergridjobcheck SET jobid=?,statuscheckdate=?,lifetimecheckdate=? WHERE id=?";
	private static final String SQL_DEL = "DELETE FROM sharegrid_usergridjobcheck WHERE id=?";
	private static final String SQL_EXIST = "SELECT COUNT(*) FROM sharegrid_usergridjobcheck WHERE id=?";
	private static final String SQL_LIST = "SELECT id,jobid,statuscheckdate,lifetimecheckdate FROM sharegrid_usergridjobcheck";
	private static final String SQL_LIST_ID = "SELECT id FROM sharegrid_usergridjobcheck";
	private static final String SQL_GET = SQL_LIST + " WHERE id=?";
	private static final String SQL_GET_BY_JID = SQL_LIST + " WHERE jobid=?";

	private DataSource ds;
	private String jdbcClass;
	private String jdbcDsn;
	private String user;
	private String password;
	private Connection con;

	/**
	 * A constructor.
	 */
	public JdbcUserGridJobCheckStateDAO(String jdbcClass, String jdbcDsn, String user, String password) throws ManagementException
	{
		// preconditions
		assert( jdbcClass != null && jdbcDsn != null );

		// Connect to DB through DriverManager

		this.jdbcClass = jdbcClass;
		this.jdbcDsn = jdbcDsn;
		this.user = user;
		this.password = password;

		try
		{
			Class.forName( jdbcClass );

			this.con = DriverManager.getConnection( jdbcDsn, user, password );
		}
		catch (Exception e)
		{
			throw new ManagementException("Unable to create a new instance.", e);
		}
	}

	/**
	 * A constructor.
	 */
	public JdbcUserGridJobCheckStateDAO(DataSource ds) throws ManagementException
	{
		// preconditions
		assert( ds != null );

		// Connect to DB through DataSource
		this.ds = ds;
	}

	@Override
	protected void finalize() throws Throwable
	{
		if ( this.ds != null )
		{
			this.ds = null;
		}
		if ( this.con != null )
		{
			try { this.con.close(); } catch (Exception e) { }
			this.con = null;
		}

		super.finalize();
	}

	public Connection getConnection() throws ManagementException
	{
		Connection con = null;

		try
		{
			if ( this.con != null )
			{
				// Get the connection from the DriverManager

				if ( ! this.con.isValid(5) )
				{
					// reconnect

					Class.forName( jdbcClass );

					this.con = DriverManager.getConnection(
						this.jdbcDsn,
						this.user,
						this.password
					);
				}
	 
				this.con.setAutoCommit( true );

				con = this.con;
			}
			else
			{
				// Get connection from DataSource

				try
				{
					con = this.ds.getConnection();
					con.setAutoCommit( true );
				}
				catch (Exception ex)
				{
					if ( con != null )
					{
						try { con.close(); } catch (SQLException se) { /* ignore */ }
						con = null;
					}

					throw ex;
				}
			}
		}
		catch (Exception e)
		{
			throw new ManagementException("Unable to create a DB connection.", e);
		}

		return con;
	}

	//@{ IUserGridJobCheckStateDAO implementation

	public Integer save(UserGridJobCheckState state) throws ManagementException
	{
		try
		{
			if ( state.getId() > 0 && this.exists( state.getId() ) )
			{
				this.update( state );
				return state.getId();
			}

			return this.insert( state );
		}
		catch (ManagementException me)
		{
			throw me;
		}
		catch (Exception e)
		{
			throw new ManagementException("Unable to save the grid job updater state.", e);
		}
	}

	public Integer insert(UserGridJobCheckState state) throws ManagementException
	{
		int id = -1;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			// Note: the state id is an auto-increment

			con = this.getConnection();

			pstmt = con.prepareStatement( SQL_INS, Statement.RETURN_GENERATED_KEYS );

			pstmt.clearParameters();

			if ( state.getJobId() != null )
			{
				pstmt.setInt( 1, state.getJobId() );
			}
			else
			{
				pstmt.setNull( 1, Types.INTEGER );
			}
			if ( state.getLastStatusCheckDate() != null )
			{
				pstmt.setTimestamp( 2, new Timestamp( state.getLastStatusCheckDate().getTime() ) );
			}
			else
			{
				pstmt.setNull( 2, Types.TIMESTAMP );
				//pstmt.setTimestamp( 2, new Timestamp( new Date().getTime() ) );
			}
			if ( state.getLastLifetimeCheckDate() != null )
			{
				pstmt.setTimestamp( 3, new Timestamp( state.getLastLifetimeCheckDate().getTime() ) );
			}
			else
			{
				pstmt.setNull( 3, Types.TIMESTAMP );
				//pstmt.setTimestamp( 3, new Timestamp( new Date().getTime() ) );
			}

			pstmt.executeUpdate();

			rs = pstmt.getGeneratedKeys();

			if ( rs.next() )
			{
				id = rs.getInt(1);
			}
			else
			{
				throw new Exception( "Auto-Generated keys not retrievable" );
			}
		}
		catch (Exception e)
		{
			throw new ManagementException("Unable to insert a new grid job check state.", e);
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmt != null )
			{
				try { pstmt.close(); } catch (SQLException se) { /* ignore */ }
				pstmt = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return id;
	}

	public void update(UserGridJobCheckState state) throws ManagementException
	{
		Connection con = null;
		PreparedStatement pstmt = null;

		try
		{
			con = this.getConnection();

			pstmt = con.prepareStatement( SQL_UPD );

			pstmt.clearParameters();

			if ( state.getJobId() != null )
			{
				pstmt.setInt( 1, state.getJobId() );
			}
			else
			{
				pstmt.setNull( 1, Types.INTEGER );
			}
			if ( state.getLastStatusCheckDate() != null )
			{
				pstmt.setTimestamp( 2, new Timestamp( state.getLastStatusCheckDate().getTime() ) );
			}
			else
			{
				pstmt.setNull( 2, Types.TIMESTAMP );
				//pstmt.setTimestamp( 2, new Timestamp( new Date().getTime() ) );
			}
			if ( state.getLastLifetimeCheckDate() != null )
			{
				pstmt.setTimestamp( 3, new Timestamp( state.getLastLifetimeCheckDate().getTime() ) );
			}
			else
			{
				pstmt.setNull( 3, Types.TIMESTAMP );
				//pstmt.setTimestamp( 3, new Timestamp( new Date().getTime() ) );
			}
			pstmt.setInt( 4, state.getId() );

			pstmt.executeUpdate();
		}
		catch (Exception e)
		{
			throw new ManagementException("Unable to update the grid job updater state.", e);
		}
		finally
		{
			if ( pstmt != null )
			{
				try { pstmt.close(); } catch (SQLException se) { /* ignore */ }
				pstmt = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}
	}

	public UserGridJobCheckState load(Integer id) throws ManagementException
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		UserGridJobCheckState state = null;

		try
		{
			con = this.getConnection();

			pstmt = con.prepareStatement( SQL_GET );

			pstmt.clearParameters();

			pstmt.setInt( 1, id );

			rs = pstmt.executeQuery();

			if ( rs.next() )
			{
				state = JdbcUserGridJobCheckStateDAO.CreateStateFromResultSet( rs );
			}
		}
		catch (Exception e)
		{
			throw new ManagementException("Unable to load the grid job updater state.", e);
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmt != null )
			{
				try { pstmt.close(); } catch (SQLException se) { /* ignore */ }
				pstmt = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return state;
	}

	public void remove(Integer id) throws ManagementException
	{
		Connection con = null;
		PreparedStatement pstmt = null;

		try
		{
			con = this.getConnection();

			pstmt = con.prepareStatement( SQL_DEL );

			pstmt.clearParameters();

			pstmt.setInt( 1, id );

			pstmt.executeUpdate();
		}
		catch (Exception e)
		{
			throw new ManagementException("Unable to remove the grid job updater state.", e);
		}
		finally
		{
			if ( pstmt != null )
			{
				try { pstmt.close(); } catch (SQLException se) { /* ignore */ }
				pstmt = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}
	}

	public boolean exists(Integer id) throws ManagementException
	{
		boolean found = false;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmt = con.prepareStatement( SQL_EXIST );

			pstmt.clearParameters();

			pstmt.setInt( 1, id );

			rs = pstmt.executeQuery();

			if ( rs.next() && rs.getInt(1) > 0 )
			{
				found = true;
			}
		}
		catch (Exception e)
		{
			throw new ManagementException("Unable to check for existence of the grid job updater state.", e);
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmt != null )
			{
				try { pstmt.close(); } catch (SQLException se) { /* ignore */ }
				pstmt = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return found;
	}

	public List<UserGridJobCheckState> list() throws ManagementException
	{
		List<UserGridJobCheckState> states = new ArrayList<UserGridJobCheckState>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmt = con.prepareStatement( SQL_LIST );

			pstmt.clearParameters();

			rs = pstmt.executeQuery();

			while ( rs.next() )
			{
				states.add(
					JdbcUserGridJobCheckStateDAO.CreateStateFromResultSet( rs )
				);
			}
		}
		catch (Exception e)
		{
			throw new ManagementException("Unable to list the grid job updater states.", e);
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmt != null )
			{
				try { pstmt.close(); } catch (SQLException se) { /* ignore */ }
				pstmt = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return states;
	}

	public List<Integer> idList() throws ManagementException
	{
		List<Integer> statesId = new ArrayList<Integer>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmt = con.prepareStatement( SQL_LIST_ID );

			pstmt.clearParameters();

			rs = pstmt.executeQuery();

			while ( rs.next() )
			{
				statesId.add( rs.getInt(1) );
			}
		}
		catch (Exception e)
		{
			throw new ManagementException("Unable to list the identifiers of the grid job updater states.", e);
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmt != null )
			{
				try { pstmt.close(); } catch (SQLException se) { /* ignore */ }
				pstmt = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return statesId;
	}

        public List<UserGridJobCheckState> listFromId(Integer[] ids) throws ManagementException
        {
                List<UserGridJobCheckState> states = new ArrayList<UserGridJobCheckState>();

                if ( ids.length > 0 )
                {
                        Connection con = null;
                        PreparedStatement pstmt = null;
                        ResultSet rs = null;

                        try
                        {
                                con = this.getConnection();

                                //String params = null;
                                //params = "(" + Strings.Repeat( "?,", ids.length - 1 ) + "?)";
                                //pstmt = con.prepareStatement( SQL_LIST + " WHERE id IN " + params ); //FIXME: how is IN operator SQL standard?

                                StringBuilder params = new StringBuilder();
				for (Integer id : ids)
				{
					if ( params.length() > 0 )
					{
						params.append( " OR " );
					}
					params.append( "id=?" );
				}

                                pstmt = con.prepareStatement( SQL_LIST + " WHERE (" + params + ")" );

				pstmt.clearParameters();

                                for (int i = 1; i <= ids.length; i++ )
                                {
                                        pstmt.setInt( i, ids[i-1] );
                                }

                                rs = pstmt.executeQuery();

                                while ( rs.next() )
                                {
                                        states.add(
                                                JdbcUserGridJobCheckStateDAO.CreateStateFromResultSet( rs )
                                        );
                                }
                        }
			catch (Exception e)
			{
				throw new ManagementException("Unable to list the grid job updater states by identifiers.", e);
			}
                        finally
                        {
                                if ( rs != null )
                                {
                                        try { rs.close(); } catch (SQLException se) { /* ignore */ }
                                        rs = null;
                                }
                                if ( pstmt != null )
                                {
                                        try { pstmt.close(); } catch (SQLException se) { /* ignore */ }
                                        pstmt = null;
                                }
                                if ( con != null )
                                {
                                        try { con.close(); } catch (SQLException se) { /* ignore */ }
                                        con = null;
                                }
                        }
                }

                return states;
        }

	public UserGridJobCheckState loadByJobId(int jobId) throws ManagementException
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		UserGridJobCheckState state = null;

		try
		{
			con = this.getConnection();

			pstmt = con.prepareStatement( SQL_GET_BY_JID );

			pstmt.clearParameters();

			pstmt.setInt( 1, jobId );

			rs = pstmt.executeQuery();

			if ( rs.next() )
			{
				state = JdbcUserGridJobCheckStateDAO.CreateStateFromResultSet( rs );
			}
		}
		catch (Exception e)
		{
			throw new ManagementException("Unable to load the grid job updater state from job id.", e);
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmt != null )
			{
				try { pstmt.close(); } catch (SQLException se) { /* ignore */ }
				pstmt = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return state;
	}

	//@} IUserGridJobCheckStateDAO implementation

	/**
	 * Returns a UserGridJobCheckState object filled up with the given result set.
	 */
	protected static UserGridJobCheckState CreateStateFromResultSet(final ResultSet rs) throws Exception
	{
		UserGridJobCheckState state = new UserGridJobCheckState();
		state.setId( rs.getInt(1) );
		state.setJobId( rs.getInt(2) );
		state.setLastStatusCheckDate( rs.getTimestamp(3) );
		state.setLastLifetimeCheckDate( rs.getTimestamp(4) );

		return state;
	}
}
