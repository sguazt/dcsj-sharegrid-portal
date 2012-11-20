/*
 * Copyright (C) 2008-2010  Distributed Computing System (DCS) Group, Computer
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpUtils;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

/**
 * File download servlet.
 *
 * This servlet should replace the actual file download method used in the user
 * space area.
 * Indeed, the actual method passes through the JSF and RichFaces servlet phases
 * which are very memory hungry.
 * By using a different servlet we should reduce the probability the a Java
 * "Out of Memory" exception will be raised during the file download.
 *
 * Configure this server in your web.xml file.
 * You can provide the following parameters for this servlet:
 * - root-path: Specifies the name of the directory where to search for the
 *   file.
 *   Default value is the root directory of your container.
 * - compress: Enable/disable data compression during the downloading.
 *   Possible values are true and false.
 *   Default value is false
 * - auto-mime: Enable/disable mime type detection.
 *   Possible values are true and false. If this value is true than servlet will
 *   set mime type according to the file extension.
 *   Default value is false (mime type is application/octet-stream).
 * - error-page: lets you describe some URL users requests will be redirected to
 *   in case of missed files. If this parameter starts with http:// than servlet
 *   redirects the request. Otherwise servlet assumes the local resource and
 *   request will be forwarded.
 *   Default value is the empty string (the standard error message will be
 *   provided).
 * - max-size: Maximum size (in bytes) allowed to download.
 *   Default value is the whole file size.
 * - disposition: Enable/disable Content-disposition header.
 *   This header lets you use file name during the downloading.
 *   You may disable it for the mobile downloading, since some phones refuse to
 *   accept content when using it. 
 *   Possible values are true or false.
 *   Default value is true.
 * .
 *
 * \todo Still need some work.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class DownloadServlet extends HttpServlet
{
	private static final String ROOT_PATH_PARAM_NAME_ = "root-path";
	private static final String COMPRESS_PARAM_NAME_ = "compress";
	private static final String AUTO_MIME_PARAM_NAME_ = "auto-mime";
	private static final String ERROR_PAGE_PARAM_NAME_ = "error-page";
	private static final String MAX_SIZE_PARAM_NAME_ = "max-size";
	private static final String DISPOSITION_PARAM_NAME_ = "disposition";
	private static final String URL_SEPARATOR_ = "/";

	private ServletContext context_;
	private String file_separator_;
	private boolean dispatch_error_;
	private String root_path_;
	private boolean compress_;
	private String mime_;
	private String error_page_;
	private long max_size_;
	private boolean disposition_;


    public DownloadServlet()
    {
		file_separator_ = System.getProperty("file.separator");
		dispatch_error_ = false;
		root_path_ = ".";
		compress_ = false;
		auto_mime_ = null;
		error_page_ = null;
		max_size_ = -1L;
		disposition_ = true;
    }


	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		context_ = config.getServletContext();

		String param;

		// Read 'root-path' parameter
		param = getInitParameter(ROOT_PATH_PARAM_NAME_);
		if (param != null)
		{
			if (!param.endsWith(file_separator_) && !param.endsWith(URL_SEPARATOR_))
			{
				param = (new StringBuilder()).append(param).append(file_separator_).toString();
			}
			root_path_ = param;
		}

		// Read 'compress' parameter
		param = getInitParameter(COMPRESS_PARAM_NAME_);
		if (param != null)
		{
			compress_ = parseBoolean(param);
		}

		// Read 'auto-mime' parameter
		param = getInitParameter(AUTO_MIME_PARAM_NAME_);
		if (param != null)
		{
			auto_mime_ = parseBoolean(param);
		}

		// Read 'error-page' parameter
		param = getInitParameter(ERROR_PAGE_PARAM_NAME_);
		if(param != null)
		{
			if (!param.startsWith("http://"))
			{
				dispatch_error_ = true;
			}
			error_page_ = param;
		}

		// Read 'max-size' parameter
		param = getInitParameter(MAX_SIZE_PARAM_NAME_);
		if (param != null)
		{
			try
			{
				max_size_ = Long.parseLong(param);
			}
			catch (NumberFormatException nfe)
			{
				max_size_ = -1L;
			}
		}

		// Read 'disposition' parameter
		param = getInitParameter(DISPOSITION_PARAM_NAME_);
		if (param != null)
		{
			disposition_ = parseBoolean(param);
		}
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request, response);
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String url = request.getRequestURL().toString();
		int ix = url.indexOf('?');
		if (ix > 0)
		{
			url = url.substring(0, ix);
		}

		String file_name = request.getQueryString();
		if (file_name == null)
		{
			file_name = "";
		}
		else
		{
			file_name = decode(file_name);
		}
		if (file_name.length() == 0)
		{
			if (error_page_ != null)
			{
				if (dispatch_error_)
				{
					RequestDispatcher dispatcher = getServletConfig().getServletContext().getRequestDispatcher(error_page_);
					dispatcher.forward(request, response);
				}
				else
				{
					response.sendRedirect(response.encodeRedirectURL(error_page_));
				}
			}
			else
			{
				response.setContentType("text/html");
				PrintWriter pwr = response.getWriter();
				pwr.println("<html><body>");
				pwr.println("<h1>Could not get file name.</h1>");
				pwr.println("</body></html>");
				pwr.flush();
				pwr.close();
			}
			return;
		}
		if (file_name.indexOf((new StringBuilder()).append("..").append(file_separator_).toString()) >= 0)
		{
			if (error_page_ != null)
			{
				if (dispatch_error_)
				{
					RequestDispatcher dispatcher = getServletConfig().getServletContext().getRequestDispatcher(error_page_);
					dispatcher.forward(request, response);
				}
				else
				{
					response.sendRedirect(response.encodeRedirectURL(error_page_));
				}
			}
			else
			{
				response.setContentType("text/html");
				PrintWriter pwr = response.getWriter();
				pwr.println("<html><body>");
				pwr.println("<h1>Could not use file name " + file_name + ".</h1>");
				pwr.println("<h2>Security restrictions.</h2>");
				pwr.println("</body></html>");
				pwr.flush();
				pwr.close();
			}
			return;
		}

		File file = lookupFile((new StringBuilder()).append(root_path_).append(file_name).toString());
		if (file == null)
		{
			if (error_page_ != null)
			{
				if (dispatch_error_)
				{
					RequestDispatcher dispatcher = getServletConfig().getServletContext().getRequestDispatcher(error_page_);
					dispatcher.forward(request, response);
				}
				else
				{
					response.sendRedirect(response.encodeRedirectURL(error_page_));
				}
			}
			else
			{
				response.setContentType("text/html");
				PrintWriter pwr = response.getWriter();
				pwr.println("<html><body><h1>");
				pwr.println((new StringBuilder()).append("Could not read file ").append(qs).toString());
				pwr.println("</h1></body></html>");
				pwr.flush();
				pwr.close();
			}
			return;
		}
		if (!file.exists() || !file.canRead())
		{
			if (error_page_ != null)
			{
				if (dispatch_error_)
				{
					RequestDispatcher dispatcher = getServletConfig().getServletContext().getRequestDispatcher(error_page_);
					dispatcher.forward(request, response);
				}
				else
				{
					response.sendRedirect(response.encodeRedirectURL(error_page_));
				}
			}
			else
			{
				response.setContentType("text/html");
				PrintWriter pwr = response.getWriter();
				pwr.println("<html><body><h1>");
				pwr.println((new StringBuilder()).append("<br><br><br>Could not read file ").append(s1).toString());
				if (!file.exists())
				{
					pwr.println(": File does not exist.");
				}
				else
				{
					pwr.println(": File is not readable.");
				}
				pwr.println("</h1></body></html>");
				pwr.flush();
				pwr.close();
			}
			return;
		}

		String fullFileName = (new StringBuilder()).append(root_path_).append(file_name).toString();
		if (compress_)
		{
			String enc = request.getHeader("Accept-Encoding");
			boolean can_compress = false;
			String compress_method = null;
			if (enc != null)
			{
				if (enc.indexOf("gzip") >= 0)
				{
					can_compress = true;
					compress_method = "gzip";
				}
			}
			if (can_compress)
			{
				response.setHeader("Content-Encoding", compress_method);
				if (disposition_)
				{
					response.setHeader("Content-Disposition", (new StringBuilder()).append("attachment;filename=\"").append(file_name).append("\"").toString());
				}
				ServletOutputStream sos = response.getOutputStream();
				GZIPOutputStream gos = new GZIPOutputStream(sos);
				dumpFile(fullFileName, gos, max_size_);
				gos.close();
				sos.close();
			}
		}
		else
		{
			if (auto_mime_)
			{
				response.setContentType(getMimeType(file_name));
			}
			else
			{
				response.setContentType("application/octet-stream");
			}

			if (disposition_)
			{
				response.setHeader("Content-Disposition", (new StringBuilder()).append("attachment;filename=\"").append(file_name).append("\"").toString());
			}
			response.setContentLength((int)file.length());
			ServletOutputStream sos = response.getOutputStream();
			dumpFile(fullFileName, sos, max_size_);
			//sos.flush();
			sos.close();
		}
	}


	private String getFromQuery(String s, String s1)
	{
		if (s == null)
		{
			return "";
		}
		int i = s.indexOf(s1);
		if (i < 0)
		{
			return "";
		}
		String s2 = s.substring(i + s1.length());
		if ((i = s2.indexOf("&")) < 0)
		{
			return s2;
		}
		else
		{
			return s2.substring(0, i);
		}
	}


	private void dumpFile(String fileName, OutputStream os, long maxSize)
	{
		int buf_size = 4096;
		byte buf[] = new byte[buf_size];
		long size = 0L;
		try
		{
			InputStream is = new BufferedInputStream(new FileInputStream(lookupFile(fileName)));
			do
			{
				int nread;
				if ((nread = is.read(buf, 0, buf_size)) != -1)
				{
					os.write(buf, 0, i);
				}
				size += nread;
			} while(l1 < l);
			is.close();
		}
		catch(IOException ioe)
		{	
		}
	}


	private String decode(String s)
	{
		StringBuffer stringbuffer = new StringBuffer(0);
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if(c == '+')
			{
				stringbuffer.append(' ');
				continue;
			}
			if(c == '%')
			{
				char c1 = '\0';
				for(int j = 0; j < 2; j++)
				{
					c1 *= '\020';
					c = s.charAt(++i);
					if(c >= '0' && c <= '9')
					{
						c1 += c - 48;
						continue;
					}
					if((c < 'A' || c > 'F') && (c < 'a' || c > 'f'))
						break;
					switch(c)
					{
					case 65: // 'A'
					case 97: // 'a'
						c1 += '\n';
						break;

					case 66: // 'B'
					case 98: // 'b'
						c1 += '\013';
						break;

					case 67: // 'C'
					case 99: // 'c'
						c1 += '\f';
						break;

					case 68: // 'D'
					case 100: // 'd'
						c1 += '\r';
						break;

					case 69: // 'E'
					case 101: // 'e'
						c1 += '\016';
						break;

					case 70: // 'F'
					case 102: // 'f'
						c1 += '\017';
						break;
					}
				}

				stringbuffer.append(c1);
			}
			else
			{
				stringbuffer.append(c);
			}
		}

		return stringbuffer.toString();
	}


//	public String getServletInfo()
//	{
//		return "ShareGrid Portal -- DownloadServlet";
//	}


	private File lookupFile(String s)
	{
		File file = new File(s);
		return file.isAbsolute() ? file : new File(context.getRealPath("/"), s);
	}


	private String getMimeType(String s)
	{
		int i = s.lastIndexOf(".");
		if (i > 0 && i < s.length() - 1)
		{
			String s1 = s.substring(i + 1);
			if(s1.equalsIgnoreCase("amr"))
				return "audio/amr";
			if(s1.equalsIgnoreCase("mid"))
				return "audio/midi";
			if(s1.equalsIgnoreCase("mmf"))
				return "application/vnd.smaf";
			if(s1.equalsIgnoreCase("qcp"))
				return "audio/vnd.qcelp";
			if(s1.equalsIgnoreCase("hqx"))
				return "application/mac-binhex40";
			if(s1.equalsIgnoreCase("cpt"))
				return "application/mac-compactpro";
			if(s1.equalsIgnoreCase("doc"))
				return "application/msword";
			if(s1.equalsIgnoreCase("jsp"))
				return "application/jsp";
			if(s1.equalsIgnoreCase("oda"))
				return "application/oda";
			if(s1.equalsIgnoreCase("pdf"))
				return "application/pdf";
			if(s1.equalsIgnoreCase("ai"))
				return "application/postscript";
			if(s1.equalsIgnoreCase("eps"))
				return "application/postscript";
			if(s1.equalsIgnoreCase("ps"))
				return "application/postscript";
			if(s1.equalsIgnoreCase("ppt"))
				return "application/powerpoint";
			if(s1.equalsIgnoreCase("rtf"))
				return "application/rtf";
			if(s1.equalsIgnoreCase("bcpio"))
				return "application/x-bcpio";
			if(s1.equalsIgnoreCase("vcd"))
				return "application/x-cdlink";
			if(s1.equalsIgnoreCase("Z"))
				return "application/x-compress";
			if(s1.equalsIgnoreCase("cpio"))
				return "application/x-cpio";
			if(s1.equalsIgnoreCase("csh"))
				return "application/x-csh";
			if(s1.equalsIgnoreCase("dcr"))
				return "application/x-director";
			if(s1.equalsIgnoreCase("dir"))
				return "application/x-director";
			if(s1.equalsIgnoreCase("dxr"))
				return "application/x-director";
			if(s1.equalsIgnoreCase("dvi"))
				return "application/x-dvi";
			if(s1.equalsIgnoreCase("gtar"))
				return "application/x-gtar";
			if(s1.equalsIgnoreCase("gz"))
				return "application/x-gzip";
			if(s1.equalsIgnoreCase("hdf"))
				return "application/x-hdf";
			if(s1.equalsIgnoreCase("cgi"))
				return "application/x-httpd-cgi";
			if(s1.equalsIgnoreCase("jnlp"))
				return "application/x-java-jnlp-file";
			if(s1.equalsIgnoreCase("skp"))
				return "application/x-koan";
			if(s1.equalsIgnoreCase("skd"))
				return "application/x-koan";
			if(s1.equalsIgnoreCase("skt"))
				return "application/x-koan";
			if(s1.equalsIgnoreCase("skm"))
				return "application/x-koan";
			if(s1.equalsIgnoreCase("latex"))
				return "application/x-latex";
			if(s1.equalsIgnoreCase("mif"))
				return "application/x-mif";
			if(s1.equalsIgnoreCase("nc"))
				return "application/x-netcdf";
			if(s1.equalsIgnoreCase("cdf"))
				return "application/x-netcdf";
			if(s1.equalsIgnoreCase("sh"))
				return "application/x-sh";
			if(s1.equalsIgnoreCase("shar"))
				return "application/x-shar";
			if(s1.equalsIgnoreCase("sit"))
				return "application/x-stuffit";
			if(s1.equalsIgnoreCase("sv4cpio"))
				return "application/x-sv4cpio";
			if(s1.equalsIgnoreCase("sv4crc"))
				return "application/x-sv4crc";
			if(s1.equalsIgnoreCase("tar"))
				return "application/x-tar";
			if(s1.equalsIgnoreCase("tcl"))
				return "application/x-tcl";
			if(s1.equalsIgnoreCase("tex"))
				return "application/x-tex";
			if(s1.equalsIgnoreCase("textinfo"))
				return "application/x-texinfo";
			if(s1.equalsIgnoreCase("texi"))
				return "application/x-texinfo";
			if(s1.equalsIgnoreCase("t"))
				return "application/x-troff";
			if(s1.equalsIgnoreCase("tr"))
				return "application/x-troff";
			if(s1.equalsIgnoreCase("roff"))
				return "application/x-troff";
			if(s1.equalsIgnoreCase("man"))
				return "application/x-troff-man";
			if(s1.equalsIgnoreCase("me"))
				return "application/x-troff-me";
			if(s1.equalsIgnoreCase("ms"))
				return "application/x-troff-ms";
			if(s1.equalsIgnoreCase("ustar"))
				return "application/x-ustar";
			if(s1.equalsIgnoreCase("src"))
				return "application/x-wais-source";
			if(s1.equalsIgnoreCase("xml"))
				return "text/xml";
			if(s1.equalsIgnoreCase("ent"))
				return "text/xml";
			if(s1.equalsIgnoreCase("cat"))
				return "text/xml";
			if(s1.equalsIgnoreCase("sty"))
				return "text/xml";
			if(s1.equalsIgnoreCase("dtd"))
				return "text/dtd";
			if(s1.equalsIgnoreCase("xsl"))
				return "text/xsl";
			if(s1.equalsIgnoreCase("zip"))
				return "application/zip";
			if(s1.equalsIgnoreCase("au"))
				return "audio/basic";
			if(s1.equalsIgnoreCase("snd"))
				return "audio/basic";
			if(s1.equalsIgnoreCase("mpga"))
				return "audio/mpeg";
			if(s1.equalsIgnoreCase("mp2"))
				return "audio/mpeg";
			if(s1.equalsIgnoreCase("mp3"))
				return "audio/mpeg";
			if(s1.equalsIgnoreCase("aif"))
				return "audio/x-aiff";
			if(s1.equalsIgnoreCase("aiff"))
				return "audio/x-aiff";
			if(s1.equalsIgnoreCase("aifc"))
				return "audio/x-aiff";
			if(s1.equalsIgnoreCase("ram"))
				return "audio/x-pn-realaudio";
			if(s1.equalsIgnoreCase("rpm"))
				return "audio/x-pn-realaudio-plugin";
			if(s1.equalsIgnoreCase("ra"))
				return "audio/x-realaudio";
			if(s1.equalsIgnoreCase("wav"))
				return "audio/x-wav";
			if(s1.equalsIgnoreCase("pdb"))
				return "chemical/x-pdb";
			if(s1.equalsIgnoreCase("xyz"))
				return "chemical/x-pdb";
			if(s1.equalsIgnoreCase("gif"))
				return "image/gif";
			if(s1.equalsIgnoreCase("ief"))
				return "image/ief";
			if(s1.equalsIgnoreCase("jpeg"))
				return "image/jpeg";
			if(s1.equalsIgnoreCase("jpg"))
				return "image/jpeg";
			if(s1.equalsIgnoreCase("jpe"))
				return "image/jpeg";
			if(s1.equalsIgnoreCase("png"))
				return "image/png";
			if(s1.equalsIgnoreCase("tiff"))
				return "image/tiff";
			if(s1.equalsIgnoreCase("tif"))
				return "image/tiff";
			if(s1.equalsIgnoreCase("ras"))
				return "image/x-cmu-raster";
			if(s1.equalsIgnoreCase("pnm"))
				return "image/x-portable-anymap";
			if(s1.equalsIgnoreCase("pbm"))
				return "image/x-portable-bitmap";
			if(s1.equalsIgnoreCase("pgm"))
				return "image/x-portable-graymap";
			if(s1.equalsIgnoreCase("ppm"))
				return "image/x-portable-pixmap";
			if(s1.equalsIgnoreCase("rgb"))
				return "image/x-rgb";
			if(s1.equalsIgnoreCase("xbm"))
				return "image/x-xbitmap";
			if(s1.equalsIgnoreCase("xpm"))
				return "image/x-xpixmap";
			if(s1.equalsIgnoreCase("xwd"))
				return "image/x-xwindowdump";
			if(s1.equalsIgnoreCase("html"))
				return "text/html";
			if(s1.equalsIgnoreCase("htm"))
				return "text/html";
			if(s1.equalsIgnoreCase("txt"))
				return "text/plain";
			if(s1.equalsIgnoreCase("rtx"))
				return "text/richtext";
			if(s1.equalsIgnoreCase("tsv"))
				return "text/tab-separated-values";
			if(s1.equalsIgnoreCase("etx"))
				return "text/x-setext";
			if(s1.equalsIgnoreCase("sgml"))
				return "text/x-sgml";
			if(s1.equalsIgnoreCase("sgm"))
				return "text/x-sgml";
			if(s1.equalsIgnoreCase("mpeg"))
				return "video/mpeg";
			if(s1.equalsIgnoreCase("mpg"))
				return "video/mpeg";
			if(s1.equalsIgnoreCase("mpe"))
				return "video/mpeg";
			if(s1.equalsIgnoreCase("qt"))
				return "video/quicktime";
			if(s1.equalsIgnoreCase("mov"))
				return "video/quicktime";
			if(s1.equalsIgnoreCase("avi"))
				return "video/x-msvideo";
			if(s1.equalsIgnoreCase("movie"))
				return "video/x-sgi-movie";
			if(s1.equalsIgnoreCase("ice"))
				return "x-conference/x-cooltalk";
			if(s1.equalsIgnoreCase("wrl"))
				return "x-world/x-vrml";
			if(s1.equalsIgnoreCase("vrml"))
				return "x-world/x-vrml";
			if(s1.equalsIgnoreCase("wml"))
				return "text/vnd.wap.wml";
			if(s1.equalsIgnoreCase("wmlc"))
				return "application/vnd.wap.wmlc";
			if(s1.equalsIgnoreCase("wmls"))
				return "text/vnd.wap.wmlscript";
			if(s1.equalsIgnoreCase("wmlsc"))
				return "application/vnd.wap.wmlscriptc";
			if(s1.equalsIgnoreCase("wbmp"))
				return "image/vnd.wap.wbmp";
			if(s1.equalsIgnoreCase("css"))
				return "text/css";
			if(s1.equalsIgnoreCase("jad"))
				return "text/vnd.sun.j2me.app-descriptor";
			if(s1.equalsIgnoreCase("jar"))
				return "application/java-archive";
			if(s1.equalsIgnoreCase("3gp"))
				return "video/3gp";
			if(s1.equalsIgnoreCase("3g2"))
				return "video/3gpp2";
			if(s1.equalsIgnoreCase("mp4"))
				return "video/3gpp";
		}
		return "application/octet-stream";
	}
}
