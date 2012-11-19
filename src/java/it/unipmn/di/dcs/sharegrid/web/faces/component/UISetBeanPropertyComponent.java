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

import it.unipmn.di.dcs.common.annotation.Experimental;

import it.unipmn.di.dcs.sharegrid.web.faces.util.FacesConstants;

/**
 * Component for setting bean properties in a JSF page.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@Experimental("Don't use it")
public class UISetBeanPropertyComponent extends AbstractUIConditionalComponent
{
	public static final String COMPONENT_FAMILY = FacesConstants.SetBeanProperty_ComponentFamily;
	public static final String COMPONENT_TYPE = FacesConstants.SetBeanProperty_ComponentType;
	public static final String RENDERER_TYPE = null;

	/** A constructor. */
	public UISetBeanPropertyComponent()
	{
		super();

		this.setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily()
	{
		return COMPONENT_FAMILY;
	}
}
