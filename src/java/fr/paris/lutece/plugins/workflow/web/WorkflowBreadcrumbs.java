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
package fr.paris.lutece.plugins.workflow.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.web.admin.BreadcrumbItem;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Builds the breadcrumbs of the workflow edition pages : the ancestors of the current page, each one linking back to its own page. The admin layout
 * displays them between the feature title and the page title.
 */
public class WorkflowBreadcrumbs
{
    private static final String JSP_MODIFY_WORKFLOW = "jsp/admin/plugins/workflow/ModifyWorkflow.jsp";
    private static final String JSP_MODIFY_STATE = "jsp/admin/plugins/workflow/ModifyState.jsp";
    private static final String JSP_MODIFY_REFLEXIVE_ACTION = "jsp/admin/plugins/workflow/GetModifyReflexiveAction.jsp";
    private static final String JSP_MODIFY_ACTION = "jsp/admin/plugins/workflow/ModifyAction.jsp";
    private static final String PARAMETER_ID_WORKFLOW = "id_workflow";
    private static final String PARAMETER_ID_STATE = "id_state";
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_PANE = "pane";
    private static final String PROPERTY_REFLEXIVE_ACTION_TITLE = "workflow.modify_reflexive_action.title";

    /** Pane of the workflow page listing the states */
    public static final String PANE_STATES = "pane-states";

    /** Pane of the workflow page listing the actions */
    public static final String PANE_ACTIONS = "pane-actions";

    private final Locale _locale;
    private final List<BreadcrumbItem> _listItems = new ArrayList<>( );

    /**
     * Constructor
     * 
     * @param locale
     *            the locale of the labels
     */
    public WorkflowBreadcrumbs( Locale locale )
    {
        _locale = locale;
    }

    /**
     * Adds a link to the workflow page
     * 
     * @param workflow
     *            the workflow, ignored if null
     * @param strPane
     *            the pane of the workflow page to open
     * @return this breadcrumbs
     */
    public WorkflowBreadcrumbs workflow( Workflow workflow, String strPane )
    {
        if ( workflow != null )
        {
            UrlItem url = new UrlItem( JSP_MODIFY_WORKFLOW );
            url.addParameter( PARAMETER_ID_WORKFLOW, workflow.getId( ) );
            url.addParameter( PARAMETER_PANE, strPane );
            addItem( escape( workflow.getName( ) ), url );
        }

        return this;
    }

    /**
     * Adds a link to the state page
     * 
     * @param state
     *            the state, ignored if null
     * @return this breadcrumbs
     */
    public WorkflowBreadcrumbs state( State state )
    {
        if ( state != null )
        {
            UrlItem url = new UrlItem( JSP_MODIFY_STATE );
            url.addParameter( PARAMETER_ID_STATE, state.getId( ) );
            addItem( escape( state.getName( ) ), url );
        }

        return this;
    }

    /**
     * Adds a link to the page of the automatic tasks of a state
     * 
     * @param state
     *            the state, ignored if null
     * @return this breadcrumbs
     */
    public WorkflowBreadcrumbs reflexiveTasks( State state )
    {
        if ( state != null )
        {
            UrlItem url = new UrlItem( JSP_MODIFY_REFLEXIVE_ACTION );
            url.addParameter( PARAMETER_ID_STATE, state.getId( ) );
            addItem( I18nService.getLocalizedString( PROPERTY_REFLEXIVE_ACTION_TITLE, _locale ), url );
        }

        return this;
    }

    /**
     * Adds a link to the action page
     * 
     * @param action
     *            the action, ignored if null
     * @return this breadcrumbs
     */
    public WorkflowBreadcrumbs action( Action action )
    {
        if ( action != null )
        {
            UrlItem url = new UrlItem( JSP_MODIFY_ACTION );
            url.addParameter( PARAMETER_ID_ACTION, action.getId( ) );
            addItem( escape( action.getName( ) ), url );
        }

        return this;
    }

    /**
     * Returns the items of the breadcrumbs
     * 
     * @return the items, from the closest to the feature to the closest to the current page
     */
    public List<BreadcrumbItem> getItems( )
    {
        return new ArrayList<>( _listItems );
    }

    /**
     * Adds an item : its title is displayed as is by the admin layout
     * 
     * @param strTitle
     *            the title, in HTML
     * @param url
     *            the URL
     */
    private void addItem( String strTitle, UrlItem url )
    {
        _listItems.add( new BreadcrumbItem( strTitle, url.getUrlWithEntity( ) ) );
    }

    /**
     * Escapes a name entered by a user
     * 
     * @param strValue
     *            the name
     * @return the name, in HTML
     */
    private static String escape( String strValue )
    {
        return StringEscapeUtils.escapeHtml4( StringUtils.defaultString( strValue ) );
    }
}
