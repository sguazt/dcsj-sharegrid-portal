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

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * The Popup panel {@code UIComponent} can be used to create a popup help
 * window.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UIOutputToolTipComponent extends AbstractUIOutputComponent
{
	public static final String COMPONENT_FAMILY = FacesConstants.OutputToolTipComponentFamily;
	public static final String COMPONENT_TYPE = FacesConstants.OutputToolTipComponentType;
	public static final String RENDERER_TYPE = FacesConstants.HtmlOutputToolTipRendererType;

	private Object[] values;

	/** A constructor. */
	public UIOutputToolTipComponent()
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

		this.autoHide = (Boolean) this.values[1];          
		this.borderWidth = (Integer) this.values[2];          
		this.caption = (String) this.values[3];
		this.closable = (Boolean) this.values[4];
		this.cornerRadius = (Integer) this.values[5];          
		this.hideDelay = (Integer) this.values[6];
		this.hideOthers = (Boolean) this.values[7];
		this.showDelay = (Integer) this.values[8];
		this.showOn = (String) this.values[9];
		this.targetId = (String) this.values[10];
		this.width = (Integer) this.values[11];
	}

	@Override
	public Object saveState(FacesContext context)
	{
		if (this.values == null)
		{
			this.values = new Object[12];
		}

		this.values[0] = super.saveState(context);        

		this.values[1] = this.autoHide;
		this.values[2] = this.borderWidth;
		this.values[3] = this.caption;
		this.values[4] = this.closable;
		this.values[5] = this.cornerRadius;
		this.values[6] = this.hideDelay;
		this.values[7] = this.hideOthers;
		this.values[8] = this.showDelay;
		this.values[9] = this.showOn;
		this.values[10] = this.targetId;
		this.values[12] = this.width;

		return this.values;
	}

	/**
	 * Use to close the popup automatically. 
	 */
	private Boolean autoHide = false;
	public boolean isAutoHide()
	{
		if (null != this.autoHide)
		{
			return this.autoHide;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"autoHide",
			true
		);
	}
	public void setAutoHide(boolean value)
	{
		this.autoHide = value;
	}

	/**
	 * Used to set the width of the tooltip borders.
	 */
	private Integer borderWidth;
	public int getBorderWidth()
	{
		if (null != this.borderWidth)
		{
			return this.borderWidth;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"borderWidth",
			-1
		);
	}
	public void setBorderWidth(int value)
	{
		this.borderWidth = value;
	}

	/**
	 * Use to close the popup automatically. 
	 */
	private String caption;
	public String getCaption()
	{
		if (null != this.caption)
		{
			return this.caption;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"caption",
			null
		);
	}
	public void setCaption(String value)
	{
		this.caption = value;
	}

	/**
	 * Used for displaying close button for bubble.
	 */
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
			false
		);
	}
	public void setClosable(boolean value)
	{
		this.closable = value;
	}

	/**
	 * Used to set the width of the tooltip borders.
	 */
	private Integer cornerRadius;
	public int getCornerRadius()
	{
		if (null != this.cornerRadius)
		{
			return this.cornerRadius;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"cornerRadius",
			-1
		);
	}
	public void setCornerRadius(int value)
	{
		this.cornerRadius = value;
	}

	/**
	 * Used for closing the popup after a given duration.
	 *
	 * Note: if property {@code closable} is true, setting this
	 * property has no effect.
	 */
	private Integer hideDelay;
	public int getHideDelay()
	{
		if (null != this.hideDelay)
		{
			return this.hideDelay;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"hideDelay",
			-1
		);
	}
	public void setHideDelay(int value)
	{
		this.hideDelay = value;
	}

	/**
	 * Used for closing the popup after a given duration.
	 *
	 * Note: if property {@code closable} is true, setting this
	 * property has no effect.
	 */
	private Boolean hideOthers;
	public boolean isHideOthers()
	{
		if (null != this.hideOthers)
		{
			return this.hideOthers;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"hideOthers",
			false
		);
	}
	public void setHideOthers(boolean value)
	{
		this.hideOthers = value;
	}

	/**
	 * Use to put a delay in ms before opening the popup.
	 */
	private Integer showDelay;
	public int getShowDelay()
	{
		if (null != this.showDelay)
		{
			return this.showDelay;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"showDelay",
			-1
		);
	}
	public void setShowDelay(int value)
	{
		this.showDelay = value;
	}

	/**
	 * Used to specify the event upon which show the tooltip.
	 */
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
			"mousemove"
		);
	}
	public void setShowOn(String value)
	{
		this.showOn = value;
	}

	/**
 	 * Set the id of the target component to which this popup is attached.
	 */
	private String targetId;
	public String getTargetId()
	{
		if (null != this.targetId)
		{
			return this.targetId;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"targetId",
			null
		);
	}
	public void setTargetId(String value)
	{
		this.targetId = value;
	}

	/**
	 * Number of pixels for the width of the popup window.
	 *
	 * The default is automatically calculated
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
	public void setWidth(int width)
	{
		this.width = width;
	}
}
