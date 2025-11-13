/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.state.daemon;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import fr.paris.lutece.plugins.workflow.modules.state.business.ChooseStateTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.state.service.IChooseStateTaskService;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

public class ChooseStateDaemon extends Daemon
{

    private IChooseStateTaskService _chooseStateTaskService = SpringContextService.getBean( "workflow.chooseStateTaskService" );
    private IWorkflowService _workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
    private IResourceWorkflowService _resourceWorkflowService = SpringContextService.getBean( ResourceWorkflowService.BEAN_SERVICE );
    private IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
    private ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );

    @Override
    public void run( )
    {
        WorkflowFilter workflowFilter = new WorkflowFilter( );
        workflowFilter.setIsEnabled( WorkflowFilter.FILTER_TRUE );

        List<Workflow> listWorkflows = _workflowService.getListWorkflowsByFilter( workflowFilter );

        for ( Workflow workflow : listWorkflows )
        {
            ActionFilter filter = new ActionFilter( );
            filter.setAutomaticReflexiveAction( true );
            filter.setIdWorkflow( workflow.getId( ) );

            List<Action> listAutomaticActions = _actionService.getListActionByFilter( filter );

            for ( Action action : listAutomaticActions )
            {
                ResourceWorkflowFilter filt = new ResourceWorkflowFilter( );
                filt.setListIdStateBefore( action.getListIdStateBefore( ) );
                filt.setIdWorkflow( workflow.getId( ) );

                List<ITask> listActionTasks = _taskService.getListTaskByIdAction( action.getId( ), Locale.getDefault( ) );
                ITask task = findTaskToExecute( listActionTasks );

                if ( task == null )
                {
                    continue;
                }

                ChooseStateTaskConfig config = _chooseStateTaskService.loadConfig( task );

                List<ResourceWorkflow> listResource = _resourceWorkflowService.getListResourceWorkflowByFilter( filt );

                for ( ResourceWorkflow resource : listResource )
                {
                    try
                    {
                        _chooseStateTaskService.chooseNewState( resource.getIdResource( ), resource.getResourceType( ), task, config, workflow.getId( ),
                                resource.getState( ).getId( ) );
                    }
                    catch( Exception e )
                    {
                        AppLogService.error( "Unexpected Error", e );
                    }
                }
            }
        }
    }

    private ITask findTaskToExecute( List<ITask> listActionTasks )
    {
        for ( ITask tsk : listActionTasks )
        {
            String beanName = Optional.ofNullable( tsk ).map( ITask::getTaskType ).map( ITaskType::getBeanName ).orElse( null );
            if ( "workflow.chooseStateTask".equals( beanName ) )
            {
                return tsk;
            }
        }
        return null;
    }
}
