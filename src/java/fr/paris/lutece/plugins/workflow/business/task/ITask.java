/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.business.task;

import fr.paris.lutece.portal.business.workflow.Action;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *  ITask
 */
public interface ITask
{
    /**
     *
     * @return the task Id
     */
    int getId(  );

    /**
     * set the task id
     * @param nId the task id
     */
    void setId( int nId );

    /**
     *
     * @return the TaskType Object
     */
    ITaskType getTaskType(  );

    /**
     * set the TaskType object
     * @param taskType the task type object
     */
    void setTaskType( ITaskType taskType );

    /**
     *
     * @return the action associate to the task
     */
    Action getAction(  );

    /**
     * set the action associate to the task
     * @param action the action associate to the task
     */
    void setAction( Action action );

    /**
    * Process the task
    * @param nIdResourceHistory the resource history id
     * @param request the request
     * @param plugin plugin
     * @param locale locale
    */
    void processTask( int nIdResourceHistory, HttpServletRequest request, Plugin plugin, Locale locale );

    /**
     * validates the user input associated to the task
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param request request
     * @param locale locale
     * @param plugin the plugin
     * @return null if there is no error in the task form
     *                    else return the error message url
     */
    String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        Plugin plugin );

    /**
     * returns the informations which must  be displayed in the tasks form
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param request request
     * @param plugin plugin
     * @param locale locale
     * @return the information which must  be displayed in the tasks form
     */
    String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Plugin plugin,
        Locale locale );

    /**
     * returns the informations which must  be displayed in the task configuration
     * @param request request
     * @param plugin plugin
     * @param locale locale
     * @return the information which must  be displayed in the task configuration
     */
    String getDisplayConfigForm( HttpServletRequest request, Plugin plugin, Locale locale );

    /**
     * Perform the task configuration
     * @param request request
     * @param locale locale
     * @param plugin plugin
     * @return the url to go after perform task configuration
     */
    String doSaveConfig( HttpServletRequest request, Locale locale, Plugin plugin );

    /**
     * return for a document the informations store during processing task
     * @param nIdHistory the document id
     * @param request the request
     * @param plugin the plugin
     * @param locale locale
     * @return the informations store during processing task
     */
    String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Plugin plugin, Locale locale );

    /**
     * return a xml wich contains  for a document the informations store during processing task
     * @param nIdHistory the document id
     * @param request the request
     * @param plugin the plugin
     * @param locale locale
     * @return the informations store during processing task
     */
    String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Plugin plugin, Locale locale );

    /**
     * remove taskInformation associate to the history
     * @param nIdHistory the document id
     * @param plugin plugin
      */
    void doRemoveTaskInformation( int nIdHistory, Plugin plugin );

    /**
     * Remove the task configuration
     * @param plugin plugin
     */
    void doRemoveConfig( Plugin plugin );

    /**
     *
     * @return true if the task use a configuration
     */
    boolean isConfigRequire(  );

    /**
     *
     * @return true if the task use a form
     */
    boolean isFormTaskRequire(  );

    /**
     * returns the task title
     * @param plugin plugin
     * @param locale locale
     * @return the task title
     */
    String getTitle( Plugin plugin, Locale locale );

    /**
     * returns the  entries of the task form
     * @param plugin plugin
     * @param locale locale
     * @return the  entries of the task form
     */
    ReferenceList getTaskFormEntries( Plugin plugin, Locale locale );

    /**
        *
        * @return true if the task may be use by automatic action
        */
    boolean isTaskForActionAutomatic(  );

    /**
     * Initialize the task
     */
    void init(  );
}
