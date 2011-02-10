/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.taskassignment.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for AssignmentHistory objects
 */
public final class AssignmentHistoryHome
{
    // Static variable pointed at the DAO instance
    private static IAssignmentHistoryDAO _dao = (IAssignmentHistoryDAO) SpringContextService.getPluginBean( "workflow",
            "workflowAssignmentHistoryDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private AssignmentHistoryHome(  )
    {
    }

    /**
     * Creation of an instance of assignmentHistory
     *
     * @param assignmentHistory the history of the assignmennt
     * @param plugin the plugin
     *
     *
     */
    public static void create( AssignmentHistory assignmentHistory, Plugin plugin )
    {
        _dao.insert( assignmentHistory, plugin );
    }

    /**
     *  remove all assignment associated width the history specified in parameter
     *
     * @param nIdHistory the history key
     * @param nIdTask The task key
     * @param plugin the Plugin
     *
     */
    public static void removeByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        _dao.deleteByHistory( nIdHistory, nIdTask, plugin );
    }

    /**
     *  remove all assignment associated width the task specified in parameter
     *
     *
     * @param nIdTask The task key
     * @param plugin the Plugin
     *
     */
    public static void removeByTask( int nIdTask, Plugin plugin )
    {
        _dao.deleteByTask( nIdTask, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
         * return the list of assignment associated width the history specified in parameter
         * @param nIdHistory the history id
         * @param nIdTask the task id
         * @param plugin the plugin
         * @return the Config Object
         */
    public static List<AssignmentHistory> getListByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        return _dao.selectByHistory( nIdHistory, nIdTask, plugin );
    }
}
