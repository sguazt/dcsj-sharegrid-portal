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
import it.unipmn.di.dcs.sharegrid.web.model.UserGridJobFacade;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.MessageConstants;
import it.unipmn.di.dcs.sharegrid.web.service.IGridJobExecutionService;
import it.unipmn.di.dcs.sharegrid.web.service.ServiceFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.FacesException;

/**
 * Page bean for displaying the list of user Grid jobs.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class JobListBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( JobListBean.class.getName() );

	private static final String JOBID_PARAM = "jid";

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

		// Security check: makes sure the requested job really belongs
		// to current user.
		if ( this.getRequestParameterMap().containsKey( JOBID_PARAM ) )
		{
			String jid = this.getRequestParameterMap().get( JOBID_PARAM ).trim();

			if ( !Strings.IsNullOrEmpty( jid ) )
			{
				try
				{
					int jobId = Integer.parseInt( jid );

					UserGridJob job = null;
					job = UserGridJobFacade.instance().getUserGridJob( jobId );
					if ( job != null )
					{
						if ( job.getUserId().intValue() != this.getSessionBean().getUser().getId() )
						{
							throw new FacesException("Unauthorized job request.");
						}
					}
				}
				catch (Exception e)
				{
					JobListBean.Log.log( Level.SEVERE, "Request for job '" + jid + "' by user '" + this.getSessionBean().getUser().getId() + "' not satisfiable.", e );
					ViewUtil.AddExceptionMessage( e );

					this.inError = true;
				}
			}
		}
	}

	//@} Lifecycle /////////////////////////////////////////////////////////

	//@{ Properties ////////////////////////////////////////////////////////

	/** PROPERTY: userList. */
	private List<UserGridJob> jobs;
	public List<UserGridJob> getJobs()
	{
		if ( this.jobs == null )
		{
			try
			{
				IGridJobExecutionService jobSvc = null;

				jobSvc = ServiceFactory.instance().gridJobExecutionService();
				this.jobs = jobSvc.getUserJobs( this.getSessionBean().getUser() );
			}
			catch (Exception e)
			{
				JobListBean.Log.log( Level.SEVERE, "Unable to retrieve jobs.", e );
				ViewUtil.AddExceptionMessage(e);
			}
		}

		return this.jobs;
	}

	/** PROPERTY: currentPage. */
	private int currentPage = 1;
	public int getCurrentPage()
	{
		return this.currentPage;
	}
	public void setCurrentPage(int value)
	{
		this.currentPage = value;
	}

	//@} Properties ////////////////////////////////////////////////////////

	//@{ Actions ///////////////////////////////////////////////////////////

//	/** Action for viewing a job. */
//	public String doViewAction()
//	{
//		return "view-ok";
//	}

	/** Action for stopping a job. */
	public String doStopAction()
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

					// Remove it

					jobSvc.cancelUserJob( job );

//					// Updates the displayed list
//Not needed since the list holds the reference to the job object
//
//					if ( this.jobs != null )
//					{
//						int pos = this.jobs.indexOf( job );
//						if ( pos != -1 )
//						{
//							this.jobs.set( pos, job );
//						}
//					}
					this.jobs = null;

					allOk = true;
				}
				catch (Exception e)
				{
					JobListBean.Log.log( Level.SEVERE, "Error while stopping job '" + jid + "'.", e );
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

	/** Action for deleting a job. */
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

					// Removes from the displayed list

// Don't work
//					if ( this.jobs != null )
//					{
//						this.jobs.remove( job.getId() );
//					}
					this.jobs = null;

					allOk = true;
				}
				catch (Exception e)
				{
					JobListBean.Log.log( Level.SEVERE, "Error while deleting job '" + jid + "'.", e );
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

	//@} Actions ///////////////////////////////////////////////////////////
}
