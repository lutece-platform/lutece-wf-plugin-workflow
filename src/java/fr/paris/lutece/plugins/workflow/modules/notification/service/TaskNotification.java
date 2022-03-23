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
package fr.paris.lutece.plugins.workflow.modules.notification.service;

import fr.paris.lutece.plugins.workflow.modules.notification.business.TaskNotificationConfig;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.Task;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * TaskNotification
 *
 */
public class TaskNotification extends Task
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_NOTIFICATION_MAIL = "admin/plugins/workflow/modules/notification/task_notification_mail.html";

    // MARKS
    private static final String MARK_MESSAGE = "message";

    // BEANS
    private static final String BEAN_NOTIFICATION_CONFIG_SERVICE = "workflow.taskNotificationConfigService";
    @Inject
    @Named( BEAN_NOTIFICATION_CONFIG_SERVICE )
    private ITaskConfigService _taskNotificationConfigService;

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
        TaskNotificationConfig config = _taskNotificationConfigService.findByPrimaryKey( this.getId( ) );

        if ( config != null )
        {
            String strSenderEmail = MailService.getNoReplyEmail( );
            Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( config.getIdMailingList( ) );
            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_MESSAGE, config.getMessage( ) );

            HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFICATION_MAIL, locale, model );

            // Send Mail
            for ( Recipient recipient : listRecipients )
            {
                // Build the mail message
                MailService.sendMailHtml( recipient.getEmail( ), config.getSenderName( ), strSenderEmail, config.getSubject( ), t.getHtml( ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig( )
    {
        _taskNotificationConfigService.remove( this.getId( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        TaskNotificationConfig config = _taskNotificationConfigService.findByPrimaryKey( this.getId( ) );

        if ( config != null )
        {
            return config.getSubject( );
        }

        return WorkflowUtils.EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getTaskFormEntries( Locale locale )
    {
        return null;
    }
}
