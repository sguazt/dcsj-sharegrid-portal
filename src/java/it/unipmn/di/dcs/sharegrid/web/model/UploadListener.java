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

import it.unipmn.di.dcs.common.annotation.Experimental;

import org.apache.commons.fileupload.ProgressListener;

/**
 * Monitor the progress of the uploaded file.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@Experimental
public class UploadListener implements ProgressListener
{
	private volatile long bytesRead;
	private volatile long contentLength;
	private volatile long item;   

	public UploadListener()
	{
		bytesRead = 0;
		contentLength = 0;
		item = 0;
	}

	public void update(long i_bytesRead, long i_contentLength, int i_item)
	{
		bytesRead = i_bytesRead;
		contentLength = i_contentLength;
		item = i_item;
	}

	public long getBytesRead()
	{
		return bytesRead;
	}

	public long getContentLength()
	{
		return contentLength;
	}
	public long getItem()
	{
		return item;
	}
}
