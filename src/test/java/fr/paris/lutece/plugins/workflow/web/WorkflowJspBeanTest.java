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
package fr.paris.lutece.plugins.workflow.web;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.icon.IIconDAO;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.state.IStateDAO;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.IWorkflowDAO;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

/**
 * WorkflowJspBean Test: change of the order of the states, actions and tasks
 */
public class WorkflowJspBeanTest extends LuteceTestCase
{
    private static final String JSP_DO_CHANGE_ORDER_STATE = "jsp/admin/plugins/workflow/DoChangeOrderState.jsp";
    private static final String JSP_DO_CHANGE_ORDER_ACTION = "jsp/admin/plugins/workflow/DoChangeOrderAction.jsp";
    private static final String JSP_DO_CHANGE_ORDER_TASK = "jsp/admin/plugins/workflow/DoChangeOrderTask.jsp";

    @Inject
    private WorkflowJspBean _bean;
    @Inject
    private ISecurityTokenService _securityTokenService;
    @Inject
    private IStateDAO _stateDAO;
    @Inject
    private IWorkflowDAO _workflowDAO;
    @Inject
    private IIconDAO _iconDAO;

    private Icon _icon;
    private Workflow _workflow;
    private Workflow _otherWorkflow;
    private State _state1;
    private State _state2;
    private State _otherState1;
    private State _otherState2;

    @BeforeEach
    protected void createWorkflows( )
    {
        _icon = new Icon( );
        _icon.setName( "icon" );
        _iconDAO.insert( _icon );

        _workflow = createWorkflow( "wf_order" );
        _otherWorkflow = createWorkflow( "wf_other" );
        _otherWorkflow.setWorkgroup( "junit_workgroup_without_user" );
        _workflowDAO.store( _otherWorkflow );
        _state1 = createState( _workflow, 1 );
        _state2 = createState( _workflow, 2 );
        _otherState1 = createState( _otherWorkflow, 1 );
        _otherState2 = createState( _otherWorkflow, 2 );
    }

    @AfterEach
    protected void removeWorkflows( )
    {
        for ( State state : new State [ ] { _state1, _state2, _otherState1, _otherState2 } )
        {
            _stateDAO.delete( state.getId( ) );
        }
        _workflowDAO.delete( _workflow.getId( ) );
        _workflowDAO.delete( _otherWorkflow.getId( ) );
        _iconDAO.delete( _icon.getId( ) );
    }

    private Workflow createWorkflow( String strName )
    {
        Workflow workflow = new Workflow( );
        workflow.setName( strName );
        workflow.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        workflow.setDescription( strName );
        workflow.setEnabled( true );
        workflow.setWorkgroup( "all" );
        _workflowDAO.insert( workflow );

        return workflow;
    }

    private State createState( Workflow workflow, int nOrder )
    {
        State state = new State( );
        state.setName( workflow.getName( ) + nOrder );
        state.setDescription( "description" );
        state.setIcon( _icon );
        state.setInitialState( false );
        state.setRequiredWorkgroupAssigned( false );
        state.setWorkflow( workflow );
        state.setOrder( nOrder );
        _stateDAO.insert( state );

        return state;
    }

    private MockHttpServletRequest getRequest( String strTokenAction )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setMethod( "POST" );

        if ( strTokenAction != null )
        {
            request.setParameter( SecurityTokenService.PARAMETER_TOKEN, _securityTokenService.getToken( request, strTokenAction ) );
        }

