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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

/**
 * A user data store with persistence implemented via JDBC.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class JdbcUserDAO implements IUserDAO
{
	////@{ FIXME: Parameterize!
	//private static final String JDBC_CLASS = "com.mysql.jdbc.Driver";
	//private static final String JDBC_DSN = "jdbc:mysql://localhost/sharegrid";
	////@} FIXME: Parameterize!

	// SQL statements
	private static final String SQL_USER_GET = "SELECT id,name,nickname,email,password,seclevel,accstatus,regdate,lastlogindate,language,tzoffset FROM sharegrid_user WHERE id=?";
	private static final String SQL_USER_GET_NICK = "SELECT id,name,nickname,email,password,seclevel,accstatus,regdate,lastlogindate,language,tzoffset FROM sharegrid_user WHERE nickname=?";
	private static final String SQL_USER_INS = "INSERT INTO sharegrid_user (name,nickname,email,password,seclevel,accstatus,regdate,lastlogindate,language,tzoffset) VALUES (?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_USER_UPD = "UPDATE sharegrid_user SET name=?,nickname=?,email=?,password=?,seclevel=?,accstatus=?,lastlogindate=?,language=?,tzoffset=? WHERE id=?";
	private static final String SQL_USER_DEL = "DELETE FROM sharegrid_user WHERE id=?";
	private static final String SQL_USER_LIST = "SELECT id,name,nickname,email,password,seclevel,accstatus,regdate,lastlogindate,language,tzoffset FROM sharegrid_user";
	private static final String SQL_USER_LIST_ID = "SELECT id FROM sharegrid_user";
	private static final String SQL_USER_EXIST = "SELECT COUNT(*) FROM sharegrid_user WHERE id=?";
	private static final String SQL_USER_EXIST_NICK = "SELECT COUNT(*) FROM sharegrid_user WHERE UPPER(nickname)=UPPER(?)";

	private DataSource ds;
	private String jdbcClass; // {@code null} if DataSource is used
	private String jdbcDsn; // {@code null} if DataSource is used
	private String user; // {@code null} if DataSource is used
	private String password; // {@code null} if DataSource is used
	private Connection con; // {@code null} if DataSource is used

	/**
	 * A constructor.
	 */
	public JdbcUserDAO(String jdbcClass, String jdbcDsn, String user, String password) throws Exception
	{
		// preconditions
		assert( jdbcClass != null && jdbcDsn != null );

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
	public JdbcUserDAO(DataSource ds) throws Exception
	{
		// preconditions
		assert( ds != null );

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
		Connection xcon = null;

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

				//this.init();
			}

			this.con.setAutoCommit( true );

			xcon = this.con;
		}
		else
		{
			// Get connection from DataSource

			try
			{
				xcon = this.ds.getConnection();
				xcon.setAutoCommit( true );
			}
			catch (Exception e)
			{
				if ( xcon != null )
				{
					try { xcon.close(); } catch (SQLException se) { /* ignore */ }
					xcon = null;
				}

				throw e;
			}
		}

		return xcon;
	}

	//@{ IUserDAO implementation

	public Integer save(User user) throws Exception
	{
		if ( user.getId() > 0 && this.existsUser( user.getId() ) )
		{
			this.updateUser( user );
			return user.getId();
		}

		return this.addUser( user );
	}

	public Integer insert(User user) throws Exception
	{
		int id = -1;
		Connection con = null;
		PreparedStatement pstmtUserIns = null;
		ResultSet rs = null;

		long nowTs = System.currentTimeMillis();
		final Date nowDt = new Date(nowTs);

		user.setRegistrationDate(nowDt);
		user.setLastLoginDate(nowDt);

		try
		{
			// Note: the user id is an auto-increment

			con = this.getConnection();

			pstmtUserIns = con.prepareStatement( SQL_USER_INS, Statement.RETURN_GENERATED_KEYS );

			pstmtUserIns.clearParameters();

			pstmtUserIns.setString( 1, user.getName() );
			pstmtUserIns.setString( 2, user.getNickname() );
			pstmtUserIns.setString( 3, user.getEmail() );
			pstmtUserIns.setString( 4, user.getPassword() );
			//pstmtUserIns.setInt( 5, user.getSecurityLevel().intValue() );
			pstmtUserIns.setInt( 5, user.getEffectiveSecurityLevel() );
			pstmtUserIns.setInt( 6, user.getAccountStatus().intValue() );
			pstmtUserIns.setTimestamp( 7, new Timestamp( user.getRegistrationDate().getTime() ) );
			pstmtUserIns.setTimestamp( 8, new Timestamp( user.getLastLoginDate().getTime() ) );
			pstmtUserIns.setString( 9, user.getLanguage() );
			pstmtUserIns.setInt( 10, user.getTimeZoneOffset() );

			pstmtUserIns.executeUpdate();

			rs = pstmtUserIns.getGeneratedKeys();

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
			if ( pstmtUserIns != null )
			{
				try { pstmtUserIns.close(); } catch (SQLException se) { /* ignore */ }
				pstmtUserIns = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return id;
	}

	public void update(User user) throws Exception
	{
		Connection con = null;
		PreparedStatement pstmtUserUpd = null;

		try
		{
			con = this.getConnection();

			pstmtUserUpd = con.prepareStatement( SQL_USER_UPD );

			pstmtUserUpd.clearParameters();

			pstmtUserUpd.setString( 1, user.getName() );
			pstmtUserUpd.setString( 2, user.getNickname() );
			pstmtUserUpd.setString( 3, user.getEmail() );
			pstmtUserUpd.setString( 4, user.getPassword() );
			//pstmtUserUpd.setInt( 5, user.getSecurityLevel() );
			pstmtUserUpd.setInt( 5, user.getEffectiveSecurityLevel() );
			pstmtUserUpd.setInt( 6, user.getAccountStatus().intValue() );
			pstmtUserUpd.setTimestamp( 7, new Timestamp( user.getLastLoginDate().getTime() ) );
			pstmtUserUpd.setString( 8, user.getLanguage() );
			pstmtUserUpd.setInt( 9, user.getTimeZoneOffset() );
			pstmtUserUpd.setInt( 10, user.getId() );

			pstmtUserUpd.executeUpdate();
		}
		finally
		{
			if ( pstmtUserUpd != null )
			{
				try { pstmtUserUpd.close(); } catch (SQLException se) { /* ignore */ }
				pstmtUserUpd = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}
	}

	public User load(Integer id) throws Exception
	{
		Connection con = null;
		PreparedStatement pstmtUserGet = null;
		ResultSet rs = null;
		User user = null;

		try
		{
			con = this.getConnection();

			pstmtUserGet = con.prepareStatement( SQL_USER_GET );

			pstmtUserGet.clearParameters();

			pstmtUserGet.setInt( 1, id );

			rs = pstmtUserGet.executeQuery();

			if ( rs.next() )
			{
				user = JdbcUserDAO.CreateUserFromResultSet( rs );
			}
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmtUserGet != null )
			{
				try { pstmtUserGet.close(); } catch (SQLException se) { /* ignore */ }
				pstmtUserGet = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return user;
	}

	public User load(LoginInfo login) throws Exception
	{
		Connection con = null;
		PreparedStatement pstmtUserGetFromNick = null;
		ResultSet rs = null;
		User user = null;

		try
		{
			con = this.getConnection();

			pstmtUserGetFromNick = con.prepareStatement( SQL_USER_GET_NICK );

			pstmtUserGetFromNick.clearParameters();

			pstmtUserGetFromNick.setString( 1, login.getNickname() );

			rs = pstmtUserGetFromNick.executeQuery();

			if ( rs.next() )
			{
				user = JdbcUserDAO.CreateUserFromResultSet( rs );
			}
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmtUserGetFromNick != null )
			{
				try { pstmtUserGetFromNick.close(); } catch (SQLException se) { /* ignore */ }
				pstmtUserGetFromNick = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return user;
	}

	public void remove(Integer id) throws Exception
	{
		Connection con = null;
		PreparedStatement pstmtUserDel = null;

		try
		{
			con = this.getConnection();

			pstmtUserDel = con.prepareStatement( SQL_USER_DEL );

			pstmtUserDel.clearParameters();

			pstmtUserDel.setInt( 1, id );

			pstmtUserDel.executeUpdate();
		}
		finally
		{
			if ( pstmtUserDel != null )
			{
				try { pstmtUserDel.close(); } catch (SQLException se) { /* ignore */ }
				pstmtUserDel = null;
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
		PreparedStatement pstmtUserExist = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtUserExist = con.prepareStatement( SQL_USER_EXIST );

			pstmtUserExist.clearParameters();

			pstmtUserExist.setInt( 1, id );

			rs = pstmtUserExist.executeQuery();

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
			if ( pstmtUserExist != null )
			{
				try { pstmtUserExist.close(); } catch (SQLException se) { /* ignore */ }
				pstmtUserExist = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return found;
	}

	public boolean exists(LoginInfo login) throws Exception
	{
		boolean found = false;
		Connection con = null;
		PreparedStatement pstmtUserExistNick = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtUserExistNick = con.prepareStatement( SQL_USER_EXIST_NICK );

			pstmtUserExistNick.clearParameters();

			pstmtUserExistNick.setString( 1, login.getNickname() );

			rs = pstmtUserExistNick.executeQuery();

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
			if ( pstmtUserExistNick != null )
			{
				try { pstmtUserExistNick.close(); } catch (SQLException se) { /* ignore */ }
				pstmtUserExistNick = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return found;
	}

	public List<User> list() throws Exception
	{
		List<User> users = new ArrayList<User>();
		Connection con = null;
		PreparedStatement pstmtUserList = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtUserList = con.prepareStatement( SQL_USER_LIST );

			pstmtUserList.clearParameters();

			rs = pstmtUserList.executeQuery();

			while ( rs.next() )
			{
				users.add(
					JdbcUserDAO.CreateUserFromResultSet( rs )
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
			if ( pstmtUserList != null )
			{
				try { pstmtUserList.close(); } catch (SQLException se) { /* ignore */ }
				pstmtUserList = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return users;
	}

	public List<Integer> idList() throws Exception
	{
		List<Integer> usersId = new ArrayList<Integer>();
		Connection con = null;
		PreparedStatement pstmtUserIdList = null;
		ResultSet rs = null;

		try
		{
			con = this.getConnection();

			pstmtUserIdList = con.prepareStatement( SQL_USER_LIST_ID );

			pstmtUserIdList.clearParameters();

			rs = pstmtUserIdList.executeQuery();

			while ( rs.next() )
			{
				usersId.add( rs.getInt(1) );
			}
		}
		finally
		{
			if ( rs != null )
			{
				try { rs.close(); } catch (SQLException se) { /* ignore */ }
				rs = null;
			}
			if ( pstmtUserIdList != null )
			{
				try { pstmtUserIdList.close(); } catch (SQLException se) { /* ignore */ }
				pstmtUserIdList = null;
			}
			if ( con != null )
			{
				try { con.close(); } catch (SQLException se) { /* ignore */ }
				con = null;
			}
		}

		return usersId;
	}

	public List<User> listFromId(Integer[] ids) throws Exception
	{
		List<User> users = new ArrayList<User>();

		if ( ids.length > 0 )
		{
			Connection con = null;
			PreparedStatement pstmtUserList = null;
			ResultSet rs = null;

			try
			{
				con = this.getConnection();

				String params = null;
				//params = "(" + Strings.Repeat( "?,", ids.length - 1 ) + "?)";
				params = "(id=?" + Strings.Repeat( " OR id=?", ids.length - 1 ) + ")";

				//pstmtUserList = con.prepareStatement( SQL_USER_LIST + " WHERE id IN " + params ); //FIXME: how is IN operator SQL standard?
				pstmtUserList = con.prepareStatement( SQL_USER_LIST + " WHERE " + params );

				pstmtUserList.clearParameters();

				for (int i = 1; i <= ids.length; i++ )
				{
					pstmtUserList.setInt( i, ids[i-1] );
				}

				rs = pstmtUserList.executeQuery();

				while ( rs.next() )
				{
					users.add(
						JdbcUserDAO.CreateUserFromResultSet( rs )
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
				if ( pstmtUserList != null )
				{
					try { pstmtUserList.close(); } catch (SQLException se) { /* ignore */ }
					pstmtUserList = null;
				}
				if ( con != null )
				{
					try { con.close(); } catch (SQLException se) { /* ignore */ }
					con = null;
				}
			}
		}

		return users;
	}

	//@} IUserDAO implementation

	@Deprecated
	public int saveUser(User user) throws Exception
	{
		return this.save(user);
	}

	@Deprecated
	public int addUser(User user) throws Exception
	{
		return this.insert(user);
	}

	@Deprecated
	public void updateUser(User user) throws Exception
	{
		this.update(user);
	}

	@Deprecated
	public User loadUser(int id) throws Exception
	{
		return this.load(id);
	}

	@Deprecated
	public User loadUser(LoginInfo login) throws Exception
	{
		return this.load(login);
	}

	@Deprecated
	public void removeUser(int id) throws Exception
	{
		this.remove(id);
	}

	@Deprecated
	public boolean existsUser(int id) throws Exception
	{
		return this.exists(id);
	}

	@Deprecated
	public boolean existsUser(LoginInfo login) throws Exception
	{
		return this.exists(login);
	}

	@Deprecated
	public List<User> users() throws Exception
	{
		return this.list();
	}

	@Deprecated
	public List<Integer> usersId() throws Exception
	{
		return this.idList();
	}

	protected static User CreateUserFromResultSet(final ResultSet rs) throws Exception
	{
		User user = new User();
		user.setId( rs.getInt(1) );
		user.setName( rs.getString(2) );
		user.setNickname( rs.getString(3) );
		user.setEmail( rs.getString(4) );
		user.setPassword( rs.getString(5) );
		//user.setSecurityLevel( SecurityLevels.fromInt( rs.getInt(6) ) );
		user.setEffectiveSecurityLevel( rs.getInt(6) );
		user.setAccountStatus( AccountStatus.fromInt( rs.getInt(7) ) );
		user.setRegistrationDate( rs.getTimestamp( 8 ) );
		user.setLastLoginDate( rs.getTimestamp( 9 ) );
		user.setLanguage( rs.getString( 10 ) );
		user.setTimeZoneOffset( rs.getInt( 11 ) );

		return user;
	}
}
