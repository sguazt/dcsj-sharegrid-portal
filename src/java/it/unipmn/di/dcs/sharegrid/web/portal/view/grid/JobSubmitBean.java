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

import it.unipmn.di.dcs.common.conversion.Convert;
import it.unipmn.di.dcs.common.format.SizeFormat;
import it.unipmn.di.dcs.common.io.FileUtil;
import it.unipmn.di.dcs.common.util.Arrays;
import it.unipmn.di.dcs.common.util.Strings;
import it.unipmn.di.dcs.common.util.collection.Collections;

import it.unipmn.di.dcs.grid.core.format.jdf.JdfExporter;
import it.unipmn.di.dcs.grid.core.format.jdf.JdfImporter;
import it.unipmn.di.dcs.grid.core.middleware.sched.BotJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.BotTask;
import it.unipmn.di.dcs.grid.core.middleware.sched.IBotJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.IBotTask;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJob;
import it.unipmn.di.dcs.grid.core.middleware.sched.IJobRequirements;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageIn;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageInAction;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageInRule;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageOut;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageOutAction;
import it.unipmn.di.dcs.grid.core.middleware.sched.IStageOutRule;
import it.unipmn.di.dcs.grid.core.middleware.sched.JobRequirements;
import it.unipmn.di.dcs.grid.core.middleware.sched.RemoteCommand;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageIn;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageInAction;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageInMode;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageInRule;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageInType;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageOut;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageOutAction;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageOutMode;
import it.unipmn.di.dcs.grid.core.middleware.sched.StageOutRule;

import it.unipmn.di.dcs.sharegrid.web.model.Conf;
import it.unipmn.di.dcs.sharegrid.web.model.IUploadedFile;
import it.unipmn.di.dcs.sharegrid.web.model.User;
import it.unipmn.di.dcs.sharegrid.web.model.UserFacade;
import it.unipmn.di.dcs.sharegrid.web.service.IGridJobExecutionService;
import it.unipmn.di.dcs.sharegrid.web.service.ServiceFactory;
import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.TaskInfoBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.UploadedFileInfoBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.MessageConstants;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.TaskViewMode;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;

//import org.apache.myfaces.custom.fileupload.UploadedFile;

