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

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Checks for multipart HTTP requests and parses the multipart form data so
 * that all regular form fields.
 *
 * This filter should be definied in the web.xml file and accepts the following
 * parameters:
 * <ul>
 * <li><em>uploadMaxSize</em>: the maximum uploadable size (for a complete request).</li>
 * <li><em>uploadMaxFileSize</em>: the maximum uploadable size for a single uploaded file.</li>
 * <li><em>uploadThresholdSize</em>: the maximum size storable in the server
 * memory.</li>
 * <li><em>uploadRepositoryPath</em>: the path where storing uploaded files.</li>
 * </ul>
 * All parameters are optionals.
 *
 * Example:
 * <p>
 * <pre>
 * &lt;filter&gt;
 *   &lt;description&gt;
 *     Check for multipart HTTP requests and parse the multipart form data.
 *   &lt;/description&gt;
 *   &lt;filter-name&gt;MultipartFilter&lt;/filter-name&gt;
 *   &lt;filter-class&gt;it.unipmn.di.sharegrid.web.servlet.MultipartFilter&lt;/filter-class&gt;
 *   &lt;init-param&gt;
 *     &lt;description&gt;
 *       Sets the maximum file size of the uploaded file in bytes.
 *       Set to -1 to indicate an unlimited file size.
 *     &lt;/description&gt;
 *     &lt;param-name&gt;uploadMaxSize&lt;/param-name&gt;
 *     &lt;param-value&gt;1048576&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 *   &lt;filter-name&gt;MultipartFilter&lt;/filter-name&gt;
 *   &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 * </p>
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class MultipartFilter implements Filter
{
	private static final transient Logger Log = Logger.getLogger( MultipartFilter.class.getName() );

	public static final String DoFilterCalledAttr = MultipartFilter.class.getName() + ".DoFilterCalled";

	/** The overall maximum uploadable size. */
	private long maxSize;

	/** The maximum uploadable size for a single size. */
	private long maxFileSize;

	/** The size threshold beyond which files are written directly to disk. */
	private int thresholdSize;

	/** The path where storing uploaded files. */
	private String repositoryPath;

	//@{ Filter implementation

	public void init(FilterConfig filterConfig) throws ServletException
	{
		String param = null;

		// Look for 'uploadMaxSize'
		param = filterConfig.getInitParameter( ServletConstants.UploadMaxSizeParamName );
		if (param != null)
		{
			try
			{
				this.maxSize = ServletUtil.ParseSizeLong( param );
			}
			catch (Exception e)
			{
				//ServletException se = new ServletException();
				//se.initCause(e);
				//throw se;
				throw new ServletException("Init parameter '" + ServletConstants.UploadMaxSizeParamName + "' is not numeric.", e);
			}
		}
		else
		{
			this.maxSize = ServletConstants.DefaultUploadMaxSize;
		}

		// Look for 'uploadMaxFileSize'
		param = filterConfig.getInitParameter( ServletConstants.UploadMaxFileSizeParamName );
		if (param != null)
		{
			try
			{
				this.maxFileSize = ServletUtil.ParseSizeLong( param );
			}
			catch (Exception e)
			{
				throw new ServletException("Init parameter '" + ServletConstants.UploadMaxFileSizeParamName + "' is not numeric.", e);
			}
		}
		else
		{
			this.maxFileSize = ServletConstants.DefaultUploadMaxFileSize;
		}

		// Look for 'uploadThresholdSize'
		param = filterConfig.getInitParameter( ServletConstants.UploadThresholdSizeParamName );
		if (param != null)
		{
			try
			{
				this.thresholdSize = ServletUtil.ParseSizeInt( param );
			}
			catch (Exception e)
			{
				throw new ServletException("Init parameter '" + ServletConstants.UploadThresholdSizeParamName + "' is not numeric.", e);
			}
		}
		else
		{
			this.maxSize = ServletConstants.DefaultUploadThresholdSize;
		}

		// Look for 'uploadRepositoryPath'
		param = filterConfig.getInitParameter( ServletConstants.UploadRepositoryPathParamName );
		if (param != null)
		{
			this.repositoryPath = param;
		}
		else
		{
			this.repositoryPath = ServletConstants.DefaultUploadRepositoryPath;
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		if ( request.getAttribute( MultipartFilter.DoFilterCalledAttr ) != null )
		{
			chain.doFilter( request, response );
			return;
		}

		request.setAttribute( MultipartFilter.DoFilterCalledAttr, "true" );

		// sanity check
		if ( !(response instanceof HttpServletResponse) )
		{
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpReq = (HttpServletRequest) request;

		if ( !ServletFileUpload.isMultipartContent(httpReq) )
		{
			chain.doFilter(request, response);
			return;
		}

		// Wraps the current request into a multipart request object
		// for handling multipart form parameters.

		MultipartRequestWrapper reqWrapper = null;

		reqWrapper = new MultipartRequestWrapper(
			httpReq,
			this.maxSize,
			this.maxFileSize,
			this.thresholdSize,
			this.repositoryPath
		);

		chain.doFilter( reqWrapper, response );
	}

	public void destroy()
	{
		// empty
	}

	//@} Filter implementation
}
