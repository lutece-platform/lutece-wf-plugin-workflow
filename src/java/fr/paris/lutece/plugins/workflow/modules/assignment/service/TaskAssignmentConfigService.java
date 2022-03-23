/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.assignment.service;

import fr.paris.lutece.plugins.workflow.modules.assignment.business.TaskAssignmentConfig;
import fr.paris.lutece.plugins.workflow.modules.assignment.business.WorkgroupConfig;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.TaskConfigService;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.inject.Inject;

/**
 *
 * TaskAssignmentConfigService
 *
 */
public class TaskAssignmentConfigService extends TaskConfigService
{
    /**
     * The name of the bean of this service
     */
    public static final String BEAN_SERVICE = "workflow.taskAssignmentConfigService";
    @Inject
    private IWorkgroupConfigService _workgroupConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void create( ITaskConfig config )
    {
        super.create( config );

        TaskAssignmentConfig taskAssignmentConfig = getConfigBean( config );

        if ( taskAssignmentConfig != null )
        {
            List<WorkgroupConfig> listWorkgroups = taskAssignmentConfig.getWorkgroups( );

            if ( listWorkgroups != null )
            {
                for ( WorkgroupConfig workgroupConfig : listWorkgroups )
                {
                    // Workaround in case of task duplication
                    workgroupConfig.setIdTask( config.getIdTask( ) );
                    _workgroupConfigService.create( workgroupConfig, WorkflowUtils.getPlugin( ) );
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void update( ITaskConfig config )
    {
        super.update( config );
        // update workgroups
        _workgroupConfigService.removeByTask( config.getIdTask( ), WorkflowUtils.getPlugin( ) );

        TaskAssignmentConfig taskAssignmentConfig = getConfigBean( config );

        if ( taskAssignmentConfig != null )
        {
            List<WorkgroupConfig> listWorkgroups = taskAssignmentConfig.getWorkgroups( );

            if ( listWorkgroups != null )
            {
                for ( WorkgroupConfig workgroupConfig : listWorkgroups )
                {
                    _workgroupConfigService.create( workgroupConfig, WorkflowUtils.getPlugin( ) );
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void remove( int nIdTask )
    {
        _workgroupConfigService.removeByTask( nIdTask, WorkflowUtils.getPlugin( ) );
        super.remove( nIdTask );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T findByPrimaryKey( int nIdTask )
    {
        TaskAssignmentConfig config = super.findByPrimaryKey( nIdTask );

        if ( config != null )
        {
            config.setWorkgroups( _workgroupConfigService.getListByConfig( nIdTask, WorkflowUtils.getPlugin( ) ) );
        }

        return (T) config;
    }
}