/**
 * Page bean for submitting grid jobs.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class JobSubmitBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( JobSubmitBean.class.getName() );

	private static final String UPLOAD_URL = "/resources/upld"; //FIXME: move to a conf class
	private static final String NEW_PARAM_NAME = "new";

	private static final String ADVANCED_TASKVIEWMODE = "a";
	private static final String SIMPLE_TASKVIEWMODE = "s";

	private static final String MANUALJOB_INSERTMODE = "manJobInMode";
	private static final String IMPORTJOB_INSERTMODE = "impJobInMode";

	private static final String EXEC_CMD_ARGS_ID = JobSubmitBean.class.getName() + ".eca";
	private static final String EXEC_CMD_ARGS_FILE_ID = JobSubmitBean.class.getName() + ".ecaf";
	private static final String EXEC_FILE_ID = JobSubmitBean.class.getName() + ".jef";
	private static final String EXTRA_FILES_ID = JobSubmitBean.class.getName() + ".jefs";
	private static final String INPUT_FILES_ID = JobSubmitBean.class.getName() + ".jifs";
	private static final String JOB_FILE_ID = JobSubmitBean.class.getName() + ".jf";
	private static final String JOB_INSERT_MODE_ID = JobSubmitBean.class.getName() + ".jim";
//	private static final String JOB_NAME_ID = JobSubmitBean.class.getName() + ".jn"; //FIXME: maybe useless
//	private static final String JOB_REQS_ID = JobSubmitBean.class.getName() + ".je";
	private static final String OUTPUT_FILES_ID = JobSubmitBean.class.getName() + ".jofs";
	private static final String OUTPUT_FILES_FILE_ID = JobSubmitBean.class.getName() + ".joffs";
	private static final String SENTINEL_ID = JobSubmitBean.class.getName() + ".__sentinel__";
//	private static final String TASK_EXEC_CMD_ID = JobSubmitBean.class.getName() + ".tec";
	private static final String TASK_INPUT_FILES_ID = JobSubmitBean.class.getName() + ".tjifs";
	private static final String TASK_OUTPUT_FILES_ID = JobSubmitBean.class.getName() + ".tjofs";
//	private static final String TASK_VIEW_ID = JobSubmitBean.class.getName() + ".tv";
	private static final String TASKS_ID = JobSubmitBean.class.getName() + ".tl";

	/** Regex expression for matching newline characters. */
	private static final Pattern NewLinePattern = Pattern.compile("\\r\\n|\\n");

	/** Tells if this object state have to be "cross-request" stored. */
	private boolean saveState = true;

	///@{ Lifecycle ///////////////////////////////////////////////////////

	@Override
	@SuppressWarnings("unchecked")
	public void init()
	{
		super.init();

		Map sessionMap = this.getSessionMap();

		if ( sessionMap == null )
		{
			JobSubmitBean.Log.log( Level.SEVERE, "Session not initialized." );
			ViewUtil.AddErrorMessage( MessageConstants.INVALID_SESSION );
			return;
		}

		Map requestMap = this.getRequestParameterMap();
		if (
			(
				requestMap != null
				&& requestMap.containsKey(NEW_PARAM_NAME)
				&& "1".equals(requestMap.get(NEW_PARAM_NAME))
			) || (
				!sessionMap.containsKey(SENTINEL_ID)
				|| sessionMap.get(SENTINEL_ID) == Boolean.FALSE
			)
		) {
			// New job submission: free all previously stored
			// session data

			sessionMap.remove(EXEC_CMD_ARGS_ID);
			sessionMap.remove(EXEC_CMD_ARGS_FILE_ID);
			sessionMap.remove(EXEC_FILE_ID);
			sessionMap.remove(EXTRA_FILES_ID);
			sessionMap.remove(INPUT_FILES_ID);
			sessionMap.remove(JOB_FILE_ID);
			sessionMap.remove(JOB_INSERT_MODE_ID);
//			sessionMap.remove(JOB_REQS_ID);
//			sessionMap.remove(JOB_NAME_ID);
			sessionMap.remove(OUTPUT_FILES_ID);
			sessionMap.remove(OUTPUT_FILES_FILE_ID);
//			sessionMap.remove(TASK_EXEC_CMD_ID);
			sessionMap.remove(TASK_INPUT_FILES_ID);
			sessionMap.remove(TASK_OUTPUT_FILES_ID);
//			sessionMap.remove(TASK_VIEW_ID);
			sessionMap.remove(TASKS_ID);

			sessionMap.put(SENTINEL_ID, Boolean.TRUE);

//			if ( requestMap != null && requestMap.containsKey(NEW_PARAM_NAME) )
//			{
//				requestMap.remove(NEW_PARAM_NAME);
//			}
		}
		else if ( sessionMap.containsKey(SENTINEL_ID) && sessionMap.get(SENTINEL_ID) == Boolean.TRUE )
		{
//			if ( this.getCurJobInsertMode().equals(IMPORTJOB_INSERTMODE) )
//			{
//System.err.println("init>> IMPORT JOB INSERT MODE");//XXX
				if (sessionMap.containsKey(INPUT_FILES_ID) )
				{
					this.setInFilesInfo( (Collection<UploadedFileInfoBean>) sessionMap.get( INPUT_FILES_ID ) );
				}
				if (sessionMap.containsKey(JOB_FILE_ID) )
				{
					this.setJobFileInfo( (UploadedFileInfoBean) sessionMap.get( JOB_FILE_ID ) );
				}
//			}
//			else
//			{
//System.err.println("init>> MANUAL JOB INSERT MODE");//XXX
				if (sessionMap.containsKey(EXEC_CMD_ARGS_ID) )
				{
					this.setExecCmdArgs( (String) sessionMap.get( EXEC_CMD_ARGS_ID ) );
				}
				if (sessionMap.containsKey(EXEC_CMD_ARGS_FILE_ID) )
				{
					this.setExecCmdArgsFileInfo( (UploadedFileInfoBean) sessionMap.get( EXEC_CMD_ARGS_FILE_ID ) );
				}
				if (sessionMap.containsKey(EXEC_FILE_ID) )
				{
					this.setExecFileInfo( (UploadedFileInfoBean) sessionMap.get( EXEC_FILE_ID ) );
				}
				if (sessionMap.containsKey(EXTRA_FILES_ID) )
				{
					this.setExtraFilesInfo( (Collection<UploadedFileInfoBean>) sessionMap.get( EXTRA_FILES_ID ) );
				}
				if (sessionMap.containsKey(INPUT_FILES_ID) )
				{
					this.setInFilesInfo( (Collection<UploadedFileInfoBean>) sessionMap.get( INPUT_FILES_ID ) );
				}
//				if (sessionMap.containsKey(JOB_REQS_ID) )
//				{
//					this.setJobReqs( (String) sessionMap.get( JOB_REQS_ID ) );
//				}
//				if (sessionMap.containsKey(JOB_NAME_ID) )
//				{
//					this.setJobName( (String) sessionMap.get( JOB_NAME_ID ) );
//				}
				if (sessionMap.containsKey(OUTPUT_FILES_ID) )
				{
					this.setOutFilesInfo( (List<String>) sessionMap.get( OUTPUT_FILES_ID ) );
				}
				if (sessionMap.containsKey(OUTPUT_FILES_FILE_ID) )
				{
					this.setOutFileFileInfo( (UploadedFileInfoBean) sessionMap.get( OUTPUT_FILES_FILE_ID ) );
				}
//				if (sessionMap.containsKey(TASK_EXEC_CMD_ID) )
//				{
//					this.setTaskExecCmd( (String) sessionMap.get( TASK_EXEC_CMD_ID ) );
//				}
				if (sessionMap.containsKey(TASK_INPUT_FILES_ID) )
				{
					this.setTaskInFilesInfo( (Collection<UploadedFileInfoBean>) sessionMap.get( TASK_INPUT_FILES_ID ) );
				}
				if (sessionMap.containsKey(TASK_OUTPUT_FILES_ID) )
				{
					this.setTaskOutFilesInfo( (List<String>) sessionMap.get( TASK_OUTPUT_FILES_ID ) );
				}
//				if (sessionMap.containsKey(TASK_VIEW_ID) )
//				{
//					this.setTaskViewMode( (String) sessionMap.get( TASK_VIEW_ID ) );
//				}
				if (sessionMap.containsKey(TASKS_ID) )
				{
					this.setTasks( (List<IBotTask>) sessionMap.get( TASKS_ID ) );
				}
//			}
			if (sessionMap.containsKey(JOB_INSERT_MODE_ID) )
			{
				this.setCurJobInsertMode( (String) sessionMap.get( JOB_INSERT_MODE_ID ) );
			}
		}
//		else
//		{
//			if ( this.getCurJobInsertMode().equals(IMPORTJOB_INSERTMODE) )
//			{
//				sessionMap.remove(INPUT_FILES_ID);
//				sessionMap.remove(JOB_FILE_ID);
//			}
//			else
//			{
//				sessionMap.remove(EXEC_CMD_ARGS_ID);
//				sessionMap.remove(EXEC_CMD_ARGS_FILE_ID);
//				sessionMap.remove(EXEC_FILE_ID);
//				sessionMap.remove(INPUT_FILES_ID);
//				sessionMap.remove(JOB_REQS_ID);
//				sessionMap.remove(JOB_NAME_ID);
//				sessionMap.remove(OUTPUT_FILES_ID);
//				sessionMap.remove(OUTPUT_FILES_FILE_ID);
//				sessionMap.remove(TASK_EXEC_CMD_ID);
//				sessionMap.remove(TASK_INPUT_FILES_ID);
//				sessionMap.remove(TASK_OUTPUT_FILES_ID);
//				sessionMap.remove(TASK_VIEW_ID);
//				sessionMap.remove(TASKS_ID);
//			}
//
//			sessionMap.remove(JOB_INSERT_MODE_ID);
//			sessionMap.put(SENTINEL_ID, Boolean.TRUE);
//		}
	}

	@Override
	public void prerender()
	{
		super.prerender();

		this.setExecFile( null );
		this.setExecCmdArgsFile( null );
		this.setExtraFile( null );
		this.setInFile( null );
		this.setJobFile( null );
		this.setOutFile( null );
		this.setOutFileFile( null );
		this.setTaskInFile( null );
		this.setTaskOutFile( null );
	}

	@Override
	@SuppressWarnings("unchecked")
	public void destroy()
	{
		super.destroy();

		Map sessionMap = this.getSessionMap();

		if ( sessionMap != null )
		{
			if ( this.saveState )
			{
				// Saves page state to session

				if ( this.getCurJobInsertMode().equals(IMPORTJOB_INSERTMODE) )
				{
//System.err.println("destroy>> IMPORT JOB INSERT MODE");//XXX
					sessionMap.put( INPUT_FILES_ID, this.getInFilesInfo() );
					sessionMap.put( JOB_FILE_ID, this.getJobFileInfo() );
				}
				else
				{
//System.err.println("destroy>> MANUAL JOB INSERT MODE");//XXX
					sessionMap.put( EXEC_CMD_ARGS_ID, this.getExecCmdArgs() );
					sessionMap.put( EXEC_CMD_ARGS_FILE_ID, this.getExecCmdArgsFileInfo() );
					sessionMap.put( EXEC_FILE_ID, this.getExecFileInfo() );
					sessionMap.put( EXTRA_FILES_ID, this.getExtraFilesInfo() );
					sessionMap.put( INPUT_FILES_ID, this.getInFilesInfo() );
//					sessionMap.put( JOB_NAME_ID, this.getJobName() );
//					sessionMap.put( JOB_REQS_ID, this.getJobReqs() );
					sessionMap.put( OUTPUT_FILES_ID, this.getOutFilesInfo() );
					sessionMap.put( OUTPUT_FILES_FILE_ID, this.getOutFileFileInfo() );
//					sessionMap.put( TASK_EXEC_CMD_ID, this.getTaskExecCmd() );
					sessionMap.put( TASK_INPUT_FILES_ID, this.getTaskInFilesInfo() );
					sessionMap.put( TASK_OUTPUT_FILES_ID, this.getTaskOutFilesInfo() );
//					sessionMap.put( TASK_VIEW_ID, this.getTaskViewMode() );
					sessionMap.put( TASKS_ID, this.getTasks() );
				}
				sessionMap.put( JOB_INSERT_MODE_ID, this.getCurJobInsertMode() );
			}
			else
			{
//				if ( this.getCurJobInsertMode().equals(IMPORTJOB_INSERTMODE) )
//				{
//System.err.println("destroy>> IMPORT JOB INSERT MODE");//XXX
					sessionMap.remove(INPUT_FILES_ID);
					sessionMap.remove(JOB_FILE_ID);
//				}
//				else
//				{
//System.err.println("destroy>> MANUAL JOB INSERT MODE");//XXX
					sessionMap.remove(EXEC_CMD_ARGS_ID);
					sessionMap.remove(EXEC_CMD_ARGS_FILE_ID);
					sessionMap.remove(EXEC_FILE_ID);
					sessionMap.remove(EXTRA_FILES_ID);
					sessionMap.remove(INPUT_FILES_ID);
//					sessionMap.remove(JOB_NAME_ID);
//					sessionMap.remove(JOB_REQS_ID);
					sessionMap.remove(OUTPUT_FILES_ID);
					sessionMap.remove(OUTPUT_FILES_FILE_ID);
//					sessionMap.remove(TASK_EXEC_CMD_ID);
					sessionMap.remove(TASK_INPUT_FILES_ID);
					sessionMap.remove(TASK_OUTPUT_FILES_ID);
//					sessionMap.remove(TASK_VIEW_ID);
					sessionMap.remove(TASKS_ID);
//				}
				sessionMap.remove(JOB_INSERT_MODE_ID);
				sessionMap.remove(SENTINEL_ID);
			}
		}
	}

	///@} Lifecycle ///////////////////////////////////////////////////////

	///@{ Properties ///////////////////////////////////////////////////////

	/**
	 * PROPERTY: advanceTaskViewMode.
	 *
	 * Tells if the advanced task view mode is used.
	 */
	public boolean isAdvancedTaskViewMode() { return this.taskViewMode.equals(ADVANCED_TASKVIEWMODE); }

	/**
	 * PROPERTY: curJobInsertMode.
	 *
	 * Current job insertion mode.
	 */
	private String curJobInsertMode = IMPORTJOB_INSERTMODE;
	public String getCurJobInsertMode() { return this.curJobInsertMode; }
	public void setCurJobInsertMode(String value) { this.curJobInsertMode = value; }

	/**
	 * PROPERTY: execArgInFileCombined.
	 *
	 * Tells if each input file must be applied to every argument.
	 */
	private boolean execArgInFilesCombined;
	public boolean isExecArgInFilesCombined() { return this.execArgInFilesCombined; }
	public void setExecArgInFilesCombined(boolean value) { this.execArgInFilesCombined = value; }

	/**
	 * PROPERTY: execCmdArgs.
	 *
	 * The arguments for the executable command.
	 */
	private String execCmdArgs;
	public String getExecCmdArgs() { return this.execCmdArgs; }
	public void setExecCmdArgs(String value) { this.execCmdArgs = value; this.execCmdArgsArray = null; }

	/**
	 * PROPERTY: execCmdArgsArray.
	 *
	 * The array of the arguments for the executable command.
	 * The array is built by splitting the arguments string
	 * (property <code>execCmdArgs</code>) using the newline
	 * character as the split character.
	 */
	private String[] execCmdArgsArray;
	public String[] getExecCmdArgsArray()
	{
		if ( !Strings.IsNullOrEmpty( this.getExecCmdArgs() ) && this.execCmdArgsArray == null )
		{
			// Lazy initialization of execCmdArgsArray: one line => one element
			this.execCmdArgsArray = NewLinePattern.split( this.getExecCmdArgs() );
		}
		return this.execCmdArgsArray;
	}
	protected void setExecCmdArgsArray(String[] value)
	{
		this.setExecCmdArgs(null);
		this.execCmdArgsArray = value;
	}

	/** PROPERTY: execCmdMinLen. */
	public int getExecCmdMinLen() { return 1; } //TODO

	/** PROPERTY: execCmdMaxLen. */
	public int getExecCmdMaxLen() { return 255; } //TODO

	/**
	 * PROPERTY: execCmdArgsFile.
	 *
	 * The uploaded executable command arguments file.
	 */
	private IUploadedFile execCmdArgsFile;
	public IUploadedFile getExecCmdArgsFile() { return this.execCmdArgsFile; }
	public void setExecCmdArgsFile(IUploadedFile value) { this.execCmdArgsFile = value; }

	/**
	 * PROPERTY: execCmdArgsFileInfo.
	 *
	 * The uploaded executable command arguments file info.
	 */
	private UploadedFileInfoBean execCmdArgsFileInfo;
	public UploadedFileInfoBean getExecCmdArgsFileInfo() { return this.execCmdArgsFileInfo; }
	public String getExecCmdArgsFileInfoString() { return this.execCmdArgsFileInfo.toString(); }
	public void setExecCmdArgsFileInfo(UploadedFileInfoBean value) { this.execCmdArgsFileInfo = value; }
 
	/** PROPERTY: execCmdArgsFileMinLen. */
	public int getExecCmdArgsFileMinLen() { return 1; }

	/** PROPERTY: execCmdArgsFileMaxLen. */
	public int getExecCmdArgsFileMaxLen() { return 255; } //TODO

	/** PROPERTY: execCmdArgsFileUploaded. */
	public boolean isExecCmdArgsFileUploaded() { return this.execCmdArgsFileInfo != null; }

	/**
	 * PROPERTY: execExtraFileAsArg.
	 *
	 * Tells if extra files should be passed as argument to the executable
	 * command ({@code true}) or not ({@code false}).
	 */
	private boolean execExtraFileAsArg;
	public boolean isExecExtraFileAsArg() { return this.execExtraFileAsArg; }
	public void setExecExtraFileAsArg(boolean value) { this.execExtraFileAsArg = value; }

	/**
	 * PROPERTY: execExtraFileOpt.
	 *
	 * Command line option for passing extra file to executable commands.
	 */
	private String execExtraFileOpt = "<";
	public String getExecExtraFileOpt() { return this.execExtraFileOpt; }
	public void setExecExtraFileOpt(String value) { this.execExtraFileOpt = value; }

	/**
	 * PROPERTY: execFile.
	 *
	 * The uploaded executable.
	 */
	private IUploadedFile execFile;
	public IUploadedFile getExecFile() { return this.execFile; }
	public void setExecFile(IUploadedFile value) { this.execFile = value; }

	/**
	 * PROPERTY: execFileInfo.
	 *
	 * The uploaded executable info.
	 */
	private UploadedFileInfoBean execFileInfo;
	public UploadedFileInfoBean getExecFileInfo() { return this.execFileInfo; }
	public String getExecFileInfoString() { return this.execFileInfo.toString(); }
	public void setExecFileInfo(UploadedFileInfoBean value) { this.execFileInfo = value; }
 
	/** PROPERTY: execFileMinLen. */
	public int getExecFileMinLen() { return 1; }

	/** PROPERTY: execFileMaxLen. */
	public int getExecFileMaxLen() { return 255; } //TODO

	/** PROPERTY: execFileUploaded. */
	public boolean isExecFileUploaded() { return this.execFileInfo != null; }

	/**
	 * PROPERTY: execInFileAsArg.
	 *
	 * Tells if input files should be passed as argument to the executable
	 * command ({@code true}) or not ({@code false}).
	 */
	private boolean execInFileAsArg;
	public boolean isExecInFileAsArg() { return this.execInFileAsArg; }
	public void setExecInFileAsArg(boolean value) { this.execInFileAsArg = value; }

	/**
	 * PROPERTY: execInFileOpt.
	 *
	 * Command line option for passing input file to executable commands.
	 */
	private String execInFileOpt = "<";
	public String getExecInFileOpt() { return this.execInFileOpt; }
	public void setExecInFileOpt(String value) { this.execInFileOpt = value; }

	/**
	 * PROPERTY: execName.
	 *
	 * The name of the executable.
	 */
	private String execName;
	public String getExecName() { return this.execName; }
	public void setExecName(String value) { this.execName = value; }

	/**
	 * PROPERTY: extraFile.
	 *
	 * The last uploaded extra file. 
	 */
	private IUploadedFile extraFile;
	public IUploadedFile getExtraFile() { return this.extraFile; }
	public void setExtraFile(IUploadedFile value) { this.extraFile = value; }

	/**
	 * PROPERTY: extraFilesInfo.
	 *
	 * The uploaded extra files.
	 */
	//private List<UploadedFileInfoBean> extraFilesInfo = new ArrayList<UploadedFileInfoBean>();
	private Map<String,UploadedFileInfoBean> extraFilesInfo = new TreeMap<String,UploadedFileInfoBean>();
	public Map<String,UploadedFileInfoBean> getExtraFilesInfoMap() { return this.extraFilesInfo; }
	public Collection<String> getExtraFilesName() { return this.extraFilesInfo.keySet(); }
	public Collection<UploadedFileInfoBean> getExtraFilesInfo() { return this.extraFilesInfo.values(); }
	public void setExtraFilesInfo(Collection<UploadedFileInfoBean> value)
	{
		this.extraFilesInfo.clear();
		for (UploadedFileInfoBean extraFileInfo : value)
		{
			this.extraFilesInfo.put( extraFileInfo.getName(), extraFileInfo );
		}
	}
	public void addExtraFileInfo(UploadedFileInfoBean value) { this.extraFilesInfo.put( value.getName(), value ); }
	public void removeExtraFileInfo(UploadedFileInfoBean value) { this.extraFilesInfo.remove( value.getName() ); }
 
	/**
	 * PROPERTY: extraFilesInfoRemoved.
	 *
	 * The uploaded extra files to remove.
	 */ 
	private List<String> extraFilesInfoRemoved = new ArrayList<String>();
	public List<String> getExtraFilesInfoRemoved() { return this.extraFilesInfoRemoved; }
	public void setExtraFilesInfoRemoved(List<String> value) { this.extraFilesInfoRemoved = (value != null) ? new ArrayList<String>(value) : new ArrayList<String>(); }
	public void addExtraFileInfoRemoved(String value) { this.extraFilesInfoRemoved.add( value ); }
 
	/** PROPERTY: extraFileUploaded. */
	public boolean isExtraFileUploaded() { return this.extraFilesInfo != null && !this.extraFilesInfo.isEmpty(); }

	/** PROPERTY: importJobInsertMode. */
	public String getImportJobInsertMode() { return IMPORTJOB_INSERTMODE; }

	/**
	 * PROPERTY: inFile.
	 *
	 * The last uploaded input file. 
	 */
	private IUploadedFile inFile;
	public IUploadedFile getInFile() { return this.inFile; }
	public void setInFile(IUploadedFile value) { this.inFile = value; }

	/**
	 * PROPERTY: inFilesInfo.
	 *
	 * The uploaded input files.
	 */
	//private List<UploadedFileInfoBean> inFilesInfo = new ArrayList<UploadedFileInfoBean>();
	private Map<String,UploadedFileInfoBean> inFilesInfo = new TreeMap<String,UploadedFileInfoBean>();
	public Map<String,UploadedFileInfoBean> getInFilesInfoMap() { return this.inFilesInfo; }
	public Collection<String> getInFilesName() { return this.inFilesInfo.keySet(); }
	public Collection<UploadedFileInfoBean> getInFilesInfo() { return this.inFilesInfo.values(); }
	public void setInFilesInfo(Collection<UploadedFileInfoBean> value)
	{
		this.inFilesInfo.clear();
		for (UploadedFileInfoBean inFileInfo : value)
		{
			this.inFilesInfo.put( inFileInfo.getName(), inFileInfo );
		}
	}
	public void addInFileInfo(UploadedFileInfoBean value) { this.inFilesInfo.put( value.getName(), value ); }
	public void removeInFileInfo(UploadedFileInfoBean value) { this.inFilesInfo.remove( value.getName() ); }
 
	/**
	 * PROPERTY: inFilesInfoRemoved.
	 *
	 * The uploaded input files to remove.
	 */ 
	private List<String> inFilesInfoRemoved = new ArrayList<String>();
	public List<String> getInFilesInfoRemoved() { return this.inFilesInfoRemoved; }
	public void setInFilesInfoRemoved(List<String> value) { this.inFilesInfoRemoved = (value != null) ? new ArrayList<String>(value) : new ArrayList<String>(); }
	public void addInFileInfoRemoved(String value) { this.inFilesInfoRemoved.add( value ); }
 
	/** PROPERTY: inFileUploaded. */
	public boolean isInFileUploaded() { return this.inFilesInfo != null && !this.inFilesInfo.isEmpty(); }

	/**
	 * PROPERTY: jobFile.
	 *
	 * The uploaded job file.
	 */
	private IUploadedFile jobFile;
	public IUploadedFile getJobFile() { return this.jobFile; }
	public void setJobFile(IUploadedFile value) { this.jobFile = value; }

	/**
	 * PROPERTY: jobFileInfo.
	 *
	 * The uploaded job file info.
	 */
	private UploadedFileInfoBean jobFileInfo;
	public UploadedFileInfoBean getJobFileInfo() { return this.jobFileInfo; }
	public String getJobFileInfoString() { return this.jobFileInfo.toString(); }
	public void setJobFileInfo(UploadedFileInfoBean value) { this.jobFileInfo = value; }
 
	/** PROPERTY: jobFileUploaded. */
	public boolean isJobFileUploaded() { return this.jobFileInfo != null; }

	/**
	 * PROPERTY: jobName.
	 *
	 * The job name.
	 */
	private String jobName;
	public String getJobName() { return this.jobName; }
	public void setJobName(String value) { this.jobName = value; }

	/** PROPERTY: jobNameMinLen. */
	public int getJobNameMinLen() { return 1; } //TODO

	/** PROPERTY: jobNameMaxLen. */
	public int getJobNameMaxLen() { return 255; } //TODO

	/**
	 * PROPERTY: jobPreview.
	 *
	 * The stringified version of a job.
	 */
	private String jobPreview = null;
	public String getJobPreview() { return this.jobPreview; }
	public void setJobPreview(String value) { this.jobPreview = value; }

	/**
	 * PROPERTY: jobPreviewed.
	 *
	 * Tells if current job is to be previewed.
	 */
	public boolean isJobPreviewed() { return this.getJobPreview() != null; }

	/**
	 * PROPERTY: jobReqs.
	 *
	 * The job requirements.
	 */
	private String jobReqs;
	public String getJobReqs() { return this.jobReqs; }
	public void setJobReqs(String value) { this.jobReqs = value; }

	/**
	 * PROPERTY: execOutFileAsArg.
	 *
	 * Tells if output files should be passed as argument to the executable
	 * command ({@code true}) or not ({@code false}).
	 */
	private boolean execOutFileAsArg;
	public boolean isExecOutFileAsArg() { return this.execOutFileAsArg; }
	public void setExecOutFileAsArg(boolean value) { this.execOutFileAsArg = value; }

	/**
	 * PROPERTY: execOutFileOpt.
	 *
	 * Command line option for passing output file to executable commands.
	 */
	private String execOutFileOpt = ">";
	public String getExecOutFileOpt() { return this.execOutFileOpt; }
	public void setExecOutFileOpt(String value) { this.execOutFileOpt = value; }

	/**
	 * PROPERTY: manualExecutable.
	 *
	 * Flag indicating if the executable is uploaded or manually inserted.
	 */
	private boolean manualExecutable;
	public boolean isManualExecutable() { return this.manualExecutable; }
	public void setManualExecutable(boolean value) { this.manualExecutable = value; }

	/** PROPERTY: manualJobInsertMode. */
	public String getManualJobInsertMode() { return MANUALJOB_INSERTMODE; }

	/**
	 * PROPERTY: oneOutputFilePerTask.
	 *
	 * Flag indicating if each task has associated only one output file.
	 */
	private boolean oneOutputFilePerTask;
	public boolean isOneOutputFilePerTask() { return this.oneOutputFilePerTask; }
	public void setOneOutputFilePerTask(boolean value) { this.oneOutputFilePerTask = value; }

	/**
	 * PROPERTY: outFile.
	 *
	 * The last output file added.
	 */
	private String outFile;
	public String getOutFile() { return this.outFile; }
	public void setOutFile(String value) { this.outFile = value; }

	/** PROPERTY: outFileAdded. */
	public boolean isOutFileAdded() { return this.outFilesInfo != null && !this.outFilesInfo.isEmpty(); }

	/**
	 * PROPERTY: outFilesInfo.
	 *
	 * The output files.
	 */
	private List<String> outFilesInfo = new ArrayList<String>();
	public List<String> getOutFilesInfo() { return this.outFilesInfo; }
	public void setOutFilesInfo(List<String> value) { this.outFilesInfo = (value != null ) ? new ArrayList<String>(value) : new ArrayList<String>(); }
	public void addOutFileInfo(String value) { this.outFilesInfo.add( value ); }
	public void removeOutFileInfo(String value) { this.outFilesInfo.remove( value ); }
 
	/**
	 * PROPERTY: outFilesInfoBak.
	 *
	 * A backup copy of the output files.
	 */
	private List<String> outFilesInfoBak = new ArrayList<String>();
	protected List<String> getOutFilesInfoBak() { return this.outFilesInfoBak; }
	protected void setOutFilesInfoBak(List<String> value) { this.outFilesInfoBak = (value != null ) ? new ArrayList<String>(value) : new ArrayList<String>(); }
	protected void addOutFileInfoBak(String value) { this.outFilesInfoBak.add( value ); }
	protected void removeOutFileInfoBak(String value) { this.outFilesInfoBak.remove( value ); }
 
	/**
	 * PROPERTY: outFilesInfoRemoved.
	 *
	 * The output files to remove.
	 */
	private List<String> outFilesInfoRemoved = new ArrayList<String>();
	public List<String> getOutFilesInfoRemoved() { return this.outFilesInfoRemoved; }
	public void setOutFilesInfoRemoved(List<String> value) { this.outFilesInfoRemoved = (value != null) ? new ArrayList<String>(value) : new ArrayList<String>(); }
	public void addOutFileInfoRemoved(String value) { this.outFilesInfoRemoved.add( value ); }
 
	/**
	 * PROPERTY: outFileFile.
	 *
	 * The uploaded executable command arguments file.
	 */
	private IUploadedFile outFileFile;
	public IUploadedFile getOutFileFile() { return this.outFileFile; }
	public void setOutFileFile(IUploadedFile value) { this.outFileFile = value; }

	/**
	 * PROPERTY: outFileFileInfo.
	 *
	 * The uploaded executable command arguments file info.
	 */
	private UploadedFileInfoBean outFileFileInfo;
	public UploadedFileInfoBean getOutFileFileInfo() { return this.outFileFileInfo; }
	public String getOutFileFileInfoString() { return this.outFileFileInfo.toString(); }
	public void setOutFileFileInfo(UploadedFileInfoBean value) { this.outFileFileInfo = value; }
 
	/** PROPERTY: outFileFileMinLen. */
	public int getOutFileFileMinLen() { return 1; }

	/** PROPERTY: outFileFileMaxLen. */
	public int getOutFileFileMaxLen() { return 255; } //TODO

	/** PROPERTY: outFileFileUploaded. */
	public boolean isOutFileFileUploaded() { return this.outFileFileInfo != null; }

	/** PROPERTY: simpleTaskViewMode. */
	public boolean isSimpleTaskViewMode() { return this.taskViewMode.equals(SIMPLE_TASKVIEWMODE); }

	/** PROPERTY: taskAdded. */
	public boolean isTaskAdded() { return this.tasks != null && !this.tasks.isEmpty(); }

	/**
	 * PROPERTY: taskExecCmd.
	 *
	 * The task executable command.
	 */
	private String taskExecCmd;
	public String getTaskExecCmd() { return this.taskExecCmd; }
	public void setTaskExecCmd(String value) { this.taskExecCmd = value; }

	/**
	 * PROPERTY: taskIds.
	 *
	 * The task id list.
	 */
	private List<Integer> taskIds = new ArrayList<Integer>();
	public List<Integer> getTaskIds()
	{
		if ( Collections.IsNullOrEmpty(this.taskIds) )
		{
			for (int i=0; i < this.getTasks().size(); i++)
			{
				this.taskIds.add(i+1);
			}
		}
		return this.taskIds;
	}
