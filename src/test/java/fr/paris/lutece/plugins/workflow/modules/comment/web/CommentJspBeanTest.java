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
package fr.paris.lutece.plugins.workflow.modules.comment.web;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

/**
 * CommentJspBean Test: removal of a comment
 */
public class CommentJspBeanTest extends LuteceTestCase
{
    private static final String JSP_DO_REMOVE_COMMENT = "jsp/admin/plugins/workflow/modules/comment/DoRemoveComment.jsp";
    private static final String EXTERNAL_URL = "https://evil.example/phish";

    @Inject
    private CommentJspBean _bean;
    @Inject
    private ISecurityTokenService _securityTokenService;

    private MockHttpServletRequest getRemoveRequest( String strMethod )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setMethod( strMethod );
        request.setParameter( "id_history", "1" );
        request.setParameter( "id_task", "1" );
        request.setParameter( "return_url", EXTERNAL_URL );

        return request;
    }

    @Test
    public void testDoRemoveCommentNoToken( )
    {
        MockHttpServletRequest request = getRemoveRequest( "POST" );

        assertThrows( AccessDeniedException.class, ( ) -> _bean.doRemoveComment( request ) );
    }

    @Test
    public void testDoRemoveCommentGet( )
    {
        MockHttpServletRequest request = getRemoveRequest( "GET" );
        request.setParameter( SecurityTokenService.PARAMETER_TOKEN, _securityTokenService.getToken( request, JSP_DO_REMOVE_COMMENT ) );

        assertThrows( AccessDeniedException.class, ( ) -> _bean.doRemoveComment( request ) );
    }

    @Test
    public void testGetSafeReturnUrl( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        String strAdminHome = AppPathService.getBaseUrl( request ) + AppPathService.getAdminMenuUrl( );
        String strInternalUrl = AppPathService.getBaseUrl( request ) + "jsp/admin/plugins/forms/ManageForms.jsp?id=1";

        assertEquals( strAdminHome, _bean.getSafeReturnUrl( request, EXTERNAL_URL ) );
        assertEquals( strAdminHome, _bean.getSafeReturnUrl( request, "//evil.example/phish" ) );
        assertEquals( strAdminHome, _bean.getSafeReturnUrl( request, "javascript:alert(1)" ) );
        assertEquals( strAdminHome, _bean.getSafeReturnUrl( request, null ) );
        assertEquals( strInternalUrl, _bean.getSafeReturnUrl( request, strInternalUrl ) );
        assertEquals( "jsp/admin/plugins/forms/ManageForms.jsp", _bean.getSafeReturnUrl( request, "jsp/admin/plugins/forms/ManageForms.jsp" ) );
    }
}
