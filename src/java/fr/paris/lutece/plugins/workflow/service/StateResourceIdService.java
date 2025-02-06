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
package fr.paris.lutece.plugins.workflow.service;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Locale;

/**
 *
 * class StateResourceIdService
 *
 */
public class StateResourceIdService extends ResourceIdService
{
    /** Permission for viewing the resource associated to the state */
    public static final String PERMISSION_VIEW = "VIEW";

    /**
     * Permission to view every workgroups
     */
    public static final String PERMISSION_VIEW_ALL_WORKGROUP = "VIEW_ALL_WORKGROUP";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "workflow.permission.label.resource_type_state";
    private static final String PROPERTY_LABEL_VIEW = "workflow.permission.label.view_state";
    private static final String PROPERTY_LABEL_VIEW_ALL_WORKGROUP = "workflow.permission.label.view_all_workgroup";

    /** Creates a new instance of DocumentTypeResourceIdService */
    public StateResourceIdService( )
    {
        setPluginName( WorkflowPlugin.PLUGIN_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register( )
    {
        ResourceType rt = new ResourceType( );
        rt.setResourceIdServiceClass( StateResourceIdService.class.getName( ) );
        rt.setPluginName( WorkflowPlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( State.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission( );
        p.setPermissionKey( PERMISSION_VIEW );
        p.setPermissionTitleKey( PROPERTY_LABEL_VIEW );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_VIEW_ALL_WORKGROUP );
        p.setPermissionTitleKey( PROPERTY_LABEL_VIEW_ALL_WORKGROUP );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        IStateService stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
        IWorkflowService workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
        List<State> listState = stateService.getListStateByFilter( new StateFilter( ) );

        ReferenceList reflistState = new ReferenceList( );

        for ( State state : listState )
        {
            state.setWorkflow( workflowService.findByPrimaryKey( state.getWorkflow( ).getId( ) ) );
            reflistState.addItem( state.getId( ), state.getWorkflow( ).getName( ) + "/" + state.getName( ) );
        }

        return reflistState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( String strId, Locale locale )
    {
        IStateService stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
        IWorkflowService workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
        int nId = WorkflowUtils.convertStringToInt( strId );
        State state = stateService.findByPrimaryKey( nId );

        if ( state != null )
        {
            state.setWorkflow( workflowService.findByPrimaryKey( state.getWorkflow( ).getId( ) ) );
        }

        return ( state != null ) ? ( state.getWorkflow( ).getName( ) + "/" + state.getName( ) ) : null;
    }
}
