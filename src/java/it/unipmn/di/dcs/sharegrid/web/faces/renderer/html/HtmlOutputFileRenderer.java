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

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIOutputFileComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.renderer.util.RendererUtil;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesUtil;

import java.io.IOException;

import javax.el.ValueExpression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import javax.servlet.http.HttpServletRequest;

/**
 * Renderer for output file components.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class HtmlOutputFileRenderer extends AbstractHtmlRenderer
{
	protected Object oldBinding = null;

	/** A constructor. */
	public HtmlOutputFileRenderer()
	{
		super();
	}

	@Override
	public boolean getRendersChildren()
	{
		return false;
	}

	@Override
	protected void doEncodingBegin(FacesContext context, UIComponent component) throws IOException
	{
		super.doEncodingBegin( context, component );

		UIOutputFileComponent outFileComp = (UIOutputFileComponent) component;
		if (
			outFileComp.getVar() != null
			|| component.getChildCount() > 0
		) {
			this.setElValue(context, outFileComp);
		}
		if ( UIOutputFileComponent.METHOD_DOWNLOAD.equals( outFileComp.getMethod() ) )
		{
			this.renderLink(context, outFileComp);
		}
	}

	@Override
	protected void doEncodingEnd(FacesContext context, UIComponent component) throws IOException
	{
		UIOutputFileComponent dl = (UIOutputFileComponent) component;
		if (UIOutputFileComponent.METHOD_INLINE.equals(dl.getMethod()))
		{
			this.renderInline(context, dl);
		}
		else if (UIOutputFileComponent.METHOD_DOWNLOAD.equals(dl.getMethod()))
		{
			ResponseWriter writer = context.getResponseWriter();
			writer.endElement("a"); // finsh up the link!
		}
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		request.getSession().setAttribute( UIOutputFileComponent.class.getName()  + "-" + dl.getClientId(context), dl );
		this.resetElValue(context, dl);

		super.doEncodingEnd(context, component);
	}

	protected void setElValue(FacesContext context, UIOutputFileComponent comp)
	{
		ValueExpression ve = context.getApplication().getExpressionFactory().createValueExpression(
			context.getELContext(),
			"#{"+ comp.getVar() +"}",
			String.class
		);
		ve.setValue( context.getELContext(), this.generateUri(context, comp) );
		//ValueBinding vb = Util.getValueBinding("#{"+ comp.getUrlVar() +"}");
		//vb.setValue(context, generateUri(context, comp));

	}

	protected void resetElValue(FacesContext context, UIOutputFileComponent comp)
	{
		ValueExpression ve = context.getApplication().getExpressionFactory().createValueExpression(
			context.getELContext(),
			"#{"+ comp.getVar() +"}",
			String.class
		);
		ve.setValue( context.getELContext(), null );
		//ValueBinding vb = Util.getValueBinding("#{"+comp.getUrlVar()+"}");
		//vb.setValue(context, null);
	}

	protected void renderInline(FacesContext context, UIOutputFileComponent comp) throws IOException
	{
		//String width = comp.getWidth();
		//String height = comp.getHeight();

		ResponseWriter writer = context.getResponseWriter();
		String uri = this.generateUri(context, comp);
		if (Boolean.TRUE.equals(comp.getIframe()))
		{
			writer.startElement("iframe", comp);
			writer.writeAttribute("src", uri, "src");
			//writer.writeAttribute("width", width, "width");
			//writer.writeAttribute("height", height, "height");
			RendererUtil.RenderPassThruAttributes( writer, comp, AbstractHtmlRenderer.PassthruAttributes );
			writer.endElement("iframe");
		}
		else
		{
			writer.startElement("object", comp);
			writer.writeAttribute("data", uri, "data");
			writer.writeAttribute("type", comp.getMimeType(), "type");
			//writer.writeAttribute("width", width, "width");
			//writer.writeAttribute("height", height, "height");
			RendererUtil.RenderPassThruAttributes( writer, comp, PassthruAttributes );
			writer.endElement("object");
		}
	}

	protected void renderLink(FacesContext context, UIOutputFileComponent comp) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("a", comp);
		writer.writeAttribute("href", generateUri(context, comp), "data");
		RendererUtil.RenderPassThruAttributes( writer, comp, PassthruAttributes );
		if (comp.getChildCount() > 0)
		{
			//
		}
		else
		{
			writer.writeText("Download", null);
		}
	}

	// TODO:  Look at the Util method by the same name.  There's a good opportunity
	// for a refactor here.  Thank you, merging code bases. :)
	protected String generateUri(FacesContext context, UIOutputFileComponent comp)
	{
		String uri = "";
		String mapping = FacesUtil.GetFacesMapping(context);
		if (FacesUtil.IsPrefixMapped(mapping))
		{
			uri = mapping + "/" + UIOutputFileComponent.DOWNLOAD_URI;
		}
		else
		{
			uri = UIOutputFileComponent.DOWNLOAD_URI + mapping;
		}

		uri += "?" + UIOutputFileComponent.REQUEST_PARAM + "=" + comp.getClientId(context);

		return uri;
	}
}
