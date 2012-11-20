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

package it.unipmn.di.dcs.sharegrid.web.model.cloud;

import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;

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
 * A svc data store with persistence implemented via JDBC.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class JdbcUserCloudServiceDAO implements IUserCloudServiceDAO
{
	// SQL statements
	private static final String SQL_CLOUDSVC_INS = "INSERT INTO sharegrid_usercloudsvc (userid,name,submitdate,stopdate,schedsvcid,status,middlewareid) VALUES (?,?,?,?,?,?,?)";
	private static final String SQL_CLOUDSVC_UPD = "UPDATE sharegrid_usercloudsvc SET userid=?,name=?,submitdate=?,stopdate=?,schedsvcid=?,status=?,middlewareid=? WHERE id=?";
	private static final String SQL_CLOUDSVC_DEL = "DELETE FROM sharegrid_usercloudsvc WHERE id=?";
	private static final String SQL_CLOUDSVC_LIST = "SELECT id,userid,name,submitdate,stopdate,schedsvcid,status,middlewareid FROM sharegrid_usercloudsvc";
	private static final String SQL_CLOUDSVC_GET = SQL_CLOUDSVC_LIST + " WHERE id=?";
	private static final String SQL_CLOUDSVC_GET_BY_UID = SQL_CLOUDSVC_LIST + " WHERE userid=?";
	private static final String SQL_CLOUDSVC_GET_BY_NAME = SQL_CLOUDSVC_LIST + " WHERE name=?";
	private static final String SQL_CLOUDSVC_LIST_BY_UID = SQL_CLOUDSVC_LIST + " WHERE userid=?";
	//private static final String SQL_CLOUDSVC_LIST_FROM_STATUS = SQL_CLOUDSVC_LIST + " WHERE ";
	//private static final String SQL_CLOUDSVC_LIST_BY_UID_STATUS = SQL_CLOUDSVC_LIST + " WHERE ";
	private static final String SQL_CLOUDSVC_LIST_PENDING = SQL_CLOUDSVC_LIST + " WHERE (stopdate IS NULL"
								+ " OR status= " + JdbcUserCloudServiceDAO.ExecutionStatusToInt( ExecutionStatus.READY )
								+ " OR status= " + JdbcUserCloudServiceDAO.ExecutionStatusToInt( ExecutionStatus.RUNNING )
								+ " OR status= " + JdbcUserCloudServiceDAO.ExecutionStatusToInt( ExecutionStatus.STAGING_IN )
								+ " OR status= " + JdbcUserCloudServiceDAO.ExecutionStatusToInt( ExecutionStatus.STAGING_OUT )
								+ " OR status= " + JdbcUserCloudServiceDAO.ExecutionStatusToInt( ExecutionStatus.UNKNOWN )
								+ " OR status= " + JdbcUserCloudServiceDAO.ExecutionStatusToInt( ExecutionStatus.UNSTARTED )
								+ ")";
	private static final String SQL_CLOUDSVC_LIST_ID = "SELECT id FROM sharegrid_usercloudsvc";
	private static final String SQL_CLOUDSVC_EXIST = "SELECT COUNT(*) FROM sharegrid_usercloudsvc WHERE id=?";

	private DataSource ds;
	private String jdbcClass;
	private String jdbcDsn;
	private String user;
	private String password;
	private Connection con;

	/**
	 * A constructor.
	 */
        public JdbcUserCloudServiceDAO(String jdbcClass, String jdbcDsn, String user, String password) throws Exception
        {
		// preconditions
		assert( jdbcClass != null && jdbcDsn != null );

		// Connect to DB through DriverManager

		this.jdbcClass = jdbcClass;
		this.jdbcDsn = jdbcDsn;
		this.user = user;
		this.password = password;

		Class.forName( jdbcClass );

		this.con = DriverManager.getConnection( jdbcDsn, user, password );

		//this.init();
	}

	/**
	 * A constructor.
	 */
	public JdbcUserCloudServiceDAO(DataSource ds) throws Exception
	{
		// preconditions
		assert( ds != null );

		// Connect to DB through DataSource

		this.ds = ds;

		//this.init();
	}

	//private void init() throws SQLException
	//{
	//	// empty
	//}

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

	public Connection getConnection() throws Exception
	{
		Connection con = null;

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
			catch (Exception e)
			{
				if ( con != null )
				{
					try { con.close(); } catch (SQLException se) { /* ignore */ }
					con = null;
				}

				throw e;
			}
		}

		return con;
	}

	//@{ IUserCloudServiceDAO implementation

	public Integer save(UserCloudService svc) throws Exception
	{
		if ( svc.getId() > 0 && this.exists( svc.getId() ) )
		{
			this.update( svc );
			return svc.getId();
		}

		return this.insert( svc );
	}

	public Integer insert(UserCloudService svc) throws Exception
	{
		int id = -1;
		Connection con = null;
		PreparedStatement pstmtSvcIns = null;
		ResultSet rs = null;

		try
		{
			// Note: the svc id is an auto-increment

			con = this.getConnection();

			pstmtSvcIns = con.prepareStatement( SQL_CLOUDSVC_INS, Statement.RETURN_GENERATED_KEYS );
			pstmtSvcIns.clearParameters();

			pstmtSvcIns.setInt( 1, svc.getUserId() );
			pstmtSvcIns.setString( 2, svc.getName() );
			if ( svc.getSubmissionDate() != null )
			{
				pstmtSvcIns.setTimestamp( 3, new Timestamp( svc.getSubmissionDate().getTime() ) );
			}
			else
			{
				pstmtSvcIns.setNull( 3, Types.TIMESTAMP );
			}
			if ( svc.getStoppingDate() != null )
			{
				pstmtSvcIns.setTimestamp( 4, new Timestamp( svc.getStoppingDate().getTime() ) );
			}
			else
			{
				pstmtSvcIns.setNull( 4, Types.TIMESTAMP );
			}
			if ( svc.getSchedulerServiceId() != null )
			{
				pstmtSvcIns.setInt( 5, svc.getSchedulerServiceId() );
			}
			else
			{
				pstmtSvcIns.setNull( 5, Types.INTEGER );
			}
			if ( svc.getStatus() != null )
			{
				pstmtSvcIns.setInt( 6, JdbcUserCloudServiceDAO.ExecutionStatusToInt( svc.getStatus() ) );
			}
			else
			{
				pstmtSvcIns.setNull( 6, Types.INTEGER );
			}
			if ( svc.getMiddlewareId() != null )
			{
				pstmtSvcIns.setInt( 7, svc.getMiddlewareId() );
			}
			else
			{
				pstmtSvcIns.setNull( 7, Types.INTEGER );
			}

			pstmtSvcIns.executeUpdate();

			rs = pstmtSvcIns.getGeneratedKeys();

			if ( rs.next() )
			{
				id = rs.getInt(1);
			}
			else
			{
				throw new Exception( "Auto-Generated keys not retrievable" );
			}
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmtSvcIns != null )
			{
				try { pstmtSvcIns.close(); } catch (SQLException se) { /* ignore */ }
				pstmtSvcIns = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return id;
	}

	public void update(UserCloudService svc) throws Exception
	{
		Connection con = null;
		PreparedStatement pstmtSvcUpd = null;

		try
		{
			con = this.getConnection();

			pstmtSvcUpd = con.prepareStatement( SQL_CLOUDSVC_UPD );

			pstmtSvcUpd.clearParameters();

			pstmtSvcUpd.setInt( 1, svc.getUserId() );
			pstmtSvcUpd.setString( 2, svc.getName() );
			if ( svc.getSubmissionDate() != null )
			{
				pstmtSvcUpd.setTimestamp( 3, new Timestamp( svc.getSubmissionDate().getTime() ) );
			}
			else
			{
				pstmtSvcUpd.setNull( 3, Types.TIMESTAMP );
			}
			if ( svc.getStoppingDate() != null )
			{
				pstmtSvcUpd.setTimestamp( 4, new Timestamp( svc.getStoppingDate().getTime() ) );
			}
			else
			{
				pstmtSvcUpd.setNull( 4, Types.TIMESTAMP );
			}
			if ( svc.getSchedulerServiceId() != null )
			{
				pstmtSvcUpd.setInt( 5, svc.getSchedulerServiceId() );
			}
			else
			{
				pstmtSvcUpd.setNull( 5, Types.INTEGER );
			}
			if ( svc.getStatus() != null )
			{
				pstmtSvcUpd.setInt( 6, JdbcUserCloudServiceDAO.ExecutionStatusToInt( svc.getStatus() ) );
			}
			else
			{
				pstmtSvcUpd.setNull( 6, Types.INTEGER );
			}
			if ( svc.getMiddlewareId() != null )
			{
				pstmtSvcUpd.setInt( 7, svc.getMiddlewareId() );
			}
			else
			{
				pstmtSvcUpd.setNull( 7, Types.INTEGER );
			}

			pstmtSvcUpd.setInt( 8, svc.getId() );

			pstmtSvcUpd.executeUpdate();
		}
		finally
		{
			if ( pstmtSvcUpd != null )
			{
				try { pstmtSvcUpd.close(); } catch (SQLException se) { /* ignore */ }
				pstmtSvcUpd = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}
	}

	public UserCloudService load(Integer id) throws Exception
	{
		Connection con = null;
		PreparedStatement pstmtSvcGet = null;
		ResultSet rs = null;
		UserCloudService svc = null;

		try
		{
			con = this.getConnection();

			pstmtSvcGet = con.prepareStatement( SQL_CLOUDSVC_GET );

			pstmtSvcGet.clearParameters();

			pstmtSvcGet.setInt( 1, id );

			rs = pstmtSvcGet.executeQuery();

			if ( rs.next() )
			{
				svc = JdbcUserCloudServiceDAO.CreateSvcFromResultSet( rs );
			}
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmtSvcGet != null )
			{
				try { pstmtSvcGet.close(); } catch (SQLException se) { /* ignore */ }
				pstmtSvcGet = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return svc;
	}

	public void remove(Integer id) throws Exception
	{
		Connection con = null;
		PreparedStatement pstmtSvcDel = null;

		try
		{
			con = this.getConnection();

			pstmtSvcDel = con.prepareStatement( SQL_CLOUDSVC_DEL );

			pstmtSvcDel.clearParameters();

			pstmtSvcDel.setInt( 1, id );

			pstmtSvcDel.executeUpdate();
		}
		finally
		{
			if ( pstmtSvcDel != null )
			{
				try { pstmtSvcDel.close(); } catch (SQLException se) { /* ignore */ }
				pstmtSvcDel = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}
	}

	public boolean exists(Integer id) throws Exception
	{
		boolean found = false;
		Connection con = null;
		PreparedStatement pstmtSvcExist = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtSvcExist = con.prepareStatement( SQL_CLOUDSVC_EXIST );

			pstmtSvcExist.clearParameters();

			pstmtSvcExist.setInt( 1, id );

			rs = pstmtSvcExist.executeQuery();

			if ( rs.next() && rs.getInt(1) > 0 )
			{
				found = true;
			}
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmtSvcExist != null )
			{
				try { pstmtSvcExist.close(); } catch (SQLException se) { /* ignore */ }
				pstmtSvcExist = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return found;
	}

	public List<UserCloudService> list() throws Exception
	{
		List<UserCloudService> svcs = new ArrayList<UserCloudService>();
		Connection con = null;
		PreparedStatement pstmtSvcList = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtSvcList = con.prepareStatement( SQL_CLOUDSVC_LIST );

			pstmtSvcList.clearParameters();

			rs = pstmtSvcList.executeQuery();

			while ( rs.next() )
			{
				svcs.add(
					JdbcUserCloudServiceDAO.CreateSvcFromResultSet( rs )
				);
			}
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmtSvcList != null )
			{
				try { pstmtSvcList.close(); } catch (SQLException se) { /* ignore */ }
				pstmtSvcList = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return svcs;
	}

        public List<UserCloudService> listFromId(Integer[] ids) throws Exception
        {
                List<UserCloudService> svcs = new ArrayList<UserCloudService>();

                if ( ids.length > 0 )
                {
                        Connection con = null;
                        PreparedStatement pstmtSvcList = null;
                        ResultSet rs = null;

                        try
                        {
                                con = this.getConnection();

                                String params = null;
                                params = "(" + Strings.Repeat( "?,", ids.length - 1 ) + "?)";

                                pstmtSvcList = con.prepareStatement( SQL_CLOUDSVC_LIST + " WHERE id IN " + params ); //TODO: how is IN operator SQL standard?

                                for (int i = 1; i <= ids.length; i++ )
                                {
                                        pstmtSvcList.setInt( i, ids[i-1] );
                                }
                               pstmtSvcList.clearParameters();

                                rs = pstmtSvcList.executeQuery();

                                while ( rs.next() )
                                {
                                        svcs.add(
                                                JdbcUserCloudServiceDAO.CreateSvcFromResultSet( rs )
                                        );
                                }
                        }
                        finally
                        {
                                if ( rs != null )
                                {
                                        try { rs.close(); } catch (SQLException se) { /* ignore */ }
                                        rs = null;
                                }
                                if ( pstmtSvcList != null )
                                {
                                        try { pstmtSvcList.close(); } catch (SQLException se) { /* ignore */ }
                                        pstmtSvcList = null;
                                }
                                if ( con != null )
                                {
                                        try { con.close(); } catch (SQLException se) { /* ignore */ }
                                        con = null;
                                }
                        }
                }

                return svcs;
        }

	public List<Integer> idList() throws Exception
	{
		List<Integer> svcsId = new ArrayList<Integer>();
		Connection con = null;
		PreparedStatement pstmtSvcIdList = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtSvcIdList = con.prepareStatement( SQL_CLOUDSVC_LIST_ID );

			pstmtSvcIdList.clearParameters();

			rs = pstmtSvcIdList.executeQuery();

			while ( rs.next() )
			{
				svcsId.add( rs.getInt(1) );
			}
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmtSvcIdList != null )
			{
				try { pstmtSvcIdList.close(); } catch (SQLException se) { /* ignore */ }
				pstmtSvcIdList = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return svcsId;
	}

	public List<UserCloudService> listFromUser(int userId) throws Exception
	{
		List<UserCloudService> svcs = new ArrayList<UserCloudService>();
		Connection con = null;
		PreparedStatement pstmtSvcList = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtSvcList = con.prepareStatement( SQL_CLOUDSVC_LIST_BY_UID );

			pstmtSvcList.clearParameters();

			pstmtSvcList.setInt( 1, userId );

			rs = pstmtSvcList.executeQuery();

			while ( rs.next() )
			{
				svcs.add(
					JdbcUserCloudServiceDAO.CreateSvcFromResultSet( rs )
				);
			}
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmtSvcList != null )
			{
				try { pstmtSvcList.close(); } catch (SQLException se) { /* ignore */ }
				pstmtSvcList = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return svcs;
	}

        public List<UserCloudService> listFromStatus(final ExecutionStatus[] status) throws Exception
	{
		List<UserCloudService> svcs = new ArrayList<UserCloudService>();

		if ( status.length > 0 )
		{
			Connection con = null;
			PreparedStatement pstmtSvcList = null;
			ResultSet rs = null;

			try
			{
				con = this.getConnection();

				String params = null;
				params = "(" + Strings.Repeat( "?,", status.length - 1 ) + "?)";

				pstmtSvcList = con.prepareStatement( SQL_CLOUDSVC_LIST + " WHERE status IN " + params ); //TODO: how is IN operator SQL standard?

				for (int i = 1; i <= status.length; i++ )
				{
					pstmtSvcList.setInt( i, JdbcUserCloudServiceDAO.ExecutionStatusToInt( status[i] ) );
				}

				pstmtSvcList.clearParameters();

				rs = pstmtSvcList.executeQuery();

				while ( rs.next() )
				{
					svcs.add(
						JdbcUserCloudServiceDAO.CreateSvcFromResultSet( rs )
					);
				}
			}
			finally
			{
				if ( rs != null )
				{
					try { rs.close(); } catch (SQLException se) { /* ignore */ }
					rs = null;
				}
				if ( pstmtSvcList != null )
				{
					try { pstmtSvcList.close(); } catch (SQLException se) { /* ignore */ }
					pstmtSvcList = null;
				}
				if ( con != null )
				{
					try { con.close(); } catch (SQLException se) { /* ignore */ }
					con = null;
				}
			}
		}

		return svcs;
	}

        public List<UserCloudService> listFromUserAndStatus(int userId, final ExecutionStatus[] status) throws Exception
	{
		List<UserCloudService> svcs = new ArrayList<UserCloudService>();

		if ( status.length > 0 )
		{
			Connection con = null;
			PreparedStatement pstmtSvcList = null;
			ResultSet rs = null;

			try
			{
				con = this.getConnection();

				String params = null;
				params = "(" + Strings.Repeat( "?,", status.length - 1 ) + "?)";

				pstmtSvcList = con.prepareStatement( SQL_CLOUDSVC_LIST + " WHERE userid=? AND status IN " + params ); //TODO: how is IN operator SQL standard?

				pstmtSvcList.setInt( 1, userId );

				for (int i = 1; i <= status.length; i++ )
				{
					pstmtSvcList.setInt( i + 1, JdbcUserCloudServiceDAO.ExecutionStatusToInt( status[i] ) );
				}

				pstmtSvcList.clearParameters();

				rs = pstmtSvcList.executeQuery();

				while ( rs.next() )
				{
					svcs.add(
						JdbcUserCloudServiceDAO.CreateSvcFromResultSet( rs )
					);
				}
			}
			finally
			{
				if ( rs != null )
				{
					try { rs.close(); } catch (SQLException se) { /* ignore */ }
					rs = null;
				}
				if ( pstmtSvcList != null )
				{
					try { pstmtSvcList.close(); } catch (SQLException se) { /* ignore */ }
					pstmtSvcList = null;
				}
				if ( con != null )
				{
					try { con.close(); } catch (SQLException se) { /* ignore */ }
					con = null;
				}
			}
		}

		return svcs;
	}

	public List<UserCloudService> listPendings() throws Exception
	{
		List<UserCloudService> svcs = new ArrayList<UserCloudService>();
		Connection con = null;
		PreparedStatement pstmtSvcList = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtSvcList = con.prepareStatement( SQL_CLOUDSVC_LIST_PENDING );

			pstmtSvcList.clearParameters();

//			pstmtSvcList.setInt( 1, JdbcUserCloudServiceDAO.ExecutionStatusToInt( ExecutionStatus.READY ) );
//			pstmtSvcList.setInt( 2, JdbcUserCloudServiceDAO.ExecutionStatusToInt( ExecutionStatus.RUNNING ) );
//			pstmtSvcList.setInt( 3, JdbcUserCloudServiceDAO.ExecutionStatusToInt( ExecutionStatus.UNSTARTED ) );
//			pstmtSvcList.setInt( 4, JdbcUserCloudServiceDAO.ExecutionStatusToInt( ExecutionStatus.UNKNOWN ) );

			rs = pstmtSvcList.executeQuery();

			while ( rs.next() )
			{
				svcs.add(
					JdbcUserCloudServiceDAO.CreateSvcFromResultSet( rs )
				);
			}
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmtSvcList != null )
			{
				try { pstmtSvcList.close(); } catch (SQLException se) { /* ignore */ }
				pstmtSvcList = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return svcs;
	}

	//@} IUserCloudServiceDAO implementation

	/**
	 * Returns a UserCloudService object filled up with the given result set.
	 */
	protected static UserCloudService CreateSvcFromResultSet(final ResultSet rs) throws Exception
	{
		UserCloudService svc = new UserCloudService();
		svc.setId( rs.getInt(1) );
		svc.setUserId( rs.getInt(2) );
		svc.setName( rs.getString(3) );
		svc.setSubmissionDate( rs.getTimestamp(4) );
		svc.setStoppingDate( rs.getTimestamp(5) );
		svc.setSchedulerServiceId( rs.getInt(6) );
		svc.setStatus( JdbcUserCloudServiceDAO.IntToExecutionStatus( rs.getInt(7) ) );
		svc.setMiddlewareId( rs.getInt(8) );

		return svc;
	}

	/**
	 * Converts the given integer to the related execution status value.
	 */
	protected static ExecutionStatus IntToExecutionStatus(int status)
	{
		switch (status)
		{
			case 1:
				return ExecutionStatus.UNSTARTED;
			case 2:
				return ExecutionStatus.READY;
			case 3:
				return ExecutionStatus.STAGING_IN;
			case 4:
				return ExecutionStatus.RUNNING;
//			case 5:
//				return ExecutionStatus.STAGING_OUT;
			case 5:
				return ExecutionStatus.SUSPENDED;
//			case 6:
//				return ExecutionStatus.FINISHED;
			case 6:
				return ExecutionStatus.STOPPED;
			case 7:
				return ExecutionStatus.CANCELLED;
			case 8:
				return ExecutionStatus.FAILED;
			case 9:
				return ExecutionStatus.ABORTED;
			case 0:
			default:
				return ExecutionStatus.UNKNOWN;
		}
	}

	/**
	 * Converts the given execution status to the related integer value.
	 */
	protected static int ExecutionStatusToInt(ExecutionStatus status)
	{
		switch (status)
		{
			case ABORTED:
				return 9;
			case CANCELLED:
				return 7;
			case FAILED:
				return 8;
			case STOPPED:
				return 6;
			case READY:
				return 2;
			case RUNNING:
				return 4;
			case STAGING_IN:
				return 3;
//			case STAGING_OUT:
//				return 5;
			case SUSPENDED:
				return 5;
			case UNSTARTED:
				return 1;
			case UNKNOWN:
			default:
				return 0;
		}
	}
}
