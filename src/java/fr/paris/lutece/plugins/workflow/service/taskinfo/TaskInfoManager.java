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

import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * TaskInfoManager
 *
 */
public final class TaskInfoManager
{
    /**
     * Private constructor
     */
    private TaskInfoManager(  )
    {
    }

    /**
     * Get the list of provider
     * @return the list of providers
     */
    public static List<ITaskInfoProvider> getProvidersList(  )
    {
        return SpringContextService.getBeansOfType( ITaskInfoProvider.class );
    }

    /**
     * Get the provider from a given provider key
     * @param strProviderKey the provider key
     * @return the provider
     */
    public static ITaskInfoProvider getProvider( String strProviderKey )
    {
        if ( StringUtils.isNotBlank( strProviderKey ) )
        {
            for ( ITaskInfoProvider provider : getProvidersList(  ) )
            {
                if ( ( provider != null ) && ( provider.getTaskType(  ) != null ) &&
                        strProviderKey.equals( provider.getTaskType(  ).getKey(  ) ) )
                {
                    return provider;
                }
            }
        }

        return null;
    }

    /**
     * Get the task resource info. This method will first get
     * the appropriate provider from the given id task.
     * @param nIdHistory the id history
     * @param nIdTask the id task
     * @param request the HTTP request
     * @return the task resource info
     */
    public static String getTaskResourceInfo( int nIdHistory, int nIdTask, HttpServletRequest request )
    {
        String strInfo = StringUtils.EMPTY;
        Locale locale = getLocale( request );
        ITaskService taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );
        ITask task = taskService.findByPrimaryKey( nIdTask, locale );

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

    /**
     * Get the locale
     * @param request the HTTP request
     * @return the locale
     */
    private static Locale getLocale( HttpServletRequest request )
    {
        Locale locale = null;

        if ( request != null )
        {
            locale = request.getLocale(  );
        }
        else
        {
            locale = I18nService.getDefaultLocale(  );
        }

        return locale;
    }
}
