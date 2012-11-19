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

package it.unipmn.di.dcs.sharegrid.web.management.grid;

import java.util.Date;

/**
 * Represents the check state for a user grid job.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UserGridJobCheckState
{
	/** The identifier for this entity. */
	private int id;

	/** The job identifier. */
	private Integer jobId;

	/** The date of the last job status check. */
	private Date lastStatusDate;

	/** The date of the last job lifetime check. */
	private Date lastLifetimeDate;

	/** Sets the identifier for this entity. */
	public void setId(int value)
	{
		this.id = value;
	}

	/** Returns the identifier for this entity. */
	public int getId()
	{
		return this.id;
	}

	/** Set the job identifier. */
	public void setJobId(Integer value)
	{
		this.jobId = value;
	}

	/** Returns the job identifier. */
	public Integer getJobId()
	{
		return this.jobId;
	}

	/** Sets the date of the last job status check. */
	public void setLastStatusCheckDate(Date value)
	{
		this.lastStatusDate = value;
	}

	/** Returns the date of the last job status check. */
	public Date getLastStatusCheckDate()
	{
		return this.lastStatusDate;
	}

	/** Sets he date of the last job lifetime check. */
	public void setLastLifetimeCheckDate(Date value)
	{
		this.lastLifetimeDate = value;
	}

	/** Returns he date of the last job lifetime check. */
	public Date getLastLifetimeCheckDate()
	{
		return this.lastLifetimeDate;
	}
}
