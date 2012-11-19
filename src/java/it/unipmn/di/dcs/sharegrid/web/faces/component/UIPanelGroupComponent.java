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

//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.UIComponent;

/**
 * Component for panel groups.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UIPanelGroupComponent extends HtmlPanelGroup
{
	public static final String COMPONENT_TYPE = FacesConstants.PanelGroupComponentType;
	public static final String COMPONENT_FAMILY = FacesConstants.PanelGroupComponentFamily;
	public static final String[] PASSTHROUGH_ATTRIBUTES = new String[] {
		"style",
	};

	private Object[] values;

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

		this.values[1] = this.colSpan;

		return this.values;
	}

	@Override
	public void restoreState(FacesContext context, Object state)
	{
		this.values = (Object[]) state;

		super.restoreState(context, this.values[0]);

		this.colSpan = (Integer) this.values[1];
	}

	private Integer colSpan;
	public int getColSpan()
	{
		if (null != this.colSpan)
		{
			return this.colSpan;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"colSpan",
			1
		);
	}
	public void setColSpan(int value)
	{
		this.colSpan = value;
	}

//	/**
//	 * Get an Iterator over the <code>UIPanelGroupComponent</code> children
//	 * found for this component.
//	 *
//	 * @return An Iterator over the <code>UIPanelGroupComponent</code>
//	 * children.
//	 */
//	public Iterator getGroupChildren()
//	{
//		// Get UIPanelGroupComponent children.
//		if (this.groupChildren == null)
//		{
//			this.groupChildren = new ArrayList();
//			Iterator kids = this.getChildren().iterator();
//			while (kids.hasNext())
//			{
//				UIComponent kid = (UIComponent) kids.next();
//				if ((kid instanceof TableColumn))
//				{
//					this.groupChildren.add(kid);
//				}
//			}
//		}
//		return this.groupChildren.iterator();
//	}
}
