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

import it.unipmn.di.dcs.common.format.SizeFormat;
import it.unipmn.di.dcs.common.io.FileUtil;
import it.unipmn.di.dcs.common.io.IOUtil;
import it.unipmn.di.dcs.common.io.ZipUtil;
import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;
import it.unipmn.di.dcs.sharegrid.web.model.UserGridJob;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.MessageConstants;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.MessagesUtil;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;
import it.unipmn.di.dcs.sharegrid.web.service.IGridJobExecutionService;
import it.unipmn.di.dcs.sharegrid.web.service.ServiceFactory;

import java.util.ArrayList;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;

/**
 * Page bean for handling the user disk space.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UserStorageBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( UserStorageBean.class.getName() );

	/**
	 * Name of the request parameter representing the current path (relative to
	 * the user home) to be processed by the current action.
	 */
	private static final String PATHID_PARAM = "pid";

	/**
	 * Name of the request parameter representing the current job for which
	 * the base storage path is requested.
	 */
	private static final String JOBID_PARAM = "jid";

	/**
	 * Name of the request parameter representing the current file (relative to
	 * the user home) to be processed by the current action.
	 */
	private static final String FILEID_PARAM = "fid";

	private Map<String,UserStorageBean.UserFileInfoBean> filesMap = new HashMap<String,UserStorageBean.UserFileInfoBean>();

	//@{ Lifecycle /////////////////////////////////////////////////////////

	@Override
	public void init()
	{
		super.init();

		// Retrieve and set the user home directory
		String userHomePath;
		userHomePath =	UserFacade.instance().getUserBasePath(
					this.getSessionBean().getUser(),
					true
		);
		if (!Strings.IsNullOrEmpty(userHomePath))
		{
			this.setUserHomePathFile(
				new File(userHomePath)
			);
		}
		else
		{
			this.inError = true;
			ViewUtil.AddErrorMessage( MessageConstants.EXIST_KO, "User Home" );
			UserStorageBean.Log.log( Level.SEVERE, "Unable to get/create user home path." );
			return;
		}

		// Set the parent path of the current opened path.

		File parentPathFile = null;
		parentPathFile = this.getUserHomePathFile();

		if (
			this.getRequestParameterMap().containsKey( PATHID_PARAM )
			&& !Strings.IsNullOrEmpty( this.getRequestParameterMap().get( PATHID_PARAM ) )
		)
		{
			// Found parameter for current path

			String path = null;
			path = this.getRequestParameterMap().get( PATHID_PARAM ).trim();
			path =	this.getUserHomePathFile().getAbsolutePath()
				+ File.separator
				+ ( "..".equals(path) ? ".." : UserStorageBean.StripPath( path ) );

			// Make sure the requested path does exist
			File pathFile = new File( path );
			if ( pathFile.exists() )
			{
				this.setOpenedPathFile( pathFile );
				parentPathFile = pathFile.getParentFile();

//				if ( pathFile.equals( this.getUserHomePathFile() ) )
//				{
//					parentPathFile = pathFile;
//				}
//				else
//				{
//					// the current opened path is a plain
//					// file. So use as parentPath the
//					// parent path name.
//					parentPathFile = pathFile.getParentFile();
//				}
			}
			else
			{
				// fallback to the default path and warn the
				// user.

				this.setOpenedPathFile( this.getUserHomePathFile() );

				ViewUtil.AddWarnMessage( MessageConstants.NOT_EXIST_KO, this.getRequestParameterMap().get( PATHID_PARAM ) );
			}
		}
		else if (
			this.getRequestParameterMap().containsKey( JOBID_PARAM )
			&& !Strings.IsNullOrEmpty( this.getRequestParameterMap().get( JOBID_PARAM ) )
		)
		{
			// Found parameter for current job

			String jid = null;
			int jobId;
			String jobPath = null;
			IGridJobExecutionService jobSvc = null;
			UserGridJob job = null;

			jid = this.getRequestParameterMap().get( JOBID_PARAM ).trim();

			jobId = Integer.parseInt( jid );

			jobSvc = ServiceFactory.instance().gridJobExecutionService();

			try
			{
				job = jobSvc.getUserJob( jobId );
			}
			catch (Exception e)
			{
				this.inError = true;
				UserStorageBean.Log.log( Level.SEVERE, "Unable to load job '" + jid + "'.", e );
				ViewUtil.AddExceptionMessage( e );

				return;
			}

			jobSvc = null;

			// Security check: Does current user own the requested job?
			if ( job.getUserId() != this.getSessionBean().getUser().getId() )
			{
				this.inError = true;
				ViewUtil.AddErrorMessage( MessageConstants.AUTHORIZATION_KO, "Job ''" + jid + "'" );

				return;
			}

			jobPath = UserFacade.instance().getUserGridBaseLocalPath(
				this.getSessionBean().getUser(),
				job.getName(),
				false
			);

			if ( jobPath != null )
			{
				File jobPathFile = null;
				jobPathFile = new File( jobPath );
				this.setOpenedPathFile( jobPathFile );
				parentPathFile = jobPathFile.getParentFile();
			}
			else
			{
				this.inError = true;
				ViewUtil.AddErrorMessage( MessageConstants.EXIST_KO, "Job ''" + jid + "'" );
				return;
			}
		}
		else
		{
			this.setOpenedPathFile( this.getUserHomePathFile() );
			parentPathFile = null;
		}

		// Set the parent directory
		this.setParentPathFile( parentPathFile );

		// Set the current working directory
		if ( this.getOpenedPathFile().isDirectory() )
		{
			this.setCwdFile( this.getOpenedPathFile() );
		}
		else
		{
			if ( this.getParentPathFile() != null )
			{
				this.setCwdFile( this.getParentPathFile() );
			}
			else
			{
				this.setCwdFile( this.getUserHomePathFile() );
			}
		}
	}

	//@} Lifecycle /////////////////////////////////////////////////////////

	//@{ Properties ////////////////////////////////////////////////////////

	/** PROPERTY: pageNUmber. */
	private int pageNumber = 0;
	public void setPageNumber(int value) { this.pageNumber = value; }
	public int getPageNumber() { return this.pageNumber; }

	/**
	 * PROPERTY: inError.
	 *
	 * A flag indicating if an error is occurred ({@c true} value) or not
	 * ({@code false} value).
	 */
	private boolean inError = false;
	public boolean isInError() { return this.inError; }

	/**
	 * PROPERTY: homePathFile.
	 *
	 * A {@code File} object representing the ShareGrid user home directory.
	 */
	private File homePathFile;
	public File getUserHomePathFile() { return this.homePathFile; }
	protected void setUserHomePathFile(File value)
	{
		this.homePathFile = value;
		if ( value != null )
		{
			String fileId = Integer.toString( value.hashCode() );
			this.setUserHomePathInfo(
				new UserStorageBean.UserFileInfoBean(
					value.getAbsolutePath(),
					fileId,
					value,
					true,
					//value.equals( this.parentPathFile )
					false
				)
			);
		}
	}

	/**
	 * PROPERTY: parentPathFile.
	 *
	 * A {@code File} object representing the parent path of the current
	 * working directory.
	 */
	private File parentPathFile;
	public File getParentPathFile() { return this.parentPathFile; }
	protected void setParentPathFile(File value)
	{
		this.parentPathFile = value;
		if ( value != null )
		{
			String fileId = Integer.toString( value.hashCode() );
			this.setParentPathInfo(
				new UserStorageBean.UserFileInfoBean(
					this.getUserHomePathFile().getAbsolutePath(),
					fileId,
					value,
					//value.equals( this.homePathFile ),
					false,
					true
				)
			);
		}
	}

	/**
	 * PROPERTY: cwdFile.
	 *
	 * A {@code File} object representing the current working directory.
	 */
	private File cwdFile;
	public File getCwdFile() { return this.cwdFile; }
	public void setCwdFile(File value)
	{
		this.cwdFile = value;
		if ( value != null )
		{
			this.setCwdPath(
				"[" + MessagesUtil.GetString( MessageConstants.LABELS_BUNDLE_ID, "home" ) + "]"
				+ File.separator
				+ IOUtil.GetRelativePath(
					value,
					this.getUserHomePathFile()
				)
			);
			String fileId = Integer.toString( value.hashCode() );
			this.setCwdInfo(
				new UserStorageBean.UserFileInfoBean(
					this.getUserHomePathFile().getAbsolutePath(),
					fileId,
					value,
					value.equals( this.homePathFile ),
					value.equals( this.parentPathFile )
				)
			);
		}
	}

	/**
	 * PROPERTY: cwdPath.
	 *
	 * The current working directory.
	 */
	private String cwdPath;
	public String getCwdPath() { return this.cwdPath; }
	public void setCwdPath(String value) { this.cwdPath = value; }

	/**
	 * PROPERTY: openedPathFile.
	 *
	 * A {@code File} object representing the last opened file.
	 */
	private File openedPathFile;
	public File getOpenedPathFile() { return this.openedPathFile; }
	protected void setOpenedPathFile(File value) { this.openedPathFile = value; }

