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

import java.util.List;


/**
 *
 * IIconDAO
 *
 */
public interface IIconDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param icon instance of the Icon object to insert
     * @param plugin the plugin
     */
    void insert( Icon icon, Plugin plugin );

    /**
     * update record in the table.
     *
     * @param  icon instance of the Icon object to update
     * @param plugin the plugin
     */
    void store( Icon icon, Plugin plugin );

    /**
     * update icon metadata in the table.
     *
     * @param  icon instance of the Icon object to update
     * @param plugin the plugin
     */
    void storeMetadata( Icon icon, Plugin plugin );

    /**
     * Load the icon Object
     * @param nIdIcon the icon id
     * @param plugin the plugin
     * @return the icon Object
     */
    Icon load( int nIdIcon, Plugin plugin );

    /**
     * Delete the Icon Object
     * @param nIdIcon theicon id
     * @param plugin the plugin
     */
    void delete( int nIdIcon, Plugin plugin );

    /**
     * select all Icons
     * @param plugin the plugin
     * @return a list of Icon
     */
    List<Icon> selectAll( Plugin plugin );
}
