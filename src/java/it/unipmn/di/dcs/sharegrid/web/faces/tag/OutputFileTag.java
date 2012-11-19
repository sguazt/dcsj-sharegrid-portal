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

package it.unipmn.di.dcs.sharegrid.web.faces.tag;

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIOutputFileComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

/**
 * Tag for uploading files.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class OutputFileTag extends AbstractOutputTag
{ 
	/** A constructor. */
	public OutputFileTag()
	{
		super();
	}

	/** PROPERTY: method. */
	private ValueExpression method;
	public ValueExpression getMethod()
	{
		return this.method;
	}
	public void setMethod(ValueExpression value)
	{
		this.method = value;
	}

	/** PROPERTY: mimeType. */
	private ValueExpression mimeType;
	public ValueExpression getMimeType()
	{
		return this.mimeType;
	}
	public void setMimeType(ValueExpression value)
	{
		this.mimeType = value;
	}

	/** PROPERTY: data. */
	private ValueExpression data;
	public ValueExpression getData()
	{
		return this.data;
	}
	public void setData(ValueExpression value)
	{
		this.data = value;
	}

	/** PROPERTY: fileName. */
	private ValueExpression fileName;
	public ValueExpression getFileName()
	{
		return this.fileName;
	}
	public void setFileName(ValueExpression value)
	{
		this.fileName = value;
	}

	/** PROPERTY: iframe. */
	private ValueExpression iframe;
	public ValueExpression getIFrame()
	{
		return this.iframe;
	}
	public void setIFrame(ValueExpression value)
	{
		this.iframe = value;
	}

	/** PROPERTY: width. */
	private ValueExpression width;
	public ValueExpression getWidth()
	{
		return this.width;
	}
	public void setWidth(ValueExpression value)
	{
		this.width = value;
	}

	/** PROPERTY: height. */
	private ValueExpression height;
	public ValueExpression getHeight()
	{
		return this.height;
	}
	public void setHeight(ValueExpression value)
	{
		this.height = value;
	}

	/** PROPERTY: text. */
	private ValueExpression text;
	public ValueExpression getText()
	{
		return this.text;
	}
	public void setText(ValueExpression value)
	{
		this.text = value;
	}

	/** PROPERTY: var. */
	private ValueExpression var;
	public ValueExpression getVar()
	{
		return this.var;
	}
	public void setVar(ValueExpression value)
	{
		this.var = value;
	}

	@Override
	public String getComponentType()
	{
		return FacesConstants.OutputFileComponentType;
	}  

	@Override
	public String getRendererType()
	{
		return FacesConstants.HtmlOutputFileRendererType;
	} 

	@Override
	public void release()
	{
		super.release();

		this.method = null;
		this.mimeType = null;
		this.data = null;
		this.fileName = null;
		this.width = null;
		this.height = null;
		this.iframe = null;
		this.text = null;
		this.var = null;
	}

	@Override
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

		UIOutputFileComponent outputFile = null;

		try
		{
			outputFile = (UIOutputFileComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Unexpected component " + component.toString() + ". Expected: '" + UIOutputFileComponent.class.getName() + "'. Perhaps you're missing a tag?");
		}

		if (this.method != null)
		{
			outputFile.setValueExpression("method", this.method);
		}
		if (this.mimeType != null)
		{
			outputFile.setValueExpression("mimeType", this.mimeType);
		}
		if (this.data != null)
		{
			outputFile.setValueExpression("data", this.data);
		}
		if (this.fileName != null)
		{
			outputFile.setValueExpression("fileName", this.fileName);
		}
		if (this.width != null)
		{
			outputFile.setValueExpression("width", this.width);
		}
		if (this.height != null)
		{
			outputFile.setValueExpression("height", this.height);
		}
		if (this.iframe != null)
		{
			outputFile.setValueExpression("iframe", this.iframe);
		}
		if (this.text != null)
		{
			outputFile.setValueExpression("text", this.text);
		}
		if (this.var != null)
		{
			outputFile.setValueExpression("var", this.text);
		}
	}
}
