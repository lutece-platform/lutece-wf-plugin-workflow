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
package fr.paris.lutece.plugins.workflow.modules.state.service;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.business.resource.ResourceWorkflowDAO;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskInformationHome;
import fr.paris.lutece.plugins.workflow.util.MockTask;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.icon.IIconDAO;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.IStateDAO;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.IWorkflowDAO;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Change of state by the daemons: a resource is changed once, even when the daemon runs on several instances
 */
public class ChangeStateClaimTest extends LuteceTestCase
{
    private static final String RESOURCE_TYPE = "CHANGE_STATE_CLAIM_TEST";
    private static final int ID_RESOURCE = 1;
    private static final int TIMEOUT = 300;

    @Inject
    private IChangeStateTaskService _changeStateTaskService;
    @Inject
    private IActionService _actionService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private ResourceWorkflowDAO _resourceWorkflowDAO;
    @Inject
    private IStateDAO _stateDAO;
    @Inject
    private IWorkflowDAO _workflowDAO;
    @Inject
    private IIconDAO _iconDAO;
    private Icon _icon;
    private Workflow _workflow;
    private State _state1;
    private State _state2;
    private Action _action;
    private MockTask _task;
    private ResourceWorkflow _resourceWorkflow;

    @BeforeEach
    protected void createWorkflow( )
    {
        _icon = new Icon( );
        _icon.setName( "icon" );
        _iconDAO.insert( _icon );

        _workflow = new Workflow( );
        _workflow.setName( "wf change state claim" );
        _workflow.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        _workflow.setDescription( "strDescription" );
        _workflow.setEnabled( true );
        _workflow.setWorkgroup( "group" );
        _workflowDAO.insert( _workflow );

        _state1 = newState( "state 1" );
        _state2 = newState( "state 2" );

        _action = new Action( );
        _action.setName( "change state" );
        _action.setDescription( "strDescription" );
        _action.setIcon( _icon );
        _action.setListIdStateBefore( List.of( _state1.getId( ) ) );
        _action.setStateAfter( _state1 );
        _action.setWorkflow( _workflow );
        _action.setAutomaticReflexiveAction( true );
        _action.setOrder( 0 );
        _actionService.create( _action );

        _task = new MockTask( );
        _task.setAction( _action );

        _resourceWorkflow = new ResourceWorkflow( );
        _resourceWorkflow.setIdResource( ID_RESOURCE );
        _resourceWorkflow.setResourceType( RESOURCE_TYPE );
        _resourceWorkflow.setState( _state1 );
        _resourceWorkflow.setWorkFlow( _workflow );
        _resourceWorkflowDAO.insert( _resourceWorkflow );
    }

    @AfterEach
    protected void removeWorkflow( )
    {
        for ( ResourceHistory history : histories( ) )
        {
            ChangeStateTaskInformationHome.remove( history.getId( ), _task.getId( ) );
            _resourceHistoryService.remove( history.getId( ) );
        }
        _resourceWorkflowDAO.delete( _resourceWorkflow );
        _actionService.remove( _action.getId( ) );
        _stateDAO.delete( _state1.getId( ) );
        _stateDAO.delete( _state2.getId( ) );
        _iconDAO.delete( _icon.getId( ) );
        _workflowDAO.delete( _workflow.getId( ) );
    }

    /**
     * A resource claimed by another instance is not changed
     */
    @Test
    public void testClaimedResourceIsNotChanged( )
    {
        assertTrue( _resourceWorkflowDAO.lock( ID_RESOURCE, RESOURCE_TYPE, _workflow.getId( ), List.of( _state1.getId( ) ), TIMEOUT ) );

        assertFalse( changeState( ) );
        assertEquals( _state1.getId( ), currentState( ) );
        assertEquals( 0, histories( ).size( ) );
    }

    /**
     * The second run (the daemon of another instance working on the same list) does not change the state again
     */
    @Test
    public void testResourceIsChangedOnce( )
    {
        assertTrue( changeState( ) );
        assertFalse( changeState( ) );

        assertEquals( _state2.getId( ), currentState( ) );
        assertEquals( 1, histories( ).size( ) );
        assertTrue( _resourceWorkflowDAO.lock( ID_RESOURCE, RESOURCE_TYPE, _workflow.getId( ), List.of( _state2.getId( ) ), TIMEOUT ),
                "the claim must be released after the change" );
    }

    private boolean changeState( )
    {
        return _changeStateTaskService.doChangeStateIfInState( _task, ID_RESOURCE, RESOURCE_TYPE, _workflow.getId( ), _state1.getId( ), _state2.getId( ) );
    }

    private int currentState( )
    {
        return _resourceWorkflowDAO.load( ID_RESOURCE, RESOURCE_TYPE, _workflow.getId( ) ).getState( ).getId( );
    }

    private List<ResourceHistory> histories( )
    {
        return _resourceHistoryService.getAllHistoryByResource( ID_RESOURCE, RESOURCE_TYPE, _workflow.getId( ) );
    }

    private State newState( String strName )
    {
        State state = new State( );
        state.setName( strName );
        state.setDescription( "strDescription" );
        state.setIcon( _icon );
        state.setInitialState( false );
        state.setRequiredWorkgroupAssigned( false );
        state.setWorkflow( _workflow );
        state.setOrder( 1 );
        _stateDAO.insert( state );

        return state;
    }
}
