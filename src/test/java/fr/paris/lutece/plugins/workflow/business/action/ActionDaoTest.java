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
package fr.paris.lutece.plugins.workflow.business.action;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.action.IActionDAO;
import fr.paris.lutece.plugins.workflowcore.business.action.IActionStateDAO;
import fr.paris.lutece.plugins.workflowcore.business.icon.IIconDAO;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.state.IStateDAO;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.IWorkflowDAO;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

public class ActionDaoTest extends LuteceTestCase
{
    @Inject
    private IActionDAO _dao;
    @Inject
    private IIconDAO iconDAO;
    @Inject
    private IStateDAO stateDAO;
    @Inject
    private IWorkflowDAO workflowDAO;
    @Inject
    private IActionStateDAO _actionStateDAO;

    private Icon icon;
    private Workflow wf;
    private State stateBefore;
    private State stateAfter;
    private List<Integer> listIdStateBefore = new ArrayList<>( );

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

        stateBefore = new State( );
        stateBefore.setWorkflow( wf );
        stateBefore.setInitialState( false );
        stateBefore.setRequiredWorkgroupAssigned( false );
        stateDAO.insert( stateBefore );
        stateAfter = new State( );
        stateAfter.setWorkflow( wf );
        stateAfter.setInitialState( false );
        stateAfter.setRequiredWorkgroupAssigned( false );
        stateDAO.insert( stateAfter );
        
        listIdStateBefore.add( stateBefore.getId( ) );
    }

    @AfterEach
    protected void tearDown( ) throws Exception
    {
        iconDAO.delete( icon.getId( ) );
        stateDAO.delete( stateBefore.getId( ) );
        stateDAO.delete( stateAfter.getId( ) );
        workflowDAO.delete( wf.getId( ) );
        listIdStateBefore.clear( );
        super.tearDown( );
    }

    @Test
    public void testCRUD( )
    {
        Action action = new Action( );
        action.setName( "name" );
        action.setDescription( "strDescription" );
        action.setIcon( icon );
        action.setListIdStateBefore( listIdStateBefore );
        action.setStateAfter( stateAfter );
        action.setWorkflow( wf );
        action.setOrder( 0 );

        _dao.insert( action );

        Action load = _dao.load( action.getId( ) );
        assertEquals( action.getName( ), load.getName( ) );

        action.setName( "new name" );
        _dao.store( action );
        load = _dao.load( action.getId( ) );
        assertEquals( action.getName( ), load.getName( ) );

        _dao.delete( action.getId( ) );
        load = _dao.load( action.getId( ) );
        assertNull( load );
    }

    @Test
    public void testLoadWithIcon( )
    {
        Action action = new Action( );
        action.setName( "name" );
        action.setDescription( "strDescription" );
        action.setIcon( icon );
        action.setListIdStateBefore( listIdStateBefore );
        action.setStateAfter( stateAfter );
        action.setWorkflow( wf );
        action.setOrder( 0 );

        _dao.insert( action );

        Action load = _dao.loadWithIcon( action.getId( ) );
        assertEquals( icon.getName( ), load.getIcon( ).getName( ) );

        _dao.delete( action.getId( ) );
    }

    @Test
    public void testSelectActionsByFilter( )
    {
        Action action = new Action( );
        action.setName( "name" );
        action.setDescription( "strDescription" );
        action.setIcon( icon );
        action.setListIdStateBefore( listIdStateBefore );
        action.setStateAfter( stateAfter );
        action.setWorkflow( wf );
        action.setOrder( 0 );

        _dao.insert( action );
        _actionStateDAO.insert( action.getId( ), listIdStateBefore );
        
        ActionFilter filter = new ActionFilter( );
        filter.setIdStateAfter( stateAfter.getId( ) );
        filter.setIdStateBefore( stateBefore.getId( ) );
        filter.setIdWorkflow( wf.getId( ) );
        filter.setIdIcon( icon.getId( ) );
        filter.setAutomaticReflexiveAction( false );
        filter.setIsMassAction( false );

        List<Action> list = _dao.selectActionsByFilter( filter );
        assertEquals( 1, list.size( ) );

        _dao.delete( action.getId( ) );
    }

    @Test
    public void testFindStatesBetweenOrders( )
    {
        Action action = new Action( );
        action.setName( "name" );
        action.setDescription( "strDescription" );
        action.setIcon( icon );
        action.setListIdStateBefore( listIdStateBefore );
        action.setStateAfter( stateAfter );
        action.setWorkflow( wf );
        action.setOrder( 3 );

        _dao.insert( action );

        List<Action> list = _dao.findActionsBetweenOrders( 2, 4, wf.getId( ) );
        assertEquals( 1, list.size( ) );

        _dao.delete( action.getId( ) );
    }

    @Test
    public void testFindStatesAfterOrder( )
    {
        Action action = new Action( );
        action.setName( "name" );
        action.setDescription( "strDescription" );
        action.setIcon( icon );
        action.setListIdStateBefore( listIdStateBefore );
        action.setStateAfter( stateAfter );
        action.setWorkflow( wf );
        action.setOrder( 3 );

        _dao.insert( action );

        List<Action> list = _dao.findActionsAfterOrder( 2, wf.getId( ) );
        assertEquals( 1, list.size( ) );

        _dao.delete( action.getId( ) );
    }
}
