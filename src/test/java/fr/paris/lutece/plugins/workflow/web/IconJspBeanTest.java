/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.Locale;

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
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

/**
 * IconJspBean Test: security token and icon used by a state
 */
public class IconJspBeanTest extends LuteceTestCase
{
    private static final String JSP_DO_REMOVE_ICON = "jsp/admin/plugins/workflow/DoRemoveIcon.jsp";

    @Inject
    private IconJspBean _bean;
    @Inject
    private ISecurityTokenService _securityTokenService;
    @Inject
    private IIconDAO _iconDAO;
    @Inject
    private IStateDAO _stateDAO;
    @Inject
    private IWorkflowDAO _workflowDAO;

    private Icon _icon;
    private Workflow _workflow;
    private State _state;

    @BeforeEach
    protected void createIcon( )
    {
        _icon = new Icon( );
        _icon.setName( "icon of a state" );
        _iconDAO.insert( _icon );

        _workflow = new Workflow( );
        _workflow.setName( "wf_icon" );
        _workflow.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        _workflow.setDescription( "wf_icon" );
        _workflow.setEnabled( true );
        _workflow.setWorkgroup( "all" );
        _workflowDAO.insert( _workflow );

        _state = new State( );
        _state.setName( "state with icon" );
        _state.setDescription( "description" );
        _state.setIcon( _icon );
        _state.setInitialState( false );
        _state.setRequiredWorkgroupAssigned( false );
        _state.setWorkflow( _workflow );
        _state.setOrder( 1 );
        _stateDAO.insert( _state );
    }

    @AfterEach
    protected void removeIcon( )
    {
        _stateDAO.delete( _state.getId( ) );
        _workflowDAO.delete( _workflow.getId( ) );
        _iconDAO.delete( _icon.getId( ) );
    }

    private MockHttpServletRequest getRequest( String strTokenAction )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setMethod( "POST" );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), WorkflowJspBean.RIGHT_MANAGE_WORKFLOW );
        request.setParameter( "id_icon", Integer.toString( _icon.getId( ) ) );

        if ( strTokenAction != null )
        {
            request.setParameter( SecurityTokenService.PARAMETER_TOKEN, _securityTokenService.getToken( request, strTokenAction ) );
        }

        return request;
    }

    @Test
    public void testDoRemoveIconWithoutToken( )
    {
        MockHttpServletRequest request = getRequest( null );

        assertThrows( AccessDeniedException.class, ( ) -> _bean.doRemoveIcon( request ) );
        assertNotNull( _iconDAO.load( _icon.getId( ) ) );
    }

    @Test
    public void testDoModifyIconWithoutToken( )
    {
        MockHttpServletRequest request = getRequest( null );
        request.setParameter( "name", "renamed" );

        assertThrows( AccessDeniedException.class, ( ) -> _bean.doModifyIcon( request ) );
        assertEquals( "icon of a state", _iconDAO.load( _icon.getId( ) ).getName( ) );
    }

    @Test
    public void testDoCreateIconWithoutToken( )
    {
        MockHttpServletRequest request = getRequest( null );

        assertThrows( AccessDeniedException.class, ( ) -> _bean.doCreateIcon( request ) );
    }

    @Test
    public void testRemoveIconOfAStateIsRefused( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = getRequest( null );
        String strUrl = _bean.getConfirmRemoveIcon( request );
        assertTrue( strUrl.endsWith( AdminMessageService.getMessageRelativeUrl( ) ), strUrl );
        AdminMessage message = AdminMessageService.getMessage( request );
        assertEquals( AdminMessage.TYPE_STOP, message.getType( ) );
        assertEquals( "States of the workflow are associated with this icon, you cannot delete this icon!", message.getText( Locale.ENGLISH ) );

        MockHttpServletRequest requestWithToken = getRequest( JSP_DO_REMOVE_ICON );
        _bean.doRemoveIcon( requestWithToken );
        assertNotNull( _iconDAO.load( _icon.getId( ) ) );
    }
}
