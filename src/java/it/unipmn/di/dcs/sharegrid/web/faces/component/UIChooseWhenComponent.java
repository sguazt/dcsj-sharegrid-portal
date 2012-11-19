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

/**
 * {@code UIComponent} for processing 'WHEN' conditions
 * during the rendering phase.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UIChooseWhenComponent extends AbstractUIConditionalComponent
{
	public static final String COMPONENT_FAMILY = FacesConstants.ChooseWhenComponentFamily;
	public static final String COMPONENT_TYPE = FacesConstants.ChooseWhenComponentType;
	public static final String RENDERER_TYPE = FacesConstants.ChooseWhenRendererType;

	/**
	 * Constructor for {@code When} component.
	 */
	public UIChooseWhenComponent()
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
