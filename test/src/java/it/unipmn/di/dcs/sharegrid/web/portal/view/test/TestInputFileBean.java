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

package it.unipmn.di.dcs.sharegrid.web.portal.view.test;

import it.unipmn.di.dcs.sharegrid.web.model.IUploadedFile;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.servlet.ServletContext;

/**
 * Page-Bean for testing the {@c inputFile} tag.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class TestInputFileBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( TestInputFileBean.class.getName() );

	/** PROPERTY: uploadedFile. */
	private IUploadedFile uploadedFile;
	public IUploadedFile getUploadedFile()
	{
		Log.info( "[TestInputFileBean::getUploadedFile>> returning Uploaded File." );
		return this.uploadedFile;
	}
	public void setUploadedFile(IUploadedFile value)
	{
		Log.info( "[TestInputFileBean::setUploadedFile>> setting Uploaded File." );
		this.uploadedFile = value;
	}

	/** PROPERTY: fileUploaded. */
	private boolean fileUploaded = false;
	public boolean isFileUploaded()
	{
		return this.fileUploaded;
	}
	public void setFileUploaded(boolean value)
	{
		this.fileUploaded = value;
	}

	public String doUploadAction()
	{
		boolean allOk;
		FacesMessage msg = null;

		allOk = this.uploadFile();
		if ( allOk )
		{
			msg = new FacesMessage( "Upload OK");
		}
		else
		{
			msg = new FacesMessage( "Upload KO");
		}
		this.getFacesContext().addMessage( null, msg );

		// This propery is true even if there was an error during
		// the upload
		this.setFileUploaded( true );

		return allOk ? "upload-ok" : "upload-ko";
	}

	protected boolean uploadFile()
	{
		boolean allOk = false;

		if ( this.getUploadedFile() == null )
		{
			return false;
		}

		String uploadedFileName = null;
		String justFileName = null;
		int index;

		uploadedFileName = this.getUploadedFile().getFileName();
		index = uploadedFileName.lastIndexOf('/');
		if ( index >= 0 )
		{
			justFileName = uploadedFileName.substring( index + 1 );
		}
		else
		{
			// try backslash
			index = uploadedFileName.lastIndexOf('\\');
			if ( index >= 0 )
			{
				justFileName = uploadedFileName.substring( index + 1 );
			}
			else
			{
				// No forward or back slashes
				justFileName = uploadedFileName;
			}
		}

		OutputStream os = null;
		InputStream is = null;

		try
		{
			// get the path to the "upload" directory
			// from the servlet context
			ServletContext svlCtx = (ServletContext) this.getFacesContext().getExternalContext().getContext();

			String realPath = null;
			realPath = System.getProperty("java.io.tmpdir") + File.separator + justFileName;

			is = new BufferedInputStream(
				this.getUploadedFile().getInputStream()
			);

			os = new BufferedOutputStream(
				new FileOutputStream(
					realPath
				)
			);

			int buflen = 4096;
			int nread = 0;
			byte[] buf = new byte[ buflen ];
			while ( ( nread = is.read( buf ) ) != -1 )
			{
				if ( nread > 0 )
				{
					os.write( buf, 0, nread );
				}
			}
			os.flush();

			//this.setFileUploaded( true );

			allOk = true;
		}
		catch (Exception e)
		{
			Log.log( Level.SEVERE, "Unable to upload file: " + this.getUploadedFile().getFileName(), e );
		}
		finally
		{
			if ( os != null )
			{
				try { os.close(); } catch (IOException e) { /* ignore */ }
				os = null;
			}
			if ( is != null )
			{
				try { is.close(); } catch (IOException e) { /* ignore */ }
				is = null;
			}
		}

		return allOk;
	}
}
