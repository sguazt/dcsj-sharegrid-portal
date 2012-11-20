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

package it.unipmn.di.dcs.sharegrid.web.model.cloud;

import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a cloud service submitted by a specific user.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UserCloudService implements Serializable
{
	private static final long serialVersionUID = 42L;

	private int id;
	private Integer userId;
	private String name;
	private Date submitDate;
	private Date stopDate;
	private Integer schedSvcId;
	private ExecutionStatus status;
	private Integer middlewareId;

	/** Sets the id for this entity. */
	public void setId(int value)
	{
		this.id = value;
	}

	/** Returns the id of this entity. */
	public int getId()
	{
		return this.id;
	}

	/** Sets the id for the user that submitted the service. */
	public void setUserId(Integer value)
	{
		this.userId = value;
	}

	/** Returns the id of the user that submitted the service. */
	public Integer getUserId()
	{
		return this.userId;
	}

	/** Sets the symbolic name for the user service. */
	public void setName(String value)
	{
		this.name = value;
	}

	/** Returns the symbolic name of the user service. */
	public String getName()
	{
		return this.name;
	}

	/** Sets the submission date for the user service. */
	public void setSubmissionDate(Date value)
	{
		this.submitDate = value;
	}

	/** Returns the submission date of the user service. */
	public Date getSubmissionDate()
	{
		return this.submitDate;
	}

	/** Sets the Stopping date for the user service. */
	public void setStoppingDate(Date value)
	{
		this.stopDate = value;
	}

	/** Returns the Stopping date of the user service. */
	public Date getStoppingDate()
	{
		return this.stopDate;
	}

	/** Sets the scheduler assigned id for the user service. */
	public void setSchedulerServiceId(Integer value)
	{
		this.schedSvcId = value;
	}

	/** Returns the id of the user service assigned by the scheduler. */
	public Integer getSchedulerServiceId()
	{
		return this.schedSvcId;
	}

	/** Sets the execution status for the user service. */
	public void setStatus(ExecutionStatus value)
	{
		this.status = value;
	}

	/** Returns the execution status of the user service. */
	public ExecutionStatus getStatus()
	{
		return this.status;
	}

	/** Sets the middleware id for the user service. */
	public void setMiddlewareId(Integer value)
	{
		this.middlewareId = value;
	}

	/** Returns the id of the middleware the scheduler belongs to. */
	public Integer getMiddlewareId()
	{
		return this.middlewareId;
	}

	/** Returns {@code true} if the service execution status is ABORTED. */
	public boolean isAborted() { return this.status == ExecutionStatus.ABORTED; }

	/** Returns {@code true} if the service execution status is CANCELLED. */
	public boolean isCancelled() { return this.status == ExecutionStatus.CANCELLED; }

	/** Returns {@code true} if the service execution status is FAILED. */
	public boolean isFailed() { return this.status == ExecutionStatus.FAILED; }

//	/** Returns {@code true} if the service execution status is FINISHED. */
//	public boolean isFinished() { return this.status == ExecutionStatus.FINISHED; }

	/** Returns {@code true} if the service execution status is READY. */
	public boolean isReady() { return this.status == ExecutionStatus.READY; }

	/** Returns {@code true} if the service execution status is RUNNING. */
	public boolean isRunning() { return this.status == ExecutionStatus.RUNNING; }

	/** Returns {@code true} if the service execution status is STAGING_IN. */
	public boolean isStagingIn() { return this.status == ExecutionStatus.STAGING_IN; }

	/** Returns {@code true} if the service execution status is STOPPED. */
	public boolean isStopped() { return this.status == ExecutionStatus.STOPPED; }

	/** Returns {@code true} if the service execution status is SUSPENDED. */
	public boolean isSuspended() { return this.status == ExecutionStatus.SUSPENDED; }

	/**Returns {@code true} if the service execution status is UNSTARTED. */
	public boolean isUnstarted() { return this.status == ExecutionStatus.UNSTARTED; }

	@Override
	public String toString()
	{
		return "<"
		+       "Id: " + this.getId() + ","
		+       "Name: " + this.getName() + ","
		+       "Submission Date: " + this.getSubmissionDate() + ","
		+       "Stopping Date: " + this.getStoppingDate() + ","
		+       "Middleware Id: " + this.getMiddlewareId() + ","
		+       "Scheduler Service Id: " + this.getSchedulerServiceId() + ","
		+       "Status: " + this.getStatus()
		+       ">";
	}
}
