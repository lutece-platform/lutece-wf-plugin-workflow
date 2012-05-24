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
package fr.paris.lutece.plugins.workflow.modules.notification.service;

import fr.paris.lutece.plugins.workflow.modules.notification.business.ITaskNotificationConfigDAO;
import fr.paris.lutece.plugins.workflow.modules.notification.business.TaskNotificationtConfig;
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;


/**
 *
 * TaskNotificationConfigService
 *
 */
public class TaskNotificationConfigService implements ITaskNotificationConfigService
{
    public static final String BEAN_SERVICE = "workflow.taskNotificationConfigService";
    @Inject
    private ITaskNotificationConfigDAO _taskNotificationConfigDAO;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void create( TaskNotificationtConfig config, Plugin plugin )
    {
        _taskNotificationConfigDAO.insert( config, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void update( TaskNotificationtConfig config, Plugin plugin )
    {
        _taskNotificationConfigDAO.store( config, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void remove( int nIdTask, Plugin plugin )
    {
        _taskNotificationConfigDAO.delete( nIdTask, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskNotificationtConfig findByPrimaryKey( int nIdTask, Plugin plugin )
    {
        return _taskNotificationConfigDAO.load( nIdTask, plugin );
    }
}
