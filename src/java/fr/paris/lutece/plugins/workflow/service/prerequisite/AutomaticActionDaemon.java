/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.workflow.service.prerequisite;

import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import jakarta.enterprise.inject.spi.CDI;

import java.util.List;
import java.util.Locale;

/**
 * Daemon to execute automatic actions on resources
 */
public class AutomaticActionDaemon extends Daemon
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        IWorkflowService workflowService = CDI.current( ).select( IWorkflowService.class ).get( );
        WorkflowFilter workflowFilter = new WorkflowFilter( );
        List<Workflow> listWorkflows = workflowService.getListWorkflowsByFilter( workflowFilter );
        IResourceWorkflowService resourceWorkflowService = CDI.current( ).select( IResourceWorkflowService.class ).get( );
        int nNbResourcesFound = 0;
        int nNbErrors = 0;

        for ( Workflow workflow : listWorkflows )
        {
            IActionService actionService = CDI.current( ).select( IActionService.class ).get( );
            ActionFilter filter = new ActionFilter( );
            filter.setAutomaticReflexiveAction( false );
            filter.setIsAutomaticState( 1 );
            filter.setIdWorkflow( workflow.getId( ) );

            List<Action> listAutomaticActions = actionService.getListActionByFilter( filter );

            for ( Action action : listAutomaticActions )
            {
                List<ResourceWorkflow> listResource = resourceWorkflowService.getAllResourceWorkflowByListState( action.getListIdStateBefore( ) );

                for ( ResourceWorkflow resource : listResource )
                {
                    try
                    {
                        WorkflowService.getInstance( ).doProcessAction( resource.getIdResource( ), resource.getResourceType( ), action.getId( ),
                                resource.getExternalParentId( ), null, Locale.getDefault( ), true, null );
                    }
                    catch ( AppException e )
                    {
                        AppLogService.error( "An error occurred processing Action {} for resource Id {} of type {} ",
                      		action.getId( ), resource.getIdResource( ), resource.getResourceType( ) ,  e);
                        nNbErrors++;
                    }

                    nNbResourcesFound++;
                }
            }
        }

        StringBuilder sbResult = new StringBuilder( );
        sbResult.append( "Automatic actions performed on ").append( nNbResourcesFound ).append(" resources ");
        sbResult.append( " with ").append( nNbErrors ).append( "errors.");
        if ( nNbErrors > 0 ) sbResult.append( " See logs for details.");

        setLastRunLogs( sbResult.toString( ) );
    }
}
