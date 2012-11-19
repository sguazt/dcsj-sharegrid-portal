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

import javax.faces.context.FacesContext;

/**
 * The Popup panel {@code UIComponent} can be used to create a popup
 * window.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UIOutputPopupPanelComponent extends AbstractUIOutputComponent
{
	public static final String COMPONENT_FAMILY = FacesConstants.OutputPopupPanelComponentFamily;
	public static final String COMPONENT_TYPE = FacesConstants.OutputPopupPanelComponentType;
	public static final String RENDERER_TYPE = FacesConstants.HtmlOutputPopupPanelRendererType;

	private Object[] values;

	/** A constructor. */
	public UIOutputPopupPanelComponent()
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

		this.autoScroll = (Boolean) this.values[1];
		this.bodyClass = (String) this.values[2];
		this.bodyContentId = (String) this.values[3];
		this.border = (Boolean) this.values[4];
		this.closable = (Boolean) this.values[5];
		this.closeAction = (String) this.values[6];
		this.headerClass = (String) this.values[7];
		this.headerTitle = (String) this.values[8];
		this.height = (Integer) this.values[9];
		this.maximizable = (Boolean) this.values[10];
		this.minimizable = (Boolean) this.values[11];
		this.modal = (Boolean) this.values[12];
		this.resizable = (Boolean) this.values[13];
		this.showOn = (String) this.values[14];
		this.showOnId = (String) this.values[15];
		this.width = (Integer) this.values[16];
	}

	@Override
	public Object saveState(FacesContext context)
	{
		if (this.values == null)
		{
			this.values = new Object[17];
		}

		this.values[0] = super.saveState(context);        

		this.values[1] = this.autoScroll;
		this.values[2] = this.bodyClass;
		this.values[3] = this.bodyContentId;
		this.values[4] = this.border;
		this.values[5] = this.closable;
		this.values[6] = this.closeAction;
		this.values[7] = this.headerClass;
		this.values[8] = this.headerTitle;
		this.values[9] = this.height;
		this.values[10] = this.maximizable;
		this.values[11] = this.minimizable;
		this.values[12] = this.modal;
		this.values[13] = this.resizable;
		this.values[14] = this.showOn;
		this.values[15] = this.showOnId;
		this.values[16] = this.width;

		return this.values;
	}

	private Boolean autoScroll;
	public boolean isAutoScroll()
	{
		if (null != this.autoScroll)
		{
			return this.autoScroll;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"autoScroll",
			false
		);
	}
	public void setAutoScroll(Boolean value)
	{
		this.autoScroll = value;
	}

	private String bodyClass;
	public String getBodyClass()
	{
		if (null != this.bodyClass)
		{
			return this.bodyClass;
		}
		
		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"bodyClass",
			null
		);
	}
	public void setBodyClass(String value)
	{
		this.bodyClass = value;
	}

	private String bodyContentId;
	public String getBodyContentId()
	{
		if (null != this.bodyContentId)
		{
			return this.bodyContentId;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"bodyContentId",
			null
		);
	}
	public void setBodyContentId(String value)
	{
		this.bodyContentId = value;
	}

	private Boolean border;
	public boolean isBorder()
	{
		if (null != this.border)
		{
			return this.border;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"border",
			true
		);
	}
	public void setBorder(boolean value)
	{
		this.border = value;
	}

	private Boolean closable;
	public boolean isClosable()
	{
		if (null != this.closable)
		{
			return this.closable;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"closable",
			true
		);
	}
	public void setClosable(boolean value)
	{
		this.closable = value;
	}

	private String closeAction;
	public String getCloseAction()
	{
		if (null != this.closeAction)
		{
			return this.closeAction;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"closeAction",
			"hide"
		);
	}
	public void setCloseAction(String value)
	{
		this.closeAction = value;
	}

	private String headerClass;
	public String getHeaderClass()
	{
		if (null != this.headerClass)
		{
			return this.headerClass;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"headerClass",
			null
		);
	}
	public void setHeaderClass(String value)
	{
		this.headerClass = value;
	}

	private String headerTitle;
	public String getHeaderTitle()
	{
		if (null != this.headerTitle)
		{
			return this.headerTitle;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"headerTitle",
			null
		);
	}
	public void setHeaderTitle(String value)
	{
		this.headerTitle = value;
	}

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

	private Boolean maximizable;
	public boolean isMaximizable()
	{
		if (null != this.maximizable)
		{
			return this.maximizable;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"maximizable",
			false
		);
	}
	public void setMaximizable(boolean value)
	{
		this.maximizable = value;
	}

	private Boolean minimizable;
	public boolean isMinimizable()
	{
		if (null != this.minimizable)
		{
			return this.minimizable;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"minimizable",
			false
		);
	}
	public void setMinimizable(boolean value)
	{
		this.minimizable = value;
	}

	private Boolean modal;
	public boolean isModal()
	{
		if (null != this.modal)
		{
			return this.modal;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"modal",
			false
		);
	}
	public void setModal(boolean value)
	{
		this.modal = value;
	}

	private Boolean resizable;
	public boolean isResizable()
	{
		if (null != this.resizable)
		{
			return this.resizable;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"resizable",
			true
		);
	}
	public void setResizable(boolean value)
	{
		this.resizable = value;
	}

	private String showOn;
	public String getShowOn()
	{
		if (null != this.showOn)
		{
			return this.showOn;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"showOn",
			"click"
		);
	}
	public void setShowOn(String value)
	{
		this.showOn = value;
	}

	private String showOnId;
	public String getShowOnId()
	{
		if (null != this.showOnId)
		{
			return this.showOnId;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"showOnId",
			null
		);
	}
	public void setShowOnId(String value)
	{
		this.showOnId = value;
	}

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
