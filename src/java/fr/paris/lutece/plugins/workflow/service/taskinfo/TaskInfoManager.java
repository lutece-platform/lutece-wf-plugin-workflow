/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.service.taskinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.business.task.ITask;
import fr.paris.lutece.plugins.workflow.business.task.TaskHome;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * 
 * TaskInfoManager
 *
 */
public final class TaskInfoManager
{
	private static final String BEAN_WORKFLOW_TASK_INFO_MANAGER = "workflow.taskInfoManager";

	// MESSAGE
	private static final String MESSAGE_PLUGIN_NOT_FOUND = "Plugin not found or not installed for plugin name ";
	private Map<String, ITaskInfoProvider> _listProviders;

	/**
	 * Private constructor
	 */
	private TaskInfoManager(  )
	{
		_listProviders = new HashMap<String, ITaskInfoProvider>(  );
	}

	/**
	 * Get the instance of the manager
	 * @return the manager
	 */
	public static TaskInfoManager getManager(  )
	{
		return (TaskInfoManager) SpringContextService.getPluginBean( WorkflowPlugin.PLUGIN_NAME, 
				BEAN_WORKFLOW_TASK_INFO_MANAGER );
	}

	/**
	 * Register a provider
	 * @param provider the provider
	 */
	public void registerProvider( ITaskInfoProvider provider )
	{
		_listProviders.put( provider.getTaskType(  ).getKey(  ), provider );
	}

	/**
	 * Get the list of provider
	 * @return the list of providers
	 */
	public List<ITaskInfoProvider> getProvidersList(  )
	{
		List<ITaskInfoProvider> listProviders = new ArrayList<ITaskInfoProvider>(  );

        for ( ITaskInfoProvider provider : _listProviders.values(  ) )
        {
            Plugin plugin = PluginService.getPlugin( provider.getPluginName(  ) );

            if ( ( plugin != null ) && plugin.isInstalled(  ) )
            {
            	listProviders.add( provider );
            }
            else if ( AppLogService.isDebugEnabled(  ) )
            {
                AppLogService.debug( MESSAGE_PLUGIN_NOT_FOUND + provider.getPluginName(  ) );
            }
        }

        return listProviders;
	}

	/**
	 * Get the provider from a given provider key
	 * @param strProviderKey the provider key
	 * @return the provider
	 */
	public ITaskInfoProvider getProvider( String strProviderKey )
	{
		return _listProviders.get( strProviderKey );
	}

	/**
	 * Get the task resource info. This method will first get
	 * the appropriate provider from the given id task.
	 * @param nIdHistory the id history
	 * @param nIdTask the id task
	 * @param request the HTTP request
	 * @return the task resource info
	 */
	public String getTaskResourceInfo( int nIdHistory, int nIdTask, HttpServletRequest request )
	{
		String strInfo = StringUtils.EMPTY;
		Plugin pluginWorkflow = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
		ITask task = TaskHome.findByPrimaryKey( nIdTask, pluginWorkflow, request.getLocale(  ) );
		
		if ( task != null )
		{
			ITaskInfoProvider provider = getProvider( task.getTaskType(  ).getKey(  ) );
			if ( provider != null )
			{
				strInfo = provider.getTaskResourceInfo( nIdHistory, nIdTask, request );
			}
		}
		return strInfo;
	}
}
