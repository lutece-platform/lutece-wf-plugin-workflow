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
package fr.paris.lutece.plugins.workflow.modules.archive.service;

import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveConfig;
import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveResource;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

/**
 * Service for Archive Task
 */
public interface IArchiveService
{

    /**
     * Load config of task.
     * 
     * @param task
     * @return
     */
    ArchiveConfig loadConfig( ITask task );

    /**
     * Load the {@link ArchiveResource}
     * 
     * @param nIdHistory
     * @param nIdTask
     * @return ArchiveResource
     */
    ArchiveResource getArchiveResource( int nIdHistory, int nIdTask );

    /**
     * Creates the Archive Resource.
     * 
     * @param resourceWorkflow
     * @param config
     */
    void createArchiveResource( ResourceWorkflow resourceWorkflow, ArchiveConfig config );

    /**
     * Determines if a resource is up for archival. <br>
     * 
     * @param resourceWorkflow
     * @param config
     * @return true if the resource should be archived
     */
    boolean isResourceUpForArchival( ResourceWorkflow resourceWorkflow, ArchiveConfig config );

    /**
     * Get resource owning the history
     * 
     * @param nIdHistory
     * @return
     */
    ResourceWorkflow getResourceWorkflowByHistory( int nIdHistory );

    /**
     * Archives the resource according to the specified config
     * 
     * @param resourceWorkflow
     * @param task
     * @param config
     */
    void archiveResource( ResourceWorkflow resourceWorkflow, ITask task, ArchiveConfig config );

    /**
     * Delete the {@link ArchiveResource} by idResource
     * 
     * @param idResource
     * @param idTask
     */
    void removeArchiveResource( int idResource, int idTask );

    /**
     * Deletes the config for the task.
     * 
     * @param task
     */
    void removeConfig( ITask task );
}
