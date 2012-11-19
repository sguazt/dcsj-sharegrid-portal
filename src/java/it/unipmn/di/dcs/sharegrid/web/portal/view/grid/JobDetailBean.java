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

package it.unipmn.di.dcs.sharegrid.web.portal.view.grid;

import it.unipmn.di.dcs.common.util.Strings;

import it.unipmn.di.dcs.sharegrid.web.model.UserGridJob;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.MessageConstants;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;
import it.unipmn.di.dcs.sharegrid.web.service.IGridJobExecutionService;
import it.unipmn.di.dcs.sharegrid.web.service.ServiceFactory;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

/**
 * Page bean for displaying the detail of a user Grid job.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class JobDetailBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( JobDetailBean.class.getName() );

	private static final String JOBID_PARAM = "id";

	/**
	 * Flag indicating an error is happened during the normal
	 * page processing.
	 */
	private boolean inError = false;

	//@{ Lifecycle /////////////////////////////////////////////////////////

	@Override
	public void init()
	{
		super.init();

		Map<String,String> paramsMap = this.getRequestParameterMap();
		if ( paramsMap.containsKey( JOBID_PARAM ) ) {
			String jid = paramsMap.get( JOBID_PARAM ).trim();

			if ( !Strings.IsNullOrEmpty( jid ) )
			{
				try
				{
					// Decode the job-id parameter
					int jobId;
					jobId = Integer.parseInt( jid );

					// Retrieve the job for checking against the owner
					IGridJobExecutionService jobSvc = null;
					UserGridJob job = null;

					jobSvc = ServiceFactory.instance().gridJobExecutionService();

					job = jobSvc.getUserJob( jobId );

					// Makes sure current user can view this job
					if ( job != null )
					{
						if ( job.getUserId().intValue() == this.getSessionBean().getUser().getId() )
						{
							this.setJob( job );
						}
						else
						{
							this.inError = true;
							ViewUtil.AddErrorMessage( MessageConstants.AUTHORIZATION_KO, "Job ''" + paramsMap.get(JOBID_PARAM) + "'" );
						}
					}
					else
					{
						this.inError = true;
						ViewUtil.AddErrorMessage( MessageConstants.EXIST_KO, "Job ''" + paramsMap.get(JOBID_PARAM) + "'" );
					}
				}
				catch (Exception e)
				{
					this.inError = true;
					JobDetailBean.Log.log( Level.SEVERE, "Unable to retrieve job '" + jid + "'", e );
					ViewUtil.AddExceptionMessage( e );
				}
			}
		}
		else
		{
			this.inError = true;
			ViewUtil.AddErrorMessage( MessageConstants.MISSING_PARAM_KO, "id" );
		}
	}

	//@} Lifecycle /////////////////////////////////////////////////////////

	//@{ Properties ////////////////////////////////////////////////////////

	/** PROPERTY: job. */
	private UserGridJob job;
	public UserGridJob getJob() { return this.job; }
	protected void setJob(UserGridJob value) { this.job = value; }

	//@} Properties ////////////////////////////////////////////////////////

	//@{ Actions ///////////////////////////////////////////////////////////

	/** Action for deleting current job. */
	public String doDeleteAction()
	{
		if ( this.inError )
		{
			return "delete-ko";
		}

		boolean allOk = false;

		if ( this.getRequestParameterMap().containsKey( JOBID_PARAM ) )
		{
			String jid = this.getRequestParameterMap().get( JOBID_PARAM ).trim();

			if ( !Strings.IsNullOrEmpty( jid ) )
			{
				try
				{
					// Decode the job-id parameter
					int jobId;
					jobId = Integer.parseInt( jid );

					// Retrieve the job
					IGridJobExecutionService jobSvc = null;
					UserGridJob job = null;

					jobSvc = ServiceFactory.instance().gridJobExecutionService();
					job = jobSvc.getUserJob( jobId );

					// Remove it
					jobSvc.removeUserJob( job );

					allOk = true;
				}
				catch (Exception e)
				{
					JobDetailBean.Log.log( Level.SEVERE, "Error while deleting job '" + jid + "'.", e );
					ViewUtil.AddErrorMessage( MessageConstants.DELETE_KO, "job" );
					ViewUtil.AddExceptionMessage( e );
				}
			}
		}
                else
		{
			ViewUtil.AddErrorMessage( MessageConstants.DELETE_KO, "job" );
			ViewUtil.AddWarnMessage( MessageConstants.MISSING_PARAM_KO, JOBID_PARAM );
		}

		return allOk ? "delete-ok" : "delete-ko";
	}

	/** Action for stopping current job. */
	public String doStoppingAction()
	{
		if ( this.inError )
		{
			return "stop-ko";
		}

		boolean allOk = false;

		if ( this.getRequestParameterMap().containsKey( JOBID_PARAM ) )
		{
			String jid = this.getRequestParameterMap().get( JOBID_PARAM ).trim();

			if ( !Strings.IsNullOrEmpty( jid ) )
			{
				try
				{
					// Decode the job-id parameter
					int jobId;
					jobId = Integer.parseInt( jid );

					// Retrieve the job
					IGridJobExecutionService jobSvc = null;
					UserGridJob job = null;

					jobSvc = ServiceFactory.instance().gridJobExecutionService();
					job = jobSvc.getUserJob( jobId );

					// Stop it
					jobSvc.cancelUserJob( job );

					allOk = true;
				}
				catch (Exception e)
				{
					JobDetailBean.Log.log( Level.SEVERE, "Error while stopping job '" + jid + "'.", e );
					ViewUtil.AddErrorMessage( MessageConstants.STOP_KO, "job" );
					ViewUtil.AddExceptionMessage( e );
				}
			}
		}
                else
		{
			ViewUtil.AddErrorMessage( MessageConstants.STOP_KO, "job" );
			ViewUtil.AddWarnMessage( MessageConstants.MISSING_PARAM_KO, JOBID_PARAM );
		}

		return allOk ? "stop-ok" : "stop-ko";
	}

	//@} Actions ///////////////////////////////////////////////////////////
}
