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
package fr.paris.lutece.plugins.workflow.business;

import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for State objects
 */
public final class StateHome
{
    // Static variable pointed at the DAO instance
    private static IStateDAO _dao = (IStateDAO) SpringContextService.getPluginBean( "workflow", "workflowStateDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private StateHome(  )
    {
    }

    /**
     * Creation of an instance of state
     *
     * @param state The instance of state which contains the informations to store
     * @param plugin the plugin
     *
     *
     */
    public static void create( State state, Plugin plugin )
    {
        _dao.insert( state, plugin );
    }

    /**
     * Update of state which is specified in parameter
     *
     * @param state The instance of state which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( State state, Plugin plugin )
    {
        _dao.store( state, plugin );
    }

    /**
     *  remove state which is specified in parameter
     *
     * @param nIdState The state id to remove
     * @param plugin the Plugin
     *
     */
    public static void remove( int nIdState, Plugin plugin )
    {
        State state = findByPrimaryKey( nIdState, plugin );

        if ( state != null )
        {
            //remove workflow resources associated
            List<ResourceWorkflow> listResourceWorkflow = ResourceWorkflowHome.getAllResourceWorkflowByState( nIdState,
                    plugin );

            for ( ResourceWorkflow resourceWorkflow : listResourceWorkflow )
            {
                ResourceWorkflowHome.remove( resourceWorkflow, plugin );
            }
        }

        _dao.delete( nIdState, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
         * Load the state Object
         * @param nIdState the state id
         * @param plugin the plugin
         * @return the State Object
         */
    public static State findByPrimaryKey( int nIdState, Plugin plugin )
    {
        return _dao.load( nIdState, plugin );
    }

    /**
     * Load the state Object from the given resource
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param nIdWorkflow the workflow id
     * @param plugin the plugin
     * @return the state Object
     */
    public static State findByResource( int nIdResource, String strResourceType, int nIdWorkflow, Plugin plugin )
    {
        return _dao.findByResource( nIdResource, strResourceType, nIdWorkflow, plugin );
    }

    /**
     * Select states by filter
     * @param filter the state filter
     * @param plugin the plugin
     * @return State List
     */
    public static List<State> getListStateByFilter( StateFilter filter, Plugin plugin )
    {
        return _dao.selectStatesByFilter( filter, plugin );
    }

    /**
     * return the initial state of the workflow
     * @param nIdWorkflow yhe workflow id
     * @param plugin the plugin
     * @return  return the initial state of the workflow
     */
    public static State getInitialState( int nIdWorkflow, Plugin plugin )
    {
        // test if initial test already exist
        StateFilter filter = new StateFilter(  );
        filter.setIdWorkflow( nIdWorkflow );
        filter.setIsInitialState( StateFilter.FILTER_TRUE );

        List<State> listState = getListStateByFilter( filter, plugin );

        if ( listState.size(  ) != 0 )
        {
            return listState.get( 0 );
        }

        return null;
    }
}
