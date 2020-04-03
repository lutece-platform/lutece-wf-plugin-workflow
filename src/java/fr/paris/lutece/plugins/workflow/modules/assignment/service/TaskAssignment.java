/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.assignment.service;

import fr.paris.lutece.plugins.workflow.modules.assignment.business.AssignmentHistory;
import fr.paris.lutece.plugins.workflow.modules.assignment.business.TaskAssignmentConfig;
import fr.paris.lutece.plugins.workflow.modules.assignment.business.WorkgroupConfig;
import fr.paris.lutece.plugins.workflow.modules.comment.business.TaskCommentConfig;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.task.Task;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * TaskAssignment
 *
 */
public class TaskAssignment extends Task
{
    // TEMPLATE
    private static final String TEMPLATE_TASK_NOTIFICATION_MAIL = "admin/plugins/workflow/modules/notification/task_notification_mail.html";

    // PARAMETER
    private static final String PARAMETER_WORKGROUPS = "workgroups";

    // MARK
    private static final String MARK_MESSAGE = "message";

    // PROPERTIES
    private static final String PROPERTY_MAIL_SENDER_NAME = "module.workflow.assignment.task_assignment_config.mailSenderName";

    // BEANS
    private static final String BEAN_COMMENT_CONFIG_SERVICE = "workflow.taskCommentConfigService";
    @Inject
    @Named( BEAN_COMMENT_CONFIG_SERVICE )
    private ITaskConfigService _taskCommentConfigService;
    @Inject
    private IAssignmentHistoryService _assignmentHistoryService;
    @Inject
    @Named( TaskAssignmentConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskAssignmentConfigService;
    @Inject
    private IWorkgroupConfigService _workgroupConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( )
    {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String [ ] tabWorkgroups = request.getParameterValues( PARAMETER_WORKGROUPS + "_" + this.getId( ) );
        AdminUser admin = AdminUserService.getAdminUser( request );
        List<String> listWorkgroup = new ArrayList<String>( );
        TaskAssignmentConfig config = _taskAssignmentConfigService.findByPrimaryKey( this.getId( ) );

        for ( int i = 0; i < tabWorkgroups.length; i++ )
        {
            listWorkgroup.add( tabWorkgroups [i] );

            // add history
            AssignmentHistory history = new AssignmentHistory( );
            history.setIdResourceHistory( nIdResourceHistory );
            history.setIdTask( this.getId( ) );
            history.setWorkgroup( tabWorkgroups [i] );
            _assignmentHistoryService.create( history, WorkflowUtils.getPlugin( ) );

            if ( config.isNotify( ) )
            {
                WorkgroupConfig workgroupConfig = _workgroupConfigService.findByPrimaryKey( this.getId( ), tabWorkgroups [i], WorkflowUtils.getPlugin( ) );

                if ( ( workgroupConfig != null ) && ( workgroupConfig.getIdMailingList( ) != WorkflowUtils.CONSTANT_ID_NULL ) )
                {
                    Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( workgroupConfig.getIdMailingList( ) );

                    String strSenderEmail = MailService.getNoReplyEmail( );

                    Map<String, Object> model = new HashMap<String, Object>( );
                    model.put( MARK_MESSAGE, config.getMessage( ) );

                    HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFICATION_MAIL, locale, model );

                    if ( config.isUseUserName( ) )
                    {
                        String strSenderName = I18nService.getLocalizedString( PROPERTY_MAIL_SENDER_NAME, locale );

                        // Send Mail
                        for ( Recipient recipient : listRecipients )
                        {
                            // Build the mail message
                            MailService.sendMailHtml( recipient.getEmail( ), strSenderName, strSenderEmail, config.getSubject( ), t.getHtml( ) );
                        }
                    }
                    else
                    {
                        for ( Recipient recipient : listRecipients )
                        {
                            // Build the mail message
                            MailService.sendMailHtml( recipient.getEmail( ), admin.getFirstName( ) + " " + admin.getLastName( ), admin.getEmail( ),
                                    config.getSubject( ), t.getHtml( ) );
                        }
                    }
                }
            }
        }

        IResourceHistoryService resourceHistoryService = SpringContextService.getBean( "workflow.resourceHistoryService" );
        IResourceWorkflowService resourceWorkflowService = SpringContextService.getBean( "workflow.resourceWorkflowService" );

        // update resource workflow
        ResourceHistory resourceHistory = resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        ResourceWorkflow resourceWorkflow = resourceWorkflowService.findByPrimaryKey( resourceHistory.getIdResource( ), resourceHistory.getResourceType( ),
                resourceHistory.getWorkflow( ).getId( ) );
        resourceWorkflow.setWorkgroups( listWorkgroup );
        resourceWorkflowService.update( resourceWorkflow );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig( )
    {
        // remove config
        _taskAssignmentConfigService.remove( this.getId( ) );
        // remove task information
        _assignmentHistoryService.removeByTask( this.getId( ), WorkflowUtils.getPlugin( ) );
        _workgroupConfigService.removeByTask( this.getId( ), WorkflowUtils.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        _assignmentHistoryService.removeByHistory( nIdHistory, this.getId( ), WorkflowUtils.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        TaskAssignmentConfig config = _taskAssignmentConfigService.findByPrimaryKey( this.getId( ) );

        if ( config != null )
        {
            return config.getTitle( );
        }

        return WorkflowUtils.EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getTaskFormEntries( Locale locale )
    {
        Map<String, String> mapEntriesForm = null;
        TaskCommentConfig config = _taskCommentConfigService.findByPrimaryKey( this.getId( ) );

        if ( config != null )
        {
            mapEntriesForm = new HashMap<String, String>( );
            mapEntriesForm.put( PARAMETER_WORKGROUPS + "_" + this.getId( ), config.getTitle( ) );
        }

        return mapEntriesForm;
    }
}
