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

import fr.paris.lutece.plugins.workflow.service.WorkflowService;
import fr.paris.lutece.portal.business.workflow.Action;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.business.workflow.Workflow;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for workflow objects
 */
public final class WorkflowHome
{
    // Static variable pointed at the DAO instance
    private static IWorkflowDAO _dao = (IWorkflowDAO) SpringContextService.getPluginBean( "workflow", "workflowDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private WorkflowHome(  )
    {
    }

    /**
     * Creation of an instance of workflow
     *
     * @param workflow The instance of workflow which contains the informations to store
     * @param plugin the plugin
     *
     *
     *
     */
    public static void create( Workflow workflow, Plugin plugin )
    {
        _dao.insert( workflow, plugin );
    }

    /**
     * Update of workflow which is specified in parameter
     *
     * @param workflow The instance of workflow which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( Workflow workflow, Plugin plugin )
    {
        _dao.store( workflow, plugin );
    }

    /**
     *  remove workflow  which is specified in parameter
     *
     * @param  nIdWorkflow the workflow to remove
     * @param plugin the Plugin
     *
     */
    public static void remove( int nIdWorkflow, Plugin plugin )
    {
        //remove workflow resources associated
        List<ResourceWorkflow> listResourceWorkflow = ResourceWorkflowHome.getAllResourceWorkflowByWorkflow( nIdWorkflow,
                plugin );

        for ( ResourceWorkflow resourceWorkflow : listResourceWorkflow )
        {
            WorkflowService.getInstance(  )
                           .doRemoveWorkFlowResource( resourceWorkflow.getIdResource(  ),
                resourceWorkflow.getResourceType(  ), nIdWorkflow );
        }

        // remove actions associated
        ActionFilter actionFilter = new ActionFilter(  );
        actionFilter.setIdWorkflow( nIdWorkflow );

        List<Action> listActionToRemove = ActionHome.getListActionByFilter( actionFilter, plugin );

        for ( Action actionToRemove : listActionToRemove )
        {
            ActionHome.remove( actionToRemove.getId(  ), plugin );
        }

        //remove states associated
        StateFilter stateFilter = new StateFilter(  );
        stateFilter.setIdWorkflow( nIdWorkflow );

        List<State> listState = StateHome.getListStateByFilter( stateFilter, plugin );

        for ( State state : listState )
        {
            StateHome.remove( state.getId(  ), plugin );
        }

        _dao.delete( nIdWorkflow, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
         * Load the workflow Object
         * @param nIdWorkflow the workflow id
         * @param plugin the plugin
         * @return the State Object
         */
    public static Workflow findByPrimaryKey( int nIdWorkflow, Plugin plugin )
    {
        return _dao.load( nIdWorkflow, plugin );
    }

    /**
     * return the workflow list by filter
     * @param filter the filter
     * @param plugin the plugin
     * @return the workflow list
     */
    public static List<Workflow> getListWorkflowsByFilter( WorkflowFilter filter, Plugin plugin )
    {
        return _dao.selectWorkflowByFilter( filter, plugin );
    }
}
