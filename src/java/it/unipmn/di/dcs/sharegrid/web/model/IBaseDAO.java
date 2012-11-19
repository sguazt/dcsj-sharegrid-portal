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

package it.unipmn.di.dcs.sharegrid.web.model;

import java.util.List;

/**
 * Interface for objects data store.
 *
 * The generic types are the object type (<code>OT</code>) and the object-id
 * type (<code>OI</code>).
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IBaseDAO<OT,OI>
{
	/**
	 * Inserts a new object or updates an existing one.
	 */
	OI save(OT o) throws Exception;

	/**
	 * Inserts a new object.
	 */
	OI insert(OT o) throws Exception;

	/**
	 * Updates a new object.
	 */
	void update(OT o) throws Exception;

	/**
	 * Loads an existing object from a object-id.
	 */
	OT load(OI id) throws Exception;

	/**
	 * Removes an existing object.
	 */
	void remove(OI id) throws Exception;

	/**
	 * Return <code>true</code> if the given object does not exist;
	 * <code>false</code> otherwise.
	 */
	boolean exists(OI id) throws Exception;

	/**
	 * Returns the list of all objects.
	 */
	List<OT> list() throws Exception;

	/**
	 * Returns the list of all objects associated to the given identifiers.
	 */
	List<OT> listFromId(OI[] ids) throws Exception;

	/**
	 * Returns the list of all object identifier.
	 */
	List<OI> idList() throws Exception;
}