//	public void setTaskIds(List<Integer> value) { this.taskIds = new ArrayList<Integer>(value); }
//	public void addTaskId(Integer value) { this.taskIds.add( value ); }
//	public void removeTaskId(Integer value) { this.taskIds.remove( value ); }

	/**
	 * PROPERTY: taskInFile.
	 *
	 * The last uploaded task input file.
	 */
	private IUploadedFile taskInFile;
	public IUploadedFile getTaskInFile() { return this.taskInFile; }
	public void setTaskInFile(IUploadedFile value) { this.taskInFile = value; }

	/**
	 * PROPERTY: taskInFilesInfo.
	 *
	 * The uploaded task input files.
	 */
	private Map<String,UploadedFileInfoBean> taskInFilesInfo = new TreeMap<String,UploadedFileInfoBean>();
	public Map<String,UploadedFileInfoBean> getTaskInFilesInfoMap() { return this.taskInFilesInfo; }
	public Collection<String> getTaskInFilesName() { return this.taskInFilesInfo.keySet(); }
	public Collection<UploadedFileInfoBean> getTaskInFilesInfo() { return this.taskInFilesInfo.values(); }
	public void setTaskInFilesInfo(Collection<UploadedFileInfoBean> value)
	{
		this.taskInFilesInfo.clear();
		for (UploadedFileInfoBean taskInFileInfo : value)
		{
			this.taskInFilesInfo.put( taskInFileInfo.getName(), taskInFileInfo );
		}
	}
	public void addTaskInFileInfo(UploadedFileInfoBean value) { this.taskInFilesInfo.put( value.getName(), value ); }
	public void removeTaskInFileInfo(UploadedFileInfoBean value) { this.taskInFilesInfo.remove( value.getName() ); }
 
	/**
	 * PROPERTY: taskInFilesInfoRemoved.
	 *
	 * The uploaded task input files to remove.
	 */
	private List<String> taskInFilesInfoRemoved = new ArrayList<String>();
	public List<String> getTaskInFilesInfoRemoved() { return this.taskInFilesInfoRemoved; }
	public void setTaskInFilesInfoRemoved(List<String> value) { this.taskInFilesInfoRemoved = (value != null) ? new ArrayList<String>(value) : new ArrayList<String>(); }
	public void addTaskInFileInfoRemoved(String value) { this.taskInFilesInfoRemoved.add( value ); }
 
	/** PROPERTY: taskInFileUploaded. */
	public boolean isTaskInFileUploaded() { return this.taskInFilesInfo != null && !this.taskInFilesInfo.isEmpty(); }

	/**
	 * PROPERTY: taskInfos.
	 *
	 * The task info list.
	 */
	private List<TaskInfoBean> taskInfos = new ArrayList<TaskInfoBean>();
	public List<TaskInfoBean> getTaskInfos()
	{
		if ( Collections.IsNullOrEmpty(this.taskInfos) )
		{
			for (int i=0; i < this.getTasks().size(); i++)
			{
				this.taskInfos.add( new TaskInfoBean(i+1, this.getTasks().get(i), 30));
			}
		}
		return this.taskInfos;
	}

	/**
	 * PROPERTY: taskOutFile.
	 *
	 * The last task output file added.
	 */
	private String taskOutFile;
	public String getTaskOutFile() { return this.taskOutFile; }
	public void setTaskOutFile(String value) { this.taskOutFile = value; }

	/** PROPERTY: taskOutFileAdded. */
	public boolean isTaskOutFileAdded() { return this.taskOutFilesInfo != null && !this.taskOutFilesInfo.isEmpty(); }

	/**
	 * PROPERTY: taskOutFilesInfo.
	 *
	 * The task output files.
	 */
	private List<String> taskOutFilesInfo = new ArrayList<String>();
	public List<String> getTaskOutFilesInfo() { return this.taskOutFilesInfo; }
	public void setTaskOutFilesInfo(List<String> value) { this.taskOutFilesInfo = (value != null) ? new ArrayList<String>(value) : new ArrayList<String>(); }
	public void addTaskOutFileInfo(String value) { this.taskOutFilesInfo.add( value ); }
	public void removeTaskOutFileInfo(String value) { this.taskOutFilesInfo.remove( value ); }
 
	/**
	 * PROPERTY: taskOutFilesInfoRemoved.
	 *
	 * The task output files to remove.
	 */
	private List<String> taskOutFilesInfoRemoved = new ArrayList<String>();
	public List<String> getTaskOutFilesInfoRemoved() { return this.taskOutFilesInfoRemoved; }
	public void setTaskOutFilesInfoRemoved(List<String> value) { this.taskOutFilesInfoRemoved = (value != null) ? new ArrayList<String>(value) : new ArrayList<String>(); }
	public void addTaskOutFileInfoRemoved(String value) { this.taskOutFilesInfoRemoved.add( value ); }
 
