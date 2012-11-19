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

package it.unipmn.di.dcs.sharegrid.web.faces.servlet;

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIOutputFileComponent;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Phase listener for file download.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class DownloadPhaseListener implements PhaseListener
{
	private static final long serialVersionUID = 1L;

	private static final transient Logger Log = Logger.getLogger(DownloadPhaseListener.class.getName());

	public DownloadPhaseListener()
	{
		// empty
	}

	//@{ PhaseListener implementation //////////////////////////////////////

	public void beforePhase(PhaseEvent e)
	{
		PhaseId phase = e.getPhaseId();
		if (phase == PhaseId.RENDER_RESPONSE)
		{
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			String uri = request.getRequestURI();
			if ((uri != null) && (uri.indexOf(UIOutputFileComponent.DOWNLOAD_URI) > -1))
			{
				try
				{
					HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
					String clientId = request.getParameter(UIOutputFileComponent.REQUEST_PARAM);
					UIOutputFileComponent comp = (UIOutputFileComponent) request.getSession().getAttribute( UIOutputFileComponent.class.getName() + "-" + clientId);
					if (comp != null)
					{
						Object value = comp.getData();
						if (value != null)
						{
							byte[] data = null;
							if (value instanceof byte[])
							{
								// data as array of bytes

								data = (byte[]) value;
							}
							else if (value instanceof ByteArrayOutputStream)
							{
								// data as a byte stream

								data = ((ByteArrayOutputStream) value).toByteArray();
							}
							else if (value instanceof InputStream)
							{
								// data as input stream

								data = this.getBytes( (InputStream) value );
							}
							else if (value instanceof String)
							{
								// data as file name

								FileInputStream fis = null;
								try
								{
									fis = new FileInputStream( (String) value );
									data = this.getBytes( fis );
								}
								catch (Exception ex)
								{
									if ( fis != null )
									{
										try { fis.close(); } catch (Exception x) { /* ignore */ }
										fis = null;
									}
								}
							}
							else
							{
								throw new FacesException("HtmlOutputFile: an unsupported data type was found:  " + value.getClass().getName());
							}
							if (UIOutputFileComponent.METHOD_DOWNLOAD.equals(comp.getMethod()))
							{
								response.setHeader("Content-Disposition", "attachment; filename=\"" + comp.getFileName() + "\"");
							}
							else
							{
								response.setHeader("Content-Disposition", "inline; filename=\"" + comp.getFileName() + "\"");
							}
							String mimeType = comp.getMimeType();
							response.setContentType(mimeType);
							ServletOutputStream sos = response.getOutputStream();
							sos.write(data);
							sos.flush();
							context.responseComplete();
						}
						request.getSession().removeAttribute( UIOutputFileComponent.class.getName() + "-" + clientId);
					}
				}
				catch (IOException ioe)
				{
					DownloadPhaseListener.Log.log( Level.SEVERE, "Unable to complete the download.", ioe );
				}
			}
		}
	}

	public void afterPhase(PhaseEvent e)
	{
		// empty
	}

	public PhaseId getPhaseId()
	{
		return PhaseId.RENDER_RESPONSE;
		//return PhaseId.ANY_PHASE; //use this for debugging
	}

	//@} PhaseListener implementation //////////////////////////////////////

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
			DownloadPhaseListener.Log.log( Level.SEVERE, "Error while reading bytes,", ioe );
		}
		finally
		{
			try { is.close(); } catch (Exception e1) { /* ignore */ }
			is = null;
		}

		return baos.toByteArray();
	}
}
