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

import javax.inject.Inject;
import javax.inject.Named;

import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveConfig;
import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveResource;
import fr.paris.lutece.plugins.workflow.modules.archive.business.IArchiveResourceDao;
import fr.paris.lutece.plugins.workflowcore.business.resource.IResourceHistoryDAO;
import fr.paris.lutece.plugins.workflowcore.business.resource.IResourceWorkflowDAO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

/**
 * Implements {@link IArchiveService}
 */
public class ArchiveService implements IArchiveService
{
    public static final String BEAN_SERVICE = "workflow.archiveService";

    @Inject
    @Named( "workflow.taskArchiveConfigService" )
    private ITaskConfigService _taskArchiveConfigService;

    @Inject
    private IResourceHistoryDAO _resourceHistoryDAO;

    @Inject
    private IResourceWorkflowDAO _resourceWorkflowDAO;

    @Inject
    private IArchiveResourceDao _archiveResourceDao;

    @Override
    public ArchiveConfig loadConfig( ITask task )
    {
        ArchiveConfig config = _taskArchiveConfigService.findByPrimaryKey( task.getId( ) );
        if ( config == null )
        {
            config = new ArchiveConfig( );
            config.setIdTask( task.getId( ) );
            _taskArchiveConfigService.create( config );
        }
        return config;
    }

    @Override
    public ArchiveResource getArchiveResource( int nIdHistory, int nIdTask )
    {
        ResourceHistory history = _resourceHistoryDAO.load( nIdHistory );
        if ( history == null )
        {
            return null;
        }

        ResourceWorkflow resource = _resourceWorkflowDAO.load( history.getIdResource( ), history.getResourceType( ), history.getWorkflow( ).getId( ) );
        if ( resource == null )
        {
            return null;
        }
        return _archiveResourceDao.load( resource.getIdResource( ), nIdTask );
    }
}
