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
package fr.paris.lutece.plugins.workflow.business.state;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.icon.IIconDAO;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.state.IStateDAO;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.business.workflow.IWorkflowDAO;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

public class StateDaoTest extends LuteceTestCase
{
    @Inject
    private IStateDAO _dao;
    @Inject
    private IWorkflowDAO workflowDAO;
    @Inject
    private IIconDAO iconDAO;
    private Workflow wf;
    private Icon icon;

    @BeforeEach
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        icon = new Icon( );
        icon.setName( "icon" );
        iconDAO.insert( icon );

        wf = new Workflow( );
        wf.setName( "wf1" );
        wf.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        wf.setDescription( "strDescription" );
        wf.setEnabled( true );
        wf.setWorkgroup( "group" );
        workflowDAO.insert( wf );
    }

    @AfterEach
    protected void tearDown( ) throws Exception
    {
        iconDAO.delete( icon.getId( ) );
        workflowDAO.delete( wf.getId( ) );

        super.tearDown( );
    }

    @Test
    public void testCRUD( )
    {
        State state = new State( );
        state.setName( "strName" );
        state.setDescription( "strDescription" );
        state.setIcon( icon );
        state.setInitialState( false );
        state.setRequiredWorkgroupAssigned( false );
        state.setWorkflow( wf );
        state.setOrder( 3 );

        _dao.insert( state );

        State load = _dao.load( state.getId( ) );
        assertEquals( state.getName( ), load.getName( ) );

        state.setName( "new name" );
        _dao.store( state );
        load = _dao.load( state.getId( ) );
        assertEquals( state.getName( ), load.getName( ) );

        _dao.delete( state.getId( ) );
        load = _dao.load( state.getId( ) );
        assertNull( load );
    }

    @Test
    public void testSelectStatesByFilter( )
    {
        State state = new State( );
        state.setName( "strName" );
        state.setDescription( "strDescription" );
        state.setIcon( icon );
        state.setInitialState( false );
        state.setRequiredWorkgroupAssigned( false );
        state.setWorkflow( wf );
        state.setOrder( 3 );

        _dao.insert( state );

        StateFilter filter = new StateFilter( );
        filter.setIdWorkflow( wf.getId( ) );
        filter.setIsInitialState( 0 );

        List<State> list = _dao.selectStatesByFilter( filter );
        assertEquals( 1, list.size( ) );

        _dao.delete( state.getId( ) );
    }

    @Test
    public void testFindStatesBetweenOrders( )
    {
        State state = new State( );
        state.setName( "strName" );
        state.setDescription( "strDescription" );
        state.setIcon( icon );
        state.setInitialState( false );
        state.setRequiredWorkgroupAssigned( false );
        state.setWorkflow( wf );
        state.setOrder( 3 );

        _dao.insert( state );

        StateFilter filter = new StateFilter( );
        filter.setIdWorkflow( wf.getId( ) );
        filter.setIsInitialState( 0 );

        List<State> list = _dao.findStatesBetweenOrders( 2, 4, wf.getId( ) );
        assertEquals( 1, list.size( ) );

        _dao.delete( state.getId( ) );
    }

    @Test
    public void testFindStatesAfterOrder( )
    {
        State state = new State( );
        state.setName( "strName" );
        state.setDescription( "strDescription" );
        state.setIcon( icon );
        state.setInitialState( false );
        state.setRequiredWorkgroupAssigned( false );
        state.setWorkflow( wf );
        state.setOrder( 3 );

        _dao.insert( state );

        StateFilter filter = new StateFilter( );
        filter.setIdWorkflow( wf.getId( ) );
        filter.setIsInitialState( 0 );

        List<State> list = _dao.findStatesAfterOrder( 2, wf.getId( ) );
        assertEquals( 1, list.size( ) );

        _dao.delete( state.getId( ) );
    }
}
