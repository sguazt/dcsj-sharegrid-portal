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

package it.unipmn.di.dcs.sharegrid.web.faces.renderer.html;

import it.unipmn.di.dcs.common.annotation.FIXME;
import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIInputFileComponent;
import it.unipmn.di.dcs.sharegrid.web.model.IUploadedFile;
import it.unipmn.di.dcs.sharegrid.web.model.UploadedFile;
import it.unipmn.di.dcs.sharegrid.web.servlet.MultipartRequestWrapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.ServletRequest;

import org.apache.commons.fileupload.FileItem;

/**
 * Renderer for input file component.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@FIXME("Handle wrapped MultipartRequestWrapper objects (see code for details)")
public class HtmlInputFileRenderer extends AbstractHtmlRenderer
{
	@Override
	@SuppressWarnings("unchecked")
	protected void doDecoding(FacesContext context, UIComponent component)
	{
		super.doDecoding( context, component );

		ExternalContext externalCtx = context.getExternalContext(); 

		String clientId = null;
		FileItem item = null;

		clientId = component.getClientId(context);

		if ( externalCtx.getRequest() instanceof ServletRequest )
		{
			ServletRequest request = (ServletRequest) externalCtx.getRequest();

/*
			while (
				request != null
				&& !(request instanceof MultipartRequestWrapper)
			)
			{
				if (request instanceof HttpServletRequestWrapper)
				{
					request = ((HttpServletRequestWrapper) request).getRequest();
				}
				else
				{
					request = null;
				}
			}
*/

			if ( request != null )
			{
				if ( request instanceof HttpServletRequest )
				{
					if ( !( request instanceof MultipartRequestWrapper ) )
					{
						request = new MultipartRequestWrapper( (HttpServletRequest) request);
					}
					item = ((MultipartRequestWrapper) request).getFileItem(clientId);
				}
				else
				{
					//throw new FacesException("Unexpected Request object: " + request.getClass().getName() );
					item = ((MultipartRequestWrapper) request).getFileItem(clientId);
				}
			}
			else
			{
				Map<String,FileItem> fileItems = null;
				fileItems = (Map<String,FileItem>) externalCtx.getRequestMap().get( MultipartRequestWrapper.UploadedFilesAttr );
				if (fileItems != null)
				{
					item = (FileItem) fileItems.get(clientId);
				}
			}
		}
		else
		{
			//FIXME:
			//MultipartRequestWrapper might have been wrapped again by one or more additional
			//Filters. We try to find the MultipartWrapper, but if a filter has wrapped
			//the ServletRequest with a class other than HttpServletRequestWrapper
			//this will fail.
			//throw new FacesException("Unexpected Request object: " + request.getClass().getName() );
			Map<String,FileItem> fileItems = null;

			try
			{
				fileItems = (Map<String,FileItem>) externalCtx.getRequestMap().get( MultipartRequestWrapper.UploadedFilesAttr );
			}
			catch (ClassCastException cce)
			{
				throw new FacesException(cce);
			}

			if (fileItems != null)
			{
				item = (FileItem) fileItems.get(clientId);
			}
		}

		//FileItem item = (FileItem) request.getAttribute(clientId);

		if ( item != null )
		{
			UploadedFile upload = new UploadedFile( item );
			//upload.setFileName( item.getName() );
			//upload.setContentType( item.getContentType() );
			//upload.setSize( item.getSize() );
			//boolean isInMemory = item.isInMemory();

			((EditableValueHolder) component).setSubmittedValue(upload);  
			((EditableValueHolder) component).setValid(true);  
		}
		else
		{
			((EditableValueHolder) component).setValid(false);  
		}

/*

		Object target = component.getAttributes().get("target");

		if (target != null)
		{
			File file = null;
			if (target instanceof File)
			{
				file = (File) target;
			}
			else
			{
				ServletContext servletContext = (ServletContext) external.getContext();
				String realPath = servletContext.getRealPath(target.toString());
				file = new File(realPath); 
			}

			try
			{
				// ugh--write is declared with "throws Exception"
				item.write(file);
			}
			catch (Exception ex)
			{ 
				throw new FacesException(ex);
			}
		}
*/
	}   

	@Override
	protected void doEncodingBegin(FacesContext context, UIComponent component)  throws IOException
	{
		super.doEncodingBegin( context, component );

        if (!(component instanceof UIInputFileComponent))
        {
            throw new IllegalArgumentException("HtmlInputFileRenderer can only render UIInputFileComponent components.");
        }

		UIInputFileComponent inputFile = null;
		inputFile = (UIInputFileComponent) component;

		ResponseWriter writer = context.getResponseWriter();

		String clientId = component.getClientId(context);

		writer.startElement("input", component);
		writer.writeAttribute("type", "file", "type");
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("name", clientId, "name");
		writer.writeAttribute("title", inputFile.getTitle(), "title");
		writer.writeAttribute("accept", inputFile.getAccept(), "accept");

		try
		{
			IUploadedFile value = (UploadedFile) ( ((UIInputFileComponent) component).getValue() );
			if ( value != null )
			{
				if ( !Strings.IsNullOrEmpty( value.getFileName() ) )
				{
					writer.writeAttribute("value", value.getFileName(), "value");
				}
			}
		}
		catch (ClassCastException cce)
		{
			throw new IOException( "Not a valid file.", cce );
		}

		writer.endElement("input");
		writer.flush();
	}

//	@Override
//	protected void doEncodingEnd(FacesContext context, UIComponent component)  throws IOException
//	{
//		// empty
//	}

	@Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException
	{
		if ( submittedValue instanceof IUploadedFile )
		{
			IUploadedFile upload = (IUploadedFile) submittedValue;

			if ( upload.getSize()>0 && !Strings.IsNullOrEmpty( upload.getFileName() ) )
			{
				return upload;
			}
		}

		return null;
	}
}
