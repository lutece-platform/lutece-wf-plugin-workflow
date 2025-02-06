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
package fr.paris.lutece.plugins.workflow.service.prerequisite;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.IPrerequisiteConfig;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IAutomaticActionPrerequisiteService;

/**
 * Interface for prerequisite services of manual actions.<br>
 * Before displaying an action, every prerequisite of the action are checked. If one of them failed, the action is not displayed.<br>
 * <br>
 * <b>Implementations of this interface must be declared as Spring beans.</b>
 */
public interface IManualActionPrerequisiteService extends IAutomaticActionPrerequisiteService
{

    /**
     * Check if a resource matches this prerequisite.
     * 
     * @param user
     *            The user trying to do the action
     * @param nIdResource
     *            The id of the resource
     * @param strResourceType
     *            The resource type
     * @param config
     *            The configuration of the prerequisite, or null if the prerequisite has no configuration
     * @param nIdAction
     *            The id of the action that will be performed if the resource matches every prerequisites
     * @return True if the resource matches this prerequisite, false otherwise
     */
    default boolean canManualActionBePerformed( User user, int nIdResource, String strResourceType, IPrerequisiteConfig config, int nIdAction )
    {
        return canActionBePerformed( nIdResource, strResourceType, config, nIdAction );
    }
}
