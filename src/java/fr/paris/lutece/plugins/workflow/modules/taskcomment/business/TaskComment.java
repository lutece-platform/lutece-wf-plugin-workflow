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
package fr.paris.lutece.plugins.workflow.modules.taskcomment.business;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.business.task.Task;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 *
 *TaskComment
 */
public class TaskComment extends Task
{
    // Xml Tags
    private static final String TAG_COMMENT = "comment";

    //templates
    private static final String TEMPLATE_TASK_COMMENT_CONFIG = "admin/plugins/workflow/modules/taskcomment/task_comment_config.html";
    private static final String TEMPLATE_TASK_COMMENT_FORM = "admin/plugins/workflow/modules/taskcomment/task_comment_form.html";
    private static final String TEMPLATE_TASK_COMMENT_INFORMATION = "admin/plugins/workflow/modules/taskcomment/task_comment_information.html";

    //	Markers
    private static final String MARK_CONFIG = "config";
    private static final String MARK_COMMENT_VALUE = "comment_value";

    //Parameters
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_IS_MANDATORY = "is_mandatory";
    private static final String PARAMETER_COMMENT_VALUE = "comment_value";

    //Properties
    private static final String FIELD_TITLE = "wmodule.workflow.taskcomment.task_comment_config.label_title";

    //Messages
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.taskcomment.task_comment_config.message.mandatory.field";
    private static final String MESSAGE_NO_CONFIGURATION_FOR_TASK_COMMENT = "module.workflow.taskcomment.task_comment_config.message.no_configuration_for_task_comment";

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
        String strIsMandatory = request.getParameter( PARAMETER_IS_MANDATORY );

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) )
        {
            strError = FIELD_TITLE;
        }

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        TaskCommentConfig config = TaskCommentConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        if ( config == null )
        {
            config = new TaskCommentConfig(  );
            config.setIdTask( this.getId(  ) );
            config.setMandatory( strIsMandatory != null );
            config.setTitle( strTitle );
            TaskCommentConfigHome.create( config, plugin );
        }
        else
        {
            config.setMandatory( strIsMandatory != null );
            config.setTitle( strTitle );
            TaskCommentConfigHome.update( config, plugin );
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
        String strCommentValue = request.getParameter( PARAMETER_COMMENT_VALUE + "_" + this.getId(  ) );
        TaskCommentConfig config = TaskCommentConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        if ( config == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_CONFIGURATION_FOR_TASK_COMMENT,
                AdminMessage.TYPE_STOP );
        }

        if ( ( config != null ) && config.isMandatory(  ) &&
                ( ( strCommentValue == null ) || strCommentValue.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) ) )
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
        Map<String, Object> model = new HashMap<String, Object>(  );

        TaskCommentConfig config = TaskCommentConfigHome.findByPrimaryKey( this.getId(  ), plugin );
        model.put( MARK_CONFIG, config );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_COMMENT_CONFIG, locale, model );

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
        TaskCommentConfig config = TaskCommentConfigHome.findByPrimaryKey( this.getId(  ), plugin );
        model.put( MARK_CONFIG, config );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_COMMENT_FORM, locale, model );

        return template.getHtml(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getDisplayTaskInformation(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        CommentValue commentValue = CommentValueHome.findByPrimaryKey( nIdHistory, this.getId(  ), plugin );

        Map<String, Object> model = new HashMap<String, Object>(  );
        TaskCommentConfig config = TaskCommentConfigHome.findByPrimaryKey( this.getId(  ), plugin );
        model.put( MARK_CONFIG, config );
        model.put( MARK_COMMENT_VALUE, commentValue );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_COMMENT_INFORMATION, locale, model );

        return template.getHtml(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#processTask(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        String strCommentValue = request.getParameter( PARAMETER_COMMENT_VALUE + "_" + this.getId(  ) );
        CommentValue commentValue = new CommentValue(  );
        commentValue.setIdResourceHistory( nIdResourceHistory );
        commentValue.setIdTask( this.getId(  ) );
        commentValue.setValue( strCommentValue );
        CommentValueHome.create( commentValue, plugin );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doRemoveConfig(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void doRemoveConfig( Plugin plugin )
    {
        TaskCommentConfigHome.remove( this.getId(  ), plugin );
        CommentValueHome.removeByTask( this.getId(  ), plugin );
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
        CommentValueHome.removeByHistory( nIdHistory, this.getId(  ), plugin );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTaskInformationXml(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getTaskInformationXml( int idHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        StringBuffer strXml = new StringBuffer(  );
        CommentValue commentValue = CommentValueHome.findByPrimaryKey( idHistory, this.getId(  ), plugin );

        if ( commentValue != null )
        {
            XmlUtil.addElementHtml( strXml, TAG_COMMENT, commentValue.getValue(  ) );
        }
        else
        {
            XmlUtil.addEmptyElement( strXml, TAG_COMMENT, null );
        }

        return strXml.toString(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTitle(fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getTitle( Plugin plugin, Locale locale )
    {
        TaskCommentConfig config = TaskCommentConfigHome.findByPrimaryKey( this.getId(  ), plugin );

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
            refListEntriesForm.addItem( PARAMETER_COMMENT_VALUE + "_" + this.getId(  ), config.getTitle(  ) );
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
