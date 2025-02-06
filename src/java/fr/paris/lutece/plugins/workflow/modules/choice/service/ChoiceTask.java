/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.choice.service;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;

import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * 
 * ChoiceTask
 *
 */
public class ChoiceTask extends SimpleTask
{

    private static final String PROPERTY_TASK_TITLE = "module.workflow.choice.task_title";
    private static final String PARAMETER_CHOICE_NO = "button_no";

    private static final String BEAN_CONFIG_SERVICE = "workflow.choiceTaskConfigService";
    @Inject
    @Named( BEAN_CONFIG_SERVICE )
    private ITaskConfigService _config;

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( PROPERTY_TASK_TITLE, locale );
    }

    @Override
    public boolean processTaskWithResult( int nIdResourceHistory, HttpServletRequest request, Locale locale, User user )
    {
    	// return false if the button "NO" is clicked, true otherwise
        return (request.getParameter( PARAMETER_CHOICE_NO ) == null );
    }

    @Override
    public void doRemoveConfig( )
    {
        _config.remove( getId( ) );
    }

}
