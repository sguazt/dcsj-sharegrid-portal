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

import it.unipmn.di.dcs.sharegrid.web.model.IUploadedFile;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import java.io.Serializable;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 * Component for input file upload.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UIInputFileComponent extends AbstractUIInputComponent implements Serializable
{	
	public static final String COMPONENT_FAMILY = FacesConstants.InputFileComponentFamily;
	public static final String COMPONENT_TYPE = FacesConstants.InputFileComponentType;
	public static final String RENDERER_TYPE = FacesConstants.HtmlInputFileRendererType;

	private Object[] values;

	/** A constructor. */
	public UIInputFileComponent()
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
	public Object saveState(FacesContext context)
	{
		if (this.values == null)
		{
			this.values = new Object[2];
		}

		this.values[0] = super.saveState(context);

		this.values[1] = this.accept;

		return this.values;
	}

	@Override
	public void restoreState(FacesContext context, Object state)
	{
		this.values = (Object[]) state;

		super.restoreState(context, this.values[0]);

		this.accept = (String) this.values[1];
	}

	public void setUploadedFile(IUploadedFile value)
	{
		this.setValue( value );
	}

	public IUploadedFile getUploadedFile()
	{
		return (IUploadedFile) this.getValue();
	}

	/**
	 * A comma-separated list of content types that a server will handle correctly.
	 * User agents may use this information to filter out non-conforming files when
	 * prompting a user to select files to be sent to the server.
	 */
	private String accept;
	public String getAccept()
	{
		if (null != this.accept)
		{
			return this.accept;
		}
		ValueExpression ve = getValueExpression("accept");
		if (ve != null)
		{
			return (String) ve.getValue(this.getFacesContext().getELContext());
		}

		return null;
	}
	public void setAccept(String accept)
	{
		this.accept = accept;
	}
}
