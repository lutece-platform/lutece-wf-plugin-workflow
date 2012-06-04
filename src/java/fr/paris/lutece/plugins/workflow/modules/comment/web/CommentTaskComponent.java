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
package fr.paris.lutece.plugins.workflow.modules.comment.web;

import fr.paris.lutece.plugins.workflow.modules.comment.business.CommentValue;
import fr.paris.lutece.plugins.workflow.modules.comment.business.TaskCommentConfig;
import fr.paris.lutece.plugins.workflow.modules.comment.service.ICommentValueService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.AbstractTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * CommentTaskComponent
 *
 */
public class CommentTaskComponent extends AbstractTaskComponent
{
    // XML
    private static final String TAG_COMMENT = "comment";

    // TEMPLATES
    private static final String TEMPLATE_TASK_COMMENT_CONFIG = "admin/plugins/workflow/modules/comment/task_comment_config.html";
    private static final String TEMPLATE_TASK_COMMENT_FORM = "admin/plugins/workflow/modules/comment/task_comment_form.html";
    private static final String TEMPLATE_TASK_COMMENT_INFORMATION = "admin/plugins/workflow/modules/comment/task_comment_information.html";

    // MARKS
    private static final String MARK_CONFIG = "config";
    private static final String MARK_COMMENT_VALUE = "comment_value";

    // PARAMETERS
    private static final String PARAMETER_COMMENT_VALUE = "comment_value";

    // MESSAGES
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.comment.task_comment_config.message.mandatory.field";
    private static final String MESSAGE_NO_CONFIGURATION_FOR_TASK_COMMENT = "module.workflow.comment.task_comment_config.message.no_configuration_for_task_comment";

    // SERVICES
    @Inject
    private ICommentValueService _commentValueService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        ITask task )
    {
        String strError = WorkflowUtils.EMPTY_STRING;
        String strCommentValue = request.getParameter( PARAMETER_COMMENT_VALUE + "_" + task.getId(  ) );
        TaskCommentConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );

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

        if ( StringUtils.isNotBlank( strError ) )
        {
            Object[] tabRequiredFields = { strError };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        TaskCommentConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
        model.put( MARK_CONFIG, config );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_COMMENT_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        TaskCommentConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
        model.put( MARK_CONFIG, config );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_COMMENT_FORM, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        CommentValue commentValue = _commentValueService.findByPrimaryKey( nIdHistory, task.getId(  ),
                WorkflowUtils.getPlugin(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        TaskCommentConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
        model.put( MARK_CONFIG, config );
        model.put( MARK_COMMENT_VALUE, commentValue );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_COMMENT_INFORMATION, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        StringBuffer strXml = new StringBuffer(  );
        CommentValue commentValue = _commentValueService.findByPrimaryKey( nIdHistory, task.getId(  ),
                WorkflowUtils.getPlugin(  ) );

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
}
