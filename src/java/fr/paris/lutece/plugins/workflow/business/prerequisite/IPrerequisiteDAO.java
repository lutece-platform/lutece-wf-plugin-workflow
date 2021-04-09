/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.workflow.business.prerequisite;

import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;

/**
 * Interface for prerequisite DAO
 */
public interface IPrerequisiteDAO
{

    /**
     * Get a prerequisite by its primary key
     * 
     * @param nIdPrerequisite
     *            The id of the prerequisite to get
     * @param plugin
     *            The plugin
     * @return The prerequisite, or null if no prerequisite has the given id
     */
    Prerequisite findByPrimaryKey( int nIdPrerequisite, Plugin plugin );

    /**
     * Creates a new prerequisite
     * 
     * @param prerequisite
     *            The prerequisite to create
     * @param plugin
     *            the plugin
     */
    void create( Prerequisite prerequisite, Plugin plugin );

    /**
     * Updates a prerequisite
     * 
     * @param prerequisite
     *            The prerequisite to update
     * @param plugin
     *            The plugin
     */
    void update( Prerequisite prerequisite, Plugin plugin );

    /**
     * Removes a prerequisite from its id
     * 
     * @param nIdPrerequisite
     *            the id of the prerequisite to remove
     * @param plugin
     *            The plugin
     */
    void remove( int nIdPrerequisite, Plugin plugin );

    /**
     * Get a list of prerequisite associated with an action
     * 
     * @param nIdAction
     *            The id of the action
     * @param plugin
     *            The plugin
     * @return The list of prerequisite, or an empty list if no prerequisite is associated with the given action
     */
    List<Prerequisite> findByIdAction( int nIdAction, Plugin plugin );
}
