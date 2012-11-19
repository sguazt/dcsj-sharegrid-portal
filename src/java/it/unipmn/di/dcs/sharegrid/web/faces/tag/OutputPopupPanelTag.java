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

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIOutputPopupPanelComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

/**
 * Tag for popup panels.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class OutputPopupPanelTag extends AbstractOutputTag
{ 
	@Override
	public String getComponentType()
	{
		return FacesConstants.OutputPopupPanelComponentType;
	}  

	@Override
	public String getRendererType()
	{
		return FacesConstants.HtmlOutputPopupPanelRendererType;
	} 

	@Override
	public void release()
	{
		super.release();

		this.autoScroll = null;
		this.bodyClass = null;
		this.bodyContentId = null;
		this.border = null;
		this.closable = null;
		this.closeAction = null;
		this.headerClass = null;
		this.height = null;
		this.maximizable = null;
		this.minimizable = null;
		this.modal = null;
		this.resizable = null;
		this.showOn = null;
		this.showOnId = null;
		this.width = null;
	}

	@Override
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

		UIOutputPopupPanelComponent outputPopupPanel = null;

		try
		{
			outputPopupPanel = (UIOutputPopupPanelComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Unexpected component " + component.toString() + ". Expected: '" + UIOutputPopupPanelComponent.class.getName() + "'. Perhaps you're missing a tag?");
		}

		if (this.autoScroll != null)
		{
			outputPopupPanel.setValueExpression("autoScroll", this.autoScroll);
		}
		if (this.bodyClass != null)
		{
			outputPopupPanel.setValueExpression("bodyClass", this.bodyClass);
		}
		if (this.bodyContentId != null)
		{
			outputPopupPanel.setValueExpression("bodyContentId", this.bodyContentId);
		}
		if (this.border != null)
		{
			outputPopupPanel.setValueExpression("border", this.border);
		}
		if (this.closable != null)
		{
			outputPopupPanel.setValueExpression("closable", this.closable);
		}
		if (this.closeAction != null)
		{
			outputPopupPanel.setValueExpression("closeAction", this.closeAction);
		}
		if (this.headerClass != null)
		{
			outputPopupPanel.setValueExpression("headerClass", this.headerClass);
		}
		if (this.headerTitle != null)
		{
			outputPopupPanel.setValueExpression("headerTitle", this.headerTitle);
		}
		if (this.height != null)
		{
			outputPopupPanel.setValueExpression("height", this.height);
		}
		if (this.maximizable != null)
		{
			outputPopupPanel.setValueExpression("maximizable", this.maximizable);
		}
		if (this.minimizable != null)
		{
			outputPopupPanel.setValueExpression("minimizable", this.minimizable);
		}
		if (this.modal != null)
		{
			outputPopupPanel.setValueExpression("modal", this.modal);
		}
		if (this.resizable != null)
		{
			outputPopupPanel.setValueExpression("resizable", this.resizable);
		}
		if (this.showOn != null)
		{
			outputPopupPanel.setValueExpression("showOn", this.showOn);
		}
		if (this.showOnId != null)
		{
			outputPopupPanel.setValueExpression("showOnId", this.showOnId);
		}
		if (this.width != null)
		{
			outputPopupPanel.setValueExpression("width", this.width);
		}
	}

	/** PROPERTY: autoScroll. */
	private ValueExpression autoScroll;
	public ValueExpression getAutoScroll()
	{
		return this.autoScroll;
	}
	public void setAutoScroll(ValueExpression value)
	{
		this.autoScroll = value;
	}

	/** PROPERTY: bodyClass. */
	private ValueExpression bodyClass;
	public ValueExpression getBodyClass()
	{
		return this.bodyClass;
	}
	public void setBodyClass(ValueExpression value)
	{
		this.bodyClass = value;
	}

	/** PROPERTY: bodyContentId. */
	private ValueExpression bodyContentId;
	public ValueExpression getBodyContentId()
	{
		return this.bodyContentId;
	}
	public void setBodyContentId(ValueExpression value)
	{
		this.bodyContentId = value;
	}

	/** PROPERTY: border. */
	private ValueExpression border;
	public ValueExpression getBorder()
	{
		return this.border;
	}
	public void setBorder(ValueExpression value)
	{
		this.border = value;
	}

	/** PROPERTY: closable. */
	private ValueExpression closable;
	public ValueExpression getClosable()
	{
		return this.closable;
	}
	public void setClosable(ValueExpression value)
	{
		this.closable = value;
	}

	/** PROPERTY: closeAction. */
	private ValueExpression closeAction;
	public ValueExpression getCloseAction()
	{
		return this.closeAction;
	}
	public void setCloseAction(ValueExpression value)
	{
		this.closeAction = value;
	}

	/** PROPERTY: headerClass. */
	private ValueExpression headerClass;
	public ValueExpression getHeaderClass()
	{
		return this.headerClass;
	}
	public void setHeaderClass(ValueExpression value)
	{
		this.headerClass = value;
	}

	/** PROPERTY: headerTitle. */
	private ValueExpression headerTitle;
	public ValueExpression getHeaderTitle()
	{
		return this.headerTitle;
	}
	public void setHeaderTitle(ValueExpression value)
	{
		this.headerTitle = value;
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

	/** PROPERTY: maximizable. */
	private ValueExpression maximizable;
	public ValueExpression getMaximizable()
	{
		return this.maximizable;
	}
	public void setMaximizable(ValueExpression value)
	{
		this.maximizable = value;
	}

	/** PROPERTY: minimizable. */
	private ValueExpression minimizable;
	public ValueExpression getMinimizable()
	{
		return this.minimizable;
	}
	public void setMinimizable(ValueExpression value)
	{
		this.minimizable = value;
	}

	/** PROPERTY: modal. */
	private ValueExpression modal;
	public ValueExpression getModal()
	{
		return this.modal;
	}
	public void setModal(ValueExpression value)
	{
		this.modal = value;
	}

	/** PROPERTY: resizable. */
	private ValueExpression resizable;
	public ValueExpression getResizable()
	{
		return this.resizable;
	}
	public void setResizable(ValueExpression value)
	{
		this.resizable = value;
	}

	/** PROPERTY: showOn. */
	private ValueExpression showOn;
	public ValueExpression getShowOn()
	{
		return this.showOn;
	}
	public void setShowOn(ValueExpression value)
	{
		this.showOn = value;
	}

	/** PROPERTY: showOnId. */
	private ValueExpression showOnId;
	public ValueExpression getShowOnId()
	{
		return this.showOnId;
	}
	public void setShowOnId(ValueExpression value)
	{
		this.showOnId = value;
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
}
