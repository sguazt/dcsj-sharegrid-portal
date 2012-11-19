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

import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.fileupload.FileItem;

/**
 * Represent an uploaded file.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UploadedFile implements IUploadedFile, Serializable
{
	private transient FileItem fileItem;

	/** A constructor. */
	public UploadedFile(FileItem fileItem)
	{
		this.fileItem = fileItem;
	}

	//@{ IUloadedFile implementation

	public String getFileName()
	{
		return this.fileItem.getName();
	}

	public String getContentType()
	{
		return this.fileItem.getContentType();
	}

	public long getSize()
	{
		return this.fileItem.getSize();
	}

	public Object getOpaqueData()
	{
		return null;
	}

	public InputStream getInputStream() throws IOException
	{
		return this.fileItem.getInputStream();
	}

	public void dispose()
	{
		this.fileItem.delete();
	}

	//@} IUloadedFile implementation
}
