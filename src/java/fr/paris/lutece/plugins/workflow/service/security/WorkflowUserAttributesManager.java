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
package fr.paris.lutece.plugins.workflow.service.security;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.portal.service.security.UserAttributesService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * 
 * WorkflowUserAttributesManager
 *
 */
public final class WorkflowUserAttributesManager
{
	private static final String BEAN_WORKFLOW_USER_ATTRIBUTES_MANAGER = "workflow.userAttributesManager";
	private UserAttributesService _userAttributesService;

	/**
	 * Private constructor
	 */
	private WorkflowUserAttributesManager(  )
	{
	}

	/**
	 * Get the instance of the service
	 * @return the service
	 */
	public static WorkflowUserAttributesManager getManager(  )
	{
		return (WorkflowUserAttributesManager) SpringContextService.getPluginBean( WorkflowPlugin.PLUGIN_NAME, 
				BEAN_WORKFLOW_USER_ATTRIBUTES_MANAGER );
	}

	/**
	 * Check if the UserAttributesService is enabled
	 * @return true if the service is enabled, false otherwise
	 */
	public boolean isEnabled(  )
	{
		return _userAttributesService != null;
	}

	/**
	 * Set the UserAttributesService
	 * @param userAttributesService the UserAttributesService
	 */
	public void setUserAttributesService( UserAttributesService userAttributesService )
	{
		_userAttributesService = userAttributesService;
	}

	/**
	 * Get the attribute
	 * @param strUserId the id user guid
	 * @param strAttribute the attribute
	 * @return the attribute value
	 */
	public String getAttribute( String strUserId , String strAttribute )
	{
		return isEnabled(  ) ? _userAttributesService.getAttribute( strUserId, strAttribute ) : StringUtils.EMPTY;
	}

	/**
	 * Get the attributes
	 * @param strUserId the user id
	 * @return a map of attribute key - attribute value
	 */
	public Map<String, String> getAttributes( String strUserId )
	{
		return isEnabled(  ) ? _userAttributesService.getAttributes( strUserId ) : null;
	}
}
