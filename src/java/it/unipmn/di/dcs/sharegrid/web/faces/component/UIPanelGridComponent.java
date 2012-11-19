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

import javax.faces.component.UIPanel;

/**
 * Component for panel grids.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UIPanelGridComponent extends UIPanel
{
	/**
	 * The standard component type for this component.
	 */
	public static final String COMPONENT_TYPE = FacesConstants.PanelGridComponentType;

	/**
	 * The standard component family for this component.
	 */
	public static final String COMPONENT_FAMILY = FacesConstants.PanelGridComponentFamily;

	public static final String[] PASSTHROUGH_ATTRIBUTES = new String[] {
		"bgcolor",
		"border",
		"cellpadding",
		"cellspacing",
		"dir",
		"frame",
		"lang",
		"onclick",
		"ondblclick",
		"onkeydown",
		"onkeypress",
		"onkeyup",
		"onmousedown",
		"onmousemove",
		"onmouseout",
		"onmouseover",
		"onmouseup",
		"rules",
		"style",
		"summary",
		"title",
		"width"
	};

	@Override
	public String getFamily()
	{
		return COMPONENT_FAMILY;
	}
}
