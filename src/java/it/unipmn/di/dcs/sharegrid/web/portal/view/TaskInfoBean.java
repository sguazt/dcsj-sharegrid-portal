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

package it.unipmn.di.dcs.sharegrid.web.portal.view;

import java.io.Serializable;

import it.unipmn.di.dcs.grid.core.middleware.sched.IBotTask;

/**
 * Represent a task of a Bag-of-Task job.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public final class TaskInfoBean implements Serializable
{
	private String id;
	private String name;
	private int maxNameLen;

	public TaskInfoBean()
	{
		this.maxNameLen = 1;
	}

	public TaskInfoBean(int pos, IBotTask task, int maxNameLen)
	{
		this.id = Integer.toString(pos);
		this.maxNameLen = maxNameLen;
		if ( task.getCommands().size() > 0 )
		{
			this.name = task.getCommands().get(0).getCommand();
			if ( this.name.length() > this.maxNameLen )
			{
				this.name = this.name.substring( 0, this.maxNameLen - 3 ) + "...";
			}
		}
		else
		{
			this.name = "<unknown>";
		}

		this.name = "Task #" + this.id + ": " + this.name;
	}

	/** PROPERTY: id. */
	public String getId() { return this.id; }
	public void setId(String value) { this.id = value; }

	/** PROPERY: name. */
	public String getName() { return this.name; }
	public void setName(String value) { this.name = value; }

	/** PROPERY: maxNameLen. */
	public int getMaxNameLen() { return this.maxNameLen; }
}
