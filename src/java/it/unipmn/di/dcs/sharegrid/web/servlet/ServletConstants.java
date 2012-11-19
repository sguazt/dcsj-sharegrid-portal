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

/**
 * Container class for constants.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public final class ServletConstants
{
        public static final int UnlimitedSize = -1;
        public static final int OneKiloByte = 1024;
        public static final int OneMegaByte = OneKiloByte * 1024;
        public static final int OneGigaByte = OneMegaByte * 1024;

	public static final String UploadMaxSizeParamName = "uploadMaxSize";
        public static final long DefaultUploadMaxSize = OneMegaByte;

	public static final String UploadMaxFileSizeParamName = "uploadMaxFileSize";
        public static final long DefaultUploadMaxFileSize = OneMegaByte;

	public static final String UploadThresholdSizeParamName = "uploadThresholdSize";
        public static final int DefaultUploadThresholdSize = 10 * OneKiloByte;

	public static final String UploadRepositoryPathParamName = "uploadRepositoryPath";
        public static final String DefaultUploadRepositoryPath = System.getProperty("java.io.tmpdir");

        public static final String DefaultCharset = "UTF-8";

        public static final String FormUrlEncodedType = "application/x-www-form-urlencoded";
}
