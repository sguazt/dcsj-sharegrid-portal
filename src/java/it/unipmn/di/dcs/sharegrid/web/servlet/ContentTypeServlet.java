/**
 * A simple HTTP Compression Servlet
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.zip.*;
import java.net.*;

/**
 * The servlet class.
 */
public class ContentTypeServlet extends HttpServlet
{
	private static final String JNLP_MIME_TYPE          = "application/x-java-jnlp-file";
	private static final String JAR_MIME_TYPE           = "application/x-java-archive";
	private static final String PACK200_MIME_TYPE       = "application/x-java-pack200";

	// HTTP Compression RFC 2616 : Standard headers
	public static final String ACCEPT_ENCODING          = "accept-encoding";
	public static final String CONTENT_TYPE             = "content-type";
	public static final String CONTENT_ENCODING         = "content-encoding";

	// HTTP Compression RFC 2616 : Standard header for HTTP/Pack200 Compression
	public static final String GZIP_ENCODING            = "gzip";
	public static final String PACK200_GZIP_ENCODING    = "pack200-gzip";
	   
	private void sendHtml(HttpServletResponse response, String s) throws IOException {
		 PrintWriter out = response.getWriter();
		 
		 out.println("<html>");
		 out.println("<head>");
		 out.println("<title>ContentType</title>");
		 out.println("</head>");
		 out.println("<body>");
		 out.println(s);
		 out.println("</body>");
		 out.println("</html>");
	}

	/* 
	 * Copy the inputStream to output ,
	 */    
	private void sendOut(InputStream in, OutputStream ostream) 
				 throws IOException {
		byte buf[] = new byte[8192];

		int n = in.read(buf);
		while (n > 0 ) {
			ostream.write(buf,0,n);
			n = in.read(buf);
		}
		ostream.close();
		in.close();
	}

	boolean doFile(String name, HttpServletResponse response) {
		File f = new File(name);
		if (f.exists()) {
			getServletContext().log("Found file " + name);

			response.setContentLength(Integer.parseInt(
						Long.toString(f.length())));

			response.setDateHeader("Last-Modified",f.lastModified());
			return true;  
		}
		getServletContext().log("File not found " + name);
		return false;
	}


	/** Called when someone accesses the servlet. */
	public void doGet(HttpServletRequest request, 
				HttpServletResponse response) 
				throws IOException, ServletException {
		
		String encoding = request.getHeader(ACCEPT_ENCODING);
		String pathInfo = request.getPathInfo();
		String pathInfoEx = request.getPathTranslated();
		String contentType = request.getContentType();
		StringBuffer requestURL  = request.getRequestURL();
		String requestName = pathInfo; 
		
		ServletContext sc = getServletContext();
		sc.log("----------------------------");
		sc.log("pathInfo="+pathInfo);
		sc.log("pathInfoEx="+pathInfoEx);
		sc.log("Accept-Encoding="+encoding);
		sc.log("Content-Type="+contentType);
		sc.log("requestURL="+requestURL);
		
		if (pathInfoEx == null) {
			response.sendError(response.SC_NOT_FOUND);
			return;
		}
		String outFile = pathInfo;
		boolean found = false;
		String contentEncoding = null;
		

		// Pack200 Compression
		if (encoding != null && contentType != null &&
				contentType.compareTo(JAR_MIME_TYPE) == 0 &&
				encoding.toLowerCase().indexOf(PACK200_GZIP_ENCODING) > -1){

			contentEncoding = PACK200_GZIP_ENCODING;
			
			
			if (doFile(pathInfoEx.concat(".pack.gz"),response)) {
				outFile = pathInfo.concat(".pack.gz") ;
				found = true;
			} else {
				// Pack/Compress and transmit, not very efficient.
				found = false;
			}
		}

		// HTTP Compression
		if (found == false && encoding != null &&
				contentType != null &&
				contentType.compareTo(JAR_MIME_TYPE) == 0 && 
				encoding.toLowerCase().indexOf("gzip") > -1) {
				
			contentEncoding = GZIP_ENCODING;

			if (doFile(pathInfoEx.concat(".gz"),response)) {
				outFile = pathInfo.concat(".gz");
				found = true;
			}             
		}

		// No Compression
		if (found == false) { // just send the file
			contentEncoding = null;
			sc.log(CONTENT_ENCODING + "=" + "null");
			doFile(pathInfoEx,response);
			outFile = pathInfo;
		}

		response.setHeader(CONTENT_ENCODING, contentEncoding);
		sc.log(CONTENT_ENCODING + "=" + contentEncoding + 
				" : outFile="+outFile);


		if (sc.getMimeType(pathInfo) != null) {
			response.setContentType(sc.getMimeType(pathInfo));
		}
		
		InputStream in = sc.getResourceAsStream(outFile);
		OutputStream out = response.getOutputStream();

		if (in != null) {
			try {
				sendOut(in,out);
			} catch (IOException ioe) {
				if (ioe.getMessage().compareTo("Broken pipe") == 0) {
					sc.log("Broken Pipe while writing");
					return;
				} else  throw ioe;
			}
		} else response.sendError(response.SC_NOT_FOUND);
		
	}
}
