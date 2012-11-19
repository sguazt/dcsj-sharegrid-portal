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

import it.unipmn.di.dcs.grid.core.middleware.sched.ExecutionStatus;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJob;

import java.io.Serializable;
import java.util.Date;

/**
 * Represent a Grid job submitted by a user of the portal.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class UserGridJob implements Serializable
{

// [non-javadoc]
//
// Open issues:
// * Job-Serialization:
//   >> Job serialization must remain experimental since large jobs still
//   >> generates errors on DB queries:
//   >> --- [snip] ---
//   >> Caused by: com.mysql.jdbc.PacketTooBigException: Packet for query is too
//   >> large (1189022 > 1048576). You can change this value on the server by
//   >> setting the max_allowed_packet' variable.
//   >> --- [/snip] ---
//
// [/non-javadoc]

	private static final long serialVersionUID = 42L;

	private int id;
	private Integer userId;
	private String name;
	private Date submitDate;
	private Date stopDate;
	private String schedjid;
	private ExecutionStatus status;
	private Integer middlewareId;
	private IJob job;

	public void setId(int value)
	{
		this.id = value;
	}

	public int getId()
	{
		return this.id;
	}

	public void setUserId(Integer value)
	{
		this.userId = value;
	}
	
	public Integer getUserId()
	{
		return this.userId;
	}

	public void setName(String value)
	{
		this.name = value;
	}
	
	public String getName()
	{
		return this.name;
	}

	public void setSubmissionDate(Date value)
	{
		this.submitDate = value;
	}

	public Date getSubmissionDate()
	{
		return this.submitDate;
	}

	public void setStoppingDate(Date value)
	{
		this.stopDate = value;
	}

	public Date getStoppingDate()
	{
		return this.stopDate;
	}

	public void setSchedulerJobId(String value)
	{
		this.schedjid = value;
	}

	public String getSchedulerJobId()
	{
		return this.schedjid;
	}

	public void setStatus(ExecutionStatus value)
	{
		this.status = value;
	}

	public ExecutionStatus getStatus()
	{
		return this.status;
	}

	public void setMiddlewareId(Integer value)
	{
		this.middlewareId = value;
	}

	public Integer getMiddlewareId()
	{
		return this.middlewareId;
	}

	public void setRawJob(IJob job)
	{
//@{FIXME: See "Job-Serialization" issue.
//		this.job = job;
//@}FIXME: See "Job-Serialization" issue.
		this.job = null;
	}

	public IJob getRawJob()
	{
//@{FIXME: See "Job-Serialization" issue.
//		return this.job;
//@}FIXME: See "Job-Serialization" issue.
		return null;
	}

	public boolean isAborted() { return this.status == ExecutionStatus.ABORTED; }

	public boolean isCancelled() { return this.status == ExecutionStatus.CANCELLED; }

	public boolean isFailed() { return this.status == ExecutionStatus.FAILED; }

	public boolean isFinished() { return this.status == ExecutionStatus.FINISHED; }

	public boolean isReady() { return this.status == ExecutionStatus.READY; }

	public boolean isRunning() { return this.status == ExecutionStatus.RUNNING; }

	public boolean isUnstarted() { return this.status == ExecutionStatus.UNSTARTED; }

	@Override
	public String toString()
	{
		return "<"
		+	"Id: " + this.getId() + ","
		+	"Name: " + this.getName() + ","
		+	"Submission Date: " + this.getSubmissionDate() + ","
		+	"Stopping Date: " + this.getStoppingDate() + ","
		+	"Middleware Id: " + this.getMiddlewareId() + ","
		+	"Scheduler Job Id: " + this.getSchedulerJobId() + ","
		+	"Status: " + this.getStatus()
		+	">";
	}
}
