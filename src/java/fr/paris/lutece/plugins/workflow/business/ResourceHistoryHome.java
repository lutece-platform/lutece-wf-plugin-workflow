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

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for ResourceHistoryHome objects
 */
public final class ResourceHistoryHome
{
    // Static variable pointed at the DAO instance
    private static IResourceHistoryDAO _dao = (IResourceHistoryDAO) SpringContextService.getPluginBean( "workflow",
            "workflowResourceHistoryDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ResourceHistoryHome(  )
    {
    }

    /**
     * Creation of an instance of resoureceHistory
     *
     * @param resourceHistory The instance of resourceHistory which contains the informations to store
     * @param plugin the plugin
     *
     *
     */
    public static void create( ResourceHistory resourceHistory, Plugin plugin )
    {
        _dao.insert( resourceHistory, plugin );
    }

    /**
     *  remove resourceHistory which is specified in parameter
     *
     * @param  nIdHistory History id to remove
     * @param plugin the Plugin
     *
     */
    public static void remove( int nIdHistory, Plugin plugin )
    {
        _dao.delete( nIdHistory, plugin );
    }

    /**
     * Delete ResourceHistory list by list of id resource
     * @param listIdResource the resource list id
     * @param strResourceType the resource type
     * @param nIdWorflow the workflow id
     * @param plugin the plugin
     */
    public static void removeByListIdResource( List<Integer> listIdResource, String strResourceType,
        Integer nIdWorflow, Plugin plugin )
    {
        _dao.deleteByListIdResource( listIdResource, strResourceType, nIdWorflow, plugin );
    }

    /**
     * Get list history id by list of id resource
     * @param listIdResource the resource list id
     * @param strResourceType the resource type
     * @param nIdWorflow the workflow id
     * @param plugin the plugin
     * @return list of history id
     */
    public static List<Integer> getListHistoryIdByListIdResourceId( List<Integer> listIdResource,
        String strResourceType, Integer nIdWorflow, Plugin plugin )
    {
        return _dao.getListHistoryIdByListIdResourceId( listIdResource, strResourceType, nIdWorflow, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the resource history Object
     * @param nIdHistory the resource history key
     * @param plugin the plugin
     * @return the resource workflow Object
     */
    public static ResourceHistory findByPrimaryKey( int nIdHistory, Plugin plugin )
    {
        ResourceHistory resourceHistory = _dao.load( nIdHistory, plugin );
        resourceHistory.setAction( ActionHome.findByPrimaryKey( resourceHistory.getAction(  ).getId(  ), plugin ) );

        return resourceHistory;
    }

    /**
     * Load all  ResourceHistory Object for a given resource
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param nIdWorkflow the workflow id
     * @param plugin the plugin
     * @return the list of ResourceHistory
     */
    public static List<ResourceHistory> getAllHistoryByResource( int nIdResource, String strResourceType,
        int nIdWorkflow, Plugin plugin )
    {
        List<ResourceHistory> listResourceHistory = _dao.selectByResource( nIdResource, strResourceType, nIdWorkflow,
                plugin );

        for ( ResourceHistory resourceHistory : listResourceHistory )
        {
            resourceHistory.setAction( ActionHome.findByPrimaryKey( resourceHistory.getAction(  ).getId(  ), plugin ) );
        }

        return listResourceHistory;
    }

    /**
     * Load all  ResourceHistory Object for a given resource
     * @param nIdAction the action id
     * @param plugin the plugin
     * @return the list of ResourceHistory
     */
    public static List<ResourceHistory> getAllHistoryByAction( int nIdAction, Plugin plugin )
    {
        List<ResourceHistory> listResourceHistory = _dao.selectByAction( nIdAction, plugin );

        for ( ResourceHistory resourceHistory : listResourceHistory )
        {
            resourceHistory.setAction( ActionHome.findByPrimaryKey( resourceHistory.getAction(  ).getId(  ), plugin ) );
        }

        return listResourceHistory;
    }

    /**
     * Load the last resource history depending creation date
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param nIdWorkflow the workflow id
     * @param plugin the plugin
     * @return the list of ResourceHistory
     */
    public static ResourceHistory getLastHistoryResource( int nIdResource, String strResourceType, int nIdWorkflow,
        Plugin plugin )
    {
        List<ResourceHistory> listResourceHistory = _dao.selectByResource( nIdResource, strResourceType, nIdWorkflow,
                plugin );

        for ( ResourceHistory resourceHistory : listResourceHistory )
        {
            resourceHistory.setAction( ActionHome.findByPrimaryKey( resourceHistory.getAction(  ).getId(  ), plugin ) );
        }

        return ( listResourceHistory.size(  ) > 0 ) ? listResourceHistory.get( 0 ) : null;
    }
}
