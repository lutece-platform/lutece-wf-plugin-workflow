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

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.workflow.modules.comment.business.CommentValue;
import fr.paris.lutece.plugins.workflow.modules.comment.service.CommentResourceIdService;
import fr.paris.lutece.plugins.workflow.modules.comment.service.ICommentValueService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.web.task.ITaskComponentManager;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.util.url.UrlItem;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * Controller for Comments
 *
 */
@SessionScoped
@Named
public class CommentJspBean extends MVCAdminJspBean
{
    /**
     * Generated serial id
     */
    private static final long serialVersionUID = 5300419950066235152L;

    // Parameters
    private static final String PARAMETER_ID_HISTORY = "id_history";
    private static final String PARAMETER_ID_TASK = "id_task";
    private static final String PARAMETER_RETURN_URL = "return_url";

    // JSPs
    private static final String JSP_DO_REMOVE_COMMENT = "jsp/admin/plugins/workflow/modules/comment/DoRemoveComment.jsp";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_COMMENT = "module.workflow.comment.message.confirm_remove_comment";

    // Other constants
    private static final String PARAMETER_ENCODING = "UTF-8";

    // Services
    @Inject
    private transient ICommentValueService _commentValueService;
    @Inject
    private transient IResourceHistoryService _resourceHistoryService;
    @Inject
    private transient ITaskService _taskService;
    @Inject
    private transient ITaskComponentManager _taskComponentManager;

    /**
     * Gets the confirmation page to remove a comment
     *
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return the confirmation page to remove the comment
     * @throws UnsupportedEncodingException
     *             if there is an exception during the encoding of the return_url parameter
     */
    public String getConfirmRemoveComment( HttpServletRequest request ) throws AccessDeniedException, UnsupportedEncodingException
    {
        if ( !canDeleteComment( request ) )
        {
            throw new AccessDeniedException( "The connected user is not allowed to delete this comment" );
        }

        String strIdHistory = request.getParameter( PARAMETER_ID_HISTORY );
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        String strReturnUrl = request.getParameter( PARAMETER_RETURN_URL );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_COMMENT );
        url.addParameter( PARAMETER_ID_HISTORY, strIdHistory );
        url.addParameter( PARAMETER_ID_TASK, strIdTask );
        url.addParameter( PARAMETER_RETURN_URL, URLEncoder.encode( strReturnUrl, PARAMETER_ENCODING ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_COMMENT, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Removes a comment
     *
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     * @throws UnsupportedEncodingException
     *             if there is an exception during the decoding of the return_url parameter
     */
    public String doRemoveComment( HttpServletRequest request ) throws AccessDeniedException, UnsupportedEncodingException
    {
        if ( !canDeleteComment( request ) )
        {
            throw new AccessDeniedException( "The connected user is not allowed to delete this comment" );
        }

        String strIdHistory = request.getParameter( PARAMETER_ID_HISTORY );
        int nIdHistory = WorkflowUtils.convertStringToInt( strIdHistory );
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        int nIdTask = WorkflowUtils.convertStringToInt( strIdTask );

        _commentValueService.removeByHistory( nIdHistory, nIdTask, WorkflowUtils.getPlugin( ) );

        // Remove history if no other task information to display
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );
        List<ITask> listActionTasks = _taskService.getListTaskByIdAction( resourceHistory.getAction( ).getId( ), request.getLocale( ) );

        Iterator<ITask> iterator = listActionTasks.iterator( );
        boolean informationToDisplay = false;
        while ( iterator.hasNext( ) )
        {
            ITask task = iterator.next( );

            String strTaskinformation = _taskComponentManager.getDisplayTaskInformation( resourceHistory.getId( ), request, request.getLocale( ), task );
            if ( !StringUtils.isEmpty( strTaskinformation ) )
            {
                informationToDisplay = true;
                break;
            }
        }

        if ( !informationToDisplay )
        {
            // Does the action resource should really be deleted if no information is displayed in history ?
            for ( ITask actionTask : listActionTasks )
            {
                actionTask.doRemoveTaskInformation( nIdHistory );
            }
            _resourceHistoryService.remove( nIdHistory );
        }

        return URLDecoder.decode( request.getParameter( PARAMETER_RETURN_URL ), PARAMETER_ENCODING );
    }

    /**
     * Tests whether the comment can be delete or not
     * 
     * @param request
     *            the request
     * @return {@code true} if the comment can be deleted, {@code false} otherwise
     */
    private boolean canDeleteComment( HttpServletRequest request )
    {
        String strIdHistory = request.getParameter( PARAMETER_ID_HISTORY );
        int nIdHistory = WorkflowUtils.convertStringToInt( strIdHistory );
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        int nIdTask = WorkflowUtils.convertStringToInt( strIdTask );
        AdminUser userConnected = AdminUserService.getAdminUser( request );

        CommentValue commentValue = _commentValueService.findByPrimaryKey( nIdHistory, nIdTask, WorkflowUtils.getPlugin( ) );

        boolean bHasPermissionDeletion = RBACService.isAuthorized( commentValue, CommentResourceIdService.PERMISSION_DELETE, (User) userConnected );
        boolean bIsOwner = _commentValueService.isOwner( nIdHistory, userConnected );

        return bHasPermissionDeletion || bIsOwner;
    }
}
