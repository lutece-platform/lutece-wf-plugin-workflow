/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.taskassignment.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.business.ResourceHistory;
import fr.paris.lutece.plugins.workflow.business.ResourceHistoryHome;
import fr.paris.lutece.plugins.workflow.business.ResourceWorkflow;
import fr.paris.lutece.plugins.workflow.business.ResourceWorkflowHome;
import fr.paris.lutece.plugins.workflow.business.task.Task;
import fr.paris.lutece.plugins.workflow.modules.taskcomment.business.TaskCommentConfig;
import fr.paris.lutece.plugins.workflow.modules.taskcomment.business.TaskCommentConfigHome;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 *
 *TaskComment
 */
public class TaskAssignment extends Task
{
    //templates
    private static final String TEMPLATE_TASK_ASSIGNMENT_CONFIG = "admin/plugins/workflow/modules/taskassignment/task_assignment_config.html";
    private static final String TEMPLATE_TASK_ASSIGNMENT_FORM = "admin/plugins/workflow/modules/taskassignment/task_assignment_form.html";
    private static final String TEMPLATE_TASK_ASSIGNMENT_INFORMATION = "admin/plugins/workflow/modules/taskassignment/task_assignment_information.html";
    private static final String TEMPLATE_TASK_NOTIFICATION_MAIL = "admin/plugins/workflow/modules/tasknotification/task_notification_mail.html";

    //	Markers
    private static final String MARK_CONFIG = "config";
    private static final String MARK_WORKGROUP_LIST = "workgroup_list";
    private static final String MARK_ITEM = "item";
    private static final String MARK_MESSAGE = "message";
    private static final String MARK_MAILING_LIST = "mailing_list";

    //Parameters
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_IS_MULTIPLE_OWNER = "is_multiple_owner";
    private static final String PARAMETER_WORKGROUPS = "workgroups";
    private static final String PARAMETER_ID_MAILING_LIST = "id_mailing_list";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_IS_NOTIFICATION = "is_notify";
    private static final String PARAMETER_SUBJECT = "subject";
    private static final String PARAMETER_IS_USE_USER_NAME = "is_use_user_name";

    //Properties
    private static final String FIELD_TITLE = "module.workflow.taskassignment.task_assignment_config.label_title";
    private static final String FIELD_WORKGROUPS = "module.workflow.taskassignment.task_assignment_config.label_workgroups";
    private static final String FIELD_MAILINGLIST_SUBJECT = "module.workflow.taskassignment.task_assignment_config.label_mailinglist_subject";
    private static final String FIELD_MAILINGLIST_MESSAGE = "module.workflow.taskassignment.task_assignment_config.label_mailinglist_message";
    private static final String PROPERTY_SELECT_EMPTY_CHOICE = "module.workflow.taskassignment.task_assignment_config.label_empty_choice";

    //Messages
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.taskassignment.task_assignment_config.message.mandatory.field";
    private static final String MESSAGE_NO_CONFIGURATION_FOR_TASK_ASSIGNMENT = "module.workflow.taskassignment.task_assignment_config.message.no_configuration_for_task_comment";
    private static final String MESSAGE_NO_MAILINGLIST_FOR_WORKGROUP = "module.workflow.taskassignment.task_assignment_config.message.no_mailinglist_for_workgroup";
    private static final String PROPERTY_MAIL_SENDER_NAME = "module.workflow.taskassignment.task_assignment_config.mailSenderName";

