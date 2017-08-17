/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.notification.web;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * NotificationTaskComponent
 *
 */
public class NotificationTaskComponent extends NoFormTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_NOTIFICATION_CONFIG = "admin/plugins/workflow/modules/notification/task_notification_config.html";

    // MARKS
    private static final String MARK_CONFIG = "config";
    private static final String MARK_MAILING_LIST = "mailing_list";
    private static final String MARK_DEFAULT_SENDER_NAME = "default_sender_name";

    // PROPERTIES
    private static final String PROPERTY_NOTIFICATION_MAIL_DEFAULT_SENDER_NAME = "module.workflow.notification.notification_mail.default_sender_name";
    private static final String PROPERTY_SELECT_EMPTY_CHOICE = "module.workflow.notification.task_notification_config.label_empty_choice";

    // SERVICES

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        String strNothing = I18nService.getLocalizedString( PROPERTY_SELECT_EMPTY_CHOICE, locale );
        ReferenceList refMailingList = new ReferenceList( );
        refMailingList.addItem( WorkflowUtils.CONSTANT_ID_NULL, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( AdminUserService.getAdminUser( request ) ) );

        ITaskConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        String strDefaultSenderName = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_DEFAULT_SENDER_NAME, locale );
        model.put( MARK_CONFIG, config );
        model.put( MARK_MAILING_LIST, refMailingList );
        model.put( MARK_DEFAULT_SENDER_NAME, strDefaultSenderName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFICATION_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }
}
