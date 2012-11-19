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

package it.unipmn.di.dcs.sharegrid.web.portal.view;

import it.unipmn.di.dcs.common.format.SizeFormat;

import java.io.Serializable;

/**
 * Request bean for handling file upload.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UploadedFileInfoBean extends AbstractRequestBean implements Serializable
{
	private String name;
	private long size;
	private String type;
	private String md5;
	private String realPath;

	/** PROPERTY: name. */
	public String getName() { return this.name; }
	public void setName(String value) { this.name = value; }

	/** PROPERTY: size. */
	public long getSize() { return this.size; }
	public void setSize(long value) { this.size = value; }
 
	/** PROPERTY: prettySize. */
	public String getPrettySize() { return SizeFormat.FormatByte( (double) this.size ); }
 
	/** PROPERTY: type. */
	public String getType() { return this.type; }
	public void setType(String value) { this.type = value; }
 
	/** PROPERTY: md5. */
	public String getMd5() { return this.md5; }
	public void setMd5(String value) { this.md5 = value; }

	/** PROPERTY: realPath. */
	public String getRealPath() { return this.realPath; }
	public void setRealPath(String value) { this.realPath = value; }

	@Override
	public String toString()
	{
		return	"<Name: " + this.name
			+ ",Type: " + this.type
			+ ",Size: " + Long.toString(this.size)
			+ ",MD5: " + this.md5
			+ ">";
	}
}
