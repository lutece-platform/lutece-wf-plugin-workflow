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
package fr.paris.lutece.plugins.workflow.business.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.icon.IIconDAO;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.IStateDAO;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.IWorkflowDAO;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Claim of a resource by the action processed on it
 */
public class ResourceWorkflowDaoTest extends LuteceTestCase
{
    private static final String RESOURCE_TYPE = "LOCK_TEST";
    private static final int ID_RESOURCE = 1;
    private static final int TIMEOUT = 300;
    private static final int THREADS = 8;

    @Inject
    private ResourceWorkflowDAO _dao;
    @Inject
    private IStateDAO _stateDAO;
    @Inject
    private IWorkflowDAO _workflowDAO;
    @Inject
    private IIconDAO _iconDAO;
    private Workflow _workflow;
    private Icon _icon;
    private State _state;
    private State _otherState;
    private ResourceWorkflow _resourceWorkflow;

    @BeforeEach
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        _icon = new Icon( );
        _icon.setName( "icon" );
        _iconDAO.insert( _icon );

        _workflow = new Workflow( );
        _workflow.setName( "wf lock" );
        _workflow.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        _workflow.setDescription( "strDescription" );
        _workflow.setEnabled( true );
        _workflow.setWorkgroup( "group" );
        _workflowDAO.insert( _workflow );

        _state = newState( "state" );
        _otherState = newState( "other state" );

        _resourceWorkflow = new ResourceWorkflow( );
        _resourceWorkflow.setIdResource( ID_RESOURCE );
        _resourceWorkflow.setResourceType( RESOURCE_TYPE );
        _resourceWorkflow.setState( _state );
        _resourceWorkflow.setWorkFlow( _workflow );
        _dao.insert( _resourceWorkflow );
    }

    @AfterEach
    protected void tearDown( ) throws Exception
    {
        _dao.delete( _resourceWorkflow );
        _stateDAO.delete( _state.getId( ) );
        _stateDAO.delete( _otherState.getId( ) );
        _iconDAO.delete( _icon.getId( ) );
        _workflowDAO.delete( _workflow.getId( ) );

        super.tearDown( );
    }

    /**
     * A claimed resource cannot be claimed again until it is released
     */
    @Test
    public void testLockAndUnlock( )
    {
        assertTrue( lock( _state, TIMEOUT ) );
        assertFalse( lock( _state, TIMEOUT ) );

        _dao.unlock( ID_RESOURCE, RESOURCE_TYPE, _workflow.getId( ) );
        assertTrue( lock( _state, TIMEOUT ) );
    }

    /**
     * A resource that is not in one of the states before the action is not claimed
     */
    @Test
    public void testLockOtherState( )
    {
        assertFalse( lock( _otherState, TIMEOUT ) );
        assertFalse( _dao.lock( ID_RESOURCE, RESOURCE_TYPE, _workflow.getId( ), new ArrayList<>( ), TIMEOUT ) );
    }

    /**
     * A claim older than the timeout (instance stopped during the action) is ignored
     */
    @Test
    public void testExpiredLock( ) throws InterruptedException
    {
        assertTrue( lock( _state, TIMEOUT ) );
        Thread.sleep( 50 );

        assertTrue( lock( _state, 0 ) );
    }

    /**
     * Concurrent claims of the same resource: only one succeeds
     */
    @Test
    public void testConcurrentLock( ) throws Exception
    {
        ExecutorService executor = Executors.newFixedThreadPool( THREADS );
        CountDownLatch start = new CountDownLatch( 1 );
        List<Future<Boolean>> futures = new ArrayList<>( );

        try
        {
            for ( int i = 0; i < THREADS; i++ )
            {
                futures.add( executor.submit( ( ) -> {
                    start.await( );
                    return lock( _state, TIMEOUT );
                } ) );
            }
            start.countDown( );

            int nClaims = 0;
            for ( Future<Boolean> future : futures )
            {
                nClaims += future.get( ) ? 1 : 0;
            }
            assertEquals( 1, nClaims );
        }
        finally
        {
            executor.shutdownNow( );
        }
    }

    private boolean lock( State stateBefore, int nTimeout )
    {
        return _dao.lock( ID_RESOURCE, RESOURCE_TYPE, _workflow.getId( ), List.of( stateBefore.getId( ) ), nTimeout );
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
