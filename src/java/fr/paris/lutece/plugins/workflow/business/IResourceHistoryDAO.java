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

import java.util.List;


/**
 *
 * IResourceHistoryDAO
 *
 */
public interface IResourceHistoryDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param resourceHistory instance of the ResourceHistory object to insert
     * @param plugin the plugin
     */
    void insert( ResourceHistory resourceHistory, Plugin plugin );

    /**
     * Load the Resource History Object
     * @param nIdHistory the history id
     * @param plugin the plugin
     * @return the Resource History Object
     *
     */
    ResourceHistory load( int nIdHistory, Plugin plugin );

    /**
     * Load all  ResourceHistory Object for a given resource
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param nIdWorkflow the workflow id
     * @param plugin the plugin
     * @return the list of ResourceHistory
     */
    List<ResourceHistory> selectByResource( int nIdResource, String strResourceType, int nIdWorkflow, Plugin plugin );

    /**
     * Load all  ResourceHistory Object for a given resource
     * @param nIdAction the action id
     * @param plugin the plugin
     * @return the list of ResourceHistory
     */
    List<ResourceHistory> selectByAction( int nIdAction, Plugin plugin );

    /**
     * Delete the ResourceHistory Object
     * @param nIdHistory the history id
     * @param plugin the plugin
     */
    void delete( int nIdHistory, Plugin plugin );

    /**
     * Delete ResourceHistory list by list of id resource
     * @param listIdResource the resource list id
     * @param strResourceType the resource type
     * @param nIdWorflow the workflow id
     * @param plugin the plugin
     */
    void deleteByListIdResource( List<Integer> listIdResource, String strResourceType, Integer nIdWorflow, Plugin plugin );

    /**
     * Get list history id by list of id resource
     * @param listIdResource the resource list id
     * @param strResourceType the resource type
     * @param nIdWorflow the workflow id
     * @param plugin the plugin
     * @return list of history id
     */
    List<Integer> getListHistoryIdByListIdResourceId( List<Integer> listIdResource, String strResourceType,
        Integer nIdWorflow, Plugin plugin );
}
