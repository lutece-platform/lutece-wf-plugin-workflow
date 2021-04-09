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
package fr.paris.lutece.plugins.workflow.modules.archive.web;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.archive.ArchivalType;
import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveConfig;
import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveResource;
import fr.paris.lutece.plugins.workflow.modules.archive.service.IArchiveService;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;

public class ArchiveTaskComponent extends NoFormTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_ARCHIVE_CONFIG = "admin/plugins/workflow/modules/archive/task_archive_config.html";

    // MARKS
    private static final String MARK_LIST_TYPE = "type_list";
    private static final String MARK_CONFIG = "config";
    private static final String MARK_LIST_STATE = "state_list";

    // PARAMETERS
    private static final String PARAMETER_TYPE = "archival_type";
    private static final String PARAMETER_DELAY = "archival_delay";
    private static final String PARAMETER_NEXT_STATE = "next_state";

    // PROPERTIES
    private static final String FIELD_TYPE = "module.workflow.archive.task.config.type.label";
    private static final String FIELD_DELAY = "module.workflow.archive.task.config.delay.label";
    private static final String FIELD_STATE = "module.workflow.archive.task.config.state.label";
    private static final String PREFIX_TYPE = "module.workflow.archive.task.type.";

    // MESSAGES
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.archive.task.config.message.mandatory.field";
    private static final String MESSAGE_NUMERIC_FIELD = "module.workflow.archive.task.config.message.numeric.field";
    private static final String MESSAGE_INFORMATION_ARCHIVED = "module.workflow.archive.task.information.archived.message";
    private static final String MESSAGE_INFORMATION_NOT_ARCHIVED = "module.workflow.archive.task.information.notarchived.message";

    @Inject
    private IArchiveService _archiveService;

    @Inject
    private IActionService _actionService;

    @Inject
    private IStateService _stateService;

    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        ArchiveConfig config = _archiveService.loadConfig( task );
        if ( config == null )
        {
            config = new ArchiveConfig( );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_CONFIG, config );
        model.put( MARK_LIST_TYPE, getTypeArchivalList( locale ) );
        model.put( MARK_LIST_STATE, getListStates( task.getAction( ).getId( ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ARCHIVE_CONFIG, locale, model );
        return template.getHtml( );
    }

    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        String strError = null;
        String strType = request.getParameter( PARAMETER_TYPE );
        String strDelay = request.getParameter( PARAMETER_DELAY );
        String strState = request.getParameter( PARAMETER_NEXT_STATE );

        if ( StringUtils.isBlank( strType ) )
        {
            strError = FIELD_TYPE;
        }

        if ( StringUtils.isBlank( strDelay ) )
        {
            strError = FIELD_DELAY;
        }

        if ( StringUtils.isBlank( strState ) )
        {
            strError = FIELD_STATE;
        }

        if ( strError != null )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }
        if ( !StringUtils.isNumeric( strDelay ) )
        {
            Object [ ] tabNumericFields = {
                    I18nService.getLocalizedString( FIELD_DELAY, locale )
            };
            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabNumericFields, AdminMessage.TYPE_STOP );
        }

        ArchiveConfig config = _archiveService.loadConfig( task );
        boolean create = config == null;
        if ( create )
        {
            config = new ArchiveConfig( );
            config.setIdTask( task.getId( ) );
        }
        config.setTypeArchival( ArchivalType.valueOf( strType ) );
        config.setNextState( Integer.valueOf( strState ) );
        config.setDelayArchival( Integer.valueOf( strDelay ) );
        if ( create )
        {
            getTaskConfigService( ).create( config );
        }
        else
        {
            getTaskConfigService( ).update( config );
        }

        return null;
    }

    private ReferenceList getTypeArchivalList( Locale locale )
    {
        ReferenceList refList = new ReferenceList( );
        for ( ArchivalType archivalType : ArchivalType.values( ) )
        {
            refList.addItem( archivalType.name( ), I18nService.getLocalizedString( PREFIX_TYPE + archivalType.name( ), locale ) );
        }
        return refList;
    }

    private ReferenceList getListStates( int nIdAction )
    {
        ReferenceList referenceListStates = new ReferenceList( );
        Action action = _actionService.findByPrimaryKey( nIdAction );

        if ( ( action != null ) && ( action.getWorkflow( ) != null ) )
        {
            StateFilter stateFilter = new StateFilter( );
            stateFilter.setIdWorkflow( action.getWorkflow( ).getId( ) );

            List<State> listStates = _stateService.getListStateByFilter( stateFilter );

            referenceListStates.addItem( -1, StringUtils.EMPTY );
            referenceListStates.addAll( ReferenceList.convert( listStates, "id", "name", true ) );
        }

        return referenceListStates;
    }

    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        ArchiveResource archiveResource = _archiveService.getArchiveResource( nIdHistory, task.getId( ) );
        if ( archiveResource == null )
        {
            return null;
        }
        ArchiveConfig config = _archiveService.loadConfig( task );
        if ( archiveResource.isArchived( ) )
        {
            String [ ] params = new String [ ] {
                    DateUtil.getDateString( archiveResource.getArchivalDate( ), locale ),
                    I18nService.getLocalizedString( PREFIX_TYPE + config.getTypeArchival( ), locale )
            };
            return I18nService.getLocalizedString( MESSAGE_INFORMATION_ARCHIVED, params, locale );
        }
        else
        {
            String [ ] params = new String [ ] {
                    DateUtil.getDateString( archiveResource.getInitialDate( ), locale ),
                    I18nService.getLocalizedString( PREFIX_TYPE + config.getTypeArchival( ), locale )
            };
            return I18nService.getLocalizedString( MESSAGE_INFORMATION_NOT_ARCHIVED, params, locale );
        }
    }

    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }
}
