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
package fr.paris.lutece.plugins.workflow.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.business.resource.ResourceWorkflowDAO;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.icon.IIconDAO;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.IStateDAO;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.IWorkflowDAO;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.exception.ResourceWorkflowLockedException;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Claim of the resource by the processing of an action
 */
public class WorkflowServiceLockTest extends LuteceTestCase
{
    private static final String RESOURCE_TYPE = "LOCK_SERVICE_TEST";
    private static final int ID_RESOURCE = 1;
    private static final int TIMEOUT = 300;

    @Inject
    private IWorkflowService _workflowService;
    @Inject
    private IActionService _actionService;
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
    private State _state3;
    private Action _manualAction;
    private Action _automaticAction;
    private ResourceWorkflow _resourceWorkflow;

    @BeforeEach
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        _icon = new Icon( );
        _icon.setName( "icon" );
        _iconDAO.insert( _icon );

        _workflow = new Workflow( );
        _workflow.setName( "wf lock service" );
        _workflow.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        _workflow.setDescription( "strDescription" );
        _workflow.setEnabled( true );
        _workflow.setWorkgroup( "group" );
        _workflowDAO.insert( _workflow );

        _state1 = newState( "state 1" );
        _state2 = newState( "state 2" );
        _state3 = newState( "state 3" );
        // state 1 -> state 2 by hand, then state 2 -> state 3 automatically
        _manualAction = newAction( "manual", _state1, _state2, false );
        _automaticAction = newAction( "automatic", _state2, _state3, true );

        _resourceWorkflow = new ResourceWorkflow( );
        _resourceWorkflow.setIdResource( ID_RESOURCE );
        _resourceWorkflow.setResourceType( RESOURCE_TYPE );
        _resourceWorkflow.setState( _state1 );
        _resourceWorkflow.setWorkFlow( _workflow );
        _resourceWorkflowDAO.insert( _resourceWorkflow );
    }

    @AfterEach
    protected void tearDown( ) throws Exception
    {
        _resourceWorkflowDAO.delete( _resourceWorkflow );
        _actionService.remove( _manualAction.getId( ) );
        _actionService.remove( _automaticAction.getId( ) );
        _stateDAO.delete( _state1.getId( ) );
        _stateDAO.delete( _state2.getId( ) );
        _stateDAO.delete( _state3.getId( ) );
        _iconDAO.delete( _icon.getId( ) );
        _workflowDAO.delete( _workflow.getId( ) );

        super.tearDown( );
    }

    /**
     * A resource claimed by another request (on this instance or another one) is not processed: the caller gets an exception
     * to display, the state is unchanged
     */
    @Test
    public void testClaimedResourceIsNotProcessed( )
    {
        assertTrue( _resourceWorkflowDAO.lock( ID_RESOURCE, RESOURCE_TYPE, _workflow.getId( ), List.of( _state1.getId( ) ), TIMEOUT ) );

        assertThrows( ResourceWorkflowLockedException.class, ( ) -> processManualAction( ) );
        assertEquals( _state1.getId( ), currentState( ) );
    }

    /**
     * The automatic action chained by an action runs under the claim of this action, and the claim is released at the end
     */
    @Test
    public void testChainedActionAndRelease( )
    {
        processManualAction( );

        assertEquals( _state3.getId( ), currentState( ) );
        assertTrue( _resourceWorkflowDAO.lock( ID_RESOURCE, RESOURCE_TYPE, _workflow.getId( ), List.of( _state3.getId( ) ), TIMEOUT ),
                "the claim must be released after the action" );
    }

    private void processManualAction( )
    {
        _workflowService.doProcessAction( ID_RESOURCE, RESOURCE_TYPE, _manualAction.getId( ), null, null, Locale.FRANCE, false, null, null );
    }

    private int currentState( )
    {
        return _resourceWorkflowDAO.load( ID_RESOURCE, RESOURCE_TYPE, _workflow.getId( ) ).getState( ).getId( );
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

    private Action newAction( String strName, State stateBefore, State stateAfter, boolean bAutomatic )
    {
        Action action = new Action( );
        action.setName( strName );
        action.setDescription( "strDescription" );
        action.setIcon( _icon );
        action.setListIdStateBefore( List.of( stateBefore.getId( ) ) );
        action.setStateAfter( stateAfter );
        action.setWorkflow( _workflow );
        action.setAutomaticState( bAutomatic );
        action.setOrder( 0 );
        _actionService.create( action );

        return action;
    }
}
