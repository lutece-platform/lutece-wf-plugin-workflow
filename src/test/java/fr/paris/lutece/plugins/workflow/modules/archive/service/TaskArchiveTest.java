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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveResource;
import fr.paris.lutece.plugins.workflow.modules.archive.business.IArchiveResourceDao;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.icon.IIconDAO;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.state.IStateDAO;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.IWorkflowDAO;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

/**
 * The archive trace of a resource survives the anonymization of its history
 */
public class TaskArchiveTest extends LuteceTestCase
{
    private static final String RESOURCE_TYPE = "TASK_ARCHIVE_TEST";
    private static final int ID_RESOURCE = 1;
    private static final int ID_TASK = 9999;

    @Inject
    private IArchiveResourceDao _archiveResourceDao;
    @Inject
    private IActionService _actionService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private IWorkflowDAO _workflowDAO;
    @Inject
    private IIconDAO _iconDAO;
    @Inject
    private IStateDAO _stateDAO;
    private Icon _icon;
    private Workflow _workflow;
    private State _state;
    private Action _action;
    private ResourceHistory _history;

    @BeforeEach
    protected void createHistory( )
    {
        _icon = new Icon( );
        _icon.setName( "icon" );
        _iconDAO.insert( _icon );

        _workflow = new Workflow( );
        _workflow.setName( "wf task archive" );
        _workflow.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        _workflow.setDescription( "strDescription" );
        _workflow.setEnabled( true );
        _workflow.setWorkgroup( "group" );
        _workflowDAO.insert( _workflow );

        _state = new State( );
        _state.setName( "archived" );
        _state.setDescription( "strDescription" );
        _state.setIcon( _icon );
        _state.setInitialState( false );
        _state.setRequiredWorkgroupAssigned( false );
        _state.setWorkflow( _workflow );
        _state.setOrder( 1 );
        _stateDAO.insert( _state );

        _action = new Action( );
        _action.setName( "archive" );
        _action.setDescription( "strDescription" );
        _action.setIcon( _icon );
        _action.setListIdStateBefore( List.of( _state.getId( ) ) );
        _action.setStateAfter( _state );
        _action.setWorkflow( _workflow );
        _action.setOrder( 0 );
        _actionService.create( _action );

        _history = new ResourceHistory( );
        _history.setIdResource( ID_RESOURCE );
        _history.setResourceType( RESOURCE_TYPE );
        _history.setAction( _action );
        _history.setWorkFlow( _workflow );
        _history.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        _history.setUserAccessCode( "admin" );
        _resourceHistoryService.create( _history );

        ArchiveResource archiveResource = new ArchiveResource( );
        archiveResource.setIdResource( ID_RESOURCE );
        archiveResource.setIdTask( ID_TASK );
        archiveResource.setInitialDate( Timestamp.valueOf( LocalDateTime.now( ).minusDays( 20 ) ) );
        archiveResource.setIsArchived( true );
        _archiveResourceDao.insert( archiveResource );
    }

    @AfterEach
    protected void removeHistory( )
    {
        _archiveResourceDao.delete( ID_RESOURCE, ID_TASK );
        _resourceHistoryService.remove( _history.getId( ) );
        _actionService.remove( _action.getId( ) );
        _stateDAO.delete( _state.getId( ) );
        _iconDAO.delete( _icon.getId( ) );
        _workflowDAO.delete( _workflow.getId( ) );
    }

    /**
     * The anonymization keeps the archive trace: without it the daemon archives the resource again on each run
     */
    @Test
    public void testAnonymizeKeepsArchiveTrace( )
    {
        TaskArchive task = CDI.current( ).select( TaskArchive.class ).get( );
        task.setId( ID_TASK );

        task.doAnonymizeTaskInformation( _history.getId( ) );

        ArchiveResource archiveResource = _archiveResourceDao.load( ID_RESOURCE, ID_TASK );
        assertNotNull( archiveResource, "the archive trace must be kept" );
        assertTrue( archiveResource.isArchived( ) );
    }
}
