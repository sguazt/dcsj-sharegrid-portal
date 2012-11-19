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

package it.unipmn.di.dcs.sharegrid.web.servlet;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;

import javax.faces.FacesException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.ServletContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
//import org.apache.commons.io.FileCleaner;
import org.apache.commons.io.FileCleaningTracker;

/**
 * An {@code HttpServletRequestWrapper} for handling multipart form request
 * issued when uploading a file.
 *
 * @see <a href="http://commons.apache.org/fileupload/using.html"Apache FileUpload></a>
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class MultipartRequestWrapper extends HttpServletRequestWrapper
{
	private static final transient Logger Log = Logger.getLogger( MultipartRequestWrapper.class.getName() );

	public static final String UploadedFilesAttr = MultipartRequestWrapper.class.getName() + ".UploadedFiles";

	/** The request parameters map. */
	private Map<String,String[]> params;

	/** The request file items map. */
	private Map<String,FileItem> fileItems;

	/** A constructor. */
	public MultipartRequestWrapper(HttpServletRequest request)
	{
		super( request );

		this.init(
			request,
			ServletConstants.DefaultUploadMaxSize,
			ServletConstants.DefaultUploadMaxFileSize,
			ServletConstants.DefaultUploadThresholdSize,
			ServletConstants.DefaultUploadRepositoryPath
		);
	}

	/** A constructor. */
	public MultipartRequestWrapper(HttpServletRequest request, long maxSize)
	{
		super( request );

		this.init(
			request,
			maxSize,
			ServletConstants.DefaultUploadMaxFileSize,
			ServletConstants.DefaultUploadThresholdSize,
			ServletConstants.DefaultUploadRepositoryPath
		);
	}

	/** A constructor. */
	public MultipartRequestWrapper(HttpServletRequest request, long maxSize, long maxFileSize)
	{
		super( request );

		this.init(
			request,
			maxSize,
			maxFileSize,
			ServletConstants.DefaultUploadThresholdSize,
			ServletConstants.DefaultUploadRepositoryPath
		);
	}

	/** A constructor. */
	public MultipartRequestWrapper(HttpServletRequest request, long maxSize, long maxFileSize, int thresholdSize)
	{
		super( request );

		this.init(
			request,
			maxSize,
			maxFileSize,
			thresholdSize,
			ServletConstants.DefaultUploadRepositoryPath
		);
	}

	/** A constructor. */
	public MultipartRequestWrapper(HttpServletRequest request, long maxSize, long maxFileSize, int thresholdSize, String repositoryPath)
	{
		super( request );

		this.init(
			request,
			maxSize,
			maxFileSize,
			thresholdSize,
			repositoryPath
		);
	}

	/**
	 * Hide the content type so that no one tries to re-download the
	 * uploaded files.
	 */
	@Override
	public String getContentType()
	{
		return ServletConstants.FormUrlEncodedType;
	}

	@Override
	public String getParameter(String key)
	{
		// preconditions
		assert( key != null );

		String[] values = this.params.get(key);

		return	( values != null )
			? values[0]
			: null;
	}

	@Override
	public Enumeration<String> getParameterNames()
	{
		return Collections.enumeration( this.params.keySet() );
	}

	@Override
	public String[] getParameterValues(String key)
	{
		// preconditions
		assert( key != null );

		return this.params.get(key);
	}

	@Override
	public Map<String,String[]> getParameterMap()
	{
		return Collections.unmodifiableMap( this.params );
	}

	@Override
	public Object getAttribute(String name)
	{
		if ( name.equals( MultipartRequestWrapper.UploadedFilesAttr ) )
		{
			return this.getFileItems();
		}
		return super.getAttribute(name);
	}

	public Map<String,FileItem> getFileItems()
	{
		return	this.fileItems;
	}

	/** Returns the <code>FileItem</code> associated to the given key. */
	public FileItem getFileItem(String key)
	{
		// preconditions
		assert( key != null );

		return	( this.fileItems != null )
			? this.fileItems.get(key)
			: null;
	}

	/** Performs initializations stuff. */
	@SuppressWarnings("unchecked") // ServletFileUpload#parseRequest() does not return generic type.
	private void init(HttpServletRequest request, long maxSize, long maxFileSize, int thresholdSize, String repositoryPath)
	{
		if ( !ServletFileUpload.isMultipartContent(request) )
		{
			String errorText = "Content-Type is not multipart/form-data but '" + request.getContentType() + "'";

			MultipartRequestWrapper.Log.severe(errorText);

			throw new FacesException(errorText);
		}
		else
		{
			this.params = new HashMap<String,String[]>();
			this.fileItems = new HashMap<String,FileItem>();

			DiskFileItemFactory factory = null;
			ServletFileUpload upload = null;

			//factory = new DiskFileItemFactory();
			factory = MultipartRequestWrapper.CreateDiskFileItemFactory(
				request.getSession().getServletContext(),
				thresholdSize,
				new File(repositoryPath)
			);
			//factory.setRepository(new File(repositoryPath));
			//factory.setSizeThreshold(thresholdSize);

			upload = new ServletFileUpload(factory);
			upload.setSizeMax(maxSize);
			upload.setFileSizeMax(maxFileSize);

			String charset = request.getCharacterEncoding();
			if ( charset != null )
			{
				charset = ServletConstants.DefaultCharset;
			}
			upload.setHeaderEncoding( charset );

			List<FileItem> itemList = null;
			try
			{
				//itemList = (List<FileItem>) upload.parseRequest(request);
				itemList = (List<FileItem>) upload.parseRequest(request);
			}
			catch (FileUploadException fue)
			{
				MultipartRequestWrapper.Log.severe( fue.getMessage() );
				throw new FacesException(fue);
			}
			catch (ClassCastException cce)
			{
				// This shouldn't happen!
				MultipartRequestWrapper.Log.severe( cce.getMessage() );
				throw new FacesException(cce);
			}

			MultipartRequestWrapper.Log.fine("parametercount = " + itemList.size());

			for (FileItem item : itemList)
			{
				String key = item.getFieldName();

//				{
//					String value = item.getString();
//					if (value.length() > 100) {
//						value = value.substring(0, 100) + " [...]";
//					}
//					MultipartRequestWrapper.Log.fine(
//						"Parameter : '" + key + "'='" + value + "' isFormField="
//						+ item.isFormField() + " contentType='" + item.getContentType() + "'"
//					);
//				}
				if ( item.isFormField() )
				{
					Object inStock = this.params.get(key);
					if ( inStock == null )
					{
						String[] values = null;
						try
						{
							// TODO: enable configuration of  'accept-charset'
							values = new String[] { item.getString(ServletConstants.DefaultCharset) };
						}
						catch (UnsupportedEncodingException uee)
						{
							MultipartRequestWrapper.Log.warning("Caught: " + uee);

							values = new String[] { item.getString() };
						}
						this.params.put(key, values);
					}
					else if ( inStock instanceof String[] )
					{
						// two or more parameters
						String[] oldValues = (String[]) inStock;
						String[] values = new String[ oldValues.length + 1 ];

						int i = 0;
						while ( i < oldValues.length )
						{
							values[i] = oldValues[i];
							i++;
						}

						try
						{
							// TODO: enable configuration of  'accept-charset'
							values[i] = item.getString(ServletConstants.DefaultCharset);
						}
						catch (UnsupportedEncodingException uee)
						{
							MultipartRequestWrapper.Log.warning("Caught: " + uee);

							values[i] = item.getString();
						}
						this.params.put(key, values);
					}
					else
					{
						MultipartRequestWrapper.Log.severe( "Program error. Unsupported class: " + inStock.getClass().getName() );
					}
				}
				else
				{
					//String fieldName = item.getFieldName();
					//String fileName = item.getName();
					//String contentType = item.getContentType();
					//boolean isInMemory = item.isInMemory();
					//long sizeInBytes = item.getSize();
					//...
					this.fileItems.put(key, item);
				}
			}
		}
	}

	/** Creates an instance of DiskFileItemFactory and set the clean-up manager. */
	private static DiskFileItemFactory CreateDiskFileItemFactory(ServletContext context, int thresholdSize, File repository)
	{
		FileCleaningTracker fileCleaningTracker = null;
		fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(context);

		DiskFileItemFactory factory = null;
		factory = new DiskFileItemFactory(
			//fileCleaningTracker,
			thresholdSize,
			repository
		);
		factory.setFileCleaningTracker( fileCleaningTracker );

		return factory;
	}
}
