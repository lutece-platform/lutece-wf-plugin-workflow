/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.archive.service;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import fr.paris.lutece.plugins.workflow.modules.assignment.service.IAssignmentHistoryService;
import fr.paris.lutece.plugins.workflow.modules.comment.service.ICommentValueService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

/**
 * Service for archival of type delete of plugin-workflow.
 */
public class WorkflowDeleteArchiveProcessingService extends AbstractArchiveProcessingService
{
    public static final String BEAN_NAME = "workflow.workflowDeleteArchiveProcessingService";

    private static final String TASK_TYPE_COMMENT = "taskTypeComment";
    private static final String TASK_TYPE_ASSIGNMENT = "taskTypeAssignment";
    private static final String TASK_TYPE_ARCHIVE = "taskTypeArchive";
    private static final String TASK_TYPE_CHOOSE_TASK = "chooseStateTask";

    @Inject
    private IArchiveService _archiveService;

    @Inject
    private IAssignmentHistoryService _assignmentHistoryService;

    @Inject
    private ICommentValueService _commentValueService;

    @Inject
    private IResourceWorkflowService _resourceWorkflowService;
    
    @Override
    public void archiveResource( ResourceWorkflow resourceWorkflow )
    {
        List<ResourceHistory> historyList = getListHistoryByResource( resourceWorkflow );

        archiveTaskComment( historyList );
        archiveTaskAssignment( historyList );
        archiveTaskArchive( historyList );
        archiveTaskChooseTask( historyList );

        archiveResourceAndHistory( resourceWorkflow );
    }

    private void archiveTaskComment( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_COMMENT );
            for ( ITask task : taskList )
            {
                _commentValueService.removeByHistory( history.getId( ), task.getId( ), WorkflowUtils.getPlugin( ) );
            }
        }
    }

    private void archiveTaskAssignment( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_ASSIGNMENT );
            for ( ITask task : taskList )
            {
                _assignmentHistoryService.removeByHistory( history.getId( ), task.getId( ), WorkflowUtils.getPlugin( ) );
            }
        }
    }

    private void archiveTaskArchive( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_ARCHIVE );
            for ( ITask task : taskList )
            {
                _archiveService.removeArchiveResource( history.getIdResource( ), task.getId( ) );
            }
        }
    }
    
    private void archiveTaskChooseTask( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_CHOOSE_TASK );
            for ( ITask task : taskList )
            {
                task.doRemoveTaskInformation( history.getId( ) );
            }
        }
    }

    private void archiveResourceAndHistory( ResourceWorkflow resourceWorkflow )
    {
        _resourceHistoryService.removeByListIdResource( Collections.singletonList( resourceWorkflow.getIdResource( ) ), resourceWorkflow.getResourceType( ),
                resourceWorkflow.getWorkflow( ).getId( ) );
        _resourceWorkflowService.remove( resourceWorkflow );
    }
}