//	/**
//	 * PROPERTY: tasksNum.
//	 *
//	 * The number of identical tasks.
//	 */
//	private int tasksNum = 1;
//	public int getTasksNum() { return this.tasksNum; }
//	public void setTasksNum(int value) { this.tasksNum = value; }

	/**
	 * PROPERTY: tasks.
	 *
	 * The task list.
	 */
	private List<IBotTask> tasks = new ArrayList<IBotTask>();
	public List<IBotTask> getTasks() { return this.tasks; }
	public void setTasks(List<IBotTask> value) { this.tasks = (value != null) ? new ArrayList<IBotTask>(value) : new ArrayList<IBotTask>(); }
	public void addTask(IBotTask value) { this.tasks.add( value ); }
	public void removeTask(IBotTask value) { this.tasks.remove( value ); }

	/**
	 * PROPERTY: tasksRemoved.
	 *
	 * The task id list to remove.
	 */
	private List<String> tasksRemoved = new ArrayList<String>();
	public List<String> getTasksRemoved() { return this.tasksRemoved; }
	public void setTasksRemoved(List<String> value) { this.tasksRemoved = (value != null) ? new ArrayList<String>(value) : new ArrayList<String>(); }
	public void addTaskRemoved(String value) { this.tasksRemoved.add( value ); }
 
	/**
	 * PROPERTY: taskViewMode.
	 *
	 * The type of task insertion.
	 */
	private String taskViewMode = SIMPLE_TASKVIEWMODE;
	public String getTaskViewMode() { return this.taskViewMode; }
	public void setTaskViewMode(String value) { this.taskViewMode = value; }

	/** PROPERTY: taskViewModeAdvanced. */
	public String getTaskViewModeAdvanced() { return ADVANCED_TASKVIEWMODE; }

	/** PROPERTY: taskViewModeSimple. */
	public String getTaskViewModeSimple() { return SIMPLE_TASKVIEWMODE; }

	/**
	 * PROPERTY: uploadOutFile.
	 *
	 * Flag indicating if the output files are imported
	 * from a text file or manually inserted.
	 */
	private boolean uploadOutFile = false;
	public boolean isUploadOutFile() { return this.uploadOutFile; }
	public void setUploadOutFile(boolean value) { this.uploadOutFile = value; }

	/**
	 * PROPERTY: uploadExecCmdArgs.
	 *
	 * Flag indicating if the executable command arguments are imported
	 * from a text file or manually inserted.
	 */
	private boolean uploadExecCmdArgs = false;
	public boolean isUploadExecCmdArgs() { return this.uploadExecCmdArgs; }
	public void setUploadExecCmdArgs(boolean value) { this.uploadExecCmdArgs = value; }

	///@} Properties ///////////////////////////////////////////////////////

	///@{ Actions //////////////////////////////////////////////////////////

	/**
	 * Action for adding an output file to current job.
	 */
	public String addOutFileAction()
	{
		boolean allOk = true;

		if ( !Strings.IsNullOrEmpty( this.getOutFile() ) )
		{
			this.addOutFileInfo( this.getOutFile() );
			this.setOutFile( "" );

			ViewUtil.AddInfoMessage( MessageConstants.ITEM_ADD_OK );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.ITEM_ADD_KO );
		}

		return allOk ? "out-add-ok" : "out-add-ko";
	}

	/**
	 * Action for adding a task
	 */
	public String addTaskAction()
	{
		boolean allOk = true;

		IBotTask task = null;

		try
		{
			IGridJobExecutionService jobSvc = null;

			jobSvc = ServiceFactory.instance().gridJobExecutionService();

			task = new BotTask();

			// Creates a stage-in section (if needed)
			if ( !Collections.IsNullOrEmpty( this.getTaskInFilesInfo() ) )
			{
				IStageIn taskStageIn = new StageIn();

				for (UploadedFileInfoBean inFileInfo : this.getTaskInFilesInfo())
				{
					// Creates a stage-in action for the input file
					IStageInAction action = new StageInAction();
					//action.setLocalName( inFileInfo.getRealPath() );
					action.setLocalName( inFileInfo.getName() );
					action.setRemoteName( inFileInfo.getName() );
					action.setType( StageInType.VOLATILE ); //FIXME
					action.setMode( StageInMode.ALWAYS_OVERWRITE ); //FIXME
					
					// Adds the action to the task stage-in rule-set
					taskStageIn.addRule(
						new StageInRule( action )
					);
				}

				task.setStageIn( taskStageIn  );
			}

			if ( !Strings.IsNullOrEmpty( this.getTaskExecCmd() ) )
			{
				// Sets the executable command string
				task.addCommand(
					new RemoteCommand(
						this.getTaskExecCmd()
					)
				);
			}

			// Creates a stage-out section (if needed)
			if ( !Collections.IsNullOrEmpty( this.getTaskOutFilesInfo() ) )
			{
				IStageOut taskStageOut = new StageOut();

				for (String outFileInfo : this.getTaskOutFilesInfo())
				{
					// Creates a stage-out action for the output file
					IStageOutAction action = new StageOutAction();
					action.setRemoteName( outFileInfo );
					action.setLocalName(
						jobSvc.createTaskUniqueFileName( outFileInfo ) //TODO
					);
//					String localFileName = null;
//					String basePath = null;
//					basePath = UserFacade.instance().getUserGridStageOutLocalPath( this.getSessionBean().getUser(), /*FIXME: job name might not be set */ );
//					localFileName = basePath + File.separator + jobSvc.createTaskUniqueFileName( outFileInfo );
//					action.setLocalName( localFileName );
					action.setMode( StageOutMode.ALWAYS_OVERWRITE );

					// Adds the action to the job stage-out rule-set
					taskStageOut.addRule(
						new StageOutRule( action )
					);
				}

				task.setStageOut( taskStageOut  );
			}
		}
		catch (Exception e)
		{
			allOk = false;

			ViewUtil.AddExceptionMessage( e );
			JobSubmitBean.Log.log( Level.SEVERE, "Error while adding a task.", e );
		}

		if ( allOk && task != null )
		{
			this.addTask( task );

			// clears task form fields
			this.setTaskExecCmd("");
			this.getTaskInFilesInfoMap().clear();
			this.getTaskOutFilesInfo().clear();
			this.getTaskIds().clear();
			this.getTaskInfos().clear();
		}

		if ( allOk )
		{
			ViewUtil.AddInfoMessage( MessageConstants.ITEM_ADD_OK );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.ITEM_ADD_KO );
		}

		return allOk ? "task-add-ok" : "task-add-ko";
	}

	/**
	 * Action for adding an output file to current task.
	 */
	public String addTaskOutFileAction()
	{
		boolean allOk = true;

		if ( !Strings.IsNullOrEmpty( this.getTaskOutFile() ) )
		{
			this.addTaskOutFileInfo( this.getTaskOutFile() );
			this.setTaskOutFile( "" );

			ViewUtil.AddInfoMessage( MessageConstants.ITEM_ADD_OK );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.ITEM_ADD_KO );
		}

		return allOk ? "task-out-add-ok" : "task-out-add-ok";
	}

	public String doCancelAction()
	{
		this.saveState = false;

		return "cancel";
	}

	/**
	 * Action for exporting a job.
	 */
	public String exportJobAction()
	{
		if ( MANUALJOB_INSERTMODE.equals( this.getCurJobInsertMode() ) )
		{
			return this.exportManualJobAction();
		}

		return this.exportJobFileAction();
	}

	/**
	 * Action for exporting an imported job.
	 */
	public String exportJobFileAction()
	{
		if ( !this.isJobFileUploaded() )
		{
			ViewUtil.AddErrorMessage( MessageConstants.MISSING_INFO_JOB_PREVIEW_KO );
			if ( JobSubmitBean.Log.isLoggable(Level.FINE) )
			{
				JobSubmitBean.Log.fine( "Not enough information for exporting a job." ); 
			}

			return "jobfile-preview-ko";
		}

		try
		{
			String job = null;

			job = this.exportJobFile();
 
			String fileName = null;

			// Makes sure the job has a name
			if ( Strings.IsNullOrEmpty( this.getJobName() ) )
			{
				fileName = JobSubmitBean.CreateJobName() + ".jdf";
			}
			else
			{
				fileName = this.getJobName() + ".jdf";
			}

			// Prepare the response object
                        HttpServletResponse response = null;
			response = (HttpServletResponse) this.getFacesContext().getExternalContext().getResponse();
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                        String mimeType = "application/octet-stream";
                        response.setContentType(mimeType);

			// Write the file to the response.
                        ServletOutputStream sos = null;
			sos = response.getOutputStream();
                        sos.print( job );
                        sos.flush();

			// Complete the response
                        this.getFacesContext().responseComplete();
		}
		catch (Exception e)
		{
			ViewUtil.AddExceptionMessage(e);
			JobSubmitBean.Log.log( Level.SEVERE, "Unable to export imported job", e );

			return "jobfile-preview-ko";
		}

		return "jobfile-preview-ok";
	}

	/**
	 * Action for exporting a manual-inserted job.
	 */
	public String exportManualJobAction()
	{
		try
		{
			String job = null;

			job = this.exportManualJob();

			String fileName = null;

			// Makes sure the job has a name
			if ( Strings.IsNullOrEmpty( this.getJobName() ) )
			{
				fileName = JobSubmitBean.CreateJobName() + ".jdf";
			}
			else
			{
				fileName = this.getJobName() + ".jdf";
			}

			// Prepare the response object
                        HttpServletResponse response = null;
			response = (HttpServletResponse) this.getFacesContext().getExternalContext().getResponse();
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                        String mimeType = "application/octet-stream";
                        response.setContentType(mimeType);

			// Write the file to the response.
                        ServletOutputStream sos = null;
			sos = response.getOutputStream();
                        sos.print( job );
                        sos.flush();

			// Complete the response
                        this.getFacesContext().responseComplete();
		}
		catch (Exception e)
		{
			JobSubmitBean.Log.log( Level.SEVERE, "Unable to export manual job.", e );
			ViewUtil.AddExceptionMessage(e);

			return "manjob-preview-ko";
		}

		return "manjob-preview-ok";
	}

	/**
	 * Action for submitting a job.
	 */
	public String jobSubmissionAction()
	{
		if ( MANUALJOB_INSERTMODE.equals( this.getCurJobInsertMode() ) )
		{
			return this.submitManualJobAction();
		}

		return this.submitJobFileAction();
	}

	/**
	 * Action for previewing a job.
	 */
	public String previewJobAction()
	{
		if ( MANUALJOB_INSERTMODE.equals( this.getCurJobInsertMode() ) )
		{
			return this.previewManualJobAction();
		}

		return this.previewJobFileAction();
	}

	/**
	 * Action for previewing an imported job.
	 */
	public String previewJobFileAction()
	{
//		FileReader frd = null;
//		StringWriter swr = null;

		if ( !this.isJobFileUploaded() )
		{
			ViewUtil.AddErrorMessage( MessageConstants.MISSING_INFO_JOB_PREVIEW_KO );
			if ( JobSubmitBean.Log.isLoggable(Level.FINE) )
			{
				JobSubmitBean.Log.fine( "Not enough information for previewing a job." ); 
			}

			return "jobfile-preview-ko";
		}

//		try
//		{
//			swr = new StringWriter();
//			frd = new FileReader( this.getJobFileInfo().getRealPath() );
//			if ( frd != null )
//			{
//				int bufLen = 2048;
//				char[] buf = new char[bufLen];
//				int nread = 0;
//				while ( (nread = frd.read( buf, 0, bufLen )) != -1 )
//				{
//					swr.write( buf, 0, nread );
//				}
//			}
// 
//			this.setJobPreview( swr.toString() );
//		}
//		catch (Exception e)
//		{
//			ViewUtil.AddExceptionMessage(e);
//			JobSubmitBean.Log.severe( e.toString() );
//			e.printStackTrace(); //TODO: log via the Logger
//
//			return "jobfile-preview-ko";
//		}
//		finally
//		{
//			if ( frd != null )
//			{
//				try { frd.close(); } catch (Exception e) { }
//				frd = null;
//			}
//			if ( swr != null )
//			{
//				try { swr.close(); } catch (Exception e) { }
//				swr = null;
//			}
//		}

		try
		{
			String job = null;

			job = this.exportJobFile();
 
			this.setJobPreview( job );
		}
		catch (Exception e)
		{
			ViewUtil.AddExceptionMessage(e);
			JobSubmitBean.Log.log( Level.SEVERE, "Unable to preview imported job", e );

			return "jobfile-preview-ko";
		}

		return "jobfile-preview-ok";
	}

	/**
	 * Action for previewing a manual-inserted job.
	 */
	public String previewManualJobAction()
	{
//		// Makes sure the job has a name
//		if ( Strings.IsNullOrEmpty( this.getJobName() ) )
//		{
//			this.setJobName( JobSubmitBean.CreateJobName() );
//		}
//
//		try
//		{
//			IBotJob job = null;
//
//			job = this.createBotJob();
//			job = this.prepareBotJobForPreview(job);
//
//			StringWriter swr = new StringWriter();
//			JdfExporter jdfExp = new JdfExporter();
//
//			jdfExp.export( job, new PrintWriter(swr) );
//			this.setJobPreview( swr.toString() );
//
//			swr.close();
//			swr = null;
//		}
//		catch (Exception e)
//		{
//			JobSubmitBean.Log.log( Level.SEVERE, "Error while previewing manual job.", e );
//			ViewUtil.AddExceptionMessage(e);
//
//			return "manjob-preview-ko";
//		}

		try
		{
			String job = null;

			job = this.exportManualJob();

			this.setJobPreview( job );
		}
		catch (Exception e)
		{
			JobSubmitBean.Log.log( Level.SEVERE, "Error while previewing manual job.", e );
			ViewUtil.AddExceptionMessage(e);

			return "manjob-preview-ko";
		}

		return "manjob-preview-ok";
	}

	/**
	 * Action for removing the executable command arguments file from
	 * current job.
	 */
	public String removeExecCmdArgsFileAction() throws IOException
	{
		boolean allOk = true;

		allOk = this.removeUploadedFile(this.getExecCmdArgsFileInfo());

		if ( allOk )
		{
			ViewUtil.AddInfoMessage( MessageConstants.FILE_DELETE_OK, this.execCmdArgsFileInfo.getName() );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.FILE_DELETE_KO, this.execCmdArgsFileInfo.getName() );
		}

		this.setExecCmdArgsFileInfo( null );

		return allOk ? "execargs-remove-ok" : "execargs-remove-ko";
	}

	/**
	 * Action for removing the executable file from current job.
	 */
	public String removeExecFileAction() throws IOException
	{
		boolean allOk = true;

		allOk = this.removeUploadedFile(this.getExecFileInfo());

		if ( allOk )
		{
			ViewUtil.AddInfoMessage( MessageConstants.FILE_DELETE_OK, this.execFileInfo.getName() );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.FILE_DELETE_KO, this.execFileInfo.getName() );
		}

		this.setExecFileInfo( null );

		return allOk ? "exec-remove-ok" : "exec-remove-ko";
	}

	/**
	 * Action for removing one or more extra files from current job.
	 */
	public String removeExtraFilesAction() throws IOException
	{
		boolean allOk = true;

		for (String extraFileName : this.getExtraFilesInfoRemoved())
		{
			UploadedFileInfoBean extraFileInfo = null;

			if ( ! this.getExtraFilesInfoMap().containsKey(extraFileName) )
			{
				allOk = false;
				JobSubmitBean.Log.severe( "Error while removing input file: '" + extraFileInfo.getName() + "'. Not Found." );
				ViewUtil.AddErrorMessage( MessageConstants.FILE_DELETE_KO_NOTFOUND, extraFileInfo.getName() );
				break;
			}

			extraFileInfo = this.getExtraFilesInfoMap().get( extraFileName );

			allOk = this.removeUploadedFile( extraFileInfo );
			if ( !allOk )
			{
				allOk = false;
				JobSubmitBean.Log.severe( "Error while removing input file: '" + extraFileInfo.getName() + "'." );
				ViewUtil.AddErrorMessage( MessageConstants.FILE_DELETE_KO, extraFileInfo.getName() );
				break;
			}
			this.removeExtraFileInfo( extraFileInfo );
		}

		if ( allOk )
		{
			this.getExtraFilesInfoRemoved().clear();

			ViewUtil.AddInfoMessage( MessageConstants.FILES_DELETE_OK );
		}

		return allOk ? "extra-remove-ok" : "extra-remove-ko";
	}

	/**
	 * Action for removing one or more input files from current job.
	 */
	public String removeInFilesAction() throws IOException
	{
		boolean allOk = true;

		for (String inFileName : this.getInFilesInfoRemoved())
		{
			UploadedFileInfoBean inFileInfo = null;

			if ( ! this.getInFilesInfoMap().containsKey(inFileName) )
			{
				allOk = false;
				JobSubmitBean.Log.severe( "Error while removing input file: '" + inFileInfo.getName() + "'. Not Found." );
				ViewUtil.AddErrorMessage( MessageConstants.FILE_DELETE_KO_NOTFOUND, inFileInfo.getName() );
				break;
			}

			inFileInfo = this.getInFilesInfoMap().get( inFileName );

			allOk = this.removeUploadedFile( inFileInfo );
			if ( !allOk )
			{
				allOk = false;
				JobSubmitBean.Log.severe( "Error while removing input file: '" + inFileInfo.getName() + "'." );
				ViewUtil.AddErrorMessage( MessageConstants.FILE_DELETE_KO, inFileInfo.getName() );
				break;
			}
			this.removeInFileInfo( inFileInfo );
		}

		if ( allOk )
		{
			this.getInFilesInfoRemoved().clear();

			ViewUtil.AddInfoMessage( MessageConstants.FILES_DELETE_OK );
		}

		return allOk ? "in-remove-ok" : "in-remove-ko";
	}

	/**
	 * Action for removing the job file.
	 */
	public String removeJobFileAction() throws IOException
	{
		boolean allOk = true;

		allOk = this.removeUploadedFile(this.getJobFileInfo());

		if ( allOk )
		{
			ViewUtil.AddInfoMessage( MessageConstants.FILE_DELETE_OK, this.jobFileInfo.getName() );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.FILE_DELETE_KO, this.jobFileInfo.getName() );
		}

		this.setJobFileInfo( null );

		return allOk ? "job-remove-ok" : "job-remove-ko";
	}

	/**
	 * Action for removing the output file from
	 * current job.
	 */
	public String removeOutFileFileAction() throws IOException
	{
		boolean allOk = true;

		allOk = this.removeUploadedFile(this.getOutFileFileInfo());

		if ( allOk )
		{
			ViewUtil.AddInfoMessage( MessageConstants.FILE_DELETE_OK, this.getOutFileFileInfo().getName() );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.FILE_DELETE_KO, this.getOutFileFileInfo().getName() );
		}

		this.setOutFileFileInfo( null );

		return allOk ? "outfile-remove-ok" : "outfile-remove-ko";
	}

	/**
	 * Action for removing one or more output files from current job.
	 */
	public String removeOutFilesAction()
	{
		boolean allOk = true;

//		for (String outFileName : this.getOutFilesInfoRemoved())
//		{
//			this.removeOutFileInfo( outFileName );
//		}

		this.getOutFilesInfo().removeAll( this.getOutFilesInfoRemoved() );
		this.getOutFilesInfoRemoved().clear();

		if ( allOk )
		{
			ViewUtil.AddInfoMessage( MessageConstants.ITEMS_REMOVE_OK );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.ITEMS_REMOVE_KO );
		}

		return allOk ? "out-remove-ok" : "out-remove-ko";
	}

	/**
	 * Action for removing a task
	 */
	public String removeTaskAction()
	{
		boolean allOk = true;
		int maxIdx = this.getTasks().size() - 1;

		for ( String index : this.getTasksRemoved() )
		{
			int idx = Integer.parseInt( index ) - 1;
			if ( idx >= 0 && idx <= maxIdx )
			{
				this.getTasks().remove( idx );
				this.getTaskInfos().remove( idx );
			}
		}
		this.getTasksRemoved().clear();

		if ( allOk )
		{
			ViewUtil.AddInfoMessage( MessageConstants.ITEMS_REMOVE_OK );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.ITEMS_REMOVE_KO );
		}

		return allOk ? "task-remove-ok" : "task-remove-ko";
	}

	/**
	 * Action for removing one or more input files from current task.
	 */
	public String removeTaskInFilesAction() throws IOException
	{
		boolean allOk = true;

		for (String inFileName : this.getTaskInFilesInfoRemoved())
		{
			UploadedFileInfoBean inFileInfo = null;

			if ( ! this.getTaskInFilesInfoMap().containsKey(inFileName) )
			{
				allOk = false;
				JobSubmitBean.Log.severe( "Error while removing task input file: '" + inFileInfo.getName() + "'. Not Found." );
				ViewUtil.AddErrorMessage( MessageConstants.FILE_DELETE_KO_NOTFOUND, inFileInfo.getName() );
				break;
			}

			inFileInfo = this.getTaskInFilesInfoMap().get( inFileName );

			allOk = this.removeUploadedFile( inFileInfo );
			if ( !allOk )
			{
				allOk = false;
				JobSubmitBean.Log.severe( "Error while removing input file: '" + inFileInfo.getName() + "'." );
				ViewUtil.AddErrorMessage( MessageConstants.FILE_DELETE_KO, inFileInfo.getName() );
				break;
			}
			this.removeTaskInFileInfo( inFileInfo );
		}

		if ( allOk )
		{
			this.getTaskInFilesInfoRemoved().clear();

			ViewUtil.AddInfoMessage( MessageConstants.FILES_DELETE_OK );
		}

		return allOk ? "task-in-remove-ok" : "task-in-remove-ko";
	}

	/**
	 * Action for removing one or more output files to current task.
	 */
	public String removeTaskOutFilesAction()
	{
		boolean allOk = true;

		this.getTaskOutFilesInfo().removeAll( this.getTaskOutFilesInfoRemoved() );
		this.getTaskOutFilesInfoRemoved().clear();

		if ( allOk )
		{
			ViewUtil.AddInfoMessage( MessageConstants.ITEMS_REMOVE_OK );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.ITEMS_REMOVE_KO );
		}

		return allOk ? "task-out-remove-ok" : "task-out-remove-ko";
	}

	/**
	 * Action for submitting an imported job.
	 */
	public String submitJobFileAction()
	{
		if ( !this.isJobFileUploaded() )
		{
			ViewUtil.AddErrorMessage( MessageConstants.MISSING_INFO_JOB_SUBMIT_KO );
			if ( JobSubmitBean.Log.isLoggable(Level.FINE) )
			{
				JobSubmitBean.Log.fine( "Not enough information for submitting a job." ); 
			}

			return "jobfile-submit-ko";
		}

		FileInputStream fis = null;

		try
		{
			IGridJobExecutionService jobSvc = null;
			IBotJob job = null;

			jobSvc = ServiceFactory.instance().gridJobExecutionService();

			JdfImporter jdfImp = new JdfImporter();

			fis = new FileInputStream(
				this.getJobFileInfo().getRealPath()
			);
			job = jdfImp.importBotJob( fis );

			if ( job != null )
			{
				job = this.prepareBotJobForSubmit(job);

				jobSvc = ServiceFactory.instance().gridJobExecutionService();
				jobSvc.executeJob( this.getSessionBean().getUser(), job );
			}
			else
			{
				ViewUtil.AddErrorMessage( MessageConstants.MISSING_INFO_JOB_SUBMIT_KO );
				if ( JobSubmitBean.Log.isLoggable(Level.FINE) )
				{
					JobSubmitBean.Log.fine( "Not enough information for submitting a new job." ); 
				}

				return "jobfile-submit-ko";
			}
                }
                catch (Exception e)
                {
			JobSubmitBean.Log.log( Level.SEVERE, "Error while submitting job file.", e );
                        ViewUtil.AddExceptionMessage(e);

                        return "jobfile-submit-ko";
                }
		finally
		{
			if ( fis != null )
			{
				try { fis.close(); } catch (Exception e) { }
				fis = null;
			}
		}

		// remove current state
		this.saveState = false;

		return "jobfile-submit-ok";
	}

	/**
	 * Action for submitting a manual-inserted job.
	 */
	public String submitManualJobAction()
	{
                try
                {
			IGridJobExecutionService jobSvc = null;
			IBotJob job = null;

			jobSvc = ServiceFactory.instance().gridJobExecutionService();

			job = this.createBotJob();
			job = this.prepareBotJobForSubmit(job);

			if ( job != null )
			{
				jobSvc = ServiceFactory.instance().gridJobExecutionService();
				jobSvc.executeJob( this.getSessionBean().getUser(), job );
			}
			else
			{
				ViewUtil.AddErrorMessage( MessageConstants.MISSING_INFO_JOB_SUBMIT_KO );
				if ( JobSubmitBean.Log.isLoggable(Level.FINE) )
				{
					JobSubmitBean.Log.fine( "Not enough information for submitting a new job." ); 
				}

				return "manjob-submit-ko";
			}
                }
                catch (Exception e)
                {
			JobSubmitBean.Log.log( Level.SEVERE, "Error while submitting manual job.", e );
                        ViewUtil.AddExceptionMessage(e);

                        return "manjob-submit-ko";
                }

		// remove current state
		this.saveState = false;

		return "manjob-submit-ok";
	}

	/**
	 * Action for uploading the executable command arguments file.
	 */
	public String uploadExecCmdArgsFileAction() throws IOException
	{
		boolean allOk = true;

		if ( this.execCmdArgsFileInfo != null )
		{
			allOk = this.removeUploadedFile(this.execFileInfo);
		}

		if ( allOk )
		{
			this.execCmdArgsFileInfo = new UploadedFileInfoBean();

			allOk = this.uploadFile( this.execCmdArgsFile, this.execCmdArgsFileInfo );
		}

		if ( allOk )
		{
			ViewUtil.AddInfoMessage( MessageConstants.FILE_UPLOAD_OK, this.execCmdArgsFileInfo.getName() );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.FILE_UPLOAD_KO, this.execCmdArgsFileInfo.getName() );

			this.setExecCmdArgsFileInfo( null );
		}

		return allOk ? "execargs-upload-ok" : "execargs-upload-ko";
	}

	/**
	 * Action for uploading the executable file (common to all tasks).
	 */
	public String uploadExecFileAction() throws IOException
	{
		boolean allOk = true;

		if ( this.execFileInfo != null )
		{
			allOk = this.removeUploadedFile(this.execFileInfo);
		}

		if ( allOk )
		{
			this.execFileInfo = new UploadedFileInfoBean();

			allOk = this.uploadFile( this.execFile, this.execFileInfo );
		}

		if ( allOk )
		{
			ViewUtil.AddInfoMessage( MessageConstants.FILE_UPLOAD_OK, this.execFileInfo.getName() );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.FILE_UPLOAD_KO, this.execFileInfo.getName() );

			this.setExecFileInfo( null );
		}

		return allOk ? "exec-upload-ok" : "exec-upload-ko";
	}

	/**
	 * Action for uploading an extra file for current job.
	 */
	public String uploadExtraFileAction() throws IOException
	{
		boolean allOk = true;

		UploadedFileInfoBean extraFileInfo = new UploadedFileInfoBean();

		allOk = this.uploadFile( this.getExtraFile(), extraFileInfo );

		if ( allOk )
		{
			this.addExtraFileInfo( extraFileInfo );

			ViewUtil.AddInfoMessage( MessageConstants.FILE_UPLOAD_OK, extraFileInfo.getName() );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.FILE_UPLOAD_KO, extraFileInfo.getName() );
		}

		return allOk ? "extra-upload-ok" : "extra-upload-ko";
	}

	/**
	 * Action for uploading an input file for current job.
	 */
	public String uploadInFileAction() throws IOException
	{
		boolean allOk = true;

		UploadedFileInfoBean inFileInfo = new UploadedFileInfoBean();

		allOk = this.uploadFile( this.getInFile(), inFileInfo );

		if ( allOk )
		{
			this.addInFileInfo( inFileInfo );

			ViewUtil.AddInfoMessage( MessageConstants.FILE_UPLOAD_OK, inFileInfo.getName() );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.FILE_UPLOAD_KO, inFileInfo.getName() );
		}

		return allOk ? "in-upload-ok" : "in-upload-ko";
	}

	/**
	 * Action for importing a job file.
	 */
	public String uploadJobFileAction() throws IOException
	{
		boolean allOk = true;

		// Clean-up previously submitted job-file
		if ( this.jobFileInfo != null )
		{
			allOk = this.removeUploadedFile(this.jobFileInfo);
		}

		// Add the file
		if ( allOk )
		{
			this.jobFileInfo = new UploadedFileInfoBean();

			allOk = this.uploadFile( this.jobFile, this.jobFileInfo );

			if ( allOk )
			{
				FileInputStream fis = null;

				try
				{
					IBotJob job = null;
					JdfImporter jdfImp = new JdfImporter();

					fis = new FileInputStream(
						this.getJobFileInfo().getRealPath()
					);
					job = jdfImp.importBotJob( fis );
					if ( job != null )
					{
						allOk = true;
					}
					else
					{
						allOk = false;
					}
				}
				catch (Exception e)
				{
					ViewUtil.AddExceptionMessage(e);
					JobSubmitBean.Log.severe( e.toString() );
					e.printStackTrace(); //TODO: log via the Logger

					allOk = false;
				}
				finally
				{
					if ( fis != null )
					{
						try { fis.close(); } catch (Exception e) { }
						fis = null;
					}
				}
			}
		}

		if ( allOk )
		{
			ViewUtil.AddInfoMessage( MessageConstants.FILE_UPLOAD_OK, this.jobFileInfo.getName() );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.FILE_UPLOAD_KO, this.jobFileInfo.getName() );

			this.setJobFile( null );
			this.setJobFileInfo( null );
		}

		return allOk ? "job-upload-ok" : "job-upload-ko";
	}

	/**
	 * Action for uploading the executable command arguments file.
	 */
	public String uploadOutFileFileAction() throws IOException
	{
		boolean allOk = true;

		if ( this.getOutFileFileInfo() != null )
		{
			allOk = this.removeUploadedFile(this.execFileInfo);
		}

		if ( allOk )
		{
			this.setOutFileFileInfo( new UploadedFileInfoBean() );

			allOk = this.uploadFile( this.getOutFileFile(), this.getOutFileFileInfo() );
		}

		if ( allOk )
		{
			ViewUtil.AddInfoMessage( MessageConstants.FILE_UPLOAD_OK, this.getOutFileFileInfo().getName() );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.FILE_UPLOAD_KO, this.getOutFileFileInfo().getName() );

			this.setOutFileFileInfo( null );
		}

		return allOk ? "outfile-upload-ok" : "outfile-upload-ko";
	}

	/**
	 * Action for uploading an input file for current task.
	 */
	public String uploadTaskInFileAction() throws IOException
	{
		boolean allOk = true;

		UploadedFileInfoBean inFileInfo = new UploadedFileInfoBean();

		allOk = this.uploadFile( this.getTaskInFile(), inFileInfo );

		if ( allOk )
		{
			this.addTaskInFileInfo( inFileInfo );

			ViewUtil.AddInfoMessage( MessageConstants.FILE_UPLOAD_OK, inFileInfo.getName() );
		}
		else
		{
			ViewUtil.AddErrorMessage( MessageConstants.FILE_UPLOAD_KO, inFileInfo.getName() );
		}

		return allOk ? "task-in-upload-ok" : "task-in-upload-ko";
	}

	///@} Actions //////////////////////////////////////////////////////////

	///@{ Value Change Listeners ///////////////////////////////////////////

	/**
	 * Process the given <code>ValueChangeEvent</code> dispatched when the
	 * job insertion mode changes.
	 */
	public void jobInsertModeChangeHandler(ValueChangeEvent event)
	{
		//this.setCurJobInsertMode( (String) event.getNewValue() );

		String newMode = null;

		newMode = (String) event.getNewValue();

System.err.println("jobInsertModeChangeHandler>> Current Insert Mode: " + this.getCurJobInsertMode());//XXX
System.err.println("jobInsertModeChangeHandler>> New Insert Mode: " + newMode);//XXX
		if ( !newMode.equals( this.getCurJobInsertMode() ) )
		{
			// Clean-up previously submitted form data

			// ... clean-up form data stored in sessions
			Map sessionMap = this.getSessionMap();

			if ( sessionMap != null )
			{
System.err.println("jobInsertModeChangeHandler>> Session Found " );//XXX
				sessionMap.remove(EXEC_CMD_ARGS_ID);
				sessionMap.remove(EXEC_FILE_ID);
				sessionMap.remove(EXTRA_FILES_ID);
				sessionMap.remove(INPUT_FILES_ID);
				sessionMap.remove(JOB_FILE_ID);
//				sessionMap.remove(JOB_INSERT_MODE_ID);
//				sessionMap.remove(JOB_NAME_ID);
//				sessionMap.remove(JOB_REQS_ID);
				sessionMap.remove(OUTPUT_FILES_ID);
				sessionMap.remove(OUTPUT_FILES_FILE_ID);
//				sessionMap.remove(TASK_EXEC_CMD_ID);
				sessionMap.remove(TASK_INPUT_FILES_ID);
				sessionMap.remove(TASK_OUTPUT_FILES_ID);
//				sessionMap.remove(TASK_VIEW_ID);
				sessionMap.remove(TASKS_ID);
			}

			// ... clean-up form data otherwise stored in session (in destroy)
			this.setExecFile( null );
			this.setExecCmdArgsFile( null );
			this.setExtraFile( null );
			this.setInFile( null );
			this.setJobFile( null );
			this.setOutFile( null );
			this.setTaskInFile( null );
			this.setTaskOutFile( null );

			this.setCurJobInsertMode( newMode );
//			this.saveState = false;
		}

		FacesContext.getCurrentInstance( ).renderResponse( );
	}

	///@} Value Change Listeners ///////////////////////////////////////////

	/**
	 * Create a BoT job from form parameters.
	 */
	private IBotJob createBotJob() throws Exception
	{
		IBotJob job = null;

		IGridJobExecutionService jobSvc = null;

		jobSvc = ServiceFactory.instance().gridJobExecutionService();

		if ( this.getTaskViewMode().equals( ADVANCED_TASKVIEWMODE ) )
		{
			// Advanced View

			job = new BotJob( this.getJobName() );

			IStageIn jobStageIn = null; // job stage-in
			IStageOut jobStageOut = null; // job stage-out

			StringBuilder xargs = null; // extra executable arguments

			jobStageIn = new StageIn();
			jobStageOut = new StageOut();
			xargs = new StringBuilder();

			// Check for the kind of executable arguments (if any)
			if ( this.isUploadExecCmdArgs() )
			{
				// empty the manually inserted arguments
				this.setExecCmdArgs(null);

				// Read the args file (if any) and populate the
				// args array
				if ( this.getExecCmdArgsFileInfo() != null )
				{
					// Read the text file
					BufferedReader brd = null;
					try
					{
						brd = new BufferedReader(
							new FileReader(
								this.getExecCmdArgsFileInfo().getRealPath()
							)
						 );

						String line = null;
						List<String> args = new ArrayList<String>();

						while ( (line = brd.readLine()) != null )
						{
							args.add( line );
						}
						this.setExecCmdArgsArray( args.toArray(new String[0]) );
					}
					catch (IOException ioe)
					{
						//JobSubmitBean.Log.log( Level.SEVERE, "Error while reading executable arguments file.", ioe );
						//ViewUtil.AddExceptionMessage(ioe);
						//return null;
						throw new Exception("Error while reading executable arguments file.", ioe);
					}
					finally
					{
						if ( brd != null )
						{
							try { brd.close(); } catch (Exception e) { /* ignore */ }
							brd = null;
						}
					}
				}
			}

			// Check for the kind of output file insertion (if any)
			if ( this.isUploadOutFile() )
			{
				// empty the manually inserted output files
				this.setOutFilesInfoBak( this.getOutFilesInfo() );
				this.setOutFilesInfo(null);
				this.setOutFile(null);

				// Read the args file (if any) and populate the
				// args array
				if ( this.getOutFileFileInfo() != null )
				{
					// Read the text file
					BufferedReader brd = null;
					try
					{
						brd = new BufferedReader(
							new FileReader(
								this.getOutFileFileInfo().getRealPath()
							)
						 );

						String line = null;
						List<String> files = new ArrayList<String>();

						while ( (line = brd.readLine()) != null )
						{
							files.add( line );
						}
						this.setOutFilesInfo( files );
					}
					catch (IOException ioe)
					{
						//JobSubmitBean.Log.log( Level.SEVERE, "Error while reading executable arguments file.", ioe );
						//ViewUtil.AddExceptionMessage(ioe);
						//return null;
						throw new Exception("Error while reading output file.", ioe);
					}
					finally
					{
						if ( brd != null )
						{
							try { brd.close(); } catch (Exception e) { /* ignore */ }
							brd = null;
						}
					}
				}
			}

			// Add job requirements
			if ( !Strings.IsNullOrEmpty( this.getJobReqs() ) )
			{
				// Create a fake-job for getting a job Requirements object

				String fakeJdf = "job:\nrequirements: " + this.getJobReqs() + "\n";
				JdfImporter jdfImp = new JdfImporter();
				IJob fakeJob = jdfImp.importJob(new java.io.StringReader(fakeJdf));
				job.setRequirements(
					//new JobRequirements( this.getJobReqs() )
					fakeJob.getRequirements()
				);
			}

			// Job level stage-in for the Executable file
			if ( this.getExecFileInfo() != null && this.isManualExecutable() )
			{
				// Creates a stage-in action for the executable file
				IStageInAction action = new StageInAction();
				// Note: real-path is added only when submitting the job
				action.setLocalName( this.getExecFileInfo().getName() );
				//action.setLocalName( this.getExecFileInfo().getRealPath() );
				action.setRemoteName( this.getExecFileInfo().getName() );
				action.setType( StageInType.VOLATILE );
				action.setMode( StageInMode.ALWAYS_OVERWRITE );

				// Adds the action to the job stage-in rule-set
				jobStageIn.addRule(
					new StageInRule( action )
				);

//XXX: deferred since the behaviour depends on other parameters
//				// Sets the executable command string
//				job.addCommand(
//					new RemoteCommand(
//						this.getExecFileInfo().getName()
//						+ " "
//						+ ( !Strings.IsNullOrEmpty( this.getExecCmdArgs() ) ? this.getExecCmdArgs() : "" )
//					)
//				);
			}

			// Job level stage-in for input files
			if ( !this.isExecArgInFilesCombined() )
			{
				for (UploadedFileInfoBean inFileInfo : this.getInFilesInfo())
				{
					// Creates a stage-in action for the input file
					IStageInAction action = new StageInAction();
					// Note: real-path is added only when submitting the job
					action.setLocalName( inFileInfo.getName() );
					//action.setLocalName( inFileInfo.getRealPath() );
					action.setRemoteName( inFileInfo.getName() );
					action.setType( StageInType.VOLATILE );
					action.setMode( StageInMode.ALWAYS_OVERWRITE );

					// Adds the action to the job stage-in rule-set
					jobStageIn.addRule(
						new StageInRule( action )
					);

					// Append this input file to the executable arguments
					if ( this.isExecInFileAsArg() )
					{
						if ( xargs.length() > 0 )
						{
							xargs.append(" ");
						}
						xargs.append( this.getExecInFileOpt() + " " + inFileInfo.getName() );
					}
				}
			}
			// Job level stage-in for additional files
			if ( !Collections.IsNullOrEmpty(this.getExtraFilesInfo()) )
			{
				for (UploadedFileInfoBean extraFileInfo : this.getInFilesInfo())
				{
					// Creates a stage-in action for the additional file
					IStageInAction action = new StageInAction();
					// Note: real-path is added only when submitting the job
					action.setLocalName( extraFileInfo.getName() );
					action.setRemoteName( extraFileInfo.getName() );
					action.setType( StageInType.VOLATILE );
					action.setMode( StageInMode.ALWAYS_OVERWRITE );

					// Adds the action to the job stage-in rule-set
					jobStageIn.addRule(
						new StageInRule( action )
					);

					// Append this extra file to the executable arguments
					if ( this.isExecExtraFileAsArg() )
					{
						if ( xargs.length() > 0 )
						{
							xargs.append(" ");
						}
						xargs.append( this.getExecExtraFileOpt() + " " + extraFileInfo.getName() );
					}
				}
			}
			job.setStageIn( jobStageIn );

			// Job level stage-out for output files
			// (Set only if the option "one output file per task" is false)
			if ( !Collections.IsNullOrEmpty(this.getOutFilesInfo()) && !this.isOneOutputFilePerTask())
			{
				for (String outFileInfo : this.getOutFilesInfo())
				{
					// Creates a stage-out action for the output file
					IStageOutAction action = new StageOutAction();
					action.setRemoteName( outFileInfo );
					action.setLocalName(
						jobSvc.createTaskUniqueFileName( outFileInfo ) //TODO
					);
//					String localFileName = null;
//					String basePath = null;
//					basePath = UserFacade.instance().getUserGridStageOutLocalPath( this.getSessionBean().getUser(), job.getName() );
//					localFileName = basePath + File.separator + jobSvc.createTaskUniqueFileName( outFileInfo );
//					action.setLocalName( localFileName );
					action.setMode( StageOutMode.ALWAYS_OVERWRITE );

					// Adds the action to the job stage-out rule-set
					jobStageOut.addRule(
						new StageOutRule( action )
					);

					// Append this output file to the executable arguments
					if ( this.isExecOutFileAsArg() )
					{
						if ( xargs.length() > 0 )
						{
							xargs.append(" ");
						}
						xargs.append( this.getExecOutFileOpt() + " " + outFileInfo );
					}
				}
			}
			job.setStageOut( jobStageOut );

			/// Add tasks

			// Check for executable: manually inserted of uploaded file
			String execName = null;

			if ( this.isManualExecutable() )
			{
				execName = this.getExecName();
			}
			else if ( this.getExecFileInfo() != null )
			{
				execName = this.getExecFileInfo().getName();
			}
			if ( Strings.IsNullOrEmpty(execName) )
			{
				//ViewUtil.AddWarnMessage("Executable command not specified (neither uploaded nor manually inserted)"); //NO I18N
				//return null;
				throw new Exception("Executable command not specified (neither uploaded nor manually inserted)"); //NO I18N
			}

			// Going to create a task for each command argument
			// --> one line => one task
			if ( !Arrays.IsNullOrEmpty(this.getExecCmdArgsArray()) )
			{
				// Add one task for each command argument

				//for ( int i = 1;  i < this.getTasksNum(); i++ )
				int taskNum = 0;
				for (String cmdArg : this.getExecCmdArgsArray())
				{
					// Skip empty lines
					if ( Strings.IsNullOrEmpty( cmdArg ) )
					{
						continue;
					}

					// Check for how to treat input files
					if ( this.isExecArgInFilesCombined() )
					{
						// Creates one task for the current arguments and for each input file
						// (cartesian product)

						for (UploadedFileInfoBean inFileInfo : this.getInFilesInfo())
						{
							IBotTask task = new BotTask();

							StringBuilder xargs2 = new StringBuilder();

							// Creates a stage-in section for this task

							IStageIn taskStageIn = new StageIn();
							// Creates a stage-in action for the input file
							IStageInAction inAction = new StageInAction();
							// Note: real-path is added only when submitting the job
							inAction.setLocalName( inFileInfo.getName() );
							//action.setLocalName( inFileInfo.getRealPath() );
							inAction.setRemoteName( inFileInfo.getName() );
							inAction.setType( StageInType.VOLATILE );
							inAction.setMode( StageInMode.ALWAYS_OVERWRITE );
							// Adds the action to the task stage-in rule-set
							taskStageIn.addRule(
								new StageInRule( inAction )
							);
							// Adds the stage-in section to this task
							task.setStageIn( taskStageIn );

							// Creates a stage-out section for this task

							if (
								this.isOneOutputFilePerTask()
								&& !Collections.IsNullOrEmpty(this.getOutFilesInfo())
							) {
								// Safety check for preventing out-of-bound accesses
								if ( taskNum >= this.getOutFilesInfo().size() )
								{
									// Reset the counter and start from the beginning
									taskNum = 0;
								}

								String outFileInfo = this.getOutFilesInfo().get(taskNum);

								IStageOut taskStageOut = new StageOut();
								// Creates a stage-in action for the input file
								IStageOutAction outAction = new StageOutAction();
								outAction.setRemoteName( outFileInfo );
								// Note: real-path is added only when submitting the job
								outAction.setLocalName(
									jobSvc.createTaskUniqueFileName( outFileInfo ) //TODO
								);
								outAction.setMode( StageOutMode.ALWAYS_OVERWRITE );
								// Adds the action to the task stage-in rule-set
								taskStageOut.addRule(
									new StageOutRule( outAction )
								);
								// Adds the stage-in section to this task
								task.setStageOut( taskStageOut );

								// Append this output file to the executable arguments
								if ( this.isExecOutFileAsArg() )
								{
									if ( !Strings.IsNullOrEmpty(this.getExecOutFileOpt()) )
									{
										if ( xargs2.length() > 0 )
										{
											xargs2.append(" ");
										}
										xargs2.append( this.getExecOutFileOpt() );
									}
									if ( xargs2.length() > 0 )
									{
										xargs2.append(" ");
									}
									xargs2.append( outFileInfo );
								}

								taskNum++;
							}

							// Creates a remote section for this task

							// Sets the executable command string
							task.addCommand(
								new RemoteCommand(
									//this.getExecFileInfo().getName() // TODO: should this path be prefixed with a "real remote path"?
									execName // TODO: should this path be prefixed with a "real remote path"?
									+ ( !Strings.IsNullOrEmpty( cmdArg ) ? (" " + cmdArg) : "" )
									+ ( (this.isExecInFileAsArg() && !Strings.IsNullOrEmpty( this.getExecInFileOpt() )) ? (" " + this.getExecInFileOpt()) : "" )
									+ ( this.isExecInFileAsArg() ? (" " + inFileInfo.getName()) : "" )
									+ ( !Strings.IsNullOrEmpty( xargs2.toString() ) ? (" " + xargs2.toString()) : "" ) // for output files, ...
								)
							);

							// Adds the task to the job
							job.addTask( task );
						}
					}
					else
					{
						// Creates a task with only the remote and final sections

						IBotTask task = new BotTask();

						StringBuilder xargs2 = new StringBuilder();

						// Creates a stage-out section for this task

						if (
							this.isOneOutputFilePerTask()
							&& !Collections.IsNullOrEmpty(this.getOutFilesInfo())
						) {
							// Safety check for preventing out-of-bound accesses
							if ( taskNum >= this.getOutFilesInfo().size() )
							{
								// Reset the counter and start from the beginning
								taskNum = 0;
							}

							String outFileInfo = this.getOutFilesInfo().get(taskNum);

							IStageOut taskStageOut = new StageOut();
							// Creates a stage-in action for the input file
							IStageOutAction outAction = new StageOutAction();
							outAction.setRemoteName( outFileInfo );
							// Note: real-path is added only when submitting the job
							outAction.setLocalName(
								jobSvc.createTaskUniqueFileName( outFileInfo ) //TODO
							);
							outAction.setMode( StageOutMode.ALWAYS_OVERWRITE );
							// Adds the action to the task stage-in rule-set
							taskStageOut.addRule(
								new StageOutRule( outAction )
							);
							// Adds the stage-in section to this task
							task.setStageOut( taskStageOut );

							// Append this output file to the executable arguments
							if ( this.isExecOutFileAsArg() )
							{
								if ( !Strings.IsNullOrEmpty(this.getExecOutFileOpt()) )
								{
									if ( xargs2.length() > 0 )
									{
										xargs2.append(" ");
									}
									xargs2.append( this.getExecOutFileOpt() );
								}
								if ( xargs2.length() > 0 )
								{
									xargs2.append(" ");
								}
								xargs2.append( outFileInfo );
							}

							taskNum++;
						}

						// Sets the executable command string
						task.addCommand(
							new RemoteCommand(
								//this.getExecFileInfo().getName() // TODO: should this path be prefixed with a "real remote path"?
								execName // TODO: should this path be prefixed with a "real remote path"?
								+ ( !Strings.IsNullOrEmpty( cmdArg ) ? (" " + cmdArg) : "" )
								+ ( !Strings.IsNullOrEmpty( xargs.toString() ) ? (" " + xargs.toString()) : "" ) // for input files, extra files, ...
								+ ( !Strings.IsNullOrEmpty( xargs2.toString() ) ? (" " + xargs2.toString()) : "" ) // for output files, ...
							)
						);

						job.addTask( task );
					}
				}
			}
			else
			{
				// Add "empty" tasks: either one for each input file (if any) or only one.

				if ( !Collections.IsNullOrEmpty(this.getInFilesInfo()) && this.isExecArgInFilesCombined() )
				{
					// ...add one task for each input file is specified

					int taskNum = 0;

					for (UploadedFileInfoBean inFileInfo : this.getInFilesInfo())
					{
						IBotTask task = new BotTask();

						StringBuilder xargs2 = new StringBuilder();

						// Creates a stage-in section for this task

						IStageIn taskStageIn = new StageIn();
						// Creates a stage-in action for the input file
						IStageInAction action = new StageInAction();
						// Note: real-path is added only when submitting the job
						action.setLocalName( inFileInfo.getName() );
						//action.setLocalName( inFileInfo.getRealPath() );
						action.setRemoteName( inFileInfo.getName() );
						action.setType( StageInType.VOLATILE );
						action.setMode( StageInMode.ALWAYS_OVERWRITE );
						// Adds the action to the task stage-in rule-set
						taskStageIn.addRule(
							new StageInRule( action )
						);
						// Adds the stage-in section to this task
						task.setStageIn( taskStageIn );

						// Creates a stage-out section for this task

						if (
							this.isOneOutputFilePerTask()
							&& !Collections.IsNullOrEmpty(this.getOutFilesInfo())
						) {
							// Safety check for preventing out-of-bound accesses
							if ( taskNum >= this.getOutFilesInfo().size() )
							{
								// Reset the counter and start from the beginning
								taskNum = 0;
							}

							String outFileInfo = this.getOutFilesInfo().get(taskNum);

							IStageOut taskStageOut = new StageOut();
							// Creates a stage-in action for the input file
							IStageOutAction outAction = new StageOutAction();
							outAction.setRemoteName( outFileInfo );
							// Note: real-path is added only when submitting the job
							outAction.setLocalName(
								jobSvc.createTaskUniqueFileName( outFileInfo ) //TODO
							);
							outAction.setMode( StageOutMode.ALWAYS_OVERWRITE );
							// Adds the action to the task stage-in rule-set
							taskStageOut.addRule(
								new StageOutRule( outAction )
							);
							// Adds the stage-in section to this task
							task.setStageOut( taskStageOut );

							// Append this output file to the executable arguments
							if ( this.isExecOutFileAsArg() )
							{
								if ( !Strings.IsNullOrEmpty(this.getExecOutFileOpt()) )
								{
									if ( xargs2.length() > 0 )
									{
										xargs2.append(" ");
									}
									xargs2.append( this.getExecOutFileOpt() );
								}
								if ( xargs2.length() > 0 )
								{
									xargs2.append(" ");
								}
								xargs2.append( outFileInfo );
							}

							taskNum++;
						}

						// Creates a remote section for this task

						// Sets the executable command string
						task.addCommand(
							new RemoteCommand(
								execName // TODO: should this path be prefixed with a "real remote path"?
								+ ( (this.isExecInFileAsArg() && !Strings.IsNullOrEmpty( this.getExecInFileOpt() )) ? (" " + this.getExecInFileOpt()) : "" )
								+ ( this.isExecInFileAsArg() ? (" " + inFileInfo.getName()) : "" )
								+ ( !Strings.IsNullOrEmpty( xargs.toString() ) ? (" " + xargs.toString()) : "" ) // for input files, extra files, ...
								+ ( !Strings.IsNullOrEmpty( xargs2.toString() ) ? (" " + xargs2.toString()) : "" ) // for output files, ...
							)
						);

						job.addTask( task );
					}
				}
				else
				{
					// Just insert a remote section at job level...
					job.addCommand(
							new RemoteCommand(
								execName // TODO: should this path be prefixed with a "real remote path"?
								+ ( !Strings.IsNullOrEmpty( xargs.toString() ) ? (" " + xargs.toString()) : "" ) // for output files, ...
							)
					);

					// ...add an empty task if no input file
					// is specified or if input files and
					// arguments haven't to be combined.
					// Note: input file, if any, are all
					// associated to this unique task

					IBotTask task = new BotTask();

					job.addTask( task );
				}
			}

			// restore the manually inserted output files
			if ( this.isUploadOutFile() )
			{
				this.setOutFilesInfo( this.getOutFilesInfoBak() );
				this.setOutFilesInfoBak(null);
			}
		}
		else if ( this.getTaskViewMode().equals( SIMPLE_TASKVIEWMODE ) )
		{
			// Simple View

			if ( this.getTasks().size() > 0 )
			{
				job = new BotJob( this.getJobName() );
				job.setTasks( this.getTasks() );
			}
		}
		else
		{
			//ViewUtil.AddErrorMessage("Bad Task-View mode."); // NO I18N
			//return null;
			throw new Exception("Bad Task-View mode."); //NO I18N
		}

		return job;
	}

	/** Returns a job name specific for the current job. */
	private static String CreateJobName()
	{
		SimpleDateFormat dtFormatter = new SimpleDateFormat( "yyyyMMdd_HHmm" );

		return "sgweb_" + dtFormatter.format( new Date() );
	}

	/**
	 * Export an imported job.
	 */
	public String exportJobFile() throws Exception
	{
		String expJob = null;
		FileReader frd = null;
		StringWriter swr = null;

		if ( !this.isJobFileUploaded() )
		{
			throw new Exception("No job file imported.");
		}

		swr = new StringWriter();
		frd = new FileReader( this.getJobFileInfo().getRealPath() );
		if ( frd != null )
		{
			int bufLen = 2048;
			char[] buf = new char[bufLen];
			int nread = 0;
			while ( (nread = frd.read( buf, 0, bufLen )) != -1 )
			{
				swr.write( buf, 0, nread );
			}
		}

		expJob = swr.toString();

		if ( frd != null )
		{
			try { frd.close(); } catch (Exception e) { /* ignore */ }
			frd = null;
		}
		if ( swr != null )
		{
			try { swr.close(); } catch (Exception e) { /* ignore */ }
			swr = null;
		}

		return expJob;
	}

	/**
	 * Export a manual-inserted job.
	 */
	public String exportManualJob() throws Exception
	{
		// Makes sure the job has a name
		if ( Strings.IsNullOrEmpty( this.getJobName() ) )
		{
			this.setJobName( JobSubmitBean.CreateJobName() );
		}

		String expJob = null;
		IBotJob job = null;

		job = this.createBotJob();
		job = this.prepareBotJobForExport(job);

		StringWriter swr = new StringWriter();
		JdfExporter jdfExp = new JdfExporter();

		jdfExp.export( job, new PrintWriter(swr) );
		expJob = swr.toString();

		if ( swr != null )
		{
			try { swr.close(); } catch (Exception e) { /* ignore */ }
			swr = null;
		}

		return expJob;
	}

	/**
	 * Prepare the given BoT job for the preview.
	 */
	private IBotJob prepareBotJobForExport(IBotJob job)
	{
		// passthrough

		return job;
	}

