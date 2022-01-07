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
package fr.paris.lutece.plugins.workflow.modules.state.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskInformation;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskInformationHome;
import fr.paris.lutece.plugins.workflow.modules.state.service.IChangeStateTaskService;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

public class ChangeStateTaskComponent extends NoFormTaskComponent
{
    private static final String TEMPLATE_TASK_CHANGE_STATE_CONFIG = "admin/plugins/workflow/modules/state/task_change_state_config_form.html";
    private static final String TEMPLATE_TASK_CHANGE_STATE_INFORMATION = "admin/plugins/workflow/modules/state/task_choose_state_information.html";

    // Marks
    private static final String MARK_LIST_STATES = "list_states";
    private static final String MARK_NEW_STATE = "new_state";
    private static final String MARK_CONFIG = "config";

    @Inject
    private IChangeStateTaskService _changeStateTaskService;

    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        ChangeStateTaskConfig config = _changeStateTaskService.loadConfig( task );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_CONFIG, config );
        model.put( MARK_LIST_STATES, _changeStateTaskService.getListStates( task.getAction( ).getId( ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_CHANGE_STATE_CONFIG, locale, model );

        return template.getHtml( );
    }

    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );
        ChangeStateTaskInformation taskInformation = ChangeStateTaskInformationHome.find( nIdHistory, task.getId( ) );
        if ( taskInformation == null )
        {
            return null;
        }

        model.put( MARK_NEW_STATE, taskInformation.getState( ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_CHANGE_STATE_INFORMATION, locale, model );

        return template.getHtml( );
    }
}
