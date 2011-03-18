/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.tasknotification.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.business.task.Task;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;


/**
 *
 *TaskComment
 */
public class TaskNotification extends Task
{
    //templates
    private static final String TEMPLATE_TASK_NOTIFICATION_CONFIG = "admin/plugins/workflow/modules/tasknotification/task_notification_config.html";
    private static final String TEMPLATE_TASK_NOTIFICATION_MAIL = "admin/plugins/workflow/modules/tasknotification/task_notification_mail.html";

    //	Markers
    private static final String MARK_CONFIG = "config";
    private static final String MARK_MESSAGE = "message";
    private static final String MARK_MAILING_LIST = "mailing_list";
    private static final String MARK_DEFAULT_SENDER_NAME = "default_sender_name";

    //Parameters
    private static final String PARAMETER_ID_MAILING_LIST = "id_mailing_list";
    private static final String PARAMETER_SUBJECT = "subject";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_SENDER_NAME = "sender_name";

    //Properties
    private static final String PROPERTY_NOTIFICATION_MAIL_DEFAULT_SENDER_NAME = "module.workflow.tasknotification.notification_mail.default_sender_name";
    private static final String PROPERTY_SELECT_EMPTY_CHOICE = "module.workflow.tasknotification.task_notification_config.label_empty_choice";
    private static final String FIELD_MAILING_LIST = "module.workflow.tasknotification.task_notification_config.label_mailing_list";
    private static final String FIELD_SENDER_NAME = "module.workflow.tasknotification.task_notification_config.label_sender_name";
    private static final String FIELD_SUBJECT = "module.workflow.tasknotification.task_notification_config.label_subject";
    private static final String FIELD_MESSAGE = "module.workflow.tasknotification.task_notification_config.label_message";
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.taskcomment.task_comment_config.message.mandatory.field";

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#init()
     */
    public void init(  )
    {
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doSaveConfig(javax.servlet.http.HttpServletRequest, java.util.Locale, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public String doSaveConfig( HttpServletRequest request, Locale locale, Plugin plugin )
    {
        String strError = WorkflowUtils.EMPTY_STRING;

        String strSenderName = request.getParameter( PARAMETER_SENDER_NAME );
        String strSubject = request.getParameter( PARAMETER_SUBJECT );
        String strMessage = request.getParameter( PARAMETER_MESSAGE );
        String strIdMailingList = request.getParameter( PARAMETER_ID_MAILING_LIST );
        int nIdMailingList = WorkflowUtils.convertStringToInt( strIdMailingList );

        if ( nIdMailingList == WorkflowUtils.CONSTANT_ID_NULL )
        {
            strError = FIELD_MAILING_LIST;
        }
        else if ( ( strSenderName == null ) || strSenderName.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) )
        {
            strError = FIELD_SENDER_NAME;
        }
        else if ( ( strSubject == null ) || strSubject.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) )
        {
            strError = FIELD_SUBJECT;
        }
        else if ( ( strMessage == null ) || strMessage.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) )
        {
            strError = FIELD_MESSAGE;
        }

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        TaskNotificationtConfig config = TaskNotificationConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        if ( config == null )
        {
            config = new TaskNotificationtConfig(  );
            config.setIdTask( this.getId(  ) );
            config.setIdMailingList( nIdMailingList );
            config.setSenderName( strSenderName );
            config.setSubject( strSubject );
            config.setMessage( strMessage );
            TaskNotificationConfigHome.create( config, plugin );
        }
        else
        {
            config.setIdMailingList( nIdMailingList );
            config.setSenderName( strSenderName );
            config.setSubject( strSubject );
            config.setMessage( strMessage );
            TaskNotificationConfigHome.update( config, plugin );
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doValidateTask(int, java.lang.String, javax.servlet.http.HttpServletRequest, java.util.Locale, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        Plugin plugin )
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getDisplayConfigForm(javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getDisplayConfigForm( HttpServletRequest request, Plugin plugin, Locale locale )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        String strNothing = I18nService.getLocalizedString( PROPERTY_SELECT_EMPTY_CHOICE, locale );
        ReferenceList refMailingList = new ReferenceList(  );
        refMailingList.addItem( WorkflowUtils.CONSTANT_ID_NULL, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( AdminUserService.getAdminUser( request ) ) );

        TaskNotificationtConfig config = TaskNotificationConfigHome.findByPrimaryKey( this.getId(  ), plugin );
        String strDefaultSenderName = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_DEFAULT_SENDER_NAME,
                locale );
        model.put( MARK_CONFIG, config );
        model.put( MARK_MAILING_LIST, refMailingList );
        model.put( MARK_DEFAULT_SENDER_NAME, strDefaultSenderName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFICATION_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getDisplayTaskForm(int, java.lang.String, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Plugin plugin, Locale locale )
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getDisplayTaskInformation(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#processTask(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        TaskNotificationtConfig config = TaskNotificationConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        if ( config != null )
        {
            String strSenderEmail = MailService.getNoReplyEmail(  );
            Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( config.getIdMailingList(  ) );
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_MESSAGE, config.getMessage(  ) );

            HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFICATION_MAIL, locale, model );

            // Send Mail
            for ( Recipient recipient : listRecipients )
            {
                // Build the mail message
                MailService.sendMailHtml( recipient.getEmail(  ), config.getSenderName(  ), strSenderEmail,
                    config.getSubject(  ), t.getHtml(  ) );
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doRemoveConfig(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void doRemoveConfig( Plugin plugin )
    {
        TaskNotificationConfigHome.remove( this.getId(  ), plugin );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#isConfigRequire()
     */
    public boolean isConfigRequire(  )
    {
        // TODO Auto-generated method stub
        return true;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#isFormTaskRequire()
     */
    public boolean isFormTaskRequire(  )
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doRemoveTaskInformation(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void doRemoveTaskInformation( int nIdHistory, Plugin plugin )
    {
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTaskInformationXml(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getTaskInformationXml( int idHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTitle(fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getTitle( Plugin plugin, Locale locale )
    {
        TaskNotificationtConfig config = TaskNotificationConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        if ( config != null )
        {
            return config.getSubject(  );
        }

        return WorkflowUtils.EMPTY_STRING;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTaskFormEntries(fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public ReferenceList getTaskFormEntries( Plugin plugin, Locale locale )
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#isTaskForActionAutomatic()
     */
    public boolean isTaskForActionAutomatic(  )
    {
        return true;
    }
}
