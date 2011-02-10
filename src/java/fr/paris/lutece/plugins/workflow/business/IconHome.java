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

import fr.paris.lutece.portal.business.workflow.Icon;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for Icon  objects
 */
public final class IconHome
{
    // Static variable pointed at the DAO instance
    private static IIconDAO _dao = (IIconDAO) SpringContextService.getPluginBean( "workflow", "workflowIconDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private IconHome(  )
    {
    }

    /**
     * Creation of an instance of icon
     *
     * @param icon The instance of icon which contains the informations to store
     * @param plugin the plugin
     *
     *
     *
     */
    public static void create( Icon icon, Plugin plugin )
    {
        _dao.insert( icon, plugin );
    }

    /**
     * Update of icon which is specified in parameter
     *
     * @param icon  The instance of icon which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( Icon icon, Plugin plugin )
    {
        _dao.store( icon, plugin );
    }

    /**
     * Update metatdata of icon which is specified in parameter
     *
     * @param icon  The instance of icon which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void updateMetadata( Icon icon, Plugin plugin )
    {
        _dao.storeMetadata( icon, plugin );
    }

    /**
     *  remove icon which is specified in parameter
     *
     * @param  nIdIcon The icon key to remove
     * @param plugin the Plugin
     *
     */
    public static void remove( int nIdIcon, Plugin plugin )
    {
        _dao.delete( nIdIcon, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
         * Load the icon Object
         * @param nIdIcon the icon id
         * @param plugin the plugin
         * @return the Icon Object
         */
    public static Icon findByPrimaryKey( int nIdIcon, Plugin plugin )
    {
        return _dao.load( nIdIcon, plugin );
    }

    /**
     * return the list of all icon
     * @param plugin the plugin
     * @return a list of icon
     */
    public static List<Icon> getListIcons( Plugin plugin )
    {
        return _dao.selectAll( plugin );
    }
}