//	/** PROPERTY: parentPath. */
//	public String getParentPath() { return this.getParentPathFile().getAbsolutePath(); }
//	//protected void setParentPath(String value) { this.curPath = value; }
//
//	/** PROPERTY: parentPathName. */
//	public String getParentPathName() { return this.getParentPathFile().getName(); }

	/** PROPERTY: userHomeInfo. */
	private UserStorageBean.UserFileInfoBean userHomeInfo;
	public UserStorageBean.UserFileInfoBean getUserHomePathInfo() { return this.userHomeInfo; }
	public void setUserHomePathInfo(UserStorageBean.UserFileInfoBean value) { this.userHomeInfo = value; }

	/** PROPERTY: parentPathInfo. */
	private UserStorageBean.UserFileInfoBean parentPathInfo;
	public UserStorageBean.UserFileInfoBean getParentPathInfo() { return this.parentPathInfo; }
	public void setParentPathInfo(UserStorageBean.UserFileInfoBean value) { this.parentPathInfo = value; }

	/** PROPERTY: cwdInfo. */
	private UserStorageBean.UserFileInfoBean cwdInfo;
	public UserStorageBean.UserFileInfoBean getCwdInfo() { return this.cwdInfo; }
	public void setCwdInfo(UserStorageBean.UserFileInfoBean value) { this.cwdInfo = value; }

	/**
	 * PROPERTY: fileInfos.
	 *
	 * Returns a list of pairs: &lt;file-name,is-directory?&gt;
	 */
	//public List<UserFileInfoBean> getFiles()
	private List<UserStorageBean.UserFileInfoBean> fileInfos;
	public List<UserStorageBean.UserFileInfoBean> getFileInfos()
	{
//		synchronized (this) {
//		if (this.fileInfos == null) {

		//List<UserFileInfoBean> files = new ArrayList<UserFileInfoBean>();

		if ( this.filesMap == null || this.filesMap.isEmpty() )
		{
			this.fileInfos = null;
			File[] files = this.getCwdFile().listFiles();

			for (File child : this.getCwdFile().listFiles())
			{
				String fileId = Integer.toString( child.hashCode() );

				UserStorageBean.UserFileInfoBean fileInfo = null;
				fileInfo = new UserStorageBean.UserFileInfoBean(
					this.getUserHomePathFile().getAbsolutePath(),
					fileId,
					child,
					false,
					false
				);

				this.filesMap.put( fileId, fileInfo );

				//files.add( fileInfo );
			}
		}

		if ( this.fileInfos == null || this.fileInfos.isEmpty() )
		{
			this.fileInfos = new ArrayList<UserStorageBean.UserFileInfoBean>();
			// Add "home" path
			this.fileInfos.add( this.getUserHomePathInfo() );
			// Add "parent" path (unless it's located at an upper
			// level than the 'home' path in the directory tree)
			if (
				this.getParentPathFile() != null
				&& (
					! this.getUserHomePathFile().getAbsolutePath().startsWith( this.getParentPathFile().getAbsolutePath() )
					|| this.getUserHomePathFile().getAbsolutePath().length() >= this.getParentPathFile().getAbsolutePath().length()
				)
			) {
				this.fileInfos.add( this.getParentPathInfo() );
			}

			List<UserStorageBean.UserFileInfoBean> fileList = new ArrayList<UserStorageBean.UserFileInfoBean>( this.filesMap.values() );
			Collections.sort(
				fileList,
				new UserStorageBean.FileNameComparator()
			);
			this.fileInfos.addAll( fileList );
			//this.fileInfos.addAll( this.filesMap.values() );
		}
//		}
//		}

		return this.fileInfos;
	}

	//@} Properties ////////////////////////////////////////////////////////

	//@{ Actions ///////////////////////////////////////////////////////////

	/**
	 * Perform the action for downloading a specified file.
	 */
	public String doDownloadAction()
	{
		if ( this.isInError() )
		{
			return "download-ko";
		}

		if ( ! this.getRequestParameterMap().containsKey( FILEID_PARAM ) )
		{
			ViewUtil.AddErrorMessage( MessageConstants.REQUEST_PARAMETER_KO_NOTFOUND, "fid" );

			return "download-ko";
		}

		String fileId = this.getRequestParameterMap().get( FILEID_PARAM );

		if ( ! this.filesMap.containsKey( fileId ) )
		{
			ViewUtil.AddErrorMessage( MessageConstants.REQUEST_PARAMETER_KO_BADVALUE, "fid", fileId );

			return "download-ko";
		}

		UserFileInfoBean fileInfo = this.filesMap.get( fileId );

		try
		{
			// Get file information
			String path = this.getUserHomePathFile().getAbsolutePath() + File.separator + fileInfo.getRelativePath();
			File dlFile = new File( path );
			if ( !dlFile.exists() )
			{
				ViewUtil.AddErrorMessage( MessageConstants.FILE_DOWNLOAD_KO_NOTFOUND, path );
				return "download-ko";
			}
			path = dlFile.getAbsolutePath();
			String fileName = dlFile.getName();

			// Prepare the response object
			HttpServletResponse response = null;
			response = (HttpServletResponse) this.getFacesContext().getExternalContext().getResponse();
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			String mimeType = "application/octet-stream";
			response.setContentType(mimeType);

			// Write the file to the response.
			ServletOutputStream sos = response.getOutputStream();
			sos.write( this.getBytes( fileInfo.getInputStream() ) );
			sos.flush();

			// Complete the response
			this.getFacesContext().responseComplete();
		}
		catch (Exception e)
		{
			UserStorageBean.Log.log( Level.SEVERE, "Error while preparing download for file: " + fileInfo.getName(), e );
			ViewUtil.AddExceptionMessage( e );

			return "download-ko";
		}

		return "download-ok";
	}

	/**
	 * Perform the action for zipping and downloading a specified file.
	 */
	public String doZipAction()
	{
		if ( this.isInError() )
		{
			return "zip-ko";
		}

		if ( ! this.getRequestParameterMap().containsKey( FILEID_PARAM ) )
		{
			ViewUtil.AddErrorMessage( MessageConstants.REQUEST_PARAMETER_KO_NOTFOUND, "fid" );

			return "zip-ko";
		}

		String fileId = this.getRequestParameterMap().get( FILEID_PARAM );

		if ( ! this.filesMap.containsKey( fileId ) )
		{
			ViewUtil.AddErrorMessage( MessageConstants.REQUEST_PARAMETER_KO_BADVALUE, "fid", fileId );

			return "download-ko";
		}

		UserFileInfoBean fileInfo = this.filesMap.get( fileId );

		try
		{
			// Get file information
			String path = this.getUserHomePathFile().getAbsolutePath() + File.separator + fileInfo.getRelativePath();
			File dlFile = new File( path );
			if ( !dlFile.exists() )
			{
				ViewUtil.AddErrorMessage( MessageConstants.FILE_ZIP_KO_NOTFOUND, path );
				return "zip-ko";
			}
			path = dlFile.getAbsolutePath();
			String fileName = dlFile.getName();

			File zipFile = File.createTempFile( fileName, ".zip" );
			zipFile.deleteOnExit();
			String publicZipName = fileName + ".zip";

			ZipUtil.CreateArchive(
				zipFile.getAbsolutePath(),
				new String[] { path },
				9,
				new String[] { dlFile.getParent() }
			);

			// Prepare the response object
			HttpServletResponse response = (HttpServletResponse) this.getFacesContext().getExternalContext().getResponse();
			response.setHeader("Content-Disposition", "attachment; filename=\"" + publicZipName + "\"");
			String mimeType = "application/zip";
			response.setContentType(mimeType);

			// Write the file to the response.
			ServletOutputStream sos = response.getOutputStream();
			sos.write( this.getBytes( new FileInputStream( zipFile ) ) );
			sos.flush();

			// Complete the response
			this.getFacesContext().responseComplete();
		}
		catch (Exception e)
		{
			UserStorageBean.Log.log( Level.SEVERE, "Error while preparing zip for file: " + fileInfo.getName(), e );
			ViewUtil.AddExceptionMessage( e );

			return "zip-ko";
		}

		return "zip-ok";
	}

	/**
	 * Perform the action for deleting a given file.
	 */
	public String doDeleteAction()
	{
		if ( this.isInError() )
		{
			return "delete-ko";
		}

		if ( ! this.getRequestParameterMap().containsKey( FILEID_PARAM ) )
		{
			ViewUtil.AddErrorMessage( MessageConstants.REQUEST_PARAMETER_KO_NOTFOUND, "fid" );

			return "delete-ko";
		}

		String fileId = this.getRequestParameterMap().get( FILEID_PARAM );

		if ( ! this.filesMap.containsKey( fileId ) )
		{
			ViewUtil.AddErrorMessage( MessageConstants.REQUEST_PARAMETER_KO_BADVALUE, "fid", fileId );

			return "delete-ko";
		}

		UserFileInfoBean fileInfo = this.filesMap.get( fileId );

		try
		{
			// Get file information
			String path = this.getUserHomePathFile().getAbsolutePath() + File.separator + fileInfo.getRelativePath();
			File delFile = new File( path );
			if ( !delFile.exists() )
			{
				ViewUtil.AddErrorMessage( MessageConstants.FILE_DELETE_KO_NOTFOUND, fileInfo.getRelativePath() );
				return "delete-ko";
			}
			if ( !FileUtil.TryDelete( delFile.getAbsolutePath(), true ) )
			{
				ViewUtil.AddErrorMessage( MessageConstants.FILE_DELETE_KO, fileInfo.getRelativePath() );

				if ( !delFile.exists() )
				{
					// Remove the file from the files list
					if ( this.fileInfos != null )
					{
						this.fileInfos.remove( fileInfo );
					}
					// Remove the file from the files map
					this.filesMap.remove( fileId );
				}

				return "delete-ko";
			}

			// Remove the file from the files list
			if ( this.fileInfos != null )
			{
				this.fileInfos.remove( fileInfo );
			}
			// Remove the file from the files map
			this.filesMap.remove( fileId );

//
//			this.setCwdFile( this.getParentPathFile() );
//			this.setOpenedPathFile( this.getCwdFile() );
		}
		catch (Exception e)
		{
			UserStorageBean.Log.log( Level.SEVERE, "Error while deleting file: " + fileInfo.getName(), e );
			ViewUtil.AddExceptionMessage( e );

			return "delete-ko";
		}

		return "delete-ok";
	}

	/**
	 * Perform the action for opening a given file.
	 */
	public String doOpenAction()
	{
		if ( this.isInError() )
		{
			return "open-ko";
		}

this.setPageNumber(1);//FIXME
		return "open-ok";
	}

	//@} Actions ///////////////////////////////////////////////////////////

	protected static String StripPath(String path)
	{
		// For security reasons strip:
		// * 'foo/..'
		// * '../bar'
		// * 'foo/../bar'
		// * but not 'foo..bar'
		// for avoiding unauthorized access
		//
		return path.replaceAll(
			Pattern.quote( File.separator + ".." ), File.separator
		).replaceAll(
			Pattern.quote( ".." + File.separator ), File.separator
		);
	}

	protected  byte[] getBytes(InputStream is)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try
		{
			int count = 0;
			byte[] buffer = new byte[4096];
			while ((count = is.read(buffer)) != -1)
			{
				if (count > 0)
				{
					baos.write(buffer, 0, count);
				}
			}
		}
		catch (IOException ioe)
		{
			Log.log( Level.SEVERE, "Error while reading bytes,", ioe );
		}
		finally
		{
			try { is.close(); } catch (Exception e1) { /* ignore */ }
			is = null;
		}

		return baos.toByteArray();
	}

	public static class UserFileInfoBean
	{
		private String userHome;
		private String id;
		private File file;
		private boolean isHome;
		private boolean isParent;
		private String name;
		private String relativePath;
		private String icon;
		private InputStream is;
		private Date lastModifiedDate;

		public UserFileInfoBean(String basePath, String id, File file, boolean isHome, boolean isParent)
		{
			this.userHome = basePath;
			this.id = id;
			this.file = file;
			this.isHome = isHome;
			this.isParent = isParent;

			// Derived members

			if ( this.isHome() )
			{
				this.name = "[" + MessagesUtil.GetString( MessageConstants.LABELS_BUNDLE_ID, "home" ) + "]";
			}
			else if ( this.isParent() )
			{
				this.name = "..";
			}
			else
			{
				this.name = this.file.getName();
			}
			this.relativePath = IOUtil.GetRelativePath(
				this.file.getAbsolutePath(),
				this.userHome
			);
//			this.relativePath = this.file.getAbsolutePath().substring( this.userHome.length() );
//			if ( !Strings.IsNullOrEmpty(this.relativePath) && this.relativePath.charAt(0) == File.separatorChar )
//			{
//				this.relativePath = this.relativePath.substring(1);
//			}
			
//			if ( Strings.IsNullOrEmpty( this.relativePath ) )
//			{
//				this.relativePath = ".";
//			}
			// FIXME: Temporary workaround for setting the file icon.
			// FIXME>>This should be eliminated once the
			// FIXME>>choose-when-otherwise tag is ready to be used
			if ( file.isDirectory() )
			{
				if ( this.isHome() )
				{
					this.icon = "folder_home";
				}
				else if ( this.isParent() )
				{
					this.icon = "folder_parent";
				}
				else
				{
					this.icon = "folder";
				}
			}
			else
			{
				this.icon = "file";
			}

			this.lastModifiedDate = new Date( file.lastModified() );
		}

		public String getId() { return this.id; }

		public String getRelativePath() { return this.relativePath; }

		public String getName() { return this.name; }

		public boolean isDirectory() { return this.file.isDirectory(); }

		public long getSize() { return this.file.length(); }

		public String getPrettySize() { return SizeFormat.FormatByte( (double) this.getSize() ); }

		public String getIcon() { return this.icon; }

		public boolean isParent() { return this.isParent; }

		public boolean isHome() { return this.isHome; }

		public InputStream getInputStream()
		{
			if ( this.is == null )
			{
				try
				{
					this.is = new FileInputStream( this.file );
				}
				catch (Exception e)
				{
					UserStorageBean.Log.log( Level.WARNING, "Error: attempt to open inexistent file.", e );

					this.is = null;
				}
			}

			return this.is;
		}

		public String getType() { return "application/octet-stream"; }

		public Date getLastModifiedDate() { return this.lastModifiedDate; }

		@Override
		protected void finalize()
		{
			if ( this.is != null )
			{
				try { this.is.close(); } catch (Exception e) { /* ignore */ }
				this.is = null;
			}
		}
	}

	private static class FileNameComparator implements Comparator<UserStorageBean.UserFileInfoBean>
	{
		public int compare(UserStorageBean.UserFileInfoBean o1, UserStorageBean.UserFileInfoBean o2)
		{
			return o1.getName().compareTo( o2.getName() );
		}
	}
}
