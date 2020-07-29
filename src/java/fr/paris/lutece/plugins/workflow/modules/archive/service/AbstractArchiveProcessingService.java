/*
 * Copyright (c) 2002-2020, City of Paris
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;

/**
 * Abstract implementation of {@link IArchiveProcessingService}
 */
public abstract class AbstractArchiveProcessingService implements IArchiveProcessingService
{
    @Inject
    protected IResourceHistoryService _resourceHistoryService;

    @Inject
    protected ITaskService _taskService;

    /**
     * Search for all Tasks of the given history (with the currect task type).
     * 
     * @param history
     * @return
     */
    protected List<ITask> findTasksByHistory( ResourceHistory history, String taskType )
    {
        List<ITask> result = new ArrayList<>( );
        List<ITask> allTasks = _taskService.getListTaskByIdAction( history.getAction( ).getId( ), Locale.getDefault( ) );
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

    protected List<ResourceHistory> getListHistoryByResource( ResourceWorkflow resourceWorkflow )
    {
        return _resourceHistoryService.getAllHistoryByResource( resourceWorkflow.getIdResource( ), resourceWorkflow.getResourceType( ),
                resourceWorkflow.getWorkflow( ).getId( ) );
    }
}
