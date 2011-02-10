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
package fr.paris.lutece.plugins.workflow.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.HashMap;
import java.util.List;


/**
 *
 * IResourceWorkflowDAO
 *
 */
public interface IResourceWorkflowDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param resourceWorkflow instance of the ResourceWorkflow object to insert
     * @param plugin the plugin
     */
    void insert( ResourceWorkflow resourceWorkflow, Plugin plugin );

    /**
     * update record in the table.
     *
     * @param resourceWorkflow instance of the ResourceWorkflow object to update
     * @param plugin the plugin
     */
    void store( ResourceWorkflow resourceWorkflow, Plugin plugin );

    /**
     * Load the Resource workflow Object
     * @param nIdResource the resource Id
     * @param strResourceType the resource type
     * @param nIdWorkflow the workflow id
     * @param plugin the plugin
     * @return the Document workflow Object
     */
    ResourceWorkflow load( int nIdResource, String strResourceType, int nIdWorkflow, Plugin plugin );

    /**
     * Delete  resourceWorkflow Object
     * @param resourceWorkflow resourceWorkflow object
     * @param plugin the plugin
     */
    void delete( ResourceWorkflow resourceWorkflow, Plugin plugin );

    /**
     * Delete Workgroups list by list of id resource
     * @param listIdResource the resource list id
     * @param strResourceType the resource type
     * @param nIdWorflow the workflow id
     * @param plugin the plugin
     */
    void removeWorkgroupsByListIdResource( List<Integer> listIdResource, String strResourceType, Integer nIdWorflow,
        Plugin plugin );

    /**
     * Delete resource list by list of id resource
     * @param listIdResource the resource list id
     * @param strResourceType the resource type
     * @param nIdWorflow the workflow id
     * @param plugin the plugin
     */
    void removeByListIdResource( List<Integer> listIdResource, String strResourceType, Integer nIdWorflow, Plugin plugin );

    /**
     * Select All resourceWorkflow associated to the workflow
     * @param nIdWorkflow workflow id
     * @param plugin the plugin
     * @return List of resourceWorkflow Object
     */
    List<ResourceWorkflow> selectResourceWorkflowByWorkflow( int nIdWorkflow, Plugin plugin );

    /**
     * Select All resourceWorkflow associated to the workflow
     * @param nIdWorkflow workflow id
     * @param plugin the plugin
     * @return List of Id resource
     */
    List<Integer> selectResourceIdByWorkflow( int nIdWorkflow, Plugin plugin );

    /**
     * Select All resourceWorkflow associated to the state
     * @param nIdState workflow state
     * @param plugin the plugin
     * @return List of resourceWorkflow Object
     */
    List<ResourceWorkflow> selectResourceWorkflowByState( int nIdState, Plugin plugin );

    /**
     * select the resource entity owner
     * @param resourceWorkflow the resource
     * @param plugin  the plugin
     * @return a list of  entities Owner
     */
    List<String> selectWorkgroups( ResourceWorkflow resourceWorkflow, Plugin plugin );

    /**
     * delete all resource entities owner
     * @param resourceWorkflow the resource
     * @param plugin  the plugin
     */
    void deleteWorkgroups( ResourceWorkflow resourceWorkflow, Plugin plugin );

    /**
     * insert a new entities owner
     * @param resourceWorkflow the resource
     * @param strWorkgroup the workgroupkey
     * @param plugin the plugin
     */
    void insertWorkgroup( ResourceWorkflow resourceWorkflow, String strWorkgroup, Plugin plugin );

    /**
     * Select ResourceWorkflow by filter
     * @param filter the ResourceWorkflow filter
     * @param plugin the plugin
     * @return ResourceWorkflow List
     */
    List<ResourceWorkflow> getListResourceWorkflowByFilter( ResourceWorkflowFilter filter, Plugin plugin );

    /**
     * Select ResourceWorkflow id by filter
     * @param filter the ResourceWorkflow filter
     * @param plugin  the plugin
     * @return ResourceWorkflow id list
     */
    List<Integer> getListResourceWorkflowIdByFilter( ResourceWorkflowFilter filter, Plugin plugin );

    /**
     * Select ResourceWorkflow by filter
     * @param filter the ResourceWorkflow filter
     * @param plugin the plugin
     * @return ResourceWorkflow List
     */
    List<Integer> getListResourceWorkflowIdByFilter( ResourceWorkflowFilter filter, List<Integer> lListIdWorkflowState,
        Plugin plugin );

    /**
     * Select id state by list id resource
     * @param lListIdResource the resource list id
     * @param nIdWorflow The worflow id
     * @param strResourceType the ressource type
     * @param nIdExternalParentId the external parent id
     * @param plugin the plugin
     * @return
     */
    HashMap<Integer, Integer> getListIdStateByListId( List<Integer> lListIdResource, int nIdWorflow,
        String strResourceType, Integer nIdExternalParentId, Plugin plugin );
}
