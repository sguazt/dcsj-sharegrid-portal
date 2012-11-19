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

package it.unipmn.di.dcs.sharegrid.web.faces.component;

import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import java.io.Serializable;

import javax.faces.context.FacesContext;

/**
 * <code>UIComponent</code> component for file output.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UIOutputFileComponent extends AbstractUIOutputComponent implements Serializable
{
	public static final String COMPONENT_FAMILY = FacesConstants.OutputFileComponentFamily;
	public static final String COMPONENT_TYPE = FacesConstants.OutputFileComponentType;
	public static final String RENDERER_TYPE = FacesConstants.HtmlOutputFileRendererType;
	public static final String DOWNLOAD_URI = "dcs___Download";
	public static final String METHOD_DOWNLOAD = "download";
	public static final String METHOD_INLINE = "inline";
	public static final String REQUEST_PARAM = "componentId";

	private Object[] values;

	/** A constructor. */
	public UIOutputFileComponent()
	{
		super();

		this.setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily()
	{
		return COMPONENT_FAMILY;
	}

	@Override
	public void restoreState(FacesContext context, Object state)
	{
		this.values = (Object[]) state;

		super.restoreState(context, this.values[0]);

		this.method = (String) this.values[1];
		this.mimeType = (String) this.values[2];
		this.data = this.values[3];
		this.fileName = (String) this.values[4];
		this.width = (Integer) this.values[5];
		this.height = (Integer) this.values[6];
		this.iframe = (Boolean) this.values[7];
		this.text = (String) this.values[8];
	}

	@Override
	public Object saveState(FacesContext context)
	{
		if (this.values == null)
		{
			this.values = new Object[9];
		}
		this.values[0] = super.saveState(context);

		this.values[1] = method;
		this.values[2] = mimeType;
		this.values[3] = data;
		this.values[4] = fileName;
		this.values[5] = width;
		this.values[6] = height;
		this.values[7] = iframe;
		this.values[8] = text;

		return this.values;
	}

	/**
	 * This property is the data of the actual object to be rendered/downloaded.
	 */
	private Object data;
	public Object getData()
	{
		if (null != this.data)
		{
			return this.data;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"data",
			null
		);
	}
	public void setData(Object value)
	{
		this.data = value;
	}

	/**
	 * The name of the file to be sent to the client
	 */
	private String fileName;
	public String getFileName()
	{
		if (null != this.fileName)
		{
			return this.fileName;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"fileName",
			null
		);
	}
	public void setFileName(String value)
	{
		this.fileName = value;
	}

	/**
	 * The height of the object to be rendered for method="inline"
	 */
	private Integer height;
	public int getHeight()
	{
		if (null != this.height)
		{
			return this.height;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"height",
			-1
		);
	}
	public void setHeight(int value)
	{
		this.height = value;
	}

	/**
	 * If true, the object is rendered as the source of an iframe; if false,
	 * and &lt;object&gt; tag is rendered
	 */
	private Boolean iframe;
	public boolean getIframe()
	{
		if (null != this.iframe)
		{
			return this.iframe;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"iframe",
			false
		);
	}
	public void setIframe(boolean value)
	{
		this.iframe = value;
	}

	/**
	 * This property determines the method with which the object is delivered to 
	 * the client.
	 *
	 * Valid options are inline or download.  If "inline" is chosen,
	 * the object will be rendered in the body of the page via an
	 * &lt;object/&gt; tag. If "download" is chosen, a link will be generated,
	 * which the user can click to download the object.
	 */
	private String method;
	public String getMethod()
	{
		if (null != this.method)
		{
			return this.method;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"method",
			METHOD_DOWNLOAD
		);
	}
	public void setMethod(String value)
	{
		this.method = value;
	}

	/**
	 * The user will need to supply the mime type for the download.
	 */
	private String mimeType;
	public String getMimeType()
	{
		if (null != this.mimeType)
		{
			return this.mimeType;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"mimeType",
			"application/octet-stream"
		);
	}
	public void setMimeType(String value)
	{
		this.mimeType = value;
	}

	/**
	 * The text to be rendered as the link label if method="download"
	 */
	private String text;
	public String getText()
	{
		if (null != this.text)
		{
			return this.text;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"text",
			null
		);
	}
	public void setText(String value)
	{
		this.text = text;
	}

	/**
	 * If set, this property will cause an EL variable, by the name of {@code var}
	 * to be added to the ELContext for the duration of the component rendering (i.e.,
	 * it will only be available to child components).
	 */
	private String var;
	public String getVar()
	{
		if (null != this.var)
		{
			return this.var;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"var",
			"out"
		);
	}
	public void setVar(String value)
	{
		this.var = value;
	}

	/**
	 * The width of the object to be rendered for method="inline"
	 */
	private Integer width;
	public int getWidth()
	{
		if (null != this.width)
		{
			return this.width;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"width",
			-1
		);
	}
	public void setWidth(int value)
	{
		this.width = value;
	}
}
