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
package fr.paris.lutece.plugins.workflow.web.task;

import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * TaskComponentManager
 *
 */
public final class TaskComponentManager
{
    /**
     * Private constructor
     */
    private TaskComponentManager(  )
    {
    }

    /**
     * Get the list of task components
     * @return a list of {@link ITaskComponent}
     */
    public static List<ITaskComponent> getTaskComponents(  )
    {
        return SpringContextService.getBeansOfType( ITaskComponent.class );
    }

    /**
     * Get the task component from a given key
     * @param strKey the key
     * @return an instance of {@link ITaskComponent}
     */
    public static ITaskComponent getTaskComponent( String strKey )
    {
        for ( ITaskComponent component : getTaskComponents(  ) )
        {
            if ( component.isInvoked( strKey ) )
            {
                return component;
            }
        }

        return null;
    }

    /**
     * Get the task component from a given task
     * @param task the task
     * @return an instance of {@link ITaskComponent}
     */
    public static ITaskComponent getTaskComponent( ITask task )
    {
        if ( ( task != null ) && ( task.getTaskType(  ) != null ) )
        {
            return getTaskComponent( task.getTaskType(  ).getKey(  ) );
        }

        return null;
    }

    /**
    * Returns the informations which must  be displayed in the tasks form
    * @param nIdResource the resource id
    * @param strResourceType the resource type
    * @param request request
    * @param locale locale
    * @param task the task
    * @return the information which must  be displayed in the tasks form
    */
    public static String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        ITaskComponent component = getTaskComponent( task );

        if ( component != null )
        {
            return component.getDisplayTaskForm( nIdResource, strResourceType, request, locale, task );
        }

        return null;
    }

    /**
    * Returns the informations which must  be displayed in the task configuration
    * @param request request
    * @param locale locale
    * @param task the task
    * @return the information which must  be displayed in the task configuration
    */
    public static String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        ITaskComponent component = getTaskComponent( task );

        if ( component != null )
        {
            return component.getDisplayConfigForm( request, locale, task );
        }

        return null;
    }

    /**
     * Return for a document the informations store during processing task
     * @param nIdHistory the document id
     * @param request the request
     * @param locale locale
     * @param task the task
     * @return the informations store during processing task
     */
    public static String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale,
        ITask task )
    {
        ITaskComponent component = getTaskComponent( task );

        if ( component != null )
        {
            return component.getDisplayTaskInformation( nIdHistory, request, locale, task );
        }

        return null;
    }

    /**
     * Return a xml which contains  for a document the informations store during processing task
     * @param nIdHistory the document id
     * @param request the request
     * @param locale locale
     * @param task the task
     * @return the informations store during processing task
     */
    public static String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        ITaskComponent component = getTaskComponent( task );

        if ( component != null )
        {
            return component.getTaskInformationXml( nIdHistory, request, locale, task );
        }

        return null;
    }

    /**
     * validates the user input associated to the task
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param request request
     * @param locale locale
     * @param task the task
     * @return null if there is no error in the task form else return the error message url
     */
    public static String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        ITaskComponent component = getTaskComponent( task );

        if ( component != null )
        {
            return component.doValidateTask( nIdResource, strResourceType, request, locale, task );
        }

        return null;
    }

    /**
     * Perform the task configuration
     * @param request request
     * @param locale locale
     * @param task the task
     * @return the url to go after perform task configuration
     */
    public static String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        ITaskComponent component = getTaskComponent( task );

        if ( component != null )
        {
            return component.doSaveConfig( request, locale, task );
        }

        return null;
    }
}
