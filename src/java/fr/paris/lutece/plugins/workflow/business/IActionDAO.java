/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import fr.paris.lutece.portal.business.workflow.Action;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;


/**
 * IActionDAO
 **/
public interface IActionDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param action instance of the Action object to insert
     * @param plugin the plugin
     */
    void insert( Action action, Plugin plugin );

    /**
     * update record in the table.
     *
     * @param  action instance of the Action object to update
     * @param plugin the plugin
     */
    void store( Action action, Plugin plugin );

    /**
     * Load the action Object
     * @param nIdAction the state id
     * @param plugin the plugin
     * @return the Action Object
     */
    Action load( int nIdAction, Plugin plugin );

    /**
     * Load the action Object with icon associated
     * @param nIdAction the state id
     * @param plugin the plugin
     * @return the Action Object
     */
    Action loadWithIcon( int nIdAction, Plugin plugin );

    /**
     * Delete the action Object
     * @param nIdAction the action id
     * @param plugin the plugin
     */
    void delete( int nIdAction, Plugin plugin );

    /**
     * select all actions by filter
     * @param filter the action filter
     * @param plugin the plugin
     * @return a list of action
     */
    List<Action> selectActionsByFilter( ActionFilter filter, Plugin plugin );
}
