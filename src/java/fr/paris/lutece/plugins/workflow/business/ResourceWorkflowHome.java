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
package fr.paris.lutece.plugins.workflow.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.HashMap;
import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for DocumentWorkflowHome objects
 */
public final class ResourceWorkflowHome
{
    // Static variable pointed at the DAO instance
    private static IResourceWorkflowDAO _dao = (IResourceWorkflowDAO) SpringContextService.getPluginBean( "workflow",
            "workflowResourceWorkflowDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ResourceWorkflowHome(  )
    {
    }

    /**
     * Creation of an instance of resoureceWorkflow
     *
     * @param resourceWorkflow The instance of resourceWorkflow which contains the informations to store
     * @param plugin the plugin
     */
    public static void create( ResourceWorkflow resourceWorkflow, Plugin plugin )
    {
    	
    	List<String> listWorkgroup = resourceWorkflow.getWorkgroups(  );
    	resourceWorkflow.setAssociatedWithWorkgroup( listWorkgroup != null && listWorkgroup.size( ) > 0 );
    	
    	_dao.insert( resourceWorkflow, plugin );

        

        if ( listWorkgroup != null )
        {
            for ( String workgroup : listWorkgroup )
            {
                _dao.insertWorkgroup( resourceWorkflow, workgroup, plugin );
            }
        }
    }

    /**
     * Update of resourceWorkflow which is specified in parameter
     *
     * @param  resourceWorkflow The instance of resourceWorkflow which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( ResourceWorkflow resourceWorkflow, Plugin plugin )
    {
    	
    	List<String> listWorkgroup = resourceWorkflow.getWorkgroups(  );
    	resourceWorkflow.setAssociatedWithWorkgroup( listWorkgroup != null && listWorkgroup.size( ) > 0 );
        _dao.store( resourceWorkflow, plugin );
        //update workgroups list
        _dao.deleteWorkgroups( resourceWorkflow, plugin );
        
        if ( listWorkgroup != null )
        {
            for ( String workgroup : listWorkgroup )
            {
                _dao.insertWorkgroup( resourceWorkflow, workgroup, plugin );
            }
        }
    }

    /**
     *  remove resourceWorkflow which is specified in parameter
     *
     * @param  resourceWorkflow The instance of resourceWorkflow which contains the informations to remove
     * @param plugin the Plugin
     *
     */
    public static void remove( ResourceWorkflow resourceWorkflow, Plugin plugin )
    {
        _dao.deleteWorkgroups( resourceWorkflow, plugin );
        _dao.delete( resourceWorkflow, plugin );
    }

    /**
     * Delete record and workflow list by list id resource
     * @param listIdResource list of resource id
     * @param strResourceType the resource type
     * @param nIdWorflow the worflow id
     * @param plugin the plugin
     */
    public static void removeByListIdResource( List<Integer> listIdResource, String strResourceType,
        Integer nIdWorflow, Plugin plugin )
    {
        _dao.removeWorkgroupsByListIdResource( listIdResource, strResourceType, nIdWorflow, plugin );
        _dao.removeByListIdResource( listIdResource, strResourceType, nIdWorflow, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders
    /**
     * Load the resourceWorkflow Object
     * @param nIdResource the resource Id
     * @param strResourceType the resource type
     * @param nIdWorkflow the workflow id
     * @param plugin the plugin
     * @return the resource workflow Object
     */
    public static ResourceWorkflow findByPrimaryKey( int nIdResource, String strResourceType, int nIdWorkflow,
        Plugin plugin )
    {
        ResourceWorkflow resourceWorkflow = _dao.load( nIdResource, strResourceType, nIdWorkflow, plugin );

        if ( resourceWorkflow != null )
        {
            resourceWorkflow.setWorkgroups( _dao.selectWorkgroups( resourceWorkflow, plugin ) );
        }

        return resourceWorkflow;
    }

    /**
     * Select All resourceWorkflow Object associated to the workflow
     * @param nIdWorkflow workflow id
     * @param plugin the plugin
     * @return List of resourceWorkflow Object
     */
    public static List<ResourceWorkflow> getAllResourceWorkflowByWorkflow( int nIdWorkflow, Plugin plugin )
    {
        return _dao.selectResourceWorkflowByWorkflow( nIdWorkflow, plugin );
    }

    /**
     * Select All resourceWorkflow Object associated to the workflow
     * @param nIdWorkflow workflow id
     * @param plugin the plugin
     * @return List of Id resource
     */
    public static List<Integer> getAllResourceIdByWorkflow( int nIdWorkflow, Plugin plugin )
    {
        return _dao.selectResourceIdByWorkflow( nIdWorkflow, plugin );
    }

    /**
     * Select All resourceWorkflow Object associated to the state
     * @param nIdState state
     * @param plugin the plugin
     * @return List of resourceWorkflow Object
     */
    public static List<ResourceWorkflow> getAllResourceWorkflowByState( int nIdState, Plugin plugin )
    {
        return _dao.selectResourceWorkflowByWorkflow( nIdState, plugin );
    }

    /**
     * Select ResourceWorkflow by filter
     * @param resourceWorkflowFilter the filter
     * @param plugin the plugin
     * @return ResourceWorkflow List
     */
    public static List<ResourceWorkflow> getListResourceWorkflowByFilter( 
        ResourceWorkflowFilter resourceWorkflowFilter, Plugin plugin )
    {
        return _dao.getListResourceWorkflowByFilter( resourceWorkflowFilter, plugin );
    }

    /**
     * Select Resource Workflow id by filter
     * @param resourceWorkflowFilter the filter
     * @param lListIdWorkflowState list of workflow state
     * @param plugin the plugin
     * @return ResourceWorkflow list id
     */
    public static List<Integer> getListResourceIdWorkflowByFilter( ResourceWorkflowFilter resourceWorkflowFilter,
        List<Integer> lListIdWorkflowState, Plugin plugin )
    {
        return _dao.getListResourceWorkflowIdByFilter( resourceWorkflowFilter, lListIdWorkflowState, plugin );
    }

    /**
     * Select ResourceWorkflow ID by filter
     * @param resourceWorkflowFilter the filter
     * @param plugin the plugin
     * @return ResourceWorkflow ID List
     */
    public static List<Integer> getListResourceIdWorkflowByFilter( ResourceWorkflowFilter resourceWorkflowFilter,
        Plugin plugin )
    {
        return _dao.getListResourceWorkflowIdByFilter( resourceWorkflowFilter, plugin );
    }

    /**
     * Select id state by list id resource
     * @param lListIdResource the resource list id
     * @param nIdWorflow The worflow id
     * @param strResourceType the ressource type
     * @param nIdExternalParentId the external parent id
     * @param plugin the plugin
     * @return
     */
    public static HashMap<Integer, Integer> getListIdStateByListId( List<Integer> lListIdRessource, int nIdWorflow,
        String strResourceType, Integer nIdExternalParentId, Plugin plugin )
    {
        return _dao.getListIdStateByListId( lListIdRessource, nIdWorflow, strResourceType, nIdExternalParentId, plugin );
    }
}
