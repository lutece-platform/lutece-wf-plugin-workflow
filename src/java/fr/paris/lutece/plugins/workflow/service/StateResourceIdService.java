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
package fr.paris.lutece.plugins.workflow.service;

import fr.paris.lutece.plugins.workflow.business.StateFilter;
import fr.paris.lutece.plugins.workflow.business.StateHome;
import fr.paris.lutece.plugins.workflow.business.WorkflowHome;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Locale;


/**
 *
 * class  StateResourceIdService
 *
 */
public class StateResourceIdService extends ResourceIdService
{
    /** Permission for viewing the resource associated to the state */
    public static final String PERMISSION_VIEW = "VIEW";
    public static final String PERMISSION_VIEW_ALL_WORKGROUP = "VIEW_ALL_WORKGROUP";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "workflow.permission.label.resource_type_state";
    private static final String PROPERTY_LABEL_VIEW = "workflow.permission.label.view_state";
    private static final String PROPERTY_LABEL_VIEW_ALL_WORKGROUP = "workflow.permission.label.view_all_workgroup";

    /** Creates a new instance of DocumentTypeResourceIdService */
    public StateResourceIdService(  )
    {
        setPluginName( WorkflowPlugin.PLUGIN_NAME );
    }

    /**
     * Initializes the service
     */
    public void register(  )
    {
        ResourceType rt = new ResourceType(  );
        rt.setResourceIdServiceClass( StateResourceIdService.class.getName(  ) );
        rt.setPluginName( WorkflowPlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( State.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission(  );
        p.setPermissionKey( PERMISSION_VIEW );
        p.setPermissionTitleKey( PROPERTY_LABEL_VIEW );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_VIEW_ALL_WORKGROUP );
        p.setPermissionTitleKey( PROPERTY_LABEL_VIEW_ALL_WORKGROUP );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * Returns a list of Testresource ids
     * @param locale The current locale
     * @return A list of resource ids
     */
    public ReferenceList getResourceIdList( Locale locale )
    {
        List<State> listState = StateHome.getListStateByFilter( new StateFilter(  ),
                PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME ) );

        ReferenceList reflistState = new ReferenceList(  );

        for ( State state : listState )
        {
            state.setWorkflow( WorkflowHome.findByPrimaryKey( state.getWorkflow(  ).getId(  ),
                    PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME ) ) );
            reflistState.addItem( state.getId(  ), state.getWorkflow(  ).getName(  ) + "/" + state.getName(  ) );
        }

        return reflistState;
    }

    /**
     * Returns the Title of a given resource
     * @param strId The Id of the resource
     * @param locale The current locale
     * @return The Title of a given resource
     */
    public String getTitle( String strId, Locale locale )
    {
        int nId = WorkflowUtils.convertStringToInt( strId );
        State state = StateHome.findByPrimaryKey( nId, PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME ) );

        if ( state != null )
        {
            state.setWorkflow( WorkflowHome.findByPrimaryKey( state.getWorkflow(  ).getId(  ),
                    PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME ) ) );
        }

        return ( state != null ) ? ( state.getWorkflow(  ).getName(  ) + "/" + state.getName(  ) ) : null;
    }
}
