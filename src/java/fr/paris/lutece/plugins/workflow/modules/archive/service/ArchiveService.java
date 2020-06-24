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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import fr.paris.lutece.plugins.workflow.modules.archive.IResourceArchiver;
import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveConfig;
import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveResource;
import fr.paris.lutece.plugins.workflow.modules.archive.business.IArchiveResourceDao;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.IResourceHistoryDAO;
import fr.paris.lutece.plugins.workflowcore.business.resource.IResourceWorkflowDAO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

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
    
    @Inject
    private IActionService _actionService;

    @Inject
    private IStateService _stateService;
    
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    
    @Inject
    private IResourceWorkflowService _resourceWorkflowService;
    
    private static final String USER_AUTO = "auto";

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

    @Override
    public void createArchiveResource( ResourceWorkflow resourceWorkflow, ArchiveConfig config )
    {
        ArchiveResource archiveResource = _archiveResourceDao.load( resourceWorkflow.getIdResource( ), config.getIdTask( ) );
        if ( archiveResource == null )
        {
            archiveResource = new ArchiveResource( );
            archiveResource.setIdResource( resourceWorkflow.getIdResource( ) );
            archiveResource.setIdTask( config.getIdTask( ) );
            archiveResource.setIsArchived( false );
            archiveResource.setInitialDate( Timestamp.valueOf( LocalDateTime.now( ) ) );
            _archiveResourceDao.insert( archiveResource );
        }
    }

    @Override
    public boolean isResourceUpForArchival( ResourceWorkflow resourceWorkflow, ArchiveConfig config )
    {
        if ( config.getDelayArchival( ) <= 0 )
        {
            return true;
        }
        ArchiveResource archiveResource = _archiveResourceDao.load( resourceWorkflow.getIdResource( ), config.getIdTask( ) );

        if ( archiveResource == null || archiveResource.isArchived( ) )
        {
            return false;
        }
        return LocalDateTime.now( ).isAfter( calculateArchivalDate( archiveResource, config.getDelayArchival( ) ) );
    }

    @Override
    public ResourceWorkflow getResourceWorkflowByHistory( int nIdHistory )
    {
        ResourceHistory history = _resourceHistoryDAO.load( nIdHistory );
        if ( history == null )
        {
            return null;
        }

        return _resourceWorkflowDAO.load( history.getIdResource( ), history.getResourceType( ), history.getWorkflow( ).getId( ) );
    }

    private LocalDateTime calculateArchivalDate(  ArchiveResource archiveResource, int daysBeforeArchival )
    {
        if ( daysBeforeArchival > 0 )
        {
            return archiveResource.getInitialDate( ).toLocalDateTime( ).plusDays( daysBeforeArchival );
        }
        return LocalDate.now( ).atStartOfDay( );
    }

    @Override
    public void archiveResource( ResourceWorkflow resourceWorkflow, ITask task, ArchiveConfig config )
    {
        if ( config.getNextState( ) != resourceWorkflow.getState( ).getId( ) )
        {
            doChangeState( task, resourceWorkflow.getIdResource( ), resourceWorkflow.getResourceType( ),  resourceWorkflow.getWorkflow( ).getId( ), config.getNextState( ) );
        }
        List<IResourceArchiver> archiverList = SpringContextService.getBeansOfType( IResourceArchiver.class ).stream( )
                .filter( ra -> ra.getType( ).equals( config.getTypeArchival( ) ) ).collect( Collectors.toList( ) );

        for ( IResourceArchiver archiver : archiverList )
        {
            archiver.archiveResource( resourceWorkflow );
        }

        ArchiveResource archiveResource = _archiveResourceDao.load( resourceWorkflow.getIdResource( ), config.getIdTask( ) );
        // If the archival process is not a full deletion and keeps the archiveResource,
        // it must be updated
        if ( archiveResource != null )
        {
            archiveResource.setArchivalDate( Timestamp.valueOf( LocalDateTime.now( ) ) );
            archiveResource.setIsArchived( true );
            _archiveResourceDao.store( archiveResource );
        }
    }
    
    private void doChangeState( ITask task, int nIdResource, String strResourceType, int nIdWorkflow, int newState )
    {
        Locale locale = I18nService.getDefaultLocale( );
        State state = _stateService.findByPrimaryKey( newState );
        Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

        if ( state != null && action != null )
        {

            // Create Resource History
            ResourceHistory resourceHistory = new ResourceHistory( );
            resourceHistory.setIdResource( nIdResource );
            resourceHistory.setResourceType( strResourceType );
            resourceHistory.setAction( action );
            resourceHistory.setWorkFlow( action.getWorkflow( ) );
            resourceHistory.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
            resourceHistory.setUserAccessCode( USER_AUTO );
            _resourceHistoryService.create( resourceHistory );

            // Update Resource
            ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( nIdResource, strResourceType, nIdWorkflow );
            resourceWorkflow.setState( state );
            _resourceWorkflowService.update( resourceWorkflow );

            // Execute the relative tasks of the state in the workflow
            // We use AutomaticReflexiveActions because we don't want to change the state of the resource by executing actions.
            WorkflowService.getInstance( ).doProcessAutomaticReflexiveActions( nIdResource, strResourceType, state.getId( ), null, locale, null );
        }
    }
}
