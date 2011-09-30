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
package fr.paris.lutece.plugins.workflow.business;

import fr.paris.lutece.plugins.workflow.business.task.ITask;
import fr.paris.lutece.plugins.workflow.business.task.TaskHome;
import fr.paris.lutece.portal.business.workflow.Action;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Locale;


/**
 * This class provides instances management methods (create, find, ...) for Action objects
 */
public final class ActionHome
{
    // Static variable pointed at the DAO instance
    private static IActionDAO _dao = (IActionDAO) SpringContextService.getPluginBean( "workflow", "workflowActionDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ActionHome(  )
    {
    }

    /**
     * Creation of an instance of action
     *
     * @param action The instance of action which contains the informations to store
     * @param plugin the plugin
     *
     *
     */
    public static void create( Action action, Plugin plugin )
    {
        _dao.insert( action, plugin );
    }

    /**
     * Update of action which is specified in parameter
     *
     * @param action The instance of action which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( Action action, Plugin plugin )
    {
        _dao.store( action, plugin );
    }

    /**
     *  remove action which is specified in parameter
     *
     * @param nIdAction The action id which contains the informations to remove
     * @param plugin the Plugin
     *
     */
    public static void remove( int nIdAction, Plugin plugin )
    {
        Action action = findByPrimaryKey( nIdAction, plugin );

        if ( action != null )
        {
            //remove resource history associated
            List<ResourceHistory> listResourceHistory = ResourceHistoryHome.getAllHistoryByAction( nIdAction, plugin );

            for ( ResourceHistory resourceHistory : listResourceHistory )
            {
                ResourceHistoryHome.remove( resourceHistory.getId(  ), plugin );
            }
        }

        //remove all task associated with the action
        List<ITask> listTask = TaskHome.getListTaskByIdAction( nIdAction, plugin, Locale.FRENCH );

        for ( ITask task : listTask )
        {
            task.doRemoveConfig( plugin );
            TaskHome.remove( task.getId(  ), plugin );
        }

        _dao.delete( nIdAction, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the Action Object
     * @param nIdAction the action id
     * @param plugin the plugin
     * @return the Action Object
     */
    public static Action findByPrimaryKey( int nIdAction, Plugin plugin )
    {
        return _dao.loadWithIcon( nIdAction, plugin );
    }

    /**
     * Select actions by filter
     * @param filter the action filter
     * @param plugin the plugin
     * @return Action List
     */
    public static List<Action> getListActionByFilter( ActionFilter filter, Plugin plugin )
    {
        return _dao.selectActionsByFilter( filter, plugin );
    }
}
