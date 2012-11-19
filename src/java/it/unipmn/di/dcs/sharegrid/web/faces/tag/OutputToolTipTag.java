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

import it.unipmn.di.dcs.sharegrid.web.faces.component.UIOutputToolTipComponent;
import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

/**
 * Tag for bubble panels.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class OutputToolTipTag extends AbstractOutputTag
{ 
	/** A constructor. */
	public OutputToolTipTag()
	{
		super();
	}

	/** PROPERTY: autoHide. */
	private ValueExpression autoHide;
	public ValueExpression getAutoHide()
	{
		return this.autoHide;
	}
	public void setAutoHide(ValueExpression value)
	{
		this.autoHide = value;
	}

	/** PROPERTY: borderWidth. */
	private ValueExpression borderWidth;
	public ValueExpression getBorderWidth()
	{
		return this.borderWidth;
	}
	public void setBorderWidth(ValueExpression value)
	{
		this.borderWidth = value;
	}

	/** PROPERTY: caption. */
	private ValueExpression caption;
	public ValueExpression getCaption()
	{
		return this.caption;
	}
	public void setCaption(ValueExpression value)
	{
		this.caption = value;
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

	/** PROPERTY: cornerRadius. */
	private ValueExpression cornerRadius;
	public ValueExpression getCornerRadius()
	{
		return this.cornerRadius;
	}
	public void setCornerRadius(ValueExpression value)
	{
		this.cornerRadius = value;
	}

	/** PROPERTY: hideDelay. */
	private ValueExpression hideDelay;
	public ValueExpression getHideDelay()
	{
		return this.hideDelay;
	}
	public void setHideDelay(ValueExpression value)
	{
		this.hideDelay = value;
	}

	/** PROPERTY: hideOthers. */
	private ValueExpression hideOthers;
	public ValueExpression getHideOthers()
	{
		return this.hideOthers;
	}
	public void setHideOthers(ValueExpression value)
	{
		this.hideOthers = value;
	}

	/** PROPERTY: showDelay. */
	private ValueExpression showDelay;
	public ValueExpression getShowDelay()
	{
		return this.showDelay;
	}
	public void setShowDelay(ValueExpression value)
	{
		this.showDelay = value;
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

	/** PROPERTY: targetId. */
	private ValueExpression targetId;
	public ValueExpression getTargetId()
	{
		return this.targetId;
	}
	public void setTargetId(ValueExpression value)
	{
		this.targetId = value;
	}

	/** PROPERTY: visible. */
	private ValueExpression visible;
	public ValueExpression getVisible()
	{
		return this.visible;
	}
	public void setVisible(ValueExpression value)
	{
		this.visible = value;
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

	@Override
	public String getComponentType()
	{
		return FacesConstants.OutputToolTipComponentType;
	}  

	@Override
	public String getRendererType()
	{
		return FacesConstants.HtmlOutputToolTipRendererType;
	} 

	@Override
	public void release()
	{
		super.release();

		this.autoHide = null;
		this.borderWidth = null;
		this.caption = null;
		this.closable = null;
		this.cornerRadius = null;
		this.hideDelay = null;
		this.hideOthers = null;
		this.showDelay = null;
		this.showOn = null;
		this.targetId = null;
		this.visible = null;
		this.width = null;
	}

	@Override
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

		UIOutputToolTipComponent outputToolTip = null;

		try
		{
			outputToolTip = (UIOutputToolTipComponent) component;
		}
		catch (ClassCastException cce)
		{
			throw new IllegalStateException("Unexpected component " + component.toString() + ". Expected: '" + UIOutputToolTipComponent.class.getName() + "'. Perhaps you're missing a tag?");
		}

		if (this.autoHide != null)
		{
			outputToolTip.setValueExpression("autoHide", this.autoHide);
		}
		if (this.borderWidth != null)
		{
			outputToolTip.setValueExpression("borderWidth", this.borderWidth);
		}
		if (this.caption != null)
		{
			outputToolTip.setValueExpression("caption", this.caption);
		}
		if (this.closable != null)
		{
			outputToolTip.setValueExpression("closable", this.closable);
		}
		if (this.cornerRadius != null)
		{
			outputToolTip.setValueExpression("cornerRadius", this.cornerRadius);
		}
		if (this.hideDelay != null)
		{
			outputToolTip.setValueExpression("hideDelay", this.hideDelay);
		}
		if (this.hideOthers != null)
		{
			outputToolTip.setValueExpression("hideOthers", this.hideOthers);
		}
		if (this.showDelay != null)
		{
			outputToolTip.setValueExpression("showDelay", this.showDelay);
		}
		if (this.showOn != null)
		{
			outputToolTip.setValueExpression("showOn", this.showOn);
		}
		if (this.targetId != null)
		{
			outputToolTip.setValueExpression("targetId", this.targetId);
		}
		if (this.visible != null)
		{
			outputToolTip.setValueExpression("visible", this.visible);
		}
		if (this.width != null)
		{
			outputToolTip.setValueExpression("width", this.width);
		}
	}
}
