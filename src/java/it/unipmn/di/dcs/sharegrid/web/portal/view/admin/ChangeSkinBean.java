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

package it.unipmn.di.dcs.sharegrid.web.portal.view.admin;

import it.unipmn.di.dcs.common.annotation.Experimental;
import it.unipmn.di.dcs.common.util.Pair;

import it.unipmn.di.dcs.sharegrid.web.portal.view.AbstractPageBean;
import it.unipmn.di.dcs.sharegrid.web.portal.view.util.ViewUtil;;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;

/**
 * Page bean for changing the site skin.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
@Experimental("Maybe we need to change to way of changing the skin since it seems that at deploying time the symlink is replaced with a real directory. This seems to be a 'problem' related to JAR which converts links into real files. So, instead of removing and creating the symlink 'current' we would delete the directory 'current' and create a new one copying inside of it all files contained in the directory of the chosen skin")
public class ChangeSkinBean extends AbstractPageBean
{
	private static final transient Logger Log = Logger.getLogger( ChangeSkinBean.class.getName() );

	//@{ Properties ////////////////////////////////////////////////////////

	/** PROPERTY: oldSkin. */
	private String oldSkin;
	public String getOldSkin() { return this.oldSkin; }
	public void setOldSkin(String value) { this.oldSkin = value; }

	/** PROPERTY: skin. */
	private String skin;
	public String getSkin() { return this.skin; }
	public void setSkin(String value) { this.setOldSkin(this.skin); this.skin = value; }

	/** PROPERTY: availableSkins. */
	private List<Pair<String,String>> availableSkins;
	public List<Pair<String,String>> getAvailableSkins()
	{
		if (this.availableSkins != null)
		{
			return this.availableSkins;
		}

		ExternalContext extCtx = this.getFacesContext().getExternalContext();

		ServletContext srvCtx = (ServletContext) extCtx.getContext();

		Set<String> skinPaths = null;
		skinPaths = extCtx.getResourcePaths("/resources/theme/");

		this.availableSkins = new ArrayList<Pair<String,String>>();
		for (String skinPath : skinPaths)
		{
			String skinRealPath = null;
			skinRealPath = srvCtx.getRealPath(skinPath);

			File skinFile = null;
			skinFile = new File(skinRealPath);

			String skinName = null;
			skinName = skinFile.getName();

			if ("current".equals(skinName))
			{
				String trueSkinName = null;
				try
				{
					trueSkinName = skinFile.getCanonicalFile().getName();
					this.setSkin(trueSkinName);
				}
				catch (Exception e)
				{
					Log.log( Level.SEVERE, "Current skin cannot be opened", e );
					ViewUtil.AddExceptionMessage(e);
				}
			}
			else
			{
				this.addAvailableSkin(skinName, skinName.toUpperCase());
			}
		}

		return this.availableSkins;
	}

	protected void setAvailableSkins(List<Pair<String,String>> value) { this.availableSkins = value; }
	protected void addAvailableSkin(String id, String name) { this.availableSkins.add(new Pair<String,String>(id, name)); }

	//@} Properties ////////////////////////////////////////////////////////

	//@{ Actions ///////////////////////////////////////////////////////////

	/** Action for changing the site skin. */
	public String doChangeSkin()
	{
		try
		{
			if (this.getOldSkin() == null || !this.getOldSkin().equals(this.getSkin()))
			{
				ExternalContext extCtx = this.getFacesContext().getExternalContext();

				ServletContext srvCtx = (ServletContext) extCtx.getContext();

				Set<String> skinPaths = null;
				skinPaths = extCtx.getResourcePaths("/resources/theme/" + this.getSkin());
				if (skinPaths.size() == 1)
				{
					String skinPath = null;
					skinPath = "/resources/theme/" + this.getSkin();

					String skinRealPath = null;
					skinRealPath = srvCtx.getRealPath(skinPath);

					File skinFile = null;
					skinFile = new File(skinRealPath);

					// Remove the 'current' symlink
					File curSkinFile = null;
					curSkinFile = new File(skinFile.getPath() + File.separator + "current");
					curSkinFile.delete();

					// Create a new 'current' symlink
					Runtime.getRuntime().exec("ln -s " + skinFile.getAbsolutePath() + " " + curSkinFile.getAbsolutePath());
				}
				else
				{
					//TODO: error
				}

			}

			Log.info("Yuo choose skin: " + this.getSkin());
		}
		catch (Exception e)
		{
			Log.log( Level.SEVERE, "Error while changing the skin '" + this.getSkin() + "'.", e );
			ViewUtil.AddExceptionMessage( e );

			return "changeskin-ko";
		}

		return "changeskin-ok";
	}

	//@} Actions ///////////////////////////////////////////////////////////
}