//	/**
//	 * Prepare the given BoT job for the preview.
//	 */
//	private IBotJob prepareBotJobForPreview(IBotJob job)
//	{
//		return this.prepareBotJobForExport(job);
//	}

	/**
	 * Prepare the given BoT job for the submission.
	 */
	private IBotJob prepareBotJobForSubmit(IBotJob job)
	{
		// Makes sure job gets a symbolic name.
		// This will be the root folder where stoting
		// this job files.
		if ( Strings.IsNullOrEmpty( job.getName() ) )
		{
			job.setName( JobSubmitBean.CreateJobName() );
		}

		// Add real paths

		String baseInPath = null; // Base path for local stage-in files
		String baseOutPath = null; // Base path for local stage-out files
		String uploadPath = null; // Base path for local stage-out files

		baseInPath = UserFacade.instance().getUserGridStageInLocalPath( this.getSessionBean().getUser(), job.getName(), true );
		baseOutPath = UserFacade.instance().getUserGridStageOutLocalPath( this.getSessionBean().getUser(), job.getName(), true );
		//ServletContext svlCtx = (ServletContext) this.getExternalContext().getContext();
		//uploadPath = svlCtx.getRealPath( Conf.instance().getWebUploadPath() );
		uploadPath = UserFacade.instance().getUserTmpPath( this.getSessionBean().getUser(), false );

		if ( job.getStageIn() != null && job.getStageIn().getRules() != null )
		{
			for (IStageInRule rule : job.getStageIn().getRules())
			{
				for (IStageInAction action : rule.getActions())
				{
					String oldName = uploadPath + File.separator + action.getLocalName();
					String newName = baseInPath + File.separator + action.getLocalName();

					FileUtil.TryMove( oldName, newName );
					action.setLocalName( newName );
				}
			}
		}
		if ( job.getStageOut() != null && job.getStageOut().getRules() != null )
		{
			for (IStageOutRule rule : job.getStageOut().getRules())
			{
				for (IStageOutAction action : rule.getActions())
				{
					action.setLocalName(
						baseOutPath + File.separator + action.getLocalName()
					);
				}
			}
		}
		if ( job.getTasks() != null )
		{
			for (IBotTask task : job.getTasks())
			{
				if ( task.getStageIn() != null && task.getStageIn().getRules() != null )
				{
					for (IStageInRule rule : task.getStageIn().getRules())
					{
						for (IStageInAction action : rule.getActions())
						{
							String oldName = uploadPath + File.separator + action.getLocalName();
							String newName = baseInPath + File.separator + action.getLocalName();

							FileUtil.TryMove( oldName, newName );
							action.setLocalName( newName );
						}
					}
				}
				if ( task.getStageOut() != null && task.getStageOut().getRules() != null )
				{
					for (IStageOutRule rule : task.getStageOut().getRules())
					{
						for (IStageOutAction action : rule.getActions())
						{
							action.setLocalName(
								baseOutPath + File.separator + action.getLocalName()
							);
						}
					}
				}
			}
		}

		return job;
	}

	/**
	 * Removes the given uploaded file from the server.
	 */
	protected boolean removeUploadedFile(UploadedFileInfoBean uploadInfo)
	{
		boolean retVal = true;

		if ( uploadInfo != null )
		{
			try
			{
				new File( uploadInfo.getRealPath() ).delete();

				retVal = true;
			}
			catch (SecurityException se)
			{
				JobSubmitBean.Log.warning( "Unable to delete uploaded file '" + uploadInfo.getRealPath() + "': " + se.toString() );
				retVal = false;
			}
		}

		return retVal;
	}

	/**
	 * Helper method for storing the uploaded file in a well-known location.
	 */
	private static String StoreUploadedFile(IUploadedFile upload, String outFileName) throws IOException
	{
		OutputStream os = null;
		InputStream is = null;
		byte[] inDigest = null;
		byte[] outDigest = null;

		try
		{
			MessageDigest inDigestAlg = MessageDigest.getInstance("MD5");
			MessageDigest outDigestAlg = MessageDigest.getInstance("MD5");

			is = new BufferedInputStream(
				new DigestInputStream(
					upload.getInputStream(),
					inDigestAlg
				)
			);

			os = new BufferedOutputStream(
				new DigestOutputStream(
					new FileOutputStream(
						outFileName
					),
					outDigestAlg
				)
			);

			int bufLen = 2048;
			int nread = 0;
			byte[] buf = new byte[ bufLen ];
			while ( ( nread = is.read( buf ) ) != -1 )
			{
				if ( nread > 0 )
				{
					os.write( buf, 0, nread );
				}
			}
			os.flush();

			outDigest = outDigestAlg.digest();
			inDigest = inDigestAlg.digest();
		}
		catch (NoSuchAlgorithmException nsae)
		{
			// This shouldn't never happer because the algorithm
			// name is hardcoded.
			ViewUtil.AddExceptionMessage( nsae );
			nsae.printStackTrace(); //TODO: log via the Logger
		}
		finally
		{
			if ( os != null )
			{
				try
				{
					os.close();
					os = null;
				}
				catch (IOException ex)
				{
					ex.printStackTrace(); //TODO: log via the Logger
				}
			}
			if ( is != null )
			{
				try
				{
					is.close();
					is = null;
				}
				catch (IOException ex)
				{
					ex.printStackTrace(); //TODO: log via the Logger
				}
			}
		}


		if (
			inDigest == null
			|| outDigest == null
			|| !MessageDigest.isEqual( inDigest, outDigest )
		)
		{
			throw new IOException( "File signature doesn't match" );
		}

		// Make the uploaded file executable
		try
		{
			File of = new File(outFileName);
			of.setExecutable(true);
		}
		catch (SecurityException se)
		{
			se.printStackTrace(); //TODO: log via the Logger
		}

		return Convert.BytesToBase64( inDigest );
	}

	/**
	 * Uploads the given file on the server.
	 */
	protected boolean uploadFile(IUploadedFile upldFile, UploadedFileInfoBean upldFileInfo)
	{
		boolean retVal = false;

		FacesContext facesContext = FacesContext.getCurrentInstance();

		upldFileInfo.setType( upldFile.getContentType() );
		upldFileInfo.setSize( upldFile.getSize() );

		String uploadedFileName = upldFile.getFileName();
		//String uploadedFileName = upldFile.getOriginalName();

		// Some browsers return complete path name, some don't
		// Make sure we only have the file name
		// First, try forward slash
		int index = uploadedFileName.lastIndexOf('/');
		String justFileName = null;
		if ( index >= 0 )
		{
			justFileName = uploadedFileName.substring( index + 1 );
		}
		else
		{
			// Try backslash
			index = uploadedFileName.lastIndexOf('\\');
			if (index >= 0)
			{
				justFileName = uploadedFileName.substring( index + 1 );
			}
			else
			{
				// No forward or back slashes
				justFileName = uploadedFileName;
			}
		}
		upldFileInfo.setName( justFileName );

		//this.fileNameStaticText.setValue(justFileName);
		//Long uploadedFileSize = new Long(uploadedFile.getSize());
		//this.fileSizeStaticText.setValue(uploadedFileSize);
		//String uploadedFileType = uploadedFile.getContentType();
		//this.fileTypeStaticText.setValue(uploadedFileType);

		try
		{
			// get the path to the "upload" directory
			// from the servlet context
			ServletContext svlCtx = (ServletContext) facesContext.getExternalContext().getContext();

			//String realPath = theApplicationsServletContext.getRealPath( UPLOAD_URL );
			String realPath = null;

			//realPath = theApplicationsServletContext.getRealPath( UPLOAD_URL );
			realPath = UserFacade.instance().getUserTmpPath( this.getSessionBean().getUser(), true );
			File userPath = new File( realPath );
			if ( ! userPath.isAbsolute() )
			{
				realPath = svlCtx.getRealPath( Conf.instance().getWebUploadPath() ) + File.separator + realPath;
			}
			userPath = null;

//			File file = new File(realPath + File.separatorChar + justFileName);

			String digest = null;

			realPath = realPath + File.separator + justFileName;

			digest = JobSubmitBean.StoreUploadedFile(
				upldFile,
				realPath
			);

			upldFileInfo.setRealPath( realPath );
			upldFileInfo.setMd5( digest );

//			facesContext.getExternalContext().getApplicationMap().put( "execfile_bytes", this.execFile.getBytes() );
//			facesContext.getExternalContext().getApplicationMap().put( "execfile_type", this.execFile.getContentType() );
//			facesContext.getExternalContext().getApplicationMap().put( "execfile_name", justFileName );

			retVal = true;
		}
		catch (Exception e)
		{
			ViewUtil.AddExceptionMessage( e );
			JobSubmitBean.Log.log( Level.SEVERE, "Error while uploading file: " + upldFile.getFileName(), e );
			retVal = false;
		}

		return retVal;
	}

