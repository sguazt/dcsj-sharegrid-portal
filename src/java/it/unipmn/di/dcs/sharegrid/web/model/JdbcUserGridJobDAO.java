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

import it.unipmn.di.dcs.common.conversion.Convert;
import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.grid.core.format.ExportFormatType;
import it.unipmn.di.dcs.grid.core.format.ImportFormatType;
import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJob;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Clob;
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
 * A job data store with persistence implemented via JDBC.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class JdbcUserGridJobDAO implements IUserGridJobDAO
{
	// SQL statements
	private static final String SQL_GRIDJOB_INS = "INSERT INTO sharegrid_usergridjob (userid,name,submitdate,stopdate,schedjid,status,middlewareid,expjob) VALUES (?,?,?,?,?,?,?,?)";
	private static final String SQL_GRIDJOB_UPD = "UPDATE sharegrid_usergridjob SET userid=?,name=?,submitdate=?,stopdate=?,schedjid=?,status=?,middlewareid=?,expjob=? WHERE id=?";
	private static final String SQL_GRIDJOB_DEL = "DELETE FROM sharegrid_usergridjob WHERE id=?";
	private static final String SQL_GRIDJOB_LIST = "SELECT id,userid,name,submitdate,stopdate,schedjid,status,middlewareid,expjob FROM sharegrid_usergridjob";
	private static final String SQL_GRIDJOB_LIST_BY_UID = SQL_GRIDJOB_LIST + " WHERE userid=?";
	//private static final String SQL_GRIDJOB_LIST_BY_STATUS = SQL_GRIDJOB_LIST + " WHERE ";
	//private static final String SQL_GRIDJOB_LIST_BY_UID_STATUS = SQL_GRIDJOB_LIST + " WHERE ";
	//private static final String SQL_GRIDJOB_LIST_PENDING = SQL_GRIDJOB_LIST + " WHERE stopdate IS NULL OR status IN (?,?,?,?)"; //FIXME: How the IN operator is SQL standard
	private static final String SQL_GRIDJOB_GET = SQL_GRIDJOB_LIST + " WHERE id=?";
	private static final String SQL_GRIDJOB_GET_BY_UID = SQL_GRIDJOB_LIST + " WHERE userid=?";
	private static final String SQL_GRIDJOB_GET_BY_NAME = SQL_GRIDJOB_LIST + " WHERE name=?";
	private static final String SQL_GRIDJOB_LIST_PENDING =	SQL_GRIDJOB_LIST
								+ " WHERE (stopdate IS NULL"
								+ " OR status=" + JdbcUserGridJobDAO.ExecutionStatusToInt( ExecutionStatus.READY )
								+ " OR status=" + JdbcUserGridJobDAO.ExecutionStatusToInt( ExecutionStatus.RUNNING )
								+ " OR status=" + JdbcUserGridJobDAO.ExecutionStatusToInt( ExecutionStatus.UNSTARTED )
								+ " OR status=" + JdbcUserGridJobDAO.ExecutionStatusToInt( ExecutionStatus.UNKNOWN )
								+ ")";
	private static final String SQL_GRIDJOB_LIST_PENDING_BY_MINAGE = SQL_GRIDJOB_LIST_PENDING + " AND (submitdate IS NULL OR DATEDIFF(CURRENT_TIMESTAMP(),submitdate)*86400 >= ?)";
	private static final String SQL_GRIDJOB_LIST_ID = "SELECT id FROM sharegrid_usergridjob";
	private static final String SQL_GRIDJOB_EXIST = "SELECT COUNT(*) FROM sharegrid_usergridjob WHERE id=?";

	private DataSource ds;
	private String jdbcClass;
	private String jdbcDsn;
	private String user;
	private String password;
	private Connection con;

	/**
	 * A constructor.
	 */
        public JdbcUserGridJobDAO(String jdbcClass, String jdbcDsn, String user, String password) throws Exception
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
	public JdbcUserGridJobDAO(DataSource ds) throws Exception
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

	//@{ IUserGridJobDAO implementation

	public Integer save(UserGridJob job) throws Exception
	{
		if ( job.getId() > 0 && this.exists( job.getId() ) )
		{
			this.update( job );
			return job.getId();
		}

		return this.insert( job );
	}

	public Integer insert(UserGridJob job) throws Exception
	{
		int id = -1;
		Connection con = null;
		PreparedStatement pstmtJobIns = null;
		ResultSet rs = null;

		try
		{
			// Note: the job id is an auto-increment

			con = this.getConnection();

			pstmtJobIns = con.prepareStatement( SQL_GRIDJOB_INS, Statement.RETURN_GENERATED_KEYS );

			pstmtJobIns.clearParameters();

			pstmtJobIns.setInt( 1, job.getUserId() );
			pstmtJobIns.setString( 2, job.getName() );
			if ( job.getSubmissionDate() != null )
			{
				pstmtJobIns.setTimestamp( 3, new Timestamp( job.getSubmissionDate().getTime() ) );
			}
			else
			{
				pstmtJobIns.setNull( 3, Types.TIMESTAMP );
			}
			if ( job.getStoppingDate() != null )
			{
				pstmtJobIns.setTimestamp( 4, new Timestamp( job.getStoppingDate().getTime() ) );
			}
			else
			{
				pstmtJobIns.setNull( 4, Types.TIMESTAMP );
			}
			pstmtJobIns.setString( 5, job.getSchedulerJobId() );
			if ( job.getStatus() != null )
			{
				pstmtJobIns.setInt( 6, JdbcUserGridJobDAO.ExecutionStatusToInt( job.getStatus() ) );
			}
			else
			{
				pstmtJobIns.setNull( 6, Types.INTEGER );
			}
			if ( job.getMiddlewareId() != null )
			{
				pstmtJobIns.setInt( 7, job.getMiddlewareId() );
			}
			else
			{
				pstmtJobIns.setNull( 7, Types.INTEGER );
			}
			if ( job.getRawJob() != null )
			{
				ByteArrayOutputStream baos = null;
				ObjectOutputStream oos = null;

				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				job.getRawJob().writeExternal(oos);
				pstmtJobIns.setString( 8, Convert.BytesToBase64( baos.toString().getBytes() ) );
				//pstmtJobIns.setClob( 8, job.toString() ); //FIXME: don't work
			}
			else
			{
				pstmtJobIns.setNull( 8, Types.VARCHAR );
				//pstmtJobIns.setNull( 8, Types.CLOB ); //FIXME: commented since CLOB seems not working
			}

			pstmtJobIns.executeUpdate();

			rs = pstmtJobIns.getGeneratedKeys();

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
			if ( pstmtJobIns != null )
			{
				try { pstmtJobIns.close(); } catch (SQLException se) { /* ignore */ }
				pstmtJobIns = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return id;
	}

	public void update(UserGridJob job) throws Exception
	{
		Connection con = null;
		PreparedStatement pstmtJobUpd = null;

		try
		{
			con = this.getConnection();

			pstmtJobUpd = con.prepareStatement( SQL_GRIDJOB_UPD );

			pstmtJobUpd.clearParameters();

			pstmtJobUpd.setInt( 1, job.getUserId() );
			pstmtJobUpd.setString( 2, job.getName() );
			if ( job.getSubmissionDate() != null )
			{
				pstmtJobUpd.setTimestamp( 3, new Timestamp( job.getSubmissionDate().getTime() ) );
			}
			else
			{
				pstmtJobUpd.setNull( 3, Types.TIMESTAMP );
			}
			if ( job.getStoppingDate() != null )
			{
				pstmtJobUpd.setTimestamp( 4, new Timestamp( job.getStoppingDate().getTime() ) );
			}
			else
			{
				pstmtJobUpd.setNull( 4, Types.TIMESTAMP );
			}
			pstmtJobUpd.setString( 5, job.getSchedulerJobId() );
			if ( job.getStatus() != null )
			{
				pstmtJobUpd.setInt( 6, JdbcUserGridJobDAO.ExecutionStatusToInt( job.getStatus() ) );
			}
			else
			{
				pstmtJobUpd.setNull( 6, Types.INTEGER );
			}

			if ( job.getMiddlewareId() != null )
			{
				pstmtJobUpd.setInt( 7, job.getMiddlewareId() );
			}
			else
			{
				pstmtJobUpd.setNull( 7, Types.INTEGER );
			}
			if ( job.getRawJob() != null )
			{
				ByteArrayOutputStream baos = null;
				ObjectOutputStream oos = null;

				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				job.getRawJob().writeExternal(oos);
				pstmtJobUpd.setString( 8, Convert.BytesToBase64( baos.toString().getBytes() ) );
				//pstmtJobUpd.setClob( 8, job.toString() ); //FIXME: don't work
			}
			else
			{
				pstmtJobUpd.setNull( 8, Types.VARCHAR );
				//pstmtJobUpd.setNull( 8, Types.CLOB ); //FIXME: commented since CLOB seems not working
			}
			pstmtJobUpd.setInt( 9, job.getId() );

			pstmtJobUpd.executeUpdate();
		}
		finally
		{
			if ( pstmtJobUpd != null )
			{
				try { pstmtJobUpd.close(); } catch (SQLException se) { /* ignore */ }
				pstmtJobUpd = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}
	}

	public UserGridJob load(Integer id) throws Exception
	{
		Connection con = null;
		PreparedStatement pstmtJobGet = null;
		ResultSet rs = null;
		UserGridJob job = null;

		try
		{
			con = this.getConnection();

			pstmtJobGet = con.prepareStatement( SQL_GRIDJOB_GET );

			pstmtJobGet.clearParameters();

			pstmtJobGet.setInt( 1, id );

			rs = pstmtJobGet.executeQuery();

			if ( rs.next() )
			{
				job = JdbcUserGridJobDAO.CreateJobFromResultSet( rs );
			}
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmtJobGet != null )
			{
				try { pstmtJobGet.close(); } catch (SQLException se) { /* ignore */ }
				pstmtJobGet = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return job;
	}

	public void remove(Integer id) throws Exception
	{
		Connection con = null;
		PreparedStatement pstmtJobDel = null;

		try
		{
			con = this.getConnection();

			pstmtJobDel = con.prepareStatement( SQL_GRIDJOB_DEL );

			pstmtJobDel.clearParameters();

			pstmtJobDel.setInt( 1, id );

			pstmtJobDel.executeUpdate();
		}
		finally
		{
			if ( pstmtJobDel != null )
			{
				try { pstmtJobDel.close(); } catch (SQLException se) { /* ignore */ }
				pstmtJobDel = null;
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
		PreparedStatement pstmtJobExist = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtJobExist = con.prepareStatement( SQL_GRIDJOB_EXIST );

			pstmtJobExist.clearParameters();

			pstmtJobExist.setInt( 1, id );

			rs = pstmtJobExist.executeQuery();

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
			if ( pstmtJobExist != null )
			{
				try { pstmtJobExist.close(); } catch (SQLException se) { /* ignore */ }
				pstmtJobExist = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return found;
	}

	public List<UserGridJob> list() throws Exception
	{
		List<UserGridJob> jobs = new ArrayList<UserGridJob>();
		Connection con = null;
		PreparedStatement pstmtJobList = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtJobList = con.prepareStatement( SQL_GRIDJOB_LIST );

			pstmtJobList.clearParameters();

			rs = pstmtJobList.executeQuery();

			while ( rs.next() )
			{
				jobs.add(
					JdbcUserGridJobDAO.CreateJobFromResultSet( rs )
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
			if ( pstmtJobList != null )
			{
				try { pstmtJobList.close(); } catch (SQLException se) { /* ignore */ }
				pstmtJobList = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return jobs;
	}

        public List<UserGridJob> listFromId(Integer[] ids) throws Exception
        {
                List<UserGridJob> jobs = new ArrayList<UserGridJob>();

                if ( ids.length > 0 )
                {
                        Connection con = null;
                        PreparedStatement pstmtJobList = null;
                        ResultSet rs = null;

                        try
                        {
                                con = this.getConnection();

                                //String params = null;
                                //params = "(" + Strings.Repeat( "?,", ids.length - 1 ) + "?)";
                                //pstmtJobList = con.prepareStatement( SQL_GRIDJOB_LIST + " WHERE id IN " + params ); //FIXME: how is IN operator SQL standard?

                                StringBuilder params = new StringBuilder();
				for (Integer id : ids)
				{
					if ( params.length() > 0 )
					{
						params.append( " OR " );
					}
					params.append( "id=?" );
				}

                                pstmtJobList = con.prepareStatement( SQL_GRIDJOB_LIST + " WHERE (" + params + ")" );

				pstmtJobList.clearParameters();

                                for (int i = 1; i <= ids.length; i++ )
                                {
                                        pstmtJobList.setInt( i, ids[i-1] );
                                }

                                rs = pstmtJobList.executeQuery();

                                while ( rs.next() )
                                {
                                        jobs.add(
                                                JdbcUserGridJobDAO.CreateJobFromResultSet( rs )
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
                                if ( pstmtJobList != null )
                                {
                                        try { pstmtJobList.close(); } catch (SQLException se) { /* ignore */ }
                                        pstmtJobList = null;
                                }
                                if ( con != null )
                                {
                                        try { con.close(); } catch (SQLException se) { /* ignore */ }
                                        con = null;
                                }
                        }
                }

                return jobs;
        }

	public List<Integer> idList() throws Exception
	{
		List<Integer> jobsId = new ArrayList<Integer>();
		Connection con = null;
		PreparedStatement pstmtJobIdList = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtJobIdList = con.prepareStatement( SQL_GRIDJOB_LIST_ID );

			pstmtJobIdList.clearParameters();

			rs = pstmtJobIdList.executeQuery();

			while ( rs.next() )
			{
				jobsId.add( rs.getInt(1) );
			}
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmtJobIdList != null )
			{
				try { pstmtJobIdList.close(); } catch (SQLException se) { /* ignore */ }
				pstmtJobIdList = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return jobsId;
	}

	public List<UserGridJob> listFromUser(int userId) throws Exception
	{
		List<UserGridJob> jobs = new ArrayList<UserGridJob>();
		Connection con = null;
		PreparedStatement pstmtJobList = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtJobList = con.prepareStatement( SQL_GRIDJOB_LIST_BY_UID );

			pstmtJobList.clearParameters();

			pstmtJobList.setInt( 1, userId );

			rs = pstmtJobList.executeQuery();

			while ( rs.next() )
			{
				jobs.add(
					JdbcUserGridJobDAO.CreateJobFromResultSet( rs )
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
			if ( pstmtJobList != null )
			{
				try { pstmtJobList.close(); } catch (SQLException se) { /* ignore */ }
				pstmtJobList = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return jobs;
	}

        public List<UserGridJob> listFromStatus(final ExecutionStatus[] status) throws Exception
	{
		List<UserGridJob> jobs = new ArrayList<UserGridJob>();

		if ( status.length > 0 )
		{
			Connection con = null;
			PreparedStatement pstmtJobList = null;
			ResultSet rs = null;

			try
			{
				con = this.getConnection();

				String params = null;
				params = "(" + Strings.Repeat( "?,", status.length - 1 ) + "?)";

				pstmtJobList = con.prepareStatement( SQL_GRIDJOB_LIST + " WHERE status IN " + params ); //TODO: how is IN operator SQL standard?

				pstmtJobList.clearParameters();

				for (int i = 1; i <= status.length; i++ )
				{
					pstmtJobList.setInt( i, JdbcUserGridJobDAO.ExecutionStatusToInt( status[i] ) );
				}

				rs = pstmtJobList.executeQuery();

				while ( rs.next() )
				{
					jobs.add(
						JdbcUserGridJobDAO.CreateJobFromResultSet( rs )
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
				if ( pstmtJobList != null )
				{
					try { pstmtJobList.close(); } catch (SQLException se) { /* ignore */ }
					pstmtJobList = null;
				}
				if ( con != null )
				{
					try { con.close(); } catch (SQLException se) { /* ignore */ }
					con = null;
				}
			}
		}

		return jobs;
	}

        public List<UserGridJob> listFromUserAndStatus(int userId, final ExecutionStatus[] status) throws Exception
	{
		List<UserGridJob> jobs = new ArrayList<UserGridJob>();

		if ( status.length > 0 )
		{
			Connection con = null;
			PreparedStatement pstmtJobList = null;
			ResultSet rs = null;

			try
			{
				con = this.getConnection();

				String params = null;
				params = "(" + Strings.Repeat( "?,", status.length - 1 ) + "?)";

				pstmtJobList = con.prepareStatement( SQL_GRIDJOB_LIST + " WHERE userid=? AND status IN " + params ); //TODO: how is IN operator SQL standard?

				pstmtJobList.clearParameters();

				pstmtJobList.setInt( 1, userId );

				for (int i = 1; i <= status.length; i++ )
				{
					pstmtJobList.setInt( i + 1, JdbcUserGridJobDAO.ExecutionStatusToInt( status[i] ) );
				}

				rs = pstmtJobList.executeQuery();

				while ( rs.next() )
				{
					jobs.add(
						JdbcUserGridJobDAO.CreateJobFromResultSet( rs )
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
				if ( pstmtJobList != null )
				{
					try { pstmtJobList.close(); } catch (SQLException se) { /* ignore */ }
					pstmtJobList = null;
				}
				if ( con != null )
				{
					try { con.close(); } catch (SQLException se) { /* ignore */ }
					con = null;
				}
			}
		}

		return jobs;
	}

	public List<UserGridJob> listPendings() throws Exception
	{
		List<UserGridJob> jobs = new ArrayList<UserGridJob>();
		Connection con = null;
		PreparedStatement pstmtJobList = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtJobList = con.prepareStatement( SQL_GRIDJOB_LIST_PENDING );

			pstmtJobList.clearParameters();

//			pstmtJobList.setInt( 1, JdbcUserGridJobDAO.ExecutionStatusToInt( ExecutionStatus.READY ) );
//			pstmtJobList.setInt( 2, JdbcUserGridJobDAO.ExecutionStatusToInt( ExecutionStatus.RUNNING ) );
//			pstmtJobList.setInt( 3, JdbcUserGridJobDAO.ExecutionStatusToInt( ExecutionStatus.UNSTARTED ) );
//			pstmtJobList.setInt( 4, JdbcUserGridJobDAO.ExecutionStatusToInt( ExecutionStatus.UNKNOWN ) );

			rs = pstmtJobList.executeQuery();

			while ( rs.next() )
			{
				jobs.add(
					JdbcUserGridJobDAO.CreateJobFromResultSet( rs )
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
			if ( pstmtJobList != null )
			{
				try { pstmtJobList.close(); } catch (SQLException se) { /* ignore */ }
				pstmtJobList = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return jobs;
	}

	public List<UserGridJob> listPendingsByMinAge(long minAge) throws Exception
	{
		List<UserGridJob> jobs = new ArrayList<UserGridJob>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmt = con.prepareStatement( SQL_GRIDJOB_LIST_PENDING_BY_MINAGE );

			pstmt.clearParameters();

			pstmt.setLong( 1, minAge );

			rs = pstmt.executeQuery();

			while ( rs.next() )
			{
				jobs.add(
					JdbcUserGridJobDAO.CreateJobFromResultSet( rs )
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

		return jobs;
	}

	//@} IUserGridJobDAO implementation

	/**
	 * Returns a UserGridJob object filled up with the given result set.
	 */
	protected static UserGridJob CreateJobFromResultSet(final ResultSet rs) throws Exception
	{
		UserGridJob job = new UserGridJob();
		job.setId( rs.getInt(1) );
		job.setUserId( rs.getInt(2) );
		job.setName( rs.getString(3) );
		job.setSubmissionDate( rs.getTimestamp(4) );
		job.setStoppingDate( rs.getTimestamp(5) );
		job.setSchedulerJobId( rs.getString(6) );
		job.setStatus( JdbcUserGridJobDAO.IntToExecutionStatus( rs.getInt(7) ) );
		job.setMiddlewareId( rs.getInt(8) );
//TODO
//		if ( !Strings.IsNullOrEmpty( rs.getString(9) ) )
//		{
//			ByteArrayInputStream bais = null;
//			ObjectInputStream ois = null;
//			IJob rawJob = null;
//			bais = new ByteArrayInputStream(
//				Convert.Base64ToBytes( rs.getString(9) )
//			);
//			ois = new ObjectInputStream(bais);
//			rawJob = new BotJob();//FIXME
//			rawJob.readExternal(iis);
//			job.setRawJob( rawJob );
//		}

		return job;
	}

	/**
	 * Converts the given integer to the related execution status value.
	 */
	protected static ExecutionStatus IntToExecutionStatus(int status)
	{
		switch (status)
		{
			case 1:
				return ExecutionStatus.ABORTED;
			case 2:
				return ExecutionStatus.CANCELLED;
			case 3:
				return ExecutionStatus.FAILED;
			case 4:
				return ExecutionStatus.FINISHED;
			case 5:
				return ExecutionStatus.READY;
			case 6:
				return ExecutionStatus.RUNNING;
			case 7:
				return ExecutionStatus.UNSTARTED;
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
				return 1;
			case CANCELLED:
				return 2;
			case FAILED:
				return 3;
			case FINISHED:
				return 4;
			case READY:
				return 5;
			case RUNNING:
				return 6;
			case UNSTARTED:
				return 7;
			case UNKNOWN:
			default:
				return 0;
		}
	}

	/**
	 * Converts the given integer to the related export format type value.
	 */
	protected static ImportFormatType IntToImportFormatType(int format)
	{
		switch (format)
		{
			case 1:
				return ImportFormatType.JDF;
			case 0:	
			default:
				return ImportFormatType.UNKNOWN;
		}
	}

	/**
	 * Converts the given export format type to the related integer value.
	 */
	protected static int ExportFormatTypeToInt(ExportFormatType format)
	{
		switch (format)
		{
			case JDF:
				return 1;
			case UNKNOWN:
			default:
				return 0;
		}
	}

	protected static String ClobToString(Clob clob)
	{
		Reader rd = null;
		StringWriter wr = null;
		String res = null;

		try
		{
			rd = clob.getCharacterStream();
			wr = new StringWriter();

			int buflen = 2048;
			char[] buf = new char[buflen];
			int nread = 0;
			while ( (nread = rd.read(buf, 0, buflen)) != -1 )
			{
				wr.write(buf, 0, nread);
			}
			res = wr.toString();
		}
		catch (Exception e)
		{
			// ignore
		}
		finally
		{
			if ( rd != null )
			{
				try { rd.close(); } catch (Exception e) { /* ignore */ }
				rd = null;
			}
			if ( wr != null )
			{
				try { wr.close(); } catch (Exception e) { /* ignore */ }
				wr = null;
			}
		}

		return res;
	}

//	protected static IJob ClobToJob(Clob clob, int fmtType)
//	{
//		switch (fmtType)
//		{
//			case ImportFormatType.JDF:
//				return new JdfImporter().importBotJob(
//					new StringReader(
//						JdbcUserGridJobDAO.ClobToString( clob )
//					)
//				);
//			case ImportFormatType.UNKNOWN:
//			default:
//				return null;
//		}
//	}
}