    // Xml Tags
    private static final String TAG_ASSIGNMENT = "assignment";
    private static final String TAG_LIST_WORKGROUP = "list-workgroup";
    private static final String TAG_WORKGROUP = "workgroup";

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
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strIsMultipleOwner = request.getParameter( PARAMETER_IS_MULTIPLE_OWNER );
        String strIsNotification = request.getParameter( PARAMETER_IS_NOTIFICATION );
        String strIsUseUserName = request.getParameter( PARAMETER_IS_USE_USER_NAME );
        String strMessage = request.getParameter( PARAMETER_MESSAGE );
        String strSubject = request.getParameter( PARAMETER_SUBJECT );
        String[] tabWorkgroups = request.getParameterValues( PARAMETER_WORKGROUPS );

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) )
        {
            strError = FIELD_TITLE;
        }

        if ( ( tabWorkgroups == null ) || ( tabWorkgroups.length == 0 ) )
        {
            strError = FIELD_WORKGROUPS;
        }

        if ( ( strIsNotification != null ) && ( ( strSubject == null ) || strSubject.equals( "" ) ) )
        {
            strError = FIELD_MAILINGLIST_SUBJECT;
        }

        if ( ( strIsNotification != null ) && ( ( strMessage == null ) || strMessage.equals( "" ) ) )
        {
            strError = FIELD_MAILINGLIST_MESSAGE;
        }

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        TaskAssignmentConfig config = TaskAssignmentConfigHome.findByPrimaryKey( this.getId(  ), plugin );
        Boolean bCreate = false;

        if ( config == null )
        {
            config = new TaskAssignmentConfig(  );
            config.setIdTask( this.getId(  ) );
            bCreate = true;
        }

        //add workgroups
        List<WorkgroupConfig> listWorkgroupConfig = new ArrayList<WorkgroupConfig>(  );
        WorkgroupConfig workgroupConfig;

        for ( int i = 0; i < tabWorkgroups.length; i++ )
        {
            workgroupConfig = new WorkgroupConfig(  );
            workgroupConfig.setIdTask( this.getId(  ) );
            workgroupConfig.setWorkgroupKey( tabWorkgroups[i] );

            if ( strIsNotification != null )
            {
                if ( WorkflowUtils.convertStringToInt( request.getParameter( PARAMETER_ID_MAILING_LIST + "_" +
                                tabWorkgroups[i] ) ) != -1 )
                {
                    workgroupConfig.setIdMailingList( WorkflowUtils.convertStringToInt( request.getParameter( PARAMETER_ID_MAILING_LIST +
                                "_" + tabWorkgroups[i] ) ) );
                }
                else
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_NO_MAILINGLIST_FOR_WORKGROUP,
                        AdminMessage.TYPE_STOP );
                }
            }

            listWorkgroupConfig.add( workgroupConfig );
        }

        config.setTitle( strTitle );
        config.setNotify( strIsNotification != null );
        config.setMultipleOwner( strIsMultipleOwner != null );
        config.setWorkgroups( listWorkgroupConfig );
        config.setMessage( strMessage );
        config.setSubject( strSubject );
        config.setUseUserName( strIsUseUserName != null );

        if ( bCreate )
        {
            TaskAssignmentConfigHome.create( config, plugin );
        }
        else
        {
            TaskAssignmentConfigHome.update( config, plugin );
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
        String strError = WorkflowUtils.EMPTY_STRING;
        String[] tabWorkgroups = request.getParameterValues( PARAMETER_WORKGROUPS + "_" + this.getId(  ) );
        TaskAssignmentConfig config = TaskAssignmentConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        if ( config == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_CONFIGURATION_FOR_TASK_ASSIGNMENT,
                AdminMessage.TYPE_STOP );
        }

        if ( ( tabWorkgroups == null ) || ( tabWorkgroups.length == 0 ) )
        {
            strError = config.getTitle(  );
        }

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { strError };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getDisplayConfigForm(javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getDisplayConfigForm( HttpServletRequest request, Plugin plugin, Locale locale )
    {
        String strNothing = I18nService.getLocalizedString( PROPERTY_SELECT_EMPTY_CHOICE, locale );

        TaskAssignmentConfig config = TaskAssignmentConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        ReferenceList refWorkgroups = AdminWorkgroupService.getUserWorkgroups( AdminUserService.getAdminUser( request ),
                locale );

        List<HashMap<String, Object>> listWorkgroups = new ArrayList<HashMap<String, Object>>(  );

        for ( ReferenceItem referenceItem : refWorkgroups )
        {
            if ( !referenceItem.getCode(  ).equals( AdminWorkgroupService.ALL_GROUPS ) )
            {
                HashMap<String, Object> workgroupsItem = new HashMap<String, Object>(  );
                workgroupsItem.put( MARK_ITEM, referenceItem );

                if ( ( config != null ) && ( config.getWorkgroups(  ) != null ) )
                {
                    for ( WorkgroupConfig workgroupSelected : config.getWorkgroups(  ) )
                    {
                        if ( referenceItem.getCode(  ).equals( workgroupSelected.getWorkgroupKey(  ) ) )
                        {
                            workgroupsItem.put( MARK_CONFIG, workgroupSelected );

                            break;
                        }
                    }
                }

                listWorkgroups.add( workgroupsItem );
            }
        }

        ReferenceList refMailingList = new ReferenceList(  );
        refMailingList.addItem( WorkflowUtils.CONSTANT_ID_NULL, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( AdminUserService.getAdminUser( request ) ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_WORKGROUP_LIST, listWorkgroups );
        model.put( MARK_CONFIG, config );
        model.put( MARK_MAILING_LIST, refMailingList );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ASSIGNMENT_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getDisplayTaskForm(int, java.lang.String, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Plugin plugin, Locale locale )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        TaskAssignmentConfig config = TaskAssignmentConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        ReferenceList refWorkgroups = new ReferenceList(  );

        for ( ReferenceItem referenceItem : AdminWorkgroupService.getUserWorkgroups( AdminUserService.getAdminUser( 
                    request ), locale ) )
        {
            if ( !referenceItem.getCode(  ).equals( AdminWorkgroupService.ALL_GROUPS ) )
            {
                if ( ( config != null ) && ( config.getWorkgroups(  ) != null ) )
                {
                    for ( WorkgroupConfig workgroupSelected : config.getWorkgroups(  ) )
                    {
                        if ( referenceItem.getCode(  ).equals( workgroupSelected.getWorkgroupKey(  ) ) )
                        {
                            refWorkgroups.add( referenceItem );
                        }
                    }
                }
            }
        }

        model.put( MARK_CONFIG, config );
        model.put( MARK_WORKGROUP_LIST, refWorkgroups );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ASSIGNMENT_FORM, locale, model );

        return template.getHtml(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getDisplayTaskInformation(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        List<AssignmentHistory> listAssignmentHistory = AssignmentHistoryHome.getListByHistory( nIdHistory,
                this.getId(  ), plugin );
        Map<String, Object> model = new HashMap<String, Object>(  );

        TaskAssignmentConfig config = TaskAssignmentConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        ReferenceList refWorkgroups = new ReferenceList(  );

        for ( ReferenceItem referenceItem : AdminWorkgroupService.getUserWorkgroups( AdminUserService.getAdminUser( 
                    request ), locale ) )
        {
            if ( !referenceItem.getCode(  ).equals( AdminWorkgroupService.ALL_GROUPS ) )
            {
                if ( listAssignmentHistory != null )
                {
                    for ( AssignmentHistory assignmentHistory : listAssignmentHistory )
                    {
                        if ( referenceItem.getCode(  ).equals( assignmentHistory.getWorkgroup(  ) ) )
                        {
                            refWorkgroups.add( referenceItem );
                        }
                    }
                }
            }
        }

        model.put( MARK_CONFIG, config );
        model.put( MARK_WORKGROUP_LIST, refWorkgroups );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ASSIGNMENT_INFORMATION, locale, model );

        return template.getHtml(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#processTask(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        String[] tabWorkgroups = request.getParameterValues( PARAMETER_WORKGROUPS + "_" + this.getId(  ) );
        AdminUser admin = AdminUserService.getAdminUser( request );
        List<String> listWorkgroup = new ArrayList<String>(  );
        TaskAssignmentConfig config = TaskAssignmentConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        for ( int i = 0; i < tabWorkgroups.length; i++ )
        {
            listWorkgroup.add( tabWorkgroups[i] );

            //add history 
            AssignmentHistory history = new AssignmentHistory(  );
            history.setIdResourceHistory( nIdResourceHistory );
            history.setIdTask( this.getId(  ) );
            history.setWorkgroup( tabWorkgroups[i] );
            AssignmentHistoryHome.create( history, plugin );

            if ( config.isNotify(  ) )
            {
                WorkgroupConfig workgroupConfig = WorkgroupConfigHome.findByPrimaryKey( this.getId(  ),
                        tabWorkgroups[i], plugin );

                if ( ( workgroupConfig != null ) &&
                        ( workgroupConfig.getIdMailingList(  ) != WorkflowUtils.CONSTANT_ID_NULL ) )
                {
                    Collection<Recipient> listRecipients = new ArrayList<Recipient>(  );
                    listRecipients = AdminMailingListService.getRecipients( workgroupConfig.getIdMailingList(  ) );

                    String strSenderEmail = MailService.getNoReplyEmail(  );

                    Map<String, Object> model = new HashMap<String, Object>(  );
                    model.put( MARK_MESSAGE, config.getMessage(  ) );

                    HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFICATION_MAIL, locale, model );

                    if ( config.isUseUserName(  ) )
                    {
                        String strSenderName = I18nService.getLocalizedString( PROPERTY_MAIL_SENDER_NAME, locale );

                        // Send Mail
                        for ( Recipient recipient : listRecipients )
                        {
                            // Build the mail message
                            MailService.sendMailHtml( recipient.getEmail(  ), strSenderName, strSenderEmail,
                                config.getSubject(  ), t.getHtml(  ) );
                        }
                    }
                    else
                    {
                        for ( Recipient recipient : listRecipients )
                        {
                            // Build the mail message
                            MailService.sendMailHtml( recipient.getEmail(  ),
                                admin.getFirstName(  ) + " " + admin.getLastName(  ), admin.getEmail(  ),
                                config.getSubject(  ), t.getHtml(  ) );
                        }
                    }
                }
            }
        }

        //update resource workflow 
        ResourceHistory resourceHistory = ResourceHistoryHome.findByPrimaryKey( nIdResourceHistory, plugin );
        ResourceWorkflow resourceWorkflow = ResourceWorkflowHome.findByPrimaryKey( resourceHistory.getIdResource(  ),
                resourceHistory.getResourceType(  ), resourceHistory.getWorkflow(  ).getId(  ), plugin );
        resourceWorkflow.setWorkgroups( listWorkgroup );
        ResourceWorkflowHome.update( resourceWorkflow, plugin );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doRemoveConfig(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void doRemoveConfig( Plugin plugin )
    {
        //remove config
        TaskAssignmentConfigHome.remove( this.getId(  ), plugin );
        //remove task information
        AssignmentHistoryHome.removeByTask( this.getId(  ), plugin );
        WorkgroupConfigHome.removeByTask( this.getId(  ), plugin );
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
        return true;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doRemoveTaskInformation(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void doRemoveTaskInformation( int nIdHistory, Plugin plugin )
    {
        AssignmentHistoryHome.removeByHistory( nIdHistory, this.getId(  ), plugin );
    }

    public void doRemoveTaskInformation( Plugin plugin )
    {
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTaskInformationXml(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getTaskInformationXml( int idHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        List<AssignmentHistory> listAssignmentHistory = AssignmentHistoryHome.getListByHistory( idHistory,
                this.getId(  ), plugin );

        StringBuffer strXml = new StringBuffer(  );

        XmlUtil.beginElement( strXml, TAG_ASSIGNMENT );
        XmlUtil.beginElement( strXml, TAG_LIST_WORKGROUP );

        for ( ReferenceItem referenceItem : AdminWorkgroupService.getUserWorkgroups( AdminUserService.getAdminUser( 
                    request ), locale ) )
        {
            if ( !referenceItem.getCode(  ).equals( AdminWorkgroupService.ALL_GROUPS ) )
            {
                if ( listAssignmentHistory != null )
                {
                    for ( AssignmentHistory assignmentHistory : listAssignmentHistory )
                    {
                        if ( referenceItem.getCode(  ).equals( assignmentHistory.getWorkgroup(  ) ) )
                        {
                            XmlUtil.addElementHtml( strXml, TAG_WORKGROUP, referenceItem.getName(  ) );
                        }
                    }
                }
            }
        }

        XmlUtil.endElement( strXml, TAG_LIST_WORKGROUP );
        XmlUtil.endElement( strXml, TAG_ASSIGNMENT );

        return strXml.toString(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTitle(fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getTitle( Plugin plugin, Locale locale )
    {
        TaskAssignmentConfig config = TaskAssignmentConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        if ( config != null )
        {
            return config.getTitle(  );
        }

        return WorkflowUtils.EMPTY_STRING;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTaskFormEntries(fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public ReferenceList getTaskFormEntries( Plugin plugin, Locale locale )
    {
        ReferenceList refListEntriesForm = null;
        TaskCommentConfig config = TaskCommentConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        if ( config != null )
        {
            refListEntriesForm = new ReferenceList(  );
            refListEntriesForm.addItem( PARAMETER_WORKGROUPS + "_" + this.getId(  ), config.getTitle(  ) );
        }

        return refListEntriesForm;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#isTaskForActionAutomatic()
     */
    public boolean isTaskForActionAutomatic(  )
    {
        return false;
    }
}