        return request;
    }

    @Test
    public void testDoChangeOrderStateNoToken( )
    {
        MockHttpServletRequest request = getRequest( null );
        request.setParameter( "id_state", Integer.toString( _state2.getId( ) ) );
        request.setParameter( "order_id", "1" );

        assertThrows( AccessDeniedException.class, ( ) -> _bean.doChangeOrderState( request ) );
        assertEquals( 2, _stateDAO.load( _state2.getId( ) ).getOrder( ) );
    }

    @Test
    public void testDoChangeOrderStateUnknownState( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = getRequest( JSP_DO_CHANGE_ORDER_STATE );
        request.setParameter( "id_state", "999999" );
        request.setParameter( "order_id", "1" );

        assertNotNull( _bean.doChangeOrderState( request ) );
    }

    @Test
    public void testDoChangeOrderStateIgnoresWorkflowOfRequest( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = getRequest( JSP_DO_CHANGE_ORDER_STATE );
        request.setParameter( "id_state", Integer.toString( _state2.getId( ) ) );
        request.setParameter( "order_id", "1" );
        request.setParameter( "id_workflow", Integer.toString( _otherWorkflow.getId( ) ) );

        _bean.doChangeOrderState( request );

        assertEquals( 1, _stateDAO.load( _state2.getId( ) ).getOrder( ) );
        assertEquals( 2, _stateDAO.load( _state1.getId( ) ).getOrder( ) );
        // The workflow given in the request is not changed
        assertEquals( 1, _stateDAO.load( _otherState1.getId( ) ).getOrder( ) );
        assertEquals( 2, _stateDAO.load( _otherState2.getId( ) ).getOrder( ) );
    }

    @Test
    public void testDoChangeOrderActionNoTokenAndUnknownAction( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = getRequest( null );
        request.setParameter( "id_action", "999999" );

        assertThrows( AccessDeniedException.class, ( ) -> _bean.doChangeOrderAction( request ) );

        MockHttpServletRequest requestWithToken = getRequest( JSP_DO_CHANGE_ORDER_ACTION );
        requestWithToken.setParameter( "id_action", "999999" );

        assertNotNull( _bean.doChangeOrderAction( requestWithToken ) );
    }

    @Test
    public void testDoChangeOrderTaskNoTokenAndUnknownTask( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = getRequest( null );
        request.setParameter( "id_task", "999999" );

        assertThrows( AccessDeniedException.class, ( ) -> _bean.doChangeOrderTask( request ) );

        MockHttpServletRequest requestWithToken = getRequest( JSP_DO_CHANGE_ORDER_TASK );
        requestWithToken.setParameter( "id_task", "999999" );

        assertNotNull( _bean.doChangeOrderTask( requestWithToken ) );
    }

    @Test
    public void testDoUpdateTaskOrderNoToken( )
    {
        MockHttpServletRequest request = getRequest( null );
        request.setParameter( "id_action", "999999" );

        AccessDeniedException exception = assertThrows( AccessDeniedException.class, ( ) -> _bean.doUpdateTaskOrder( request ) );
        // Refused on the token, before the action is loaded
        assertFalse( exception.getMessage( ).startsWith( "Action not found" ) );
    }

    private MockHttpServletRequest getUserRequest( )
    {
        MockHttpServletRequest request = getRequest( null );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), WorkflowJspBean.RIGHT_MANAGE_WORKFLOW );

        return request;
    }

    @Test
    public void testInitWorkflowOfAnotherWorkgroup( )
    {
        MockHttpServletRequest request = getUserRequest( );
        request.setParameter( "id_workflow", Integer.toString( _otherWorkflow.getId( ) ) );

        assertThrows( AccessDeniedException.class, ( ) -> _bean.init( request, WorkflowJspBean.RIGHT_MANAGE_WORKFLOW ) );
    }

    @Test
    public void testInitStateOfAnotherWorkgroup( )
    {
        MockHttpServletRequest request = getUserRequest( );
        request.setParameter( "id_state", Integer.toString( _otherState1.getId( ) ) );

        assertThrows( AccessDeniedException.class, ( ) -> _bean.init( request, WorkflowJspBean.RIGHT_MANAGE_WORKFLOW ) );
    }

    @Test
    public void testInitWorkflowOfAllWorkgroups( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = getUserRequest( );
        request.setParameter( "id_workflow", Integer.toString( _workflow.getId( ) ) );
        request.setParameter( "id_state", Integer.toString( _state1.getId( ) ) );

        _bean.init( request, WorkflowJspBean.RIGHT_MANAGE_WORKFLOW );
    }
}