/*
	public String monitorUpload() throws IOException
	{
		try
		{
			String contentType = (String)application.getAttribute("fileupload_type");
			String fileName = (String)application.getAttribute("fileupload_name");

			String allowCache = request.getParameter("allowCache");
			String openDirectly = request.getParameter("openDirectly");

			if(allowCache == null || allowCache.equalsIgnoreCase("false"))
			{
			response.setHeader("pragma", "no-cache");
			response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
			response.setHeader("Expires", "01 Apr 1995 01:10:10 GMT");
			}

			if(contentType!=null)
			{
			response.setContentType(contentType);
			}

			if(fileName != null)
			{
			fileName = fileName.substring(fileName.lastIndexOf('\\')+1);
			fileName = fileName.substring(fileName.lastIndexOf('/')+1);

			StringBuffer contentDisposition = new StringBuffer();

			if(openDirectly==null || openDirectly.equalsIgnoreCase("false"))
			{
			contentDisposition.append("attachment;");
			}

			contentDisposition.append("filename=\"");
			contentDisposition.append(fileName);
			contentDisposition.append("\"");

			response.setHeader ("Content-Disposition", contentDisposition.toString());
			}

			byte[] bytes = (byte[])application.getAttribute("fileupload_bytes");
			if (bytes != null)
			{
				response.getOutputStream().write(bytes);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
*/
}
