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
package fr.paris.lutece.plugins.workflow.modules.archive.daemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveConfig;
import fr.paris.lutece.plugins.workflow.modules.archive.service.ArchiveService;
import fr.paris.lutece.plugins.workflow.modules.archive.service.IArchiveService;
import fr.paris.lutece.plugins.workflow.modules.archive.service.TaskArchive;
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

public class ArchiveDaemon extends Daemon
{
    private IWorkflowService _workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
    private IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
    private ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );
    private IResourceWorkflowService _resourceWorkflowService = SpringContextService.getBean( ResourceWorkflowService.BEAN_SERVICE );
    private IArchiveService _archiveService = SpringContextService.getBean( ArchiveService.BEAN_SERVICE );

    @Override
    public void run( )
    {
        WorkflowFilter workflowFilter = new WorkflowFilter( );
        workflowFilter.setIsEnabled( 1 );

        List<Workflow> listWorkflows = _workflowService.getListWorkflowsByFilter( workflowFilter );

        for ( Workflow wf : listWorkflows )
        {
            ActionFilter filter = new ActionFilter( );
            filter.setAutomaticReflexiveAction( true );
            filter.setIdWorkflow( wf.getId( ) );

            List<Action> listAutomaticActions = _actionService.getListActionByFilter( filter );

            for ( Action action : listAutomaticActions )
            {
                processAction( action, wf );
            }
        }
    }

    private void processAction( Action action, Workflow wf )
    {
        List<ITask> listTasks = getListTaskByIdActionAndTaskType( action.getId( ), "taskTypeArchive", Locale.getDefault( ) );

        if ( CollectionUtils.isNotEmpty( listTasks ) )
        {
            ResourceWorkflowFilter filt = new ResourceWorkflowFilter( );
            filt.setListIdStateBefore( action.getListIdStateBefore( ) );
            filt.setIdWorkflow( wf.getId( ) );

            List<ResourceWorkflow> listResource = _resourceWorkflowService.getListResourceWorkflowByFilter( filt );
            if ( CollectionUtils.isNotEmpty( listResource ) )
            {
                for ( ITask task : listTasks )
                {
                    ArchiveConfig config = _archiveService.loadConfig( task );
                    if ( config == null )
                    {
                        AppLogService.error( "No config for archive task: " + task.getId( ) );
                        continue;
                    }
                    for ( ResourceWorkflow resourceWorkflow : listResource )
                    {
                        ( (TaskArchive) task ).doArchiveResource( resourceWorkflow, config );
                    }
                }
            }
        }
    }

    private List<ITask> getListTaskByIdActionAndTaskType( int nIdAction, String taskType, Locale locale )
    {
        List<ITask> result = new ArrayList<>( );
        List<ITask> allTasks = _taskService.getListTaskByIdAction( nIdAction, locale );
        for ( ITask task : allTasks )
        {
            String currentType = Optional.ofNullable( task ).map( ITask::getTaskType ).map( ITaskType::getKey ).orElse( null );
            if ( taskType.equals( currentType ) )
            {
                result.add( task );
            }
        }
        return result;
    }
}
