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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.faces.component.UISelectItems;
import javax.faces.FacesException;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

/**
 * Component for selected items
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UISelectItemsComponent extends UISelectItems
{
	public static final String COMPONENT_TYPE = FacesConstants.SelectItemsComponentType;
	public static final String COMPONENT_FAMILY = FacesConstants.SelectItemsComponentFamily;

	private Object[] values;
 
	@Override
	public Object getValue()
	{
		return this.createSelectItems(super.getValue());
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
			this.values = new Object[4];
		}

		this.values[0] = super.saveState(context);

		this.values[1] = this.var;
		this.values[2] = this.itemLabel;
		this.values[3] = this.itemValue;

		return this.values;
	}

	@Override
	public void restoreState(FacesContext context, Object state)
	{
		this.values = (Object[]) state;

		super.restoreState(context, this.values[0]);

		this.var = (String) this.values[1];
		this.itemLabel = this.values[2];
		this.itemValue = this.values[3];
	}

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
			null
		);
	}
	public void setVar(String value)
	{
		this.var = value;
	}

	private Object itemLabel;
	public Object getItemLabel()
	{
		if (null != this.itemLabel)
		{
			return this.itemLabel;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"itemLabel",
			null
		);
	}
	public void setItemLabel(Object itemLabel)
	{
		this.itemLabel = itemLabel;
	}

	private Object itemValue;
	public Object getItemValue()
	{
		if (null != this.itemValue)
		{
			return this.itemValue;
		}

		return UIComponentUtil.GetAttributeValue(
			this.getFacesContext(),
			this,
			"itemValue",
			null
		);
	}
	public void setItemValue(Object value)
	{
		this.itemValue = value;
	}

	@SuppressWarnings("unchecked")
	private SelectItem[] createSelectItems(Object value)
	{
		List<SelectItem> items = new ArrayList<SelectItem>();

		if (value instanceof SelectItem[])
		{
			return (SelectItem[]) value;
		}
		else if (value instanceof Collection)
		{
			Collection<Object> col = null;

			try
			{
				col = (Collection<Object>) value;
			}
			catch (ClassCastException cce)
			{
				throw new FacesException(cce);
			}

			for (Object item : col)
			{
				if (item instanceof SelectItemGroup)
				{
					SelectItemGroup itemGroup = (SelectItemGroup) item;		
					SelectItem[] itemsFromGroup = itemGroup.getSelectItems();
					for (int i = 0; i < itemsFromGroup.length; i++)
					{
						items.add( itemsFromGroup[i] );
					}
				}
				else
				{
					FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put( this.getVar(), item );
					SelectItem selectItem = this.createSelectItem();
					FacesContext.getCurrentInstance().getExternalContext().getRequestMap().remove( this.getVar() );
					items.add(selectItem);
				}
			}
		}
		else if (value instanceof Map)
		{
			Map<String,Object> map = null;

			try
			{
				map = (Map<String,Object>) value;
			}
			catch (ClassCastException cce)
			{
				throw new FacesException(cce);
			}

			for (Map.Entry<String,Object> item : map.entrySet() )
			{
				FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put( this.getVar(), item.getValue() );
				SelectItem selectItem = this.createSelectItem();
				FacesContext.getCurrentInstance().getExternalContext().getRequestMap().remove( this.getVar() );
				items.add( selectItem );
			}
		}
		
		return items.toArray(new SelectItem[0]);
	}

	private SelectItem createSelectItem()
	{
		SelectItem item = null;
		Object value = this.getItemValue();
		String label = this.getItemLabel() != null ? this.getItemLabel().toString() : null;
		
		if(label != null)
		{
			item = new SelectItem(value, label);
		}
		else
		{
			item = new SelectItem(value);
		}
		
		return item;
	}
}
